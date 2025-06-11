package com.iyuba.talkshow.lil.help_fix.ui.study.rank;

import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_rank;
import com.iyuba.talkshow.lil.help_mvp.mvp.BaseView;

/**
 * @title:
 * @date: 2023/5/25 10:09
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface RankView extends BaseView {

    //展示数据
    void showData(boolean isRefresh, Eval_rank bean);
}
