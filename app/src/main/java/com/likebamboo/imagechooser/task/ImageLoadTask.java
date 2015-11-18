/**
 * ImageLoadTask.java
 * ImageSelector
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;

import com.likebamboo.imagechooser.listener.OnTaskResultListener;
import com.likebamboo.imagechooser.log.L;
import com.likebamboo.imagechooser.model.ImageGroup;

import java.io.File;
import java.util.ArrayList;

/**
 * contentProvider 비동기 작업을 사용하여 이미지를 스캔
 * 
 * @author likebamboo
 */
public class ImageLoadTask extends BaseTask {

    /**
     * Context 객체
     */
    private Context mContext = null;

    /**
     * 사진을 저장하여 <폴더, 폴더 이미지 목록> 값 쌍
     */
    private ArrayList<ImageGroup> mGruopList = new ArrayList<ImageGroup>();

    public ImageLoadTask(Context context) {
        super();
        mContext = context;
        result = mGruopList;
    }

    public ImageLoadTask(Context context, OnTaskResultListener listener) {
        super();
        mContext = context;
        result = mGruopList;
        setOnResultListener(listener);
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = mContext.getContentResolver();
        // JPEG 및 PNG 이미지
        StringBuilder selection = new StringBuilder();
        selection.append(Media.MIME_TYPE).append("=?");
        selection.append(" or ");
        selection.append(Media.MIME_TYPE).append("=?");

        Cursor mCursor = null;
        try {
            // 초기화 커서
            mCursor = mContentResolver.query(mImageUri, null, selection.toString(), new String[] {
                    "image/jpeg", "image/png"
            }, Media.DATE_TAKEN);
            // 순회결과
            while (mCursor.moveToNext()) {
                // 사진경로얻기
                String path = mCursor.getString(mCursor.getColumnIndex(Media.DATA));

                // 사진 경로 얻기
                File file = new File(path);
                String parentName = "";
                if (file.getParentFile() != null) {
                    parentName = file.getParentFile().getName();
                } else {
                    parentName = file.getName();
                }
                //imageGroup 객체를 구축
                ImageGroup item = new ImageGroup();
                //imageGroup 폴더 이름을 설정
                item.setDirName(parentName);

                //이미지 그룹 폴더 이름을 설정
                int searchIdx = mGruopList.indexOf(item);
                if (searchIdx >= 0) {
                    // 그렇다면, 그룹 번호 +1
                    ImageGroup imageGroup = mGruopList.get(searchIdx);
                    imageGroup.addImage(path);
                } else {
                    // 그렇지 않으면, 객체는 그룹리스트에 추가된다
                    item.addImage(path);
                    mGruopList.add(item);
                }
            }
        } catch (Exception e) {
            // 输出日志
            L.e(e);
            return false;
        } finally {
            // 关闭游标
            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }
        }
        return true;
    }
}
