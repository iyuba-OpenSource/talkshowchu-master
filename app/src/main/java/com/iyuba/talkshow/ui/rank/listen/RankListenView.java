package com.iyuba.talkshow.ui.rank.listen;

import com.iyuba.talkshow.data.model.RankListenBean;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by carl shen on 2021/7/26
 * New Primary English, new study experience.
 */
public interface RankListenView extends MvpView {
    void showRankingList(List<RankListenBean.DataBean> data);

    void showMoreRankList(List<RankListenBean.DataBean> data);

    void showUserInfo(RankListenBean rankListenBean);

    void showLoadingDialog();

    void dismissLoadingDialog();

}
