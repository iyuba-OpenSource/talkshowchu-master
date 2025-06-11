package com.iyuba.talkshow.lil.help_fix.model.remote.manager;

import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.lil.help_fix.model.remote.RemoteManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_click_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_clock_submit;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_reward_vip;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_stream_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.newService.AdService;
import com.iyuba.talkshow.lil.help_fix.util.SignUtil;

import java.util.List;

import io.reactivex.Observable;

public class AdRemoteManager {

    //接口-获取广告信息(开屏、插屏、banner)
    //开屏：0，banner：4
    public static Observable<List<Ad_result>> getAd(int uid,int adFlag,int appId){
        AdService commonService = RemoteManager.getInstance().createJson(AdService.class);
        return commonService.getAd(appId,uid,adFlag);
    }

    //接口-获取信息流广告数据
    public static Observable<List<Ad_stream_result>> getTemplateAd(int userId,int flag,int appId){
        AdService adService = RemoteManager.getInstance().createJson(AdService.class);
        return adService.getStreamAd(appId,userId,flag);
    }

    //信息流广告
    public static Observable<List<Ad_stream_result>> getStreamAd(int uid){
        int appId = App.APP_ID;
        int flag = 2;

        AdService commonService = RemoteManager.getInstance().createJson(AdService.class);
        return commonService.getStreamAd(appId,uid,flag);
    }

    //获取信息流中web广告的相关展示信息
    //http://dev.iyuba.cn/getAdEntryAll.jsp?flag=5&appId=105&uid=0
    public static Observable<List<Ad_result>> getStreamWebAd(int uid){
        int appId = App.APP_ID;
        int flag = 5;

        AdService commonService = RemoteManager.getInstance().createJson(AdService.class);
        return commonService.getAd(appId,uid,flag);
    }

    //接口-点击广告获取奖励
    public static Observable<Ad_click_result> getAdClickReward(int uid, int platform, int adSpace){
        int appId = App.APP_ID;
        long timestamp = System.currentTimeMillis()/1000L;
        String sign = SignUtil.getAdClickSign(uid,appId,timestamp);

        AdService commonService = RemoteManager.getInstance().createJson(AdService.class);
        return commonService.getAdClick(uid,appId,platform,adSpace,timestamp,sign);
    }

    //接口-激励广告获取vip
    public static Observable<Ad_reward_vip> getAdRewardVip(int uid){
        int appId = App.APP_ID;
        long timestamp = System.currentTimeMillis()/1000L;
        String sign = SignUtil.getRewardAdVipSign(timestamp,uid,appId);

        AdService commonService = RemoteManager.getInstance().createJson(AdService.class);
        return commonService.getAdRewardVip(uid,appId,timestamp,sign);
    }

    //接口-提交广告数据
    public static Observable<Ad_clock_submit> submitAdData(int userId, String device, String deviceId, String packageName, String ads){
        int appId = App.APP_ID;
        long timestamp = System.currentTimeMillis()/1000L;
        int os = 2;

        AdService commonService = RemoteManager.getInstance().createJson(AdService.class);
        return commonService.submitAdData(String.valueOf(timestamp),appId,device,deviceId,userId,packageName,os,ads);
    }
}
