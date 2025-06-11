package com.iyuba.talkshow.lil.help_fix.util.ad.bean;

import com.iyuba.talkshow.data.model.RecyclerItem;

/**
 * @title: 信息流广告-web
 * @date: 2023/9/13 15:04
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class AdStreamWebBean implements RecyclerItem {

    private String title;//标题
    private String picUrl;//图片链接
    private String linkUrl;//跳转链接

    public AdStreamWebBean(String title, String picUrl, String linkUrl) {
        this.title = title;
        this.picUrl = picUrl;
        this.linkUrl = linkUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }
}
