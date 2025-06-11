package com.iyuba.talkshow.lil.help_mvp.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期处理工具
 */
public class DateUtil {

    //日期格式类型(这里只提供一种)
    public static final String YMDHMSS = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String FORMAT_YMDHMSS = "^[1-9]\\d{3}\\-(0[1-9]|1[0-2])\\-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d{1})\\:[0-5]\\d{1}\\:[0-5]\\d{1}\\.\\d{3}$";
    public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
    private static final String FORMAT_YMDHMS = "^[1-9]\\d{3}\\-(0[1-9]|1[0-2])\\-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d{1})\\:[0-5]\\d{1}\\:[0-5]\\d{1}$";
    public static final String YMD = "yyyy-MM-dd";
    private static final String FORMAT_YMD = "^[1-9]\\d{3}\\-(0[1-9]|1[0-2])\\-(0[1-9]|[1-2][0-9]|3[0-1])$";
    public static final String HMS = "HH:mm:ss";
    public static final String FORMAT_HMS = "(20|21|22|23|[0-1]\\d{1})\\:[0-5]\\d{1}\\:[0-5]\\d{1}$";
    public static final String HM = "HH:mm";

    //日期格式类型(第二种)
    public static final String YMDHMSS2 = "yyyy/MM/dd HH:mm:ss.SSS";
    private static final String FORMAT_YMDHMSS2 = "^[1-9]\\d{3}\\\\(0[1-9]|1[0-2])\\\\(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d{1})\\:[0-5]\\d{1}\\:[0-5]\\d{1}\\.\\d{3}$";
    public static final String YMDHMS2 = "yyyy/MM/dd HH:mm:ss";
    private static final String FORMAT_YMDHMS2 = "^[1-9]\\d{3}\\\\(0[1-9]|1[0-2])\\\\(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d{1})\\:[0-5]\\d{1}\\:[0-5]\\d{1}$";
    public static final String YMD2 = "yyyy/MM/dd";
    private static final String FORMAT_YMD2 = "^[1-9]\\d{3}\\\\(0[1-9]|1[0-2])\\\\(0[1-9]|[1-2][0-9]|3[0-1])$";

    public static final String HOUR = "hour";//小时
    public static final String MINUTE = "minute";//分钟
    public static final String SECOND = "second";//秒

    public static final String YMD3 = "yyyyMMdd";

    //将long时间转换成string类型
    public static String toDateStr(long time,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        return sdf.format(new Date(time));
    }

    //将string类型转换成long时间
    public static long toDateLong(String time,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.CHINA);
            return sdf.parse(time).getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    //判断格式是否匹配
    public static boolean isDateMatch(String time,String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(time);
        return matcher.matches();
    }

    //根据当前日期字符串获取对应的格式
    public static String matchDateFormat(String time){
        if (isDateMatch(time,FORMAT_YMDHMSS)){
            return YMDHMSS;
        }else if (isDateMatch(time,FORMAT_YMDHMS)){
            return YMDHMS;
        }else if (isDateMatch(time,FORMAT_YMD)){
            return YMD;
        }else if (isDateMatch(time,FORMAT_HMS)){
            return HMS;
        }else if (isDateMatch(time,FORMAT_YMDHMSS2)){
            return YMDHMSS2;
        }else if (isDateMatch(time,FORMAT_YMDHMS2)){
            return YMDHMS2;
        }else if (isDateMatch(time,FORMAT_YMD2)){
            return YMD2;
        }
        return null;
    }

    //获取日期字符串格式并且转换成long
    public static long matchDateToLong(String time){
        String format = matchDateFormat(time);
        if (TextUtils.isEmpty(format)){
            return 0;
        }

        return toDateLong(time,format);
    }

    //获取时间段之内的天数
    public static long computeDay(long time){
        long dayScale = 24*60*60*1000;
        return time/dayScale;
    }

    //将时间转换成播放格式
    public static String transPlayFormat(String type,long millisTime){
        long sec = millisTime/1000;

        //小时
        long hour = sec/(60*60);
        long minute = sec/60;

        String hourStr = null;
        if (hour>0){
            if (hour>=10){
                hourStr = String.valueOf(hour);
            }else {
                hourStr = "0"+hour;
            }

            //这里处理一下分钟
            minute = minute%60;
        }

        String minuteStr = null;
        if (minute>0){
            if (minute>=10){
                minuteStr = String.valueOf(minute);
            }else {
                minuteStr = "0"+minute;
            }

            sec = sec%60;
        }

        String secStr = null;
        if (sec>0){
            if (sec>=10){
                secStr = String.valueOf(sec);
            }else {
                secStr = "0"+sec;
            }
        }


        StringBuilder builder = new StringBuilder();
        if (type.equals(HOUR)){
            builder.append(hourStr==null?"00":hourStr);
            builder.append(":");
            builder.append(minuteStr==null?"00":minuteStr);
            builder.append(":");
            builder.append(secStr==null?"00":secStr);
        }

        if (type.equals(MINUTE)){
            builder.append(minuteStr==null?"00":minuteStr);
            builder.append(":");
            builder.append(secStr==null?"00":secStr);
        }

        if (type.equals(SECOND)){
            builder.append(secStr==null?"00":secStr);
        }

        return builder.toString();
    }
}
