package com.iyuba.talkshow.newce.wordstep;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.AllWordsRespons;
import com.iyuba.talkshow.data.model.ExamWord;
import com.iyuba.talkshow.data.model.ExamWordResponse;
import com.iyuba.talkshow.data.model.UpdateWordResponse;
import com.iyuba.talkshow.event.RefreshWordEvent;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newce.study.word.WordMvpView;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.LogUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.wordtest.db.BookLevelDao;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.BookLevels;
import com.iyuba.wordtest.entity.NewBookLevels;
import com.iyuba.wordtest.entity.TalkShowTests;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.manager.WordManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carl shen on 2020/11/16
 * New Primary English, new study experience.
 */
@ConfigPersistent
public class WordStepPresenter extends BasePresenter<WordMvpView> {

    private final DataManager mDataManager;

    private Subscription mLoadNewSub;
    private Subscription mLoadSub;
    private Subscription mLoadWordSub;
    private Subscription mLoginSub;

    @Inject
    public WordStepPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mLoadNewSub);
        RxUtil.unsubscribe(mLoadSub);
        RxUtil.unsubscribe(mLoadWordSub);
        RxUtil.unsubscribe(mLoginSub);
    }

    /*public void registerToken(final String token, final String opTopken, String operator) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoginSub);
        mLoginSub = mDataManager.registerByMob(token, opTopken, operator)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RegisterMobResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("WordStepPresenter", "registerToken onError  " + e.getMessage());
                        }
                        getMvpView().goResultActivity(null);
                    }

                    @Override
                    public void onNext(RegisterMobResponse response) {
                        if (response == null) {
                            Log.e("MainFragPresenter", "registerToken onNext response is null.");
                            getMvpView().goResultActivity(null);
                            return;
                        }
                        Log.e("WordStepPresenter", "registerToken onNext isLogin " + response.isLogin);
                        if (1 == response.isLogin) {
                            getMvpView().goResultActivity(new LoginResult());
                            getMvpView().showToastShort("您已经登录成功，可以进行学习了。");
                            // already login, need update user info
                            if (response.userinfo != null) {
                                mAccountManager.setUser(response.userinfo, "");
                                mAccountManager.saveUser();
                            } else {
                                Log.e("WordStepPresenter", "registerToken onNext response.userinfo is null.");
                            }
                            EventBus.getDefault().post(new LoginEvent());
                        } else {
                            if (response.res != null) {
                                // register by this phone
                                RegisterMobResponse.MobBean mobBean = response.res;
                                LoginResult loginResult = new LoginResult();
                                loginResult.setPhone(mobBean.phone);
                                getMvpView().goResultActivity(loginResult);
                            } else {
                                Log.e("WordStepPresenter", "registerToken onNext response.res is null.");
                                getMvpView().goResultActivity(null);
                            }
                        }
                    }
                });
    }*/

    public void getAllWords(int bookId) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoadSub);
        mLoadSub = mDataManager.getAllWordsByType(App.DEFAULT_WORDS)
                .compose(RxUtil.io2main())
                .subscribe(new Subscriber<AllWordsRespons>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("WordStepPresenter", "getAllWords onError  " + e.getMessage());
                        }
                        getMvpView().dismissLoadingDialog();
                    }

                    @Override
                    public void onNext(AllWordsRespons respons) {
                        getMvpView().dismissLoadingDialog();
                        if (respons == null || respons.getData() == null) {
                            Log.e("WordStepPresenter", "getAllWords onNext response is null. ");
                            return;
                        }
                        TalkShowApplication.getSubHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                WordDataBase.getInstance(TalkShowApplication.getInstance()).getTalkShowWordsDao().insertWord(respons.getData());
                                Log.e("WordStepPresenter", "getAllWords onNext response getSize() " + respons.getSize());
                                EventBus.getDefault().post(new RefreshWordEvent(bookId, 0));
                            }
                        });
                    }
                });
    }

    public void getWordsById(int bookId) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoadWordSub);
        mLoadWordSub = mDataManager.getWordByBookId(bookId)
                .compose(RxUtil.io2main())
                .subscribe(new Subscriber<UpdateWordResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("WordStepPresenter", "getWordsById onError  " + e.getMessage());
                        }
                        EventBus.getDefault().post(new RefreshWordEvent(bookId, 0));
                        getMvpView().dismissLoadingDialog();
                    }

                    @Override
                    public void onNext(UpdateWordResponse respons) {
                        getMvpView().dismissLoadingDialog();
                        if (respons == null || respons.getData() == null) {
                            EventBus.getDefault().post(new RefreshWordEvent(bookId, 0));
                            Log.e("WordStepPresenter", "getWordsById onNext response is null. ");
                            return;
                        }
                        TalkShowApplication.getSubHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                //插入单词数据
                                WordDataBase.getInstance(TalkShowApplication.getInstance()).getTalkShowWordsDao().insertWord(respons.getData());

                                //格局类型查询数据
                                if (WordManager.WordDataVersion == 2) {
                                    NewBookLevels newLevels = WordDataBase.getInstance(TalkShowApplication.getInstance()).getNewBookLevelDao().getBookLevel(bookId,String.valueOf(UserInfoManager.getInstance().getUserId()));
                                    if (newLevels == null) {
                                        newLevels = new NewBookLevels(bookId, 0, 0, 0, String.valueOf(UserInfoManager.getInstance().getUserId()));
                                        newLevels.version = respons.getBookVersion();
                                        WordDataBase.getInstance(TalkShowApplication.getInstance()).getNewBookLevelDao().saveBookLevel(newLevels);
                                    }
                                    EventBus.getDefault().post(new RefreshWordEvent(bookId, 0));
                                    return;
                                }
                                BookLevels levels = WordDataBase.getInstance(TalkShowApplication.getInstance()).getBookLevelDao().getBookLevel(bookId);
                                if (levels == null) {
                                    levels = new BookLevels(bookId, 0, 0, 0);
                                }
                                levels.version = respons.getBookVersion();
                                WordDataBase.getInstance(TalkShowApplication.getInstance()).getBookLevelDao().updateBookLevel(levels);
                                EventBus.getDefault().post(new RefreshWordEvent(bookId, 0));
                            }
                        });
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void refreshWords(int bookId, int version) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoadWordSub);
        mLoadWordSub = mDataManager.updateWordByBookId(bookId, version)
                .compose(RxUtil.io2main())
                .subscribe(new Subscriber<UpdateWordResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("WordStepPresenter", "refreshWords onError  " + e.getMessage());
                        }
                        EventBus.getDefault().post(new RefreshWordEvent(bookId, 0));
                        getMvpView().dismissLoadingDialog();
                    }

                    @Override
                    public void onNext(UpdateWordResponse response) {
                        getMvpView().dismissLoadingDialog();
                        if (response == null || response.getData() == null) {
                            EventBus.getDefault().post(new RefreshWordEvent(bookId, 0));
                            Log.e("WordStepPresenter", "refreshWords onNext response is null. ");
                            return;
                        }
                        TalkShowApplication.getSubHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                WordDataBase.getInstance(TalkShowApplication.getInstance()).getTalkShowWordsDao().insertWord(response.getData());
                                if (WordManager.WordDataVersion == 2) {
                                    NewBookLevels newLevels = WordDataBase.getInstance(TalkShowApplication.getInstance()).getNewBookLevelDao().getBookLevel(bookId,String.valueOf(UserInfoManager.getInstance().getUserId()));
                                    if (newLevels == null) {
                                        newLevels = new NewBookLevels(bookId, 0, 0, 0, String.valueOf(UserInfoManager.getInstance().getUserId()));
                                        newLevels.version = response.getBookVersion();
                                        WordDataBase.getInstance(TalkShowApplication.getInstance()).getNewBookLevelDao().saveBookLevel(newLevels);
                                    }
                                    EventBus.getDefault().post(new RefreshWordEvent(bookId, 0));
                                    return;
                                }
                                BookLevels levels = WordDataBase.getInstance(TalkShowApplication.getInstance()).getBookLevelDao().getBookLevel(bookId);
                                if (levels == null) {
                                    levels = new BookLevels(bookId, 0, 0, 0);
                                }
                                levels.version = response.getBookVersion();
                                WordDataBase.getInstance(TalkShowApplication.getInstance()).getBookLevelDao().updateBookLevel(levels);
                                EventBus.getDefault().post(new RefreshWordEvent(bookId, 0));
                            }
                        });
                    }
                });
    }

    public void syncExamWord(int bookId, boolean showTip) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoadNewSub);
        mLoadNewSub = mDataManager.getExamWordDetail(UserInfoManager.getInstance().getUserId(),
                "" + App.APP_ID, "" + bookId, "W", 2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExamWordResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissLoadingDialog();
                        if (e != null) {
                            Log.e("WordstepFragment", "getVoaSeries onError  " + e.getMessage());
                        }
                        if (showTip) {
                            getMvpView().showToastShort("同步失败！");
                        }
                    }

                    @Override
                    public void onNext(ExamWordResponse response) {
                        getMvpView().dismissLoadingDialog();
                        if (showTip) {
                            getMvpView().showToastShort("同步成功！");
                        }
                        if (response == null) {
                            Log.e("WordstepFragment", "syncExamWord onNext response is null. ");
                            return;
                        }
                        Log.e("WordstepFragment", "syncExamWord onNext response.uid " + response.uid);
                        TalkShowApplication.getSubHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                int largeUnit = 0;
                                WordDataBase db = WordDataBase.getInstance(TalkShowApplication.getInstance());
                                if (db != null && response.totalWrong > 0) {
                                    List<ExamWord> rightWords = response.dataWrong;
                                    TalkShowWords talkShowWords;
                                    for (ExamWord words: rightWords) {
                                        talkShowWords = db.getTalkShowWordsDao().getUnitWord(words.Lesson, words.LessonId, words.TestId);
                                        if (talkShowWords == null) {
                                            Log.e("WordstepFragment", "syncExamWord getUnitWord is null. " );
                                            talkShowWords = new TalkShowWords();
                                            talkShowWords.answer = "1";
                                            talkShowWords.wrong = words.score;
                                            talkShowWords.updateTime = words.testTime;
                                            talkShowWords.book_id = response.lesson;
                                            talkShowWords.unit_id = words.LessonId;
                                            talkShowWords.position = words.TestId;
                                            talkShowWords.word = words.userAnswer;
                                            talkShowWords.version = 0;
                                            talkShowWords.voa_id = 0;
                                            talkShowWords.idindex = 0;
                                            talkShowWords.pic_url = "";
                                            talkShowWords.audio = "";
                                            talkShowWords.examples = "";
                                            talkShowWords.pron = "";
                                            talkShowWords.def = "";
                                            talkShowWords.flag = 0;
                                            talkShowWords.Sentence = "";
                                            talkShowWords.Sentence_audio = "";
                                            talkShowWords.Sentence_cn = "";
                                            int result = db.getTalkShowWordsDao().updateSingleWord(talkShowWords);
                                            Log.e("WordstepFragment", "syncExamWord insertWord result " + result);
                                            long[] update = db.getTalkShowTestsDao().insertWord(WordManager.getInstance().Words2Tests(talkShowWords, String.valueOf(UserInfoManager.getInstance().getUserId())));
                                            Log.e("WordstepFragment", "syncExamWord insertTest update " + update[0]);
                                        } else {
                                            talkShowWords.answer = "1";
                                            talkShowWords.wrong = words.score;
                                            talkShowWords.updateTime = words.testTime;
                                            int result = db.getTalkShowWordsDao().updateSingleWord(talkShowWords);
                                            Log.e("WordstepFragment", "syncExamWord updateSingleWord result " + result);
                                            TalkShowTests talkTest = db.getTalkShowTestsDao().getUnitWord(talkShowWords.book_id, talkShowWords.unit_id, talkShowWords.position, String.valueOf(UserInfoManager.getInstance().getUserId()));
                                            if ((talkTest == null)) {
                                                long[] update = db.getTalkShowTestsDao().insertWord(WordManager.getInstance().Words2Tests(talkShowWords, String.valueOf(UserInfoManager.getInstance().getUserId())));
                                                Log.e("WordstepFragment", "syncExamWord updateTest update " + update[0]);
                                            } else {
                                                int update = db.getTalkShowTestsDao().updateSingleWord(WordManager.getInstance().Words2Tests(talkShowWords, String.valueOf(UserInfoManager.getInstance().getUserId())));
                                                Log.e("WordstepFragment", "syncExamWord updateSingleWord update " + update);
                                            }
                                        }
                                        if (talkShowWords.unit_id > largeUnit) {
                                            largeUnit = talkShowWords.unit_id;
                                        }
                                    }
                                }
                                if (db != null && response.totalRight > 0) {
                                    List<ExamWord> rightWords = response.dataRight;
                                    TalkShowWords talkShowWords;
                                    for (ExamWord words: rightWords) {
                                        talkShowWords = db.getTalkShowWordsDao().getUnitWord(words.Lesson, words.LessonId, words.TestId);
                                        if (talkShowWords == null) {
                                            Log.e("WordstepFragment", "syncExamWord getUnitWord is null. " );
                                            talkShowWords = new TalkShowWords();
                                            talkShowWords.answer = "1";
                                            talkShowWords.wrong = words.score;
                                            talkShowWords.updateTime = words.testTime;
                                            talkShowWords.book_id = response.lesson;
                                            talkShowWords.unit_id = words.LessonId;
                                            talkShowWords.position = words.TestId;
                                            talkShowWords.word = words.userAnswer;
                                            talkShowWords.version = 0;
                                            talkShowWords.voa_id = 0;
                                            talkShowWords.idindex = 0;
                                            talkShowWords.pic_url = "";
                                            talkShowWords.audio = "";
                                            talkShowWords.examples = "";
                                            talkShowWords.pron = "";
                                            talkShowWords.def = "";
                                            talkShowWords.flag = 0;
                                            talkShowWords.Sentence = "";
                                            talkShowWords.Sentence_audio = "";
                                            talkShowWords.Sentence_cn = "";
                                            int result = db.getTalkShowWordsDao().updateSingleWord(talkShowWords);
                                            Log.e("WordstepFragment", "syncExamWord insertWord result " + result);
                                            long[] update = db.getTalkShowTestsDao().insertWord(WordManager.getInstance().Words2Tests(talkShowWords, String.valueOf(UserInfoManager.getInstance().getUserId())));
                                            Log.e("WordstepFragment", "syncExamWord insertTest update " + update[0]);
                                        } else {
                                            talkShowWords.answer = "1";
                                            talkShowWords.wrong = words.score;
                                            talkShowWords.updateTime = words.testTime;
                                            int result = db.getTalkShowWordsDao().updateSingleWord(talkShowWords);
                                            Log.e("WordstepFragment", "syncExamWord updateSingleWord result " + result);
                                            TalkShowTests talkTest = db.getTalkShowTestsDao().getUnitWord(talkShowWords.book_id, talkShowWords.unit_id, talkShowWords.position, String.valueOf(UserInfoManager.getInstance().getUserId()));
                                            if ((talkTest == null)) {
                                                long[] update = db.getTalkShowTestsDao().insertWord(WordManager.getInstance().Words2Tests(talkShowWords, String.valueOf(UserInfoManager.getInstance().getUserId())));
                                                Log.e("WordstepFragment", "syncExamWord updateTest update " + update[0]);
                                            } else {
                                                int update = db.getTalkShowTestsDao().updateSingleWord(WordManager.getInstance().Words2Tests(talkShowWords, String.valueOf(UserInfoManager.getInstance().getUserId())));
                                                Log.e("WordstepFragment", "syncExamWord updateSingleWord update " + update);
                                            }
                                        }
                                        if (talkShowWords.unit_id > largeUnit) {
                                            largeUnit = talkShowWords.unit_id;
                                        }
                                    }
                                }
                                if (db != null && largeUnit > 0) {
                                    /*while (largeUnit > 0) {
                                        List<TalkShowWords> words = db.getTalkShowWordsDao().getUnitWords(bookId, largeUnit);
                                        if (words != null && words.size() > 0) {
                                            int sum = 0;
                                            for (TalkShowWords words1: words) {
                                                if (words1.wrong < 1) {
                                                    sum++;
                                                }
                                            }
                                            if (sum * 100 / words.size() > 20) {
                                                largeUnit--;
                                            } else {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    }*/

                                    //原来的逻辑：这里的操作是先获取全的单词数据，然后将错误单词的比率>20%时则回退
                                    //正确的逻辑：获取全部的单词数据，正确单词的数量>80%才能确认，光计算错误的不行
                                    while (largeUnit > 0) {
                                        //总单词数量
                                        List<TalkShowWords> words = db.getTalkShowWordsDao().getUnitWords(bookId, largeUnit);
                                        //已经测试的单词数量
                                        List<TalkShowTests> tests = db.getTalkShowTestsDao().getAnswerWords(bookId,largeUnit,"1",String.valueOf(UserInfoManager.getInstance().getUserId()));
                                        if (words != null && words.size() > 0) {
                                            int sum = 0;
                                            for (TalkShowTests words1: tests) {
                                                //这里计算正确的数量
                                                if (words1.wrong == 1) {
                                                    sum++;
                                                }
                                            }

                                            //这里计算正确数量的比率
                                            if (sum * 100 / words.size() < 80) {
                                                largeUnit--;
                                            } else {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    }

                                    if (WordManager.WordDataVersion == 2) {
                                        NewBookLevels newLevels = db.getNewBookLevelDao().getBookLevel(bookId,String.valueOf(UserInfoManager.getInstance().getUserId()));
                                        if (newLevels == null) {
                                            newLevels = new NewBookLevels(bookId, 0, 0, 0, String.valueOf(UserInfoManager.getInstance().getUserId()));
                                            newLevels.level = largeUnit;
                                            db.getNewBookLevelDao().saveBookLevel(newLevels);
                                        } else {
                                            newLevels.level = largeUnit;
                                            db.getNewBookLevelDao().updateBookLevel(newLevels);
                                        }
                                        Log.e("WordstepFragment", "syncExamWord newLevels.level " + newLevels.level);
                                        EventBus.getDefault().post(new RefreshWordEvent(bookId, 0));
                                    } else {
                                        BookLevelDao bookLevelDao = db.getBookLevelDao();
                                        BookLevels levels = bookLevelDao.getBookLevel(bookId);
                                        if (levels == null) {
                                            levels = new BookLevels(bookId,0,0 ,0);
                                        }
                                        levels.level = largeUnit;
                                        Log.e("WordstepFragment", "syncExamWord levels.level " + levels.level);
                                        bookLevelDao.updateBookLevel(levels);
                                        EventBus.getDefault().post(new RefreshWordEvent(bookId, 0));
                                    }
                                } else if (db != null) {
                                    if (WordManager.WordDataVersion == 2) {
                                        NewBookLevels newLevels = db.getNewBookLevelDao().getBookLevel(bookId,String.valueOf(UserInfoManager.getInstance().getUserId()));
                                        if (newLevels == null) {
                                            newLevels = new NewBookLevels(bookId, 0, 0, 0, String.valueOf(UserInfoManager.getInstance().getUserId()));
                                            db.getNewBookLevelDao().saveBookLevel(newLevels);
                                        } else {
                                            newLevels.level = 0;
                                            db.getNewBookLevelDao().updateBookLevel(newLevels);
                                        }
                                        Log.e("WordstepFragment", "syncExamWord newLevels.level " + newLevels.level);
                                        EventBus.getDefault().post(new RefreshWordEvent(bookId, 0));
                                    } else {
                                        BookLevelDao bookLevelDao = db.getBookLevelDao();
                                        BookLevels levels = bookLevelDao.getBookLevel(bookId);
                                        if (levels == null) {
                                            levels = new BookLevels(bookId,0,0 ,0);
                                        } else {
                                            levels.level = 0;
                                        }
                                        Log.e("WordstepFragment", "syncExamWord levels.level " + levels.level);
                                        bookLevelDao.updateBookLevel(levels);
                                        EventBus.getDefault().post(new RefreshWordEvent(bookId, 0));
                                    }
                                }
                            }
                        });
                    }
                });
    }

}
