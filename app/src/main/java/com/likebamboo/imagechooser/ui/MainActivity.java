/**
 * MainActivity.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser.ui;

import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.likebamboo.imagechooser.R;
import com.likebamboo.imagechooser.listener.OnTaskResultListener;
import com.likebamboo.imagechooser.model.ImageGroup;
import com.likebamboo.imagechooser.task.ImageLoadTask;
import com.likebamboo.imagechooser.ui.adapter.ImageGroupAdapter;
import com.likebamboo.imagechooser.utils.SDcardUtil;
import com.likebamboo.imagechooser.utils.TaskUtil;
import com.likebamboo.imagechooser.widget.LoadingLayout;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * 사진은 메인 인터페이스를 선택, 모든 사진 폴더를 나열
 * 
 * @author likebamboo
 */
public class MainActivity extends BaseActivity implements OnItemClickListener {
    /**
     * loading 레이아웃
     */
    private LoadingLayout mLoadingLayout = null;

    /**
     * 사진 그룹의 GridView
     */
    private GridView mGroupImagesGv = null;

    /**
     * 어댑터
     */
    private ImageGroupAdapter mGroupAdapter = null;

    /**
     * 이미지 스캔 일반 작업
     */
    private ImageLoadTask mLoadTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        loadImages();
    }

    /**
     * 초기화 인터페이스 요소
     */
    private void initView() {
        mLoadingLayout = (LoadingLayout)findViewById(R.id.loading_layout);
        mGroupImagesGv = (GridView)findViewById(R.id.images_gv);
    }

    /**
     *  사진로드
     */
    private void loadImages() {

        Logger.d("LoadImages");

        mLoadingLayout.showLoading(true);
        Logger.d("SDcardUtil.hasExternalStorage():"+SDcardUtil.hasExternalStorage());
        if (!SDcardUtil.hasExternalStorage()) {
            mLoadingLayout.showEmpty(getString(R.string.donot_has_sdcard));
            return;
        }

        // 스레드실행
        if (mLoadTask != null && mLoadTask.getStatus() == Status.RUNNING) {
            return;
        }

        mLoadTask = new ImageLoadTask(this, new OnTaskResultListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResult(boolean success, String error, Object result) {
                mLoadingLayout.showLoading(false);
                // 로드성공
                if (success && result != null && result instanceof ArrayList) {
                    setImageAdapter((ArrayList<ImageGroup>)result);
                } else {
                    // 로드 오류 메시지를 표시 할 수 없습니다
                    mLoadingLayout.showFailed(getString(R.string.loaded_fail));
                }
            }
        });
        TaskUtil.execute(mLoadTask);
    }

    /**
     * GridView 어댑터
     * 
     * @param data
     */
    private void setImageAdapter(ArrayList<ImageGroup> data) {
        if (data == null || data.size() == 0) {
            mLoadingLayout.showEmpty(getString(R.string.no_images));
        }
        mGroupAdapter = new ImageGroupAdapter(this, data, mGroupImagesGv);
        mGroupImagesGv.setAdapter(mGroupAdapter);
        mGroupImagesGv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
        ImageGroup imageGroup = mGroupAdapter.getItem(position);
        if (imageGroup == null) {
            return;
        }
        ArrayList<String> childList = imageGroup.getImages();
        Intent mIntent = new Intent(MainActivity.this, ImageListActivity.class);
        mIntent.putExtra(ImageListActivity.EXTRA_TITLE, imageGroup.getDirName());
        mIntent.putStringArrayListExtra(ImageListActivity.EXTRA_IMAGES_DATAS, childList);
        startActivity(mIntent);
    }
}
