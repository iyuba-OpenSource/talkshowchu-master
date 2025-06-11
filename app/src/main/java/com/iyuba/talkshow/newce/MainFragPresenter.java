package com.iyuba.talkshow.newce;

import android.util.Log;

import com.iyuba.imooclib.data.local.IMoocDBManager;
import com.iyuba.imooclib.data.model.StudyProgress;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.manager.VersionManager;
import com.iyuba.talkshow.data.model.ArticleRecord;
import com.iyuba.talkshow.data.model.CategoryFooter;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.data.model.StudyRecordResponse;
import com.iyuba.talkshow.data.model.StudyResponse;
import com.iyuba.talkshow.data.model.TitleSeries;
import com.iyuba.talkshow.data.model.TitleSeriesResponse;
import com.iyuba.talkshow.data.model.UpdateWordResponse;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.remote.UploadStudyRecordService;
import com.iyuba.talkshow.event.ReadEvent;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.help_fix.util.FixUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newce.study.StudyActivity;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.GetLocation;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.BookLevels;
import com.iyuba.wordtest.entity.NewBookLevels;
import com.iyuba.wordtest.manager.WordManager;

import org.greenrobot.eventbus.EventBus;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carl shen on 2020/7/29
 * New Primary English, new study experience.
 */
@ConfigPersistent
public class MainFragPresenter extends BasePresenter<MainFragMvpView> {

    private final DataManager mDataManager;
    private final VersionManager mVersionManager;
    private final ConfigManager mConfigManager;
    private final GetLocation mGetLocation;

    private Subscription mLoadNewSub;
    private Subscription mLoadSub;
    private Subscription mLoadLoopSub;
    private Subscription mLoginSub;
    private Subscription mGetVoaByIdSub;
    private Subscription mGetMoreVoaSub;
    private Subscription mLoadSeriesSub;
    private Subscription mLoadWordSub;
    private Subscription mSyncMicroSub;

    @Inject
    public MainFragPresenter(DataManager dataManager, ConfigManager configManager,
                             GetLocation getLocation, VersionManager versionManager) {
        mDataManager = dataManager;
        mConfigManager = configManager;
        mGetLocation = getLocation;
        mVersionManager = versionManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mLoginSub);
        RxUtil.unsubscribe(mLoadNewSub);
        RxUtil.unsubscribe(mLoadSub);
        RxUtil.unsubscribe(mLoadLoopSub);
        RxUtil.unsubscribe(mGetVoaByIdSub);
        RxUtil.unsubscribe(mGetMoreVoaSub);
        RxUtil.unsubscribe(mLoadSeriesSub);
        RxUtil.unsubscribe(mLoadWordSub);
        RxUtil.unsubscribe(mSyncMicroSub);
    }
    public int getCourseCategory() {
        return mConfigManager.getCourseCategory();
    }
    public int getUnitId4Voa(Voa voa) {
        return mDataManager.getUnitId4Voa(voa);
    }

    public void loadMoreVoas(final CategoryFooter category, int limit, String ids) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetMoreVoaSub);
        List<Voa> voas = new ArrayList<>();
        mGetMoreVoaSub = mDataManager.getVoasByCategoryNotWith(category.getCategory(), limit, ids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Voa>() {
                    @Override
                    public void onCompleted() {
                        if (voas.isEmpty()) {
                            getMvpView().showToast(R.string.no_data);
                        } else {
                            getMvpView().showVoasByCategory(voas, category);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Voa voa) {
                        voas.add(voa);
                    }
                });
    }

    public void loadVoas(int bookId) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoadSub);
        Log.e("MainFragPresenter", "loadVoas bookId " + bookId);
        mLoadSub = mDataManager.getXiaoxueByBookId(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Voa>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("MainFragPresenter", "loadMoreVoas onError " + e.getMessage());
                        }
                        getMvpView().showVoas(null);
//                        if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
//                            getMvpView().showToast(R.string.please_check_network);
//                        }
                    }

                    @Override
                    public void onNext(List<Voa> voas) {
                        //没有数据的话，需要下拉刷新数据显示
                        if (voas==null ||voas.size()==0){
                            getMvpView().refreshNetVoaData();
                            return;
                        }

                        getMvpView().showVoas(voas);
                    }
                });
    }

    public void getWordsById(int bookId) {
        RxUtil.unsubscribe(mLoadWordSub);
        mLoadWordSub = mDataManager.getWordByBookId(bookId)
                .subscribe(new Subscriber<UpdateWordResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("MainFragPresenter", "getWordsById onError  " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(UpdateWordResponse respons) {
                        if (respons == null || respons.getData() == null) {
                            Log.e("MainFragPresenter", "getWordsById onNext response is null. ");
                            return;
                        }
                        WordDataBase.getInstance(TalkShowApplication.getContext()).getTalkShowWordsDao().insertWord(respons.getData());
                        if (WordManager.WordDataVersion == 2) {
                            NewBookLevels newLevels = WordDataBase.getInstance(TalkShowApplication.getContext()).getNewBookLevelDao().getBookLevel(bookId,String.valueOf(UserInfoManager.getInstance().getUserId()));
                            if (newLevels == null) {
                                newLevels = new NewBookLevels(bookId, 0, 0, 0, String.valueOf(UserInfoManager.getInstance().getUserId()));
                                newLevels.version = respons.getBookVersion();
                                WordDataBase.getInstance(TalkShowApplication.getContext()).getNewBookLevelDao().saveBookLevel(newLevels);
                            }
                            EventBus.getDefault().post(new ReadEvent());
                            return;
                        }
                        BookLevels levels = WordDataBase.getInstance(TalkShowApplication.getContext()).getBookLevelDao().getBookLevel(bookId);
                        if (levels == null) {
                            levels = new BookLevels(bookId, 0, 0, 0);
                        }
                        levels.version = respons.getBookVersion();
                        WordDataBase.getInstance(TalkShowApplication.getContext()).getBookLevelDao().updateBookLevel(levels);
                    }
                });
    }

    public void SyncMicroStudyRecord() {
        RxUtil.unsubscribe(mSyncMicroSub);
        mSyncMicroSub = mDataManager.getMicroStudyRecord(UserInfoManager.getInstance().getUserId(), Constant.MOOC_TYPE, "1", "1000")
                .subscribe(new Subscriber<StudyRecordResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("MainFragPresenter", "SyncMicroStudyRecord onError " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(StudyRecordResponse response) {
                        if (response == null || response.getData() == null) {
                            Log.e("MainFragPresenter", "SyncMicroStudyRecord onNext response is null. ");
                            return;
                        }
                        Log.e("MainFragPresenter", "SyncMicroStudyRecord getData().size = " + response.getData().size());
                        List<StudyProgress> studyList = new ArrayList<>();
                        for (StudyResponse study : response.getData()) {
                            if (study == null) {
                                continue;
                            }
                            StudyProgress studyProgress = new StudyProgress();
                            studyProgress.uid = UserInfoManager.getInstance().getUserId();
                            studyProgress.lesson = study.Lesson;
                            studyProgress.lessonId = study.LessonId;
                            studyProgress.startTime = study.BeginTime;
                            studyProgress.endTime = study.EndTime;
                            studyProgress.endFlag = study.EndFlg;
                            if (studyProgress.endFlag == 1) {
                                studyProgress.percentage = 100;
                            } else {
                                studyProgress.percentage = 0;
                            }
                            studyList.add(studyProgress);
                        }
                        if (studyList.size() > 0) {
                            IMoocDBManager.getInstance().saveStudyProgressList(studyList);
                            Log.e("MainFragPresenter", "SyncMicroStudyRecord studyList().size = " + studyList.size());
                            EventBus.getDefault().post(new ReadEvent());
                        }
                    }
                });
    }

    /*public void loadLoop() {
        checkViewAttached();
        RxUtil.unsubscribe(mLoadLoopSub);
        String type = Constant.Apk.isChild() ? LoopService.GetLoopInfo.Param.Value.CHILD_LOOP_TYPE
                : LoopService.GetLoopInfo.Param.Value.LOOP_TYPE;
        mLoadLoopSub = mDataManager.getLunboItems(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Subscriber<List<LoopItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("MainFragPresenter", "loadLoop onError " + e.getMessage());
                        }
                        if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
                            getMvpView().showToast(R.string.main_need_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(List<LoopItem> loopItems) {
                        getMvpView().setBanner(loopItems);
                    }
                });
    }

    private void login(double latitude, double longitude) {
        User user = mAccountManager.getSaveUser();

        if (user != null) {
            if (!TextUtils.isEmpty(user.getUsername())
                    && !TextUtils.isEmpty(user.getPassword())) {
                checkViewAttached();
                RxUtil.unsubscribe(mLoginSub);
                mLoginSub = mAccountManager.login(user.getUsername(),
                        user.getPassword(), latitude, longitude, null);
            }
        }
    }

    public void login() {
        Location location = mGetLocation.getLocation();
        login(location.getLatitude(), location.getLongitude());
    }

    public void loginWithoutLocation() {
        login(0, 0);
    }*/

    public void getVoaById(int voaId) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetVoaByIdSub);
        mGetVoaByIdSub = mDataManager.getVoaById(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Subscriber<Voa>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("MainFragPresenter", "getVoaById onError " + e.getMessage());
                        }
//                        if (getMvpView() != null) {
//                            getMvpView().showToast(R.string.database_error);
//                        }
                    }

                    @Override
                    public void onNext(Voa voa) {
                        if (getMvpView() != null) {
                            getMvpView().startDetailActivity(voa, StudyActivity.title_default, 0,-1);
                        }
                    }
                });
    }

    public void checkVersion() {
        mVersionManager.checkVersion(callBack);
    }

    VersionManager.AppUpdateCallBack callBack = new VersionManager.AppUpdateCallBack() {
        @Override
        public void appUpdateSave(final String versionCode, final String appUrl) {
            String updateAlert = ((BaseActivity) getMvpView()).getString(R.string.about_update_alert);
            getMvpView().showAlertDialog(
                    MessageFormat.format(updateAlert, versionCode),
                    (dialog, which) -> getMvpView().startAboutActivity(versionCode, appUrl));
        }

        @Override
        public void appUpdateFailed() {

        }
    };

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
                            Log.e("MainFragPresenter", "registerToken onError  " + e.getMessage());
                        }
                        getMvpView().goResultActivity(null);
                    }

                    @Override
                    public void onNext(RegisterMobResponse response) {
                        if (response == null) {
                            Log.e("MainFragPresenter", "registerToken onNext response is null.");
                            getMvpView().goResultActivity(null);
                            return;
                        }
                        Log.e("MainFragPresenter", "registerToken onNext isLogin " + response.isLogin);
                        if (1 == response.isLogin) {
                            getMvpView().goResultActivity(new LoginResult());
                            getMvpView().showToast("您已经登录成功，可以进行学习了。");
                            // already login, need update user info
                            if (response.userinfo != null) {
                                mAccountManager.setUser(response.userinfo, "");
                                mAccountManager.saveUser();
                            } else {
                                Log.e("MainFragPresenter", "registerToken onNext response.userinfo is null.");
                            }
                            EventBus.getDefault().post(new LoginEvent());
                        } else {
                            if (response.res != null) {
                                // register by this phone
                                RegisterMobResponse.MobBean mobBean = response.res;
                                LoginResult loginResult = new LoginResult();
                                loginResult.setPhone(mobBean.phone);
                                getMvpView().goResultActivity(loginResult);
                            } else {
                                Log.e("MainFragPresenter", "registerToken onNext response.res is null.");
                                getMvpView().goResultActivity(null);
                            }
                        }
                    }
                });
    }*/

    public void getVoaSeries(String series) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoadNewSub);
        mLoadNewSub = mDataManager.getTitleSeriesList(series, UserInfoManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TitleSeriesResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("MainFragPresenter", "getVoaSeries onError  " + e.getMessage());
                        }
//                        getMvpView().showVoas(null);
                        loadVoas(Integer.parseInt(series));
                    }

                    @Override
                    public void onNext(TitleSeriesResponse response) {
                        if (response == null || response.getData() == null) {
                            Log.e("MainFragPresenter", "getVoaSeries onNext response is null. ");
//                            getMvpView().showVoas(null);
                            loadVoas(Integer.parseInt(series));
                            return;
                        }
                        Log.e("MainFragPresenter", "getVoaSeries onNext getTotal " + response.getTotal());
                        List<TitleSeries> seriesData = response.getData();
                        List<Voa> voaData = new ArrayList<>();
                        for (TitleSeries series: seriesData) {
                            try {
                                voaData.add(Series2Voa(series));
                            } catch (Exception var2) {
                                Log.e("MainFragPresenter", "getVoaSeries onNext id " + series.Id);
                                var2.printStackTrace();
                            }
                        }
                        Log.e("MainFragPresenter", "getVoaSeries onNext voaData " + voaData.size());
                        getMvpView().showVoas(voaData);
                        getMvpView().showTitleSeries(seriesData);
                        TalkShowApplication.getSubHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                for (Voa series: voaData) {
                                    long result = mDataManager.insertVoaDB(series);
//                                    Log.e("MainFragPresenter", "getVoaSeries insertVoaDB result " + result);
                                }
                            }
                        });
                    }
                });
    }
    public static Voa Series2Voa(TitleSeries series) {
        if (series == null) {
            return null;
        }
        return Voa.builder().setUrl(series.Sound).setPic(series.Pic).setTitle(FixUtil.transToStandardText(series.Title)).setTitleCn(FixUtil.transToStandardText(series.Title_cn))
                .setVoaId(series.Id).setCategory(series.Category).setDescCn(series.DescCn).setSeries(series.series)
                .setCreateTime(series.CreatTime).setPublishTime(series.PublishTime).setHotFlag(series.HotFlg).setReadCount(series.ReadCount)
                .setClickRead(series.clickRead).setSound(series.Sound.replace("http://staticvip."+Constant.Web.WEB_SUFFIX.replace("/","")+"/sounds/voa", ""))
                .setTotalTime(series.totalTime).setPercentId(series.percentage).setOutlineId(series.outlineid).setPackageId(series.packageid).setCategoryId(series.categoryid).setClassId(series.classid)
                .setIntroDesc(series.IntroDesc).setPageTitle(series.Title).setKeyword(series.Keyword)
                //这里增加video参数
                .setVideo(series.video)
                .build();
    }

    public List<SeriesData> getSeries4Id(int seriesId) {
        return mDataManager.getSeriesId(seriesId);
    }

    public Observable<SeriesData> getSeriesById(int seriesId){
        return mDataManager.getSeriesById(seriesId);
    }

    public void getTitleSeries(String series) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoadSeriesSub);
        mLoadSeriesSub = mDataManager.getTitleSeriesList(series, UserInfoManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Subscriber<TitleSeriesResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("MainFragPresenter", "getTitleSeries onError  " + e.getMessage());
                        }
//                        if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
//                            getMvpView().showToast(R.string.please_check_network);
//                        }
                    }

                    @Override
                    public void onNext(TitleSeriesResponse response) {
                        if (response == null || response.getData() == null) {
                            Log.e("MainFragPresenter", "getTitleSeries onNext response is null. ");
                            return;
                        }
                        Log.e("MainFragPresenter", "getTitleSeries onNext getTotal " + response.getTotal());
                        getMvpView().showTitleSeries(response.getData());
                    }
                });
    }

    public List<VoaSoundNew> getVoaSoundVoaId(int voaid) {
        return mDataManager.getVoaSoundVoaUid(UserInfoManager.getInstance().getUserId(), voaid);
    }
    public List<ArticleRecord> getArticleByVoaId(int voaid) {
        return mDataManager.getArticleByUid(UserInfoManager.getInstance().getUserId(), voaid);
    }
    public void syncVoaTexts(final int voaId) {
        Log.e("MainFragPresenter", "syncVoaTexts voaId " + voaId);
        checkViewAttached();
        RxUtil.unsubscribe(mLoadWordSub);
        mLoadWordSub = mDataManager.syncVoaTexts(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VoaText>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("MainFragPresenter", "syncVoaTexts onError  " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(List<VoaText> voaTextList) {
                        Log.e("MainFragPresenter", "syncVoaTexts onNext ok.");
                    }
                });
    }

    public UploadStudyRecordService getUploadStudyRecordService() {
        return mDataManager.getUploadStudyRecordService();
    }

    public void saveArticleRecord(final ArticleRecord record) {
        /*RxUtil.unsubscribe(mLoadLoopSub);
        mLoadLoopSub = mDataManager.saveArticleRecord(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e(ReadFragment.TAG, "saveArticleRecord onError  " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.e(ReadFragment.TAG, "saveArticleRecord onNext " + aBoolean);
                    }
                });*/

        mDataManager.saveArticleRecordNew(record);
    }
}
