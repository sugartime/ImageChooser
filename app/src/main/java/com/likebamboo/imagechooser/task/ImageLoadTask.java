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
            // 初始化游标
            mCursor = mContentResolver.query(mImageUri, null, selection.toString(), new String[] {
                    "image/jpeg", "image/png"
            }, Media.DATE_TAKEN);
            // 遍历结果
            while (mCursor.moveToNext()) {
                // 获取图片的路径
                String path = mCursor.getString(mCursor.getColumnIndex(Media.DATA));

                // 获取该图片的所在文件夹的路径
                File file = new File(path);
                String parentName = "";
                if (file.getParentFile() != null) {
                    parentName = file.getParentFile().getName();
                } else {
                    parentName = file.getName();
                }
                // 构建一个imageGroup对象
                ImageGroup item = new ImageGroup();
                // 设置imageGroup的文件夹名称
                item.setDirName(parentName);

                // 寻找该imageGroup是否是其所在的文件夹中的第一张图片
                int searchIdx = mGruopList.indexOf(item);
                if (searchIdx >= 0) {
                    // 如果是，该组的图片数量+1
                    ImageGroup imageGroup = mGruopList.get(searchIdx);
                    imageGroup.addImage(path);
                } else {
                    // 否则，将该对象加入到groupList中
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
