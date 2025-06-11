package com.iyuba.talkshow.constant;

import com.iyuba.wordtest.data.LoginType;

/**
 * @title: 新的配置文件
 * @date: 2023/8/29 16:16
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ConfigData {

    /****************************审核配置***************************/
    /***********人教版审核*********/
    //是否进行人教版审核
    public static boolean renVerifyCheck = false;
    //人教版审核的渠道id
    public static int getRenLimitChannelId(String channel){
        switch (channel){
            case "huawei":
                return 5111;
            case "xiaomi":
                return 5112;
            case "oppo":
                return 5113;
            case "vivo":
                return 5114;
            case "honor":
                return 5115;
        }
        return 5110;
    }

    /************微课审核**********/
    //是否进行微课控制
    public static boolean mocVerifyCheck = true;
    //微课审核的渠道id
    public static int getMocLimitChannelId(String channel){
        switch (channel){
            case "huawei":
                return 5101;
            case "xiaomi":
                return 5102;
            case "oppo"://多个应用市场使用这个，使用时判断是否为oppo的手机，然后判断
                return 5103;
            case "vivo":
                return 5104;
            case "honor":
                return 5105;
        }
        return 5100;
    }

    /************视频审核***********/
    //是否进行视频控制
    public static boolean videoVerifyCheck = false;
    //视频审核的渠道id
    public static int getVideoLimitChannelId(String channel){
        switch (channel){
            case "huawei":
                return 5121;
            case "xiaomi":
                return 5122;
            case "oppo"://多个应用市场使用这个，使用时判断是否为oppo的手机，然后判断
                return 5123;
            case "vivo":
                return 5124;
            case "honor":
                return 5125;
        }
        return 5120;
    }

    /***************小说审核***********/
    //是否进行小说控制
    public static boolean novelVerifyCheck = false;
    //小说审核的渠道id
    public static int getNovelLimitChannelId(String channel){
        switch (channel){
            case "huawei":
                return 5131;
            case "xiaomi":
                return 5132;
            case "oppo"://多个应用市场使用这个，使用时判断是否为oppo的手机，然后判断
                return 5133;
            case "vivo":
                return 5134;
            case "honor":
                return 5135;
        }
        return 5130;
    }

    /*****************************广告配置**************************/
    //有道广告
    public static final String YOUDAO_AD_SPLASH_CODE = "a710131df1638d888ff85698f0203b46";//开屏
    public static final String YOUDAO_AD_STEAM_CODE = "3438bae206978fec8995b280c49dae1e";//信息流
    public static final String YOUDAO_AD_BANNER_CODE = "230d59b7c0a808d01b7041c2d127da95";//banner

    //爱语吧广告
    //com.iyuba.talkshow.juniorenglish 开屏 0044
    //com.iyuba.talkshow.juniorenglish banner 0045
    public static final String IYUBA_AD_SPLASH_CODE = "0044";
    public static final String IYUBA_AD_BANNER_CODE = "0045";

    /*****************************登录配置**************************/
    //登录类型
    public static final String loginType = LoginType.loginByVerify;

    /*****************************mob配置**************************/
    //mob的key
//    public static final String mob_key = "2f6dcf8b701cc";
    public static final String mob_key = "38863fc450eba";
    //mob的secret
//    public static final String mob_secret = "0c2dd1bfba3c682712b7c7982985edc2";
    public static final String mob_secret = "63d28dd701041c033a6b4c135ab5fbd6";

    /******************************友盟配置*************************/
    public static final String umeng_key = "5ec23cee167edd25a90001a3";

    /******************************微信配置*************************/
    //微信appid--用于微信分享、微信支付、小程序登录等
    public static final String wx_key = "wxa512f1de837454c6";
    //小程序原始id
    public static final String wx_small_name = "gh_f6acd8765d6b";

    /******************************分享功能配置***********************/
    //是否开启分享功能
    public static final boolean openShare = true;
    //是否开启qq分享
    public static final boolean openQQShare = true;
    //是否开启微信分享
    public static final boolean openWeChatShare = true;
    //是否开启微信小程序分享(需要开启微信分享才能使用)
    public static final boolean openWxSmallShare = false;
    //是否开启微博分享
    public static final boolean openWeiBoShare = false;
}