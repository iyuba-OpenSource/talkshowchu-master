package com.iyuba.talkshow.data.model;

import java.io.Serializable;

public class WavListItem implements Serializable ,Comparable{
    public String getUrl() {
        return URL;
    }

    public void setUrl(String url) {
        this.URL = url;
    }

    public float getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(float beginTime) {
        this.beginTime = beginTime;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getEndTime() {
        return endTime;
    }

    public void setEndTime(float endTime) {
        this.endTime = endTime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public static final String BEGIN_TIME = "begin_time";
    public static final String DURATION = "duration";
    public static final String END_TIME = "endTime";
    public static final String INDEX = "index";

    String URL ;
    float beginTime ;
    float duration ;
    float endTime ;
    int index ;




    @Override
    public int compareTo(Object o) {
        if (index>((WavListItem)o).index) {
            return 1;
        }else {
            return -1 ;
        }
    }
}
