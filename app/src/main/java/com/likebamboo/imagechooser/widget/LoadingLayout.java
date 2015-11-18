/**
 * LoadingLayout.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.likebamboo.imagechooser.R;

/**
 * 블록영역 Loading
 * 
 * @author likebamboo
 */
public class LoadingLayout extends LinearLayout {
    /**
     * 로드 ProgressBar
     */
    private ProgressBar mLoadingProgressBar = null;

    /**
     * 重试布局 레이아웃을 다시 시도
     */
    private LinearLayout mRetryLayout = null;

    /**
     * 오류메세지 TextView
     */
    private TextView mErrorTv = null;

    /**
     * 재시도 인터페이스
     */
    private IRetryListener mRetryListener = null;

    /**
     * 다시 시도할지 여부
     */
    private boolean canRetry = true;

    public interface IRetryListener {
        void onRetry();
    }

    public LoadingLayout(Context context) {
        super(context);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    /**
     * 초기화 레이아웃
     */
    private void initView() {
        mLoadingProgressBar = (ProgressBar)findViewById(R.id.loading_pb);
        mRetryLayout = (LinearLayout)findViewById(R.id.loading_fail_layout);
        mRetryLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!canRetry) {
                    return false;
                }
                // onTouch down的时候才触发重试异步线程，否则会多次触发的。
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mRetryListener != null) {
                        mRetryListener.onRetry();
                    }
                }
                return true;
            }
        });
        mErrorTv = (TextView)findViewById(R.id.loading_fail_tv);
    }

    /**
     * 로딩 포시/숨기기
     */
    public void showLoading(boolean show) {
        if (show) {
            setVisibility(View.VISIBLE);
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            mRetryLayout.setVisibility(View.GONE);
        } else {
            setVisibility(View.GONE);
        }
    }

    /**
     * 디스플레이 정보를로드하지 못했습니다
     */
    public void showFailed(CharSequence msg) {
        setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.GONE);
        mRetryLayout.setVisibility(View.VISIBLE);
        mErrorTv.setText(msg);
        mErrorTv.setTextColor(getResources().getColor(android.R.color.black));
        if (!canRetry) {
            canRetry = true;
        }
    }

    /**
     * 디스플레이 "빈"메시지
     * 
     * @param msg
     */
    public void showEmpty(CharSequence msg) {
        setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.GONE);
        mRetryLayout.setVisibility(View.VISIBLE);
        mErrorTv.setText(msg);
        mErrorTv.setTextColor(getResources().getColor(android.R.color.background_dark));
        mErrorTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        canRetry = false;
    }

    /**
     * 무게 모니터링 장치 설정
     * 
     * @param listener
     */
    public void setRetryListener(IRetryListener listener) {
        this.mRetryListener = listener;
    }

}
