package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @desction: 用户信息
 * @date: 2023/4/20 13:25
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class User_info implements Serializable {
    private static final long serialVersionUID = -5987752621837256789L;


    /**
     * albums : 0
     * gender : 1
     * distance :
     * blogs : 0
     * middle_url : head/2023/1/10/11/32/57/aaf6706c-ec71-44c0-a17e-a063535d3ad4-m.jpg
     * contribute : 0
     * shengwang : 0
     * bio : bug创造者
     * posts : 0
     * relation : 0
     * result : 201
     * isteacher : 0
     * credits : 330
     * nickname : 来来回回改bug
     * text : test
     * email :
     * views : 2632
     * amount : 210
     * follower : 0
     * mobile :
     * allThumbUp : 12
     * icoins : 330
     * message :
     * friends : 0
     * doings : 12
     * expireTime : 1683539740
     * money : 14
     * following : 0
     * sharings : 0
     * vipStatus : 24
     * username : aiyuba_lil
     */

    private String albums;
    private String gender;
    private String distance;
    private String blogs;
    private String middle_url;//
    private String contribute;
    private String shengwang;
    private String bio;//签名
    private String posts;
    private int relation;
    private int result;
    private String isteacher;
    private String credits;//积分
    private String nickname;//昵称
    private String text;
    private String email;//邮箱
    private String views;
    private int amount;//爱语币
    private int follower;
    private String mobile;//手机号
    private String allThumbUp;
    private String icoins;
    private String message;
    private String friends;
    private String doings;//获得赞的数量
    private long expireTime;//vip持续时间，需要*1000
    private int money;//红包数量，单位：角
    private int following;
    private String sharings;
    private String vipStatus;//vip状态
    private String username;//用户名

    public String getAlbums() {
        return albums;
    }

    public String getGender() {
        return gender;
    }

    public String getDistance() {
        return distance;
    }

    public String getBlogs() {
        return blogs;
    }

    public String getMiddle_url() {
        return middle_url;
    }

    public String getContribute() {
        return contribute;
    }

    public String getShengwang() {
        return shengwang;
    }

    public String getBio() {
        return bio;
    }

    public String getPosts() {
        return posts;
    }

    public int getRelation() {
        return relation;
    }

    public int getResult() {
        return result;
    }

    public String getIsteacher() {
        return isteacher;
    }

    public String getCredits() {
        return credits;
    }

    public String getNickname() {
        return nickname;
    }

    public String getText() {
        return text;
    }

    public String getEmail() {
        return email;
    }

    public String getViews() {
        return views;
    }

    public int getAmount() {
        return amount;
    }

    public int getFollower() {
        return follower;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAllThumbUp() {
        return allThumbUp;
    }

    public String getIcoins() {
        return icoins;
    }

    public String getMessage() {
        return message;
    }

    public String getFriends() {
        return friends;
    }

    public String getDoings() {
        return doings;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public int getMoney() {
        return money;
    }

    public int getFollowing() {
        return following;
    }

    public String getSharings() {
        return sharings;
    }

    public String getVipStatus() {
        return vipStatus;
    }

    public String getUsername() {
        return username;
    }
}
