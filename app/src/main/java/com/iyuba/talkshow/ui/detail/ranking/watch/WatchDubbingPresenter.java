package com.iyuba.talkshow.ui.detail.ranking.watch;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Thumb;
import com.iyuba.talkshow.data.model.result.ThumbsResponse;
import com.iyuba.talkshow.data.remote.ThumbsService;
import com.iyuba.talkshow.data.remote.UploadStudyRecordService;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WatchDubbingPresenter extends BasePresenter<WatchDubbingMvpView> {

    private final DataManager mDataManager;

    private Subscription mGetThumbSub;
    private Subscription mDoAgreeSub;
    private Subscription mDoAgainstSub;

    private Thumb mThumb;

    @Inject
    public WatchDubbingPresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetThumbSub);
        RxUtil.unsubscribe(mDoAgreeSub);
        RxUtil.unsubscribe(mDoAgainstSub);
    }

    public void checkThumb(int commentId) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetThumbSub);

        int uid = UserInfoManager.getInstance().getUserId();
        mGetThumbSub = mDataManager.getThumb(uid, commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Thumb>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Thumb thumb) {
                        if (thumb != null) {
                            mThumb = thumb;
                            getMvpView().updateThumbIv(ThumbAction.THUMB);
                        } else {
                            getMvpView().updateThumbIv(ThumbAction.UN_THUMB);
                        }
                    }
                });
    }

    public void doThumb(int id) {
        List<Thumb> myThumb = mDataManager.getCommentThumb(UserInfoManager.getInstance().getUserId(), id);
        if (myThumb == null || myThumb.size() < 1) {
            doAgreeThumb(id);
        } else {
            if (getMvpView() != null) {
                getMvpView().showToastShort("您已经点过赞了。");
            }
        }
    }

    private void doAgreeThumb(final int id) {
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
                        if (!NetStateUtil.isConnected((Context) getMvpView())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                        if (e != null) {
                            Log.e("WatchDubbingActivity", "doAgreeThumb onError  " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(ThumbsResponse response) {
                        if (TextUtils.equals(response.message(), ThumbsService.DoThumbs.Result.THUMBS_SUCCESS)) {
                            getMvpView().updateThumbIv(ThumbAction.THUMB);
                            getMvpView().updateThumbNumTv();
                            if (mThumb == null) {
                                initThumb(id);
                                mThumb.setAction(ThumbAction.THUMB);
                                insertDbThumb(mThumb);
                            }
                        }
                    }
                });
    }

    private void doAgainstThumb(int id) {
        checkViewAttached();
        RxUtil.unsubscribe(mDoAgainstSub);
        mDoAgainstSub = mDataManager.doAgainst(UserInfoManager.getInstance().getUserId(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ThumbsResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetStateUtil.isConnected((Context) getMvpView())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(ThumbsResponse response) {
                        if (TextUtils.equals(response.message(), ThumbsService.DoThumbs.Result.THUMBS_SUCCESS)) {
                            getMvpView().updateThumbIv(ThumbAction.UN_THUMB);
                            if (mThumb != null) {
                                mThumb.setAction(ThumbAction.UN_THUMB);
                                deleteDbThumb(mThumb);
                            }
                        }
                    }
                });
    }

    private void deleteDbThumb(Thumb thumb) {
        mDataManager.updateThumb(thumb)
                .subscribeOn(Schedulers.io());
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
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            getMvpView().showToast(R.string.thumbs_success);
                        } else {
                            getMvpView().showToast(R.string.thumbs_failure);
                        }
                    }
                });
    }

    private void initThumb(int id) {
        if (mThumb == null) {
            mThumb = Thumb.builder()
                    .setUid(UserInfoManager.getInstance().getUserId())
                    .setCommentId(id)
                    .build();
        }
    }

    public UploadStudyRecordService getUploadService() {
        return mDataManager.getUploadStudyRecordService();
    }
}
