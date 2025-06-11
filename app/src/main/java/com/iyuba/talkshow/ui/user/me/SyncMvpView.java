package com.iyuba.talkshow.ui.user.me;

import com.iyuba.talkshow.ui.base.MvpView;

/**
 * Created by carl shen on 2021/4/8
 * New Primary English, new study experience.
 */
public interface SyncMvpView extends MvpView {
    void showLoadingDialog();
    void startDownload(int book);
    void dismissLoadingDialog();
}
