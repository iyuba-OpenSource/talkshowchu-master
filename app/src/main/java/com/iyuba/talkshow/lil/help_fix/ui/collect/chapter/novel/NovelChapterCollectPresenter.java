package com.iyuba.talkshow.lil.help_fix.ui.collect.chapter.novel;

import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.NovelDataManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Collect_chapter;
import com.iyuba.talkshow.lil.help_mvp.mvp.BasePresenter;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/8/31 09:45
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NovelChapterCollectPresenter extends BasePresenter<NovelChapterCollectView> {

    //收藏/取消收藏章节
    private Disposable collectChapterDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(collectChapterDis);
    }

    //收藏/取消收藏章节
    public void collectAdapter(String types,String voaId,String userId,boolean isCollect){
        switch (types){
            case TypeLibrary.BookType.bookworm:
            case TypeLibrary.BookType.newCamstory:
            case TypeLibrary.BookType.newCamstoryColor:
                //书虫、剑桥彩绘、剑桥非彩绘
                collectNovelAdapterData(types, voaId, userId, isCollect);
                break;
        }
    }

    //收藏/取消收藏小说章节
    private void collectNovelAdapterData(String types,String voaId,String userId,boolean isCollect){
        checkViewAttach();
        RxUtil.unDisposable(collectChapterDis);
        NovelDataManager.collectArticle(types, voaId, userId, isCollect)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Collect_chapter>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        collectChapterDis = d;
                    }

                    @Override
                    public void onNext(Collect_chapter bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.msg.equals("Success")){
                                //删除数据库数据
                                CommonDataManager.deleteChapterCollectDataToDB(types, voaId, UserInfoManager.getInstance().getUserId());
                                //刷新显示
                                getMvpView().showCollectResult(true);
                            }else {
                                getMvpView().showCollectResult(false);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showCollectResult(false);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
