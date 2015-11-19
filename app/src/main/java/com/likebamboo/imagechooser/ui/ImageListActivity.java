/**
 * ImageListActivity.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-23
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.likebamboo.imagechooser.R;
import com.likebamboo.imagechooser.ui.adapter.ImageListAdapter;
import com.likebamboo.imagechooser.utils.Util;

import java.util.ArrayList;

/**
 * 모든 사진 목록의 폴더
 * 
 * @author likebamboo
 */
public class ImageListActivity extends BaseActivity implements OnItemClickListener {

    /**
     * title
     */
    public static final String EXTRA_TITLE = "extra_title";

    /**
     * 사진 목록 extra
     */
    public static final String EXTRA_IMAGES_DATAS = "extra_images";

    /**
     * 사진 목록 GridView
     */
    private GridView mImagesGv = null;

    /**
     * 사진 주소 데이터 소스
     */
    private ArrayList<String> mImages = new ArrayList<String>();

    /**
     * 어댑터
     */
    private ImageListAdapter mImageAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }

        initView();
        if (getIntent().hasExtra(EXTRA_IMAGES_DATAS)) {
            mImages = getIntent().getStringArrayListExtra(EXTRA_IMAGES_DATAS);
            setAdapter(mImages);
        }
    }

    /**
     * 초기화 인터페이스 요소
     */
    private void initView() {
        mImagesGv = (GridView)findViewById(R.id.images_gv);
    }

    /**
     * 어댑터 초기화 셋팅
     * 
     * @param datas
     */
    private void setAdapter(ArrayList<String> datas) {
        mImageAdapter = new ImageListAdapter(this, datas);
        mImagesGv.setAdapter(mImageAdapter);
        mImagesGv.setOnItemClickListener(this);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
     * .AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent i = new Intent(this, ImageBrowseActivity.class);
        i.putExtra(ImageBrowseActivity.EXTRA_IMAGES, mImages);
        i.putExtra(ImageBrowseActivity.EXTRA_INDEX, arg2);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if (mImageAdapter != null) {
            Util.saveSelectedImags(this, mImageAdapter.getSelectedImgs());
        }
        super.onBackPressed();
    }

}
