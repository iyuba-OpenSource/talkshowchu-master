package com.iyuba.talkshow.ui.sign.bean;

import java.io.Serializable;

public class SignRespBean implements Serializable {

    private String result;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message ;
//    private String  addcredit;//增加的积分数
//    private String  totalcredit;//当前总积分数
//    private String  days;//连续打卡天数

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
//
//    public String getAddcredit() {
//        return addcredit;
//    }
//
//    public void setAddcredit(String addcredit) {
//        this.addcredit = addcredit;
//    }
//
//    public String getTotalcredit() {
//        return totalcredit;
//    }
//
//    public void setTotalcredit(String totalcredit) {
//        this.totalcredit = totalcredit;
//    }
//
//    public String getDays() {
//        return days;
//    }
//
//    public void setDays(String days) {
//        this.days = days;
//    }
}
