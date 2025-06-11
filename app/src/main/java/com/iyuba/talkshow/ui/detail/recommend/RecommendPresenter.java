package com.iyuba.talkshow.ui.detail.recommend;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.injection.PerFragment;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@PerFragment
public class RecommendPresenter extends BasePresenter<RecommendMvpView> {
    private final DataManager mDataManager;
    private Subscription mSubscription;

    private final Subscriber<List<Voa>> subscriber = new Subscriber<List<Voa>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            getMvpView().showToast(R.string.database_error);
        }

        @Override
        public void onNext(List<Voa> voas) {
            if(voas.size() == 0) {
                getMvpView().showEmptyRecommend();
            } else {
                getMvpView().showRecommend(voas);
            }
        }
    };

    @Inject
    public RecommendPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void attachView(RecommendMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    public void getRecommendList(int voaId, int category, int pageNum, int pageSize) {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getRecommendList(voaId, category, pageNum, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getSeriesList(int voaId, int series, int pageNum, int pageSize) {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getSeriesList(voaId, series, pageNum, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
