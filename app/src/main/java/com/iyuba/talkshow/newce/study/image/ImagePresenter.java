package com.iyuba.talkshow.newce.study.image;

import android.util.Log;

import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.newce.study.read.ReadMvpView;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carl shen on 2021/10/29
 * New Primary English, new study experience.
 */
@ConfigPersistent
public class ImagePresenter extends BasePresenter<ReadMvpView> {

    private final DataManager mDataManager;

    private Subscription mGetVoaSub;
    private Subscription mSyncVoaSub;

    @Inject
    public ImagePresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetVoaSub);
        RxUtil.unsubscribe(mSyncVoaSub);
    }

    public void getVoaTexts(final int voaId) {
        Log.e(ImageFragment.TAG, "getVoaTexts ");
        checkViewAttached();
        RxUtil.unsubscribe(mGetVoaSub);
        mGetVoaSub = mDataManager.getVoaTexts(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VoaText>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e(ImageFragment.TAG, "getVoaTexts onError  " + e.getMessage());
                        }
//                        getMvpView().showEmptyTexts();
                        syncVoaTexts(voaId);
                    }

                    @Override
                    public void onNext(List<VoaText> voaTextList) {
                        if (voaTextList == null || voaTextList.isEmpty()) {
//                            getMvpView().showEmptyTexts();
                            syncVoaTexts(voaId);
                        } else {
                            getMvpView().showVoaTexts(voaTextList);
                        }
                    }
                });
    }

    public void syncVoaTexts(final int voaId) {
        Log.e(ImageFragment.TAG, "syncVoaTexts ");
        checkViewAttached();
        RxUtil.unsubscribe(mSyncVoaSub);
        mSyncVoaSub = mDataManager.syncVoaTexts(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VoaText>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e(ImageFragment.TAG, "syncVoaTexts onError  " + e.getMessage());
                        }
//                        getMvpView().showEmptyTexts();
                        getVoaTexts(voaId);
                    }

                    @Override
                    public void onNext(List<VoaText> voaTextList) {
                        Log.e(ImageFragment.TAG, "syncVoaTexts onNext ");
                        if (voaTextList == null || voaTextList.size() == 0) {
//                            getMvpView().showEmptyTexts();
                            getVoaTexts(voaId);
                        } else {
                            getMvpView().showVoaTexts(voaTextList);
                        }
                    }
                });
    }

}
