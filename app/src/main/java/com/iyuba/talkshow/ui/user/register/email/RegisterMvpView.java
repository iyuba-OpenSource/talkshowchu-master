package com.iyuba.talkshow.ui.user.register.email;

import com.iyuba.talkshow.ui.base.MvpView;

/**
 * Created by Administrator on 2016/12/15/015.
 */

public interface RegisterMvpView extends MvpView {
    void startUploadImageActivity();

    void finishRegisterActivity();

    void showToast(String message);

    void showToast(int resId);

    void showWaitingDialog();

    void dismissWaitingDialog();


}