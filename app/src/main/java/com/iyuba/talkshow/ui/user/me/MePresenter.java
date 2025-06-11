package com.iyuba.talkshow.ui.user.me;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.remote.ClearUserResponse;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.RxUtil;

import javax.inject.Inject;

import rx.Subscriber;

/**
 * Created by Administrator on 2016/12/24/024.
 */

@ConfigPersistent
public class MePresenter extends BasePresenter<MeMvpView> {
    private final DataManager mDataManager;
    private final ConfigManager mConfigManager;

    @Inject
    public MePresenter(ConfigManager configManager, DataManager dataManager) {
        this.mConfigManager = configManager;
        this.mDataManager = dataManager;
    }

    public String getUserImageUrl() {
        return UserInfoManager.getInstance().isLogin() ?
                Constant.Url.getMiddleUserImageUrl(
                        UserInfoManager.getInstance().getUserId(),
                        mConfigManager.getPhotoTimestamp()) : null;
    }

    /*註銷用戶*/
    public void clearUser() {
        mDataManager.clearUser(UserInfoManager.getInstance().getLoginAccount(), UserInfoManager.getInstance().getLoginPassword())
                .compose(RxUtil.io2main())
                .subscribe(new Subscriber<ClearUserResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showToastShort("注销失败 网络异常！");
                    }

                    @Override
                    public void onNext(ClearUserResponse response) {
                        UserInfoManager.getInstance().clearUserInfo();
                        getMvpView().showToastShort("用户已经成功注销！");
                    }
                });
    }
}
