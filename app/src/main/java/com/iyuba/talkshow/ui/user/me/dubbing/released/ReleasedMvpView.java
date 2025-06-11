package com.iyuba.talkshow.ui.user.me.dubbing.released;

import com.iyuba.talkshow.data.model.Ranking;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2017/1/13/013.
 */

public interface ReleasedMvpView extends MvpView {
    void showLoadingLayout();

    void dismissLoadingLayout();

    void setEmptyData();

    void setReleasedData(List<Ranking> mData);

    void startLoginActivity();

    void startWatchDubbingActivity(Voa voa, Ranking ranking);

    void showToast(int resId);
}
