package com.iyuba.talkshow.ui.user.register.phone;

import com.iyuba.talkshow.ui.base.MvpView;

/**
 * Created by Administrator on 2016/12/15/015.
 */

public interface RegisterByPhoneMvpView extends MvpView {

    void updateGetCodeBtn();

    void dismissWaitingDialog();

    void showToast(String message);

    void showToast(int resId);

    void registerSmsObserver();
}
