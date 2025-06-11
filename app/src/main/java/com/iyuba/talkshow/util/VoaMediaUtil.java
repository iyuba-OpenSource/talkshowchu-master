package com.iyuba.talkshow.util;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.constant.App;

/**
 * Created by Administrator on 2016/12/1 0001.
 */

public class VoaMediaUtil {

    private static final String QUESTION_MARK = "?";
    private static final String TIMESTAMP = "timestamp";
    private static final String EQUAL = "=";

    public static String getAudioUrl(String sound) {
        return Constant.Web.SOUND_PREFIX + sound + addTimestamp();
    }

//    public static String getVideoUrl(int cat , int voaId) {
////        http://staticvip.iyuba.cn/video/voa/cat/voaid.mp4
//        return Constant.Web.VIDEO_PREFIX+cat+"/"+ voaId + Constant.Voa.MP4_SUFFIX;
//    }

    // TODO: 2022/7/13 这里根据要求优化获取播放视频的url地址
    //http://staticvip.iyuba.cn + /video/voa/313/313001.mp4
    public static String getVideoUrl(String videoUrlSuffix){
        String prefix = "http://static0."+Constant.Web.WEB_SUFFIX+"video/voa/";
        prefix = prefix.replace("/video/voa/","");
        if (!videoUrlSuffix.startsWith("/")){
            videoUrlSuffix+="/"+videoUrlSuffix;
        }
        return prefix+videoUrlSuffix;
    }

    public static String getShareUrl(int cat , int voaId) {
//        http://m.iyuba.cn/voaS/playPY.jsp?apptype=juniorEnglish&id=316044
        return Constant.Web.VIDEO_PREFIX_NEW + App.SHARE_NAME_EN + "&id=" + voaId ;
    }

    public static String getAudioVipUrl(String sound) {
        return Constant.Web.VIP_SOUND_PREFIX + sound + addTimestamp();
    }

//    public static String getVideoVipUrl(int voaId) {
//        return Constant.Web.VIP_VIDEO_PREFIX+ voaId + Constant.Voa.MP4_SUFFIX;
//    }

    // TODO: 2022/7/13 普通用户和vip用户都变成拼接video即可
    public static String getVideoVipUrl(String videoUrlSuffix) {
        String prefix = "http://staticvip."+Constant.Web.WEB_SUFFIX+"video/voa/";
        prefix = prefix.replace("/video/voa/","");
        if (!videoUrlSuffix.startsWith("/")){
            videoUrlSuffix+="/"+videoUrlSuffix;
        }
        return prefix+videoUrlSuffix;
    }

    private static String addTimestamp() {
        return QUESTION_MARK + TIMESTAMP + EQUAL + TimeUtil.getTimeStamp();

    }
}
