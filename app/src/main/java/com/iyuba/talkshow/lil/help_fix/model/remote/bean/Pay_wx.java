package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

public class Pay_wx {

    /**
     * appid : wx6f3650b6c6690eaa
     * appsecret : ca26f79b0281cdae0371e8726ba2b59e
     * sign : 8BE1D441F9ECCACAD1AC862BC8122861
     * prepayid : wx241009056513908e40f123ae26c92f0000
     * mch_id : 1377693702
     * noncestr : 92b70a527191ca64ca2df1cc32142646
     * retmsg : OK
     * mch_key : l9njN3hzXb7mXBUdrlsMSzveLf5wQUn7
     * timestamp : 1706062145
     * retcode : 0
     */

    private String appid;
    private String appsecret;
    private String sign;
    private String prepayid;
    private String mch_id;
    private String noncestr;
    private String retmsg;
    private String mch_key;
    private String timestamp;
    private Integer retcode;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    public String getMch_key() {
        return mch_key;
    }

    public void setMch_key(String mch_key) {
        this.mch_key = mch_key;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getRetcode() {
        return retcode;
    }

    public void setRetcode(Integer retcode) {
        this.retcode = retcode;
    }
}
