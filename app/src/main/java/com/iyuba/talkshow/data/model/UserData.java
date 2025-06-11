package com.iyuba.talkshow.data.model;

/**
 * Created by iyuba on 2018/1/8.
 */

public class UserData {
    public long expireTime;
    public int result;
    public String follower = "";// 粉丝
    public String icoins = "";
    public String posts;
    public String credits;
    public String sharings;
    public String blogs;
    public int amount;
    public String username = "";// 用户名
    public String nickname = "";// 昵称
    public String bio;
    public String isteacher;
    public String allThumbUp;
    public String following = "";// 关注
    public String distance = "";
    public String friends;
    public String shengwang;
    public int money;
    public String gender = "";// 性别
    public String doings = "";// 发布的心情数
    public String text = "";// 最近的心情签名
    public String vipStatus = "";
    public String relation = "";// 与当前用户关系 百位我是否关注他十位特别关注 个位他是否关注我
    public String middle_url;
    public String contribute;
    public String message;
    public String email;
    public String views = "";// 访客数
    public String albums;
    public String mobile;

    //会使用到
    public String uid = "";
    public String iyubi = "";
    public String notification = "";
    public int studytime;
    public String position = "";
    public String deadline = "";



    @Override
    public String toString() {
        return "UserInfo{" +
                "expireTime=" + expireTime +
                ", result=" + result +
                ", follower='" + follower + '\'' +
                ", icoins='" + icoins + '\'' +
                ", posts='" + posts + '\'' +
                ", credits='" + credits + '\'' +
                ", sharings='" + sharings + '\'' +
                ", blogs='" + blogs + '\'' +
                ", amount=" + amount +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", following='" + following + '\'' +
                ", distance='" + distance + '\'' +
                ", friends='" + friends + '\'' +
                ", shengwang='" + shengwang + '\'' +
                ", money=" + money +
                ", gender='" + gender + '\'' +
                ", doings='" + doings + '\'' +
                ", text='" + text + '\'' +
                ", vipStatus='" + vipStatus + '\'' +
                ", relation='" + relation + '\'' +
                ", middle_url='" + middle_url + '\'' +
                ", contribute='" + contribute + '\'' +
                ", message='" + message + '\'' +
                ", email='" + email + '\'' +
                ", views='" + views + '\'' +
                ", albums='" + albums + '\'' +
                ", mobile='" + mobile + '\'' +
                ", uid='" + uid + '\'' +
                ", iyubi='" + iyubi + '\'' +
                ", notification='" + notification + '\'' +
                ", studytime=" + studytime +
                ", position='" + position + '\'' +
                ", deadline='" + deadline + '\'' +
                '}';
    }

    public UserData() {
        expireTime = 0;
        icoins = "0";
        uid = "0";
        username = "";
        doings = "0";
        views = "0";
        gender = "0";
        follower = "0";// 粉丝
        relation = "0";// 与当前用户关系 百位我是否关注他十位特别关注 个位他是否关注我
        following = "0";// 关注
        iyubi = "0";
        vipStatus = "0";
        notification = "0";
        studytime = 0;
        position = "100000";
        deadline = "";
        amount = 0;
    }
}
