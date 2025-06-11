package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @title: 小说-章节内容
 * @date: 2023/4/27 14:28
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Novel_chapter implements Serializable {
    private static final long serialVersionUID = -1798901331096683142L;


    /**
     * voaid : 20401
     * orderNumber : 4
     * level : 1
     * chapterOrder : 1
     * sound : /bookworm/sound/1_4_1.mp3
     * show : 0
     * cname_cn : 小贩
     * cname_en : Chapter1   The Pedlar
     */

    private String voaid;
    private String orderNumber;
    private String level;
    private String chapterOrder;
    private String sound;
    private int show;
    private String cname_cn;
    private String cname_en;

    public String getVoaid() {
        return voaid;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getLevel() {
        return level;
    }

    public String getChapterOrder() {
        return chapterOrder;
    }

    public String getSound() {
        return sound;
    }

    public int getShow() {
        return show;
    }

    public String getCname_cn() {
        return cname_cn;
    }

    public String getCname_en() {
        return cname_en;
    }
}
