package com.iyuba.talkshow.ui.rank.study;

import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.RankListenBean;
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
 * @desction:
 * @date: 2023/2/9 15:51
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
@ConfigPersistent
public class RankStudyPresenter extends BasePresenter<RankStudyMvpView> {

    private final DataManager dataManager;
    private Subscription mStudySub;

    @Inject
    public RankStudyPresenter(DataManager dataManager){
        this.dataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unsubscribe(mStudySub);
    }

    public void loadRankStudyList(String type,final int start, int total) {
        getMvpView().showLoadingDialog();
        RxUtil.unsubscribe(mStudySub);
        mStudySub = dataManager.getSumStudyList(UserInfoManager.getInstance().getUserId(), type, start, total)
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
        return dataManager.getRankShareUrl(UserInfoManager.getInstance().getUserId(), "studying");
    }
}
