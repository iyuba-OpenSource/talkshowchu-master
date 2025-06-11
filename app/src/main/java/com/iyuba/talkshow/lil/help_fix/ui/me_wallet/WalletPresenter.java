package com.iyuba.talkshow.lil.help_fix.ui.me_wallet;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_data;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Reward_history;
import com.iyuba.talkshow.lil.help_mvp.mvp.BasePresenter;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/8/23 09:08
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WalletPresenter extends BasePresenter<WalletView> {

    //获取奖励历史信息
    private Disposable getRewardDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(getRewardDis);
    }

    //获取奖励历史记录
    public void getRewardData(int uid,int pages,int pageCount){
        checkViewAttach();
        RxUtil.unDisposable(getRewardDis);
        CommonDataManager.getRewardHistory(uid, pages, pageCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<List<Reward_history>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getRewardDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<List<Reward_history>> bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.getResult().equals("200")){
                                getMvpView().showRewardHistory(bean.getData());
                            }else {
                                getMvpView().showRewardHistory(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showRewardHistory(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
