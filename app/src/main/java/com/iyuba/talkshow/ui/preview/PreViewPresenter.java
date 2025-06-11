package com.iyuba.talkshow.ui.preview;

import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.WavListItem;
import com.iyuba.talkshow.data.model.result.SendDubbingResponse;
import com.iyuba.talkshow.data.remote.CommentService;
import com.iyuba.talkshow.data.remote.IntegralService;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.ui.dubbing.DubbingActivity;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.StorageUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@ConfigPersistent
public class PreViewPresenter extends BasePresenter<PreviewMvpView> {
    private final DataManager mDataManager;

    private Subscription mReleaseDubbingSub;
    private Subscription mSaveRecordSub;

    private int mBackId;
    private int shuoshuoId;

    @Inject
    public PreViewPresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    public String getAacRecordPath(int voaId, long timeStamp) {
        File file = getAacRecordFile(voaId, timeStamp);
        return file.getAbsolutePath();
    }

    public String getMp3RecordPath(int voaId, long timeStamp) {
        File file = getAacRecordFile(voaId, timeStamp);
        return file.getAbsolutePath().replace("aac","mp3");
//        return file.getAbsolutePath();
    }

    public List<VoaSoundNew> getVoaSoundVoaId(int voaid) {
        return mDataManager.getVoaSoundVoaUid(UserInfoManager.getInstance().getUserId(), voaid);
    }

    public String getMp3Path(int voaId) {
        return StorageUtil.getAudioDubbingFile(TalkShowApplication.getContext(), voaId).getAbsolutePath();
    }

    private File getAacRecordFile(int voaId, long timeStamp) {
        return StorageUtil.getAacMergeFile(TalkShowApplication.getContext(), voaId, timeStamp);
    }

    public Uri getVideoUri(int voaId) {
        return Uri.fromFile(StorageUtil.getVideoDubbingFile(TalkShowApplication.getContext(), voaId));
    }

    public void releaseDubbing(Map<Integer, WavListItem>  map, int voaId, String sound, long timeStamp, int score, File file,int cat) {
        checkViewAttached();
        RxUtil.unsubscribe(mReleaseDubbingSub);
        if (!UserInfoManager.getInstance().isLogin()) {
            getMvpView().startLoginActivity();
        } else {
//            File file1 = getAacRecordFile(voaId, timeStamp);
//            File file = new File(file1.getAbsolutePath().replace("aac","mp3"));
            getMvpView().showLoadingDialog();
            mReleaseDubbingSub = mDataManager.sendDubbingComment(
                    map , UserInfoManager.getInstance().getUserId(), UserInfoManager.getInstance().getUserName(), voaId, sound, score,cat)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<SendDubbingResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (e != null) {
                                Log.e("PreViewPresenter", "releaseDubbing onError: " + e.getMessage());
                            }
                            if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                                getMvpView().showPublishFailure(R.string.please_check_network);
                                getMvpView().showToastLong(R.string.please_check_network);
                            } else {
                                getMvpView().showPublishFailure(R.string.request_fail);
                                getMvpView().showToastLong(R.string.request_fail);
//                                        RxUtil.unsubscribe(mReleaseDubbingSub);

                            }
                            DubbingActivity.isSend = false;
                        }

                        @Override
                        public void onNext(SendDubbingResponse response) {
//                            Timber.e("@@@SendDubbingResponse ~~");
                            if ((response != null) && TextUtils.equals(response.message(), CommentService.SendComment.Result.COMMENT_SUCCESS)) {
                                Integer tmp = response.shuoshuoId();
                                mBackId = tmp != null ? tmp : 0;
                                Log.e("PreViewPresenter", "releaseDubbing onNext mBackId : " + mBackId);
                                shuoshuoId = mBackId;
                                getMvpView().showPublishSuccess(R.string.released_success2);
                                getMvpView().showShareHideReleaseButton();
                                getMvpView().showShareView(response.filePath());
                                getMvpView().showToastShort(R.string.released_success);
                                DubbingActivity.isSend = true;
                            } else {
                                getMvpView().showPublishFailure(R.string.released_failure);
                                getMvpView().showToastLong(R.string.released_failure);
                                DubbingActivity.isSend = false;
                            }
                        }
                    });
        }
    }

    public int getBackId() {
        return mBackId;
    }

    public int getShuoshuoId() {
        return shuoshuoId;
    }

    public void saveRecord(Record record) {
        checkViewAttached();
        RxUtil.unsubscribe(mSaveRecordSub);
        mSaveRecordSub = mDataManager.saveRecord(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("PreViewPresenter", "saveRecord onError : " + e.getMessage());
                        }
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
//                        if (aBoolean) {
//                            getMvpView().showToast(R.string.save_success);
//                            getMvpView().startMainActivity();
//                        } else {
//                            getMvpView().showToast(R.string.save_failure);
//                        }
                        getMvpView().showToast(R.string.save_success);
                    }
                });
    }

    public int getFinishNum(int voaId, long timestamp) {
        return StorageUtil.getRecordNum(TalkShowApplication.getContext(), voaId, timestamp);
    }

    public void deleteRecord(int voaId, long timestamp) {
        StorageUtil.cleanRecordDir(TalkShowApplication.getContext(), voaId, timestamp);
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mReleaseDubbingSub);
        RxUtil.unsubscribe(mSaveRecordSub);
    }

    public void cancelUpload() {
//        RxUtil.unsubscribe(mReleaseDubbingSub);
        Timber.e("cancelUpload cancelUpload cancelUpload");
    }

    public CharSequence formatMessage(int wordCount, int averageScore, String time) {
        SpannableStringBuilder builder = new SpannableStringBuilder(
                "您一共配音了" + wordCount + "个单词，\n读了" + time + "s秒的时间，\n");

        SpannableString spannableString = new SpannableString("正确率" + averageScore + "%");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#F85778"));
        spannableString.setSpan(colorSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        builder.append(spannableString);

        if (averageScore >= 80) {
            builder.append("，读的真棒~");
        } else if (averageScore >= 60) {
            builder.append("，\n读的一般般，还需要努力呀～");
        } else {
            builder.append("，\n分数好低，还需努力啊~");
        }
        return builder;
    }

    public CharSequence formatTitle(String string) {
        SpannableString spannableString = new SpannableString(string);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#F85778"));
        spannableString.setSpan(colorSpan, string.length() - 5, string.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public IntegralService getIntegralService() {
        return mDataManager.getIntegralService();
    }
}
