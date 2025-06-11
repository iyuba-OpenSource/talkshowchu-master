package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @title: 章节详情-中小学英语
 * @date: 2023/5/22 16:33
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Junior_chapter_detail implements Serializable {


    /**
     * ImgPath :
     * EndTiming : 4.2
     * ParaId : 1
     * IdIndex : 1
     * sentence_cn :  对不起！
     * ImgWords :
     * Start_x : 0
     * End_y : 0
     * Timing : 0.6
     * End_x : 0
     * Sentence : lesson 1 Excuse me!
     * Start_y : 0
     */

    private String ImgPath;
    private double EndTiming;
    private String ParaId;
    private String IdIndex;
    private String sentence_cn;
    private String ImgWords;
    private String Start_x;
    private String End_y;
    private double Timing;
    private String End_x;
    private String Sentence;
    private String Start_y;

    public String getImgPath() {
        return ImgPath;
    }

    public double getEndTiming() {
        return EndTiming;
    }

    public String getParaId() {
        return ParaId;
    }

    public String getIdIndex() {
        return IdIndex;
    }

    public String getSentence_cn() {
        return sentence_cn;
    }

    public String getImgWords() {
        return ImgWords;
    }

    public String getStart_x() {
        return Start_x;
    }

    public String getEnd_y() {
        return End_y;
    }

    public double getTiming() {
        return Timing;
    }

    public String getEnd_x() {
        return End_x;
    }

    public String getSentence() {
        return Sentence;
    }

    public String getStart_y() {
        return Start_y;
    }
}
