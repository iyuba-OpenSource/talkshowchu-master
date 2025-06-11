package com.iyuba.talkshow.ui.user.register.submit;

import com.iyuba.talkshow.ui.base.MvpView;

public interface RegisterSubmitMvpView extends MvpView {

    void showToast(String text);

    void showToast(int resId);

    void startUploadImageActivity();
    void startLoginActivity();

    void finishRegisterActivity();

    void dismissWaitingDialog();
}
