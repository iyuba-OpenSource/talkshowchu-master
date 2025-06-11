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
    public static boolean renVerifyCheck = true;
    //人教版审核的渠道id
    public static int getRenLimitChannelId(String channel){
        switch (channel){
            case "huawei":
                return 5011;
            case "xiaomi":
                return 5012;
            case "oppo":
                return 5013;
            case "vivo":
                return 5014;
            case "honor":
                return 5015;
        }
        return 5010;
    }

    /************微课审核**********/
    //是否进行微课控制
    public static boolean mocVerifyCheck = true;
    //微课审核的渠道id
    public static int getMocLimitChannelId(String channel){
        switch (channel){
            case "huawei":
                return 5001;
            case "xiaomi":
                return 5002;
            case "oppo":
                return 5003;
            case "vivo":
                return 5004;
            case "honor":
                return 5005;
        }
        return 5000;
    }

    /************视频审核***********/
    //是否进行视频控制
    public static boolean videoVerifyCheck = true;
    //视频审核的渠道id
    public static int getVideoLimitChannelId(String channel){
        switch (channel){
            case "huawei":
                return 5021;
            case "xiaomi":
                return 5022;
            case "oppo":
                return 5023;
            case "vivo":
                return 5024;
            case "honor":
                return 5025;
        }
        return 5020;
    }

    /***************小说审核***********/
    //是否进行小说控制
    public static boolean novelVerifyCheck = true;
    //小说审核的渠道id
    public static int getNovelLimitChannelId(String channel){
        switch (channel){
            case "huawei":
                return 5031;
            case "xiaomi":
                return 5032;
            case "oppo":
                return 5033;
            case "vivo":
                return 5034;
            case "honor":
                return 5035;
        }
        return 5030;
    }

    /*****************************广告配置**************************/
    //有道广告
    public static final String YOUDAO_AD_SPLASH_CODE = "a710131df1638d888ff85698f0203b46";//开屏
    public static final String YOUDAO_AD_STEAM_CODE = "3438bae206978fec8995b280c49dae1e";//信息流
    public static final String YOUDAO_AD_BANNER_CODE = "230d59b7c0a808d01b7041c2d127da95";//banner

    //爱语吧广告
    //com.iyuba.talkshow.junior 开屏 0042
    //com.iyuba.talkshow.junior banner 0043
    public static final String IYUBA_AD_SPLASH_CODE = "0042";
    public static final String IYUBA_AD_BANNER_CODE = "0043";

    /*****************************登录配置**************************/
    //登录类型
    public static final String loginType = LoginType.loginByVerify;

    /*****************************mob配置**************************/
    //mob的key
//    public static final String mob_key = "328d6f4bfbc8c";
//    //mob的secret
//    public static final String mob_secret = "20c99d72a7a53ade3558792cce26680d";

    //mob的key
    public static final String mob_key = "38865643d556b";
    //mob的secret
    public static final String mob_secret = "6b373d5b5d2c6d941f83b5b28ec98421";

    /******************************友盟配置*************************/
    public static final String umeng_key = "5ec23cee167edd25a90001a3";

    /******************************微信配置*************************/
    //微信appid--用于微信分享、微信支付、小程序登录等
    public static final String wx_key = "wxb104da7986bda128";
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
