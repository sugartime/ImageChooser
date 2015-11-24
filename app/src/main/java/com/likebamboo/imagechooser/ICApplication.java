/**
 * ICApplication.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Application，프로그램항목
 * Application Class란 어플리케이션 컴포넌트들 사이에서 공동으로 멤버들을 사용할 수 있게 해주는  공유 클래스
 *
 * manifest 에 정의 되어있음
 * 
 * @author likebamboo
 */
public class ICApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initImageLoader(getApplicationContext());
    }

    /**
     * Application 적용 가져오기  Context객체
     * 
     * @return
     */
    public static Context getContext() {
        return mContext;
    }

    /**
     * ImageLoader 초기화
     * 
     * @param context
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                //.discCacheFileNameGenerator(new Md5FileNameGenerator())  //disc->disk 로 변경
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(8 * 1024 * 1024) //disc->disk 로 변경
                .tasksProcessingOrder(QueueProcessingType.LIFO);
        // 당신은 모드를 디버깅하는 경우，출력로그，그렇지 않으면 아무 출력하지않음
        if (BuildConfig.DEBUG) {
            builder.writeDebugLogs();
        }
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(builder.build());
    }

}
