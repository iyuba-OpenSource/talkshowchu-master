//package com.iyuba.talkshow.lil.help_fix.manager;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
//import com.iyuba.ad.adblocker.AdBlocker;
//import com.iyuba.talkshow.data.model.User;
//import com.iyuba.talkshow.data.remote.UserService;
//import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
//
//import org.apache.commons.codec.binary.Base64;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.ObjectInputStream;
//
///**
// * @title: 新的一套用户管理类
// * @date: 2023/7/13 09:47
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description: 这一套数据管理类只用于数据存储和自有体系的
// */
//public class UserManager {
//
//    private static UserManager instance;
//
//    public static UserManager getInstance(){
//        if (instance==null){
//            synchronized (UserManager.class){
//                if (instance==null){
//                    instance = new UserManager();
//                }
//            }
//        }
//        return instance;
//    }
//
//    //获取数据
//    public User getSaveUser(){
//        try {
//            SharedPreferences preferences = ResUtil.getInstance().getApplication().getSharedPreferences("kouyu_show_file", Context.MODE_PRIVATE);
//            User user = (User) loadObject(preferences.getString("mUser",null));
//            return user;
//        }catch (Exception e){
//
//        }
//        return null;
//    }
//
//    /********************获取数据******************/
//    //获取用户id
//    public int getUserId(){
//        User user = getSaveUser();
//        if (user!=null){
//            return user.getUid();
//        }
//        return 0;
//    }
//
//    //获取用户名称
//    public String getUserName(){
//        User user = getSaveUser();
//        if (user!=null){
//            return user.getUsername();
//        }
//        return "";
//    }
//
//    //判断是否登录
//    public boolean isLogin(){
//        User user = getSaveUser();
//        if (user!=null&&user.getUid()!=0){
//            return true;
//        }
//        return false;
//    }
//
//    //判断为vip
//    public boolean isVip(){
//        User user = getSaveUser();
//        if (user!=null){
//            return user.getVipStatus()>= UserService.Login.Result.Code.VIP;
//        }
//        return false;
//    }
//
//    /********************辅助功能******************/
//    public Object loadObject(String objBase64String) throws IOException, ClassNotFoundException {
//        Object obj = null;
//        if(objBase64String != null) {
//            byte[] b = Base64.decodeBase64(objBase64String.getBytes());
//            InputStream bis = new ByteArrayInputStream(b);
//            ObjectInputStream ois = new ObjectInputStream(bis); // something wrong
//            obj = ois.readObject();
//            ois.close();
//        }
//        return obj;
//    }
//}
