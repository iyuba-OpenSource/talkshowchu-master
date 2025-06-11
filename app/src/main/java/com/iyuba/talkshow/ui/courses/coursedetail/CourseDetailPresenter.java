package com.iyuba.talkshow.ui.courses.coursedetail;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.ui.base.BasePresenter;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ConfigPersistent
public class CourseDetailPresenter extends BasePresenter<CourseDetailMVPView> {


    private final DataManager dataManager;

    @Inject
    CourseDetailPresenter(DataManager dataManager ){
        this.dataManager = dataManager ;
    }

    public void  getVoas(int cat){
        dataManager.getCoursesVoas(cat).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Subscriber<List<Voa>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getMvpView().showToastShort(R.string.database_error);
                    }

                    @Override
                    public void onNext(List<Voa> voas) {
                        getMvpView().showCourses(voas);
                    }

                });
    }
}
