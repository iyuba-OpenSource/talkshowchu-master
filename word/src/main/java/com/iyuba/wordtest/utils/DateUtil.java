package com.iyuba.wordtest.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @desction:
 * @date: 2023/2/8 11:00
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class DateUtil {

    public static String formatTime(long time,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

    //转换时间
    public static String transTime(long time){
        int minTime = (int) (time/60);
        int lastMinTime = (int) (time%60);

        int hourTime = minTime/60;
        int lastHourTime = minTime%60;

        StringBuilder builder = new StringBuilder();
        if (hourTime>0){
            builder.append(hourTime+"小时");
        }

        if (lastHourTime>0){
            builder.append(lastHourTime+"分钟");
        }

        if (lastMinTime>0){
            builder.append(lastMinTime+"秒");
        }

        return builder.toString();
    }
}
