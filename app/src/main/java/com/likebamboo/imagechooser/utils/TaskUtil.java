/**
 * TaskUtil.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 273.cn. All rights reserved.
 */

package com.likebamboo.imagechooser.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

import com.orhanobut.logger.Logger;

/**
 * AsyncTask 실행도구
 * 
 * @author likebamboo
 */
public class TaskUtil {

    /**
     * 비동기작업수행
     * <p>
     * android 2.3 이하, 사용 실행 방법
     * <p>
     * android 3.0 이상버전은 executeOnExecutor 를 사용
     * 
     * @param task
     * @param params
     */
    @SuppressLint("NewApi")
    public static <Params, Progress, Result> void execute(AsyncTask<Params, Progress, Result> task,
            Params... params) {

        Logger.d("Build.VERSION.SDK_INT :"+Build.VERSION.SDK_INT);

        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);  //execute 가 실행되면 onPreExecute() -> doInBackgound() 순으로 실행된다.
        }
    }
}
