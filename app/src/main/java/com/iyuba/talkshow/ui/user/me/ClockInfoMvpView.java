package com.iyuba.talkshow.ui.user.me;

import com.iyuba.talkshow.data.model.result.ShareInfoRecord;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by carl shen on 2021/4/12
 * New Primary English, new study experience.
 */
public interface ClockInfoMvpView extends MvpView {
    void showRankings(List<ShareInfoRecord> rankingList);
    void showMoreRankings(List<ShareInfoRecord> rankingList);
    void showEmptyRankings();

    void showToast(int resId);
    void showLoadingLayout();
    void dismissLoadingLayout();
    void dismissRefreshingView();
}
