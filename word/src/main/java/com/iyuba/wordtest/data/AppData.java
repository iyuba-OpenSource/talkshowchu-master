package com.iyuba.wordtest.data;

import android.content.Context;

/**
 * @desction:
 * @date: 2023/2/16 14:41
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class AppData {

    //logo链接
    public static final String APP_PIC = "http://static3.iyuba.cn/android/images/Primary%20school%20English/Primary%20school%20English_new.png";

    public static final int APP_SHARE_HIDE = 0;

    private static final String PACKAGE_JUNIOR = "com.iyuba.talkshow.junior";
    private static final String PACKAGE_JUNIOR_ENGLISH = "com.iyuba.talkshow.juniorenglish";
    private static final String PACKAGE_PRIMARY_ENGLISH = "com.iyuba.primaryenglish";
    private static final String PACKAGE_PRIMARY_PRO = "com.iyuba.primarypro";

    //获取微信的appid
    public static String getWeChatId(Context context){
        String packageName = context.getPackageName();
        if (packageName.equals(PACKAGE_JUNIOR)){
            return "wx6f3650b6c6690eaa";
        }else if (packageName.equals(PACKAGE_JUNIOR_ENGLISH)){
            return "wxa512f1de837454c6";
        }else if (packageName.equals(PACKAGE_PRIMARY_ENGLISH)){
            return "wxf4aa17b7cc6e2ce5";
        }else if (packageName.equals(PACKAGE_PRIMARY_PRO)){
            return "wxa512f1de837454c6";
        }else {
            return "";
        }
    }

    //获取小程序的原始id
    public static String getWeChatName(Context context){
        String packageName = context.getPackageName();
        if (packageName.equals(PACKAGE_JUNIOR)){
            return "gh_f6acd8765d6b";
        }else if (packageName.equals(PACKAGE_JUNIOR_ENGLISH)){
            return "gh_f6acd8765d6b";
        }else if (packageName.equals(PACKAGE_PRIMARY_ENGLISH)){
            return "gh_f6acd8765d6b";
        }else if (packageName.equals(PACKAGE_PRIMARY_PRO)){
            return "gh_f6acd8765d6b";
        }else {
            return "";
        }
    }

    //获取小程序的id
    public static String getSmallId(Context context){
        String packageName = context.getPackageName();
        if (packageName.equals(PACKAGE_JUNIOR)){
            return "wx775f21ca9f27e238";
        }else if (packageName.equals(PACKAGE_JUNIOR_ENGLISH)){
            return "";
        }else if (packageName.equals(PACKAGE_PRIMARY_ENGLISH)){
            return "wxba18920221cc001d";
        }else if (packageName.equals(PACKAGE_PRIMARY_PRO)){
            return "wxba18920221cc001d";
        }else {
            return "";
        }
    }

    //配置微信小程序的分享
    public static final boolean openWxSmallShare = false;
}
