package com.iyuba.talkshow.lil.help_fix.ui.ad.util.show;

import android.app.Activity;
import android.text.TextUtils;

import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.tencent.vasdolly.helper.ChannelReaderUtil;

import java.util.Locale;

/**
 * 广告展示的工具数据
 */
public interface AdShowUtil {

    //参数
    class NetParam{
        //新的接口版本使用的appId
        public static int getAdId(){
            String channel = ChannelReaderUtil.getChannel(ResUtil.getInstance().getContext());
            if (channel.toLowerCase(Locale.CHINA).equals("huawei")){
                return App.APP_ID*10+2;
            }
            return App.APP_ID*10+1;
        };

        //信息流广告-起始位置
        public static final int SteamAd_startIndex = 3;
        //信息流广告-间隔位置
        public static final int SteamAd_intervalIndex = 5;

        /**
         * 广告接口的flag参数(接口使用)
         */
        public interface Flag{
            int net_spreadFlag = 1;//开屏广告、插屏、激励
            int net_templateFlag = 2;//信息流广告
            int net_bannerFlag = 4;//banner广告
        }

        /**
         * 广告的显示类型参数(接口返回)
         * youdao：youdao
         * web：就是我们的连接，和之前的逻辑一样，不过现在没返回了
         * ads2：穿山甲的
         * ads3：百度的
         * ads4：优量汇的
         * ads5：快手的
         * ads6：瑞狮
         */
        public interface AdType{
            String show_web = "web";//web类型
            String show_youdao = "youdao";//有道类型
            String show_ads1 = "ads1";//倍孜类型
            String show_ads2 = "ads2";//穿山甲类型
            String show_ads3 = "ads3";//百度类型
            String show_ads4 = "ads4";//优量汇类型
            String show_ads5 = "ads5";//快手类型
            String show_ads6 = "ads6";//瑞狮类型
            String show_other = "other";//其他类型
            String show_exception = "exception";//异常类型
        }

        /**
         * 广告的显示位置参数(自用，非接口)
         */
        public interface AdShowPosition{
            String show_spread = "spread";//开屏
            String show_banner = "banner";//banner
            String show_template = "template";//信息流
            String show_rewardVideo = "rewardVideo";//激励视频
            String show_interstitial = "interstitial";//插屏
            String show_drawVideo = "drawVideo";//draw
        }

        /**
         * 广告的操作(自用，非接口)
         * 0, 请求；1, 展现；2, 点击；
         */
        public interface AdOperation{
            String operation_request = "request";//请求
            String operation_show = "show";//展示
            String operation_click = "click";//点击
        }
    }

    //方法
    class Util{
        //判断界面是否存在
        public static boolean isPageExist(Activity context) {
            if (context == null || context.isFinishing() || context.isDestroyed()) {
                return false;
            }

            return true;
        }

        //广告名称显示
        public static String showAdName(String showType){
            if (TextUtils.isEmpty(showType)){
                return showType;
            }

            switch (showType){
                case NetParam.AdType.show_youdao:
                    return "有道广告";
                case NetParam.AdType.show_web:
                    return "web广告";
                case NetParam.AdType.show_ads1:
                    return "倍孜广告";
                case NetParam.AdType.show_ads2:
                    return "穿山甲广告";
                case NetParam.AdType.show_ads3:
                    return "百度广告";
                case NetParam.AdType.show_ads4:
                    return "优量汇广告";
                case NetParam.AdType.show_ads5:
                    return "快手广告";
                case NetParam.AdType.show_ads6:
                    return "瑞狮广告";
                case NetParam.AdType.show_exception:
                    return "异常类型广告";
                default:
                    return showType;
            }
        }

        /**
         * 获取接口中的显示位置数据(用于将数据上传到服务器)
         * ad_space：广告位，banner，信息流，开屏，激励，插屏，draw，顺序1-6
         *
         * @param showType {@link NetParam 的AdShowPosition内容}
         */
        public static int getNetAdPosition(String showType){
            switch (showType){
                case NetParam.AdShowPosition.show_banner:
                    return 1;
                case NetParam.AdShowPosition.show_template:
                    return 2;
                case NetParam.AdShowPosition.show_spread:
                    return 3;
                case NetParam.AdShowPosition.show_rewardVideo:
                    return 4;
                case NetParam.AdShowPosition.show_interstitial:
                    return 5;
                case NetParam.AdShowPosition.show_drawVideo:
                    return 6;
                default:
                    return -1;
            }
        }

        /**
         * 获取接口中的广告类型数据
         * platform：平台，百度，流量汇，穿山甲，快手，顺序1-4，0是有道
         *
         * @param adType {@link NetParam 的AdType内容}
         */
        public static int getNetAdType(String adType){
            if (TextUtils.isEmpty(adType)){
                return -1;
            }

            switch (adType){
                case NetParam.AdType.show_youdao:
                    return 0;
                case NetParam.AdType.show_ads3:
                    return 1;
                case NetParam.AdType.show_ads4:
                    return 2;
                case NetParam.AdType.show_ads2:
                    return 3;
                case NetParam.AdType.show_ads5:
                    return 4;
                default:
                    return -1;
            }
        }

        /**
         * 获取接口中的广告操作数据
         * 0, 请求；1, 展现；2, 点击；
         *
         * @param adOperation {@link NetParam 的AdOperation数据}
         */
        public static int getNetAdOperation(String adOperation){
            if (TextUtils.isEmpty(adOperation)){
                return  -1;
            }

            switch (adOperation){
                case NetParam.AdOperation.operation_request://请求
                    return 0;
                case NetParam.AdOperation.operation_show://显示
                    return 1;
                case NetParam.AdOperation.operation_click://点击
                    return 2;
                default:
                    return -1;
            }
        }
    }
}
