package com.iyuba.talkshow.lil.help_fix.ui.lesson;

import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.NovelDataManager;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.util.DBTransUtil;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_novelChapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_book;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_chapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.util.RemoteTransUtil;
import com.iyuba.talkshow.lil.help_mvp.mvp.BasePresenter;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/5/19 13:55
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class LessonPresenter extends BasePresenter<LessonView> {

    //加载小学章节内容
    private Disposable primaryChapterDis;
    //加载初中章节内容
    private Disposable middleChapterDis;
    //小说
    private Disposable novelChapterDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(novelChapterDis);
        RxUtil.unDisposable(primaryChapterDis);
        RxUtil.unDisposable(middleChapterDis);
    }

    //根据类型获取本地的章节数据
    public void loadLocalChapterData(String types,String bookLevel,String bookId){
        if (getMvpView()==null){
            return;
        }

        switch (types){
            /********小说**********/
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
            case TypeLibrary.BookType.bookworm://书虫英语
                List<ChapterEntity_novel> novelList = NovelDataManager.searchMultiChapterFromDB(types, bookLevel, bookId);
                if (novelList!=null&&novelList.size()>0){
                    getMvpView().showData(DBTransUtil.novelToChapterData(novelList));
                }else {
                    getMvpView().loadNetData();
                }
                break;
            /********中小学**********/
//            case TypeLibrary.BookType.junior_primary://小学
//            case TypeLibrary.BookType.junior_middle://初中
//                List<ChapterEntity_junior> juniorList = JuniorDataManager.getMultiChapterFromDB(bookId);
//                if (juniorList!=null&&juniorList.size()>0){
//                    getMvpView().showData(DBTransUtil.transJuniorChapterData(types,juniorList));
//                }else {
//                    getMvpView().loadNetData();
//                }
//                break;
        }
    }

    //根据类型获取服务器的章节数据
    public void loadNetChapterData(String types,String bookLevel,String bookId){
        if (getMvpView()==null){
            return;
        }

        switch (types){
            /********小说**********/
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
            case TypeLibrary.BookType.bookworm://书虫英语
                loadNovelChapterData(bookLevel,bookId,types);
                break;
            /********中小学**********/
//            case TypeLibrary.BookType.junior_primary://小学
//                getJuniorPrimaryChapterData(types, bookId);
//                break;
//            case TypeLibrary.BookType.junior_middle://初中
//                getJuniorMiddleChapterData(types, bookId);
//                break;
        }
    }

    /**************数据****************/
    //中小学-获取小学的章节数据
//    private void getJuniorPrimaryChapterData(String types,String bookId){
//        checkViewAttach();
//        RxUtil.unDisposable(primaryChapterDis);
//        JuniorDataManager.getPrimaryChapter(bookId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<BaseBean_data<List<Junior_chapter>>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        primaryChapterDis = d;
//                    }
//
//                    @Override
//                    public void onNext(BaseBean_data<List<Junior_chapter>> bean) {
//                        if (getMvpView()!=null){
//                            if (bean!=null&&bean.getResult().equals("1")&&bean.getData()!=null){
//                                //插入数据库
//                                JuniorDataManager.saveChapterToDB(RemoteTransUtil.transJuniorChapterData(types,bean.getData()));
//                                //从数据库取出
//                                List<ChapterEntity_junior> list = JuniorDataManager.getMultiChapterFromDB(bookId);
//                                //展示数据
//                                getMvpView().showData(DBTransUtil.transJuniorChapterData(types,list));
//                            }else {
//                                getMvpView().showData(null);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (getMvpView()!=null){
//                            getMvpView().showData(null);
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }
//
//    //中小学-获取初中的章节数据
//    private void getJuniorMiddleChapterData(String types,String bookId){
//        checkViewAttach();
//        RxUtil.unDisposable(middleChapterDis);
//        JuniorDataManager.getMiddleChapter(bookId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<BaseBean_data<List<Junior_chapter>>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        middleChapterDis = d;
//                    }
//
//                    @Override
//                    public void onNext(BaseBean_data<List<Junior_chapter>> bean) {
//                        if (getMvpView()!=null){
//                            if (bean!=null&&bean.getResult().equals("1")&&bean.getData()!=null){
//                                //插入数据库
//                                JuniorDataManager.saveChapterToDB(RemoteTransUtil.transJuniorChapterData(types,bean.getData()));
//                                //从数据库取出
//                                List<ChapterEntity_junior> list = JuniorDataManager.getMultiChapterFromDB(bookId);
//                                //展示数据
//                                getMvpView().showData(DBTransUtil.transJuniorChapterData(types,list));
//                            }else {
//                                getMvpView().showData(null);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (getMvpView()!=null){
//                            getMvpView().showData(null);
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

    /****************小说*************/
    //加载小说的章节数据
    private void loadNovelChapterData(String level,String orderNumber,String from){
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
                                getMvpView().showData(DBTransUtil.novelToChapterData(list));
                            }else {
                                getMvpView().showData(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showData(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
