package com.iyuba.talkshow.newce.study.read.newRead.ui;

/**
 * @title: 新界面-听力学习报告回调
 * @date: 2023/12/11 14:56
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewReadListenEvent {

    public static final String type_showReport = "showReport";//展示学习报告
    public static final String type_closeReport = "closeReport";//关闭学习报告
    public static final String type_closeReportTimer = "closeReportTimer";//关闭学习报告定时器

    private String type;//类型
    private String reward;//奖励信息

    public NewReadListenEvent(String type) {
        this.type = type;
    }

    public NewReadListenEvent(String type, String reward) {
        this.type = type;
        this.reward = reward;
    }

    public String getType() {
        return type;
    }

    public String getReward() {
        return reward;
    }
}
