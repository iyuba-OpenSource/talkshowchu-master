package com.iyuba.talkshow.lil.user.bean;

/**
 * @title: 本地用户信息数据
 * @date: 2023/11/3 10:19
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class LocalUserBean {

    private int userId;
    private String userName;
    private String nickName;
    private String userPic;
    private String sign;

    private String vipStatus;
    private long vipTime;

    private int jiFen;
    private double money;
    private int iyuIcon;

    //其他的一些参数
    private String userInfo;

    public LocalUserBean() {
        this.userId = 0;
        this.userName = "";
        this.nickName = "";
        this.userPic = "";
        this.sign = "";
        this.vipStatus = "0";
        this.vipTime = 0;
        this.jiFen = 0;
        this.money = 0.0f;
        this.iyuIcon = 0;
        this.userInfo = null;
    }

    public LocalUserBean(int userId, String userName, String nickName, String userPic, String sign, String vipStatus, long vipTime, int jiFen, double money, int iyuIcon, String userInfo) {
        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
        this.userPic = userPic;
        this.sign = sign;
        this.vipStatus = vipStatus;
        this.vipTime = vipTime;
        this.jiFen = jiFen;
        this.money = money;
        this.iyuIcon = iyuIcon;
        this.userInfo = userInfo;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getNickName() {
        return nickName;
    }

    public String getUserPic() {
        return userPic;
    }

    public String getSign() {
        return sign;
    }

    public String getVipStatus() {
        return vipStatus;
    }

    public long getVipTime() {
        return vipTime;
    }

    public int getJiFen() {
        return jiFen;
    }

    public double getMoney() {
        return money;
    }

    public int getIyuIcon() {
        return iyuIcon;
    }

    public String getUserInfo() {
        return userInfo;
    }
}
