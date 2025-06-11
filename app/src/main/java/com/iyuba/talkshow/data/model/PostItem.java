package com.iyuba.talkshow.data.model;

import java.util.List;

public class PostItem {
    public List<WavListItem> getWavListItems() {
        return wavList;
    }

    public void setWavListItems(List<WavListItem> wavListItems) {
        this.wavList = wavListItems;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public int getParaId() {
        return paraId;
    }

    public void setParaId(int paraId) {
        this.paraId = paraId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getShuoshuotype() {
        return shuoshuotype;
    }

    public void setShuoshuotype(int shuoshuotype) {
        this.shuoshuotype = shuoshuotype;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getVoaid() {
        return voaid;
    }

    public void setVoaid(int voaid) {
        this.voaid = voaid;
    }

    List<WavListItem> wavList ;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    String appName ;
    int flag = 1;
    String format ;
    int idIndex = 0;
    int paraId ;
    String platform ;
    int score ;
    int shuoshuotype ;
    String sound ;
    String topic ;
    String username;
    int voaid ;
    int category;

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
