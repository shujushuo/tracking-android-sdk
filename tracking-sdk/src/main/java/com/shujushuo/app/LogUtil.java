package com.shujushuo.app;

import android.util.Log;

public class LogUtil {


    public static void debug(String tag, String info) {
        int strLength = info.length();
        int start = 0;
        int LOG_MAXLENGTH = 2000;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.d(tag + i, info.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                Log.d(tag, info.substring(start, strLength));
                break;
            }
        }
    }

    public static void error(String tag, String info) {
        int strLength = info.length();
        int start = 0;
        int LOG_MAXLENGTH = 2000;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.e(tag + i, info.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                Log.e(tag, info.substring(start, strLength));
                break;
            }
        }
    }
}
