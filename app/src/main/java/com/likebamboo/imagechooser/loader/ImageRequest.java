/**
 * ImageRequest.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-25
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser.loader;

import android.graphics.Point;

/**
 * 지역 사진 요청 패키지
 * 
 * @author likebamboo
 */
public class ImageRequest {
    /**
     * 사진경로
     */
    private String mPath = "";

    /**
     * 사진size
     */
    private Point mSize = null;

    /**
     * 화상 보정을 한 후 로드
     */
    private LocalImageLoader.ImageCallBack mCallBack = null;

    public ImageRequest(String mPath, Point mSize, LocalImageLoader.ImageCallBack mCallBack) {
        super();
        this.mPath = mPath;
        this.mSize = mSize;
        this.mCallBack = mCallBack;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public Point getSize() {
        return mSize;
    }

    public void setSize(Point size) {
        this.mSize = size;
    }

    public LocalImageLoader.ImageCallBack getCallBack() {
        return mCallBack;
    }

    public void setCallBack(LocalImageLoader.ImageCallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ImageRequest other = (ImageRequest)obj;
        if (this.mPath.equals(other.mPath)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ImageRequest [mPath=" + mPath + ", mSize=" + mSize;
    }

}
