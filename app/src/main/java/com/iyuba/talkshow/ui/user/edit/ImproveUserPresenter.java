package com.iyuba.talkshow.ui.user.edit;

import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.result.GetUserBasicInfoResponse;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newdata.SPconfig;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carl shen on 2020/12/2.
 */
@ConfigPersistent
public class ImproveUserPresenter extends BasePresenter<ImproveUserMvpView>{

    private final DataManager mDataManager;
    private final ConfigManager mConfigManager;
    private Subscription mEditSub;
    private Subscription mGetIpSub;
    private Subscription mGetUserSub;

    @Inject
    public ImproveUserPresenter(ConfigManager configManager, DataManager dataManager) {
        mConfigManager = configManager ;
        this.mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mEditSub);
        RxUtil.unsubscribe(mGetIpSub);
        RxUtil.unsubscribe(mGetUserSub);
    }

    public void getIpInfo() {
        checkViewAttached();
        RxUtil.unsubscribe(mGetIpSub);
        mGetIpSub = mDataManager.getIpRequest(String.valueOf(UserInfoManager.getInstance().getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetIpResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("ImproveUserPresenter", "getIpInfo onError  " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(GetIpResponse response) {
                        if (response == null) {
                            Log.e("ImproveUserPresenter", "getIpInfo onNext null.");
                            return;
                        }
                        Log.e("ImproveUserPresenter", "getIpInfo onNext response.result = " + response.result);
                        getMvpView().setIPInfo(response);
                    }
                });
    }

    public void getUserBasicInfo() {
        checkViewAttached();
        RxUtil.unsubscribe(mGetUserSub);
        mGetUserSub = mDataManager.getUserBasicInfo(UserInfoManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetUserBasicInfoResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("ImproveUserPresenter", "getUserBasicInfo onError  " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(GetUserBasicInfoResponse response) {
                        if (response == null) {
                            Log.e("ImproveUserPresenter", "getUserBasicInfo onNext null.");
                            return;
                        }
                        Log.e("ImproveUserPresenter", "getUserBasicInfo onNext result = " + response.result());
                        getMvpView().setUserInfo(response);
                    }
                });
    }

    public void improveUserInfo(String gender, String age, String province, String city, String title) {
        checkViewAttached();
        getMvpView().showLoadingDialog();
        RxUtil.unsubscribe(mEditSub);
        mEditSub = mDataManager.improveUserInfo(UserInfoManager.getInstance().getUserId(), gender, age, province, city, title)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ImproveUserResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("ImproveUserPresenter", "editUserInfo onError  " + e.getMessage());
                        }
                        getMvpView().dismissWaitingDialog();
                        if(!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToastShort(R.string.please_check_network);
                        } else {
                            getMvpView().showToastShort(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(ImproveUserResponse response) {
                        getMvpView().dismissWaitingDialog();
                        if (response == null) {
                            Log.e("ImproveUserPresenter", "editUserInfo onNext null.");
                            return;
                        }
                        Log.e("ImproveUserPresenter", "getUserBasicInfo onNext result = " + response.result);
                        Log.e("ImproveUserPresenter", "getUserBasicInfo onNext response = " + response);
                        // no need save now
                        if (200 == response.result) {
                            getMvpView().showToastShort(R.string.edit_success);
                            int userId = UserInfoManager.getInstance().getUserId();
                            SPconfig.Instance().putBoolean("userId_" + userId, false);
                            Log.e("ImproveUserPresenter", "userId== " + SPconfig.Instance().loadBoolean("userId_" + userId, false));
                            getMvpView().finishImprove();
                        } else {
                            getMvpView().showToastShort(R.string.edit_failure);
                        }
                    }
                });
    }
}
