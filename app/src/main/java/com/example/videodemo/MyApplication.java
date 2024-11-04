package com.example.videodemo;

import android.app.Application;

import com.example.videodemo.activity.VideoPreviewActivity;

public class MyApplication extends Application {

    private VideoPreviewActivity videoPreviewActivity;
    private int cameraFacing = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public VideoPreviewActivity getVideoPreviewActivity() {
        return videoPreviewActivity;
    }

    public void setVideoPreviewActivity(VideoPreviewActivity videoPreviewActivity) {
        this.videoPreviewActivity = videoPreviewActivity;
    }

    public int getCameraFacing() {
        return cameraFacing;
    }

    public void setCameraFacing(int cameraFacing) {
        this.cameraFacing = cameraFacing;
    }
}
