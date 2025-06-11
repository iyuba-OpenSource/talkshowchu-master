package com.iyuba.talkshow.lil.novel.choose.book;

import com.iyuba.talkshow.lil.help_fix.manager.dataManager.NovelDataManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_data;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_book;
import com.iyuba.talkshow.lil.help_mvp.mvp.BasePresenter;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/4/27 14:23
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ChooseNovelBookPresenter extends BasePresenter<ChooseNovelBookView> {

    private Disposable novelBookDis;

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unDisposable(novelBookDis);
    }

    //加载小说的书籍数据
    public void loadNovelBookData(int level,String from){
        checkViewAttach();
        RxUtil.unDisposable(novelBookDis);
        NovelDataManager.getBookData(level,from)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<List<Novel_book>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        novelBookDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<List<Novel_book>> bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.getResult().equals("200")){
                                //保存到数据库
//                                DataManager.saveNovelBookToDB(RemoteTransUtil.transNovelBookData(bean.getData()));
//                                //从数据库取出
//                                List<BookEntity_novel> list = DataManager.getNovelBookByLevelFromDB(from,String.valueOf(level));
                                //展示数据
                                getMvpView().showBookData(bean.getData());

                                //测试-这里查询章节数据
//                                chapterList = bean.getData();
//                                curChapterIndex = 0;
//                                loadNovelChapterData(chapterList.get(curChapterIndex).getLevel(), chapterList.get(curChapterIndex).getOrderNumber(),from);
                            }else {
                                getMvpView().showBookData(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showBookData(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /********************测试***************/
    /*private int curChapterIndex = 0;
    private List<Novel_book> chapterList;
    private Disposable novelChapterDis;

    //加载小说的章节数据
    private void loadNovelChapterData(String level,String orderNumber,String from){
        if (curChapterIndex>=chapterList.size()){
            Log.d("小说数据", "--------加载完成--------"+from+"--"+level);
        }

        checkViewAttach();
        RxUtil.unDisposable(novelChapterDis);
        NovelDataManager.getChapterData(level, orderNumber,from)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_novelChapter<Novel_book, List<Novel_chapter>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        novelChapterDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_novelChapter<Novel_book, List<Novel_chapter>> bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.getResult()==200){
                                //保存在数据库
                                NovelDataManager.saveChapterToDB(RemoteTransUtil.transNovelChapterToDB(from,bean.getChapterInfo()));
                                //从数据库获取数据
                                List<ChapterEntity_novel> list = NovelDataManager.searchMultiChapterFromDB(from,String.valueOf(level),orderNumber);
                                //进行数据展示
                                Log.d("小说数据", "加载成功--章节数据--名称--"+chapterList.get(curChapterIndex).getBookname_cn()+"--类型--"+from+"--书籍id--"+orderNumber+"--数量--"+list.size());

                                curChapterIndex++;
                                //加载详情
                                detailList = list;
                                curDetailIndex = 0;
                                loadNovelChapterDetailData(from,detailList.get(curDetailIndex).voaid);
                            }else {
                                Log.d("小说数据", "加载失败--章节数据--类型--"+from+"--书籍id--"+orderNumber+"---------------------");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            Log.d("小说数据", "加载失败--章节数据--类型--"+from+"--书籍id--"+orderNumber+"---------------------");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private int curDetailIndex = 0;
    private List<ChapterEntity_novel> detailList;
    private Disposable novelChapterDetailDis;
    //获取小说的章节详情数据
    private void loadNovelChapterDetailData(String types,String voaId){
        checkViewAttach();
        RxUtil.unDisposable(novelChapterDetailDis);
        NovelDataManager.getChapterDetailData(types, voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_bookInfo_texts<Novel_book, List<Novel_chapter_detail>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        novelChapterDetailDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_bookInfo_texts<Novel_book, List<Novel_chapter_detail>> bean) {
                        if (getMvpView()!=null) {
                            if (bean!=null&&bean.getResult() == 200) {
                                //保存在本地
                                NovelDataManager.saveChapterDetailToDB(RemoteTransUtil.transNovelChapterDetailToDB(types,bean.getTexts()));
                                //从本地获取数据
                                List<ChapterDetailEntity_novel> list = NovelDataManager.searchMultiChapterDetailFromDB(types, voaId);
                                //显示在界面上

                                Log.d("小说数据", "加载成功--详情数据--名称--"+detailList.get(curDetailIndex).cname_cn+"--类型--"+types+"--voaId--"+voaId+"--数量--"+list.size());
                                RxTimer.timerInIO("testData", 1000L, new RxTimer.RxActionListener() {
                                    @Override
                                    public void onAction(long number) {
                                        RxTimer.cancelTimer("testData");
                                        curDetailIndex++;

                                        boolean isAllBreak = false;
                                        if (curDetailIndex>=detailList.size()){
                                            Log.d("小说数据", "加载完成--详情数据--类型--"+types+"--voaId--"+voaId);

                                            if (curChapterIndex>=chapterList.size()){
                                                Log.d("小说数据", "加载完成--章节数据------");
                                                isAllBreak = true;
                                            }else {
                                                loadNovelChapterData(chapterList.get(curChapterIndex).getLevel(),chapterList.get(curChapterIndex).getOrderNumber(),types);
                                            }
                                        }else {
                                            loadNovelChapterDetailData(types,detailList.get(curDetailIndex).voaid);
                                        }

                                        if (isAllBreak){
                                            return;
                                        }
                                    }
                                });
                            }else {
                                Log.d("小说数据", "加载失败--详情数据--类型--"+types+"--voaId--"+voaId+"----------");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            Log.d("小说数据", "加载失败--详情数据--类型--"+types+"--voaId--"+voaId+"----------");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }*/
}
