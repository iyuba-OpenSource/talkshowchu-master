package com.iyuba.talkshow.util;

/**
 * 服务器返回的部分信息解析
 */
public class ServiceMsgUtil {

    //显示注册返回的错误信息
    public static String showRegisterErrorMsg(String result){
        if (result.equals("0")){
            return "服务器异常";
        }else if (result.equals("00")){
            return "内部处理错误";
        }else if (result.equals("000")){
            return "用户名/邮箱/密码未填写";
        }else if (result.equals("104")){
            return "邮箱非法";
        }else if (result.equals("110")){
            return "服务器错误";
        }else if (result.equals("111")){
            return "注册成功";
        }else if (result.equals("112")){
            return "用户名已被注册,请使用其他用户名";
        }else if (result.equals("113")){
            return "邮箱已被注册";
        }else if (result.equals("114")){
            return "用户名太长或太短,请输入3-15位字符";
        }else if (result.equals("115")){
            return "手机号已被注册";
        }
        return "注册失败";
    }

    //显示登陆返回的错误信息
    public static String showLoginErrorMsg(String result){
        if (result.equals("0")){
            return "服务器错误";
        }else if (result.equals("00")){
            return "内部处理错误";
        }else if (result.equals("000")){
            return "用户名/密码未填写";
        }else if (result.equals("101")){
            return "登录成功";
        }else if (result.equals("102")){
            return "用户名不存在";
        }else if (result.equals("103")){
            return "密码不正确";
        }
        return "登录失败";
    }
}
