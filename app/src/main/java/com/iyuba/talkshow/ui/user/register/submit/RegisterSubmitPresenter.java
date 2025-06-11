package com.iyuba.talkshow.ui.user.register.submit;

import android.app.Activity;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
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
public class RegisterSubmitPresenter extends BasePresenter<RegisterSubmitMvpView> {
    private final DataManager mDataManager;
    private final GetLocation mGetLocation;

    private Subscription mRegisterSub;
    private Subscription mLoginSub;

    @Inject
    public RegisterSubmitPresenter(DataManager dataManager,
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

    public void register(final String username, final String password, String mobile) {
        checkViewAttached();
        RxUtil.unsubscribe(mRegisterSub);
        mRegisterSub = mDataManager.registerByPhone(username, password, mobile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RegisterResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissWaitingDialog();
                        if(!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(RegisterResponse response) {
                        switch (response.result()) {
                            case UserService.Register.Result.Code.SUCCESS:
                                /*Location location = mGetLocation.getLocation();
                                double longitude = location == null ? 0 : location.getLongitude();
                                double latitude = location == null ?  0 : location.getLatitude();
                                checkViewAttached();
                                RxUtil.unsubscribe(mLoginSub);
                                mLoginSub = mAccountManager.login(username, password,
                                        longitude, latitude,
                                        new OnLoginListener() {
                                            @Override
                                            public void onLoginSuccess(User user) {
                                                getMvpView().dismissWaitingDialog();
                                                getMvpView().startUploadImageActivity();
                                                getMvpView().finishRegisterActivity();
                                                mAccountManager.setUser(user);
                                                mAccountManager.saveUser(user);
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
                                break;
                            case UserService.Register.Result.Code.USERNAME_EXIST:
                                getMvpView().dismissWaitingDialog();
                                getMvpView().showToast(UserService.Register.Result.Message.USERNAME_EXIST);
                                break;
                            case UserService.Register.Result.Code.PHONE_REGISTER:
                                getMvpView().dismissWaitingDialog();
                                getMvpView().showToast(UserService.Register.Result.Message.PHONE_REGISTERED);
                                getMvpView().startLoginActivity();
                                getMvpView().finishRegisterActivity();
                                break;
                            default:
                                getMvpView().dismissWaitingDialog();
                                String showMsg = ServiceMsgUtil.showRegisterErrorMsg(String.valueOf(response.result()));
//                                getMvpView().showToast(UserService.Register.Result.Message.REGISTER_FAIL);
                                getMvpView().showToast(showMsg);
                                break;
                        }
                    }
                });
    }

}
