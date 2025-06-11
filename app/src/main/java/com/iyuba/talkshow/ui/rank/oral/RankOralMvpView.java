package com.iyuba.talkshow.ui.rank.oral;

import com.iyuba.talkshow.data.model.RankListenBean;
import com.iyuba.talkshow.data.model.RankOralBean;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * RankMvpView
 *
 * @author wayne
 * @date 2018/2/6
 */
public interface RankOralMvpView extends MvpView {
    void showRankingList(List<RankOralBean.DataBean> data);

    void showMoreRankList(List<RankOralBean.DataBean> data);

    void showUserInfo(RankOralBean rankListenBean);

    void showLoadingDialog();

    void dismissLoadingDialog();

}
