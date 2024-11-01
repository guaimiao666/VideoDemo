package com.example.videodemo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.example.videodemo.utils.PathUtil;
import com.example.videodemo.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private final String[] permissions = {
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };
    private Button jumpVideoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPermissions();
        PathUtil.createFileDir();
        initView();
        initData();
        initAction();
    }

    void initView() {
        jumpVideoBtn = findViewById(R.id.main_activity_jump_video_view_button);
    }

    void initData() {

    }

    void initAction() {
        jumpVideoBtn.setOnClickListener(this);
    }

    private void checkAndRequestPermissions() {
        List<String> notPermissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    notPermissions.add(permission);
                }
            }
            if (!notPermissions.isEmpty()) {
                ActivityCompat.requestPermissions(this, notPermissions.toArray(new String[notPermissions.size()]), 100);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            PathUtil.createFileDir();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == jumpVideoBtn.getId()) {
            Intent intent = new Intent(this, VideoPreviewActivity.class);
            startActivity(intent);
        }
    }
}