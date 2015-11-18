/**
 * SDcardUtil.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 273.cn. All rights reserved.
 */

package com.likebamboo.imagechooser.utils;

import android.os.Environment;

/**
 * SD 카드의 동작 툴
 * 
 * @author likebamboo
 */
public class SDcardUtil {

    /**
     * SD 카드가 있는지 확인
     * 
     * @return
     */
    public static boolean hasExternalStorage() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}
