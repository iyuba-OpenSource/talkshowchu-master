package com.iyuba.talkshow.newce.study.eval;

import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by carl shen on 2020/7/28
 * New Primary English, new study experience.
 */

public interface EvalMvpView extends MvpView {
    void showVoaTexts(List<VoaText> voaTextList);

    void showEmptyTexts();

    void showLoadingDialog();

    void dismissLoadingDialog();

    void showMergeDialog();

    void dismissMergeDialog();

    void startPreviewActivity();

    void showToast(int resId);

    void onDraftRecordExist(Record record);

}
