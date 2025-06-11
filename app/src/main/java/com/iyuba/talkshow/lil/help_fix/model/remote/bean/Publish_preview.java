package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @desction: 发布的回调
 * @date: 2023/3/3 11:06
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class Publish_preview implements Serializable {


    /**
     * AddScore : 0
     * Message : OK
     * ShuoShuoId : 19851450
     * ResultCode : 200
     */

    private int AddScore;
    private String Message;
    private int ShuoShuoId;
    private int ResultCode;

    public int getAddScore() {
        return AddScore;
    }

    public void setAddScore(int AddScore) {
        this.AddScore = AddScore;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public int getShuoShuoId() {
        return ShuoShuoId;
    }

    public void setShuoShuoId(int ShuoShuoId) {
        this.ShuoShuoId = ShuoShuoId;
    }

    public int getResultCode() {
        return ResultCode;
    }

    public void setResultCode(int ResultCode) {
        this.ResultCode = ResultCode;
    }
}
