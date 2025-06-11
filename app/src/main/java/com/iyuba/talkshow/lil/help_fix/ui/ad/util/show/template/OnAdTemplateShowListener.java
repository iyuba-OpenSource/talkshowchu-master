package com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.template;

//信息流广告回调接口
public interface OnAdTemplateShowListener {
    //加载完成广告
    void onLoadFinishAd();

    //展示广告
    void onAdShow(String showAdMsg);

    //点击广告
    void onAdClick();
}
