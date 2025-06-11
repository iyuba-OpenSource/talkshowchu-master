package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @title: 评测-排行-点赞
 * @date: 2023/5/25 18:19
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Eval_rank_agree implements Serializable {


    /**
     * ResultCode : 001
     * Message : OK
     */

    private String ResultCode;
    private String Message;

    public String getResultCode() {
        return ResultCode;
    }

    public String getMessage() {
        return Message;
    }
}
