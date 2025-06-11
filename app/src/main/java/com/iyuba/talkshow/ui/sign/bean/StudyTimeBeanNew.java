package com.iyuba.talkshow.ui.sign.bean;

import java.io.Serializable;

public class StudyTimeBeanNew implements Serializable {



    private String result ;//算成功
    private String totalTime ; //今日总学习时长,单位秒
    private String totalDaysTime ;//总学习天数时长, 单位秒
    private String shareId;

    private String totalWords;//总学习单词数,单位各

    private String totalDays;//熊学习天数,单位天
    private String sentence;//返回的英文的名言警句

    private String totalUser;//总用户数

    private String ranking;//今日当前排名

    private String totalWord;//今日总单词数

    public String getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(String totalUser) {
        this.totalUser = totalUser;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getTotalWord() {
        return totalWord;
    }

    public void setTotalWord(String totalWord) {
        this.totalWord = totalWord;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalDaysTime() {
        return totalDaysTime;
    }

    public void setTotalDaysTime(String totalDaysTime) {
        this.totalDaysTime = totalDaysTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(String totalWords) {
        this.totalWords = totalWords;
    }

    public String getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(String totalDays) {
        this.totalDays = totalDays;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }
}
