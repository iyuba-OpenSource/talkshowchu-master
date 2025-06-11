package com.iyuba.talkshow.newce.study.eval;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.remote.UploadStudyRecordService;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.StorageUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ConfigPersistent
public class EvalPresenter extends BasePresenter<EvalMvpView> {
    private final DataManager mDataManager;

    private Subscription mGetVoaSub;
    private Subscription mSyncVoaSub;
    private Subscription mSaveRecordSub;
    private Subscription mSaveVoaSoundSub;
    private Subscription mDelete1RecordSub;

    @Inject
    public EvalPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetVoaSub);
        RxUtil.unsubscribe(mSyncVoaSub);
        RxUtil.unsubscribe(mSaveRecordSub);
        RxUtil.unsubscribe(mSaveVoaSoundSub);
        RxUtil.unsubscribe(mDelete1RecordSub);
    }

    public void getVoaTexts(final int voaId) {
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
                            Log.e(EvalFragment.TAG, "getVoaTexts onError  " + e.getMessage());
                        }
                        syncVoaTexts(voaId);
                    }

                    @Override
                    public void onNext(List<VoaText> voaTextList) {
                        if (voaTextList == null || voaTextList.isEmpty()) {
                            syncVoaTexts(voaId);
                        } else {
                            getMvpView().showVoaTexts(voaTextList);
                        }
                    }
                });
    }

    public void syncVoaTexts(final int voaId) {
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
                            Log.e(EvalFragment.TAG, "syncVoaTexts onError  " + e.getMessage());
                        }
                        if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
                            getMvpView().showToast(R.string.please_check_network);
                        }
                        getMvpView().showEmptyTexts();
                    }

                    @Override
                    public void onNext(List<VoaText> voaTextList) {
                        if (voaTextList == null || voaTextList.size() == 0) {
                            getMvpView().showEmptyTexts();
                        } else {
                            getMvpView().showVoaTexts(voaTextList);
                        }
                    }
                });
    }

    void saveVoaSound(final VoaSoundNew record) {
        Log.e(EvalFragment.TAG, " saveRecord ");
//        checkViewAttached();
        RxUtil.unsubscribe(mSaveVoaSoundSub);
        mSaveVoaSoundSub = mDataManager.saveVoaSound(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.e(EvalFragment.TAG, " saveVoaSound onCompleted ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e(EvalFragment.TAG, " saveVoaSound onError " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.e(EvalFragment.TAG, " saveVoaSound onNext " + aBoolean);
                        RxUtil.unsubscribe(mSaveVoaSoundSub);
                    }
                });
    }

    public List<VoaSoundNew> getVoaSoundItemid(long itemid) {
        return mDataManager.getVoaSoundItemUid(UserInfoManager.getInstance().getUserId(), itemid);
    }

    public List<VoaSoundNew> getVoaSoundVoaId(int voaid) {
        return mDataManager.getVoaSoundVoaUid(UserInfoManager.getInstance().getUserId(), voaid);
    }

    public List<Record> getRecordByVoaId(int voaid) {
        return mDataManager.getRecordByVoaId(voaid);
    }

    void saveRecord(final Record record) {
        Log.e(EvalFragment.TAG, " saveRecord ");
        checkViewAttached();
        RxUtil.unsubscribe(mSaveRecordSub);
        mSaveRecordSub = mDataManager.saveRecord(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.e(EvalFragment.TAG, " saveRecord onCompleted ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(EvalFragment.TAG, " saveRecord onError " + e.getMessage());
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.e(EvalFragment.TAG, " saveRecord onNext " + aBoolean);
                        checkViewAttached();
                        RxUtil.unsubscribe(mSaveRecordSub);
                    }
                });
    }

    public void deleteRecord(long timestamp) {
        checkViewAttached();
        RxUtil.unsubscribe(mDelete1RecordSub);
        getMvpView().showLoadingDialog();
        mDelete1RecordSub = mDataManager.deleteRecord(timestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissLoadingDialog();
                        e.printStackTrace();
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        getMvpView().dismissLoadingDialog();
                        ((Fragment) getMvpView()).onDestroy();
                    }
                });
    }

    int getFinishNum(int voaId, long timestamp) {
        return StorageUtil.getRecordNum(TalkShowApplication.getInstance(), voaId, timestamp);
    }

    UploadStudyRecordService getUploadStudyRecordService() {
        return mDataManager.getUploadStudyRecordService();
    }

    /**
     * 如果存在草稿，取数据，读取分数
     */
    void checkDraftExist(long mTimeStamp) {
        mDataManager.getDraftRecord(mTimeStamp)
                .subscribe(new Subscriber<List<Record>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<Record> records) {
                        if (records != null && records.size() > 0) {
                            getMvpView().onDraftRecordExist(records.get(0));
                        }
                    }
                });
    }

}
