package com.iyuba.talkshow.ui.user.me.dubbing.draft;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.injection.PerFragment;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Administrator on 2017/1/13/013.
 */

@PerFragment
public class DraftPresenter extends BasePresenter<DraftMvpView> {

    private Subscription mGetDraftSub;
    private Subscription mDeleteDraftSub;
    private Subscription mGetVoaDub;

    private final DataManager mDataManager;

    @Inject
    public DraftPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetDraftSub);
        RxUtil.unsubscribe(mDeleteDraftSub);
        RxUtil.unsubscribe(mGetVoaDub);
    }

    public void getDraftData() {
        checkViewAttached();
        RxUtil.unsubscribe(mGetDraftSub);
        mGetDraftSub = mDataManager.getDraftRecord()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Record>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(List<Record> records) {
                        getMvpView().dismissLoadingLayout();
                        if(records == null || records.size() != 0) {
                            getMvpView().setDraftData(records);
                        } else {
                            getMvpView().setEmptyData();
                        }
                    }
                });
    }

    public void deleteDraftData(List<String> timestamps) {
        checkViewAttached();
        RxUtil.unsubscribe(mDeleteDraftSub);
        mDeleteDraftSub = mDataManager.deleteRecord(timestamps)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
//                        getMvpView().showToast("删除成功");

                        getDraftData();
                    }
                });
    }

    public void getVoa(int voaId, final long timestamp) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetVoaDub);
        mGetVoaDub = mDataManager.getVoaById(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Voa>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Voa voa) {
                        if(voa != null) {
                            getMvpView().startDubbingActivity(voa, timestamp);
                        } else {
                            getMvpView().showToast(R.string.data_deleted);
                        }
                    }
                });
    }
}
