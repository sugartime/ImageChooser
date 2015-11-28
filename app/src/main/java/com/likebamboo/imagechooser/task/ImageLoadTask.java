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

        /*
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.MIME_TYPE
        };
        */

        // JPEG 및 PNG 이미지
        /*
        StringBuilder selection = new StringBuilder();
        selection.append(Media.MIME_TYPE).append("=image/jpeg");
        selection.append(" or ");
        selection.append(Media.MIME_TYPE).append("=image/png");
        */

        /*
        String selection = Media.MIME_TYPE + "=image/jpeg"
                         + " OR "
                         + Media.MIME_TYPE + "=image/png";
        */

        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        };


        //ContentResolver mContentResolver = mContext.getContentResolver();

        Cursor mCursor = mContext.getContentResolver().query(mImageUri,projection,null,null,Media.DATE_TAKEN);
        try {

            if (mCursor.moveToFirst()) {

                String path;
                String bucket;
                String date;

                int pathColumn = mCursor.getColumnIndex(
                        MediaStore.Images.Media.DATA);

                int bucketColumn = mCursor.getColumnIndex(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                int dateColumn = mCursor.getColumnIndex(
                        MediaStore.Images.Media.DATE_TAKEN);




                do {

                    // Get the field values
                    path = mCursor.getString(pathColumn);
                    bucket = mCursor.getString(bucketColumn);
                    date = mCursor.getString(dateColumn);

                    //Logger.d(" path="+path+" bucket=" + bucket + "  date_taken=" + date);

                    //imageGroup = 폴더
                    ImageGroup item = new ImageGroup();
                    //imageGroup 폴더 이름을 설정
                    item.setDirName(bucket);

                    //폴더가 들어있는 리스트에 들어있는지 확인
                    int searchIdx = mGruopList.indexOf(item);

                    //폴더리스트 에 폴더가 이미 있으면
                    if (searchIdx >= 0) {
                        // 폴더에 이미지경로를 추가
                        mGruopList.get(searchIdx).addImage(path);
                    } else {
                        // 그렇지 않으면,
                        item.addImage(path); //폴더에 이미지경로 추가후
                        mGruopList.add(item); // 폴더리스트에 해당폴더 추가
                    }
                    //Logger.d("mGruopList.size()="+mGruopList.size());


                } while (mCursor.moveToNext());
            }

        }catch (Exception e) {
            // 로그
            //L.e(e);
            Logger.d("Error");
            return false;
        } finally {
            // 커서닫기
            if (mCursor != null && !mCursor.isClosed()) {
                Logger.d("Cursor Close");
                mCursor.close();
            }
        }


        return true;
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    /*
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

            // cursor 의 결과가 항상 같은 데이터를가져옴, 휴대폰을 껏다키지 않은한
            // if (mCursor.moveToFirst()) 와 do...while 문으로 변경
            Logger.d("mCursor.moveToFirst()="+mCursor.moveToFirst());
            Logger.d("result="+result);
            Logger.d("mGruopList.size()="+mGruopList.size());

            //if (mCursor.moveToFirst()) {

               // do{
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

               // }
               //while (mCursor.moveToNext());



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
    */
}
