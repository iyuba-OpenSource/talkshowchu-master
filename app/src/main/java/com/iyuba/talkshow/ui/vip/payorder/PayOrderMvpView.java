package com.iyuba.talkshow.ui.vip.payorder;

import com.iyuba.talkshow.ui.base.MvpView;

/**
 * Created by Administrator on 2016/12/20/020.
 */

public interface PayOrderMvpView extends MvpView {

    void showToast(String text);

    void showOrderUnusualDialog();

    void showWeixinNotInstallDialog();

    void showWaitingDialog();

    void dismissWaitingDialog();

    void showToast(int resId);

    void finishActivity();

    //显示支付宝的状态
    void showAliPayStatus(String payStatus);
}
