package com.iyuba.talkshow.ui.preview;

import com.iyuba.talkshow.ui.base.MvpView;

/**
 * Created by Administrator on 2016/12/9 0009.
 */

public interface PreviewMvpView extends MvpView {

    void startLoginActivity();

    void showToast(int resId);

    void showShareView(String url);

    void showShareHideReleaseButton();

    void startMyDubbingActivity();

    void showReleaseButton();

    void showLoadingDialog();

    void dismissLoadingDialog();

    void showPublishSuccess(int resID);

    void showPublishFailure(int resID);

    void startMainActivity();
}
