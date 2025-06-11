package com.iyuba.talkshow.lil.user.listener;

/**
 * @title: 用户信息的回调接口
 * @date: 2023/11/7 13:49
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface UserinfoCallbackListener {
    void onSuccess();
    void onFail(String errorMsg);
}
