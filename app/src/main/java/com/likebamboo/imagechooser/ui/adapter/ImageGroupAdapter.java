/**
 * ImageGroupAdapter.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.likebamboo.imagechooser.R;
import com.likebamboo.imagechooser.loader.LocalImageLoader;
import com.likebamboo.imagechooser.loader.LocalImageLoader.ImageCallBack;
import com.likebamboo.imagechooser.model.ImageGroup;
import com.likebamboo.imagechooser.widget.MyImageView;

import java.util.List;

/**
 * 그룹 포토 어댑터
 * 
 * @author likebamboo
 */
public class ImageGroupAdapter extends BaseAdapter {
    /**
     * Context 객체
     */
    private Context mContext = null;

    /**
     * 사진 목록
     */
    private List<ImageGroup> mDataList = null;

    /**
     * 컨테이너
     */
    private View mContainer = null;

    public ImageGroupAdapter(Context context, List<ImageGroup> list, View container) {
        mDataList = list;
        mContext = context;
        mContainer = container;
    }

    // Adapter가 관리할 Data의 개수 설정
    @Override
    public int getCount() {
        return mDataList.size();
    }

    // Adapter가 관리하는 Data의 Item 의 Position을 <객체> 형태로 얻음
    @Override
    public ImageGroup getItem(int position) {
        if (position < 0 || position > mDataList.size()) {
            return null;
        }
        return mDataList.get(position);
    }

    // Adapter가 관리하는 Data의 Item 의 position 값의 ID 를 얻음
    @Override
    public long getItemId(int position) {
        return position;
    }

    // ListView의 뿌려질 한줄의 Row 설정
    // - position : 행의 index를 의미
    // - convertView : 행 전체를 나타내는 뷰를 의미한다.
    // - parent : 어댑터를 가지고 있는 부모 뷰를 의미한다.
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        //ViewHolder
        // 뷰들을 홀더에 꼽아놓듯이 보관하는 객체
        // 각각의 Row를 그려낼 때 그 안의 위젯들의 속성을 변경하기 위해 findViewById를 호출하는데 이것의 비용이 큰것을 줄이기 위해 사용
        ViewHolder holder = null;

        // 캐시된 뷰가 없을 경우 새로 생성하고 뷰홀더를 생성한다
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.image_group_item, null);
            holder.mImageIv = (MyImageView)view.findViewById(R.id.group_item_image_iv);
            holder.mTitleTv = (TextView)view.findViewById(R.id.group_item_title_tv);
            holder.mCountTv = (TextView)view.findViewById(R.id.group_item_count_tv);

            view.setTag(holder);
        } else {
            // 캐시된 뷰가 있을 경우 저장된 뷰홀더를 사용한다
            holder = (ViewHolder)view.getTag();
        }

        ImageGroup item = getItem(position);
        if (item != null) {
            // 사진 경로
            String path = item.getFirstImgPath();    //첫번째사진
            // 이름
            holder.mTitleTv.setText(item.getDirName());
            // 계산
            holder.mCountTv.setText(mContext.getString(R.string.image_count, item.getImageCount()));
            holder.mImageIv.setTag(path);
            // 사진로드
            // NativeImageLoade 사용, 이미지 로딩
            Bitmap bitmap = LocalImageLoader.getInstance().loadImage(path,holder.mImageIv.getPoint(), new ImageCallBack() {
                        @Override
                        public void onImageLoader(Bitmap bitmap, String path) {
                            ImageView mImageView = (ImageView)mContainer.findViewWithTag(path);
                            if (bitmap != null && mImageView != null) {
                                mImageView.setImageBitmap(bitmap);
                            }
                        }
                    });
            if (bitmap != null) {
                holder.mImageIv.setImageBitmap(bitmap);
            } else {
                holder.mImageIv.setImageResource(R.drawable.pic_thumb);
            }
        }
        return view;
    }

    // 실행에 드는 비용을 줄일려고 private으로 선언한 뒤에 getter/setter를 사용하는 방식을 취하지 않고
    // 맴버변수에 직접적으로 접근
    static class ViewHolder {
        public MyImageView mImageIv;

        public TextView mTitleTv;

        public TextView mCountTv;
    }

}
