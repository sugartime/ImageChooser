/**
 * ConfigUtil.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-24
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 구성 정보 (sharedPreference) 도구
 * 
 * @author likebamboo
 * @date 2014-4-26
 */
public class ConfigUtil {

    /**
     * 프로필 이름
     */
    private static String PREF_NAME = "ic_config";

    /**
     * 선택한 이미지 목록 구성 항목의 이름
     */
    public static String C_SELECTED_IMAGES = "c_selected_images";

    /**
     * 
     */
    private static ConfigUtil mInstance = null;

    /**
     * Context 객체
     */
    private Context mContext = null;

    /**
     * 
     */
    protected SharedPreferences mSettings = null;

    /**
     * 
     */
    protected SharedPreferences.Editor mEditor = null;

    /**
     * 자물쇠
     */
    private static Object lock = new Object();

    private ConfigUtil(Context context) {
        mContext = context;
        mSettings = mContext.getSharedPreferences(PREF_NAME, 0);
        mEditor = mSettings.edit();

    }

    /**
     * 싱글톤 리턴
     * 
     * @param context
     * @return
     */
    public static ConfigUtil getInstance(Context context) {
        if (mInstance == null) {
            synchronized (lock) {
                if (mInstance == null) {
                    mInstance = new ConfigUtil(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public boolean contains(String key) {
        return mSettings.contains(key);
    }

    public String get(String key) {
        return mSettings.getString(key, "");
    }

    public String get(String key, String defValue) {
        return mSettings.getString(key, defValue);
    }

    public void save(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public void remove(String key) {
        mEditor.remove(key);
        mEditor.commit();
    }

    public void clear() {
        mEditor.clear();
        mEditor.commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mSettings.getBoolean(key, defValue);
    }

    public void saveBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public int getInt(String key, int defValue) {
        return mSettings.getInt(key, defValue);
    }

    public void saveInt(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public long getLong(String key, long defValue) {
        return mSettings.getLong(key, defValue);
    }

    public void saveLong(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

}
