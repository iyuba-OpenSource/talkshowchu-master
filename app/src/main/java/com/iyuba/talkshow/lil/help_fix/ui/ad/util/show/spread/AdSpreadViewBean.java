package com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.spread;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 广告显示的模型-开屏广告
 */
public class AdSpreadViewBean {

    private ImageView adView;//图片控件
    private View timeView;//倒计时控件
    private View tipsView;//提示控件
    private ViewGroup adLayout;//广告样式界面

    private AdSpreadShowManager.OnAdSpreadShowListener onAdSpreadShowListener;//广告回调

    //新增内容
    private int adShowDownTime;//广告的倒计时时间
    private int operateDownTime;//操作的倒计时时间

    public AdSpreadViewBean(ImageView adView, View timeView, View tipsView, ViewGroup adLayout, AdSpreadShowManager.OnAdSpreadShowListener onAdSpreadShowListener, int adShowDownTime, int operateDownTime) {
        this.adView = adView;
        this.timeView = timeView;
        this.tipsView = tipsView;
        this.adLayout = adLayout;
        this.onAdSpreadShowListener = onAdSpreadShowListener;
        this.adShowDownTime = adShowDownTime;
        this.operateDownTime = operateDownTime;
    }

    public ImageView getAdView() {
        return adView;
    }

    public View getTimeView() {
        return timeView;
    }

    public View getTipsView() {
        return tipsView;
    }

    public ViewGroup getAdLayout() {
        return adLayout;
    }

    public AdSpreadShowManager.OnAdSpreadShowListener getOnAdSpreadShowListener() {
        return onAdSpreadShowListener;
    }

    public int getAdShowDownTime() {
        return adShowDownTime;
    }

    public int getOperateDownTime() {
        return operateDownTime;
    }
}
