package com.iyuba.talkshow.ui.rank.oral;

import android.util.Log;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.RankOralBean;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.RxUtil;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * RankPresenter
 *
 * @author wayne
 * @date 2018/2/6
 */
@ConfigPersistent
public class RankOralPresenter extends BasePresenter<RankOralMvpView> {

    private final DataManager mDataManager;
    private int total = 20;
    private int start = 0;
    private String type;

    private Subscription mRankingListSub;

    @Inject
    public RankOralPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mRankingListSub);
    }

    public void loadDayRank() {
        start = 0;
        type = "D";
        loadRankList(type, start);
    }

    public void loadWeekRank() {
        start = 0;
        type = "W";
        loadRankList(type, start);
    }

    public void loadMonthRank() {
        start = 0;
        type = "M";
        loadRankList(type, start);
    }

    public void loadRankList(String type, final int start) {
        getMvpView().showLoadingDialog();
        RxUtil.unsubscribe(mRankingListSub);
        mRankingListSub = mDataManager.getRankingList(UserInfoManager.getInstance().getUserId(), type, start, total)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RankOralBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("RankFragment", "loadRankList onError " + e.getMessage());
                        }
                        getMvpView().dismissLoadingDialog();
                        getMvpView().showToastShort(R.string.error_loading);
                    }

                    @Override
                    public void onNext(RankOralBean rankListenBean) {
                        getMvpView().dismissLoadingDialog();
                        int result = rankListenBean.getResult();
                        if (result > 0) {
                            setStartPos();
                            if (start == 0) {
                                getMvpView().showUserInfo(rankListenBean);
                                getMvpView().showRankingList(rankListenBean.getData());
                            } else {
                                getMvpView().showMoreRankList(rankListenBean.getData());
                            }
                        }
                    }
                });
    }
    public void loadEvalRankList(String type, int topicId, int start, int total) {
        getMvpView().showLoadingDialog();
        RxUtil.unsubscribe(mRankingListSub);
        mRankingListSub = mDataManager.getEvalRankList(UserInfoManager.getInstance().getUserId(), Constant.EVAL_TYPE, topicId, type, start, total)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RankOralBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("RankFragment", "loadRankList onError " + e.getMessage());
                        }
                        getMvpView().dismissLoadingDialog();
                        getMvpView().showToastShort(R.string.error_loading);
                    }

                    @Override
                    public void onNext(RankOralBean rankListenBean) {
                        getMvpView().dismissLoadingDialog();
                        if (rankListenBean == null) {
                            Log.e("RankFragment", "loadRankList onNext is null.");
                            return;
                        }
                        if (start == 0) {
                            getMvpView().showUserInfo(rankListenBean);
                        }
                        int result = rankListenBean.getResult();
                        Log.e("RankFragment", "loadRankList onNext result " + result);
                        if (result > 0) {
                            setStartPos();
                            if (start == 0) {
                                getMvpView().showRankingList(rankListenBean.getData());
                            } else {
                                getMvpView().showMoreRankList(rankListenBean.getData());
                            }
                        } else {
                            Log.e("RankFragment", "loadRankList onNext is empty.");
                        }
                    }
                });
    }
    public void loadListenRankList(String type, int topicId, final int start, int total) {
        getMvpView().showLoadingDialog();
        RxUtil.unsubscribe(mRankingListSub);
    }

    private void setStartPos() {
        start += 20;
    }

    public void loadMoreListData() {
        loadRankList(type, start);
    }

    public String getShareRankUrl(){
        return mDataManager.getRankShareUrl(UserInfoManager.getInstance().getUserId(), "speaking");
    }
}
