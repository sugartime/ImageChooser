/**
 * OnTaskResultListener.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser.listener;

/**
 * 비동기 작업 후 콜백 인터페이스
 * 
 * @author likebamboo
 */
public interface OnTaskResultListener {
    /**
     * 콜백
     * 
     * @param success 성공
     * @param error 에러，[성공은 에러메세지가 비어있음]
     * @param result 결과
     */
    void onResult(final boolean success, final String error, final Object result);
}
