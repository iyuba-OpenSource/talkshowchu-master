package com.iyuba.talkshow.newce.comment;

import com.iyuba.talkshow.data.model.RankWork;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public interface CommentMvpView extends MvpView {
    void showRankings(List<RankWork> rankingList);

    void showEmptyRankings();

    void showToast(int id);

    void showLoadingLayout();

    void dismissLoadingLayout();

    void refreshLayout();
}
