package com.iyuba.talkshow.data.model;

import androidx.annotation.Keep;

@Keep
public class UploadRecordResult {

    /**
     * result : 1
     * message : Submit Twice! appId:219!
     */

    private String result;
    private String jifen;
    private String message;

    private String reward;
    private String rewardMessage;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getJifen() {
        return jifen;
    }

    public void setJifen(String jifen) {
        this.jifen = jifen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getRewardMessage() {
        return rewardMessage;
    }

    public void setRewardMessage(String rewardMessage) {
        this.rewardMessage = rewardMessage;
    }
}
