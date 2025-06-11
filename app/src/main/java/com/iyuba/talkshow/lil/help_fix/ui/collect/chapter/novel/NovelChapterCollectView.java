package com.iyuba.talkshow.lil.help_fix.ui.collect.chapter.novel;

import com.iyuba.talkshow.lil.help_mvp.mvp.BaseView;

/**
 * @title:
 * @date: 2023/8/31 09:45
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface NovelChapterCollectView extends BaseView {

    //显示收藏/取消收藏的结果
    void showCollectResult(boolean isSuccess);
}
