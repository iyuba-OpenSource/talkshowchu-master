package com.iyuba.talkshow.lil.help_fix.data.event;

/**
 * @title: 刷新数据
 * @date: 2023/4/27 15:35
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RefreshDataEvent {

    private String type;//刷新的数据类型
    private String msg;//展示的信息

    public RefreshDataEvent(String type) {
        this.type = type;
    }

    public RefreshDataEvent(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }
}
