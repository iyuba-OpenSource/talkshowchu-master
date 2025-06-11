package com.iyuba.talkshow.ui.courses.coursedetail;

import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

interface CourseDetailMVPView extends MvpView {

    void showCourses(List<Voa> voas);
}
