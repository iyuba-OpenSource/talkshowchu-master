package com.iyuba.talkshow.ui.user.login.changeName;

import com.iyuba.talkshow.ui.base.MvpView;

public interface ChangeNameMvpView extends MvpView {

    void showToast(String text);

    void showToast(int resId);

    void loginResult(boolean result);
    void finishChangeActivity();

    void dismissWaitingDialog();
}
