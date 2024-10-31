package com.example.videodemo;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PathUtil {

    private static final String TAG = PathUtil.class.getSimpleName();
    public static final String VIDEO_PATH = Environment.getExternalStorageDirectory() + "/VideoDemo/" + "video";
    public static final String AUDIO_PATH = Environment.getExternalStorageDirectory() + "/VideoDemo/" + "audio";
    public static final String PHOTO_PATH = Environment.getExternalStorageDirectory() + "/VideoDemo/" + "photo";

    public static void createFileDir() {
        Log.d(TAG, "videoPath:" + VIDEO_PATH);
        List<String> pathList = new ArrayList<>();
        pathList.add(VIDEO_PATH);
        pathList.add(AUDIO_PATH);
        pathList.add(PHOTO_PATH);
        for (String path : pathList) {
            File file = new File(path);
            if (file.exists())
                break;
            boolean b = file.mkdirs();
            Log.d(TAG, b ? "success" : "false");
        }
    }
}
