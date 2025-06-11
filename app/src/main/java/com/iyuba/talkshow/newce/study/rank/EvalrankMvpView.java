package com.iyuba.talkshow.newce.study.rank;

import com.iyuba.talkshow.data.model.RankOralBean;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * RankMvpView
 *
 * @author wayne
 * @date 2018/2/6
 */
public interface EvalrankMvpView extends MvpView {
    void showRankingList(List<RankOralBean.DataBean> data);

    void showMoreRankList(List<RankOralBean.DataBean> data);

    void showUserInfo(RankOralBean rankOralBean);

    void showLoadingDialog();

    void dismissLoadingDialog();

}
