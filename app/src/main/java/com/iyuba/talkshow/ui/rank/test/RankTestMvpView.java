package com.iyuba.talkshow.ui.rank.test;

import com.iyuba.talkshow.data.model.RankTestBean;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * @desction:
 * @date: 2023/2/9 17:39
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public interface RankTestMvpView extends MvpView {

    void showRankingList(List<RankTestBean.DataBean> data);

    void showMoreRankList(List<RankTestBean.DataBean> data);

    void showUserInfo(RankTestBean rankTestBean);

    void showLoadingDialog();

    void dismissLoadingDialog();
}
