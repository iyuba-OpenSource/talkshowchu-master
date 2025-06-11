package com.iyuba.talkshow.lil.login;

//用于保存微信小程序登陆的token
public class FixLoginSession {

    private static FixLoginSession instance;

    public static FixLoginSession getInstance(){
        if (instance==null){
            synchronized (FixLoginSession.class){
                if (instance==null){
                    instance = new FixLoginSession();
                }
            }
        }
        return instance;
    }

    //微信小程序登陆的token
    private String wxSmallToken;

    public void setWxSmallToken(String token){
        this.wxSmallToken = token;
    }

    public String getWxSmallToken() {
        return wxSmallToken;
    }
}
