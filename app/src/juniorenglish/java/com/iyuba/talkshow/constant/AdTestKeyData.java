package com.iyuba.talkshow.constant;

/**
 * 广告测试的key数据
 */
public interface AdTestKeyData {
    /**
     * 广告位key配置如下
     *
     * 穿山甲
     * com.iyuba.talkshow.juniorenglish 开屏 0044
     * com.iyuba.talkshow.juniorenglish Banner 0045
     * com.iyuba.talkshow.juniorenglish 插屏 0429
     * com.iyuba.talkshow.juniorenglish 模版 0430
     * com.iyuba.talkshow.juniorenglish DrawVideo 0431
     * com.iyuba.talkshow.juniorenglish 激励视频 0432
     *
     * 优量汇
     * com.iyuba.talkshow.juniorenglish 开屏 0433
     * com.iyuba.talkshow.juniorenglish Banner 0434
     * com.iyuba.talkshow.juniorenglish 插屏 0435
     * com.iyuba.talkshow.juniorenglish 模版 0436
     * com.iyuba.talkshow.juniorenglish DrawVideo 0437
     * com.iyuba.talkshow.juniorenglish 激励视频 0438
     *
     * 百度
     * com.iyuba.talkshow.juniorenglish 开屏 0439
     * com.iyuba.talkshow.juniorenglish 插屏 0440
     * com.iyuba.talkshow.juniorenglish 模版 0441
     * com.iyuba.talkshow.juniorenglish 激励视频 0442
     *
     * 快手
     * com.iyuba.talkshow.juniorenglish 开屏 0443
     * com.iyuba.talkshow.juniorenglish 插屏 0444
     * com.iyuba.talkshow.juniorenglish 模版 0445
     * com.iyuba.talkshow.juniorenglish DrawVideo 0446
     * com.iyuba.talkshow.juniorenglish 激励视频 0447
     *
     *
     * 接口数据请在浏览器中查看 http://ai.iyuba.cn/mediatom/server/adplace?placeid=0447
     */

    //key值信息
    interface  KeyData{
        class SpreadAdKey{
            /**
             * 穿山甲 0044
             * 优量汇 0433
             * 百度 0439
             * 快手 0443
             */
            public static final String spread_youdao = "a710131df1638d888ff85698f0203b46";//有道
            public static final String spread_beizi = "";
            public static final String spread_csj = "0044";//穿山甲
            public static final String spread_ylh = "0433";//优量汇
            public static final String spread_baidu = "0439";//百度
            public static final String spread_ks = "0443";//快手
        }

        class TemplateAdKey{
            /**
             * 穿山甲 0430
             * 优量汇 0436
             * 百度 0441
             * 快手 0445
             */
            public static final String template_youdao = "3438bae206978fec8995b280c49dae1e";//有道
            public static final String template_csj = "0430";//穿山甲
            public static final String template_ylh = "0436";//优量汇
            public static final String template_baidu = "0441";//百度
            public static final String template_ks = "0445";//快手
            public static final String template_vlion = "";//瑞狮
        }

        class BannerAdKey{
            /**
             * 穿山甲 0045
             * 优量汇 0434
             */
            public static final String banner_youdao = "230d59b7c0a808d01b7041c2d127da95";//有道
            public static final String banner_csj = "0045";//穿山甲
            public static final String banner_ylh = "0434";//优量汇
        }

        class InterstitialAdKey{
            /**
             * 穿山甲 0429
             * 优量汇 0435
             * 百度 0440
             * 快手 0444
             */
            public static final String interstitial_csj = "0429";//穿山甲
            public static final String interstitial_ylh = "0435";//优量汇
            public static final String interstitial_baidu = "0440";//百度
            public static final String interstitial_ks = "0444";//快手
        }

        class DrawVideoAdKey{
            /**
             * 穿山甲 0431
             * 优量汇 0437
             * 快手 0446
             */
            public static final String drawVideo_csj = "0431";//穿山甲
            public static final String drawVideo_ylh = "0437";//优量汇
            public static final String drawVideo_ks = "0446";//快手
        }

        class IncentiveAdKey{
            /**
             * 穿山甲 0432
             * 优量汇 0438
             * 百度 0442
             * 快手 0447
             */
            public static final String incentive_csj = "0432";//穿山甲
            public static final String incentive_ylh = "0438";//优量汇
            public static final String incentive_baidu = "0442";//百度
            public static final String incentive_ks = "0447";//快手
        }
    }

    //相关的工具
    interface Util{
        //获取接口中的广告位置数据

        //获取接口中的广告类型数据
    }
}
