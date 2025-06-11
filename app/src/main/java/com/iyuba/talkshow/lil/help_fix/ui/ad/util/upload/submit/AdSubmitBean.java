package com.iyuba.talkshow.lil.help_fix.ui.ad.util.upload.submit;

import java.io.Serializable;

/**
 * 广告上传信息的模型
 */
public class AdSubmitBean implements Serializable {
    /**
     * ad_space : 1
     * date_time : 1710814598
     * install_package :
     * platform : 3
     * type : 1
     */

    //ads			是一个jsonarray数组, 对象数据为下面三个参数
    //platform	平台, 百度, 流量汇, 穿山甲, 快手, 顺序1-4
    //ad_space	广告位, banner, 信息流, 开屏, 激励, 插屏, draw, 顺序1-6
    //type		0, 请求；1, 展现；2, 点击；

    private String ad_space;
    private String date_time;
    private String install_package;
    private String platform;
    private String type;

    public AdSubmitBean(String ad_space, String date_time, String install_package, String platform, String type) {
        this.ad_space = ad_space;
        this.date_time = date_time;
        this.install_package = install_package;
        this.platform = platform;
        this.type = type;
    }

    public String getAd_space() {
        return ad_space;
    }

    public String getDate_time() {
        return date_time;
    }

    public String getInstall_package() {
        return install_package;
    }

    public String getPlatform() {
        return platform;
    }

    public String getType() {
        return type;
    }
}
