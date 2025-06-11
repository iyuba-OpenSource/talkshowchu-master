package com.iyuba.talkshow.ui.about;

import com.iyuba.talkshow.ui.base.MvpView;

/**
 * Created by Administrator on 2016/12/16/016.
 */

public interface AboutMvpView extends MvpView {

    void setDownloadProgress(int progress);

    void setDownloadMaxProgress(int maxProgress);

    void setProgressVisibility(int visible);

    void showToast(int resId);
}
