/**
 * ImageBrowseActivity.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.likebamboo.imagechooser.R;
import com.likebamboo.imagechooser.ui.adapter.ImagePagerAdapter;

import java.util.ArrayList;

/**
 * 큰이미지 Activity
 * 
 * @author likebamboo
 */
public class ImageBrowseActivity extends AppCompatActivity {
    /**
     * 사진검색
     */
    public static final String EXTRA_IMAGES = "extra_images";

    /**
     * 위치
     */
    public static final String EXTRA_INDEX = "extra_index";

    /**
     * 데이터 소스 목록 사진
     */
    private ArrayList<String> mDatas = new ArrayList<String>();

    /**
     * 인터페이스로 색인 할 때
     */
    private int mPageIndex = 0;

    /**
     * 사진 어댑터
     */
    private ImagePagerAdapter mImageAdapter = null;

    /**
     * viewpager
     */
    private ViewPager mViewPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browse);
        mViewPager = (ViewPager)findViewById(R.id.image_vp);
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_IMAGES)) {
            mDatas = intent.getStringArrayListExtra(EXTRA_IMAGES);
            mPageIndex = intent.getIntExtra(EXTRA_INDEX, 0);
            mImageAdapter = new ImagePagerAdapter(mDatas);
            mViewPager.setAdapter(mImageAdapter);
            mViewPager.setCurrentItem(mPageIndex);
        }
    }

}
