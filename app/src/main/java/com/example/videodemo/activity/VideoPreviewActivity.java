package com.example.videodemo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.videodemo.manager.CameraManager;
import com.example.videodemo.utils.MediaRecorderUtil;
import com.example.videodemo.MyApplication;
import com.example.videodemo.R;
import com.example.videodemo.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class VideoPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private CameraManager cameraManager;
    private Button videoBtn, audioBtn, takePictureBtn, switchCameraBtn, switchFlashlight;
    private MyApplication myApp;
    private TextView recordTimeText;
    private LinearLayout recordTimeLayout;

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
        setNewSurfaceUI();
    }

    void initView() {
        surfaceView = findViewById(R.id.video_preview_activity_surface);
        surfaceHolder = surfaceView.getHolder();
        videoBtn = findViewById(R.id.video_preview_activity_video_button);
        audioBtn = findViewById(R.id.video_preview_activity_audio_button);
        takePictureBtn = findViewById(R.id.video_preview_activity_photo_button);
        switchCameraBtn = findViewById(R.id.video_preview_activity_switch_camera_button);
        switchFlashlight = findViewById(R.id.video_preview_activity_switch_flash_light);
        recordTimeText = findViewById(R.id.video_preview_activity_record_time_text);
        recordTimeLayout = findViewById(R.id.video_preview_activity_record_time_layout);
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
        switchCameraBtn.setOnClickListener(this);
        switchFlashlight.setOnClickListener(this);
    }

    private void setNewSurfaceUI() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
        int screenWidth = Utils.getScreenWidth(this);
        int screenHeight = Utils.getScreenHeight(this);
        if (screenWidth < screenHeight) {
            int height = screenWidth / 4 * 3;
            layoutParams.height = height;
        }
        surfaceView.setLayoutParams(layoutParams);
    }

    private boolean isVideoRecord = false;
    private boolean isAudioRecord = false;
    private boolean isFlashlightOpen = false;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == videoBtn.getId()) {
            if (isAudioRecord) {
                MediaRecorderUtil.getInstance(this).stopAudioRecord();
            }
            isVideoRecord = !isVideoRecord;
            recordView(isVideoRecord);
            if (isVideoRecord) {
                MediaRecorderUtil.getInstance(this).startRecord();
            } else {
                MediaRecorderUtil.getInstance(this).stopRecord();
            }
            videoBtn.setText(isVideoRecord ? "停止录像" : "录像");
        } else if (id == audioBtn.getId()) {
            if (isVideoRecord) {
                MediaRecorderUtil.getInstance(this).stopRecord();
            }
            isAudioRecord = !isAudioRecord;
            recordView(isAudioRecord);
            if (isAudioRecord) {
                MediaRecorderUtil.getInstance(this).startAudioRecord();
            } else {
                MediaRecorderUtil.getInstance(this).stopAudioRecord();
            }
            audioBtn.setText(isAudioRecord ? "停止录音" : "录音");
        } else if (id == takePictureBtn.getId()) {
            cameraManager.takePicture();
        } else if (id == switchCameraBtn.getId()) {
            if (isAudioRecord) {
                MediaRecorderUtil.getInstance(this).stopAudioRecord();
            }
            if (isVideoRecord) {
                MediaRecorderUtil.getInstance(this).stopRecord();
            }
            CameraManager.getInstance(this).switchCamera();
            CameraManager.getInstance(this).setCameraDisplayOrientation(this);
        } else if (id == switchFlashlight.getId()) {
            isFlashlightOpen = !isFlashlightOpen;
            CameraManager.getInstance(this).switchFlash(isFlashlightOpen);
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


    Timer timer = null;
    private int secondLow = 0;
    private int secondHigh = 0;
    private int minuteLow = 0;
    private int minuteHigh = 0;
    private int hourLow = 0;
    private int hourHigh = 0;
    private void recordView(boolean flag) {
        if (flag) {
            recordTimeLayout.setVisibility(View.VISIBLE);
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    augmentTime();
                    handler.sendEmptyMessage(UPDATE_RECORD_TIME_TEXT);
                }
            }, 0, 1000);
        } else {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            resetTime();
            recordTimeLayout.setVisibility(View.GONE);
        }
    }

    private String getTimeInfo() {
        return "" + hourHigh + hourLow + ":" + minuteHigh + minuteLow + ":" + secondHigh + secondLow;
    }

    private void augmentTime() {
        secondLow++;
        if (secondLow == 10) {
            secondLow -= 10;
            secondHigh++;
            if (secondHigh == 6) {
                secondHigh -= 6;
                minuteLow++;
                if (minuteLow == 10) {
                    minuteLow -= 10;
                    minuteHigh++;
                    if (minuteHigh == 6) {
                        minuteHigh -= 6;
                        hourLow++;
                        if (hourLow == 10) {
                            hourLow -= 10;
                            hourHigh++;
                        }
                    }
                }
            }
        }
    }

    private void resetTime() {
        secondLow = 0;
        secondHigh = 0;
        minuteLow = 0;
        minuteHigh = 0;
        hourLow = 0;
        hourHigh = 0;
    }

    public static final int UPDATE_RECORD_TIME_TEXT = 1024;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_RECORD_TIME_TEXT:
                    recordTimeText.setText(getTimeInfo());
                    break;
                default:
                    break;
            }
        }
    };
}