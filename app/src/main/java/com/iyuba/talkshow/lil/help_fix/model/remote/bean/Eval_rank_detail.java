package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @title: 评测-排行-详情
 * @date: 2023/5/25 14:58
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Eval_rank_detail implements Serializable {

    /**
     * paraid : 1
     * score : 97
     * shuoshuotype : 2
     * againstCount : 0
     * agreeCount : 0
     * TopicId : 2001
     * ShuoShuo : wav8/202304/concept/20230402/16804033011800204.mp3
     * id : 19519058
     * idIndex : 14
     * CreateDate : 2023-04-02 10:41:52
     */

    private int paraid;
    private int score;
    private int shuoshuotype;
    private int againstCount;
    private int agreeCount;
    private int TopicId;
    private String ShuoShuo;
    private int id;
    private int idIndex;
    private String CreateDate;

    public int getParaid() {
        return paraid;
    }

    public int getScore() {
        return score;
    }

    public int getShuoshuotype() {
        return shuoshuotype;
    }

    public int getAgainstCount() {
        return againstCount;
    }

    public int getAgreeCount() {
        return agreeCount;
    }

    public int getTopicId() {
        return TopicId;
    }

    public String getShuoShuo() {
        return ShuoShuo;
    }

    public int getId() {
        return id;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public String getCreateDate() {
        return CreateDate;
    }
}
