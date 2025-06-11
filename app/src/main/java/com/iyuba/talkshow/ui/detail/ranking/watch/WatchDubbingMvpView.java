package com.iyuba.talkshow.ui.detail.ranking.watch;

import com.iyuba.talkshow.ui.base.MvpView;

/**
 * Created by Administrator on 2016/12/10 0010.
 */

public interface WatchDubbingMvpView extends MvpView {
    void updateThumbIv(int action);

    void updateThumbNumTv();

    void showToast(int resId);
}
