package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @title: 中小学-单词数据
 * @date: 2023/5/11 18:55
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Junior_word implements Serializable {
    private static final long serialVersionUID = 5395210287164229295L;


    /**
     * def : n.书，书本
     * updateTime : 2021-08-11 18:14:29.0
     * book_id : 205
     * version : 26
     * examples : 1
     * videoUrl : http://static2.iyuba.cn/video/313/313002/313002_3_1.mp4
     * pron : bʊk
     * voa_id : 313002
     * idindex : 3
     * audio : http://res.iciba.com/resource/amp3/oxford/0/7b/1e/7b1e114ff5a2b3e6d06064d14f9d2f7a.mp3
     * position : 1
     * Sentence_cn : 我有一本书
     * pic_url : 205/1/1.jpg
     * unit_id : 1
     * word : book
     * Sentence : I have a book.
     * Sentence_audio : http://staticvip.iyuba.cn/sounds/voa/sentence/202002/313002/313002_3_1.wav
     */

    private String def;
    private String updateTime;
    private String book_id;
    private String version;
    private String examples;
    private String videoUrl;
    private String pron;
    private String voa_id;
    private String idindex;
    private String audio;
    private String position;
    private String Sentence_cn;
    private String pic_url;
    private String unit_id;
    private String word;
    private String Sentence;
    private String Sentence_audio;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getDef() {
        return def;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getBook_id() {
        return book_id;
    }

    public String getVersion() {
        return version;
    }

    public String getExamples() {
        return examples;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getPron() {
        return pron;
    }

    public String getVoa_id() {
        return voa_id;
    }

    public String getIdindex() {
        return idindex;
    }

    public String getAudio() {
        return audio;
    }

    public String getPosition() {
        return position;
    }

    public String getSentence_cn() {
        return Sentence_cn;
    }

    public String getPic_url() {
        return pic_url;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public String getWord() {
        return word;
    }

    public String getSentence() {
        return Sentence;
    }

    public String getSentence_audio() {
        return Sentence_audio;
    }
}
