package com.iyuba.talkshow.ui.welcome;

import com.iyuba.talkshow.data.model.result.GetAdData1;
import com.iyuba.talkshow.ui.base.MvpView;

import java.io.FileInputStream;

/**
 * Created by Administrator on 2016/12/26/026.
 */

public interface WelcomeMvpView extends MvpView {

    void startHelpUseActivity(boolean flag);
    void finishActivity();
    void showToast(int resId);
}
