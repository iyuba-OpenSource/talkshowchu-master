package com.iyuba.talkshow.lil.help_fix.model.remote.base;

/**
 * @title: 用于小说-章节的基础解析
 * @date: 2023/4/28 16:00
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class BaseBean_novelChapter<F,T>{

    private int result;
    private String message;
    private F bookInfo;
    private T chapterInfo;

    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public F getBookInfo() {
        return bookInfo;
    }

    public T getChapterInfo() {
        return chapterInfo;
    }
}
