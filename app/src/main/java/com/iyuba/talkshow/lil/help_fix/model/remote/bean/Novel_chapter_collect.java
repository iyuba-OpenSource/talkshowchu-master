package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @title: 小说-收藏的文章数据
 * @date: 2023/7/7 14:08
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description: 适用于小说
 */
public class Novel_chapter_collect implements Serializable {

    /**
     * CollectDate : 2023-07-07 11:37:09.0
     * SentenceFlg : 0
     * orderNumber : 0
     * Category : 0
     * level : 0
     * bookname_en : What a Lottery
     * bookname_cn : 彩票风波
     * sound : /newCamstory/sound/0_0_1.mp3
     * SentenceId : 0
     * show : 1
     * cname_cn : 她要离家出走
     * cname_en : Chapter2&nbsp  She’s leaving home
     * voaid : 10001
     * Series : 0
     * chapterOrder : 1
     * Video :
     * topic : newcamstory
     * Sentence :
     */

    private String CollectDate;
    private int SentenceFlg;
    private String orderNumber;
    private String Category;
    private String level;
    private String bookname_en;
    private String bookname_cn;
    private String sound;
    private String SentenceId;
    private String show;
    private String cname_cn;
    private String cname_en;
    private String voaid;
    private String Series;
    private String chapterOrder;
    private String Video;
    private String topic;
    private String Sentence;

    public String getCollectDate() {
        return CollectDate;
    }

    public void setCollectDate(String CollectDate) {
        this.CollectDate = CollectDate;
    }

    public int getSentenceFlg() {
        return SentenceFlg;
    }

    public void setSentenceFlg(int SentenceFlg) {
        this.SentenceFlg = SentenceFlg;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getBookname_en() {
        return bookname_en;
    }

    public void setBookname_en(String bookname_en) {
        this.bookname_en = bookname_en;
    }

    public String getBookname_cn() {
        return bookname_cn;
    }

    public void setBookname_cn(String bookname_cn) {
        this.bookname_cn = bookname_cn;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getSentenceId() {
        return SentenceId;
    }

    public void setSentenceId(String SentenceId) {
        this.SentenceId = SentenceId;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public String getCname_cn() {
        return cname_cn;
    }

    public void setCname_cn(String cname_cn) {
        this.cname_cn = cname_cn;
    }

    public String getCname_en() {
        return cname_en;
    }

    public void setCname_en(String cname_en) {
        this.cname_en = cname_en;
    }

    public String getVoaid() {
        return voaid;
    }

    public void setVoaid(String voaid) {
        this.voaid = voaid;
    }

    public String getSeries() {
        return Series;
    }

    public void setSeries(String Series) {
        this.Series = Series;
    }

    public String getChapterOrder() {
        return chapterOrder;
    }

    public void setChapterOrder(String chapterOrder) {
        this.chapterOrder = chapterOrder;
    }

    public String getVideo() {
        return Video;
    }

    public void setVideo(String Video) {
        this.Video = Video;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSentence() {
        return Sentence;
    }

    public void setSentence(String Sentence) {
        this.Sentence = Sentence;
    }
}
