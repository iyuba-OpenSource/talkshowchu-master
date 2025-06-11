package com.iyuba.wordtest.ui.listen.bean;

/**
 * 单词听写展示的模型
 */
public class WordListenShowBean {

    private int book_id;//课程id
    private int unit_id;//单元id
    private int position;//位置
    private String uid;//用户id

    private String word;//单词
    private String porn;//音标
    private String def;//释义
    private String audio;//音频

    private String spell;//拼写的内容
    private int status;//是否正确(1-正确，0-错误)
    private int error_count;//错误次数
    private String update_time;//更新时间

    public WordListenShowBean(int book_id, int unit_id, int position, String uid, String word, String porn, String def, String audio, String spell, int status, int error_count, String update_time) {
        this.book_id = book_id;
        this.unit_id = unit_id;
        this.position = position;
        this.uid = uid;
        this.word = word;
        this.porn = porn;
        this.def = def;
        this.audio = audio;
        this.spell = spell;
        this.status = status;
        this.error_count = error_count;
        this.update_time = update_time;
    }

    public int getBook_id() {
        return book_id;
    }

    public int getUnit_id() {
        return unit_id;
    }

    public int getPosition() {
        return position;
    }

    public String getUid() {
        return uid;
    }

    public String getWord() {
        return word;
    }

    public String getPorn() {
        return porn;
    }

    public String getDef() {
        return def;
    }

    public String getAudio() {
        return audio;
    }

    public String getSpell() {
        return spell;
    }

    public int getStatus() {
        return status;
    }

    public int getError_count() {
        return error_count;
    }

    public String getUpdate_time() {
        return update_time;
    }
}