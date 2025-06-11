package com.iyuba.talkshow.newce;

import com.iyuba.talkshow.ui.base.MvpView;

/**
 * Created by carl shen on 2020/8/13
 * New Junior English, new study experience.
 */
public interface ContainerMvpView extends MvpView {

    void setDownloadProgress(int progress);

    void setDownloadMaxProgress(int maxProgress);

    void setProgressVisibility(int visible);

    void showToast(int resId);
}
