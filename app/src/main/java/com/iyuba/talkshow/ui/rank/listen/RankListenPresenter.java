package com.iyuba.talkshow.ui.rank.listen;

import android.util.Log;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.RankListenBean;
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
 * Created by carl shen on 2021/7/26
 * New Primary English, new study experience.
 */
@ConfigPersistent
public class RankListenPresenter extends BasePresenter<RankListenView> {

    private final DataManager mDataManager;
    private Subscription mRankingListSub;

    @Inject
    public RankListenPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mRankingListSub);
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
                            Log.e("RankOralFragment", "loadRankList onError " + e.getMessage());
                        }
                        getMvpView().dismissLoadingDialog();
                        getMvpView().showToastShort(R.string.error_loading);
                    }

                    @Override
                    public void onNext(RankOralBean rankListenBean) {
                        getMvpView().dismissLoadingDialog();
                        if (rankListenBean == null) {
                            Log.e("RankOralFragment", "loadRankList onNext is null.");
                            return;
                        }
                    }
                });
    }
    public void loadListenRankList(String type, int topicId, final int start, int total) {
        getMvpView().showLoadingDialog();
        RxUtil.unsubscribe(mRankingListSub);
        mRankingListSub = mDataManager.getSumListenList(UserInfoManager.getInstance().getUserId(), type, start, total)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RankListenBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("RankOralFragment", "loadListenRankList onError " + e.getMessage());
                        }
                        getMvpView().dismissLoadingDialog();
                        getMvpView().showToastShort(R.string.error_loading);
                    }

                    @Override
                    public void onNext(RankListenBean rankingListBean) {
                        getMvpView().dismissLoadingDialog();
                        if (rankingListBean == null) {
                            Log.e("RankOralFragment", "loadListenRankList onNext is null.");
                            return;
                        }
                        if (start == 0) {
                            getMvpView().showUserInfo(rankingListBean);
                        }
                        int result = rankingListBean.result;
                        Log.e("RankOralFragment", "loadListenRankList onNext result " + result);
                        if (result > 0) {
                            if (start == 0) {
                                getMvpView().showRankingList(rankingListBean.data);
                            } else {
                                getMvpView().showMoreRankList(rankingListBean.data);
                            }
                        } else {
                            Log.e("RankOralFragment", "loadRankList onNext is empty.");
                        }
                    }
                });
    }

    public String getShareRankUrl(){
        return mDataManager.getRankShareUrl(UserInfoManager.getInstance().getUserId(), "listening");
    }
}
