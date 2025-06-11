package com.iyuba.talkshow.ui.user.edit.dialog;

import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.University;
import com.iyuba.talkshow.injection.PerDialog;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/24/024.
 */

@PerDialog
public class SchoolPresenter extends BasePresenter<SchoolMvpView> {
    private static final int SHOW_SCHOOL_SIZE = 60;

    private final DataManager mDataManager;

    private Subscription mSearchSub;
    private Subscription gAllSchool;
    @Inject
    public SchoolPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSearchSub);
        RxUtil.unsubscribe(gAllSchool);
    }
    public void getallschools(){
        checkViewAttached();
        RxUtil.unsubscribe(gAllSchool);
        gAllSchool = mDataManager.getallSchools().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<University>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<University> universities) {
                getMvpView().showAllUniversities(universities);
            }
        });
    }

    public void search(String keyWord) {
        if(!"".equals(keyWord)){
        checkViewAttached();
        RxUtil.unsubscribe(mSearchSub);
        mSearchSub = mDataManager.searchSchool(keyWord, SHOW_SCHOOL_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<University>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showToast(R.string.database_error);
                        Log.e("database-error",e.toString());
                    }

                    @Override
                    public void onNext(List<University> universities) {
                        Log.e("Size",universities.size()+"");
                        if(universities!=null)
                        getMvpView().showUniversities(universities);
                    }
                });
        }
    }
}
