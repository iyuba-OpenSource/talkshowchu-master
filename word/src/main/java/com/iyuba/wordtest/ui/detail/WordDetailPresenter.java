package com.iyuba.wordtest.ui.detail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.wordtest.bean.SendEvaluateResponse;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.db.WordOp;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.entity.TalkshowTexts;
import com.iyuba.wordtest.entity.WordEntity;
import com.iyuba.wordtest.event.WordFavorEvent;
import com.iyuba.wordtest.manager.WordConfigManager;
import com.iyuba.wordtest.manager.WordManager;
import com.iyuba.wordtest.network.EvaluateApi;
import com.iyuba.wordtest.network.HttpManager;
import com.iyuba.wordtest.network.SearchTextResult;
import com.iyuba.wordtest.utils.MediaUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class WordDetailPresenter extends BasePresenter<WordDetailMvpView> {

    private Map<String, RequestBody> buildMap(String s , int position ) {
        Map<String, RequestBody> map = new HashMap<>(6);
        map.put(EvaluateApi.GetVoa.Param.Key.SENTENCE, HttpManager.fromString(s));
        map.put(EvaluateApi.GetVoa.Param.Key.IDINDEX, HttpManager.fromString(position + ""));
        map.put(EvaluateApi.GetVoa.Param.Key.NEWSID, HttpManager.fromString(position + ""));
        map.put(EvaluateApi.GetVoa.Param.Key.PARAID, HttpManager.fromString(position + ""));
        map.put(EvaluateApi.GetVoa.Param.Key.TYPE, HttpManager.fromString(WordConfigManager.EVAL_TYPE));
        map.put(EvaluateApi.GetVoa.Param.Key.USERID, HttpManager.fromString(WordManager.getInstance().userid + ""));
        return map;
    }

    void sendEvaluate(String s, TalkShowWords showWord, boolean isSent, MultipartBody.Part part){
        Map<String, RequestBody> map;
        if (isSent) {
            map = new HashMap<>(9);
            map.put(EvaluateApi.GetVoa.Param.Key.SENTENCE, HttpManager.fromString(s));
            map.put(EvaluateApi.GetVoa.Param.Key.IDINDEX, HttpManager.fromString(showWord.position + ""));
            map.put(EvaluateApi.GetVoa.Param.Key.PARAID, HttpManager.fromString(showWord.book_id + ""));
            map.put(EvaluateApi.GetVoa.Param.Key.NEWSID, HttpManager.fromString("0"));
            map.put("wordId", HttpManager.fromString("0"));
            map.put("flg", HttpManager.fromString("0"));
            map.put("appId", HttpManager.fromString(WordConfigManager.APP_ID));
            map.put(EvaluateApi.GetVoa.Param.Key.TYPE, HttpManager.fromString(WordConfigManager.EVAL_TYPE));
            map.put(EvaluateApi.GetVoa.Param.Key.USERID, HttpManager.fromString(WordManager.getInstance().userid));
        } else {
            map = new HashMap<>(9);
            map.put(EvaluateApi.GetVoa.Param.Key.SENTENCE, HttpManager.fromString(s));
            map.put(EvaluateApi.GetVoa.Param.Key.IDINDEX, HttpManager.fromString(showWord.position + ""));
            map.put(EvaluateApi.GetVoa.Param.Key.PARAID, HttpManager.fromString(showWord.book_id + ""));
            map.put(EvaluateApi.GetVoa.Param.Key.NEWSID, HttpManager.fromString("0"));
            map.put("wordId", HttpManager.fromString("0"));
            map.put("flg", HttpManager.fromString("2"));
            map.put("appId", HttpManager.fromString(WordConfigManager.APP_ID));
            map.put(EvaluateApi.GetVoa.Param.Key.TYPE, HttpManager.fromString(WordConfigManager.EVAL_TYPE));
            map.put(EvaluateApi.GetVoa.Param.Key.USERID, HttpManager.fromString(WordManager.getInstance().userid));
        }

        //区分单词和句子评测
        Observable<SendEvaluateResponse> observable = HttpManager.getEvaluateApi().evalAi(map, part);
        if (!isSent){
            observable = HttpManager.getEvaluateApi().evalAi10(map,part);
        }
                observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SendEvaluateResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SendEvaluateResponse sendEvaluateResponse) {
                        if ((getMvpView() != null) && (sendEvaluateResponse != null) && (sendEvaluateResponse.getData() != null))
                        getMvpView().showScore(Float.parseFloat(sendEvaluateResponse.getData().getTotal_score())*20,
                                sendEvaluateResponse.getData().getSentence(),sendEvaluateResponse.getData().getWords()
                        ,sendEvaluateResponse.getData().getURL());
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        if (getMvpView() != null) {
                            getMvpView().showFail();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    void getSentence(final TalkShowWords word, Context context) {
        final WordDataBase db = WordDataBase.getInstance(context.getApplicationContext());
        final TalkshowTexts texts = db
                .getTalkShowTextDao().getSentenceByWord(word.voa_id,word.idindex);
        if (texts!=null){
            getMvpView().showSentence(texts);
        }else {
            HttpManager.getSearchApi().getTexts("json",word.voa_id)
                    .concatMap(new Function<SearchTextResult, ObservableSource<TalkshowTexts>>() {
                        @Override
                        public ObservableSource<TalkshowTexts> apply(final SearchTextResult searchTextResult) {
                            if (searchTextResult == null || searchTextResult.getVoatext() == null) {
                                return null;
                            }
                            TalkshowTexts t = null;
                            for (TalkshowTexts text : searchTextResult.getVoatext()) {
                                text.voaId = word.voa_id;
                                if (text.paraId == word.idindex) {
                                    t = text;
                                    break;
                                }
                            }
                            db.getTalkShowTextDao().intserTexts(searchTextResult.getVoatext());
                            final TalkshowTexts finalT = t;
                            return Observable.create(new ObservableOnSubscribe<TalkshowTexts>() {
                                @Override
                                public void subscribe(ObservableEmitter<TalkshowTexts> emitter)  {
                                    emitter.onNext(finalT);
                                }
                            });
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<TalkshowTexts>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(TalkshowTexts text) {
                            if (getMvpView() != null) {
                                getMvpView().showSentence(text);
                            }
//                            ToastUtils.showShort("插入数据库........");
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
//                            ToastUtils.showShort("没数据啊........");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    @SuppressLint("CheckResult")
    void getNetWord(String wordTemp) {
        HttpManager.getWordApi().getWordApi(wordTemp)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<WordEntity>() {
                    @Override
                    public void accept(WordEntity s) {
                        getMvpView().showSearchResult(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable)  {
                        if (getMvpView() != null) {
                            if (MediaUtils.isConnected((Activity) getMvpView())) {
                                getMvpView().showText("获取单词网络播放地址失败");
                            } else {
                                getMvpView().showText("暂时没有这个单词的音频，请打开数据网络播放。");
                            }
                        }
                        if (throwable != null) {
                            throwable.printStackTrace();
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    void addNetWord(String wordTemp) {
        HttpManager.getWordApi().operateWord(wordTemp,"insert",
                "Iyuba", WordManager.getInstance().userid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        getMvpView().showText("单词添加成功");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable)  {
                        if (throwable != null) {
                            Log.e("WordDetailPresenter", "addNetWord throwable " + throwable.getMessage());
                        }
                        if (getMvpView() != null) {
                            WordOp wordOp = new WordOp((Context) getMvpView());
                            wordOp.deleteWord(wordTemp, Integer.parseInt(WordManager.getInstance().userid));
                            EventBus.getDefault().post(new WordFavorEvent(1));
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    void deleteNetWord(String word) {
        HttpManager.getWordApi().operateWord(word,"delete",
                "Iyuba",WordManager.getInstance().userid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        if (getMvpView() != null) {
                            getMvpView().showText("删除单词成功");
                            WordOp wordOp = new WordOp((Context) getMvpView());
                            wordOp.deleteWord(word, Integer.parseInt(WordManager.getInstance().userid));
                            EventBus.getDefault().post(new WordFavorEvent(0));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable)  {
                        throwable.printStackTrace();
                    }
                });
    }

}
