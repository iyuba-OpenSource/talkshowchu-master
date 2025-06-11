package com.iyuba.talkshow.lil.help_fix.ui.ad.util;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;

/**
 * 本地广告数据
 */
public interface AdDataUtil {

    //链接数据
    interface AdUrl{
        /***********开屏广告**********/
        //本地图片
        int localSpreadAdPic = R.drawable.default_splash_ad;

        //本地链接图片
        //http://app.iyuba.cn/dev/upload/1679379374314.jpg
        static String localSpreadAdPicUrl(){
            return "http://app."+ Constant.Web.WEB_SUFFIX+"/dev/upload/1679379374314.jpg";
        }

        //本地跳转链接
        static String localSpreadAdJumpUrl(){
            return "https://apps."+Constant.Web.WEB_SUFFIX+"/mall/teachercourse.html";
        }

        /**********banner广告**********/
        //本地图片
        int localBannerAdPic = R.drawable.default_banner_ad;

        //本地链接图片
        static String localBannerAdPicUrl(){
            return "http://app."+Constant.Web.WEB_SUFFIX+"/dev/upload/1679381438179.jpg";
        }

        //本地跳转链接
        static String localBannerAdJumpUrl(){
            return "https://apps."+Constant.Web.WEB_SUFFIX+"/mall/teachercourse.html";
        }

        /***********接口数据***********/
        //接口拼接图片
        static String fixPicUrl(String picUrl){
            return "http://dev."+Constant.Web.WEB_SUFFIX+"/"+picUrl;
        }

        //接口拼接链接
        static String fixJumpUrl(String linkUrl){
            return linkUrl;
        }
    }
}
