package com.iyuba.talkshow.ui.rank.study;

import com.iyuba.talkshow.data.model.RankListenBean;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * @desction:
 * @date: 2023/2/9 15:51
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public interface RankStudyMvpView extends MvpView {

    void showRankingList(List<RankListenBean.DataBean> data);

    void showMoreRankList(List<RankListenBean.DataBean> data);

    void showUserInfo(RankListenBean rankListenBean);

    void showLoadingDialog();

    void dismissLoadingDialog();
}
