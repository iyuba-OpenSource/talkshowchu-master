package com.iyuba.wordtest.ui;

import com.iyuba.module.mvp.MvpView;

public interface WordListMvpView extends MvpView {

    void refreshWords();
    void showText(String text);

}
