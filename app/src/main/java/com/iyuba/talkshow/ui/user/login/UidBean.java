package com.iyuba.talkshow.ui.user.login;

/**
 * 小程序登陆-根据token获取uid
 */
public class UidBean {

    /**
     * result : 201
     * uid : 0
     * message : Dont have user info, please transfer it
     */

    private int result;
    private int uid;
    private String message;

    public int getResult() {
        return result;
    }

    public int getUid() {
        return uid;
    }

    public String getMessage() {
        return message;
    }
}
