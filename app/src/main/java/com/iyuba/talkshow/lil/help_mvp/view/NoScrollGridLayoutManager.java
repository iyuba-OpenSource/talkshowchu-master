package com.iyuba.talkshow.lil.help_mvp.view;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;

/**
 * @desction: 无法滑动的GridLayoutManager
 * @date: 2023/3/13 00:43
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class NoScrollGridLayoutManager extends GridLayoutManager {

    private boolean isCanScroll = true;

    public NoScrollGridLayoutManager(Context context, int spanCount, boolean canScroll) {
        super(context, spanCount);
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
