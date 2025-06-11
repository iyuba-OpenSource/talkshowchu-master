package com.iyuba.talkshow.ui.user.detail;

import com.iyuba.talkshow.data.model.result.GetUserBasicInfoResponse;
import com.iyuba.talkshow.ui.base.MvpView;

/**
 * Created by Administrator on 2016/12/22/022.
 */

public interface ShowUserInfoMvpView extends MvpView {
    void setUserInfo(GetUserBasicInfoResponse response);

    void showToast(int resId);

    void dismissWaitingDialog();

    void finishActivity();
}
