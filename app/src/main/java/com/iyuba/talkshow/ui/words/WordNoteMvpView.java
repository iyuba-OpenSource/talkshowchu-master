package com.iyuba.talkshow.ui.words;

import androidx.annotation.StringRes;
import android.view.ActionMode;


import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

interface WordNoteMvpView extends MvpView {

    void setLoading(boolean isLoading);

    void setRecyclerEndless(boolean isEndless);

    void showMessage(String message);

    void showMessage(@StringRes int resId);

    void onLatestDataLoaded(List<Word> words, int total, boolean instantRefresh);

    void onMoreDataLoaded(List<Word> words, int page);

    void onDeleteAccomplish(int userId, ActionMode mode);

    //删除单词完成
    void onDeleteAccomplish(int userId);
}
