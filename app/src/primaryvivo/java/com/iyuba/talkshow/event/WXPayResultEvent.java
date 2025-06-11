package com.iyuba.talkshow.event;

/**
 * 微信支付的状态
 */
public class WXPayResultEvent {

//    int ERR_OK = 0;
//    int ERR_COMM = -1;
//    int ERR_USER_CANCEL = -2;
//    int ERR_SENT_FAILED = -3;
//    int ERR_AUTH_DENIED = -4;
//    int ERR_UNSUPPORT = -5;
//    int ERR_BAN = -6;

    private int resultCode;//支付状态(0为成功，其他为失败)

    public WXPayResultEvent(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }
}
