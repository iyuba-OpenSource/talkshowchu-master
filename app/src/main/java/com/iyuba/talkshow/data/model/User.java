package com.iyuba.talkshow.data.model;

import java.io.Serializable;

public class User implements Serializable {
    private int uid;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String password;
    private String imgSrc;
    private int amount;
    private int vipStatus;
    private int integral;
    private String expireTime;
    private long lastLoginTime;
    private String money ;

    //增加控制微课是否显示的数据
    private boolean mocShow;

    public boolean isMocShow() {
        return mocShow;
    }

    public void setMocShow(boolean mocShow) {
        this.mocShow = mocShow;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(int vipStatus) {
        this.vipStatus = vipStatus;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", imgSrc='" + imgSrc + '\'' +
                ", amount=" + amount +
                ", vipStatus=" + vipStatus +
                ", integral=" + integral +
                ", expireTime='" + expireTime + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                ", money=" + money +
                '}';
    }
}
