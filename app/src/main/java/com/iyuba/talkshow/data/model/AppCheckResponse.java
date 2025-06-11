package com.iyuba.talkshow.data.model;

public class AppCheckResponse {

    /**
     * result : 1
     */

    private String result;//1-审核中，0-未在审核状态

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
