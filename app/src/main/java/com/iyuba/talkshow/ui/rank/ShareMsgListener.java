package com.iyuba.talkshow.ui.rank;

/**
 * @desction:
 * @date: 2023/2/9 19:43
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public interface ShareMsgListener {

    //需要分享的信息
    String getShareMsg();

    //需要分享的链接
    String getShareUrl();
}
