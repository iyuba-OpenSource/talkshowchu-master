package com.iyuba.talkshow.ui.user.me;

import com.iyuba.talkshow.data.model.result.ShareInfoRecord;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by carl shen on 2021/4/23
 * New Primary English, new study experience.
 */
public interface CalendarMvpView extends MvpView {
    void showCalendar(List<ShareInfoRecord> rankingList);

    void showLoadingLayout();
    void dismissLoadingLayout();
}
