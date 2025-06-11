package com.iyuba.talkshow.util;

import android.os.Build;

import java.util.Date;

/**
 * Created by iyuba on 2018/1/8.
 */

public class TemporaryUserUtil {



    public static String PhoneDiviceId(){
        return Build.SERIAL;
    }

    public static long getTimeDate(){
        Date date = new Date();
        long unixTimestamp = date.getTime()/1000+3600*8; //东八区;
        long days=unixTimestamp/86400;
        return days;
    }
    public static String getSignString(){
        return MD5.getMD5ofStr( PhoneDiviceId()+ "249"+"Android"+
                getTimeDate()+"iyubaV2");
    }
}
