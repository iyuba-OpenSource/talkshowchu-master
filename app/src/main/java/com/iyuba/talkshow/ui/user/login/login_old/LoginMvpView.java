package com.iyuba.talkshow.ui.user.login.login_old;

import com.iyuba.talkshow.ui.base.MvpView;

public interface LoginMvpView extends MvpView {
    void showWaitingDialog();

    void dismissWaitingDialog();

    void showToast(String message);

    void startImproveUser(int userId);
}
