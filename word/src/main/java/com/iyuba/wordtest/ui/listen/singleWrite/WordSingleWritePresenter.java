package com.iyuba.wordtest.ui.listen.singleWrite;

import android.app.Activity;
import android.content.Context;

import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.entity.TalkShowWrite;
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

public class WordSingleWritePresenter extends BasePresenter<WordSingleWriteMvpView> {

    @Override
    public void detachView() {
        super.detachView();
    }

    /******************************数据库操作*****************************/
    //获取已经手写过的单词数据-全部
    public List<TalkShowWrite> getUnitWriteWordList(Context context, int bookId, int unitId, String uid){
        return WordDataBase.getInstance(context).getTalkShowWriteDao().getSpellWordData(bookId, unitId, uid);
    }

    //获取已经手写过的单词数据-正确
    public List<TalkShowWrite> getUnitRightWriteWordList(Context context,int bookId,int unitId,String uid){
        return WordDataBase.getInstance(context).getTalkShowWriteDao().getRightSpellWordData(bookId, unitId, uid);
    }

    //插入单个手写的单词数据
    public long insertSingleUnitWriteWord(Context context,TalkShowWrite listen){
        return WordDataBase.getInstance(context).getTalkShowWriteDao().insertSpellWord(listen);
    }

    //删除当前单元手写的单词
    public void  deleteUnitWriteWord(Context context,int bookId,int unitId,String uid){
        WordDataBase.getInstance(context).getTalkShowWriteDao().deleteSpellWord(bookId, unitId, uid);
    }

    /********************************原始数据操作***************************************/
    //获取当前单元需要展示的单词信息
    public List<TalkShowWords> getUnitWordList(Context context, int bookId, int unitId){
        return WordDataBase.getInstance(context).getTalkShowWordsDao().getUnitWords(bookId,unitId);
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
                        getMvpView().wordSearchSuccess(wordEntity);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView() != null) {
                            if (MediaUtils.isConnected((Activity) getMvpView())) {
                                getMvpView().wordSearchFail("获取单词网络播放地址失败");
                            } else {
                                getMvpView().wordSearchFail("暂时没有这个单词的音频，请打开数据网络播放。");
                            }
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //更新当前单词数据（没有音频的话）
    public long updateSingleWord(Context context,TalkShowWords words){
        return WordDataBase.getInstance(context).getTalkShowWordsDao().updateSingleWord(words);
    }

    /*************************************其他操作***************************************/
    //将结果数据转换为展示数据
    public List<WordListenShowBean> transResultShowData(List<TalkShowWrite> writes){
        List<WordListenShowBean> showList = new ArrayList<>();
        if (writes!=null && writes.size()>0){
            for (int i = 0; i < writes.size(); i++) {
                TalkShowWrite writeData = writes.get(i);

                showList.add(new WordListenShowBean(
                        writeData.book_id,
                        writeData.unit_id,
                        writeData.position,
                        writeData.uid,
                        writeData.word,
                        writeData.porn,
                        writeData.def,
                        writeData.audio,
                        writeData.spell,
                        writeData.status,
                        writeData.error_count,
                        writeData.update_time
                ));
            }
        }

        return showList;
    }
}
