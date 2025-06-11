package com.iyuba.talkshow.ui.web;

import com.iyuba.talkshow.ui.base.MvpView;
import com.iyuba.wordtest.db.OfficialAccount;

import java.util.List;

/**
 * Created by carl shen on 2021/6/7
 * New Primary English, new study experience.
 */
public interface OfficialMvpView extends MvpView {

    void showLoadingLayout();
    void dismissLoadingLayout();

    void setEmptyAccount();
    void setDataAccount(List<OfficialAccount> data);
    void setMoreAccount(List<OfficialAccount> data);

    void showToast(int resId);

}
