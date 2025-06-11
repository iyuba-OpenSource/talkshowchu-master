package com.iyuba.talkshow.ui.user.detail;

import android.content.Context;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.result.GetUserBasicInfoResponse;
import com.iyuba.talkshow.data.remote.UserService;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/22/022.
 */

@ConfigPersistent
public class ShowUserInfoPresenter extends BasePresenter<ShowUserInfoMvpView>{

    private final DataManager mDataManager;

    private Subscription mGetUserSub;

    @Inject
    public ShowUserInfoPresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetUserSub);
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
                        getMvpView().dismissWaitingDialog();
                        if(!NetStateUtil.isConnected((Context) getMvpView())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(GetUserBasicInfoResponse response) {
                        getMvpView().dismissWaitingDialog();
                        if (response.result() == UserService.GetUserBasicInfo.Result.Code.SUCCESS) {
                            getMvpView().setUserInfo(response);
                        } else {
                            getMvpView().showToast(R.string.get_user_info_error);
                            getMvpView().finishActivity();
                        }
                    }
                });
    }
}
