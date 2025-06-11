package com.iyuba.talkshow.ui.user.me.dubbing.released;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Ranking;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.result.GetMyDubbingResponse;
import com.iyuba.talkshow.data.remote.ThumbsService;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Emitter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Administrator on 2017/1/13/013.
 */

public class ReleasedPresenter extends BasePresenter<ReleasedMvpView> {
    private final DataManager mDataManager;

    private Subscription mGetReleasedSub;
    private Subscription mDeleteReleasedSub;
    private Subscription mGetVoaSub;

    @Inject
    public ReleasedPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetReleasedSub);
        RxUtil.unsubscribe(mDeleteReleasedSub);
        RxUtil.unsubscribe(mGetVoaSub);
    }

    public void getReleasedData(int uid) {
        checkViewAttached();
//        if(!mAccountManager.checkLogin()) {
//            getMvpView().startLoginActivity();
//        } else {
        if (uid == 0) {
            return;
        }
        RxUtil.unsubscribe(mGetReleasedSub);
        getMvpView().showLoadingLayout();
        mGetReleasedSub = mDataManager.getMyDubbing(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetMyDubbingResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getMvpView().dismissLoadingLayout();

                        if (!NetStateUtil.isConnected(((BaseFragment) getMvpView()).getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(GetMyDubbingResponse response) {
                        getMvpView().dismissLoadingLayout();
                        if (response.result() == ThumbsService.GetMyDubbing.Result.SUCCESS) {
                            List<Ranking> mData = new ArrayList<>();
                            for (Ranking rank : response.data()) {
                                List<Voa> voaList = mDataManager.getVoaByVoaId(rank.topicId());
                                if (voaList != null && voaList.size() > 0) {
                                    mData.add(rank);
                                }
                            }
                            getMvpView().setReleasedData(mData);
                        } else {
                            getMvpView().setEmptyData();
                        }
                    }
                });

    }

    public void deleteReleasedData(final List<Integer> idList, final Integer uid) {
        getMvpView().showLoadingLayout();
        mDeleteReleasedSub = Observable.create(new Action1<Emitter<Integer>>() {
            @Override
            public void call(Emitter<Integer> stringEmitter) {
                for (Integer s : idList) {
                    Timber.e("/ ::: id  : "+s );
                    stringEmitter.onNext(s);
                }
                stringEmitter.onCompleted();
            }
        }, Emitter.BackpressureMode.NONE)
                .flatMap(new Func1<Integer, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(Integer s) {
                        return mDataManager.deleteReleaseRecord(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().dismissLoadingLayout();
                        getReleasedData(uid);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getMvpView().dismissLoadingLayout();
                        getReleasedData(uid);
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });
    }

    public void getVoa(final Ranking ranking) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetVoaSub);
        mGetVoaSub = mDataManager.getVoaById(ranking.topicId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Voa>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Voa voa) {
                        if (voa != null) {
                            getMvpView().startWatchDubbingActivity(voa, ranking);
                        }
                    }
                });
    }
}
