package com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.interstitial;

/**
 * 广告显示的模型-插屏广告
 */
public class AdInterstitialViewBean {

    private AdInterstitialShowManager.OnAdInterstitialShowListener onAdInterstitialShowListener;//广告回调接口

    public AdInterstitialViewBean(AdInterstitialShowManager.OnAdInterstitialShowListener onAdInterstitialShowListener) {
        this.onAdInterstitialShowListener = onAdInterstitialShowListener;
    }

    public AdInterstitialShowManager.OnAdInterstitialShowListener getOnAdInterstitialShowListener() {
        return onAdInterstitialShowListener;
    }
}
