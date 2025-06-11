package com.iyuba.talkshow.lil.help_fix.data.bean;

import java.io.Serializable;

/**
 * @title: 单词显示
 * @date: 2023/5/12 11:36
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordBean implements Serializable {
    private static final long serialVersionUID = -5049587622247491342L;

    private String types;//书籍的类型
    private String bookId;//书籍id
    private String voaId;//所在的章节id

    //格外的id，新概念全四册是voaId，新概念青少版是unitId
    private String id;

    private String word;//单词
    private String pron;//音标
    private String def;//释义
    private String position;//单词所在的位置(第几个单词)

    private String sentence;//句子
    private String sentenceCn;//句子释义

    private String videoPic;//视频的图片
    private String videoUrl;//视频链接
    private String wordAudioUrl;//单词音频
    private String sentenceAudioUrl;//句子音频

    public WordBean(String types, String bookId, String voaId, String id, String word, String pron, String def, String position, String sentence, String sentenceCn, String videoPic, String videoUrl, String wordAudioUrl, String sentenceAudioUrl) {
        this.types = types;
        this.bookId = bookId;
        this.voaId = voaId;
        this.id = id;
        this.word = word;
        this.pron = pron;
        this.def = def;
        this.position = position;
        this.sentence = sentence;
        this.sentenceCn = sentenceCn;
        this.videoPic = videoPic;
        this.videoUrl = videoUrl;
        this.wordAudioUrl = wordAudioUrl;
        this.sentenceAudioUrl = sentenceAudioUrl;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTypes() {
        return types;
    }

    public String getBookId() {
        return bookId;
    }

    public String getVoaId() {
        return voaId;
    }

    public String getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getPron() {
        return pron;
    }

    public String getDef() {
        return def;
    }

    public String getPosition() {
        return position;
    }

    public String getSentence() {
        return sentence;
    }

    public String getSentenceCn() {
        return sentenceCn;
    }

    public String getVideoPic() {
        return videoPic;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getWordAudioUrl() {
        return wordAudioUrl;
    }

    public String getSentenceAudioUrl() {
        return sentenceAudioUrl;
    }
}
