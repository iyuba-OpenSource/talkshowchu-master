package com.iyuba.talkshow.data.model.result;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Administrator on 2016/12/16/016.
 */

@Root(name = "response", strict = false)
public class AppUpdateResponse {
    @Element(name = "status", required = false)
    private String status;
    @Element(name = "msg", required = false)
    private String msg;
    @Element(name = "data", required = false)
    private AppUpdateDataBody data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public AppUpdateDataBody getData() {
        return data;
    }

    public void setData(AppUpdateDataBody data) {
        this.data = data;
    }
}
