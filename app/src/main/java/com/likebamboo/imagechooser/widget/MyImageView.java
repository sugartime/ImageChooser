/**
 * MyImageView.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser.widget;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 사용자 정의 View，onMeasure 이미지 폭 과 넓이 확인방법
 * 
 * @author likebamboo
 */
public class MyImageView extends ImageView {

    /**
     * 넓이와 높이 컨트롤
     */
    private Point mPoint = new Point();

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //커스텀 뷰 생성시 반드시 오버라이딩 해야 하는 함수
    //View의 크기를 결정할때 불리는 함수
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mPoint.x = getMeasuredWidth();      //onDraw전에 View에 크기를 알고 싶은 경우 getMeasuredWidth() 호출
        mPoint.y = getMeasuredHeight();
    }

    /**
     * 포인트 리턴
     * 
     * @return
     */
    public Point getPoint() {
        return mPoint;
    }
}
