package com.iyuba.talkshow.lil.help_fix.data.bean;

import java.io.Serializable;

/**
 * @title:
 * @date: 2023/5/25 15:26
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class EvalRankDetailBean implements Serializable {

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

    //本地数据
    private String types;
    private String voaId;

    private String sentence;//句子-英文

    public EvalRankDetailBean(int paraid, int score, int shuoshuotype, int againstCount, int agreeCount, int topicId, String shuoShuo, int id, int idIndex, String createDate, String types, String voaId) {
        this.paraid = paraid;
        this.score = score;
        this.shuoshuotype = shuoshuotype;
        this.againstCount = againstCount;
        this.agreeCount = agreeCount;
        TopicId = topicId;
        ShuoShuo = shuoShuo;
        this.id = id;
        this.idIndex = idIndex;
        CreateDate = createDate;
        this.types = types;
        this.voaId = voaId;
    }

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

    public String getTypes() {
        return types;
    }

    public String getVoaId() {
        return voaId;
    }

    /********这个根据数据自己补充**********/
    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
}
