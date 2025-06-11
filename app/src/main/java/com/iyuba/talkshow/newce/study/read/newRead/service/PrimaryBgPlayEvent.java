package com.iyuba.talkshow.newce.study.read.newRead.service;

/**
 * @title: 小学-后台播放事件
 * @date: 2023/10/27 13:35
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class PrimaryBgPlayEvent {

    /******************类型******************/
    public static final String event_audio_prepareFinish = "primary_audio_prepareFinish";//音频-加载完成
    public static final String event_audio_completeFinish = "primary_audio_completeFinish";//音频-播放完成
    public static final String event_audio_play = "primary_audio_play";//音频-播放
    public static final String event_audio_pause = "primary_audio_pause";//音频-暂停
    public static final String event_audio_stop = "primary_audio_stop";//音频-停止
    public static final String event_audio_switch = "primary_switch";//音频-切换
    public static final String event_control_play = "primary_control_play";//控制栏-播放
    public static final String event_control_pause = "primary_control_pause";//控制栏-暂停
    public static final String event_control_hide = "primary_control_hide";//控制栏-隐藏
    public static final String event_data_refresh = "primary_data_refresh";//数据-刷新

    /*******************事件*****************/
    private String showType;
    private int showPosition = 0;

    public PrimaryBgPlayEvent(String showType) {
        this.showType = showType;
    }

    public PrimaryBgPlayEvent(String showType, int showPosition) {
        this.showType = showType;
        this.showPosition = showPosition;
    }

    public String getShowType() {
        return showType;
    }

    public int getShowPosition() {
        return showPosition;
    }
}
