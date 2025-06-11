package com.iyuba.wordtest.ui.detail;

import com.iyuba.module.mvp.MvpView;
import com.iyuba.wordtest.bean.SendEvaluateResponse;
import com.iyuba.wordtest.entity.TalkshowTexts;
import com.iyuba.wordtest.entity.WordEntity;

import java.util.List;

public interface WordDetailMvpView  extends MvpView {

    void showScore(Float total_score, String score, List<SendEvaluateResponse.DataBean.WordsBean> words, String url) ;

    void showFail();

    void showSentenceFail();

    void showSearchResult(WordEntity result);

    void showSentence(TalkshowTexts texts);

    void showText(String text);
}
