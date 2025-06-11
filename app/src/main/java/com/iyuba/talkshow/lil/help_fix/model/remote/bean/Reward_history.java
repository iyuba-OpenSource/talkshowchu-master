package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

/**
 * @title: 奖励的历史记录
 * @date: 2023/8/23 09:17
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Reward_history {

    /**
     * voaid : 1003
     * score : 13
     * time : 2023-08-22 18:42:04.0
     * type : 跟读合成发布
     * srid : 103
     */

    private String voaid;
    private String score;
    private String time;
    private String type;
    private String srid;

    public String getVoaid() {
        return voaid;
    }

    public void setVoaid(String voaid) {
        this.voaid = voaid;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSrid() {
        return srid;
    }

    public void setSrid(String srid) {
        this.srid = srid;
    }
}
