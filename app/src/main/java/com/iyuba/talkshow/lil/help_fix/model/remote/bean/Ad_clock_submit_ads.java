package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

/**
 * 广告-提交数据的格式化数据类
 */
public class Ad_clock_submit_ads {

    /**
     * ad_space : 1
     * date_time : 1710152981
     * install_package :
     * platform : 3
     * type : 0
     */

    private String ad_space;
    private String date_time;
    private String install_package;
    private String platform;
    private String type;

    public Ad_clock_submit_ads(String ad_space, String date_time, String install_package, String platform, String type) {
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
