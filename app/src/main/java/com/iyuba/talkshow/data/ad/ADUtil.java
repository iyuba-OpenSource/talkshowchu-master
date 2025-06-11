package com.iyuba.talkshow.data.ad;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;

/**
 * 本地广告数据
 *
 */
public interface ADUtil {

    //链接数据
    interface AdUrl{
        /***********开屏广告**********/
        //本地图片
        int localSplashADPic = R.drawable.default_splash_ad;

        //本地链接图片
        static String localSplashADPicUrl(){
            return "http://app."+ Constant.Web.WEB_SUFFIX+"dev/upload/1679379374314.jpg";
        }

        //本地跳转链接
        static String localSplashADJumpUrl(){
            return "http://app."+Constant.Web.WEB_SUFFIX;
        }

        /**********banner广告**********/
        //本地图片
        int localBannerADPic = R.drawable.default_banner_ad;

        //本地链接图片
        static String localBannerADPicUrl(){
            return "http://app."+Constant.Web.WEB_SUFFIX+"/dev/upload/1679381438179.jpg";
        }

        //本地跳转链接
        static String localBannerADJumpUrl(){
            return "http://app."+Constant.Web.WEB_SUFFIX;
        }

        /***********接口数据***********/
        //接口拼接图片
        static String fixPicUrl(String picUrl){
            return "http://dev."+Constant.Web.WEB_SUFFIX+picUrl;
        }

        //接口拼接链接
        static String fixJumpUrl(String linkUrl){
            return linkUrl;
        }
    }
}
