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
import com.orhanobut.logger.Logger;

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
                Logger.d("path= " + path);

                // 사진 경로 얻기
                File file = new File(path);
                String parentName = "";
                // 폴더가 존재하면
                if (file.getParentFile() != null) {
                    //Logger.d("!! isParentFile="+file.getParentFile().getName());
                    parentName = file.getParentFile().getName();
                } else {
                    parentName = file.getName();
                }
                //imageGroup = 폴더
                ImageGroup item = new ImageGroup();
                //imageGroup 폴더 이름을 설정
                item.setDirName(parentName);

                //폴더가 들어있는 리스트에 들어있는지 확인
                int searchIdx = mGruopList.indexOf(item);

                //폴더리스트 에 폴더가 이미 있으면
                if (searchIdx >= 0) {
                    // 폴더에 이미지경로를 추가
                    ImageGroup imageGroup = mGruopList.get(searchIdx);
                    imageGroup.addImage(path);
                } else {
                    // 그렇지 않으면, 폴더에 이미지경로 추가후
                    // 폴더리스트에 해당폴더 추가
                    item.addImage(path);
                    mGruopList.add(item);
                }
            }
        } catch (Exception e) {
            // 로그
            L.e(e);
            return false;
        } finally {
            // 커서닫기
            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }
        }
        return true;
    }
}
