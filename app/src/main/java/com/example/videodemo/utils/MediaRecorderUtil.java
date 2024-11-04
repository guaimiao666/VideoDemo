package com.example.videodemo.utils;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;

import com.example.videodemo.MyApplication;
import com.example.videodemo.manager.CameraManager;

import java.io.IOException;

public class MediaRecorderUtil {

    private static MediaRecorderUtil mediaRecorderUtil = null;
    private MyApplication myApp;
    private MediaRecorder mediaRecorder;
    private Context mContext;
    private Camera camera;

    public MediaRecorderUtil(Context context) {
        this.mContext = context;
        this.myApp = (MyApplication) context.getApplicationContext();
    }

    public static MediaRecorderUtil getInstance(Context context) {
        if (mediaRecorderUtil == null) {
            mediaRecorderUtil = new MediaRecorderUtil(context);
        }
        return mediaRecorderUtil;
    }

    public void startRecord() {
        mediaRecorder = new MediaRecorder();
        camera = CameraManager.getInstance(mContext).getCamera();
        if (camera != null) {
            camera.unlock();
            mediaRecorder.setCamera(camera);
        }
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setVideoSize(1920, 1080);
        mediaRecorder.setVideoEncodingBitRate(8 * 1920 * 1080);
        mediaRecorder.setVideoFrameRate(30);
        if (myApp.getCameraFacing() == 0) {
            mediaRecorder.setOrientationHint(90);
        } else {
            mediaRecorder.setOrientationHint(270);
        }
        mediaRecorder.setOutputFile(PathUtil.VIDEO_PATH + "/" + DateFormatUtil.getCurrentTime() + ".mp4");
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopRecord() {
        if (mediaRecorder == null)
            return;
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
        if (camera != null) {
            camera.lock();
            try {
                camera.reconnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void startAudioRecord() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioSamplingRate(8000);
        mediaRecorder.setOutputFile(PathUtil.AUDIO_PATH + "/" + DateFormatUtil.getCurrentTime() + ".aac");
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopAudioRecord() {
        if (mediaRecorder == null)
            return;
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
    }
}
