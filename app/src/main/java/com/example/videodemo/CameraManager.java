package com.example.videodemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.core.app.ActivityCompat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraManager {

    private final String TAG = getClass().getSimpleName();
    private static CameraManager cameraManager = null;
    private Context mContext;
    private MyApplication myApp;
    private Camera mCamera;
    private int cameraId;
    public CameraManager(Context context) {
        this.mContext = context;
        this.myApp = (MyApplication) context.getApplicationContext();
    }

    public static CameraManager getInstance(Context context) {
        if (cameraManager == null) {
            cameraManager = new CameraManager(context);
        }
        return cameraManager;
    }

    public void openCamera() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "没有相机权限");
            return;
        }
        if (existCamera(Camera.CameraInfo.CAMERA_FACING_BACK)) {
            this.mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            this.cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }

    public void startPreview(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null || mCamera == null)
            return;
        Camera.Parameters parameters = mCamera.getParameters();
        //设置颜色格式
        parameters.setPreviewFormat(ImageFormat.NV21);
        //设置分辨率
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        boolean hasPreviewSize = false;
        if (!supportedPreviewSizes.isEmpty()) {
            for (Camera.Size previewSize : supportedPreviewSizes) {
                if (previewSize.width == 1920 && previewSize.height == 1080) {
                    hasPreviewSize = true;
                }
            }
            if (hasPreviewSize) {
                parameters.setPreviewSize(1920, 1080);
            } else {
                parameters.setPreviewSize(640, 480);
            }
        }
        //设置对焦模式
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        if (!supportedPreviewSizes.isEmpty()) {
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
        }
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mCamera.startPreview();
    }

    public boolean existCamera(int facing) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == facing) {
                return true;
            }
        }
        return false;
    }

    public void setCameraDisplayOrientation(Activity activity) {
        try {
            if (mCamera == null) {
                return;
            }
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(cameraId, info);
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }
            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;   // compensate the mirror
            } else {
                // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }

            mCamera.setDisplayOrientation(result);
        } catch (Exception e) {
            Log.w(TAG, "setCameraDisplayOrientation catch = " + e.toString());
        }
    }

    public void releaseCamera() {
        if (mCamera == null)
            return;
        mCamera.release();
        mCamera = null;
    }

    public void takePicture() {
        if (mCamera == null)
            return;
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        if (!supportedPictureSizes.isEmpty()) {
            parameters.setPictureSize(supportedPictureSizes.get(0).width, supportedPictureSizes.get(0).height);
        }
        parameters.set("jpeg-quality", 100);
        mCamera.setParameters(parameters);
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(
                            PathUtil.PHOTO_PATH + "/" + DateFormatUtil.getCurrentTime() + ".jpg");
                    fos.write(data);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                mCamera.stopPreview();
                startPreview(myApp.getVideoPreviewActivity().getSurfaceHolder());
            }
        });
    }
}
