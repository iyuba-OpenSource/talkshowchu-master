package com.iyuba.talkshow.ui.detail.ranking;

import com.iyuba.talkshow.data.model.Ranking;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public interface RankingMvpView extends MvpView {
    void showRankings(List<Ranking> rankingList);
    void showMoreRankings(List<Ranking> rankingList);

    void showEmptyRankings();

    void showToast(int id);

    void showLoadingLayout();

    void dismissLoadingLayout();

    void dismissRefreshingView();
}
