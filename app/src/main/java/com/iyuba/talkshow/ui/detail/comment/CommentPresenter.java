package com.iyuba.talkshow.ui.detail.comment;

import android.util.Pair;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Comment;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.result.GetCommentResponse;
import com.iyuba.talkshow.data.model.result.SendCommentResponse;
import com.iyuba.talkshow.data.model.result.ThumbsResponse;
import com.iyuba.talkshow.data.remote.CommentService;
import com.iyuba.talkshow.data.remote.IntegralService;
import com.iyuba.talkshow.data.remote.ThumbsService;
import com.iyuba.talkshow.injection.PerFragment;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@PerFragment
public class CommentPresenter extends BasePresenter<CommentMvpView> {
    private final DataManager mDataManager;

    private Subscription mLoadSub;
    private Subscription mSendWordSub;
    private Subscription mSendVoiceSub;
    private Subscription mReplyWordSub;
    private Subscription mDoAgreeSub;
    private Subscription mDoAgainstSub;
    private Subscription mGetVoaSub;

    private final Subscriber mThumbSubscriber = new Subscriber<ThumbsResponse>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            if (!NetStateUtil.isConnected(((BaseFragment) getMvpView()).getContext())) {
                getMvpView().showToast(R.string.please_check_network);
            } else {
                getMvpView().showToast(R.string.request_fail);
            }
        }

        @Override
        public void onNext(ThumbsResponse result) {
            if (ThumbsService.DoThumbs.Result.THUMBS_SUCCESS.equals(result.resultCode())) {
                getMvpView().showToast(R.string.vote_success);
            } else {
                getMvpView().showToast(R.string.vote_failure);
            }
        }
    };

    @Inject
    public CommentPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    public void loadComment(int voaId, int rankingId, int pageNum, int pageSize) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoadSub);
        mLoadSub = mDataManager.getComments(voaId, rankingId, CommentService.SendComment.Param.Value.SORT_BY_DATE, pageNum, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Pair<Integer, List<Comment>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissCommentLoadingLayout();
                        getMvpView().dismissLoadingDialog();
                        getMvpView().dismissRefreshingView();

                        if (!NetStateUtil.isConnected(((BaseFragment) getMvpView()).getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(Pair<Integer, List<Comment>> pair) {
                        getMvpView().dismissCommentLoadingLayout();
                        getMvpView().dismissLoadingDialog();
                        getMvpView().dismissRefreshingView();
                        List<Comment> commentList = pair.second;
                        if (commentList == null) {
                            getMvpView().setCommentNum(0);
                            getMvpView().showEmptyComment();
                        } else {
                            getMvpView().setCommentNum(pair.first);
                            getMvpView().showComments(commentList);
                        }
                    }
                });
    }

    public void senTextComment(final int voaId, final int rankingId, String content) {
        checkViewAttached();
        RxUtil.unsubscribe(mSendWordSub);
        if (UserInfoManager.getInstance().isLogin()) {
            mSendWordSub = mDataManager.sendTextComment(UserInfoManager.getInstance().getUserId(), UserInfoManager.getInstance().getUserName(),
                    voaId, rankingId, content)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<GetCommentResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            getMvpView().dismissLoadingDialog();
                            if (!NetStateUtil.isConnected(((BaseFragment) getMvpView()).getContext())) {
                                getMvpView().showToast(R.string.please_check_network);
                            } else {
                                e.printStackTrace();
                                getMvpView().showToast(R.string.request_fail);
                            }
                        }

                        @Override
                        public void onNext(GetCommentResponse response) {
                            getMvpView().dismissLoadingDialog();
                            if (CommentService.SendComment.Result.COMMENT_SUCCESS.equals(response.message())) {
                                loadComment(voaId,rankingId,1,100);
                                getMvpView().clearInputText();
                                getMvpView().showToast(R.string.comment_success);
                            } else {
                                getMvpView().showToast(R.string.comment_failure);
                            }
                        }
                    });
        }
    }

    public void sendVoiceComment(final int voaId, final int rankingId, File file) {
        checkViewAttached();
        RxUtil.unsubscribe(mSendVoiceSub);
        if (UserInfoManager.getInstance().isLogin()) {
            mSendVoiceSub = mDataManager.sendVoiceComment(UserInfoManager.getInstance().getUserId(), UserInfoManager.getInstance().getUserName(), voaId, rankingId, file)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<SendCommentResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            getMvpView().dismissLoadingDialog();
                            if (!NetStateUtil.isConnected(((BaseFragment) getMvpView()).getContext())) {
                                getMvpView().showToast(R.string.please_check_network);
                            } else {
                                getMvpView().showToast(R.string.request_fail);
                            }
                        }

                        @Override
                        public void onNext(SendCommentResponse response) {
                            if (CommentService.SendComment.Result.COMMENT_SUCCESS.equals(response.message())) {
                                loadComment(voaId, rankingId, CommentFragment.PAGE_NUM, CommentFragment.PAGE_SIZE);
                                getMvpView().showToast(R.string.comment_success);
                            } else {
                                getMvpView().dismissLoadingDialog();
                                getMvpView().showToast(R.string.comment_failure);
                            }
                        }
                    });
        }
    }

    public void doAgree(int id) {
        checkViewAttached();
        RxUtil.unsubscribe(mDoAgreeSub);
        mDoAgreeSub = mDataManager.doAgree(UserInfoManager.getInstance().getUserId(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mThumbSubscriber);
    }

    public void doAgainst(int id) {
        checkViewAttached();
        RxUtil.unsubscribe(mDoAgainstSub);
        mDoAgainstSub = mDataManager.doAgainst(UserInfoManager.getInstance().getUserId(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mThumbSubscriber);
    }

    public void getVoa(int voaId) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetVoaSub);
        mGetVoaSub = mDataManager.getVoaById(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Voa>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (getMvpView() != null) {
                            getMvpView().showToast(R.string.database_error);
                        }
                    }

                    @Override
                    public void onNext(Voa voa) {
                        if (voa != null && getMvpView() != null) {
                            getMvpView().startDubbingActivity(voa);
                        }
                    }
                });
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetVoaSub);
        RxUtil.unsubscribe(mSendWordSub);
        RxUtil.unsubscribe(mSendVoiceSub);
        RxUtil.unsubscribe(mDoAgreeSub);
        RxUtil.unsubscribe(mDoAgainstSub);
        RxUtil.unsubscribe(mLoadSub);
    }

    public IntegralService getIntegralService() {
        return mDataManager.getIntegralService();
    }
}
