package com.iyuba.talkshow.lil.help_fix.ui.me_wallet;

import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Reward_history;
import com.iyuba.talkshow.lil.help_mvp.mvp.BaseView;

import java.util.List;

/**
 * @title:
 * @date: 2023/8/23 09:08
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface WalletView extends BaseView {

    //显示奖励的历史记录
    void showRewardHistory(List<Reward_history> list);
}
