//package com.iyuba.talkshow.ui.user.login.fix;
//
//import com.iyuba.talkshow.data.model.result.RegisterMobResponse;
//import com.iyuba.talkshow.ui.base.MvpView;
//
///**
// * @desction:
// * @date: 2023/3/14 10:50
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// */
//public interface FixLoginMvpView extends MvpView {
//
//    //获取微信的token
//    void showWxToken(String token);
//
//    //根据token获取uid
//    void showWxUserId(int uid);
//
//    //获取用户信息
//    void showUserInfo(int uid,boolean isSuccess);
//
//    //跳转用户界面
//    void startToUser();
//
//    //跳转信息完善界面
//    void startToImprover(int userId);
//
//    //获取mob的校验信息
//    void showMobVerifyCheckMsg(boolean isSuccess,boolean isToRegister,RegisterMobResponse response);
//}
