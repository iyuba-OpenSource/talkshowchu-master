package com.iyuba.talkshow.ui.user.edit.dialog;

import com.iyuba.talkshow.data.model.University;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2016/12/24/024.
 */

public interface SchoolMvpView extends MvpView {

    void showUniversities(List<University> list);

    void showToast(int resId);

    void showAllUniversities(List<University> list);
}
