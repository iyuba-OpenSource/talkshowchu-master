package com.iyuba.talkshow.ui.user.me.dubbing.released;

/**
 * 当前主要解决点赞后前一界面不刷新的问题
 */
public class ReleasedBean {

    private boolean isRefresh;

    public ReleasedBean(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }
}
