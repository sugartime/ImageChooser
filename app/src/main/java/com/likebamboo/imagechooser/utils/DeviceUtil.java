/**
 * DeviceUtil.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-24
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * 장치관련 도구
 * 
 * @author likebamboo
 */
public class DeviceUtil {
    private DeviceUtil() {
    }

    /**
     * 화면의 폭과 높이를 얻음
     * 
     * @return
     */
    public static Point getDeviceSize(Context ctx) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        Point size = new Point();
        size.x = dm.widthPixels;
        size.y = dm.heightPixels;
        return size;
    }
}
