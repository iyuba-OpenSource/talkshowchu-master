package com.iyuba.talkshow.ui.words;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;

import com.iyuba.module.toolbox.RxUtil;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.ui.base.BasePresenter;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import timber.log.Timber;


@ConfigPersistent
public class WordNotePresenter extends BasePresenter<WordNoteMvpView> {


    private final DataManager mDataManager;

    private HashSet<String> mDistinctCollection;

    private Disposable mDisposable;
    private Disposable mDeleteDisposable;
    private Disposable mInsertDisposable;

    @Inject
    public WordNotePresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
        mDistinctCollection = new HashSet<>();
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.dispose(mDisposable, mDeleteDisposable, mInsertDisposable);
        mDistinctCollection.clear();
    }

//    public Single<Boolean> deleteWords(int userId, List<String> words) {
//        String wordsStr = buildUpdateWords(words);
//        return mDataManager.updateWords(userId, "delete", "Iyuba", wordsStr)
//                .compose(this.<WordCollectResponse.Update, Boolean>applyParser());
//    }
//
//    public Single<Boolean> insertWords(int userId, List<String> words) {
//        String wordsStr = buildUpdateWords(words);
//        return mDataManager.updateWords(userId, "insert", "Iyuba", wordsStr)
//                .compose(this.<WordCollectResponse.Update, Boolean>applyParser());
//    }

    public void getLatestData(int userId, int pageCounts) {
        final HashSet<String> oldCollection = mDistinctCollection;
        mDistinctCollection = new HashSet<>();
        RxUtil.dispose(mDisposable);
        mDisposable = getDistinct(userId, 1, pageCounts)
                .compose(RxUtil.applySingleIoSchedulerWith(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().setRecyclerEndless(false);
                        }
                    }
                }))
                .subscribe(new Consumer<Pair<List<Word>, Integer>>() {
                    @Override
                    public void accept(Pair<List<Word>, Integer> pair) throws Exception {
                        List<Word> words = pair.first;
                        int total = pair.second;
                        if (isViewAttached()) {
                            getMvpView().setLoading(false);
                            if (words.size() > 0) {
                                getMvpView().onLatestDataLoaded(words, total, true);
                                getMvpView().setRecyclerEndless(true);
                            } else {
                                getMvpView().showMessage("暂无数据!");
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mDistinctCollection = oldCollection;
                        if (isViewAttached()) {
                            getMvpView().setLoading(false);
                            getMvpView().showMessage("获取失败，请稍后再试!");
                        }
                    }
                });
    }

    public void loadMore(int userId, final int pageNumber, int pageCounts) {
        RxUtil.dispose(mDisposable);
        mDisposable = getDistinct(userId, pageNumber, pageCounts)
                .compose(RxUtil.applySingleIoSchedulerWith(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().setRecyclerEndless(false);
                        }
                    }
                }))
                .subscribe(new Consumer<Pair<List<Word>, Integer>>() {
                    @Override
                    public void accept(Pair<List<Word>, Integer> pair) throws Exception {
                        if (isViewAttached()) {
                            List<Word> words = pair.first;
                            if (words.size() > 0) {
                                getMvpView().onMoreDataLoaded(words, pageNumber);
                                getMvpView().setRecyclerEndless(true);
                            } else {
                                getMvpView().showMessage("全部数据已获取完毕!");
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            getMvpView().showMessage("获取失败，请稍后再试!");
                            getMvpView().setRecyclerEndless(true);
                        }
                    }
                });
    }

    public void getLatestInActionMode(int userId, int pageCounts, final boolean oldEndlessState,
                                      final ActionMode mode) {
        final HashSet<String> oldCollection = mDistinctCollection;
        mDistinctCollection = new HashSet<>();
        RxUtil.dispose(mDisposable);
        mDisposable = getDistinct(userId, 1, pageCounts)
                .compose(RxUtil.applySingleIoSchedulerWith(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().setRecyclerEndless(false);
                        }
                    }
                }))
                .subscribe(new Consumer<Pair<List<Word>, Integer>>() {
                    @Override
                    public void accept(Pair<List<Word>, Integer> pair) throws Exception {
                        if (isViewAttached()) {
                            List<Word> words = pair.first;
                            int total = pair.second;
                            if (words.size() > 0) {
                                getMvpView().onLatestDataLoaded(words, total, false);
                                getMvpView().setRecyclerEndless(true);
                            } else {
                                getMvpView().showMessage("暂无数据!");
                            }
                            mode.finish();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        mDistinctCollection = oldCollection;
                        if (isViewAttached()) {
                            getMvpView().setRecyclerEndless(oldEndlessState);
                            getMvpView().showMessage("同步数据失败，请稍后重新同步!");
                            mode.finish();
                        }
                    }
                });
    }

    public void deleteWords(final int userId, List<String> words, final ActionMode mode) {
        RxUtil.dispose(mDeleteDisposable);
        mDeleteDisposable = mDataManager.deleteWords(userId, words)
                .compose(RxUtil.applySingleIoSchedulerWith(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                }))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (isViewAttached()) {
                            if (result) {
                                getMvpView().onDeleteAccomplish(userId, mode);
                            } else {
                                getMvpView().showMessage("删除失败，请稍后重试!");
                                mode.finish();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            getMvpView().showMessage("删除失败，请稍后重试!");
                            mode.finish();
                        }
                    }
                });
    }

    public void deleteWordsNew(final int userId, List<String> words) {
        RxUtil.dispose(mDeleteDisposable);
        mDeleteDisposable = mDataManager.deleteWords(userId, words)
                .compose(RxUtil.applySingleIoSchedulerWith(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                }))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (isViewAttached()) {
                            if (result) {
                                getMvpView().onDeleteAccomplish(userId);
                            } else {
                                getMvpView().showMessage("删除失败，请稍后重试!");
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            getMvpView().showMessage("删除失败，请稍后重试!");
                        }
                    }
                });
    }

    public void insertWords(int userId, List<String> words) {
        RxUtil.dispose(mInsertDisposable);
        mInsertDisposable = mDataManager.insertWords(userId, words)
                .compose(RxUtil.applySingleIoScheduler())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (isViewAttached()) {
                            if (result) {
                                getMvpView().showMessage(R.string.play_ins_new_word_success);
                            } else {
                                getMvpView().showMessage("添加生词未成功");
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            getMvpView().showMessage("添加生词未成功");
                        }
                    }
                });
    }

    private Single<Pair<List<Word>, Integer>> getDistinct(int userId, int pageNumber, int pageCounts) {
        final HashSet<String> set = new HashSet<>(mDistinctCollection);
        return mDataManager.getNoteWords(userId, pageNumber, pageCounts)
                .flatMap(new Function<Pair<List<Word>, Integer>, SingleSource<Pair<List<Word>, Integer>>>() {
                    @Override
                    public SingleSource<Pair<List<Word>, Integer>> apply(final Pair<List<Word>, Integer> pair) throws Exception {
                        return Observable.fromIterable(pair.first)
                                .doOnComplete(new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        mDistinctCollection.addAll(set);
                                    }
                                })
                                .distinct(new Function<Word, String>() {
                                    @Override
                                    public String apply(Word word) throws Exception {
                                        return TextUtils.isEmpty(word.key)?"null":word.key;
                                    }
                                }, new Callable<HashSet<String>>() {
                                    @Override
                                    public HashSet<String> call() throws Exception {
                                        return set;
                                    }
                                })
                                .toList()
                                .map(new Function<List<Word>, Pair<List<Word>, Integer>>() {
                                    @Override
                                    public Pair<List<Word>, Integer> apply(List<Word> words) throws Exception {
                                        return new Pair<>(words, pair.second);
                                    }
                                });
                    }
                });
    }

    private String buildUpdateWords(List<String> words) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.size(); i += 1) {
            if (i == 0) {
                sb.append(words.get(i));
            } else {
                sb.append(",").append(words.get(i));
            }
        }
        return sb.toString();
    }

}
