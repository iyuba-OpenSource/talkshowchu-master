package com.iyuba.talkshow.ui.feedback;

import com.iyuba.talkshow.ui.base.MvpView;

public interface FeedBackMvpView extends MvpView {

    void setEdtEmailError(int resId);

    void setEdtContentError(int resId);

    void showWaitingDialog();

    void dismissWaitingDialog();

    void showToast(int resId);

    void showDialog();
}
