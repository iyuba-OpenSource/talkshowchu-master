package com.iyuba.talkshow.ui.dubbing;

import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.model.WordResponse;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2016/11/30 0030.
 */

public interface DubbingMvpView extends MvpView {
    void showVoaTexts(List<VoaText> voaTextList);

    void showEmptyTexts();

    void dismissDubbingDialog();

    void showMergeDialog();

    void dismissMergeDialog();

    void startPreviewActivity();

    void showToast(int resId);

    void pause();

    void onDraftRecordExist(Record record);

    void showWord(WordResponse bean);
}
