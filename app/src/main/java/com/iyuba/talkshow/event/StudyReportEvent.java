package com.iyuba.talkshow.event;

/**
 * Created by carl shen on 2021/8/28
 * New Primary English, new study experience.
 */
public class StudyReportEvent {
    public int ReportState = 0;

    //显示奖励信息
    public String rewardPrice = "0";
    //界面类型
    public String pageType;

    public StudyReportEvent(int reportState, String pageType) {
        ReportState = reportState;
        this.pageType = pageType;
    }

    public StudyReportEvent(int reportState, String rewardPrice, String pageType) {
        ReportState = reportState;
        this.rewardPrice = rewardPrice;
        this.pageType = pageType;
    }
}
