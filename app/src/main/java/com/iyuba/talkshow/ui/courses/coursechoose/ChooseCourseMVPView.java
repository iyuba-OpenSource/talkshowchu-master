package com.iyuba.talkshow.ui.courses.coursechoose;


import com.iyuba.talkshow.data.model.LessonNewResponse;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

public interface ChooseCourseMVPView extends MvpView {
     void setMoreCourse(List<SeriesData> beans);
     void setCoures(List<SeriesData> beans);
     void setCourseFail(String showMsg);

     void setLesson(List<LessonNewResponse.Series> series);
     void setLessonFail(String showMsg);
}
