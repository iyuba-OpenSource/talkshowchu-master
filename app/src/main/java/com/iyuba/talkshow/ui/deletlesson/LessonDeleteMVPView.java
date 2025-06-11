package com.iyuba.talkshow.ui.deletlesson;

import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

interface LessonDeleteMVPView  extends MvpView {

    void showDeleteMessage(String message);

    void showBookList(List<SeriesData> seriesData);
}
