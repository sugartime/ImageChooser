/**
 * BaseTask.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser.task;

import android.os.AsyncTask;

import com.likebamboo.imagechooser.listener.OnTaskResultListener;

/**
 * 비동기 태스크 기본 클래스
 * 
 * @author likebamboo
 *
 * AsyncTask< 작업스레드가 처리할 데이터 타입 , 다이얼로그의 설정 데이터 타입, 작업의 결과로 리턴할 데이터 타입 >
 * 첫번재 데이터 타입은 doInBackground() 메소드의 파라미터 타입을 지정
 * 두번재 파라미터의 타입은 onProgressUpdate() 메소드의 파라미터 타입을 지정
 * 세번째 파라미터의 타입은 onPostExecute() 메소드의 파라미터 타입을 지정
 */
public abstract class BaseTask extends AsyncTask<Void, Void, Boolean> {

    /**
     * 오류 메시지
     */
    protected String error = "";

    /**
     * 종료여부
     */
    protected boolean interrupt = false;

    /**
     * 결과
     */
    protected Object result = null;

    /**
     * 비동기 작업 결과 콜백 인터페이스
     */
    protected OnTaskResultListener resultListener = null;

    @Override
    protected void onPostExecute(Boolean success) {
        if (!interrupt && resultListener != null) {
            resultListener.onResult(success, error, result);
        }
    }

    /**
     * 비동기 작업 취소
     */
    public void cancel() {
        super.cancel(true);
        interrupt = true;
    }

    public void setOnResultListener(OnTaskResultListener listener) {
        resultListener = listener;
    }

}
