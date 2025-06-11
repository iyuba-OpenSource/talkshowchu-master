package com.iyuba.talkshow.newce.me;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.ArticleRecord;
import com.iyuba.talkshow.data.model.Collect;
import com.iyuba.talkshow.data.model.EnterGroup;
import com.iyuba.talkshow.data.model.GetCollect;
import com.iyuba.talkshow.data.model.GetCollectResponse;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.data.model.SeriesResponse;
import com.iyuba.talkshow.data.model.StudyRecordResponse;
import com.iyuba.talkshow.data.model.StudyResponse;
import com.iyuba.talkshow.data.model.UpdateWordResponse;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaBookText;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.event.SyncDataEvent;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.ui.user.login.UidBean;
import com.iyuba.talkshow.util.LogUtil;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.VoaMediaUtil;
import com.iyuba.talkshow.util.ZipUtils;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.BookLevels;
import com.iyuba.wordtest.entity.NewBookLevels;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.manager.WordManager;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carl shen on 2020/9/18.
 */
@ConfigPersistent
public class MeFragPresenter extends BasePresenter<MeFragMvpView> {
    private final DataManager mDataManager;
    private final ConfigManager mConfigManager;
    private Subscription mGetCollectSub;
    private Subscription mSaveCollectSub;
    private Subscription mSyncStudySub;
    private Subscription mSyncVoaBookSub;
    private Subscription mLoadWordSub;
    private Subscription mLoginSub;
    private Subscription mClearUserSub;

    private Subscription mUidSub;//根据token获取uid
    private Subscription mUserinfoSub;//获取用户的信息

    @Inject
    public MeFragPresenter(ConfigManager configManager, DataManager dataManager) {
        this.mConfigManager = configManager;
        this.mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetCollectSub);
        RxUtil.unsubscribe(mSyncStudySub);
        RxUtil.unsubscribe(mSyncVoaBookSub);
        RxUtil.unsubscribe(mSaveCollectSub);
        RxUtil.unsubscribe(mLoadWordSub);
        RxUtil.unsubscribe(mLoginSub);
        RxUtil.unsubscribe(mClearUserSub);

        RxUtil.unsubscribe(mUidSub);
        RxUtil.unsubscribe(mUserinfoSub);
    }

    public String getUserImageUrl() {
        return UserInfoManager.getInstance().isLogin() ?
                Constant.Url.getMiddleUserImageUrl(
                        UserInfoManager.getInstance().getUserId(),
                        mConfigManager.getPhotoTimestamp()) : null;
    }

    public void enterGroup() {
        checkViewAttached();
        RxUtil.unsubscribe(mClearUserSub);
        mClearUserSub = mDataManager.enterGroup(UserInfoManager.getInstance().getUserId(), Constant.EVAL_TYPE)
                .compose(RxUtil.io2main())
                .subscribe(new Subscriber<EnterGroup>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("MeFragPresenter", "enterGroup onError  " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(EnterGroup items) {
                        if ((items != null) && (items.groupinfo != null)) {
                            int groupId = 0;
                            try {
                                groupId = Integer.parseInt(items.groupinfo.get(0).groupid);
                            } catch (Exception var) { }
                            Log.e("MeFragPresenter", "enterGroup groupId  " + groupId);
                        } else {
                            Log.e("MeFragPresenter", "enterGroup onNext null. ");
                        }
                    }
                });
    }


    /*public void registerToken(final String token, final String opTopken, String operator) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoginSub);
        mLoginSub = mDataManager.registerByMob(token, opTopken, operator)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RegisterMobResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("MeFragPresenter", "registerToken onError  " + e.getMessage());
                        }
                        getMvpView().goResultActivity(null);
                    }

                    @Override
                    public void onNext(RegisterMobResponse response) {
                        if (response == null) {
                            Log.e("MeFragPresenter", "registerToken onNext response is null.");
                            getMvpView().goResultActivity(null);
                            return;
                        }
                        Log.e("MeFragPresenter", "registerToken onNext isLogin " + response.isLogin);
                        if (1 == response.isLogin) {
                            getMvpView().showToastShort("您已经登录成功，可以进行学习了。");
                            // already login, need update user info
                            if (response.userinfo != null) {
                                mAccountManager.setUser(response.userinfo, "");
                                mAccountManager.saveUser();
                            } else {
                                Log.e("MeFragPresenter", "registerToken onNext response.userinfo is null.");
                            }
                            EventBus.getDefault().post(new LoginEvent());
                            getMvpView().goResultActivity(new LoginResult());
                        } else {
                            if (response.res != null) {
                                // register by this phone
                                RegisterMobResponse.MobBean mobBean = response.res;
                                LoginResult loginResult = new LoginResult();
                                loginResult.setPhone(mobBean.phone);
                                getMvpView().goResultActivity(loginResult);
                            } else {
                                Log.e("MeFragPresenter", "registerToken onNext response.res is null.");
                                getMvpView().goResultActivity(null);
                            }
                        }
                    }
                });
    }*/

    public void SyncStudyRecord() {
        checkViewAttached();
        RxUtil.unsubscribe(mSyncStudySub);
        mSyncStudySub = mDataManager.getStudyTestMode(UserInfoManager.getInstance().getUserId(), Constant.EVAL_TYPE,
                "1", "1000", "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StudyRecordResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissLoadingDialog();
                        if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
                            getMvpView().showToastShort(R.string.please_check_network);
                        } else {
                            getMvpView().showToastShort(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(StudyRecordResponse response) {
                        getMvpView().dismissLoadingDialog();
                        getMvpView().showToastShort("同步成功！");
                        if (response == null || response.getData() == null) {
                            Log.e("MeFragPresenter", "SyncStudyRecord onNext response is null. ");
                            return;
                        }
                        Log.e("MeFragPresenter", "SyncStudyRecord getData().size = " + response.getData().size());
                        TalkShowApplication.getSubHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                for (StudyResponse study : response.getData()) {
                                    List<VoaText> mTextList = mDataManager.getVoaTextbyVoaId(study.LessonId);
                                    if (mTextList == null || mTextList.size() < 1) {
                                        continue;
                                    }
                                    Log.e("MeFragPresenter", "SyncStudyRecord StudyResponse study.LessonId = " + study.LessonId);
//                                    Log.e("MeFragPresenter", "SyncStudyRecord StudyResponse study = " + study.toString());
                                    VoaText voaText = mTextList.get(0);
                                    VoaText voaText2 = mTextList.get(0);
                                    if (mTextList.size() > 1) {
                                        voaText2 = mTextList.get(mTextList.size() - 1);
                                    }
                                    int curTime = (int) voaText.timing();
                                    int endTime = (int) voaText2.endTiming();
                                    int percent = 0;
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        long date = sdf.parse(study.BeginTime).getTime();
                                        long date2 = sdf.parse(study.EndTime).getTime();
                                        percent = (int)(date2 - date)/(endTime - curTime);
                                        if (percent >= 100) {
                                            percent = 100;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    List<ArticleRecord> mArticleList = mDataManager.getArticleByUid(UserInfoManager.getInstance().getUserId(), study.LessonId);
                                    if (mArticleList != null && mArticleList.size() > 0) {
                                        for (ArticleRecord record: mArticleList) {
                                            if ((record.percent() + percent) <= 100) {
                                                percent += record.percent();
                                            }
                                        }
                                    }
                                    ArticleRecord newRecord = ArticleRecord.builder().setUid(UserInfoManager.getInstance().getUserId())
                                            .setVoa_id(study.LessonId)
                                            .setCurr_time(curTime)
                                            .setTotal_time(endTime - curTime)
                                            .setType(0).setIs_finish(study.EndFlg)
                                            .setPercent(percent)
                                            .build();
                                    boolean result = mDataManager.saveArticleRecordNew(newRecord);
                                    Log.e("MeFragPresenter", "SyncStudyRecord save = " + result);
                                }
                            }
                        });
                    }
                });
    }

    public void SyncVoaBook() {
        checkViewAttached();
        RxUtil.unsubscribe(mSyncVoaBookSub);
        mSyncVoaBookSub = mDataManager.syncVoaBook(mConfigManager.getCourseId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VoaBookText>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissLoadingDialog();
                        getMvpView().showToastShort("同步失败！");
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(List<VoaBookText> response) {
                        getMvpView().dismissLoadingDialog();
                        getMvpView().showToastShort("同步成功！");
                    }
                });
    }
    public void SyncVoaTextStudyRecord4Book(boolean study, boolean tips) {
        checkViewAttached();
        RxUtil.unsubscribe(mSyncVoaBookSub);
        String category = "";
        List<SeriesData> series = mDataManager.getSeriesId(mConfigManager.getCourseId());
        if ((series == null) || (series.size() < 1)) {
            if (study) {
                SyncStudyRecord();
            }
            return;
        }
        category = series.get(0).getCategory();
        Log.e("MeFragPresenter", "SyncVoaTextStudyRecord4Book for category " + category);
        mSyncVoaBookSub = mDataManager.syncVoaTexts4Book(category, mConfigManager.getCourseId(), UserInfoManager.getInstance().getUserId(), App.APP_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VoaBookText>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (study) {
                            SyncStudyRecord();
                        }
                        if (tips && getMvpView() != null) {
                            getMvpView().dismissLoadingDialog();
                            getMvpView().showToastShort("同步失败！");
                        }
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(List<VoaBookText> response) {
                        if (study) {
                            SyncStudyRecord();
                        }
                        if (tips && getMvpView() != null) {
                            getMvpView().dismissLoadingDialog();
                            getMvpView().showToastShort("同步成功！");
                        }
                    }
                });
    }
    public void getWordsById(int bookId) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoadWordSub);
        mLoadWordSub = mDataManager.getWordByBookId(bookId)
//                .compose(RxUtil.io2main())
                .subscribe(new Subscriber<UpdateWordResponse>() {
                    @Override
                    public void onCompleted() {
                        getSeriesList(mConfigManager.getCourseCategory() + "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("MeFragPresenter", "getWordsById onError  " + e.getMessage());
                        }
                        getSeriesList(mConfigManager.getCourseCategory() + "");
                    }

                    @Override
                    public void onNext(UpdateWordResponse respons) {
                        if (respons == null || respons.getData() == null) {
                            Log.e("MeFragPresenter", "getWordsById onNext response is null. ");
                            return;
                        }
                        Log.e("MeFragPresenter", "getWordsById onNext getBookVersion() " + respons.getBookVersion());
                        WordDataBase.getInstance(TalkShowApplication.getInstance()).getTalkShowWordsDao().insertWord(respons.getData());
                        if (WordManager.WordDataVersion == 2) {
                            NewBookLevels levels = WordDataBase.getInstance(TalkShowApplication.getInstance()).getNewBookLevelDao().getBookLevel(bookId, String.valueOf(UserInfoManager.getInstance().getUserId()));
                            if (levels == null) {
                                levels = new NewBookLevels(bookId, 0, 0, 0, String.valueOf(UserInfoManager.getInstance().getUserId()));
                                levels.version = respons.getBookVersion();
                                WordDataBase.getInstance(TalkShowApplication.getInstance()).getNewBookLevelDao().saveBookLevel(levels);
                            } else {
                                levels.version = respons.getBookVersion();
                                WordDataBase.getInstance(TalkShowApplication.getInstance()).getNewBookLevelDao().updateBookLevel(levels);
                            }
                            return;
                        }
                        BookLevels levels = WordDataBase.getInstance(TalkShowApplication.getInstance()).getBookLevelDao().getBookLevel(bookId);
                        if (levels == null) {
                            levels = new BookLevels(bookId, 0, 0, 0);
                            levels.version = respons.getBookVersion();
                            WordDataBase.getInstance(TalkShowApplication.getInstance()).getBookLevelDao().saveBookLevel(levels);
                        } else {
                            levels.version = respons.getBookVersion();
                            WordDataBase.getInstance(TalkShowApplication.getInstance()).getBookLevelDao().updateBookLevel(levels);
                        }
                    }
                });
    }
    public void getSeriesList(String catId) {
        Log.e("ChooseCoursePresenter", "getSeriesList catId " + catId);
        checkViewAttached();
        RxUtil.unsubscribe(mGetCollectSub);
        mGetCollectSub = mDataManager.getCategorySeriesList(UserInfoManager.getInstance().getUserId(), catId)
                .subscribe(new Subscriber<SeriesResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("ChooseCoursePresenter", "getSeriesList onError " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(SeriesResponse response) {
                        if (response == null || response.getData() == null) {
                            Log.e("ChooseCoursePresenter", "getSeriesList onNext response is null. ");
                            return;
                        }
                        for (SeriesData bean : response.getData()) {
                            mDataManager.insertSeriesDB(bean);
                        }
                    }
                });
    }

    public void SyncCollection() {
        checkViewAttached();
        RxUtil.unsubscribe(mGetCollectSub);
        mGetCollectSub = mDataManager.getCollectList(String.valueOf(UserInfoManager.getInstance().getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetCollectResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissLoadingDialog();
                        getMvpView().showToastShort("同步失败！");
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(GetCollectResponse response) {
                        getMvpView().dismissLoadingDialog();
                        getMvpView().showToastShort("同步成功！");
                        if ((response == null) || (response.data == null)) {
                            Log.e("MeFragPresenter", "GetCollect:total null.");
                            return;
                        }
                        Log.e("MeFragPresenter", "GetCollect:total = " + response.total);
                        TalkShowApplication.getSubHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                for (GetCollect getCollect: response.data) {
                                    if (getCollect.voaid > 0) {
                                        List<Collect> oldCollect = mDataManager.getCollect(UserInfoManager.getInstance().getUserId(), getCollect.voaid);
                                        if ((oldCollect == null) || oldCollect.size() < 1) {
                                            saveCollect(UserInfoManager.getInstance().getUserId(), getCollect.voaid, getCollect.CollectDate);
                                        }
                                        List<Voa> voaList = mDataManager.getVoaByVoaId(getCollect.voaid);
                                        if ((voaList == null) || voaList.size() < 1) {
//                                            long result = mDataManager.insertVoaDB(SyncPresenter.Collect2Voa(getCollect));
//                                            Log.e("MeFragPresenter", "GetCollect insertVoaDB result " + result);
                                        }
                                    }
                                }
                            }
                        });
                    }
                });
    }

    public void saveCollect(int uid, int voaId, String date) {
        checkViewAttached();
        RxUtil.unsubscribe(mSaveCollectSub);
        mSaveCollectSub = mDataManager.saveCollect(
                Collect.builder()
                        .setUid(uid)
                        .setVoaId(voaId)
                        .setDate(date)
                        .build())
                .compose(RxUtil.io2main())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("MeFragPresenter", "saveCollect onError " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.e("MeFragPresenter", "saveCollect onNext " + aBoolean);
                    }
                });
    }

    private void unZipDownFiles() {
        TalkShowApplication.getSubHandler().post(new Runnable() {
            @Override
            public void run() {
                String wordDirPath = com.iyuba.wordtest.utils.StorageUtil
                        .getWordZipDir(TalkShowApplication.getInstance())
                        .getAbsolutePath();
                File wordDir  = new File(wordDirPath);
                if (new File(wordDir,"words.zip").exists()) {
                    Log.e("MeFragPresenter", "unZipDownFiles:wordDirPath " + wordDirPath + "/words.zip");
                    try {
                        ZipUtils.unzipFile(wordDirPath + "/words.zip", wordDirPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("MeFragPresenter", "unZipDownFiles: not exist: " + wordDirPath + "/words.zip");
                }
                String mImageDirPath = StorageUtil.getMediaDir(TalkShowApplication.getContext(), book_id).getAbsolutePath();
                String mImageZipFile = mImageDirPath + "/" + book_id + Constant.Voa.ZIP_SUFFIX;
                if (new File(mImageZipFile).exists()) {
                    Log.e("MeFragPresenter", "unZipDownFiles:mImageZipFile " + mImageZipFile);
                    try {
                        ZipUtils.unzipFile(mImageZipFile, mImageDirPath.replace("/" + book_id, ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("MeFragPresenter", "unZipDownFiles:mImageZipFile no exist.");
                }
            }
        });
    }
    private final FileDownloadListener downloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            LogUtil.d("MeFragPresenter", "startFileDownload:pending soFarBytes = " + soFarBytes + ", totalBytes = " + totalBytes);
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            LogUtil.d("MeFragPresenter", "startFileDownload:connected soFarBytes = " + soFarBytes + ", totalBytes = " + totalBytes);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            LogUtil.d("MeFragPresenter", "startFileDownload:progress soFarBytes = " + soFarBytes + ", totalBytes = " + totalBytes);
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
            FileDownloadComplete++;
            LogUtil.d("MeFragPresenter", "----startFileDownload:blockComplete FileDownloadComplete " + FileDownloadComplete);
            if ((FileDownloadComplete + FileDownloadError) >= FileDownloadCount) {
                EventBus.getDefault().post(new SyncDataEvent(SyncDataEvent.Status.FINISH, "", 0));
                if (mMsgTimer != null) {
                    mMsgTimer.cancel();
                }
                unZipDownFiles();
            } else {
                String mMsg = (FileDownloadComplete + FileDownloadError) * 100 / FileDownloadCount + "";
                EventBus.getDefault().post(new SyncDataEvent(SyncDataEvent.Status.DOWNLOADING, mMsg, 1000));
            }
        }

        @Override
        protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
            LogUtil.d("MeFragPresenter", "startFileDownload:progress soFarBytes = " + soFarBytes + ", retryingTimes = " + retryingTimes);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            LogUtil.d("MeFragPresenter", "startFileDownload:completed" );
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            LogUtil.d("MeFragPresenter", "startFileDownload:paused soFarBytes = " + soFarBytes + ", totalBytes = " + totalBytes);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            if (task != null) {
                LogUtil.d("MeFragPresenter", "startFileDownload:getUrl  " + task.getUrl());
            }
            if (e != null) {
                LogUtil.d("MeFragPresenter", "startFileDownload:error  " + e.getMessage());
            }
            FileDownloadError++;
            if ((FileDownloadComplete + FileDownloadError) >= FileDownloadCount) {
                EventBus.getDefault().post(new SyncDataEvent(SyncDataEvent.Status.FINISH, "", 0));
                if (mMsgTimer != null) {
                    mMsgTimer.cancel();
                }
                unZipDownFiles();
                LogUtil.d("MeFragPresenter", "startFileDownload:FileDownloadError  " + FileDownloadError);
            } else {
                String mMsg = (FileDownloadComplete + FileDownloadError) * 100 / FileDownloadCount + "";
                EventBus.getDefault().post(new SyncDataEvent(SyncDataEvent.Status.DOWNLOADING, mMsg, 1000));
            }
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            LogUtil.d("MeFragPresenter", "startFileDownload:warn" );
        }
    };
    private Timer mMsgTimer;
    private FileDownloadQueueSet queueSet;
    private int FileDownloadCount = 0;
    private volatile int FileDownloadComplete = 0;
    private volatile int FileDownloadError = 0;
    private int book_id = App.getBookDefaultShowData().getBookId();
    public void startFileDownload(int bookId, boolean serialRbtn) {
        // init the download task
        queueSet = new FileDownloadQueueSet(downloadListener);
        final List<BaseDownloadTask> tasks = new ArrayList<>();
        addWordDownloadList(tasks);
        book_id = bookId;
//        List<TalkShowWords> sentenceSounds = WordDataBase.getInstance(TalkShowApplication.getInstance()).getTalkShowWordsDao().getVoasSentencdeAudioByBook(bookId);
//        addAudioImgDownloadList(tasks, sentenceSounds);
        if ((bookId < 388) || ((392 < bookId) && (bookId < 397)) || (bookId > 432)) {
            addImageDownloadList(tasks, bookId);
            List<String> videoUrls = WordDataBase.getInstance(TalkShowApplication.getInstance()).getTalkShowWordsDao().getAudioUrlsByBook(bookId);
            videoUrls.remove("");
            addVideoClipDownloadList(tasks, videoUrls);
            List<Voa> voaids = mDataManager.getVoaXiaoxueByBookId(bookId);
            addAudioDownloadList(tasks, voaids);
            addVideoDownloadList(tasks, voaids);
        }
        FileDownloadCount = tasks.size();
        FileDownloadComplete = 0;
        FileDownloadError = 0;
        Log.e("MeFragPresenter", "----------startFileDownload:FileDownloadCount " + FileDownloadCount);

        queueSet.disableCallbackProgressTimes(); // do not want each task's download progress's callback,
        // we just consider which task will completed. auto retry 1 time if download fail
        queueSet.setAutoRetryTimes(1);
        if (serialRbtn) {
            // start download in serial order
            queueSet.downloadSequentially(tasks);
        } else {
            // start parallel download
            queueSet.downloadTogether(tasks);
        }
        if (FileDownloadCount > 0) {
//            getMvpView().startDownload();
            EventBus.getDefault().post(new SyncDataEvent(SyncDataEvent.Status.START, bookId));
            queueSet.start();
            mMsgTimer = new Timer();
            mMsgTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new SyncDataEvent(SyncDataEvent.Status.ERROR));
                }
            }, 1000 * 60 * 3, 1000);
        } else {
//            getMvpView().showToastShort("您的资源已经下载完成，无需重复下载。");
            EventBus.getDefault().post(new SyncDataEvent(SyncDataEvent.Status.FINISH, "", 1));
        }
    }
    private void addWordDownloadList(List<BaseDownloadTask> tasks) {
        String wordDirPath = com.iyuba.wordtest.utils.StorageUtil
                .getWordZipDir(TalkShowApplication.getInstance())
                .getAbsolutePath();
        File wordDir  = new File(wordDirPath);
        if (!new File(wordDir,"words.zip").exists()) {
            Log.e("MeFragPresenter", "addWordDownloadList:wordDirPath " + wordDirPath + "/words.zip");
            tasks.add(FileDownloader.getImpl().create(Constant.Web.wordUrl).setTag(Constant.Web.wordUrl).setPath(wordDirPath + "/words.zip"));
        }
    }
    private void addAudioImgDownloadList(List<BaseDownloadTask> tasks, List<TalkShowWords> voas) {
        int index = 0;
        for (TalkShowWords sounds : voas) {
            String mDir = com.iyuba.wordtest.utils.StorageUtil
                    .getMediaDir(TalkShowApplication.getInstance(), sounds.book_id, sounds.unit_id)
                    .getAbsolutePath();
            String audioUrl = sounds.Sentence_audio;
            if (!StorageUtil.isAudioExist(mDir, sounds.position)) {
                boolean isSame = false;
                for (int i = 0; i < index; i++) {
                    if (voas.get(i).Sentence_audio.equals(audioUrl)) {
                        isSame = true;
                        break;
                    }
                }
                if (!isSame) {
                    Log.e("MeFragPresenter", "addAudioImgDownloadList:mDir " + mDir + "/" + StorageUtil.getAudioFilename(sounds.position));
                    tasks.add(FileDownloader.getImpl().create(audioUrl).setTag(audioUrl).setPath(mDir + "/" + StorageUtil.getAudioFilename(sounds.position)));
                }
            }
        }
    }
    private void addImageDownloadList(List<BaseDownloadTask> tasks, int book_id) {
        String imageHeaderUrl ="http://static2." + Constant.Web.WEB_SUFFIX + "images/words/zip/";
        String mPicUrl = imageHeaderUrl + book_id+Constant.Voa.ZIP_SUFFIX;

        String mImageDirPath = com.iyuba.wordtest.utils.StorageUtil
                .getMediaDir(TalkShowApplication.getInstance(), book_id)
                .getAbsolutePath();
        String mImageZipFile = mImageDirPath + "/" + book_id + Constant.Voa.ZIP_SUFFIX;
        if (!new File(mImageZipFile).exists()) {
            Log.e("MeFragPresenter", "addImageDownloadList:mImageDirPath " + mImageZipFile);
            tasks.add(FileDownloader.getImpl().create(mPicUrl).setTag(mPicUrl).setPath(mImageZipFile));
        }
    }
    private void addVideoClipDownloadList(List<BaseDownloadTask> tasks, List<String> urls) {
        for (String url : urls) {
            if (TextUtils.isEmpty(url) || !url.contains("_")) {
                continue;
            }
            String mDir = com.iyuba.wordtest.utils.StorageUtil
                    .getMediaDir(TalkShowApplication.getInstance(), url)
                    .getAbsolutePath();
            if (!com.iyuba.wordtest.utils.StorageUtil.isVideoClipExist(mDir, url)) {
                Log.e("MeFragPresenter", "addVideoClipDownloadList:mDir " + mDir + "/" + StorageUtil.getVideoClipFilename(url));
                tasks.add(FileDownloader.getImpl().create(url).setTag(url).setPath(mDir + "/" + StorageUtil.getVideoClipFilename(url)));
            }
        }
    }
    private void addAudioDownloadList(List<BaseDownloadTask> tasks, List<Voa> voas) {
        for (Voa mVoa : voas) {
            int voaId = mVoa.voaId();
            if (voaId < 1) {
                continue;
            }
            String mDir = com.iyuba.wordtest.utils.StorageUtil
                    .getMediaDir(TalkShowApplication.getInstance(), voaId)
                    .getAbsolutePath();
            String audioUrl = Constant.getSoundMp3Url(mVoa.sound(), mVoa.voaId());
            if (!StorageUtil.isAudioExist(mDir, voaId)) {
                Log.e("MeFragPresenter", "addAudioDownloadList:mDir " + mDir + "/" + StorageUtil.getAudioFilename(voaId));
                tasks.add(FileDownloader.getImpl().create(audioUrl).setTag(audioUrl).setPath(mDir + "/" + StorageUtil.getAudioFilename(voaId)));
            }
        }
    }
    private void addVideoDownloadList(List<BaseDownloadTask> tasks, List<Voa> voas) {
        for (Voa mVoa : voas) {
            int voaId = mVoa.voaId();
            if (voaId < 1) {
                continue;
            }
            String mDir = com.iyuba.wordtest.utils.StorageUtil
                    .getMediaDir(TalkShowApplication.getInstance(), voaId)
                    .getAbsolutePath();
            String mVideoUrl = VoaMediaUtil.getVideoUrl(mVoa.video());
            if (UserInfoManager.getInstance().isVip()){
                mVideoUrl = VoaMediaUtil.getVideoVipUrl(mVoa.video());
            }

            if (!StorageUtil.isVideoExist(mDir, voaId)) {
                Log.e("MeFragPresenter", "addVideoDownloadList:mDir " + mDir + "/" + StorageUtil.getVideoFilename(voaId));
                tasks.add(FileDownloader.getImpl().create(mVideoUrl).setTag(mVideoUrl).setPath(mDir + "/" + StorageUtil.getVideoFilename(voaId)));
            }
        }
    }

    public void cancelDownload() {
        if (mMsgTimer != null) {
            mMsgTimer.cancel();
        }
        FileDownloader.getImpl().pause(downloadListener);
        unZipDownFiles();
    }

    //根据token获取uid
    public void getUidByToken(String token){
        checkViewAttached();
        RxUtil.unsubscribe(mUidSub);
        mUidSub = mDataManager.getUid(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UidBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showToastShort(TalkShowApplication.getInstance().getResources().getString(R.string.wxSmallLoginFail));
                        }
                    }

                    @Override
                    public void onNext(UidBean uidBean) {
                        if (getMvpView()!=null){
                            if (uidBean.getResult() == 200){
                                getUserInfo(uidBean.getUid());
                            }else {
                                getMvpView().showToastShort(TalkShowApplication.getInstance().getResources().getString(R.string.wxSmallLoginFail));
                            }
                        }
                    }
                });
    }

    //获取用户的信息
    public void getUserInfo(int uid){
        /*checkViewAttached();
        RxUtil.unsubscribe(mUserinfoSub);
        mUserinfoSub = mDataManager.getUserInfo(uid,uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showToastShort(TalkShowApplication.getInstance().getResources().getString(R.string.wxSmallLoginFail));
                        }
                    }

                    @Override
                    public void onNext(UserData useresult) {
                        if (getMvpView()!=null){

                            if (useresult!=null&&!TextUtils.isEmpty(useresult.username)){
                                User theuser = new User();
                                theuser.setUid(uid);
                                theuser.setUsername(useresult.username);
                                theuser.setNickname(useresult.nickname);
                                theuser.setAmount(useresult.amount);
                                theuser.setEmail(useresult.email);
                                theuser.setPhone(useresult.mobile);
                                theuser.setImgSrc(useresult.middle_url);
                                theuser.setVipStatus(Integer.parseInt(useresult.vipStatus));
                                theuser.setExpireTime(TimeUtil.tansDateFrom1970(useresult.expireTime * 1000));
                                mAccountManager.setUser(theuser);
                                mAccountManager.saveUser(theuser);

                                //刷新界面
                                EventBus.getDefault().post(new LoginEvent());
                            }else {
                                getMvpView().showToastShort(TalkShowApplication.getInstance().getResources().getString(R.string.wxSmallLoginFail));
                            }
                        }
                    }
                });*/

        UserInfoManager.getInstance().getRemoteUserInfo(uid, new UserinfoCallbackListener() {
            @Override
            public void onSuccess() {
                ((Activity)getMvpView()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //刷新界面
                        EventBus.getDefault().post(new LoginEvent());
                    }
                });
            }

            @Override
            public void onFail(String errorMsg) {
                ((Activity)getMvpView()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getMvpView().showToastShort(TalkShowApplication.getInstance().getResources().getString(R.string.wxSmallLoginFail));
                    }
                });
            }
        });
    }
}
