package com.iyuba.talkshow.constant;

/**
 * 广告测试的key数据
 *
 * 自用数据
 */
public interface AdTestKeyData {

    /**
     * 广告位key配置如下
     *
     * 穿山甲
     * com.iyuba.talkshow.junior 开屏 0042
     * com.iyuba.talkshow.junior Banner 0043
     * com.iyuba.talkshow.junior 插屏 0311
     * com.iyuba.talkshow.junior 模版 0312
     * com.iyuba.talkshow.junior DrawVideo 0313
     * com.iyuba.talkshow.junior 激励视频 0314
     *
     * 优量汇
     * com.iyuba.talkshow.junior 开屏 0315
     * com.iyuba.talkshow.junior Banner 0316
     * com.iyuba.talkshow.junior 插屏 0317
     * com.iyuba.talkshow.junior 模版 0318
     * com.iyuba.talkshow.junior DrawVideo 0319
     * com.iyuba.talkshow.junior 激励视频 0320
     *
     * 百度
     * com.iyuba.talkshow.junior 开屏 0321
     * com.iyuba.talkshow.junior 插屏 0322
     * com.iyuba.talkshow.junior 模版 0323
     * com.iyuba.talkshow.junior 激励视频 0324
     *
     * 快手
     * com.iyuba.talkshow.junior 开屏 0325
     * com.iyuba.talkshow.junior 插屏 0326
     * com.iyuba.talkshow.junior 模版 0327
     * com.iyuba.talkshow.junior DrawVideo 0328
     * com.iyuba.talkshow.junior 激励视频 0329
     *
     *
     * 接口数据请在浏览器中查看 http://ai.iyuba.cn/mediatom/server/adplace?placeid=0329
     */

    //key值信息
    interface  KeyData{
        class SpreadAdKey{
            public static final String spread_youdao = "a710131df1638d888ff85698f0203b46";//有道
            public static final String spread_beizi = "";//倍孜
            public static final String spread_csj = "0042";//穿山甲
            public static final String spread_ylh = "0315";//优量汇
            public static final String spread_baidu = "0321";//百度-0321
            public static final String spread_ks = "0325";//快手
        }

        class TemplateAdKey{
            public static final String template_youdao = "3438bae206978fec8995b280c49dae1e";//有道
            public static final String template_csj = "0312";//穿山甲
            public static final String template_ylh = "0318";//优量汇
            public static final String template_baidu = "0323";//百度-0323
            public static final String template_ks = "0327";//快手
            public static final String template_vlion = "";//瑞狮
        }

        class BannerAdKey{
            public static final String banner_youdao = "230d59b7c0a808d01b7041c2d127da95";//有道
            public static final String banner_csj = "0043";//穿山甲
            public static final String banner_ylh = "0316";//优量汇
        }

        class InterstitialAdKey{
            public static final String interstitial_csj = "0311";//穿山甲
            public static final String interstitial_ylh = "0317";//优量汇
            public static final String interstitial_baidu = "0322";//百度-0322
            public static final String interstitial_ks = "0326";//快手
        }

        class DrawVideoAdKey{
            public static final String drawVideo_csj = "0313";//穿山甲
            public static final String drawVideo_ylh = "0319";//优量汇
            public static final String drawVideo_ks = "0328";//快手
        }

        class IncentiveAdKey{
            public static final String incentive_csj = "0314";//穿山甲
            public static final String incentive_ylh = "0320";//优量汇
            public static final String incentive_baidu = "0324";//百度-0324
            public static final String incentive_ks = "0329";//快手
        }
    }
}
