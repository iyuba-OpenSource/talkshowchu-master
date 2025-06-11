package com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.banner;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 广告显示的模型-banner广告
 */
public class AdBannerViewBean {

    //爱语吧sdk广告的界面
    private ViewGroup iyubaSdkAdLayout;

    //web和有道广告的界面
    private ViewGroup webAdLayout;
    private ImageView webAdImage;
    private View webAdClose;
    private View webAdTips;

    private AdBannerShowManager.OnAdBannerShowListener onAdBannerShowListener;//广告回调接口

    public AdBannerViewBean(ViewGroup iyubaSdkAdLayout, ViewGroup webAdLayout, ImageView webAdImage, View webAdClose, View webAdTips, AdBannerShowManager.OnAdBannerShowListener onAdBannerShowListener) {
        this.iyubaSdkAdLayout = iyubaSdkAdLayout;
        this.webAdLayout = webAdLayout;
        this.webAdImage = webAdImage;
        this.webAdClose = webAdClose;
        this.webAdTips = webAdTips;
        this.onAdBannerShowListener = onAdBannerShowListener;
    }

    public ViewGroup getIyubaSdkAdLayout() {
        return iyubaSdkAdLayout;
    }

    public ViewGroup getWebAdLayout() {
        return webAdLayout;
    }

    public ImageView getWebAdImage() {
        return webAdImage;
    }

    public View getWebAdClose() {
        return webAdClose;
    }

    public View getWebAdTips() {
        return webAdTips;
    }

    public AdBannerShowManager.OnAdBannerShowListener getOnAdBannerShowListener() {
        return onAdBannerShowListener;
    }
}
