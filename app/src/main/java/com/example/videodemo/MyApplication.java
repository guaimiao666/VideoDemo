package com.example.videodemo;

import android.app.Application;

import com.example.videodemo.activity.VideoPreviewActivity;

public class MyApplication extends Application {

    private VideoPreviewActivity videoPreviewActivity;

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
}
