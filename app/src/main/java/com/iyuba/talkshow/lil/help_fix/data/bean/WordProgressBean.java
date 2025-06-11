package com.iyuba.talkshow.lil.help_fix.data.bean;

import androidx.room.Ignore;

import java.io.Serializable;

/**
 * @title: 单词进度显示
 * @date: 2023/5/11 15:52
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordProgressBean implements Serializable {
    private static final long serialVersionUID = -2924349565472864620L;

    @Ignore
    private String types;
    public String bookId;
    public String id;//全四册是voaId,青少版是unitId
    public String voaId;//章节id

    public String lessonName;//课程名称

    public int size;//总数量
    @Ignore
    private int right;//正确数量
    @Ignore
    private boolean pass;//是否通过

    public WordProgressBean() {
    }

    @Ignore
    public WordProgressBean(String types, String bookId, String id, String voaId, String lessonName, int size, int right, boolean pass) {
        this.types = types;
        this.bookId = bookId;
        this.id = id;
        this.voaId = voaId;
        this.lessonName = lessonName;
        this.size = size;
        this.right = right;
        this.pass = pass;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVoaId() {
        return voaId;
    }

    public void setVoaId(String voaId) {
        this.voaId = voaId;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }
}
