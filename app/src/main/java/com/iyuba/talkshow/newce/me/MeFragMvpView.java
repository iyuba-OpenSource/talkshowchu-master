package com.iyuba.talkshow.newce.me;

import com.iyuba.talkshow.newview.LoginResult;
import com.iyuba.talkshow.ui.base.MvpView;

/**
 * Created by carl shen on 2020/9/18.
 */

public interface MeFragMvpView extends MvpView {
    void showLoadingDialog();
    void startDownload(int book);
    void dismissLoadingDialog();
}
