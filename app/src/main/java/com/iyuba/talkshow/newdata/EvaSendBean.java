package com.iyuba.talkshow.newdata;

/**
 * Created by iyuba on 2019/1/21.
 */

public class EvaSendBean {

    /**
     * ResultCode : 501
     * AddScore : 0
     * ShuoshuoId : 8381294
     * Message : OK
     */

    private String ResultCode;
    private int AddScore;
    private int ShuoshuoId;
    private String Message;

    private String reward;
    private String rewardMessage;

    public String getResultCode() {
        return ResultCode;
    }

    public void setResultCode(String ResultCode) {
        this.ResultCode = ResultCode;
    }

    public int getAddScore() {
        return AddScore;
    }

    public void setAddScore(int AddScore) {
        this.AddScore = AddScore;
    }

    public int getShuoshuoId() {
        return ShuoshuoId;
    }

    public void setShuoshuoId(int ShuoshuoId) {
        this.ShuoshuoId = ShuoshuoId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    @Override
    public String toString() {
        return "EvaSendBean{" +
                "ResultCode='" + ResultCode + '\'' +
                ", AddScore=" + AddScore +
                ", ShuoshuoId=" + ShuoshuoId +
                ", Message='" + Message + '\'' +
                ", reward='" + reward + '\'' +
                ", rewardMessage='" + rewardMessage + '\'' +
                '}';
    }
}
