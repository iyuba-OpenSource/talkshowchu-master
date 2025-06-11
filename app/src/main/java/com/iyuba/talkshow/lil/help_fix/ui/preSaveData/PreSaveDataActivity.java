package com.iyuba.talkshow.lil.help_fix.ui.preSaveData;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.LessonNewResponse;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.data.model.SeriesResponse;
import com.iyuba.talkshow.data.model.TitleSeries;
import com.iyuba.talkshow.data.model.TitleSeriesResponse;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.databinding.AtyDataPreBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.wordtest.db.WordDataBase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @title: 预存数据操作类
 * @date: 2023/9/19 09:30
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description: 这里是加载预存数据的操作
 */
public class PreSaveDataActivity extends BaseActivity {

    @Inject
    DataManager dataManager;

    //延迟加载时间
    private long delayTime = 3000L;

    //布局样式
    private AtyDataPreBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AtyDataPreBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        activityComponent().inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        binding.btnStart.setOnClickListener(v -> {
            binding.tvLog.setText("请在logcat中查看加载进度，加载完成会在此处显示");

//            loadPublishData();
            loadWordData();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /***********************************数据接口***************************/
    /***********出版社数据**********/
    //出版社数据的位置
    private int publishIndex = 0;
    //类型数据的位置
    private int typeIndex = 0;
    //出版社数据简要集合(名称，集合)
    private List<Pair<String, List<Pair<String, Integer>>>> publishList = new ArrayList<>();

    //加载出版社数据
    private void loadPublishData() {
        showLog("正在加载出版数据和类型数据");
        publishList.clear();

        dataManager.chooseLessonNew(App.APP_ID, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LessonNewResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.tvLog.setText("出版数据加载异常--" + e.getMessage());
                    }

                    @Override
                    public void onNext(LessonNewResponse response) {
                        if (response != null && response.result == 200) {
                            if (response.data != null && response.data.junior != null && response.data.junior.size() > 0) {
                                //保存出版社和数据
                                List<LessonNewResponse.Series> seriesList = response.data.junior;
                                for (int i = 0; i < seriesList.size(); i++) {
                                    LessonNewResponse.Series series = seriesList.get(i);

                                    List<Pair<String, Integer>> pairList = new ArrayList<>();
                                    for (int j = 0; j < series.SeriesData.size(); j++) {
                                        LessonNewResponse.SeriesDatas typeData = series.SeriesData.get(j);
                                        pairList.add(new Pair<>(typeData.SeriesName, typeData.Category));
                                    }

                                    publishList.add(new Pair<>(series.SourceType, pairList));
                                }

                                //加载书籍数据
                                showLog("出版数据和类型数据加载完成");
                                loadBookData(publishList.get(publishIndex).second.get(typeIndex).second);
                            } else {
                                binding.tvLog.setText("出版数据加载失败");
                            }
                        } else {
                            binding.tvLog.setText("出版数据加载失败");
                        }
                    }
                });
    }

    /************书籍数据*************/
    //书籍数据的位置
    private int bookIndex = 0;
    //书籍数据简要集合(名称，key)
    private List<Pair<String, String>> bookList = new ArrayList<>();

    //加载书籍数据
    private void loadBookData(int typeId) {
        showLog("正在加载书籍数据");
        bookList.clear();

        try {
            Thread.sleep(delayTime);
        } catch (Exception e) {

        }

        dataManager.getCategorySeriesList(0, String.valueOf(typeId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SeriesResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showLog("加载书籍数据异常--" + e.getMessage());
                        //下一个
                        typeIndex++;
                        if (typeIndex >= publishList.get(publishIndex).second.size()) {
                            publishIndex++;
                            typeIndex = 0;
                            if (publishIndex >= publishList.size()) {
                                binding.tvLog.setText("全部数据加载完成");
                            } else {
                                loadBookData(publishList.get(publishIndex).second.get(typeIndex).second);
                            }
                        } else {
                            loadBookData(publishList.get(publishIndex).second.get(typeIndex).second);
                        }
                    }

                    @Override
                    public void onNext(SeriesResponse response) {
                        if (response != null && response.getResult() == 1 && response.getData() != null && response.getData().size() > 0) {
                            //保存书籍数据
                            for (int i = 0; i < response.getData().size(); i++) {
                                SeriesData data = response.getData().get(i);
                                bookList.add(new Pair<>(data.getSeriesName(), data.getId()));
                            }

                            //保存数据库数据
                            TalkShowApplication.getSubHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    for (SeriesData bean : response.getData()) {
                                        dataManager.insertSeriesDB(bean);
                                    }
                                }
                            });

                            //加载章节数据
                            showLog("书籍数据加载完成");
                            loadChapterData(bookList.get(bookIndex).second);
                        } else {
                            showLog("书籍数据加载失败");
                            //下一个
                            typeIndex++;
                            if (typeIndex >= publishList.get(publishIndex).second.size()) {
                                publishIndex++;
                                typeIndex = 0;
                                if (publishIndex >= publishList.size()) {
                                    binding.tvLog.setText("全部数据加载完成");
                                } else {
                                    loadBookData(publishList.get(publishIndex).second.get(typeIndex).second);
                                }
                            } else {
                                loadBookData(publishList.get(publishIndex).second.get(typeIndex).second);
                            }
                        }
                    }
                });
    }

    /**************书籍章节数据*************/
    //书籍章节的位置
    private int chapterIndex = 0;
    //书籍章节数据简要集合(名称，key)
    private List<Pair<String, Integer>> chapterList = new ArrayList<>();

    //加载书籍章节数据
    private void loadChapterData(String bookId) {
        showLog("正在加载数据");
        chapterList.clear();

        try {
            Thread.sleep(delayTime);
        } catch (Exception e) {

        }

        dataManager.getTitleSeriesList(bookId, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TitleSeriesResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showLog("数据加载异常--" + e.getMessage());
                        bookIndex++;
                        if (bookIndex >= bookList.size()) {
                            //下一个类型
                            typeIndex++;
                            bookIndex = 0;
                            if (typeIndex >= publishList.get(publishIndex).second.size()) {
                                publishIndex++;
                                typeIndex = 0;
                                if (publishIndex >= publishList.size()) {
                                    binding.tvLog.setText("全部数据加载完成");
                                } else {
                                    loadBookData(publishList.get(publishIndex).second.get(typeIndex).second);
                                }
                            } else {
                                loadBookData(publishList.get(publishIndex).second.get(typeIndex).second);
                            }
                        } else {
                            loadChapterData(bookList.get(bookIndex).second);
                        }
                    }

                    @Override
                    public void onNext(TitleSeriesResponse response) {
                        if (response != null && response.getResult() == 1 && response.getData() != null && response.getData().size() > 0) {
                            //保存数据
                            for (int i = 0; i < response.getData().size(); i++) {
                                TitleSeries series = response.getData().get(i);
                                chapterList.add(new Pair<>(series.Title, series.Id));
                            }

                            //数据库操作
                            List<TitleSeries> seriesData = response.getData();
                            List<Voa> voaData = new ArrayList<>();
                            for (TitleSeries series : seriesData) {
                                try {
                                    voaData.add(Series2Voa(series));
                                } catch (Exception var2) {
                                    Log.e("MainFragPresenter", "getVoaSeries onNext id " + series.Id);
                                    var2.printStackTrace();
                                }
                            }
                            TalkShowApplication.getSubHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    for (Voa series : voaData) {
                                        long result = dataManager.insertVoaDB(series);
                                    }
                                }
                            });

                            //下一个操作
                            showLog("数据加载完成");
                            loadChapterDetailData(chapterList.get(chapterIndex).second);
                        } else {
                            //下一个数据
                            showLog("数据加载失败");
                            bookIndex++;
                            if (bookIndex >= bookList.size()) {
                                //下一个类型
                                typeIndex++;
                                bookIndex = 0;
                                if (typeIndex >= publishList.get(publishIndex).second.size()) {
                                    publishIndex++;
                                    typeIndex = 0;
                                    if (publishIndex >= publishList.size()) {
                                        binding.tvLog.setText("全部数据加载完成");
                                    } else {
                                        loadBookData(publishList.get(publishIndex).second.get(typeIndex).second);
                                    }
                                } else {
                                    loadBookData(publishList.get(publishIndex).second.get(typeIndex).second);
                                }
                            } else {
                                loadChapterData(bookList.get(bookIndex).second);
                            }
                        }
                    }
                });
    }

    /***************书籍章节详情数据************/
    //加载书籍章节详情数据
    private void loadChapterDetailData(int voaId) {
        showLog("正在加载详情数据");

        try {
            Thread.sleep(delayTime);
        } catch (Exception e) {

        }

        dataManager.syncVoaTexts(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VoaText>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showLog("详情数据加载异常--" + e.getMessage());
                        //下一个
                        chapterIndex++;
                        if (chapterIndex >= chapterList.size()) {
                            bookIndex++;
                            chapterIndex = 0;
                            if (bookIndex >= bookList.size()) {
                                typeIndex++;
                                bookIndex = 0;
                                if (typeIndex >= publishList.get(publishIndex).second.size()) {
                                    publishIndex++;
                                    typeIndex = 0;
                                    if (publishIndex >= publishList.size()) {
                                        binding.tvLog.setText("全部数据加载完成");
                                    } else {
                                        loadBookData(publishList.get(publishIndex).second.get(typeIndex).second);
                                    }
                                } else {
                                    loadBookData(publishList.get(publishIndex).second.get(typeIndex).second);
                                }
                            } else {
                                loadChapterData(bookList.get(bookIndex).second);
                            }
                        } else {
                            loadChapterDetailData(chapterList.get(chapterIndex).second);
                        }
                    }

                    @Override
                    public void onNext(List<VoaText> list) {
                        //这里不用保存，数据库自动处理

                        if (list != null && list.size() > 0) {
                            showLog("章节详情数据加载完成");
                        } else {
                            showLog("章节详情数据加载失败");
                        }

                        //下一个
                        chapterIndex++;
                        if (chapterIndex >= chapterList.size()) {
                            bookIndex++;
                            chapterIndex = 0;
                            if (bookIndex >= bookList.size()) {
                                typeIndex++;
                                bookIndex = 0;
                                if (typeIndex >= publishList.get(publishIndex).second.size()) {
                                    publishIndex++;
                                    typeIndex = 0;
                                    if (publishIndex >= publishList.size()) {
                                        binding.tvLog.setText("全部数据加载完成");
                                    } else {
                                        loadBookData(publishList.get(publishIndex).second.get(typeIndex).second);
                                    }
                                } else {
                                    loadBookData(publishList.get(publishIndex).second.get(typeIndex).second);
                                }
                            } else {
                                loadChapterData(bookList.get(bookIndex).second);
                            }
                        } else {
                            loadChapterDetailData(chapterList.get(chapterIndex).second);
                        }
                    }
                });
    }

    /****************单词数据(单独处理)**********************/
    private int[] wordArray = new int[]{399, 400, 401, 402};
    //当前的计算数据
    private int curWordIndex = 0;

    //加载单词数据
    private void loadWordData() {
        try {
            Thread.sleep(delayTime);
        } catch (Exception e) {
            Log.d("单词数据", "延迟存在问题");
        }

        if (curWordIndex >= wordArray.length) {
            Log.d("单词数据", "单词数据完成");
            binding.tvLog.setText("加载单词完成");
            return;
        }

        //当前的bookId
        int bookId = wordArray[curWordIndex];

        Log.d("单词数据", "当前加载的单词数据--" + bookId);

        dataManager.getWordByBookId(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<com.iyuba.talkshow.data.model.UpdateWordResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("单词数据", "当前加载的单词数据--失败");

                        curWordIndex++;
                        loadWordData();
                    }

                    @Override
                    public void onNext(com.iyuba.talkshow.data.model.UpdateWordResponse response) {
                        if (response != null && response.getData() != null) {
                            WordDataBase.getInstance(TalkShowApplication.getInstance()).getTalkShowWordsDao().insertWord(response.getData());

                            Log.d("单词数据", "加载数据完成--书籍id：" + bookId + "--单词数量：" + response.getData().size());
                        } else {
                            Log.d("单词数据", "加载数据失败--失败信息：" + response.getResult());
                        }

                        curWordIndex++;
                        loadWordData();
                    }
                });
    }

    /*******************************辅助功能*********************************/
    //显示log
    private void showLog(String msg) {
        //根据数据判断显示
        StringBuffer buffer = new StringBuffer();

        if (publishIndex < publishList.size()) {
            buffer.append("--出版数据--" + publishList.get(publishIndex).first);

            if (typeIndex < publishList.get(publishIndex).second.size()) {
                buffer.append("--类型数据--" + publishList.get(publishIndex).second.get(typeIndex).first + "--" + publishList.get(publishIndex).second.get(typeIndex).second);
            }
        }

        if (bookIndex < bookList.size()) {
            buffer.append("--书籍数据--" + bookList.get(bookIndex).first + "--" + bookList.get(bookIndex).second);
        }

        if (chapterIndex < chapterList.size()) {
            buffer.append("--章节数据--" + chapterList.get(chapterIndex).first + "--" + chapterList.get(chapterIndex).second);
        }

        Log.d("预存数据操作", msg + buffer.toString());
    }


    public static Voa Series2Voa(TitleSeries series) {
        if (series == null) {
            return null;
        }
        return Voa.builder().setUrl(series.Sound).setPic(series.Pic).setTitle(series.Title).setTitleCn(series.Title_cn)
                .setVoaId(series.Id).setCategory(series.Category).setDescCn(series.DescCn).setSeries(series.series)
                .setCreateTime(series.CreatTime).setPublishTime(series.PublishTime).setHotFlag(series.HotFlg).setReadCount(series.ReadCount)
                .setClickRead(series.clickRead).setSound(series.Sound.replace("http://staticvip." + Constant.Web.WEB_SUFFIX.replace("/", "") + "/sounds/voa", ""))
                .setTotalTime(series.totalTime).setPercentId(series.percentage).setOutlineId(series.outlineid).setPackageId(series.packageid).setCategoryId(series.categoryid).setClassId(series.classid)
                .setIntroDesc(series.IntroDesc).setPageTitle(series.Title).setKeyword(series.Keyword)
                //这里增加video参数
                .setVideo(series.video)
                .build();
    }
}
