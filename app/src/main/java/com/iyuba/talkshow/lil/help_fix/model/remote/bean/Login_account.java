package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @desction: 登录-账号
 * @date: 2023/4/20 00:28
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class Login_account implements Serializable {
    private static final long serialVersionUID = 4214646134588878239L;


    /**
     * Amount : 210
     * mobile :
     * message : success
     * result : 101
     * uid : 12071118
     * isteacher : 0
     * expireTime : 1683539740
     * money : 14
     * credits : 0
     * jiFen : 0
     * nickname : 来来回回改bug
     * vipStatus : 24
     * imgSrc : http://static1.iyuba.cn/uc_server/head/2023/1/10/11/32/57/aaf6706c-ec71-44c0-a17e-a063535d3ad4-b.jpg
     * email :
     * username : aiyuba_lil
     */

    private String Amount;
    private String mobile;
    private String message;
    private String result;
    private int uid;
    private String isteacher;
    private long expireTime;
    private String money;
    private int credits;
    private int jiFen;
    private String nickname;
    private String vipStatus;
    private String imgSrc;
    private String email;
    private String username;

    public String getAmount() {
        return Amount;
    }

    public String getMobile() {
        return mobile;
    }

    public String getMessage() {
        return message;
    }

    public String getResult() {
        return result;
    }

    public int getUid() {
        return uid;
    }

    public String getIsteacher() {
        return isteacher;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public String getMoney() {
        return money;
    }

    public int getCredits() {
        return credits;
    }

    public int getJiFen() {
        return jiFen;
    }

    public String getNickname() {
        return nickname;
    }

    public String getVipStatus() {
        return vipStatus;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
