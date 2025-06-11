package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

public class Pay_alipay {

    /**
     * alipayTradeStr : alipay_sdk=alipay-sdk-java-4.10.184.ALL&app_id=2021002111635676&biz_content=%7B%22body%22%3A%221%E4%B8%AA%E6%9C%88%E6%9C%AC%E5%BA%94%E7%94%A8VIP%22%2C%22out_trade_no%22%3A%2220240124100232_12071118_260%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%221%E4%B8%AA%E6%9C%88%E6%9C%AC%E5%BA%94%E7%94%A8VIP%E8%B4%AD%E4%B9%B0%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%220.01%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fvip.iyuba.cn%2FnotifyAliNewUrl.jsp&sign=HgTOQHXHzRvZQ9DYeXOhcmTee6aGo2ddIJgUMFOXscAyls0Y2HRhCgZuMPRAjDebhXOjaSJk8QHYznfBNIUFRlpPd5%2FNVJzojQ0APje1bjwMwTojdSC1MuDl1WhXfto4wTs8UPdO5hyvg5Ux7USI8AHJ2YHcgQCLKTLyz1Po0twFNquSEakCRRPJFbf%2FMb%2B2jVMlSKQJWyyqLtsQPPC4QtMNIH7w%2FT%2F3WGO6lv0f41Mgbydv6XfPh5%2FmROmovFniZF%2BHTzCamJ5nn%2BUCs0Qz8ZrkKHoFoE97di7t%2Fxa01LAUBajGKiiYh4ROVzUrWyyWrkLxA3AAIKXxRuH0QHgWZQ%3D%3D&sign_type=RSA2&timestamp=2024-01-24+10%3A02%3A32&version=1.0
     * result : 200
     * message : Success
     */

    private String alipayTradeStr;
    private String result;
    private String message;

    public String getAlipayTradeStr() {
        return alipayTradeStr;
    }

    public void setAlipayTradeStr(String alipayTradeStr) {
        this.alipayTradeStr = alipayTradeStr;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
