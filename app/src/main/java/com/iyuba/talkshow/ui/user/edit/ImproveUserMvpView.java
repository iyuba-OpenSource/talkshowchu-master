package com.iyuba.talkshow.ui.user.edit;

import com.iyuba.talkshow.data.model.result.GetUserBasicInfoResponse;
import com.iyuba.talkshow.ui.base.MvpView;

/**
 * Created by carl shen on 2020/12/2.
 */
public interface ImproveUserMvpView extends MvpView {
    void setIPInfo(GetIpResponse response);
    void setUserInfo(GetUserBasicInfoResponse response);
    void showLoadingDialog();
    void dismissWaitingDialog();
    void finishImprove();
}
