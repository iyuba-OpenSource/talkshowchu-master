package com.iyuba.talkshow.lil.login;//package com.iyuba.talkshow.lil.login;
//
//import android.app.Activity;
//import android.content.Context;
//import android.text.TextUtils;
//
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.iyuba.talkshow.R;
//import com.iyuba.talkshow.constant.App;
//import com.iyuba.talkshow.constant.ConfigData;
//import com.iyuba.talkshow.data.DataManager;
//import com.iyuba.talkshow.data.manager.AccountManager;
//import com.iyuba.talkshow.data.manager.OnLoginListener;
//import com.iyuba.talkshow.data.model.User;
//import com.iyuba.talkshow.data.model.UserData;
//import com.iyuba.talkshow.data.model.result.RegisterMobResponse;
//import com.iyuba.talkshow.data.remote.UserService;
//import com.iyuba.talkshow.event.LoginEvent;
//import com.iyuba.talkshow.ui.base.BasePresenter;
//import com.iyuba.talkshow.ui.user.login.TokenBean;
//import com.iyuba.talkshow.ui.user.login.UidBean;
//import com.iyuba.talkshow.util.GetLocation;
//import com.iyuba.talkshow.util.RxUtil;
//import com.iyuba.talkshow.util.TimeUtil;
//import com.mob.secverify.datatype.VerifyResult;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.text.MessageFormat;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.inject.Inject;
//
//import rx.Subscriber;
//import rx.Subscription;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
///**
// * @desction:
// * @date: 2023/3/14 10:50
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// */
//public class FixLoginPresenter extends BasePresenter<FixLoginMvpView> {
//
//    private final GetLocation mGetLocation;
//    private final AccountManager mAccountManager;
//    private final DataManager mDataManager;
//
//    private Subscription mLoginSub;
//    private Subscription mTokenSub;
//    private Subscription mUidSub;
//    private Subscription mUserInfoSub;
//
//    @Inject
//    public FixLoginPresenter(DataManager dataManager, AccountManager accountManager, GetLocation getLocation) {
//        this.mDataManager = dataManager;
//        this.mAccountManager = accountManager;
//        this.mGetLocation = getLocation;
//    }
//
//    @Override
//    public void detachView() {
//        super.detachView();
//    }
//
//    //账号登陆
//    public void login(final String username, final String password) {
//        checkViewAttached();
//        RxUtil.unsubscribe(mLoginSub);
//        double latitude = 0;
//        double longitude = 0;
//        mLoginSub = mAccountManager.login(username, password, latitude, longitude,
//                new OnLoginListener() {
//                    @Override
//                    public void onLoginSuccess(User user) {
//                        getMvpView().showToastShort(
//                                MessageFormat.format(
//                                        ((Context) getMvpView()).getString(R.string.welcome_back), username));
////                        EventBus.getDefault().post(new LoginEvent());
//                        // improve user info
//                        mAccountManager.setUser(user);
//                        mAccountManager.saveUser(user);
//
//                        getMvpView().startToImprover(user.getUid());
//                        ((Activity) getMvpView()).finish();
//                    }
//
//                    @Override
//                    public void onLoginFail(String errorMsg) {
//                        if(errorMsg != null) {
//                            getMvpView().showToastShort(errorMsg);
//                        }
//                    }
//                });
//    }
//
//    //秒验信息查询
//    public void verifyCheck(VerifyResult result){
//        Map<String,String> map = new HashMap<>();
//        map.put(UserService.Register.Param.Key.PROTOCOL, "10010");
//        try {
//            String token = URLEncoder.encode(result.getToken(),"UTF-8");
//            map.put("token", token);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            map.put("token", result.getToken());
//        }
//        map.put("opToken", result.getOpToken());
//        map.put("operator", result.getOperator());
//        map.put(UserService.Login.Param.Key.APP_ID, String.valueOf(App.APP_ID));
//        map.put("appkey", ConfigData.mob_key);
//
//        UserService.Creator.newUserService().registerByMob(map)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<RegisterMobResponse>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (getMvpView()!=null){
//                            getMvpView().showMobVerifyCheckMsg(false,false,null);
//                        }
//                    }
//
//                    @Override
//                    public void onNext(RegisterMobResponse response) {
//                        if (response!=null){
//                            //存在账号数据
//                            if (response.isLogin==1 && response.userinfo!=null){
//                                User user = new User();
//                                user.setUid(response.userinfo.uid());
//                                user.setUsername(response.userinfo.username());
//                                user.setNickname(response.userinfo.nickname());
//                                user.setImgSrc(response.userinfo.imageSrc());
//                                user.setMoney(response.userinfo.money());
//                                user.setAmount(response.userinfo.amount());
//                                user.setPhone(response.userinfo.mobile());
//                                user.setEmail(response.userinfo.email());
//                                user.setVipStatus(response.userinfo.vipStatus());
//                                user.setExpireTime(TimeUtil.tansDateFrom1970(response.userinfo.expireTime() * 1000));
//
//                                mAccountManager.setUser(user);
//                                mAccountManager.saveUser(user);
//                                EventBus.getDefault().post(new LoginEvent());
//
//                                getMvpView().showMobVerifyCheckMsg(true,false,response);
//                                return;
//                            }
//
//                            //存在手机号数据
//                            if (response.res!=null && !TextUtils.isEmpty(response.res.phone)){
//                                getMvpView().showMobVerifyCheckMsg(false,true,response);
//                                return;
//                            }
//
//                            getMvpView().showMobVerifyCheckMsg(false,false,null);
//                        }else {
//                            getMvpView().showMobVerifyCheckMsg(false,false,null);
//                        }
//                    }
//                });
//    }
//
//    //获取小程序的token
//    public void getSmallToken(){
//        checkViewAttached();
//        RxUtil.unsubscribe(mTokenSub);
//        mTokenSub = mDataManager.getToken()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<TokenBean>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (getMvpView()!=null){
//                            getMvpView().showWxToken(null);
//                        }
//                    }
//
//                    @Override
//                    public void onNext(TokenBean tokenBean) {
//                        if (getMvpView()!=null){
//                            if (tokenBean.getResult() == 200){
//                                getMvpView().showWxToken(tokenBean.getToken());
//                            }else {
//                                getMvpView().showWxToken(null);
//                            }
//                        }
//                    }
//                });
//    }
//
//    //获取小程序的用户id
//    public void getSmallUid(String token){
//        checkViewAttached();
//        RxUtil.unsubscribe(mUidSub);
//        mUidSub = mDataManager.getUid(token)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<UidBean>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (getMvpView()!=null){
//                            getMvpView().showWxUserId(0);
//                        }
//                    }
//
//                    @Override
//                    public void onNext(UidBean uidBean) {
//                        if (getMvpView()!=null){
//                            if (uidBean.getResult() == 200){
//                                getMvpView().showWxUserId(uidBean.getUid());
//                            }else {
//                                getMvpView().showWxUserId(0);
//                            }
//                        }
//                    }
//                });
//    }
//
//    //获取用户的信息
//    public void getUserInfo(int uid){
//        checkViewAttached();
//        RxUtil.unsubscribe(mUserInfoSub);
//        mUserInfoSub = mDataManager.getUserInfo(uid,uid)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<UserData>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (getMvpView()!=null){
//                            getMvpView().showUserInfo(uid,false);
//                        }
//                    }
//
//                    @Override
//                    public void onNext(UserData useresult) {
//                        if (getMvpView()!=null){
//
//                            if (useresult!=null&&!TextUtils.isEmpty(useresult.username)){
//                                getMvpView().showUserInfo(uid,true);
//
//                                User theuser = new User();
//                                theuser.setUid(uid);
//                                theuser.setUsername(useresult.username);
//                                theuser.setNickname(useresult.nickname);
//                                theuser.setAmount(useresult.amount);
//                                theuser.setEmail(useresult.email);
//                                theuser.setPhone(useresult.mobile);
//                                theuser.setImgSrc(useresult.middle_url);
//                                theuser.setVipStatus(Integer.parseInt(useresult.vipStatus));
//                                theuser.setExpireTime(TimeUtil.tansDateFrom1970(useresult.expireTime * 1000));
//                                mAccountManager.setUser(theuser);
//                                mAccountManager.saveUser(theuser);
//
//                                getMvpView().startToUser();
//                            }else {
//                                getMvpView().showUserInfo(uid,false);
//                            }
//                        }
//                    }
//                });
//    }
//}
