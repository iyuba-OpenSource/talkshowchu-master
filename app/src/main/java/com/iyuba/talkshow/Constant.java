package com.iyuba.talkshow;

import android.util.Log;

import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.remote.UserService;
import com.iyuba.talkshow.util.MD5;

import cn.qqtheme.framework.util.LogUtils;

public interface Constant {
    String DOMAIN_STATIC = "iyu_static";
    String HTTP_STATIC = "http://staticvip.";
    String DOMAIN_STATIC2 = "iyu_static2";
    String HTTP_STATIC2 = "http://static2.";
    String DOMAIN_AI = "iyu_ai";
    String HTTP_AI = "http://iuserspeech.";
    String DOMAINS_AI = "iys_ai";
    String HTTPS_AI = "https://iuserspeech.";
    String DOMAIN_USERSPEECH = "iyu_userspeech";
    String HTTP_USERSPEECH = "http://iuserspeech.";
    String DOMAIN_WORD = "iyu_word";
    String HTTP_WORD = "http://word.";
    String DOMAIN_M = "iyu_m";
    String HTTP_M = "http://m.";
    String DOMAIN_STATICVIP = "iyu_staticvip";
    String HTTP_STATICVIP = "http://staticvip.";
    String DOMAIN_APP = "iyu_app";
    String HTTP_APP = "http://app.";
    String DOMAIN_APPS = "iyu_apps";
    String HTTP_APPS = "http://apps.";
    String DOMAIN_VOA = "iyu_voa";
    String HTTP_VOA = "http://voa.";
    String DOMAIN_DEV = "iyu_dev";
    String HTTP_DEV = "http://dev.";
    String DOMAIN_CMS = "iyu_cms";
    String HTTP_CMS = "http://cms.";
    String DOMAIN_API = "iyu_api";
    String HTTP_API = "http://api.";
    String DOMAIN_APIS = "iyu_apis";
    String HTTP_APIS = "http://apis.";
    String DOMAIN_DAXUE = "iyu_daxue";
    String HTTP_DAXUE = "http://daxue.";
    String DOMAIN_VIP = "iyu_vip";
    String HTTP_VIP = "http://vip.";
    String DOMAIN_LONG_API = "iyu_long_api";
    String HTTP_LONG_API = "http://api.";

    String EVAL_TYPE = "juniorenglish";
    String MOOC_TYPE = "class.junior";
    String LESSON_TYPE = "junior";
    int PRODUCT_ID = 24;
    int PRODUCT_WORDS = 25;
    boolean PlayerService = true;
    boolean EvaluateCorrect = true;

    //    public final static String envir = Environment.getExternalStorageDirectory() + "/iyuba/concept2/";//文件夹路径
    String envir = TalkShowApplication.getInstance().getExternalFilesDir(null) + "/iyuba/primaryenglish";//文件夹路径
    String simRecordAddr = envir + "/sound";
    static String getsimRecordAddr() {
        return simRecordAddr;
    }
    int normalColor = 0xff414141;

    static String getSoundWavUrl(String sound, int voaId) {
        String sounds = sound.substring(sound.indexOf('/'), sound.indexOf('/', 1) + 1);
        String audioUrl = Constant.Web.sound_voa + sounds + voaId + "/" + voaId + ".wav";
        return audioUrl;
    }
    static String getSoundMp3Url(String sound, int voaId) {
//        String sounds = sound.substring(sound.indexOf('/'), sound.indexOf('/', 1) + 1);
//        String audioUrl = Constant.Web.sound_voa + sounds + voaId + "/" + voaId + ".mp3";
//        return audioUrl;

        String soundFolder = sound.substring(0,sound.lastIndexOf("/")+1);
        String audioUrl = Web.sound_voa+soundFolder+voaId+"/"+voaId+ Voa.MP3_SUFFIX;
        LogUtils.debug("原文音频播放", "getSoundMp3Url: "+audioUrl);
        return audioUrl;
    }

    class Apk {
        public static boolean isChild() {
            if (BuildConfig.APPLICATION_ID.equals("com.iyuba.talkshow.juniorenglish")
                    || BuildConfig.APPLICATION_ID.equals("com.iyuba.talkshow.junior")
                    || BuildConfig.APPLICATION_ID.equals("com.iyuba.primarypro")
                    || BuildConfig.APPLICATION_ID.equals("com.iyuba.primaryenglish")) {
                return App.APP_ID == 259;
            } else if (BuildConfig.APPLICATION_ID.equals("com.iyuba.xiaoxue")
                    || BuildConfig.APPLICATION_ID.equals("com.iyuba.talkshow.childenglishnew")
                    || BuildConfig.APPLICATION_ID.equals("com.iyuba.talkshow.childenglish")) {
                return App.APP_ID == 260;
            } else {
                return App.APP_ID == 256;
            }
        }
    }

    class Web{
        public static String WEB_SUFFIX = "iyuba.cn/";
        public static String WEB2_SUFFIX = "iyuba.com.cn/";
        public static String wordUrl = "http://static2." + WEB_SUFFIX + "aiciaudio/primary_audio.zip";
        public static String EVALUATE_URL_CORRECT = "http://iuserspeech." + WEB_SUFFIX.replace("/","") + ":9001/test/ai/";
        public static String EVALUATE_URL_NEW = "http://iuserspeech." + WEB_SUFFIX.replace("/","") + ":9001/test/eval/";
        public static String EVAL_PREFIX = "http://iuserspeech." + WEB_SUFFIX.replace("/","") + ":9001/voa/";
        public static String sound_vip = "http://static2." + WEB_SUFFIX + "newconcept/";
        public static String sound_voa = "http://staticvip." + WEB_SUFFIX + "sounds/voa/sentence";
        public static String WordBASEURL = "http://word." + WEB_SUFFIX + "words/";
        public static String VIP_VIDEO_PREFIX = "http://staticvip." + WEB_SUFFIX + "video/voa/";
        public static String VIDEO_PREFIX = "http://staticvip." + WEB_SUFFIX + "video/voa/";
        public static String VIDEO_PREFIX_NEW = "http://m." + WEB_SUFFIX + "voaS/playPY.jsp?apptype=";
        public static String VIP_SOUND_PREFIX = "http://staticvip." + WEB_SUFFIX + "sounds/voa/";
        public static String SOUND_PREFIX = "http://staticvip." + WEB_SUFFIX + "sounds/voa/";
    }

    class User {
        public static final String AVATAR_FILENAME = "avatar.jpg";
        static final String IYUBA_V2 = "iyubaV2";
        public static boolean isPreVerifyDone = false;
        public static boolean devMode = false;


        public static String getRegisterByEmailSign(String username, String password, String email) {
            StringBuilder sb = new StringBuilder();
            return MD5.getMD5ofStr(
                    sb.append(UserService.Register.Param.Value.PROTOCOL)
                            .append(username)
                            .append(MD5.getMD5ofStr(password))
                            .append(email)
                            .append(IYUBA_V2)
                            .toString());
        }

        public static String getRegisterByPhoneSign(String username, String password) {
            StringBuilder sb = new StringBuilder();
            return MD5.getMD5ofStr(
                    sb.append(UserService.Register.Param.Value.PROTOCOL)
                            .append(username)
                            .append(MD5.getMD5ofStr(password))
                            .append(IYUBA_V2)
                            .toString());
        }

        public static String getLoginSign(String username, String password) {
            StringBuilder sb = new StringBuilder();
            return MD5.getMD5ofStr(
                    sb.append(UserService.Login.Param.Value.PROTOCOL)
                            .append(username)
                            .append(MD5.getMD5ofStr(password))
                            .append(IYUBA_V2)
                            .toString());
        }


        public static String getClearUserSign(int protocol , String username, String password) {
            return MD5.getMD5ofStr(protocol + username + MD5.getMD5ofStr(password)+ "iyubaV2");
        }

        public static String getUserInfoSign(int uid) {
            return MD5.getMD5ofStr("20001" + uid + IYUBA_V2);
        }

        public static String getUserBasicInfoSign(int uid) {
            return MD5.getMD5ofStr(UserService.GetUserBasicInfo.Param.Value.PROTOCOL + uid + IYUBA_V2);
        }

        public static String editUserBasicInfoSign(int uid) {
            return MD5.getMD5ofStr(UserService.EditUserBasicInfo.Param.Value.PROTOCOL + uid + IYUBA_V2);
        }
    }

    interface Voa {
        int DEFAULT_UID = 0;
        String MP4_SUFFIX = ".mp4";
        String MP3_SUFFIX = ".mp3";
        String WAV_SUFFIX = ".wav";
        String AAC_SUFFIX = ".aac";
        String AMR_SUFFIX = ".amr";
        String JPG_SUFFIX = ".jpg";
        String ZIP_SUFFIX = ".zip";

        String SEPARATOR = "/";
        String TMP_PREFIX = "tmp";
        String SILENT_AAC_NAME = "silent.aac";
        String MERGE_AAC_NAME = "merge.aac";
        int SILENT_PIECE_TIME = 100;
        String COMMENT_VOICE_NAME = "comment_voice";
        String COMMENT_VOICE_SUFFIX = ".amr";
        int MAX_DIFFICULTY = 5;
        String FEEDBACK_END = "\n来自初中英语";
        String MERGE_MP3_NAME = "merge.mp3";
        String SIGN_PNG_NAME = "sign.png";
    }

    class Url {
        public static String PROTOCOL_URL_HEADER_IYUYAN = "http://iuserspeech."+ Web.WEB_SUFFIX.replace("/","")+ ":9001/api/ailanguageprotocol.jsp?apptype=";
        public static String PROTOCOL_URL_HEADER_IYUBA = "http://iuserspeech."+ Web.WEB_SUFFIX.replace("/","")+ ":9001/api/protocol.jsp?apptype=";
        public static String PROTOCOL_URL_HEADER_HS = "http://iuserspeech."+ Web.WEB_SUFFIX.replace("/","")+ ":9001/api/protocolsh.jsp?apptype=";
        public static String PROTOCOL_URL_SHANGHAI = "http://iuserspeech."+ Web.WEB_SUFFIX.replace("/","")+ ":9001/api/ailanguageprotocol666.jsp?apptype=";
        //北京爱语言
        public static String VIP_AGREEMENT_BJIYY_PRIVACY = "http://iuserspeech."+ Web.WEB_SUFFIX.replace("/","")+ ":9001/api/vipServiceProtocol.jsp?company=2&type=app";
        public static String CHILD_PROTOCOL_BJIYY_PRIVACY = "http://iuserspeech."+ Web.WEB_SUFFIX.replace("/","")+ ":9001/api/protocolpriForChildren.jsp?company=3&apptype=";
        public static String PROTOCOL_BJIYY_USAGE = "http://iuserspeech."+ Web.WEB_SUFFIX.replace("/","")+ ":9001/api/protocoluse.jsp?company=3&apptype=";
        public static String PROTOCOL_BJIYY_PRIVACY = "http://iuserspeech."+ Web.WEB_SUFFIX.replace("/","")+ ":9001/api/protocolpri.jsp?company=3&apptype=";
        //北京爱语吧
        public static String VIP_AGREEMENT_BJIYB_PRIVACY = "http://iuserspeech."+ Web.WEB_SUFFIX.replace("/","")+ ":9001/api/vipServiceProtocol.jsp?company=1&type=app";
        public static String CHILD_PROTOCOL_BJIYB_PRIVACY = "http://iuserspeech."+ Web.WEB_SUFFIX.replace("/","")+ ":9001/api/protocolpriForChildren.jsp?company=1&apptype=";
        public static String PROTOCOL_BJIYB_PRIVACY = "http://iuserspeech."+ Web.WEB_SUFFIX.replace("/","")+ ":9001/api/protocolpri.jsp?company=1&apptype=";
        public static String PROTOCOL_BJIYB_USAGE = "http://iuserspeech."+ Web.WEB_SUFFIX.replace("/","")+ ":9001/api/protocoluse.jsp?company=1&apptype=";

        public static String APP_ICON_URL = "http://app."+ Constant.Web.WEB_SUFFIX+"android/images/Englishtalkshow/Englishtalkshow.png";
        // 分享应用时是url
        public static String APP_SHARE_URL = "http://voa."+ Web.WEB_SUFFIX+ "voa/shareApp.jsp?appType=";
        // 邮箱注册的url
        public static String EMAIL_REGILTER = "http://api."+ Web.WEB2_SUFFIX+ "v2/api.iyuba?protocol=11002&app=meiyu";
        // 手机注册的url
        public static String PHONE_REGISTER = "http://api."+ Web.WEB2_SUFFIX+ "v2/api.iyuba?platform=android&app=meiyu&protocol=11002";
        // 用户头像url
        public static String USER_IMAGE = "http://api."+ Web.WEB2_SUFFIX+ "v2/api.iyuba?";
        public static String WEB_PAY = "http://app."+ Web.WEB_SUFFIX+"wap/servlet/paychannellist?";
        public static String AD_PIC = "http://dev."+ Web.WEB_SUFFIX+ "";
        public static String MORE_APP = "http://app."+ Web.WEB_SUFFIX+"android";
        public static String COMMENT_VOICE_BASE = "http://voa."+ Web.WEB_SUFFIX+"voa/";

        public static String VOA_IMG_BASE = "http://staticvip."+ Web.WEB_SUFFIX+"images/voa/";
        public static final String JPG_SUFFIX = ".jpg";

        public static String SHUOSHUO_PREFIX = "http://staticvip."+ Web.WEB_SUFFIX+"video/voa/";
//        public static String DUBBING_PREFIX = "http://static."+ Web.WEB_SUFFIX;
        public static String NEW_DUBBING_PREFIX = "http://iuserspeech."+ Web.WEB_SUFFIX.replace("/","")+ ":9001/";
        public static String MY_DUBBING_PREFIX = "http://voa."+ Web.WEB_SUFFIX+"voa/talkShowShare.jsp?shuoshuoId=";
//        public static final String MY_DUBBING_PREFIX_VOA = "http://m."+ Web.WEB_SUFFIX+ "voaS/play.jsp?id=";
        public static final String MP4_SUFFIX = ".mp4";
//        public static final String URL_HEADER = "http://static."+ Web.WEB_SUFFIX;

        static final String AND = "&";
        static final String EQUALITY = "=";

        interface UserImageParam {
            interface Key {
                String PROTOCOL = "protocol";
                String UID = "uid";
                String SIZE = "size";
                String TIMESTAMP = "timestamp";
            }

            interface Value {
                int PROTOCOL = 10005;
                String SIZE_BIG = "big";
                String SIZE_MIDDLE = "middle";
            }
        }

        private static String getUserImageUrl(int uid, String size, String timestamp) {
            return USER_IMAGE +
                    UserImageParam.Key.PROTOCOL + EQUALITY + UserImageParam.Value.PROTOCOL
                    + AND + UserImageParam.Key.UID + EQUALITY + uid
                    + AND + UserImageParam.Key.SIZE + EQUALITY + size
                    + AND + UserImageParam.Key.TIMESTAMP + timestamp;
        }

        public static String getBigUserImageUrl(int uid, String timestamp) {
            return getUserImageUrl(uid, UserImageParam.Value.SIZE_BIG, timestamp);
        }

        public static String getMiddleUserImageUrl(int uid, String timestamp) {
            return getUserImageUrl(uid, UserImageParam.Value.SIZE_MIDDLE, timestamp);
        }

        public static String getVoaImg(int voaId) {
            return VOA_IMG_BASE + voaId + JPG_SUFFIX;
        }

        interface WebPay {
            interface Param {
                interface Key {
                    String OUT_USER = "out_user";
                    String APP_ID = "appid";
                    String AMOUNT = "amount";
                }
            }
        }

        public static String getWebPayUrl(int uid, int amount) {
            return WEB_PAY + WebPay.Param.Key.OUT_USER + EQUALITY + uid
                    + AND + WebPay.Param.Key.APP_ID + EQUALITY + App.APP_ID
                    + AND + WebPay.Param.Key.AMOUNT + EQUALITY + amount;
        }

        public static String getAdPicUrl(String suffix) {
            return AD_PIC + suffix;
        }

        @Deprecated
        public static String getDubbingUrl(int id) {
            return SHUOSHUO_PREFIX + id + MP4_SUFFIX;
        }

        public static String getNewDubbingUrl(String id) {
            return NEW_DUBBING_PREFIX + id;
        }

        public static String getMyDubbingUrl(int backId) {
            return MY_DUBBING_PREFIX + backId;
        }

        public static String getMyDubbingUrl(int backId,String appName) {
            return MY_DUBBING_PREFIX + backId+"&apptype="+appName;
        }
    }

    interface PackageName{
        String Package_junior = "com.iyuba.talkshow.junior";//初中英语
        String Package_juniorenglish = "com.iyuba.talkshow.juniorenglish";//初中英语口语秀
    }

}
