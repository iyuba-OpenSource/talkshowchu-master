package com.iyuba.talkshow.lil.help_fix.model.remote.base;

/**
 * @title: 用于小说-章节-详情的基础解析
 * @date: 2023/4/28 16:11
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class BaseBean_novelChapterDetail<T> {

    private int result;
    private String message;
    private T texts;

    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public T getTexts() {
        return texts;
    }
}
