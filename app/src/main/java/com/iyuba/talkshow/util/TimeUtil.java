package com.iyuba.talkshow.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    public static String dateTransform(String date){
        return date;
    }

    private static final long TRANS_SEC_MIL = 1000;
    private static final String MILLS_TO_SEC = "000";
    public static SimpleDateFormat GLOBAL_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public static int secToMilliSec(float seconds) {
        return (int) (TRANS_SEC_MIL * seconds);
    }

    public static float milliSecToSec(int milliSec) {
        return ((float) milliSec) / TRANS_SEC_MIL;
    }

    public static long getTimeStamp() {
        return System.currentTimeMillis();
    }

    public static String tansDateFrom1970(String mills) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(Long.parseLong(millsToSec(mills)));
    }
    public static String tansDateFrom1970(long mills) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(mills);
    }

    private static String millsToSec(String mills) {
        return mills + MILLS_TO_SEC;
    }

    public static String getCurDate() {
        long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        return format.format(date);
    }
    public static String getFormatDate(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
    }
    public static boolean checkAfterCur(int year, int mouth, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,mouth,day);
        Date tmpDate = calendar.getTime();
        Date curDate = new Date();
        Log.e("bijiao",tmpDate.getTime()+"choose--"+curDate.getTime());
        return (tmpDate.getTime()- curDate.getTime())>0l ;

    }
}
