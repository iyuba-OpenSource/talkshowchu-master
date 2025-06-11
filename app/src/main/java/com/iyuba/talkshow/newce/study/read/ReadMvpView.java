package com.iyuba.talkshow.newce.study.read;

import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by carl shen on 2020/7/29
 * New Primary English, new study experience.
 */
public interface ReadMvpView extends MvpView {
    void showVoaTexts(List<VoaText> voaTextList);
    void showEmptyTexts();
}
