package com.iyuba.talkshow.ui.courses.coursechoose;


import android.util.Log;

import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.AbilityControlManager;
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
public class ChooseCoursePresenter extends BasePresenter<ChooseCourseMVPView> {

    private final DataManager mDataManager;
    private final ConfigManager mConfigManager;

    private Subscription mLessonSub;
    private Subscription mSeriesSub;
    private Subscription mLoadSeriesSub;

    @Inject
    public ChooseCoursePresenter(ConfigManager configManager , DataManager mDataManager) {
        this.mConfigManager = configManager ;
        this.mDataManager = mDataManager;
    }

    public List<Voa> loadVoasByBookId(int bookId) {
        checkViewAttached();
        Log.e("ChooseCoursePresenter", "loadVoasByBookId " + bookId);
        return mDataManager.getVoaXiaoxueByBookId(bookId);
    }
    public List<SeriesData> getAllSeries(int cat) {
        return mDataManager.getSeriesCategory(cat);
    }

    //获取类型数据-远程
    public void getTypeDataByRemote() {
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
                        getMvpView().setLessonFail("获取类型数据异常");
                    }

                    @Override
                    public void onNext(LessonNewResponse response) {
                        if (response == null || response.data == null) {
                            getMvpView().setLessonFail("获取类型数据失败");
                            return;
                        }

                        if (response.result != 200) {
                            getMvpView().setLessonFail("获取类型数据失败");
                            return;
                        }

                        if (getMvpView() != null) {
                            //这里根据人教版审核处理下
                            if (AbilityControlManager.getInstance().isLimitPep()){
                                List<LessonNewResponse.Series> list = response.data.junior;
                                List<LessonNewResponse.Series> tempList = new ArrayList<>();
                                for (int i = 0; i < list.size(); i++) {
                                    String type = list.get(i).SourceType;
                                    if (!type.contains("人教版")){
                                        tempList.add(list.get(i));
                                    }
                                }

                                getMvpView().setLesson(tempList);
                            }else {
                                getMvpView().setLesson(response.data.junior);
                            }
                        }
                    }
                });
    }

    //获取书籍数据-远程
    public void getBookDataByRemote(String catId) {
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
                        getBookDataByDb(catId);
                    }

                    @Override
                    public void onNext(SeriesResponse response) {
                        if (response == null || response.getData() == null) {
                            getBookDataByDb(catId);
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
    //获取书籍数据-本地
    public void getBookDataByDb(String catId) {
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
                        getMvpView().setCourseFail("获取书籍数据异常");
                    }

                    @Override
                    public void onNext(List<SeriesData> list) {
                        if ((list == null) || (list.size() < 1)) {
                            getMvpView().setCourseFail("获取书籍数据失败");
                            return;
                        }
                        if (getMvpView() != null) {
                            getMvpView().setMoreCourse(list);
                        }
                    }
                });
    }

    public int getCourseCategory() {
        return mConfigManager.getCourseCategory();
    }
    public void putCourseCategory(int type) {
        mConfigManager.putCourseCategory(type);
    }
    public int getCourseType() {
        return mConfigManager.getCourseType();
    }
    public void putCourseType(int type) {
        mConfigManager.putCourseType(type);
    }
    public int getCourseClass() {
        return mConfigManager.getCourseClass();
    }
    public void putCourseClass(int type) {
        mConfigManager.putCourseClass(type);
    }
    public String getCourseTitle() {
        return mConfigManager.getCourseTitle();
    }
    public int getCourseId() {
        return mConfigManager.getCourseId();
    }
    public void putCourseId(int parseInt,String courseTitle) {
        mConfigManager.putCourseId(parseInt);
        mConfigManager.putCourseTitle(courseTitle);
    }

    public int getWordCategory() {
        return mConfigManager.getWordCategory();
    }
    public void putWordCategory(int type) {
        mConfigManager.putWordCategory(type);
    }
    public int getWordType() {
        return mConfigManager.getWordType();
    }
    public void putWordType(int type) {
        mConfigManager.putWordType(type);
    }
    public int getWordClass() {
        return mConfigManager.getWordClass();
    }
    public void putWordClass(int type) {
        mConfigManager.putWordClass(type);
    }
    public String getWordTitle() {
        return mConfigManager.getWordTitle();
    }
    public int getWordId() {
        return mConfigManager.getWordId();
    }
    public void putWordId(int parseInt,String courseTitle) {
        mConfigManager.putWordId(parseInt);
        mConfigManager.putWordTitle(courseTitle);
    }

    /*****************************合并数据展示***********************/
    public int getMargeCategory() {
        return mConfigManager.getCourseCategory();
    }
    public void putMargeCategory(int type) {
        mConfigManager.putCourseCategory(type);
        mConfigManager.putWordCategory(type);
    }
    public int getMargeType() {
        return mConfigManager.getCourseType();
    }
    public void putMargeType(int type) {
        mConfigManager.putCourseType(type);
        mConfigManager.putWordType(type);
    }
    public int getMargeClass() {
        return mConfigManager.getCourseClass();
    }
    public void putMargeClass(int type) {
        mConfigManager.putCourseClass(type);
        mConfigManager.putWordClass(type);
    }
    public void putMargeId(int parseInt,String title) {
        mConfigManager.putCourseId(parseInt);
        mConfigManager.putCourseTitle(title);

        mConfigManager.putWordId(parseInt);
        mConfigManager.putWordTitle(title);
    }
    public String getMargeTitle() {
        return mConfigManager.getCourseTitle();
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

    //获取类型数据-远程
    public void chooseLessonNew() {
        checkViewAttached();
        RxUtil.unsubscribe(mLessonSub);

        String showFlag = "0";
        if (UserInfoManager.getInstance().isVip()){
            showFlag = UserInfoManager.getInstance().getVipStatus();
        }

        mLessonSub = mDataManager.chooseLessonNew(App.APP_ID, UserInfoManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LessonNewResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().setLessonFail("获取类型数据异常");
                    }

                    @Override
                    public void onNext(LessonNewResponse response) {
                        if (response == null || response.data == null) {
                            getMvpView().setLessonFail("获取类型数据失败");
                            return;
                        }

                        if (response.result != 200) {
                            getMvpView().setLessonFail("获取类型数据失败");
                            return;
                        }

                        if (getMvpView() != null) {
                            getMvpView().setLesson(response.data.junior);
                        }
                    }
                });
    }
    //获取书籍的数据-网络
    public void getSeriesList(String catId) {
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
                        chooseCourse(catId);
                    }

                    @Override
                    public void onNext(SeriesResponse response) {
                        if (response == null || response.getData() == null) {
                            chooseCourse(catId);
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
    //获取书籍的数据-数据库
    public void chooseCourse(String catId) {
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
                        getMvpView().setCourseFail("获取书籍数据异常");
                    }

                    @Override
                    public void onNext(List<SeriesData> list) {
                        if ((list == null) || (list.size() < 1)) {
                            getMvpView().setCourseFail("获取书籍数据失败");
                            return;
                        }
                        if (getMvpView() != null) {
                            getMvpView().setMoreCourse(list);
                        }
                    }
                });
    }
}
