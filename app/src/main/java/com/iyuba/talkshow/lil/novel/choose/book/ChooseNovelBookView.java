package com.iyuba.talkshow.lil.novel.choose.book;

import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_book;
import com.iyuba.talkshow.lil.help_mvp.mvp.BaseView;

import java.util.List;

/**
 * @title:
 * @date: 2023/4/27 14:24
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface ChooseNovelBookView extends BaseView {

    //显示数据
    void showBookData(List<Novel_book> list);
}
