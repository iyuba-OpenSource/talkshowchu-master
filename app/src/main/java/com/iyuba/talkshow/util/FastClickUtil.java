package com.iyuba.talkshow.util;

/**
 * user:dfht
 * <p>
 * date:2020/5/19
 * 快速点击判断
 */
public class FastClickUtil {

    private static long oldTime = 0;

    //快速点击判断
    public static boolean fastClick(){
        long curTime = System.currentTimeMillis();

        if ((curTime>oldTime)&&(curTime-oldTime<800)){
            return true;
        }

        oldTime = curTime;
        return false;
    }
}
