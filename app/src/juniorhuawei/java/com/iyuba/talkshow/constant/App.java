package com.iyuba.talkshow.constant;

import android.text.TextUtils;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.ui.courses.coursechoose.TypeHelper;

/**
 * App
 */
public interface App {
    int APP_ID = 259;
    String SHARE_NAME_EN = "juniorEnglish";
    String APP_NAME_EN = "juniorEnglish";
    String APP_NAME_CH = "初中英语背单词";
    String APP_NAME_PRIVACY = "隐私政策";
    String PLATFORM = "android";
    // TODO: 2022/6/16 这里默认小版本不自动更新 
    boolean APP_CHECK_UPGRADE = false;
    boolean APP_TENCENT_PRIVACY = true;
    boolean APP_HUAWEI_PRIVACY = true;
    boolean APP_HUAWEI_COMPLAIN = true;
    boolean APP_MINI_PRIVACY = false;
    boolean APP_TENCENT_MOOC = true;
    boolean APP_MOC_BOTTOM = false;
    boolean APP_WORD_BOTTOM = true;
    boolean APP_CHECK_PERMISSION = false;
    boolean APP_CHECK_AGREE = false;
    int APP_SHOW_SUBPAGE = 1;
    int APP_SHARE_WXMINIPROGRAM = 0;
    int APP_SHARE_PART = 0;
    int APP_SHARE_HIDE = 1;
    int APP_SHARE_JUNIOR = 4;
    int APP_SHARE_JUNIOR_ENGLISH = 1;
    int APP_SHARE_JUNIOR_TALKSHOW = 2;
    int APP_SHARE_JUNIOR_PRO = 3;
    int APP_SHARE_PRIMARY_ENGLISH = 4;
    public static String getAppKey(int type) {
        switch (type) {
            case APP_SHARE_JUNIOR_ENGLISH:
                return "2f6dcf8b701cc";
            case APP_SHARE_PRIMARY_ENGLISH:
                return "3093beb57f7bc";
            case APP_SHARE_JUNIOR_TALKSHOW:
                return "da2d6fdc5cb2";
            case APP_SHARE_JUNIOR_PRO:
            default:
                return "16788bea16a01";
        }
    }
    public static String getAppSecret(int type) {
        switch (type) {
            case APP_SHARE_JUNIOR_ENGLISH:
                return "0c2dd1bfba3c682712b7c7982985edc2";
            case APP_SHARE_PRIMARY_ENGLISH:
                return "36dc224a32d645da2c0722ddd28766b0";
            case APP_SHARE_JUNIOR_TALKSHOW:
                return "6bd3183af2d993d296d23a28ef7aeb13";
            case APP_SHARE_JUNIOR_PRO:
            default:
                return "5809c25db8e9137004f0b4d244e256c7";
        }
    }
    String APP_SAVE_DIR = "appUpdate";
    String UNDERLINE = "_";
    String APK_SUFFIX = ".apk";
    String AD_FOLDER = "ad";

    String ALIPAY_APP_ID = "2016050601368497";
    String RSA_PRIVATE = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCt+H1XQ65goakPmPhlh+tWbkAWMJUkI0i2Q3ksY2jZA8MEdjqzynJulYLsKhOIafWQIN+FsLeXXOinq/DQmMPuu+hqJwVXohimcMTLYY1WCSj1fqEdXOCuNzShQJV4KXVp6r/BZ46UVzoNnZFQtCVAMRxdzLUQEM+zGUV/CSd3yJH4xUR45lDzUi7mYMR66bkjTXvtrYJRILDYlqpaLlnR91/rmkOz5wHXaCW5qW+haJ6UftwS+EiBKYRmGiu0m6OQtFk1OI2elTyIm1M5Cnv748o0n90uQFEZu8hiAJDXi+eh/WiqZkPfWU1A/Hm1SXzceYPR5MHY8Mo6a279fOINAgMBAAECggEBAJbcN9z3fPyg01kKPsTUs7hUjNBxOrOGwWQEaMycO+yMfY2d9NO6B7drgYfICP4vXFmqmAp3rwzb7YiQ6pgJEUcxvZ5nzhMrJpMWkSEIrCZidRlFwPMUemW4y7PVrIfx87Zwce6GHbb3WQk7jSrdvLvImp+gh5ax7VqS3JgH2Sjd3+LGBIlkhZW9Ew5pcUoV5ol1Hss3cxfqlRV9uT3UyAR+tZxa8rwreoJTjl9A7ni8ZhXtwSA7AecIAUtSLX7FJfeLt7g4Juo84ovuDNYZM73ZDJzloJLjrP/roMSdbjtL1SdxzHEwVFlwpQ8bevY4XB2y4Dwtgx67eTMKUAHKO+UCgYEA1h8V6ec66ryfZi8AgklZVuXTUCeBJrvfcqZHDbvNZQRtfbc52BTirtpcRaVPHwEAxeYvjRpitDiIDcSeTrcRjZEIgvyzLCYfLIv8J42wS7aK5Zin5gDxIvpiAO8HrsuCoXxP1a+pyjhD3cV9vVPHfxD8hFv0KJWgFOQK8wa8j/MCgYEAz/8VHGY/5JojewvMdcOjRe6FvfykQBcD7lo6GoYXBMnVIv+MgsdYcJECFpTRUXq9KNanUHUmIXdZIb/cUhr0uDlCXqo3oGOJzXEBBlNfg4Gg5rVoz+9rcUK2nraLaKZTslwLR2ySlawWnjEJT2A1gazhMZLutuSbtJnRNOvvRf8CgYEAyrtmATAI7aYn+hT4k2MlboxuFg9BTk5Fk4Tx84PkRgf6LzSjVP75XfqrsNmC32UQuU9nqF7aI67+yqJmDTtyKCqw92yRrHRvwbrMxRp3WEh+nEJ8fd5YcfjFgALRsGNJzOIpqLYIucmqIDlUA0Vmtt17aUqzExYQGpeL8mxnbpcCgYBlTihKHMMh5LFDTQvYj+EGPpaFYnfdf1g6z1ddc9HiUyusUCtvxwgcS4Ro2zLYLJ/VNDdpyKU5x5dyCLCWjOqEj97znJRbWh/UICYPnqv2sTxdIh5aqJH8KDIqO17LKbe6N3qG3yrGG3sosVmHf6SP9FP6gUYjblUoMYLj88YmoQKBgH3SHXePRIcSBYKCzm5+iX8hgEf1Zvw7JR1w+R3oJMjzbGNnls7yOy2mMFl74IP3e7wPs+xwbyb4OB/8SKILueAEQ4ysOO9jH9eNrAbfvvUz1F8DfEGhGYOB0B0UR2405yIkafEcIo8l6i4yoPtBPHR+c09D18I1o3oiblIrXvd+";

    String localVipDesc = "1. 尊贵V标识\n" +
            "2. 同享全站会员去广告、PDF免积分导出、语音调速等特权\n" +
            "3. VIP会员全部文章无限下载\n" +
            "4. 非VIP用户单词闯关体验闯1关, VIP会员无限单词闯关\n" +
            "5. 非VIP用户每篇文章句子评测仅限3句, VIP会员无限制评测\n" +
            "6. 非VIP用户口语秀配音仅限前3篇，VIP会员无限配音\n" +
            "7. 本应用VIP仅限Android平台本应用使用(不含微课)\n" +
            "8. 更多VIP功能，敬请期待。";

    int DEFAULT_BOOKID = 217;
    int DEFAULT_SERIESID = 316;
    int COURSE_TYPE = TypeHelper.TYPE_JUNIOR;
    class Url {
        public static String APP_ICON_URL = "http://app."+Constant.Web.WEB_SUFFIX.replace("/","")+"/ios/images/junior%20English/junior%20English.png";
        //爱语言
        public static String PROTOCOL_USAGE = Constant.Url.PROTOCOL_URL_USAGE;
        public static String PROTOCOL_URL = Constant.Url.PROTOCOL_URL_PRIVACY;
        public static String SHARE_APP_URL = Constant.Url.APP_SHARE_URL + SHARE_NAME_EN;
    }

    int DEFAULT_QQ_ID = 9972;
    String DEFAULT_QQ_GROUP = "初中英语官方群";
    String DEFAULT_WORDS = "middle";
    String DEFAULT_SERIES = "313,314,315,316";
    String DEFAULT_TITLE = "7年级上";
    public static String getPrimaryTitle(String courseTitle) {
        if (TextUtils.isEmpty(courseTitle)) {
            return courseTitle;
        }
        String curTitle = courseTitle.replace("(人教版)", "");
        return curTitle;
    }

    /****微信小程序****/
    //微信的id
    String WX_KEY = "wxf4aa17b7cc6e2ce5";
    //小程序的原始id
    String WX_NAME = "gh_f6acd8765d6b";
    //小程序的appid
    String WX_SMALL_APPID = "wxba18920221cc001d";
    //是否开启小程序登陆+账号登陆（true为小程序登陆，false为账号登陆）
    boolean APP_SMALL_LOGIN = false;

    /****马甲包****/
    //马甲包标识（用于审核接口的appid）
    int VestBagTag = 2591;
    //是否需要处理马甲包
    boolean APP_SAME_CHECK = false;
    //需要处理马甲包的渠道
    String VestBag_Channel = "";

    /****oaid升级****/
    //oaid的证书名称
    String oaid_pem = "";
    //是否需要oaid重新赋值
    boolean APP_OAID_UPDATE = false;
}
