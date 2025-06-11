package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @title: 口语秀的排行榜信息
 * @date: 2023/6/13 15:44
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Dubbing_rank implements Serializable {


    /**
     * ImgSrc : http://static1.iyuba.cn/uc_server/head/2023/1/10/11/32/57/aaf6706c-ec71-44c0-a17e-a063535d3ad4-m.jpg
     * image :
     * backId : 0
     * backList :
     * UserName : aiyuba_lil
     * ShuoShuoType : 3
     * ShuoShuo : kouyu/2023/6/9/1686295177302.mp3
     * TopicCategory : primaryenglish
     * title :
     * CreateDate : 2023-06-09
     * score : 76
     * paraid : 0
     * topicid : 313026
     * againstCount : 0
     * videoUrl : video/voa/kouyu/2023/6/9/1686295177302.mp4
     * Userid : 12071118
     * agreeCount : 0
     * id : 19851429
     * idIndex : 0
     * vip : 0
     */

    private String ImgSrc;
    private String image;
    private int backId;
    private String backList;
    private String UserName;
    private String ShuoShuoType;
    private String ShuoShuo;
    private String TopicCategory;
    private String title;
    private String CreateDate;
    private String score;
    private String paraid;
    private String topicid;
    private String againstCount;
    private String videoUrl;
    private String Userid;
    private String agreeCount;
    private String id;
    private String idIndex;
    private String vip;

    public String getImgSrc() {
        return ImgSrc;
    }

    public String getImage() {
        return image;
    }

    public int getBackId() {
        return backId;
    }

    public String getBackList() {
        return backList;
    }

    public String getUserName() {
        return UserName;
    }

    public String getShuoShuoType() {
        return ShuoShuoType;
    }

    public String getShuoShuo() {
        return ShuoShuo;
    }

    public String getTopicCategory() {
        return TopicCategory;
    }

    public String getTitle() {
        return title;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public String getScore() {
        return score;
    }

    public String getParaid() {
        return paraid;
    }

    public String getTopicid() {
        return topicid;
    }

    public String getAgainstCount() {
        return againstCount;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getUserid() {
        return Userid;
    }

    public String getAgreeCount() {
        return agreeCount;
    }

    public String getId() {
        return id;
    }

    public String getIdIndex() {
        return idIndex;
    }

    public String getVip() {
        return vip;
    }
}
