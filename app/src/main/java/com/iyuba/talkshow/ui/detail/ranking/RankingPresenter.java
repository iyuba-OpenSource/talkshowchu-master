package com.iyuba.talkshow.ui.detail.ranking;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Ranking;
import com.iyuba.talkshow.data.model.result.GetRankingResponse;
import com.iyuba.talkshow.data.model.result.ThumbsResponse;
import com.iyuba.talkshow.data.remote.ThumbsService;
import com.iyuba.talkshow.injection.PerFragment;
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

@PerFragment
public class RankingPresenter extends BasePresenter<RankingMvpView> {

    private final DataManager mDataManager;
    private Subscription mGetRankingSub;
    private Subscription mDoAgreeSub;

    @Inject
    public RankingPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    public void getRanking(int voaId, int pageNum, int pageSize) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetRankingSub);
        getMvpView().showLoadingLayout();
        mGetRankingSub = mDataManager.getThumbsRanking(voaId, pageNum, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetRankingResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissLoadingLayout();
                        getMvpView().dismissRefreshingView();
                        if(!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(GetRankingResponse response) {
                        getMvpView().dismissLoadingLayout();
                        getMvpView().dismissRefreshingView();
                        if(response.resultCode() == ThumbsService.GetRanking.Result.SUCCESS) {
                            List<Ranking> rankingList = response.data();
                            if (rankingList == null || rankingList.isEmpty()) {
                                getMvpView().showEmptyRankings();
                            } else {
                                getMvpView().showRankings(rankingList);
                            }
                        } else {
                            getMvpView().showEmptyRankings();
                        }
                    }
                });
    }
    public void getMoreRanking(int voaId, int pageNum, int pageSize) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetRankingSub);
        getMvpView().showLoadingLayout();
        mGetRankingSub = mDataManager.getThumbsRanking(voaId, pageNum, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetRankingResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissLoadingLayout();
                        getMvpView().dismissRefreshingView();
                        if(!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_no_more);
                        }
                    }

                    @Override
                    public void onNext(GetRankingResponse response) {
                        getMvpView().dismissLoadingLayout();
                        getMvpView().dismissRefreshingView();
                        if(response.resultCode() == ThumbsService.GetRanking.Result.SUCCESS) {
                            List<Ranking> rankingList = response.data();
                            if (rankingList != null && !rankingList.isEmpty()) {
                                getMvpView().showMoreRankings(rankingList);
                                return;
                            }
                        }
                        getMvpView().showToast(R.string.request_no_more);
                    }
                });
    }

    public void doAgree(int id) {
        checkViewAttached();
        RxUtil.unsubscribe(mDoAgreeSub);
        mDoAgreeSub = mDataManager.doAgree(UserInfoManager.getInstance().getUserId(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ThumbsResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(ThumbsResponse result) {
                        if(ThumbsService.DoThumbs.Result.THUMBS_SUCCESS.equals(result.resultCode())) {
                            getMvpView().showToast(R.string.thumbs_success);
                        } else {
                            getMvpView().showToast(R.string.thumbs_failure);
                        }
                    }
                });
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetRankingSub);
        RxUtil.unsubscribe(mDoAgreeSub);
    }
}
