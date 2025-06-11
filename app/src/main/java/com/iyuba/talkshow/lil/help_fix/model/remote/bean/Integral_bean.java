package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @title:
 * @date: 2023/6/8 18:51
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Integral_bean implements Serializable {


    /**
     * result : 201
     * message : 本篇分享成功！分享新文章可获得积分呦！
     */

    private String result;
    private String message;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
