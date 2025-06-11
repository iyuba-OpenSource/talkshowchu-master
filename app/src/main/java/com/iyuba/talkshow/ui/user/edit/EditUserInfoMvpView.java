package com.iyuba.talkshow.ui.user.edit;

import com.iyuba.talkshow.ui.base.MvpView;

/**
 * Created by Administrator on 2016/12/22/022.
 */

public interface EditUserInfoMvpView extends MvpView {

    void setLocationTv(String text);

    void showToast(int resId);

    void showLoadingDialog();

    void dismissLoadingDialog();

    void setSaveBtnClickable(boolean clickable);
}
