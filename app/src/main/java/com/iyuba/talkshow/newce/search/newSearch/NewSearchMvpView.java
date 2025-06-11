package com.iyuba.talkshow.newce.search.newSearch;

import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Word_detail;
import com.iyuba.talkshow.ui.base.MvpView;
import com.iyuba.wordtest.entity.TalkShowWords;

import java.util.List;

public interface NewSearchMvpView extends MvpView {

    //查询单词-联网
    void showWord(String msg,Word_detail detail);
    //查询例句
    void showSentence(List<VoaText> evalList);
    //查询课文
    void showVoa(List<Voa> voaList);
}
