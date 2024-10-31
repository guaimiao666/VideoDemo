package com.example.videodemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class VideoPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private CameraManager cameraManager;
    private Button videoBtn, audioBtn, takePictureBtn;
    private MyApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        myApp = (MyApplication) getApplicationContext();
        myApp.setVideoPreviewActivity(this);
        cameraManager = CameraManager.getInstance(VideoPreviewActivity.this);
        initView();
        initData();
        initAction();
    }

    void initView() {
        surfaceView = findViewById(R.id.video_preview_activity_surface);
        surfaceHolder = surfaceView.getHolder();
        videoBtn = findViewById(R.id.video_preview_activity_video_button);
        audioBtn = findViewById(R.id.video_preview_activity_audio_button);
        takePictureBtn = findViewById(R.id.video_preview_activity_photo_button);
    }

     void initData() {

     }

     void initAction() {
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated");
                cameraManager.openCamera();
                cameraManager.startPreview(holder);
                cameraManager.setCameraDisplayOrientation(VideoPreviewActivity.this);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, "surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                Log.d(TAG, "surfaceDestroyed");
                cameraManager.releaseCamera();
            }
        });
        videoBtn.setOnClickListener(this);
        audioBtn.setOnClickListener(this);
        takePictureBtn.setOnClickListener(this);
     }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == videoBtn.getId()) {

        } else if (id == audioBtn.getId()) {

        } else if (id == takePictureBtn.getId()) {
            cameraManager.takePicture();
        }
    }

    public SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraManager.releaseCamera();
        myApp.setVideoPreviewActivity(null);
    }
}