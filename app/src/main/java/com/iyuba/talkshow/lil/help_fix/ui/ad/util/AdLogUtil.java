package com.iyuba.talkshow.lil.help_fix.ui.ad.util;

import android.util.Log;

import com.iyuba.talkshow.lil.help_mvp.util.DateUtil;

public class AdLogUtil {
    private static final String TAG = "AdLogUtil";

    //当前时间
    private static String showCurrentTime(){
        return DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMDHMSS);
    }

    //当前线程
    private static String showCurrentThread(){
        return Thread.currentThread().getName();
    }

    public static void showDebug(String tag,String showMsg){
        String timeMsg = "当前时间："+showCurrentTime();
        String threadMsg = "当前线程："+showCurrentThread();
        Log.d(tag, timeMsg+"----"+threadMsg);
        Log.d(tag, showMsg);
    }
}
