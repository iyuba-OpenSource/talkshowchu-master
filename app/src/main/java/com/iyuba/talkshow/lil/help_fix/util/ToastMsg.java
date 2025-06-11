package com.iyuba.talkshow.lil.help_fix.util;

public class ToastMsg {

    //登录接口
    public static String showLoginMsg(String key) {
        if (key.equals("000")) {
            return "账号或密码错误";
        } else if (key.equals("00")) {
            return "sign拼接错误";
        } else if (key.equals("102")) {
            return "账号不存在";
        } else if (key.equals("103")) {
            return "密码不正确";
        } else if (key.equals("0")) {
            return "服务器错误";
        }
        return "未知错误，请重试";
    }

    //注册接口
    public static String showRegisterMsg(String key) {
        if (key.equals("110")) {
            return "服务器错误";
        } else if (key.equals("112")) {
            return "账号已存在";
        } else if (key.equals("113")) {
            return "邮箱已存在";
        } else if (key.equals("114")) {
            return "账号格式错误";
        } else if (key.equals("000")) {
            return "填写的信息存在错误";
        } else if (key.equals("104")) {
            return "邮箱不正确";
        } else if (key.equals("115")) {
            return "手机号已存在";
        } else if (key.equals("00")) {
            return "sign拼接错误";
        } else if (key.equals("0")) {
            return "服务器异常";
        }
        return "未知错误，请重试";
    }

    //注销接口
    public static String showLogoutMsg(String key){
        if (key.equals("000")){
            return "账号或密码错误";
        }else if (key.equals("00")){
            return "sign参数错误";
        }else if (key.equals("103")){
            return "当前输入的密码错误";
        }else if (key.equals("102")){
            return "当前用户名不存在";
        }
        return "未知错误，请重试";
    }
}
