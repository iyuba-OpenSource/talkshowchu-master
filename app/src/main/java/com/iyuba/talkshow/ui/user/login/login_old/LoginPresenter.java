//package com.iyuba.talkshow.ui.user.login.login_old;
//
//import android.app.Activity;
//import android.content.Context;
//import android.location.Location;
//
//import com.iyuba.talkshow.R;
//import com.iyuba.talkshow.data.manager.AccountManager;
//import com.iyuba.talkshow.data.manager.OnLoginListener;
//import com.iyuba.talkshow.data.model.User;
//import com.iyuba.talkshow.injection.ConfigPersistent;
//import com.iyuba.talkshow.ui.base.BasePresenter;
//import com.iyuba.talkshow.util.GetLocation;
//import com.iyuba.talkshow.util.RxUtil;
//
//import java.text.MessageFormat;
//
//import javax.inject.Inject;
//
//import rx.Subscription;
//
//@ConfigPersistent
//public class LoginPresenter extends BasePresenter<LoginMvpView> {
//    private final GetLocation mGetLocation;
//    private final AccountManager mAccountManager;
//
//    private Subscription mLoginSub;
//
//    @Inject
//    public LoginPresenter(AccountManager accountManager, GetLocation getLocation) {
//        this.mAccountManager = accountManager;
//        this.mGetLocation = getLocation;
//    }
//
//    @Override
//    public void detachView() {
//        super.detachView();
//        RxUtil.unsubscribe(mLoginSub);
//    }
//
//    public void login(final String username, final String password) {
//        checkViewAttached();
//        RxUtil.unsubscribe(mLoginSub);
//        final Location location = mGetLocation.getLocation();
//        double latitude = location == null ? 0 : location.getLatitude();
//        double longitude = location == null ? 0 : location.getLongitude();
//        getMvpView().showWaitingDialog();
//        mLoginSub = mAccountManager.login(username, password, latitude, longitude,
//                new OnLoginListener() {
//                    @Override
//                    public void onLoginSuccess(User user) {
//                        getMvpView().dismissWaitingDialog();
//                        getMvpView().showToast(
//                                MessageFormat.format(
//                                        ((Context) getMvpView()).getString(R.string.welcome_back), username));
////                        EventBus.getDefault().post(new LoginEvent());
//                        // improve user info
//                        getMvpView().startImproveUser(user.getUid());
//                        ((Activity) getMvpView()).finish();
//                        mAccountManager.setUser(user);
//                        mAccountManager.saveUser(user);
//                    }
//
//                    @Override
//                    public void onLoginFail(String errorMsg) {
//                        getMvpView().dismissWaitingDialog();
//                        if(errorMsg != null) {
//                            getMvpView().showToast(errorMsg);
//                        }
//                    }
//                });
//    }
//}
