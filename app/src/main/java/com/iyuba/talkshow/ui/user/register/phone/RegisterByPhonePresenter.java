package com.iyuba.talkshow.ui.user.register.phone;

import android.text.TextUtils;
import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.result.GetVerifyCodeResponse;
import com.iyuba.talkshow.data.remote.VerifyCodeService;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ConfigPersistent
public class RegisterByPhonePresenter extends BasePresenter<RegisterByPhoneMvpView> {
    private final DataManager mDataManager;

    private Subscription mGetVerifySub;

    @Inject
    public RegisterByPhonePresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetVerifySub);
    }

    public void getVerifyCode(String phone) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetVerifySub);
        mGetVerifySub = mDataManager.getVerifyCode(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetVerifyCodeResponse>() {
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
                        if (e != null) {
                            Log.e("RegisterByPhoneActivity", "getVerifyCode onError " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(GetVerifyCodeResponse response) {
                        getMvpView().dismissWaitingDialog();
                        if (response == null) {
                            Log.e("RegisterByPhoneActivity", "getVerifyCode onNext null.");
                            return;
                        }
                        Log.e("RegisterByPhoneActivity", "getVerifyCode onNext result " + response.result());
                        switch (response.result()) {
                            case VerifyCodeService.GetCode.Result.Code.SUCCESS:
                                getMvpView().updateGetCodeBtn();
                                getMvpView().registerSmsObserver();
                                break;
                            case VerifyCodeService.GetCode.Result.Code.REGISTERED:
                                if (!TextUtils.isEmpty(response.message()) && response.message().contains("times")) {
                                    getMvpView().showToast(VerifyCodeService.GetCode.Result.Message.REGISTERS);
                                } else
                                getMvpView().showToast(VerifyCodeService.GetCode.Result.Message.REGISTERED);
                                break;
                            default:
                                getMvpView().showToast(VerifyCodeService.GetCode.Result.Message.FAILURE);
                                break;
                        }
                    }
                });
    }

}
