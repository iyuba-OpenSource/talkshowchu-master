package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

/**
 * 广告-定时提交
 */
public class Ad_clock_submit {

    /**
     * result : 200
     * message : insert success
     */

    private int result;
    private String message;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
