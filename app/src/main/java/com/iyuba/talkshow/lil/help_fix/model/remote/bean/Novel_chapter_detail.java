package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @title: 小说的章节详情内容
 * @date: 2023/4/27 16:08
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Novel_chapter_detail implements Serializable {
    private static final long serialVersionUID = 5906323839626770261L;

    /**
     * BeginTiming : 4.36
     * voaid : 40103
     * chapter_order : 3
     * paraid : 1
     * EndTiming : 12.91
     * image :
     * orderNumber : 1
     * sentence_audio : http://staticvip2.iyuba.cn/bookworm/sound/sentence/3_1_3/3_1_3_1_1.wav
     * level : 3
     * index : 1
     * textCH : 约翰·邓肯周一开始工作，玛丽·卡特带他参观了工厂。
     * textEN : John Duncan started work on Monday, and Mary Carter showed him round the factory.
     */

    private String BeginTiming;
    private String voaid;
    private String chapter_order;
    private String paraid;
    private String EndTiming;
    private String image;
    private String orderNumber;
    private String sentence_audio;
    private String level;
    private String index;
    private String textCH;
    private String textEN;

    public String getBeginTiming() {
        return BeginTiming;
    }

    public String getVoaid() {
        return voaid;
    }

    public String getChapter_order() {
        return chapter_order;
    }

    public String getParaid() {
        return paraid;
    }

    public String getEndTiming() {
        return EndTiming;
    }

    public String getImage() {
        return image;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getSentence_audio() {
        return sentence_audio;
    }

    public String getLevel() {
        return level;
    }

    public String getIndex() {
        return index;
    }

    public String getTextCH() {
        return textCH;
    }

    public String getTextEN() {
        return textEN;
    }
}
