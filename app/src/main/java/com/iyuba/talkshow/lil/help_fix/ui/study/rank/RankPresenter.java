package com.iyuba.talkshow.lil.help_fix.ui.study.rank;

import androidx.annotation.NonNull;

import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_rank;
import com.iyuba.talkshow.lil.help_mvp.mvp.BasePresenter;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/5/25 10:09
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RankPresenter extends BasePresenter<RankView> {

    //获取排行榜信息
    private Disposable publishRankDis;

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unDisposable(publishRankDis);
    }

    //获取排行榜信息
    public void getPublishRankData(String bookType,String voaId,int start,int total,String type,boolean isRefresh){
        checkViewAttach();
        RxUtil.unDisposable(publishRankDis);
        CommonDataManager.getEvalRankData(bookType,voaId, start, total, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Eval_rank>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        publishRankDis = d;
                    }

                    @Override
                    public void onNext(@NonNull Eval_rank bean) {
                        if (getMvpView()!=null){
                            getMvpView().showData(isRefresh,bean);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showData(isRefresh,null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
