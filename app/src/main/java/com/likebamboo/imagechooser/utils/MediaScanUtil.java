package com.likebamboo.imagechooser.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

/**
 * Created by End-User on 2015-12-10.
 */
public class MediaScanUtil {

    private Context mContext;

    private String mPath;

    private MediaScannerConnection mMediaScanner;
    private MediaScannerConnectionClient mMediaScannerClient;

    public static MediaScanUtil newInstance(Context context) {
        return new MediaScanUtil(context);
    }

    private MediaScanUtil(Context context) {
        mContext = context;
    }

    public void mediaScanning(final String path) {

        if (mMediaScanner == null) {
            mMediaScannerClient = new MediaScannerConnectionClient() {

                @Override
                public void onMediaScannerConnected() {
                    mMediaScanner.scanFile(mPath, null); // 디렉토리
                    // 가져옴
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {

                }
            };
            mMediaScanner = new MediaScannerConnection(mContext, mMediaScannerClient);
        }

        mPath = path;
        mMediaScanner.connect();
    }
}
