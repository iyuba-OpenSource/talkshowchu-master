package com.iyuba.talkshow.ui.user.image;

import android.content.Context;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.result.UploadImageResponse;
import com.iyuba.talkshow.data.remote.UserService;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.TimeUtil;
import com.iyuba.talkshow.util.request.ProgressListener;

import java.io.File;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/14/014.
 */

@ConfigPersistent
public class UploadImagePresenter extends BasePresenter<UploadImageMvp> {

    private final DataManager mDataManage;
    private final ConfigManager mConfigManager;

    private Subscription mUploadImageSub;

    @Inject
    public UploadImagePresenter(DataManager dataManager,
                                ConfigManager configManager) {
        this.mDataManage = dataManager;
        this.mConfigManager = configManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mUploadImageSub);
    }

    public String getImageUrl() {
        return Constant.Url.getBigUserImageUrl(
                UserInfoManager.getInstance().getUserId(),
                mConfigManager.getPhotoTimestamp()
        );
    }

    public void uploadImage(File file) {
        checkViewAttached();
        RxUtil.unsubscribe(mUploadImageSub);
        mUploadImageSub = mDataManage.uploadImage(UserInfoManager.getInstance().getUserId(), file, new ProgressListener() {
            @Override
            public void onProgress(long hasWrittenLen, long totalLen, boolean hasFinish) {

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UploadImageResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getMvpView().dismissWaitingDialog();

                        if (!NetStateUtil.isConnected((Context) getMvpView())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(UploadImageResponse response) {
                        getMvpView().dismissWaitingDialog();

                        if (response.status() == UserService.UploadImage.Result.Code.SUCCESS) {
                            getMvpView().setSubmitBtnClickable(true);
                            getMvpView().showToast(R.string.upload_image_success);
                            resetPhotoTimestamp();
                            getMvpView().startVipCenterActivity(true);
                            getMvpView().finishCurActivity();
                        } else {
                            getMvpView().showAlertDialog("", R.string.upload_image_modify_fail);
                        }
                    }
                });

    }

    private void resetPhotoTimestamp() {
        mConfigManager.setPhotoTimestamp(String.valueOf(TimeUtil.getTimeStamp()));
    }
}
