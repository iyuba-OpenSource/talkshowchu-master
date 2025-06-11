package com.iyuba.talkshow.ui.user.me;

import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.result.ShareInfoRecord;
import com.iyuba.talkshow.data.model.result.ShareInfoResponse;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carl shen on 2021/4/12
 * New Primary English, new study experience.
 */
@ConfigPersistent
public class ClockInfoPresenter extends BasePresenter<ClockInfoMvpView>{
    private final DataManager mDataManager;
    private Subscription mGetRankingSub;

    @Inject
    public ClockInfoPresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetRankingSub);
    }

    public void getRanking(int pageNum, int pageSize) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetRankingSub);
        getMvpView().showLoadingLayout();
        mGetRankingSub = mDataManager.getShareInfo(UserInfoManager.getInstance().getUserId(), App.APP_ID, pageNum, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShareInfoResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissLoadingLayout();
                        getMvpView().dismissRefreshingView();
                        if (e != null) {
                            Log.e("DakaInfoPresenter", "getRanking onError " + e.getMessage());
                        }
                        if(!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(ShareInfoResponse response) {
                        getMvpView().dismissLoadingLayout();
                        getMvpView().dismissRefreshingView();
                        if((response != null) && "200".equals(response.result)) {
                            Log.e("DakaInfoPresenter", "getRanking onNext response.count " + response.count);
                            List<ShareInfoRecord> rankingList = response.record;
                            if (rankingList == null || rankingList.isEmpty()) {
                                Log.e("DakaInfoPresenter", "getRanking onNext empty.");
                                getMvpView().showEmptyRankings();
                            } else {
                                getMvpView().showRankings(rankingList);
                            }
                        } else {
                            Log.e("DakaInfoPresenter", "getRanking onNext empty.");
                            getMvpView().showEmptyRankings();
                        }
                    }
                });
    }
    public void getMoreRanking(int pageNum, int pageSize) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetRankingSub);
        getMvpView().showLoadingLayout();
        mGetRankingSub = mDataManager.getShareInfo(UserInfoManager.getInstance().getUserId(), App.APP_ID, pageNum, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShareInfoResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("DakaInfoPresenter", "getMoreRanking onError " + e.getMessage());
                        }
                        getMvpView().dismissLoadingLayout();
                        getMvpView().dismissRefreshingView();
                        if(!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_no_more);
                        }
                    }

                    @Override
                    public void onNext(ShareInfoResponse response) {
                        getMvpView().dismissLoadingLayout();
                        getMvpView().dismissRefreshingView();
                        if((response != null) && "200".equals(response.result)) {
                            Log.e("DakaInfoPresenter", "getMoreRanking onNext response.count " + response.count);
                            List<ShareInfoRecord> rankingList = response.record;
                            if (rankingList != null && !rankingList.isEmpty()) {
                                getMvpView().showMoreRankings(rankingList);
                                return;
                            }
                        }
                        getMvpView().showToast(R.string.request_no_more);
                    }
                });
    }

}
