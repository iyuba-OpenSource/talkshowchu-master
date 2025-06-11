package com.iyuba.talkshow.ui.user.image;

import com.iyuba.talkshow.ui.base.MvpView;

/**
 * Created by Administrator on 2016/12/14/014.
 */

public interface UploadImageMvp extends MvpView {
    void dismissWaitingDialog();

    void setSubmitBtnClickable(boolean clickable);

    void showToast(int resId);

    void startVipCenterActivity(boolean isRegister);

    void finishCurActivity();

    void showAlertDialog(String title, int msgResId);
}
