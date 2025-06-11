package com.iyuba.wordtest.ui.listen.singleWrite;

import com.iyuba.module.mvp.MvpView;
import com.iyuba.wordtest.entity.WordEntity;

public interface WordSingleWriteMvpView extends MvpView {

    //单词查询成功
    void wordSearchSuccess(WordEntity entity);
    //单词查询失败
    void wordSearchFail(String msg);
}
