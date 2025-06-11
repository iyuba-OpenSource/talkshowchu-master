package com.iyuba.talkshow.newce.study.dubbingNew;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.dlex.bizs.DLTaskInfo;
import com.iyuba.dlex.interfaces.IDListener;
import com.iyuba.imooclib.data.model.Course;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Download;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.TitleSeries;
import com.iyuba.talkshow.data.model.TitleSeriesResponse;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.model.WordResponse;
import com.iyuba.talkshow.data.model.result.SendEvaluateResponse;
import com.iyuba.talkshow.data.remote.IntegralService;
import com.iyuba.talkshow.data.remote.UploadStudyRecordService;
import com.iyuba.talkshow.event.DownloadEvent;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.help_mvp.util.DateUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newce.MainFragPresenter;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.OkhttpUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.TimeUtil;
import com.iyuba.talkshow.util.VoaMediaUtil;
import com.iyuba.talkshow.util.WavMergeUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @desction:
 * @date: 2023/2/15 10:44
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
@ConfigPersistent
public class DubbingNewPresenter extends BasePresenter<DubbingNewMvpView> {
    private static final long SHOW_PEROID = 500;

    private DataManager dataManager;
    private DLManager mDLManager;
    private DLTaskInfo taska;
    private DLTaskInfo taskMedia;

    private Voa mVoa;
    ///storage/emulated/0/Android/data/com.iyuba.talkshow.childenglish/files/313002
    private String mDir;
    private String mVideoUrl;
    private String mMediaUrl;

    private Timer mMsgTimer;
    private String mMsg;

    private Subscription mAddDownloadSub;
    private Subscription mMergeRecordSub;
    private Subscription mSaveVoaSoundSub;
    private Subscription mGetVoaSub;
    private Subscription mSyncVoaSub;
    private Subscription mDeleteRecordSub;
    private Subscription mSaveRecordSub;
    private Disposable mInsertDisposable;
    private Subscription mLoadNewSub;
    private Subscription mSearchSub;

    @Inject
    public DubbingNewPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
        this.mDLManager = DLManager.getInstance();
    }

    @Override
    public void detachView() {
        super.detachView();

        //取消文件下载
        if (fileCall != null) {
            fileCall.cancel();
        }

        RxUtil.unsubscribe(mAddDownloadSub);
        RxUtil.unsubscribe(mMergeRecordSub);
        RxUtil.unsubscribe(mSaveVoaSoundSub);
        RxUtil.unsubscribe(mGetVoaSub);
        RxUtil.unsubscribe(mSyncVoaSub);
        RxUtil.unsubscribe(mDeleteRecordSub);
        RxUtil.unsubscribe(mSaveRecordSub);
        com.iyuba.module.toolbox.RxUtil.dispose(mInsertDisposable);
        RxUtil.unsubscribe(mSearchSub);
    }

    /******下载******/
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
//        mVideoUrl =
//                VoaMediaUtil.getVideoUrl(mVoa.category(), mVoa.voaId());
        // TODO: 2022/7/13 这里优化下，不再使用上面的接口，改为新的接口
        mVideoUrl = VoaMediaUtil.getVideoUrl(mVoa.video());
        if (UserInfoManager.getInstance().isVip()) {
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

        //取消文件下载
        if (fileCall != null) {
            fileCall.cancel();
        }
    }

    private IDListener mVideoListener = new IDListener() {
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
                            TalkShowApplication.getContext().getString(R.string.video_loading_tip), percent);
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

    private IDListener mMediaListener = new IDListener() {
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
                            TalkShowApplication.getContext().getString(R.string.media_loading_tip), percent);
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

    public void addDownload() {
//        try {
//            checkViewAttached();
        RxUtil.unsubscribe(mAddDownloadSub);
        mAddDownloadSub = dataManager.saveDownload(
                Download.builder()
                        .setUid(UserInfoManager.getInstance().getUserId())
                        .setVoaId(mVoa.voaId())
                        .setDate(DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMDHMS))
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

    public boolean checkFileExist() {
        return StorageUtil.checkFileExist(mDir, mVoa.voaId());
    }

    // 是否是试听的课程（非会员也可以进入的）
    public boolean isTrial(Voa voa) {
        if (voa == null) return true;
        return dataManager.isTrial(voa);
    }

    public void init(Voa voa) {
        this.mVoa = voa;
        mDir = StorageUtil
                .getMediaDir(TalkShowApplication.getContext(), voa.voaId())
                .getAbsolutePath();

        //这里增加一个文件夹操作
        //因为这里之前下载的音频文件和原文中的音频文件为同一个路径，但是原文中的音频有声音，这里下载的没有，因此会出现原文播放出现错误的情况
        mDir = mDir+"/dubbing";
    }

    UploadStudyRecordService getUploadStudyRecordService() {
        return dataManager.getUploadStudyRecordService();
    }

    /******数据******/
    public List<VoaSoundNew> getVoaSoundVoaId(int voaid) {
        return dataManager.getVoaSoundVoaUid(UserInfoManager.getInstance().getUserId(), voaid);
    }

    /**
     * 如果存在草稿，取数据，读取分数
     */
    void checkDraftExist(long mTimeStamp) {
        dataManager.getDraftRecord(mTimeStamp)
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
        return dataManager.uploadSentence(sentence, index, newsid, paraid, type, uid, file);
    }

    public void getVoaTexts(final int voaId) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetVoaSub);
        mGetVoaSub = dataManager.getVoaTexts(voaId)
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
        mSyncVoaSub = dataManager.syncVoaTexts(voaId)
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

    public List<VoaSoundNew> getVoaSoundItemid(long itemid) {
        return dataManager.getVoaSoundItemUid(UserInfoManager.getInstance().getUserId(), itemid);
    }

    public void getNetworkInterpretation(String selectText) {
        checkViewAttached();
        RxUtil.unsubscribe(mSearchSub);
        mSearchSub = dataManager.getWordOnNet(selectText)
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
        mInsertDisposable = dataManager.insertWords(userId, words)
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

    /******音视频******/
    void saveVoaSound(final VoaSoundNew record) {
//        Log.e("DubbingPresenter", " saveRecord ");
        checkViewAttached();
        RxUtil.unsubscribe(mSaveVoaSoundSub);
        mSaveVoaSoundSub = dataManager.saveVoaSound(record)
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

    /******录音******/
    void saveRecord(final Record record) {
        checkViewAttached();
        RxUtil.unsubscribe(mDeleteRecordSub);
        mDeleteRecordSub = dataManager.deleteRecord(record.timestamp())
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
                        mSaveRecordSub = dataManager.saveRecord(record)
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

    /******其他功能*****/
    //分享
    public IntegralService getInregralService() {
        return dataManager.getIntegralService();
    }

    //更新原文
    public void getVoaSeries(String series) {
        Log.e("DetailPresenter", "getVoaSeries " + series);
        checkViewAttached();
        RxUtil.unsubscribe(mLoadNewSub);
        mLoadNewSub = dataManager.getTitleSeriesList(series, UserInfoManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TitleSeriesResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("DetailPresenter", "getVoaSeries onError  " + e.getMessage());
                        }
                        getMvpView().showToast("更新失败");
                    }

                    @Override
                    public void onNext(TitleSeriesResponse response) {
                        getMvpView().showToast("更新成功");
                        if (response == null || response.getData() == null) {
                            Log.e("DetailPresenter", "getVoaSeries onNext response is null. ");
                            return;
                        }
                        Log.e("DetailPresenter", "getVoaSeries onNext getTotal " + response.getTotal());
                        List<TitleSeries> seriesData = response.getData();
                        List<Voa> voaData = new ArrayList<>();
                        for (TitleSeries series : seriesData) {
                            try {
                                voaData.add(MainFragPresenter.Series2Voa(series));
                            } catch (Exception var2) {
                                Log.e("DetailPresenter", "getVoaSeries onNext id " + series.Id);
                                var2.printStackTrace();
                            }
                        }
                        Log.e("DetailPresenter", "getVoaSeries onNext voaData " + voaData.size());
                        TalkShowApplication.getSubHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                for (Voa series : voaData) {
                                    long result = dataManager.insertVoaDB(series);
                                    Log.e("DetailPresenter", "getVoaSeries insertVoaDB result " + result);
                                }
                            }
                        });
                    }
                });
    }

    /******保存到相册******/
    //判断相册中有无视频
    public boolean checkAlbumVideoExist(String fileName) {
        Cursor cursor = TalkShowApplication.getContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Media._ID}, MediaStore.Video.Media.DISPLAY_NAME + " = '" + fileName + "'", null, null);
        boolean result = false;
        if (cursor != null) {
            result = cursor.getCount() > 0;
            cursor.close();
        }
        return result;
    }

    //将本地视频导入到相册
    private void importLocalVideoToAlbum(String localPath) {
        if (TextUtils.isEmpty(localPath)) {
            return;
        }

        File localVideoFile = new File(localPath);
        if (!localVideoFile.exists()) {
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, localVideoFile.getName());
        contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        } else {
            contentValues.put(MediaStore.Video.Media.DATA, Environment.getExternalStorageDirectory().getPath() + File.separator + Environment.DIRECTORY_DCIM + File.separator + localVideoFile.getName());
        }

        Uri fileUri = TalkShowApplication.getContext().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (fileUri != null) {
            try {
                ParcelFileDescriptor pfd = TalkShowApplication.getContext().getContentResolver().openFileDescriptor(fileUri, "w");
                FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());

                FileInputStream fis = new FileInputStream(localPath);
                byte[] bytes = new byte[1024];
                int len = 0;

                while ((len = fis.read(bytes)) != -1) {
                    fos.write(bytes, 0, len);
                }
                fos.close();
                fis.close();

                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId()));
            } catch (Exception e) {
                e.printStackTrace();
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.ERROR, mVoa.voaId()));
            }
        }
    }

    //下载本地视频并且导入到相册
    public void downVideoAndImportAlbum(String videoUrl, String localPath) {
        //获取本地视频和相册视频路径
        File localFile = new File(localPath);

        //1.先判断相册中有无视频
        //2.判断本地有无视频，有则直接导入到相册中
        //3.都没有视频则直接下载
        if (checkAlbumVideoExist(localFile.getName())) {
            EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, 1002));
            Log.d("下载测试", "本地存在");
            return;
        }

        if (new File(localPath).exists()) {
            try {
                localFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("下载测试", "删除本地文件");
        }

        downloadAlbumVideo(videoUrl, localPath);
    }

    //可中断的call
    private Call fileCall = null;
    //下载文件
    private long progress = 0;

    public void downloadAlbumVideo(String videoUrl, String localPath) {
        fileCall = null;
        progress = 0;

        //这里判断file存在并且创建file
        try {
            File localFile = new File(localPath);
            if (!localFile.exists()) {
                if (!localFile.getParentFile().exists()) {
                    localFile.getParentFile().mkdirs();
                }
                localFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        OkhttpUtil.downloadFile(videoUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!call.isCanceled()) {
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.ERROR, 1002));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response == null || response.body().byteStream() == null) {
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.ERROR, 1002));
                    return;
                }

                try {
                    fileCall = call;

                    long max = response.body().contentLength();
                    InputStream is = response.body().byteStream();
                    FileOutputStream fos = new FileOutputStream(localPath);
                    byte[] bytes = new byte[4096];
                    int len = 0;

                    while ((len = is.read(bytes)) != -1) {
                        fos.write(bytes, 0, len);

                        progress += len;
                        int percent = (int) (progress * 100 / max);
                        if (getMvpView() != null) {
                            mMsg = MessageFormat.format(TalkShowApplication.getContext().getString(R.string.saveAlbum_loading_tip), percent);
                        }
                        if (mMsg != null) {
                            EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.DOWNLOADING, mMsg, mVoa.voaId()));
                        }
                    }

                    fos.flush();
                    fos.close();
                    importLocalVideoToAlbum(localPath);
                } catch (IOException e) {
                    if (!call.isCanceled()) {
                        EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.ERROR, 1002));
                    }
                }
            }
        });
    }
}
