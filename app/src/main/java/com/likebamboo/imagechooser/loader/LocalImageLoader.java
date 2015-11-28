/**
 * LocalImageLoader.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser.loader;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.widget.ImageView;

import com.likebamboo.imagechooser.ICApplication;
import com.likebamboo.imagechooser.utils.DeviceUtil;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *  LruCache는 LinkedHashMap을 사용하여 최근에 사용된 object의 strong reference를 보관하고 있다가
 *
 *  정해진 사이즈를 넘어가게 되면 가장 최근에 사용되지 않은 놈부터 쫓아내는 LRU 알고리즘을 사용하는 메모리 캐시다
 *
 *  *  이미지를 로드할 때 LruCache에서 먼저 찾아보고 있으면 그걸로 바로 업데이트 하고 아니면 백그라운드 쓰레드에서 로딩한다.
 * <p>
 * 지역 사진 로더, 싱글 톤 패턴을 사용하여, 비동기 결의 지역 이미지를 사용하여, 스레드 풀로드 이미지
 * <p>
 * 스케줄링 알고리즘을로드하는 자신의 사진을 쓰기 :
 * <p>
 * 문제 설명 : 스레드 풀은 꽤 많은 사진을로드하지만, 작업을로드 할 때하는 스레드의 최대 수를 스레드 풀을 초과 할 수 있지만,로드 작업이 여전히 테이블의 특정 순서를 표시합니다.
 * <p>
 * 이것은 내가 목록의 맨 아래에 밀어 때 당신은 그림이 나오고로드를 참조하기 위해 오래 기다릴 필요가 있을지도 모른다는 문제가 있습니다. 따라서 지역 우선로드 이미지, 그것을 시각화하는 방법을 원하는 수 있습니까?
 * <p>
 * 그래서, 특히 경우, 우선 순위로드 사진 요청에 따라, 요청의 목록을 유지하기로 결정했다.
 * <p>
 * 제 1 메모리 캐시 화상 여부를 결정하고, 만약 그렇다면, 메모리로부터 화상을 제거한다. 그렇지 않은 경우, 픽쳐 요청 객체는 요청리스트에 추가 구축.
 * (원래는이 요청이있는 경우 원본 목록 새로리스트의 마지막에 추가, 삭제 그래서 요청의 우선 순위를 보장 할 수있다)
 * 
 * <pre>
 * 사진 요청 목록과 함께 다음과 같은 규칙에 따라 스레드 풀 :
 * <p>
 * 1. 요청리스트 경우 요청 수가 스레드 풀 유휴 스레드의 수보다 적은, 스레드 풀 유휴 스레드 요청 순서 (요청리스트가 모든 요청이 동시에 구현되는 이때)
 * <p>
 * 2. 요청리스트가 수가 스레드 풀 유휴 스레드의 수보다 더 큰 경우 유휴 스레드는 (높은 우선 순위를 요청한 후, 요청 될 수있는 순서에 따라 우선 순위)의 요청에 높은 우선 순위를 할당 할
 * <p>
 * 요청이 태스크를 제거하는 요청리스트로부터의 요청의 이행 후이면 동안 요청 작업 목록뿐만 아니라 미처리는, 상기 규칙에 따라 상기 요청을 계속 처리하는 경우.
 * 
 * @author likebamboo
 */
public class LocalImageLoader {
    /**
     * 캐시 메모리 LRU
     */
    private LruCache<String, Bitmap> mMemoryCache = null;

    /**
     * 싱클톤
     */
    private static LocalImageLoader mInstance = new LocalImageLoader();

    /**
     * 스레드 풀의 고정 된 수의 만들기
     */
    private ThreadPoolExecutor mThreadPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(3);

    /**
     * 이미지 요청리스트, 스케줄링
     */
    private ArrayList<ImageRequest> mImagesList = new ArrayList<ImageRequest>();

    /**
     * 리스트 요청 상태를 요청
     */
    private ArrayList<ImageRequest> mOnLoadingList = new ArrayList<ImageRequest>();

    /**
     * 스케줄 상태에 있는지
     */
    private boolean onDispath = false;

    private LocalImageLoader() {
        //응용 프로그램 최대 메모리를 가져옵니다
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        //최대 메모리 1/4 사진을 저장
        final int cacheSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            //각 사진의 크기를 가져옵니다
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    /**
     * 인스턴스 얻기
     * 
     * @return
     */
    public static LocalImageLoader getInstance() {
        return mInstance;
    }

    /**
     * 지역 이미지로드, 사진이 잘립니다되지 않습니다
     * 
     * @param path
     * @param mCallBack
     * @return
     */
    public Bitmap loadImage(final String path, final ImageCallBack mCallBack) {
        return this.loadImage(path, null, mCallBack);
    }

    /**
     * 이 방법은 이미지 뷰 MPOINT 폭과 높이를 패키징하기 위해 사용되는 지역의 이미지를로드하는 데 사용되는, 우리는 줌 제어 비트 맵 이미지 뷰의 크기에 기초 할 것이다
     * 
     * @param path
     * @param point
     * @param callBack
     * @return
     */
    @SuppressLint("HandlerLeak")
    public Bitmap loadImage(final String path, final Point point, final ImageCallBack callBack) {
        // 먼저 메모리를 얻기 Bitmap
        Bitmap bitmap = getBitmapFromMemCache(path);

        // 비트 맵의 캐시 메모리에 있지 않은 경우, 예약 된 작업의 목록에 추가된다
        if (bitmap == null) {
            addImageRequest(new ImageRequest(path, point, callBack));
        }
        //Logger.d("bitmap="+bitmap);
        return bitmap;
    }

    /**
     * 이미지 요청 작업 추가
     */
    private void addImageRequest(ImageRequest item) {
        if (null == item || TextUtils.isEmpty(item.getPath())) {
            return;
        }
        synchronized (mImagesList) {
            mImagesList.remove(item);
            mImagesList.add(item);
        }
        // 현재 상태를 예약하지 않은 경우, 일정을 시작합니다
        Logger.d("onDispath="+onDispath);
        if (!onDispath) {
            dispatch();
        }
    }

    /**
     * 사진 요청 작업을 삭제하려면
     * 
     * @param path
     */
    private void removeImageRequest(ImageRequest item) {
        if (item == null || TextUtils.isEmpty(item.getPath())) {
            return;
        }
        synchronized (mImagesList) {
            mImagesList.remove(item);
            if (mImagesList.size() > 0) {
                dispatch();
            } else {
                //예약 일시중단
                onDispath = false;
            }
        }
    }

    /**
     * 작업 스케줄링
     */
    private void dispatch() {
        // 예약 시작
        onDispath = true;

        //현재 스레드 풀이 가득 찬 경우, 더 이상 작업 처리 요청
        if (mThreadPool.getActiveCount() >= mThreadPool.getCorePoolSize()) {
            return;
        }
        // 유휴 스레드 수
        int spareThreads = mThreadPool.getCorePoolSize() - mThreadPool.getActiveCount();

        //Logger.d("mImagesList.size()="+mImagesList.size()+" spareThreads="+spareThreads);

        // 리스트는 유휴 스레드 요청의 개수보다 작으면，순차 처리 요구
        synchronized (mImagesList) {
            if (mImagesList.size() < spareThreads) {
                for (ImageRequest item : mImagesList) {
                    execute(item);
                }
            } else { // 번호 스레드 수 유휴 요청리스트, 처리 요구의 순서 미만이면
                for (int i = mImagesList.size() - 1; i >= mImagesList.size() - spareThreads; i--) {
                    execute(mImagesList.get(i));
                }
            }
        }

        onDispath=false;
    }

    /**
     * 로드 사진 작업 실행
     * 
     * @param request
     */
    private void execute(final ImageRequest request) {
        //현재 처리중인경우에는 무시
        if (mOnLoadingList.contains(request)) {
            return;
        }

        final ImageHandler handler = new ImageHandler(this, request);
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mOnLoadingList.add(request);

                Logger.d("excute(),mOnLoadingList.size()="+mOnLoadingList.size());

                Point size = request.getSize();
                if (size == null || size.x == 0 || size.y == 0) {
                    size = DeviceUtil.getDeviceSize(ICApplication.getContext());
                }
                // 먼저 썸네일 이미지를 얻음
                Bitmap mBitmap = decodeThumbBitmapForFile(request.getPath(), size.x, size.y, false);
                Message msg = handler.obtainMessage();
                msg.obj = mBitmap;
                handler.sendMessage(msg);

                mOnLoadingList.remove(request);
                // 픽처 메모리 캐시에 추가되고
                addBitmapToMemoryCache(request.getPath(), mBitmap);
            }
        });
    }

    /**
     * 비트 맵 메모리 캐시를 추가합니다
     * 
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            synchronized (mMemoryCache) {
                mMemoryCache.put(key, bitmap);
            }
        }
    }

    /**
     * key에따라  메모리 이미지를 얻으려면
     * 
     * @param key : path
     * @return
     */
     private Bitmap getBitmapFromMemCache(String key) {
        Bitmap bitmap = null;
        // 하드 참조 캐시로 시작하기
        synchronized (mMemoryCache) {
            bitmap = mMemoryCache.get(key);
            if (bitmap != null) {
                // 비트 맵을 발견 한 후, 최종적으로 LRU 알고리즘에서 삭제되는 것을 보장하기 위해, 제일의 LinkedHashMap 이동합니다.
                mMemoryCache.remove(key);
                mMemoryCache.put(key, bitmap);
                return bitmap;
            }
        }
        return null;
    }

    /**
     * View에 따르면(주로 ImageView)썸네일의 폭과 높이가 사진을 얻을 수 있습니다
     * 
     * @param path
     * @param viewWidth
     * @param viewHeight
     * @param isHighQuality 고품질인지
     * @return
     */
    private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight,
            boolean isHighQuality) {
        File f = new File(path);
        if (!f.exists()) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
         //읽어드리려는 이미지의 해상도를 알아내기 위해서
         //BitmapFactory.Options의 inJustDecodeBounds = true 로 셋팅
         //이미지를 메모리에 올려놓지 않고 해상도만 알아낼 수 있다
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // BitmapFactofy.Options의 inSampleSize 파라메터를 이용해서 이미지의 해상도를 줄임
        options.inSampleSize = computeScale(options, viewWidth, viewHeight);
        if (!isHighQuality) {
            // PS:메모리를 줄이기 위해 사이즈를 줄인다.
            options.inSampleSize += options.inSampleSize / 2 + 2;
        }
        // 화질
        options.inPreferredConfig = Config.RGB_565;

        // 실제 이미지를 읽어 메모리에 올릴 때는 inJustDecodeBounds를 false로 해둔다
        options.inJustDecodeBounds = false;

        options.inPurgeable = true;
        options.inInputShareable = true;
        // 자원 사진에 대한 액세스
        try {
            return BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <p>
     * 구글의 이미지 캐시 데모
     * <p>
     * 체크아웃：http://developer.android.com/training/displaying-bitmaps/index.html
     * <p>
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options}
     * object when decoding bitmaps using the decode* methods from
     * {@link BitmapFactory}. This implementation calculates the closest
     * inSampleSize that will result in the final decoded bitmap having a width
     * and height equal to or larger than the requested width and height. This
     * implementation does not ensure a power of 2 is returned for inSampleSize
     * which can be faster when decoding but results in a larger bitmap which
     * isn't as useful for caching purposes.
     * 
     * @param options An options object with out* params already populated (run
     *            through a decode* method with inJustDecodeBounds==true
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private int computeScale(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float)height / (float)reqHeight);
            final int widthRatio = Math.round((float)width / (float)reqWidth);

            // Choose the smallest ratio as inSampleSize value,
            // this will guarantee a final image
            // with both dimensions larger than or equal to the requested
            // height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down
            // further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static class ImageHandler extends Handler {
        private final WeakReference<LocalImageLoader> mActivity;

        private final WeakReference<ImageRequest> mRequest;

        public ImageHandler(LocalImageLoader activity, ImageRequest request) {
            mActivity = new WeakReference<LocalImageLoader>(activity);
            mRequest = new WeakReference<ImageRequest>(request);
        }

        @Override
        public void handleMessage(Message msg) {
            ImageRequest request = mRequest.get();
            if (request != null) {
                LocalImageLoader activity = mActivity.get();
                if (activity != null) {
                    activity.removeImageRequest(request);
                }
                request.getCallBack().onImageLoader((Bitmap)msg.obj, request.getPath());
            }
        }
    }


    /**
     * The default implementation of ImageListener which handles basic functionality
     * of showing a default image until the network response is received, at which point
     * it will switch to either the actual image or the error image.
     * @param view The imageView that the listener is associated with.
     * @param defaultImageResId Default image resource ID to use, or 0 if it doesn't exist.
     * @param errorImageResId Error image resource ID to use, or 0 if it doesn't exist.
     */
    public static ImageCallBack getImageListener(final ImageView view, final Object tag, final int defaultImageResId,
            final int errorImageResId) {
        if (view != null) {
            view.setImageResource(defaultImageResId);
            view.setTag(tag);
        }
        return new ImageCallBack() {
            @Override
            public void onImageLoader(Bitmap bitmap, String path) {
                if (view == null || !("" + path).equals(view.getTag())) {
                    return;
                }
                if (bitmap != null) {
                    view.setImageBitmap(bitmap);
                } else {
                    view.setImageResource(errorImageResId);
                }
            }
        };
    }
    
    /**
     * 지역 이미지를 콜백 인터페이스를로드
     */
    public interface ImageCallBack {
        /**
         * 비트 맵 및 그림 경로 콜백을 로딩 완료되면
         * 
         * @param bitmap
         * @param path
         */
        public void onImageLoader(Bitmap bitmap, String path);
    }
}
