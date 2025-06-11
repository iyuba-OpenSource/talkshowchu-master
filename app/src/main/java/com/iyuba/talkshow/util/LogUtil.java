package com.iyuba.talkshow.util;

import android.util.Log;

import com.iyuba.talkshow.BuildConfig;

public class LogUtil {

    public static void d(String tag,String showMsg){
        if (BuildConfig.DEBUG){
            Log.d(tag, showMsg);
        }
    }
}
