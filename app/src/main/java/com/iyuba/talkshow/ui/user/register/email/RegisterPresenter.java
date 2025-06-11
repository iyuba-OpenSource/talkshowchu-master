package com.iyuba.talkshow.ui.user.register.email;

import android.app.Activity;
import android.content.Context;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.result.RegisterResponse;
import com.iyuba.talkshow.data.remote.UserService;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.GetLocation;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.ServiceMsgUtil;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ConfigPersistent
public class RegisterPresenter extends BasePresenter<RegisterMvpView> {

    private final DataManager mDataManager;
    private final GetLocation mGetLocation;

    private boolean isSendRegisterRequest = false;
    private Subscription mRegisterSub;
    private Subscription mLoginSub;

    @Inject
    public RegisterPresenter(DataManager dataManager,
                             GetLocation getLocation) {
        this.mDataManager = dataManager;
        this.mGetLocation = getLocation;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mRegisterSub);
        RxUtil.unsubscribe(mLoginSub);
    }

    public void register(final String username, final String password, final String email) {
        checkViewAttached();
        RxUtil.unsubscribe(mRegisterSub);
        if (!isSendRegisterRequest) {
            isSendRegisterRequest = true;
            getMvpView().showWaitingDialog();
            mRegisterSub = mDataManager.registerByEmail(username, password, email)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<RegisterResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            getMvpView().dismissWaitingDialog();
                            if(!NetStateUtil.isConnected((Context) getMvpView())) {
                                getMvpView().showToast(R.string.please_check_network);
                            } else {
                                getMvpView().showToast(R.string.request_fail);
                            }
                        }

                        @Override
                        public void onNext(RegisterResponse registerResponse) {

                            String errMsg = null;
                            switch (registerResponse.result()) {
                                case UserService.Register.Result.Code.SUCCESS:
                                    registerSuccess(username, password);
                                    break;
                                case UserService.Register.Result.Code.USERNAME_EXIST:
                                    isSendRegisterRequest = false;
                                    errMsg = UserService.Register.Result.Message.USERNAME_EXIST;
                                    break;
                                case UserService.Register.Result.Code.EMAIL_EXIST:
                                    isSendRegisterRequest = false;
                                    errMsg = UserService.Register.Result.Message.EMAIL_REGISTERED;
                                    break;
                                case UserService.Register.Result.Code.PHONE_REGISTER:
                                    isSendRegisterRequest = false;
                                    errMsg = UserService.Register.Result.Message.PHONE_REGISTERED;
                                    break;
                                default:
                                    isSendRegisterRequest = false;
//                                    errMsg = UserService.Register.Result.Message.REGISTER_FAIL;
                                    errMsg = ServiceMsgUtil.showRegisterErrorMsg(String.valueOf(registerResponse.result()));
                                    break;
                            }
                            getMvpView().dismissWaitingDialog();
                            if(errMsg != null) {
                                getMvpView().showToast(errMsg);
                            }
                        }
                    });
        } else {
            getMvpView().showToast(((Context) getMvpView()).getString(R.string.register_operating));
        }

    }

    private void registerSuccess(final String username, final String password) {
        /*Location location = mGetLocation.getLocation();
        checkViewAttached();
        RxUtil.unsubscribe(mLoginSub);
        double latitude = location != null? location.getLatitude() : 0;
        double longitude = location != null?location.getLongitude() : 0;
        mLoginSub = mAccountManager.login(username, password,
                latitude, longitude, new OnLoginListener() {
                    @Override
                    public void onLoginSuccess(User user) {
                        getMvpView().dismissWaitingDialog();
                        getMvpView().startUploadImageActivity();
                        getMvpView().finishRegisterActivity();
                    }

                    @Override
                    public void onLoginFail(String errorMsg) {
                        getMvpView().dismissWaitingDialog();
                    }
                });*/

        UserInfoManager.getInstance().postRemoteAccountLogin(username, password, new UserinfoCallbackListener() {
            @Override
            public void onSuccess() {
                ((Activity)getMvpView()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getMvpView().dismissWaitingDialog();
                        getMvpView().startUploadImageActivity();
                        getMvpView().finishRegisterActivity();
                    }
                });
            }

            @Override
            public void onFail(String errorMsg) {
                ((Activity)getMvpView()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getMvpView().dismissWaitingDialog();
                    }
                });
            }
        });
    }

}
