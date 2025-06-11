package com.iyuba.talkshow.newce.study.dubbingNew;

import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.model.WordResponse;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * @desction:
 * @date: 2023/2/15 10:44
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public interface DubbingNewMvpView extends MvpView {

    void showVoaTexts(List<VoaText> voaTextList);

    void showEmptyTexts();

    void dismissDubbingDialog();

    void showMergeDialog();

    void dismissMergeDialog();

    void startPreviewActivity();

    void showToast(int resId);

    void showToast(String message);

    void pause();

    void onDraftRecordExist(Record record);

    void showWord(WordResponse bean);
}
