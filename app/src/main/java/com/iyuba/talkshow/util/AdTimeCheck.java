package com.iyuba.talkshow.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by carl shen on 2021/1/6
 * New Primary English, new study experience.
 */
public class AdTimeCheck {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static final String showadtime = "2019-01-28 18:00:00";

    public static boolean setAd() {
        long time = System.currentTimeMillis();
        Date date = null;
        try {
            date = sdf.parse(showadtime);
            Log.e("Tag--ad", "AdTimeCheck " + date.getTime());
            return time > date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
    }
}