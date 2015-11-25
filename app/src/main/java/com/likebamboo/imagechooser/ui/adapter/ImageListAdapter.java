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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.likebamboo.imagechooser.R;
import com.likebamboo.imagechooser.loader.LocalImageLoader;
import com.likebamboo.imagechooser.utils.Util;
import com.likebamboo.imagechooser.widget.MyImageView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * gridview 체크박스기능 어댑터
 * 
 * @author likebamboo
 */
public class ImageListAdapter extends BaseAdapter {
    /**
     * Context 객체
     */
    private Context mContext = null;

    /**
     * 사진 목록
     */
    private ArrayList<String> mDataList = new ArrayList<String>();

    /**
     * 사진 목록을 선택
     */
    private ArrayList<String> mSelectedList = new ArrayList<String>();

    public ImageListAdapter(Context context, ArrayList<String> list) {
        mDataList = list;
        mContext = context;
        mSelectedList = Util.getSeletedImages(context);
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public String getItem(int position) {
        if (position < 0 || position > mDataList.size()) {
            return null;
        }
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Logger.d("getView " + position + " " + view);

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.image_list_item, null);
            holder.mImageIv = (MyImageView)view.findViewById(R.id.list_item_iv);
            holder.mClickArea = view.findViewById(R.id.list_item_cb_click_area);
            holder.mSelectedCb = (CheckBox)view.findViewById(R.id.list_item_cb);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        final String path = getItem(position);
        // 사진로드
        holder.mImageIv.setTag(path);
        // 사진로드
        // 클래스로딩 지역이미지 NativeImageLoader
        Bitmap bitmap = LocalImageLoader.getInstance().loadImage(path, holder.mImageIv.getPoint(),
                LocalImageLoader.getImageListener(holder.mImageIv, path, R.drawable.pic_thumb, R.drawable.pic_thumb));
        if (bitmap != null) {
            holder.mImageIv.setImageBitmap(bitmap);
        } else {
            holder.mImageIv.setImageResource(R.drawable.pic_thumb);
        }

        holder.mSelectedCb.setChecked(false);
        // 사진선택
        for (String selected : mSelectedList) {
            if (selected.equals(path)) {
                holder.mSelectedCb.setChecked(true);
            }
        }

        // 클릭
        holder.mClickArea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = holder.mSelectedCb.isChecked();
                holder.mSelectedCb.setChecked(!checked);
                if (!checked) {
                    addImage(path);
                } else {
                    deleteImage(path);
                }
            }
        });

        return view;
    }

    /**
     * 선택 목록에 그림 주소 추가
     * 
     * @param path
     */
    private void addImage(String path) {
        if (mSelectedList.contains(path)) {
            return;
        }
        mSelectedList.add(path);
    }

    /**
     * 사진이 선택되어있는 주소 목록에서 삭제됩니다
     * 
     * @param path
     */
    private void deleteImage(String path) {
        mSelectedList.remove(path);
    }

    /**
     * 선택한 사진의 목록을 가져옵니다
     * 
     * @return
     */
    public ArrayList<String> getSelectedImgs() {
        return mSelectedList;
    }

    static class ViewHolder {
        public MyImageView mImageIv;

        public View mClickArea;

        public CheckBox mSelectedCb;

    }

}
