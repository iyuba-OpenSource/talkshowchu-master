package com.iyuba.talkshow.ui.user.me.dubbing.draft;

import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2017/1/13/013.
 */

public interface DraftMvpView extends MvpView {

    void showLoadingLayout();

    void dismissLoadingLayout();

    void setEmptyData();

    void setDraftData(List<Record> mData);

    void startDubbingActivity(Voa voa, long timestamp);

    void showToast(int resId);
}
