package com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.spread;

import android.content.SharedPreferences;

import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.help_mvp.util.SPUtil;

/**
 * @title: 广告显示管理
 * @date: 2023/10/25 10:46
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class AdSpreadShowSession {
    private static final String TAG = "AdShowManager";
    private static AdSpreadShowSession instance;

    public static AdSpreadShowSession getInstance(){
        if (instance==null){}
        synchronized (AdSpreadShowSession.class){
            if (instance==null){
                instance = new AdSpreadShowSession();
            }
        }
        return instance;
    }

    //缓存
    private SharedPreferences preferences;

    private SharedPreferences getPreferences(){
        if (preferences==null){
            preferences = SPUtil.getPreferences(ResUtil.getInstance().getContext(), TAG);
        }
        return preferences;
    }

    //设置开屏广告显示的免费次数
    private static final String AD_SPLASH_SHOW_COUNT = "splashShowCount";
    private static final int SPLASH_AD_FREE_COUNT = 3;

    public void setCurSplashAdShowCount() {
        int curShowCount = getPreferences().getInt(AD_SPLASH_SHOW_COUNT, 0);
        getPreferences().edit().putInt(AD_SPLASH_SHOW_COUNT,curShowCount+1).apply();
    }

    public boolean isCanShowSplashAd(){
//        if (BuildConfig.DEBUG){
//            return true;
//        }

        int curShowCount = getPreferences().getInt(AD_SPLASH_SHOW_COUNT, 0);
        if (curShowCount>=SPLASH_AD_FREE_COUNT){
            return true;
        }
        return false;
    }

    public int  getSplashShowCount(){
        return getPreferences().getInt(AD_SPLASH_SHOW_COUNT, 0);
    }
}
