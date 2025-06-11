package com.iyuba.wordtest.ui.listen.singleListen;

import android.app.Activity;
import android.content.Context;

import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.TalkShowListen;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.entity.WordEntity;
import com.iyuba.wordtest.network.HttpManager;
import com.iyuba.wordtest.ui.listen.bean.WordListenShowBean;
import com.iyuba.wordtest.utils.MediaUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @desction:
 * @date: 2023/2/7 10:19
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class WordSingleListenPresenter extends BasePresenter<WordSingleListenMvpView> {

    @Override
    public void detachView() {
        super.detachView();
    }

    //获取当前单元拼写的单词信息
    public List<TalkShowListen> getUnitListenWordList(Context context,int bookId,int unitId,String uid){
        return WordDataBase.getInstance(context).getTalkShowListenDao().getSpellWordData(bookId, unitId, uid);
    }

    //获取当前单元正确的拼写的单词信息
    public List<TalkShowListen> getUnitRightListenWordList(Context context,int bookId,int unitId,String uid){
        return WordDataBase.getInstance(context).getTalkShowListenDao().getRightSpellWordData(bookId, unitId, uid);
    }

    //插入当前单元拼写的单词
    public long insertSingleUnitListenWord(Context context,TalkShowListen listen){
        return WordDataBase.getInstance(context).getTalkShowListenDao().insertSpellWord(listen);
    }

    //删除当前单元拼写的单词
    public void  deleteUnitListenWord(Context context,int bookId,int unitId,String uid){
        WordDataBase.getInstance(context).getTalkShowListenDao().deleteSpellWord(bookId, unitId, uid);
    }

    //获取当前单元单词信息
    public List<TalkShowWords> getUnitWordList(Context context,int bookId, int unitId){
        return WordDataBase.getInstance(context).getTalkShowWordsDao().getUnitWords(bookId,unitId);
    }

    //更新当前单词数据（没有音频的话）
    public long updateSingleWord(Context context,TalkShowWords words){
        return WordDataBase.getInstance(context).getTalkShowWordsDao().updateSingleWord(words);
    }

    //查询单词信息
    public void searchWord(String wordTemp){
        HttpManager.getWordApi().getWordApi(wordTemp)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<WordEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WordEntity wordEntity) {
                        getMvpView().showSearchResult(wordEntity);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView() != null) {
                            if (MediaUtils.isConnected((Activity) getMvpView())) {
                                getMvpView().showText("获取单词网络播放地址失败");
                            } else {
                                getMvpView().showText("暂时没有这个单词的音频，请打开数据网络播放。");
                            }
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //将数据库的结果数据转换成需要展示的数据
    public List<WordListenShowBean> transResultShowData(List<TalkShowListen> listens){
        List<WordListenShowBean> showList = new ArrayList<>();
        if (listens!=null &&listens.size()>0){
            for (int i = 0; i < listens.size(); i++) {
                TalkShowListen listenData = listens.get(i);

                showList.add(new WordListenShowBean(
                        listenData.book_id,
                        listenData.unit_id,
                        listenData.position,
                        listenData.uid,
                        listenData.word,
                        listenData.porn,
                        listenData.def,
                        listenData.audio,
                        listenData.spell,
                        listenData.status,
                        listenData.error_count,
                        listenData.update_time
                ));
            }
        }

        return showList;
    }
}
