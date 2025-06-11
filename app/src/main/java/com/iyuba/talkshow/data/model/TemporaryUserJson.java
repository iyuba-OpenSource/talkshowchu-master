package com.iyuba.talkshow.data.model;

/**
 * Created by iyuba on 2018/1/8.
 */

public class TemporaryUserJson {
    /**
     * uid : 1000000002
     * message : success
     * result : 1
     */

    private int uid;
    private String message;
    private int result;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "TemporaryUserJson{" +
                "uid=" + uid +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
