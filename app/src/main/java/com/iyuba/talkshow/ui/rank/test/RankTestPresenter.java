package com.iyuba.talkshow.ui.rank.test;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.RankTestBean;
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
 * @date: 2023/2/9 17:39
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
@ConfigPersistent
public class RankTestPresenter extends BasePresenter<RankTestMvpView> {

    private final DataManager dataManager;
    private Subscription mTestSub;

    @Inject
    public RankTestPresenter(DataManager dataManager){
        this.dataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unsubscribe(mTestSub);
    }

    //获取测试数据
    public void loadRankTestData(String type,int start,int total){
        checkViewAttached();
        RxUtil.unsubscribe(mTestSub);
        mTestSub = dataManager.getRankTestList(UserInfoManager.getInstance().getUserId(), type,start,total)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RankTestBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissLoadingDialog();
                        getMvpView().showToastShort(R.string.error_loading);
                    }

                    @Override
                    public void onNext(RankTestBean rankTestBean) {
                        getMvpView().dismissLoadingDialog();
                        if (rankTestBean==null){
                            return;
                        }

                        if (start==0){
                            getMvpView().showUserInfo(rankTestBean);
                        }

                        int result = rankTestBean.getResult();
                        if (result>0){
                            if (start==0){
                                getMvpView().showRankingList(rankTestBean.getData());
                            }else {
                                getMvpView().showMoreRankList(rankTestBean.getData());
                            }
                        }
                    }
                });
    }

    public String getShareRankUrl(){
        return dataManager.getRankShareUrl(UserInfoManager.getInstance().getUserId(), "testing");
    }
}
