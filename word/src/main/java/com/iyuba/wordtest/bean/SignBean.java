package com.iyuba.wordtest.bean;

public class SignBean {

    private String result;
    private String addcredit;//增加的积分数
    private String totalcredit;//当前总积分数
    private String days;//连续打卡天数
    private String money; // 此时获得的money数量,单位是分

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }



    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAddcredit() {
        return addcredit;
    }

    public void setAddcredit(String addcredit) {
        this.addcredit = addcredit;
    }

    public String getTotalcredit() {
        return totalcredit;
    }

    public void setTotalcredit(String totalcredit) {
        this.totalcredit = totalcredit;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

//1.分享盆友圈打卡回到APP内部调用srid = 81 的接口,接口返回信息
//    {
//        addcredit = 3;//此次获得积分
//        days = 2;//连续打卡天数
//        money = 3;//此次得到红包的数量 ,单位是分
//        result = 200;//200算成功,
//        totalcredit = 96;//当money>0返回的是总金额,money=0返回的是总积分;
//    }
//
//    提醒用户:
//
//            if(money>0)
//    {
//
//        打卡成功，连续打卡 days 天,获得addcredit积分, 获得 money * 0.01 元红包，总计：totalcredit * 0.01 元，满10元[爱语课吧]微信公众号提现
//
//
//    }else{
//
//        打卡成功，连续打卡days 天,获得 addcredit积分，总积分: totalcredit;
//

}
