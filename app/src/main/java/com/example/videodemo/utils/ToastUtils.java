package com.example.videodemo.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    public static void showTextShort(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    public static void showTextLong(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }
}
