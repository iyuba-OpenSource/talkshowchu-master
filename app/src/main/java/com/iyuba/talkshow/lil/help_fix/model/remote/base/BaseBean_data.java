package com.iyuba.talkshow.lil.help_fix.model.remote.base;

/**
 * @desction: 基础类型数据
 * @date: 2023/2/24 15:53
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class BaseBean_data<T> {

    private String result;
    private String message;
    private int total;
    private int size;
    private T data;

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public int getTotal() {
        return total;
    }

    public int getSize() {
        return size;
    }

    public T getData() {
        return data;
    }
}
