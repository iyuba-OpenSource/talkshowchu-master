package com.iyuba.talkshow.newce.study.rank;

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
 * Created by carl shen on 2020/11/19
 * New Primary English, new study experience.
 */
@ConfigPersistent
public class EvalrankPresenter extends BasePresenter<EvalrankMvpView> {

    private final DataManager mDataManager;
    private final int total = 20;
    private int start = 0;
    private String type;

    private Subscription mRankingListSub;

    @Inject
    public EvalrankPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mRankingListSub);
    }

    public void loadDayEvalRank(int topicId) {
        start = 0;
        type = "D";
        loadEvalRankList(Constant.EVAL_TYPE, topicId, type, start);
//        loadEvalRankList("voa", 0, type, start);
    }
    public void loadEvalRankList(String topic, int topicId, String type, final int start) {
        getMvpView().showLoadingDialog();
        RxUtil.unsubscribe(mRankingListSub);
        mRankingListSub = mDataManager.getEvalRankList(UserInfoManager.getInstance().getUserId(), topic, topicId, type, start, total)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RankOralBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("EvalrankFragment", "loadEvalRankList onError  " + e.getMessage());
                        }
                        getMvpView().dismissLoadingDialog();
                        getMvpView().showToastShort(R.string.error_loading);
                    }

                    @Override
                    public void onNext(RankOralBean rankOralBean) {
                        getMvpView().dismissLoadingDialog();
                        if (rankOralBean != null) {
                            getMvpView().showUserInfo(rankOralBean);
                            if (rankOralBean.getData() != null) {
                                setStartPos();
                                if (start == 0) {
                                    getMvpView().showRankingList(rankOralBean.getData());
                                } else {
                                    getMvpView().showMoreRankList(rankOralBean.getData());
                                }
                            }
                        }
                    }
                });
    }

    public void loadMoreDayEvalRank(int topicId) {
        type = "D";
        loadEvalRankList(Constant.EVAL_TYPE, topicId, type, start);
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
                            Log.e("EvalrankFragment", "loadRankList onError  " + e.getMessage());
                        }
                        getMvpView().dismissLoadingDialog();
                        getMvpView().showToastShort(R.string.error_loading);
                    }

                    @Override
                    public void onNext(RankOralBean rankOralBean) {
                        getMvpView().dismissLoadingDialog();
                        if ((rankOralBean != null) && (rankOralBean.getData() != null)) {
                            setStartPos();
                            if (start == 0) {
                                getMvpView().showUserInfo(rankOralBean);
                                getMvpView().showRankingList(rankOralBean.getData());
                            } else {
                                getMvpView().showMoreRankList(rankOralBean.getData());
                            }
                        }
                    }
                });
    }

    private void setStartPos() {
        start += 20;
    }

    public void loadMoreListData() {
        loadRankList(type, start);
    }
}
