package com.iyuba.talkshow.event;

/**
 * @desction: 微信登录回调
 * @date: 2023/3/14 10:22
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class WxLoginEvent {

    private int errCode;

    public WxLoginEvent(int errCode) {
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }
}
