package com.iyuba.talkshow.lil.help_mvp.view;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * @desction: 无法滑动的LinearLayoutManager
 * @date: 2023/3/13 00:43
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class NoScrollLinearLayoutManager extends LinearLayoutManager {

    private boolean isCanScroll = true;

    public NoScrollLinearLayoutManager(Context context,boolean canScroll) {
        super(context);
        this.isCanScroll = canScroll;
    }

    @Override
    public boolean canScrollHorizontally() {
        return isCanScroll;
    }

    @Override
    public boolean canScrollVertically() {
        return isCanScroll;
    }
}
