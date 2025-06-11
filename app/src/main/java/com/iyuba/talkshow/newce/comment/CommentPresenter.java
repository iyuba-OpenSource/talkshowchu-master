package com.iyuba.talkshow.newce.comment;

import android.text.TextUtils;
import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.RankWork;
import com.iyuba.talkshow.data.model.RankWorkResponse;
import com.iyuba.talkshow.data.model.Thumb;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.model.result.ThumbsResponse;
import com.iyuba.talkshow.data.remote.ThumbsService;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.ui.detail.ranking.watch.ThumbAction;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carl shen on 2020/8/7
 * New Junior English, new study experience.
 */
@ConfigPersistent
public class CommentPresenter extends BasePresenter<CommentMvpView> {

    private final DataManager mDataManager;
    private Subscription mDoAgreeSub;
    private Subscription mGetRankingSub;
    private final HashMap<Integer, Thumb> thumbList = new HashMap<>();

    @Inject
    public CommentPresenter(DataManager dlManager) {
        mDataManager = dlManager;
    }

    public Thumb getCommentById(int commentId) {
        List<Thumb> mThumb = mDataManager.getCommentById(commentId);
        if (mThumb != null && mThumb.size() > 0) {
            Thumb thumb = mThumb.get(0);
            thumbList.put(thumb.commentId(), thumb);
            return thumb;
        }
        return null;
    }

    public Thumb getCommentThumb(int uid, int commentId) {
        List<Thumb> mThumb = mDataManager.getCommentThumb(uid, commentId);
        if (mThumb != null && mThumb.size() > 0) {
            Thumb thumb = mThumb.get(0);
            thumbList.put(thumb.commentId(), thumb);
            return thumb;
        }
        return null;
    }

    public List<VoaText> getVoaTextByParaId(int voaid, int index) {
        return mDataManager.getVoaTextByParaId(voaid, index);
    }

    public void doThumb(int id) {
        Thumb mThumb = getCommentThumb(UserInfoManager.getInstance().getUserId(), id);
        if (mThumb == null) {
            doAgreeThumb(id);
        } else {
            getMvpView().showToastShort("您已经评论过该条了");
        }
    }

    public void doAgreeThumb(final int id) {
        checkViewAttached();
        RxUtil.unsubscribe(mDoAgreeSub);
        mDoAgreeSub = mDataManager.doAgree(UserInfoManager.getInstance().getUserId(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ThumbsResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
//                        } else {
//                            getMvpView().showToast(R.string.request_fail);
                        }
                        if (e != null) {
                            Log.e("CommentActivity", "onError  " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(ThumbsResponse response) {
                        if (response == null) {
                            Log.e("CommentActivity", "doAgreeThumb response null? ");
//                            getMvpView().showToast(R.string.thumbs_failure);
                            return;
                        }
                        Log.e("CommentActivity", "doAgreeThumb response.message() " + response.message());
                        if (TextUtils.equals(response.message(), ThumbsService.DoThumbs.Result.THUMBS_SUCCESS)) {
                            Thumb mThumb = Thumb.builder().setUid(UserInfoManager.getInstance().getUserId()).setCommentId(id).build();
                            mThumb.setAction(ThumbAction.THUMB);
                            insertDbThumb(mThumb);
//                            getMvpView().refreshLayout();
                        }
                    }
                });
    }

    public void doDeleteThumb(final int id) {
        checkViewAttached();
        RxUtil.unsubscribe(mDoAgreeSub);
        mDoAgreeSub = mDataManager.deleteThumbs(UserInfoManager.getInstance().getUserId(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ThumbsResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.delete_failure);
                        }
                        if (e != null) {
                            Log.e("CommentActivity", "doDeleteThumb onError  " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(ThumbsResponse response) {
                        if (response == null) {
                            Log.e("CommentActivity", "doDeleteThumb response null? ");
                            getMvpView().showToast(R.string.delete_failure);
                            return;
                        }
                        Log.e("CommentActivity", "doDeleteThumb response.message() " + response.message());
                        if (TextUtils.equals(response.message(), ThumbsService.DoThumbs.Result.THUMBS_SUCCESS)) {
                            getMvpView().refreshLayout();
                        }
                    }
                });
    }

    private void insertDbThumb(Thumb thumb) {
        mDataManager.insertThumb(thumb)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("CommentActivity", "insertDbThumb fail! ");
//                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.e("CommentActivity", "insertDbThumb onNext = " + aBoolean);
                        if (aBoolean) {
//                            getMvpView().showToast(R.string.thumbs_success);
                        } else {
//                            getMvpView().showToast(R.string.thumbs_failure);
                        }
                    }
                });
    }

    public void getWorkRanking(int uid, int voaid) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetRankingSub);
        getMvpView().showLoadingLayout();
        mGetRankingSub = mDataManager.getWorkRanking(uid, voaid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RankWorkResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView() != null) {
                            getMvpView().dismissLoadingLayout();
                        }
                        if (e != null) {
                            Log.e("CommentActivity", "getWorkRanking onError  " + e.getMessage());
                        }
                        if(!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(RankWorkResponse response) {
                        if (getMvpView() != null) {
                            getMvpView().dismissLoadingLayout();
                        }
                        if ((response == null) || (response.data == null)) {
                            Log.e("CommentActivity", "Why getWorkRanking null? ");
                            if (getMvpView() != null) {
                                getMvpView().showEmptyRankings();
                            }
                            return;
                        }
                        Log.e("CommentActivity", "getWorkRanking response count " + response.count);
                        List<RankWork> mData = new ArrayList<>();
                        for (RankWork work: response.data) {
                            List<Voa> getVoa = mDataManager.getVoaByVoaId(work.TopicId);
                            if ((getVoa != null) && getVoa.size() > 0) {
                                mData.add(work);
                            }
                        }
                        if (getMvpView() != null) {
                            getMvpView().showRankings(mData);
                        }
                    }
                });
    }

}
