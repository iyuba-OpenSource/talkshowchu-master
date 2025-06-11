package com.iyuba.talkshow.lil.help_fix.view.dialog.searchWord;

import android.text.TextUtils;

import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Word_detail;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Word_insert;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/6/13 11:38
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class SearchWordPresenter{

    //查询单词数据
    private Disposable searchWordDis;

    //收藏/取消收藏单词
    private Disposable collectWordDis;

    //查询单词数据
    public void searchWord(String word,OnSearchWordListener onSearchWordListener){
        if (TextUtils.isEmpty(word)&&onSearchWordListener==null){
            return;
        }

        RxUtil.unDisposable(searchWordDis);
        CommonDataManager.searchWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_detail>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        searchWordDis = d;
                    }

                    @Override
                    public void onNext(Word_detail detail) {
                        if (detail!=null){
                            onSearchWordListener.onSearch(detail);
                        }else {
                            onSearchWordListener.onSearch(null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        onSearchWordListener.onSearch(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //收藏/取消收藏单词数据
    public void collectWord(String word,boolean isInsert,OnCollectWordListener onCollectWordListener){
        int userId = UserInfoManager.getInstance().getUserId();
        if (userId==0||TextUtils.isEmpty(word)||onCollectWordListener==null){
            return;
        }

        RxUtil.unDisposable(collectWordDis);
        CommonDataManager.insertOrDeleteWord(word,userId,isInsert)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_insert>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        collectWordDis = d;
                    }

                    @Override
                    public void onNext(Word_insert bean) {
                        if (bean!=null&&bean.result==1){
                            onCollectWordListener.onCollect(isInsert,true);
                        }else {
                            onCollectWordListener.onCollect(isInsert,false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        onCollectWordListener.onCollect(isInsert,false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*******************回调**************************/
    //查询单词的回调
    public interface OnSearchWordListener{
        void onSearch(Word_detail detail);
    }

    //收藏单词的回调
    public interface OnCollectWordListener{
        void onCollect(boolean isCollect,boolean isSuccess);
    }

    /******************关闭查询*****************/
    public void close(){
        RxUtil.unDisposable(searchWordDis);
        RxUtil.unDisposable(collectWordDis);
    }
}
