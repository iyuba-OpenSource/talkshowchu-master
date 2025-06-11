package com.iyuba.talkshow.ui.user.me.dubbing.unreleased;

import android.content.Context;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.StorageUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UnreleasedPresenter extends BasePresenter<UnreleasedMvpView> {
    private Subscription mGetUnreleasedSub;
    private Subscription mUnreleasedDraftSub;
    private Subscription mGetVoaSub;

    private final DataManager mDataManager;

    @Inject
    public UnreleasedPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetUnreleasedSub);
        RxUtil.unsubscribe(mUnreleasedDraftSub);
        RxUtil.unsubscribe(mGetVoaSub);
    }

    public void getUnreleasedData() {
        checkViewAttached();
        RxUtil.unsubscribe(mGetUnreleasedSub);
        mGetUnreleasedSub = mDataManager.getUnreleasedData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Record>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissLoadingLayout();
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(List<Record> records) {
                        getMvpView().dismissLoadingLayout();
                        if(records == null || records.size() == 0) {
                            getMvpView().setEmptyData();
                        } else {
                            getMvpView().setUnreleasedData(records);
                        }
                    }
                });
    }

    public void deleteUnreleasedData(final List<Record> records) {
        checkViewAttached();
        RxUtil.unsubscribe(mUnreleasedDraftSub);
        List<String> timestamps = getTimestamps(records);
        mUnreleasedDraftSub = mDataManager.deleteRecord(timestamps)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        getUnreleasedData();
                        for(Record record : records) {
                            StorageUtil.cleanRecordDir(((Context) getMvpView()),
                                    record.voaId(), record.timestamp());
                        }
                    }
                });
    }

    private List<String> getTimestamps(List<Record> records) {
        List<String> timestamps = new ArrayList<>();
        for(Record record : records) {
            timestamps.add(String.valueOf(record.timestamp()));
        }
        return timestamps;
    }

    public void getVoa(final Record record) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetVoaSub);
        mGetVoaSub = mDataManager.getVoaById(record.voaId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Voa>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Voa voa) {
                        getMvpView().startPreviewActivity(voa, record.timestamp());
                    }
                });
    }
}
