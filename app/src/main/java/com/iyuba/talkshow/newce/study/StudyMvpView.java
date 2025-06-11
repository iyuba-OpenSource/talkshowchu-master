package com.iyuba.talkshow.newce.study;

import com.iyuba.talkshow.ui.base.MvpView;

public interface StudyMvpView extends MvpView {
    void showToast(int resId);
    void showToast(String message);
    void setIsCollected(boolean isCollected);
    void showPdfFinishDialog(String url);
    void onDeductIntegralSuccess(int type);
}
