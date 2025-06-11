package com.iyuba.talkshow.ui.dubbing;

import android.content.Context;
import android.util.Log;
import android.view.ActionMode;

import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.dlex.bizs.DLTaskInfo;
import com.iyuba.dlex.interfaces.IDListener;
import com.iyuba.imooclib.data.model.Course;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Download;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.model.WordResponse;
import com.iyuba.talkshow.data.model.result.SendEvaluateResponse;
import com.iyuba.talkshow.data.remote.UploadStudyRecordService;
import com.iyuba.talkshow.event.DownloadEvent;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.TimeUtil;
import com.iyuba.talkshow.util.VoaMediaUtil;
import com.iyuba.talkshow.util.WavMergeUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@ConfigPersistent
public class DubbingPresenter extends BasePresenter<DubbingMvpView> {
    private static final long SHOW_PEROID = 500;

    private final DataManager mDataManager;
    private final DLManager mDLManager;
    private DLTaskInfo taska;
    private DLTaskInfo taskMedia;

    private Voa mVoa;
    private String mDir;
    private String mVideoUrl;
    private String mMediaUrl;

    private Subscription mGetVoaSub;
    private Subscription mSyncVoaSub;
    private Subscription mAddDownloadSub;
    private Subscription mSaveRecordSub;
    private Subscription mDeleteRecordSub;
    private Subscription mDelete1RecordSub;
    private Subscription mMergeRecordSub;
    private Subscription mSaveVoaSoundSub;

    private Timer mMsgTimer;
    private String mMsg;

    private final IDListener mVideoListener = new IDListener() {
        private int mFileLength = 0;

        @Override
        public void onPrepare() {

        }

        @Override
        public void onStart(String fileName, String realUrl, int fileLength) {
            this.mFileLength = fileLength;
        }

        @Override
        public void onProgress(int progress) {
            if (mFileLength != 0) {
                int percent = progress * 100 / mFileLength;
                if (getMvpView() != null) {
                    mMsg = MessageFormat.format(
                            ((Context) getMvpView()).getString(R.string.video_loading_tip), percent);
                }
            }
        }

        @Override
        public void onStop(int progress) {
            if (mMsgTimer != null) {
                mMsgTimer.cancel();
            }
        }

        @Override
        public void onFinish(File file) {
            StorageUtil.renameVideoFile(mDir, mVoa.voaId());
            if (StorageUtil.checkFileExist(mDir, mVoa.voaId())) {
                addDownload();
            }
            mMsgTimer.cancel();
            EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId()));
        }

        @Override
        public void onError(int status, String error) {
            mMsgTimer.cancel();
            EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId()));
        }
    };

    private final IDListener mMediaListener = new IDListener() {
        private int mFileLength = 0;

        @Override
        public void onPrepare() {

        }

        @Override
        public void onStart(String fileName, String realUrl, int fileLength) {
            this.mFileLength = fileLength;
        }

        @Override
        public void onProgress(int progress) {
            if (mFileLength != 0) {

                int percent = progress * 100 / mFileLength;
                if (getMvpView() != null) {
                    mMsg = MessageFormat.format(
                            ((Context) getMvpView()).getString(R.string.media_loading_tip), percent);
                }
            }
        }

        @Override
        public void onStop(int progress) {
            if (mMsgTimer != null) {
                mMsgTimer.cancel();
            }
        }

        @Override
        public void onFinish(File file) {
            StorageUtil.renameAudioFile(mDir, mVoa.voaId());
            if (StorageUtil.checkFileExist(mDir, mVoa.voaId())) {
                mMsgTimer.cancel();
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId()));
                addDownload();
            } else {
                //下载视频
                if (!StorageUtil.isVideoExist(mDir, mVoa.voaId())) {
                    downloadVideo();
                } else {
                    mMsgTimer.cancel();
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId()));
                }
            }
        }

        @Override
        public void onError(int status, String error) {
            if (getMvpView() != null) {
                mMsg = "音频资源下载错误，请稍后再试。";
            }
            //下载视频
            if (!StorageUtil.isVideoExist(mDir, mVoa.voaId())) {
                downloadVideo();
            } else {
                mMsgTimer.cancel();
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId()));
            }
        }
    };
    private Subscription mSearchSub;
    private Disposable mDeleteDisposable;
    private Disposable mInsertDisposable;

    @Inject
    public DubbingPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
        this.mDLManager = DLManager.getInstance();
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetVoaSub);
        RxUtil.unsubscribe(mSyncVoaSub);
        RxUtil.unsubscribe(mAddDownloadSub);
        RxUtil.unsubscribe(mSaveRecordSub);
        RxUtil.unsubscribe(mDeleteRecordSub);
        RxUtil.unsubscribe(mDelete1RecordSub);
    }

    public void init(Voa voa) {
        this.mVoa = voa;
        mDir = StorageUtil
                .getMediaDir(TalkShowApplication.getContext(), voa.voaId())
                .getAbsolutePath();
    }

    public void download() {
        mMsgTimer = new Timer();
        mMsgTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mMsg != null) {
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.DOWNLOADING, mMsg, mVoa.voaId()));
                }
            }
        }, 0, SHOW_PEROID);

        if (!StorageUtil.isAudioExist(mDir, mVoa.voaId())) {
            downloadMedia();
        } else if (!StorageUtil.isVideoExist(mDir, mVoa.voaId())) {
            downloadVideo();
        }
    }

    private void downloadVideo() {
        mVideoUrl = VoaMediaUtil.getVideoUrl(mVoa.video());
        if (UserInfoManager.getInstance().isVip()){
            mVideoUrl = VoaMediaUtil.getVideoVipUrl(mVoa.video());
        }
//        mDLManager.dlStart(mVideoUrl, mDir,
//                StorageUtil.getVideoTmpFilename(mVoa.voaId()), mVideoListener);
        Log.e("DubbingPresenter", " downloadVideo mVideoUrl " + mVideoUrl);
        DLTaskInfo task = mDLManager.getDLTaskInfo(mVideoUrl);
        if (task != null) {
            mDLManager.stopTask(task);
            mDLManager.cancelTask(task);
        }
        taska = new DLTaskInfo();
        taska.tag = mVideoUrl;
        taska.filePath = mDir;
        taska.fileName = StorageUtil.getVideoTmpFilename(mVoa.voaId());
        taska.category = Course.DOWNLOAD_VIDEO_CATEGORY;
        taska.initalizeUrl(mVideoUrl);
        taska.setDListener(mVideoListener);
        mDLManager.addDownloadTask(taska);
    }

    private void downloadMedia() {
        mMediaUrl = UserInfoManager.getInstance().isVip() ?
                VoaMediaUtil.getAudioVipUrl(mVoa.sound()) : VoaMediaUtil.getAudioUrl(mVoa.sound());
        Log.e("DubbingPresenter", " downloadMedia mMediaUrl " + mMediaUrl);
//        mDLManager.dlStart(mMediaUrl, mDir,
//                StorageUtil.getAudioTmpFilename(mVoa.voaId()), mMediaListener);
        DLTaskInfo task = mDLManager.getDLTaskInfo(mMediaUrl);
        if (task != null) {
            mDLManager.stopTask(task);
            mDLManager.cancelTask(task);
        }
        taskMedia = new DLTaskInfo();
        taskMedia.tag = mMediaUrl;
        taskMedia.filePath = mDir;
        taskMedia.fileName = StorageUtil.getAudioTmpFilename(mVoa.voaId());
        taskMedia.category = Course.DOWNLOAD_VIDEO_CATEGORY;
        taskMedia.initalizeUrl(mMediaUrl);
        taskMedia.setDListener(mMediaListener);
        mDLManager.addDownloadTask(taskMedia);
    }

    public void cancelDownload() {
        if (taskMedia != null) {
            mDLManager.cancelTask(taskMedia);
            taskMedia = null;
        }

        if (taska != null) {
            mDLManager.cancelTask(taska);
            taska = null;
        }
        if (mMsgTimer != null) {
            mMsgTimer.cancel();
        }
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
                            Log.e("DubbingPresenter", "getVoaTexts onError  " + e.getMessage());
                        }
                        syncVoaTexts(voaId);
                    }

                    @Override
                    public void onNext(List<VoaText> voaTextList) {
                        if ((voaTextList == null) || voaTextList.isEmpty()) {
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
                            Log.e("DubbingPresenter", "syncVoaTexts onError  " + e.getMessage());
                        }
                        if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(List<VoaText> voaTextList) {
                        if ((voaTextList == null) || voaTextList.isEmpty()) {
                            getMvpView().showEmptyTexts();
                        } else {
                            getMvpView().showVoaTexts(voaTextList);
                        }
                    }
                });
    }

    public boolean checkFileExist() {
        return StorageUtil.checkFileExist(mDir, mVoa.voaId());
    }

    public void addDownload() {
//        try {
//            checkViewAttached();
            RxUtil.unsubscribe(mAddDownloadSub);
            mAddDownloadSub = mDataManager.saveDownload(
                    Download.builder()
                            .setUid(UserInfoManager.getInstance().getUserId())
                            .setVoaId(mVoa.voaId())
                            .setDate(TimeUtil.getCurDate())
                            .build())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (e != null) {
                                e.printStackTrace();
                            }
                            getMvpView().showToast(R.string.database_error);
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            Log.e("DubbingPresenter", "addDownload onNext aBoolean  " + aBoolean);
                        }
                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public List<VoaSoundNew> getVoaSoundItemid(int itemid) {
        return mDataManager.getVoaSoundItemUid(UserInfoManager.getInstance().getUserId(), itemid);
    }

    public List<VoaSoundNew> getVoaSoundVoaId(int voaid) {
        return mDataManager.getVoaSoundVoaUid(UserInfoManager.getInstance().getUserId(), voaid);
    }

    void saveVoaSound(final VoaSoundNew record) {
//        Log.e("DubbingPresenter", " saveRecord ");
        checkViewAttached();
        RxUtil.unsubscribe(mSaveVoaSoundSub);
        mSaveVoaSoundSub = mDataManager.saveVoaSound(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
//                        Log.e("DubbingPresenter", " saveVoaSound onCompleted ");
                    }

                    @Override
                    public void onError(Throwable e) {
//                        Log.e("DubbingPresenter", " saveVoaSound onError " + e.getMessage());
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.e("DubbingPresenter", " saveVoaSound onNext " + aBoolean);
                        if (getMvpView() != null) {
                            getMvpView().dismissDubbingDialog();
                        }
                    }
                });
    }

    void saveRecord(final Record record) {
        checkViewAttached();
        RxUtil.unsubscribe(mDeleteRecordSub);
        mDeleteRecordSub = mDataManager.deleteRecord(record.timestamp())
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
                        checkViewAttached();
                        RxUtil.unsubscribe(mSaveRecordSub);
                        mSaveRecordSub = mDataManager.saveRecord(record)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<Boolean>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                        getMvpView().dismissDubbingDialog();
                                        getMvpView().showToast(R.string.database_error);
                                    }

                                    @Override
                                    public void onNext(Boolean aBoolean) {
                                        getMvpView().dismissDubbingDialog();
                                        ((BaseActivity) getMvpView()).finish();
                                    }
                                });
                    }
                });
    }

    public void deleteRecord(long timestamp) {
        checkViewAttached();
        RxUtil.unsubscribe(mDelete1RecordSub);
        mDelete1RecordSub = mDataManager.deleteRecord(timestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissDubbingDialog();
                        e.printStackTrace();
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        getMvpView().dismissDubbingDialog();
                        ((BaseActivity) getMvpView()).finish();
                    }
                });
    }

    int getFinishNum(int voaId, long timestamp) {
        return StorageUtil.getRecordNum((TalkShowApplication.getContext()), voaId, timestamp);
    }

    void merge(final int voaId, final long timestamp, final List<VoaText> voaTextList, final int duration) {
        checkViewAttached();
        RxUtil.unsubscribe(mMergeRecordSub);
        getMvpView().showMergeDialog();
        mMergeRecordSub =
                Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
                    WavMergeUtil.merge(TalkShowApplication.getContext(), voaId, timestamp,
                            voaTextList, duration);
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                getMvpView().dismissMergeDialog();
                                if (e != null) {
                                    e.printStackTrace();
                                }
                                getMvpView().showToast(R.string.dubbing_merge_failure);
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                getMvpView().dismissMergeDialog();
                                if (aBoolean) {
                                    getMvpView().startPreviewActivity();
                                    getMvpView().pause();
                                } else {
                                    getMvpView().showToast(R.string.dubbing_merge_failure);
                                }
                            }
                        });


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

    Observable<SendEvaluateResponse> uploadSentence(String sentence, int index, int newsid, int paraid, String type, String uid, File file) {
        checkViewAttached();
        RxUtil.unsubscribe(mMergeRecordSub);
        return mDataManager.uploadSentence(sentence, index, newsid, paraid, type, uid, file);
    }


    public void deleteWords(final int userId, List<String> words, final ActionMode mode) {
        com.iyuba.module.toolbox.RxUtil.dispose(mDeleteDisposable);
        mDeleteDisposable = mDataManager.deleteWords(userId, words)
                .compose(com.iyuba.module.toolbox.RxUtil.applySingleIoSchedulerWith(disposable -> {
                }))
                .subscribe(result -> {
                    if (isViewAttached()) {
                        if (!result) {
                            getMvpView().showToastShort("删除失败，请稍后重试!");
                            mode.finish();
                        }
                    }
                }, throwable -> {
                    Timber.e(throwable);
                    if (isViewAttached()) {
                        getMvpView().showToastShort("删除失败，请稍后重试!");
                        mode.finish();
                    }
                });
    }

    public void getNetworkInterpretation(String selectText) {
        if (mSearchSub != null)
            mSearchSub.unsubscribe();
        mSearchSub = mDataManager.getWordOnNet(selectText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WordResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(WordResponse wordBean) {
                        getMvpView().showWord(wordBean);
                    }
                });
    }

    public void insertWords(int userId, List<String> words) {
        com.iyuba.module.toolbox.RxUtil.dispose(mInsertDisposable);
        mInsertDisposable = mDataManager.insertWords(userId, words)
                .compose(com.iyuba.module.toolbox.RxUtil.applySingleIoScheduler())
                .subscribe(result -> {
                    if (isViewAttached()) {
                        if (result) {
                            getMvpView().showToastShort(R.string.play_ins_new_word_success);
                        } else {
                            getMvpView().showToastShort("添加生词未成功");
                        }
                    }
                }, throwable -> {
                    Timber.e(throwable);
                    if (isViewAttached()) {
                        getMvpView().showToastShort("添加生词未成功");
                    }
                });
    }
}
