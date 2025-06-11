package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @desction: 合成的回调
 * @date: 2023/3/3 10:51
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class Marge_eval implements Serializable {

    /**
     * result : 1
     * message : merge success
     * URL : wav6/202303/concept/20230303/16778117799060814.mp3
     */

    private String result;
    private String message;
    @SerializedName("URL")
    private String url;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
