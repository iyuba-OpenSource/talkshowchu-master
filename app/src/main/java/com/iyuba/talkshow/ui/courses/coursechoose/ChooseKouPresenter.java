package com.iyuba.talkshow.ui.courses.coursechoose;


import android.util.Log;

import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.LessonNewResponse;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.data.model.SeriesResponse;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.injection.PerFragment;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.FileUtils;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.wordtest.db.WordDataBase;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@PerFragment
public class ChooseKouPresenter extends BasePresenter<ChooseCourseMVPView> {

    private final DataManager mDataManager;
    private final ConfigManager mConfigManager;

    private Subscription mLessonSub;
    private Subscription mSeriesSub;
    private Subscription mLoadSeriesSub;

    @Inject
    public ChooseKouPresenter(ConfigManager configManager , DataManager mDataManager) {
        this.mConfigManager = configManager ;
        this.mDataManager = mDataManager;
    }

    public List<Voa> loadVoasByBookId(int bookId) {
        checkViewAttached();
        Log.e("ChooseKouPresenter", "loadVoasByBookId " + bookId);
        return mDataManager.getVoaXiaoxueByBookId(bookId);
    }
    public List<SeriesData> getAllSeries(int cat) {
        return mDataManager.getSeriesCategory(cat);
    }

    public void chooseLessonNew() {
        Log.e("ChooseKouPresenter", "chooseLessonNew app Id = " + App.APP_ID);
        checkViewAttached();
        RxUtil.unsubscribe(mLessonSub);
        mLessonSub = mDataManager.chooseLessonNew(App.APP_ID, UserInfoManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LessonNewResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("ChooseKouPresenter", "chooseLessonNew onError " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(LessonNewResponse response) {
                        if (response == null || response.data == null) {
                            Log.e("ChooseKouPresenter", "chooseLessonNew onNext response is null. ");
                            return;
                        }
                        if (response.result != 200) {
                            Log.e("ChooseKouPresenter", "chooseLessonNew onNext response.result " + response.result);
                            return;
                        }
                        if (getMvpView() != null) {
                            getMvpView().setLesson(response.data.junior);
                        }
                    }
                });
    }
    public void getSeriesList(String catId) {
        Log.e("ChooseKouPresenter", "getSeriesList catId " + catId);
        checkViewAttached();
        RxUtil.unsubscribe(mLoadSeriesSub);
        mLoadSeriesSub = mDataManager.getCategorySeriesList(UserInfoManager.getInstance().getUserId(), catId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SeriesResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("ChooseKouPresenter", "getSeriesList onError " + e.getMessage());
                        }
//                        if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
//                            getMvpView().showToastShort(R.string.please_check_network);
//                        }
                        chooseCourse(catId);
                    }

                    @Override
                    public void onNext(SeriesResponse response) {
                        if (response == null || response.getData() == null) {
                            chooseCourse(catId);
                            Log.e("ChooseKouPresenter", "getSeriesList onNext response is null. ");
                            return;
                        }
                        List<SeriesData> result = new ArrayList<>();
                        for (SeriesData series: response.getData()) {
                            if (series.getCategory().equalsIgnoreCase("" + catId)) {
                                result.add(series);
                            }
                        }
                        if (getMvpView() != null) {
                            getMvpView().setCoures(result);
                        }
                        TalkShowApplication.getSubHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                for (SeriesData bean : response.getData()) {
                                    mDataManager.insertSeriesDB(bean);
                                }
                            }
                        });
                    }
                });
    }
    public void chooseCourse(String catId) {
        Log.e("ChooseKouPresenter", "chooseCourse catId " + catId);
        checkViewAttached();
        RxUtil.unsubscribe(mSeriesSub);
        mSeriesSub = mDataManager.getSeriesList(catId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<SeriesData>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("ChooseKouPresenter", "chooseCourse onError " + e.getMessage());
                        }
//                        getSeriesList(catId);
                    }

                    @Override
                    public void onNext(List<SeriesData> list) {
                        if ((list == null) || (list.size() < 1)) {
                            Log.e("ChooseKouPresenter", "chooseCourse onNext is null? ");
//                            getSeriesList(catId);
                            return;
                        }
                        if (getMvpView() != null) {
                            getMvpView().setMoreCourse(list);
                        }
                    }
                });
    }

    public int getKouCategory() {
        return mConfigManager.getKouCategory();
    }
    public void putKouCategory(int type) {
        mConfigManager.putKouCategory(type);
    }
    public int getKouType() {
        return mConfigManager.getKouType();
    }
    public void putKouType(int type) {
        mConfigManager.putKouType(type);
    }
    public int getKouClass() {
        return mConfigManager.getKouClass();
    }
    public void putKouClass(int type) {
        mConfigManager.putKouClass(type);
    }
    public String getKouTitle() {
        return mConfigManager.getKouTitle();
    }
    public int getKouId() {
        return mConfigManager.getKouId();
    }
    public void putKouId(int parseInt,String courseTitle) {
        mConfigManager.putKouId(parseInt);
        mConfigManager.putKouTitle(courseTitle);
    }

    public void deletCourses(int bookId) {
        List<Integer> mDatas  = mDataManager.getXiaoxueVoaIdsByBookId(bookId);
        List<Integer> mNotDeleteDatas =  WordDataBase.getInstance(TalkShowApplication.getInstance()).getTalkShowWordsDao().getVoasNotIn(bookId);
        Iterator<Integer> iterator = mDatas.iterator();
        while (iterator.hasNext()){
            int i = iterator.next() ;
            if (mNotDeleteDatas.contains(i)){
                iterator.remove();
            }
        }
        for (Integer voaid: mDatas) {
            File file = StorageUtil.getMediaDir(TalkShowApplication.getInstance(),voaid);
            FileUtils.deleteFile(file);
        }
        File sentenceAudioFolder = StorageUtil.getBookFolder(TalkShowApplication.getInstance(),bookId);

        if (sentenceAudioFolder.exists()){
            FileUtils.deleteFile(sentenceAudioFolder);
        }
        WordDataBase.getInstance(TalkShowApplication.getInstance()).getBookLevelDao().updateBookDownload(bookId,0);
        getMvpView().showToastLong("删除成功！");
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mLessonSub);
        RxUtil.unsubscribe(mSeriesSub);
        RxUtil.unsubscribe(mLoadSeriesSub);
    }
}
