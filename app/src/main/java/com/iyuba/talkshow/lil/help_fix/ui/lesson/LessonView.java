package com.iyuba.talkshow.lil.help_fix.ui.lesson;

import com.iyuba.talkshow.lil.help_fix.data.bean.BookChapterBean;
import com.iyuba.talkshow.lil.help_mvp.mvp.BaseView;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/19 13:55
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface LessonView extends BaseView {

    //展示列表数据
    void showData(List<BookChapterBean> list);

    //联网加载数据
    void loadNetData();
}
