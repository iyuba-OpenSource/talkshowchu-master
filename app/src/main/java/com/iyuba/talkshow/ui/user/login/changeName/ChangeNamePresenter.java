package com.iyuba.talkshow.ui.user.login.changeName;

import android.app.Activity;
import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.result.ChangeNameResponse;
import com.iyuba.talkshow.data.remote.UserService;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ConfigPersistent
public class ChangeNamePresenter extends BasePresenter<ChangeNameMvpView> {
    private final DataManager mDataManager;
    private Subscription mLoginSub;
    private Subscription mRegisterSub;

    @Inject
    public ChangeNamePresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mLoginSub);
        RxUtil.unsubscribe(mRegisterSub);
    }

    public void login(final String username, final String password) {
        /*checkViewAttached();
        RxUtil.unsubscribe(mLoginSub);
        mLoginSub = mAccountManager.login(username, password, 0, 0,
                new OnLoginListener() {
                    @Override
                    public void onLoginSuccess(User user) {
                        getMvpView().dismissWaitingDialog();
                        getMvpView().loginResult(true);
                    }

                    @Override
                    public void onLoginFail(String errorMsg) {
                        getMvpView().dismissWaitingDialog();
                        getMvpView().loginResult(false);
                    }
                });*/

        UserInfoManager.getInstance().postRemoteAccountLogin(username, password, new UserinfoCallbackListener() {
            @Override
            public void onSuccess() {
                ((Activity)getMvpView()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getMvpView().dismissWaitingDialog();
                        getMvpView().loginResult(true);
                    }
                });
            }

            @Override
            public void onFail(String errorMsg) {
                ((Activity)getMvpView()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getMvpView().dismissWaitingDialog();
                        getMvpView().loginResult(false);
                    }
                });
            }
        });
    }
    public void ChangeUserName(String uid, String username, String oldUsername) {
        checkViewAttached();
        RxUtil.unsubscribe(mRegisterSub);
        mRegisterSub = mDataManager.ChangeUserName(uid, username, oldUsername)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ChangeNameResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().dismissWaitingDialog();
                        if(e != null) {
                            Log.e("ChangeNameActivity", "confirmPassword onError " + e.getMessage());
                        }
                        if(!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(ChangeNameResponse response) {
                        getMvpView().dismissWaitingDialog();
                        if (response == null) {
                            getMvpView().showToast(R.string.request_fail);
                            return;
                        }
                        Log.e("ChangeNameActivity", "confirmPassword onNext " + response);
                        switch (response.result) {
                            case "121":
                                getMvpView().showToast(R.string.edit_success);
                                /*User user = mAccountManager.getUser();
                                user.setUsername(username);
                                mAccountManager.setUser(user);
                                mAccountManager.saveUser(user);*/
                                //使用新的20001接口刷新数据
                                UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), new UserinfoCallbackListener() {
                                    @Override
                                    public void onSuccess() {
                                        ((Activity)getMvpView()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                EventBus.getDefault().post(new LoginEvent());
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(String errorMsg) {

                                    }
                                });
                                getMvpView().finishChangeActivity();
                                break;
                            case "0":
                            case "000":
                                getMvpView().showToast(UserService.Register.Result.Message.USERNAME_EXIST);
                                break;
                            default:
                                getMvpView().showToast(UserService.Login.Result.Message.LOGIN_SERVER);
                                break;
                        }
                    }
                });
    }

}
