package com.iyuba.talkshow.lil.help_fix.ui.study;

import android.text.TextUtils;
import android.util.Pair;

import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.lil.help_fix.data.bean.BookChapterBean;
import com.iyuba.talkshow.lil.help_fix.data.bean.ChapterDetailBean;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.NovelDataManager;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterDetailEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.util.DBTransUtil;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_bookInfo_texts;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Collect_chapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_book;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_chapter_detail;
import com.iyuba.talkshow.lil.help_fix.model.remote.util.RemoteTransUtil;
import com.iyuba.talkshow.lil.help_mvp.mvp.BasePresenter;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/5/22 16:03
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class StudyPresenter extends BasePresenter<StudyView> {

    //获取中小学章节详情数据
    private Disposable juniorChapterDetailDis;
    //获取小说的章节详情数据
    private Disposable novelChapterDetailDis;

    //收藏文章
    private Disposable collectArticleDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(novelChapterDetailDis);
        RxUtil.unDisposable(juniorChapterDetailDis);

        RxUtil.unDisposable(collectArticleDis);
    }

    //获取当前章节的数据
    private BookChapterBean getChapterData(String types, String voaId){
        if (TextUtils.isEmpty(types)){
            return null;
        }

        switch (types){
//            case TypeLibrary.BookType.junior_primary://小学
//            case TypeLibrary.BookType.junior_middle://初中
//                //中小学
//                ChapterEntity_junior junior = JuniorDataManager.getSingleChapterFromDB(voaId);
//                return DBTransUtil.transJuniorSingleChapterData(types,junior);
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                //小说
                ChapterEntity_novel novel = NovelDataManager.searchSingleChapterFromDB(types, voaId);
                return DBTransUtil.novelToSingleChapterData(novel);
        }
        return null;
    }

    //获取当前书籍的章节数据
    public List<BookChapterBean> getMultiChapterData(String types,String level,String bookId){
        List<BookChapterBean> list = new ArrayList<>();

        if (TextUtils.isEmpty(types)){
            return list;
        }

        switch (types){
//            case TypeLibrary.BookType.junior_primary://小学
//            case TypeLibrary.BookType.junior_middle://初中
//                //中小学
//                ChapterEntity_junior junior = JuniorDataManager.getSingleChapterFromDB(voaId);
//                return DBTransUtil.transJuniorSingleChapterData(types,junior);
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                //小说
                List<ChapterEntity_novel> novel = NovelDataManager.searchMultiChapterFromDB(types, level, bookId);
                if (novel!=null&&novel.size()>0){
                    list = DBTransUtil.novelToChapterData(novel);
                }
        }
        return list;
    }

    //获取当前章节详情数据
    public void getChapterDetail(String types,String bookId,String voaId){
        switch (types){
//            case TypeLibrary.BookType.junior_primary://小学
//            case TypeLibrary.BookType.junior_middle://初中
//                //中小学
//                List<ChapterDetailEntity_junior> juniorList = JuniorDataManager.getMultiChapterDetailFromDB(voaId);
//                if (juniorList!=null&&juniorList.size()>0){
//                    getMvpView().showData(DBTransUtil.transJuniorChapterDetailData(juniorList));
//                }else {
//                    if (!NetworkUtil.isConnected(ResUtil.getInstance().getContext())){
//                        ToastUtil.showToast(ResUtil.getInstance().getContext(), "请链接网络后重试~");
//                        return;
//                    }
//
//                    getMvpView().showLoading("正在加载详情内容~");
//                    loadJuniorChapterDetail(types, bookId, voaId);
//                }
//                break;
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                //小说
                List<ChapterDetailEntity_novel> novelList = NovelDataManager.searchMultiChapterDetailFromDB(types,voaId);
                if (novelList!=null&&novelList.size()>0){
                    getMvpView().showData(DBTransUtil.novelToChapterDetailData(novelList));
                }else {
                    if (!NetworkUtil.isConnected(ResUtil.getInstance().getContext())){
                        ToastUtil.showToast(ResUtil.getInstance().getContext(), "请链接网络后重试~");
                        return;
                    }

                    getMvpView().showLoading("正在加载详情内容~");
                    loadNovelChapterDetailData(types, voaId);
                }
                break;
        }
    }

    /********************中小学***********************/
    //获取中小学的章节详情数据
//    private void loadJuniorChapterDetail(String types,String bookId,String voaId){
//        checkViewAttach();
//        RxUtil.unDisposable(juniorChapterDetailDis);
//        JuniorDataManager.getJuniorChapterDetail(voaId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<BaseBean_voatext<List<Junior_chapter_detail>>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        juniorChapterDetailDis = d;
//                    }
//
//                    @Override
//                    public void onNext(BaseBean_voatext<List<Junior_chapter_detail>> bean) {
//                        if (getMvpView()!=null){
//                            if (bean!=null&&bean.getVoatext()!=null){
//                                //保存在数据库
//                                JuniorDataManager.saveChapterDetailToDB(RemoteTransUtil.transJuniorChapterDetailData(types,bookId,voaId,bean.getVoatext()));
//                                //从数据库取出
//                                List<ChapterDetailEntity_junior> list = JuniorDataManager.getMultiChapterDetailFromDB(voaId);
//                                //展示数据
//                                getMvpView().showData(DBTransUtil.transJuniorChapterDetailData(list));
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

    /********************小说*************************/
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
                                getMvpView().showData(DBTransUtil.novelToChapterDetailData(list));
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


    /********************其他操作**********************/
    //合并章节数据，判断是否格外选项显示
    public BookChapterBean margeChapterData(String types, String voaId){
        BookChapterBean chapterBean = getChapterData(types, voaId);
        //判断单词是否显示
        if (chapterBean!=null){
            chapterBean.setShowWord(false);
        }
        return chapterBean;
    }


    /****************************************收藏/取消收藏****************************/
    //收藏/取消收藏文章
    public void collectArticle(String types,String voaId,String userId,boolean isCollect){
        if (TextUtils.isEmpty(types)){
            ToastUtil.showToast(ResUtil.getInstance().getContext(), "暂无该类型数据");
            return;
        }

        switch (types){
//            case TypeLibrary.BookType.junior_primary:
//            case TypeLibrary.BookType.junior_middle:
//                //中小学
//                collectJuniorArticle(types, voaId, userId, isCollect);
//                break;
            case TypeLibrary.BookType.bookworm:
            case TypeLibrary.BookType.newCamstory:
            case TypeLibrary.BookType.newCamstoryColor:
                //小说
                collectNovelArticle(types, voaId, userId, isCollect);
                break;
        }
    }

    //中小学-收藏/取消收藏文章
//    public void collectJuniorArticle(String types,String voaId,String userId,boolean isCollect){
//        checkViewAttach();
//        RxUtil.unDisposable(collectArticleDis);
//        JuniorDataManager.collectArticle(types,userId,voaId,isCollect)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Collect_chapter>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        collectArticleDis = d;
//                    }
//
//                    @Override
//                    public void onNext(Collect_chapter bean) {
//                        if (getMvpView()!=null){
//                            if (bean!=null&&bean.msg.equals("Success")){
//                                getMvpView().showCollectArticle(true,isCollect);
//                                //刷新收藏界面回调
//                                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.junior_lesson_collect));
//                            }else {
//                                getMvpView().showCollectArticle(false,isCollect);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (getMvpView()!=null){
//                            getMvpView().showCollectArticle(false,false);
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

    //小说-收藏/取消收藏文章
    public void collectNovelArticle(String types,String voaId,String userId,boolean isCollect){
        checkViewAttach();
        RxUtil.unDisposable(collectArticleDis);
        NovelDataManager.collectArticle(types, voaId, userId, isCollect)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Collect_chapter>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        collectArticleDis = d;
                    }

                    @Override
                    public void onNext(Collect_chapter bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.msg.equals("Success")){
                                getMvpView().showCollectArticle(true,isCollect);
                                //刷新收藏界面回调
                                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.novel_lesson_collect));
                            }else {
                                getMvpView().showCollectArticle(false,isCollect);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showCollectArticle(false,false);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**********************************获取当前章节的位置***************************/
    //获取上下文数据位置
    //-1:只有一个，-2：最后一个
    public Pair<Integer,Pair<BookChapterBean,BookChapterBean>> getCurChapterIndex(String types, String level, String bookId,String voaId){
        if (TextUtils.isEmpty(types)){
            return new Pair<>(0,new Pair<>(null,null));
        }

        List<BookChapterBean> list = getMultiChapterData(types, level, bookId);
        if (list!=null&&list.size()>0){
            int index = 0;

            bookChapter:for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getVoaId().equals(voaId)){
                    index = i;
                    break bookChapter;
                }
            }

            //只有一个
            if (list.size()==1){
                return new Pair<>(-1,new Pair<>(null,null));
            }

            //第一个
            if (index==0){
                int nextIndex = index+1;
                return new Pair<>(index,new Pair<>(null,list.get(nextIndex)));
            }

            //最后一个
            if (index==list.size()-1){
                int preIndex = index-1;
                return new Pair<>(-2,new Pair<>(list.get(preIndex),null));
            }

            if (index>0&&index<list.size()-1){
                int preIndex = index-1;
                int nextIndex = index+1;

                return new Pair<>(index,new Pair<>(list.get(preIndex),list.get(nextIndex)));
            }


        }

        return new Pair<>(0,new Pair<>(null,null));
    }

    //获取随机的数据位置
    public BookChapterBean getRandomChapterData(String types, String level, String bookId){
        List<BookChapterBean> list = getMultiChapterData(types, level, bookId);
        if (list!=null&&list.size()>0){
            //使用随机数进行处理
            int randomInt = (int) (Math.random()*list.size())-1;
            return list.get(randomInt);
        }
        return null;
    }

    //获取不重复的随机的数据位置
    public BookChapterBean getRandomChapterDataNew(String types, String level, String bookId,String curvoaId){
        List<BookChapterBean> list = getMultiChapterData(types, level, bookId);
        if (list!=null&&list.size()>0){
            //使用随机数进行处理
            int randomInt = (int) (Math.random()*list.size())-1;
            BookChapterBean newData =  list.get(randomInt);

            //判断是否为当前数据
            if (newData.getVoaId().equals(curvoaId)){
                if (list.size()==1){
                    return newData;
                }

                if (randomInt==0){
                    return list.get(randomInt+1);
                }

                if (randomInt==list.size()-1){
                    return list.get(randomInt-1);
                }

                return list.get(randomInt+1);
            }

            return list.get(randomInt);
        }
        return null;
    }
}
