package com.iyuba.talkshow.lil.help_fix.data.bean;

import java.io.Serializable;

/**
 * @title: 章节-详情的数据
 * @date: 2023/5/4 16:52
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ChapterDetailBean implements Serializable {
    private static final long serialVersionUID = 3341504571488895246L;

    private String types;
    private String voaId;
    private String paraId;
    private String indexId;

    private String sentence;
    private String sentenceCn;
    private double timing;
    private double endTiming;

    private String pic;
    private String startX;
    private String startY;
    private String endX;
    private String endY;

    public ChapterDetailBean(String types, String voaId, String paraId, String indexId, String sentence, String sentenceCn, double timing, double endTiming, String pic, String startX, String startY, String endX, String endY) {
        this.types = types;
        this.voaId = voaId;
        this.paraId = paraId;
        this.indexId = indexId;
        this.sentence = sentence;
        this.sentenceCn = sentenceCn;
        this.timing = timing;
        this.endTiming = endTiming;
        this.pic = pic;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTypes() {
        return types;
    }

    public String getVoaId() {
        return voaId;
    }

    public String getParaId() {
        return paraId;
    }

    public String getIndexId() {
        return indexId;
    }

    public String getSentence() {
        return sentence;
    }

    public String getSentenceCn() {
        return sentenceCn;
    }

    public double getTiming() {
        return timing;
    }

    public double getEndTiming() {
        return endTiming;
    }

    public String getPic() {
        return pic;
    }

    public String getStartX() {
        return startX;
    }

    public String getStartY() {
        return startY;
    }

    public String getEndX() {
        return endX;
    }

    public String getEndY() {
        return endY;
    }
}
