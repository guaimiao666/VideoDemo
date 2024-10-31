package com.example.videodemo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {

    public static String getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(date);
    }
}
