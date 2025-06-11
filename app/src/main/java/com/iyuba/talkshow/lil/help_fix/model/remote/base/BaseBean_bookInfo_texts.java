package com.iyuba.talkshow.lil.help_fix.model.remote.base;

/**
 * @title:
 * @date: 2023/5/8 10:44
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class BaseBean_bookInfo_texts<F,T> {

    private int result;
    private String message;
    private F bookInfo;
    private T texts;

    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public F getBookInfo() {
        return bookInfo;
    }

    public T getTexts() {
        return texts;
    }
}
