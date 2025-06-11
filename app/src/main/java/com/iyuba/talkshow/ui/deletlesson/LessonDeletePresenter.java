package com.iyuba.talkshow.ui.deletlesson;

import android.util.Log;

import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.FileUtils;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.manager.WordManager;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;

public class LessonDeletePresenter extends BasePresenter<LessonDeleteMVPView> {

    private final DataManager mDataManager;
    private final ConfigManager mConfigManager;

    @Inject
    public LessonDeletePresenter(ConfigManager configManager, DataManager mDataManager) {
        this.mConfigManager = configManager ;
        this.mDataManager = mDataManager;
    }
    public void putCourseId(int parseInt,String courseTitle) {
        mConfigManager.putCourseId(parseInt);
        mConfigManager.putCourseTitle(courseTitle);
    }

    public void deleteLessons(int bookId, String uid) {
        List<Integer> mDatas = mDataManager.getXiaoxueVoaIdsByBookId(bookId);
//        List<Integer> mNotDeleteDatas = WordDataBase.getInstance(TalkShowApplication.getContext()).getTalkShowWordsDao().getVoasNotIn(bookId);
//        Iterator<Integer> iterator = mDatas.iterator();
//        while (iterator.hasNext()) {
//            int i = iterator.next();
//            if (mNotDeleteDatas.contains(i)) {
//                iterator.remove();
//            }
//        }
        for (Integer voaid : mDatas) {
            File file = StorageUtil.getMediaDir(TalkShowApplication.getContext(), voaid);
            FileUtils.deleteFile(file);
        }
//        File sentenceAudioFolder = StorageUtil.getBookFolder(TalkShowApplication.getContext(), bookId);
//
//        if (sentenceAudioFolder.exists()) {
//            FileUtils.deleteFile(sentenceAudioFolder);
//        }
        if (WordManager.WordDataVersion == 2) {
            WordDataBase.getInstance(TalkShowApplication.getContext()).getNewBookLevelDao().updateBookDownload(bookId, uid,0);
        } else {
            WordDataBase.getInstance(TalkShowApplication.getContext()).getBookLevelDao().updateBookDownload(bookId, 0);
        }
        getMvpView().showDeleteMessage("删除成功！");
    }

    public void getDownloadedClass() {
        List<Integer> lessonIds;
        if (WordManager.WordDataVersion == 2) {
            lessonIds = WordDataBase.getInstance(TalkShowApplication.getContext()).getNewBookLevelDao().getDownloaded();
        } else {
            lessonIds = WordDataBase.getInstance(TalkShowApplication.getContext()).getBookLevelDao().getDownloaded();
        }
        if (lessonIds.size() == 0) {
            Log.e("LessonDeletePresenter", "getDownloadedClass is 0.");
            getMvpView().showBookList(null);
        }else {
            Log.e("LessonDeletePresenter", "getDownloadedClass size= " + lessonIds.size());
            mDataManager.getSeriesListByIds(lessonIds)
                    .compose(RxUtil.io2main())
                    .subscribe(new Subscriber<List<SeriesData>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (e != null) {
                                Log.e("LessonDeletePresenter", "onError = " + e.getMessage());
                            }
                        }

                        @Override
                        public void onNext(List<SeriesData> seriesData) {
                            if (seriesData != null && seriesData.size() > 0) {
                                Log.e("LessonDeletePresenter", "onNext size= " + seriesData.size());
                                getMvpView().showBookList(seriesData);
                            } else {
                                Log.e("LessonDeletePresenter", "onNext is null. ");
                                getMvpView().showBookList(null);
                            }
                        }
                    });
        }
    }
}
