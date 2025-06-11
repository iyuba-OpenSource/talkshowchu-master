//package com.iyuba.talkshow.data.manager;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.iyuba.ad.adblocker.AdBlocker;
//import com.iyuba.module.user.IyuUserManager;
//import com.iyuba.talkshow.R;
//import com.iyuba.talkshow.TalkShowApplication;
//import com.iyuba.talkshow.data.DataManager;
//import com.iyuba.talkshow.data.model.TemporaryUserJson;
//import com.iyuba.talkshow.data.model.User;
//import com.iyuba.talkshow.data.model.UserData;
//import com.iyuba.talkshow.data.model.result.GetUserResponse;
//import com.iyuba.talkshow.data.model.result.LoginResponse;
//import com.iyuba.talkshow.data.remote.TemporaryAccountApi;
//import com.iyuba.talkshow.data.remote.UserInfoApi;
//import com.iyuba.talkshow.data.remote.UserService;
//import com.iyuba.talkshow.event.LoginEvent;
//import com.iyuba.talkshow.injection.ApplicationContext;
//import com.iyuba.talkshow.util.MD5;
//import com.iyuba.talkshow.util.NetStateUtil;
//import com.iyuba.talkshow.util.RxUtil;
//import com.iyuba.talkshow.util.ServiceMsgUtil;
//import com.iyuba.talkshow.util.TemporaryUserUtil;
//import com.iyuba.talkshow.util.TimeUtil;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.io.IOException;
//import java.util.Date;
//
//import javax.inject.Inject;
//import javax.inject.Singleton;
//
//import personal.iyuba.personalhomelibrary.PersonalHome;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import rx.Subscriber;
//import rx.Subscription;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
//@Singleton
//public class AccountManager {
//    private static final String USER = "mUser";
//
//    private final DataManager mDataManager;
//    private final ConfigManager mConfigManager;
//    private final Context mContext;
//    private static boolean islinshi = true;
//    private User mUser;
//
//    public static AccountManager getInstance(Context context, DataManager dataManager, ConfigManager configManager) {
//        return new AccountManager(context, dataManager, configManager);
//    }
//
//    @Inject
//    AccountManager(@ApplicationContext Context context, DataManager dataManager, ConfigManager configManager) {
//        this.mContext = context;
//        this.mDataManager = dataManager;
//        this.mConfigManager = configManager;
//    }
//
//    public boolean checkLogin() {
//        if (NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
//            Log.e("AccountM-checkLogin", "true");
//            if (mUser == null) {
//                mUser = getSaveUser();
//            }
//            return mUser != null;
//        } else {
//            Log.e("AccountM-checkLogin", "false");
//            return checkOffLineLogin();
//        }
//    }
//
//    private boolean checkOffLineLogin() {
//        try {
//            User tmpUser = (User) mDataManager.getPreferencesHelper().loadObject(USER);
//            if (tmpUser != null) {
//                if (!TextUtils.isEmpty(tmpUser.getUsername())
//                        && !TextUtils.isEmpty(tmpUser.getPassword())) {
//                    getSaveUser();
//                    //mUser.setUsername(tmpUser.getUsername());
//                }
//                return true;
//            }
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    public Subscription login(final String username, final String password, double latitude,
//                              double longitude, final OnLoginListener listener) {
//        return mDataManager.login(username, password, latitude, longitude)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<LoginResponse>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        if (listener != null)
//                            listener.onLoginFail(null);
//                        if (!NetStateUtil.isConnected(mContext)) {
//                            showToast(R.string.please_check_network);
//                        } else {
//                            showToast(R.string.request_fail);
//                        }
//                    }
//
//                    @Override
//                    public void onNext(LoginResponse loginResponse) {
//                        String errMsg = null;
//                        switch (loginResponse.result()) {
//                            case UserService.Login.Result.Code.SUCCESS:
//                                Log.e("loginresponesdatas", loginResponse.expireTime() + "" + loginResponse.vipStatus());
//                                setUser(loginResponse, password);
//                                if (mConfigManager.isAutoLogin()) {
//                                    saveUser();
//                                }
//                                // next repeat, above already save
////                                getUserDatas(username);
//                                getUserInfo(loginResponse.uid());
//                                EventBus.getDefault().post(new LoginEvent());
//                                if (listener != null) {
//                                    listener.onLoginSuccess(mUser);
//                                }
//                                Log.d("com.iyuba.talkshow", "onNext: "+ loginResponse);
////                                IMoviesApp.setUser(getUser().getUid()+"",getUser().getVipStatus()+"");
//                                break;
////                            case UserService.Login.Result.Code.USERNAME_NOT_EXIST:
////                                errMsg = UserService.Login.Result.Message.USERNAME_NOT_EXIST;
////                                break;
////                            case UserService.Login.Result.Code.NOT_MATCHING:
////                                errMsg = UserService.Login.Result.Message.NOT_MATCHING;
////                                break;
////                            case 0:
////                                errMsg = UserService.Login.Result.Message.LOGIN_SERVER;
////                                break;
////                            default:
////                                errMsg = UserService.Login.Result.Message.LOGIN_FAILURE;
////                                break;
//                            default:
//                                errMsg = ServiceMsgUtil.showLoginErrorMsg(String.valueOf(loginResponse.result()));
//                                break;
//                        }
//                        if (errMsg != null && listener != null) {
//                            listener.onLoginFail(errMsg);
//                        }
//                    }
//                });
//    }
//
//    private Subscription mGetUserSub;
//    public void getUserDatas(String username) {
//        RxUtil.unsubscribe(mGetUserSub);
//        mGetUserSub = mDataManager.getUser(username).
//                subscribeOn(Schedulers.io()).
//                observeOn(AndroidSchedulers.mainThread()).
//                subscribe(new Subscriber<GetUserResponse>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(GetUserResponse getUserResponse) {
//                        if ((getUserResponse != null) && getUserResponse.uid() != null) {
//                            setUser(getUserResponse);
//                            saveUser();
//                        }
//                    }
//                });
//    }
//
//    private Subscription mGetUserInfo;
//    public void getUserInfo(int uid) {
//        RxUtil.unsubscribe(mGetUserInfo);
//        mGetUserInfo = mDataManager.getUserInfo(getUid(), uid).
//                subscribeOn(Schedulers.io()).
//                observeOn(AndroidSchedulers.mainThread()).
//                subscribe(new Subscriber<UserData>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (e != null) {
//                            Log.e("AccountM-checkLogin", "getUserInfo onError " + e.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onNext(UserData useresult) {
//                        if (useresult != null) {
//                            User theuser = getUser();
//                            theuser.setUsername(useresult.username);
//                            theuser.setNickname(useresult.nickname);
//                            theuser.setAmount(useresult.amount);
//                            theuser.setMoney(String.valueOf(useresult.money));
//                            theuser.setEmail(useresult.email);
//                            theuser.setPhone(useresult.mobile);
//                            theuser.setImgSrc(useresult.middle_url);
//                            theuser.setVipStatus(Integer.parseInt(useresult.vipStatus));
//                            theuser.setExpireTime(TimeUtil.tansDateFrom1970(useresult.expireTime * 1000));
//                            saveUser(theuser);
//
//                            EventBus.getDefault().post(new LoginEvent());
//                        } else {
//                            Log.e("AccountM-checkLogin", "getUserInfo onNext is null.");
//                        }
//                    }
//                });
//    }
//
//    private void showToast(int resId) {
//        if (mContext != null)
//            Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
//    }
//
//    public boolean loginOut() {
//        mConfigManager.putLastUid(mUser.getUid());
//        mUser = null;
//        mDataManager.getPreferencesHelper().remove(USER);
//        return true;
//    }
//
//    public boolean saveUser() {
//        try {
//            mDataManager.getPreferencesHelper().putObject(USER, mUser);
//
//            //个人中心
//            com.iyuba.module.user.User videoUser = new com.iyuba.module.user.User();
//            videoUser.uid = getUid();
//            videoUser.name = getUserName();
//            videoUser.vipStatus = isVip()?"1":"0";
//            IyuUserManager.getInstance().setCurrentUser(videoUser);
//
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean saveUser(User user) {
//        try {
//            mDataManager.getPreferencesHelper().putObject(USER, user);
//            setUser(user);
//
//            //个人中心
//            com.iyuba.module.user.User videoUser = new com.iyuba.module.user.User();
//            videoUser.uid = getUid();
//            videoUser.name = getUserName();
//            videoUser.vipStatus = isVip()?"1":"0";
//            IyuUserManager.getInstance().setCurrentUser(videoUser);
//
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public User getSaveUser() {
//        try {
//            mUser = (User) mDataManager.getPreferencesHelper().loadObject(USER);
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return mUser;
//    }
//
//    public void removeUser() {
//        mConfigManager.putLastUid(0);
//        mUser = null;
//        mDataManager.getPreferencesHelper().remove(USER);
//        mConfigManager.setAdName("");
//        mConfigManager.setAdPass("");
//    }
//    public String getPassWord() {
//        if (mUser == null) {
//            return null;
//        }
//        return mConfigManager.getAdPass();
//    }
//
//    public User getUser() {
//        // Log.e("Account-getUser",mUser.toString());
//        if (mUser == null) {
//            mUser = getSaveUser();
//        }
//        return mUser;
//    }
//
//    public void setUser(User user) {
//        this.mUser = user;
//    }
//
//    public void setUser(LoginResponse loginResponse, String password) {
//        if (mUser == null) {
//            mUser = new User();
//        }
//        mUser.setUid(loginResponse.uid());
//        mUser.setImgSrc(loginResponse.imageSrc());
//        mUser.setUsername(loginResponse.username());
//        mUser.setNickname(loginResponse.nickname());
//        mUser.setPassword(password);
//        mUser.setIntegral(loginResponse.integral());
//        mUser.setExpireTime(TimeUtil.tansDateFrom1970(loginResponse.expireTime() * 1000));
//        mUser.setLastLoginTime(System.currentTimeMillis());
//        mUser.setAmount(loginResponse.amount());
//        mUser.setVipStatus(Integer.valueOf(loginResponse.vipStatus()));
//        mUser.setMoney(loginResponse.money());
//        if (!TextUtils.isEmpty(password)) {
//            mConfigManager.setAdName(loginResponse.username());
//            mConfigManager.setAdPass(password);
//        }
//        Log.e("login-user", mUser.toString() + loginResponse.vipStatus());
//    }
//
//    public void setUser(GetUserResponse response) {
//        if (mUser == null) {
//            mUser = new User();
//        }
//        mUser.setUid(Integer.valueOf(response.uid()));
//        mUser.setImgSrc(response.imageSrc());
//        mUser.setUsername(response.username());
//        mUser.setEmail(response.email());
//        mUser.setPhone(response.phone());
//        mUser.setVipStatus(Integer.valueOf(response.vipStatus()));
//        mUser.setExpireTime(TimeUtil.tansDateFrom1970(response.expireTime() + "000"));
//        mUser.setAmount(Integer.valueOf(response.amount()));
//        mUser.setMoney(response.money());
////        mUser.setMoney(response.money()); diao  TODO
//        Log.e("set-user", mUser.toString() + "");
//    }
//
//    public void getTemporaryAccount() {
//        Log.e("getTemporaryAccount", "执行了");
//        Log.e("temapi", "http://api.iyuba.com.cn/v2/api.iyuba?protocol=" + TemporaryAccountApi.PROTOCOL
//                + "&deviceId=" + TemporaryUserUtil.PhoneDiviceId() + "&platform=" + TemporaryAccountApi.PLATFORM + "&appid=" +
//                TemporaryAccountApi.APPID + "&format=" + TemporaryAccountApi.FORMAT + "&sign=" + TemporaryUserUtil.getSignString());
//        TemporaryAccountApi.Creator.newTemporaryAccountApi().getTemporaryAccount(TemporaryAccountApi.PROTOCOL,
//                TemporaryUserUtil.PhoneDiviceId(), TemporaryAccountApi.PLATFORM, TemporaryAccountApi.APPID,
//                TemporaryAccountApi.FORMAT, TemporaryUserUtil.getSignString()).enqueue(new Callback<TemporaryUserJson>() {
//            @Override
//            public void onResponse(Call<TemporaryUserJson> call, Response<TemporaryUserJson> response) {
//                if (response.isSuccessful()) {
//                    final TemporaryUserJson result = response.body();
//                    if (result != null && result.getUid() > 0) {
//                        Log.e("LinshiId", result.getUid() + "-" + result);
//                        UserInfoApi.Creator.newUserInfoApi().userInfoApi(UserInfoApi.Params.PLATFORM,
//                                UserInfoApi.Params.FORMAT, UserInfoApi.Params.PROTOCOL, UserInfoApi.Params.APPID,
//                                result.getUid() + "", result.getUid() + "",
//                                MD5.getMD5ofStr("20001" + result.getUid() + "" + "iyubaV2")).enqueue(new Callback<UserData>() {
//                            @Override
//                            public void onResponse(Call<UserData> call, Response<UserData> response) {
//                                if (response.isSuccessful()) {
//                                    UserData useresult = response.body();
//                                    if (useresult != null) {
//                                        User theuser = new User();
//                                        theuser.setUid(result.getUid());
//                                        theuser.setUsername("" + result.getUid());
//                                        theuser.setAmount(useresult.amount);
//                                        theuser.setEmail(useresult.email);
//                                        theuser.setExpireTime(TimeUtil.tansDateFrom1970(useresult.expireTime));
//                                        theuser.setPhone("");
//                                        theuser.setImgSrc(useresult.middle_url);
//                                        theuser.setVipStatus(Integer.parseInt(useresult.vipStatus));
//                                        islinshi = true;
//                                        mConfigManager.setLinshi(islinshi);
//                                        saveUser(theuser);
//                                        mUser = theuser;
//                                    }
//                                    Log.e("userdata", useresult.toString());
//
//                                }
//
//
//                            }
//
//                            @Override
//                            public void onFailure(Call<UserData> call, Throwable t) {
//                                Log.e("Throwable-userdata", t.toString());
//                            }
//                        });
//
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<TemporaryUserJson> call, Throwable t) {
//                Log.e("Throwable-TemporaryUser", t.toString());
//            }
//        });
//    }
//
//    public boolean isVip() {
//        if (mUser == null) {
//            mUser = getSaveUser();
//            Log.e("Account-user", "为空！");
//        } else {
//            Log.e("Account-getUser", mUser + "不为空");
//        }
//        return mUser != null && mUser.getVipStatus() >= UserService.Login.Result.Code.VIP;
//    }
//
//    public boolean isAppLifelongVip() {
//        return isVip() && mUser.getExpireTime()
//                .contains(String.valueOf(UserService.Login.Result.Code.YEAR_OF_LIFELONG));
//    }
//
//    public int getUid() {
//        if (mUser == null) {
//            mUser = getSaveUser();
//        }
//        return mUser != null ? mUser.getUid() : 0;
//    }
//
//    public String getUserName() {
//        if (mUser == null) {
//            mUser = getSaveUser();
//        }
//        if (null == mUser || TextUtils.isEmpty(mUser.getUsername())) {
//            return getUid() + "";
//        }
//        return mUser.getUsername();
//    }
//
//    public void initPersonHome() {
//        PersonalHome.setSaveUserinfo(getUid(), getUserName(), isVip() ? "1" : "0");
//    }
//
//    public void initMocUser() {
//        com.iyuba.module.user.User mocUser = new com.iyuba.module.user.User();
//        mocUser.uid = getUid();
//        mocUser.name = getUserName();
//        mocUser.vipStatus = "" + getUser().getVipStatus();
//        mocUser.mobile = getUser().getPhone();
//        if (TextUtils.isEmpty(getUser().getMoney())){
//            mocUser.money = 0;
//        }else {
//            float mon = Float.parseFloat(getUser().getMoney());
//            mocUser.money = (int) mon;
//        }
//        mocUser.iyubiAmount = getUser().getAmount();
//        IyuUserManager.getInstance().setCurrentUser(mocUser);
//
//        //设置广告屏蔽时间
////        if (isVip()){
////            try {
////                Date compileDate = TimeUtil.GLOBAL_SDF.parse(getUser().getExpireTime());
////                AdBlocker.getInstance().setBlockStartDate(compileDate);
////            }catch (Exception e){
////
////            }
////        }
//    }
//}
