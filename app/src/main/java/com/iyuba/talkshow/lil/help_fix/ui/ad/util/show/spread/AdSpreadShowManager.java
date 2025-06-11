package com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.spread;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.iyuba.talkshow.BuildConfig;
import com.iyuba.talkshow.constant.AdTestKeyData;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.manager.AdRemoteManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.AdDataUtil;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.AdLogUtil;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.yd.saas.base.interfaces.AdViewSpreadListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdSpread;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YouDaoNative;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 广告显示管理类-开屏广告
 */
public class AdSpreadShowManager {
    public static final String TAG = "AdSpreadShowManager";
    private static final String Show_ad_name = "开屏广告";

    private static AdSpreadShowManager instance;

    public static AdSpreadShowManager getInstance(){
        if (instance==null){
            synchronized (AdSpreadShowManager.class){
                if (instance==null){
                    instance = new AdSpreadShowManager();
                }
            }
        }
        return instance;
    }

    // TODO: 2024/7/26 临时逻辑：穆老师在新概念群里说：如果非web类型的广告在获取失败时(ad no fill)时，则去获取title的数据再加载一边
    private String randomAdType = "";
    private int randomAdCount = 0;

    /*****************************************临时数据**********************************************/
    private Activity context = null;
    private AdSpreadViewBean viewBean = null;

    public void setShowData(Activity context, AdSpreadViewBean viewBean){
        this.context = context;
        this.viewBean = viewBean;
    }

    /*****************************************广告接口**********************************************/
    //获取开屏广告数据
    private Disposable SpreadAdDis;

    public void showSpreadAd() {
        if (!AdShowUtil.Util.isPageExist(context) || viewBean == null) {
            return;
        }

        if (BuildConfig.DEBUG){
            ToastUtil.showToast(context,"正在请求开屏广告");
        }

        //显示日志
        AdLogUtil.showDebug(TAG,Show_ad_name+"-准备加载");
        //销毁广告
        stopSpreadAd();
        //开启操作计时器(限制在一定时间内需要显示广告，否则直接跳转)
        adHandler.sendEmptyMessage(time_operateDownTimer);
        //请求广告
        AdRemoteManager.getAd(UserInfoManager.getInstance().getUserId(), AdShowUtil.NetParam.Flag.net_spreadFlag, AdShowUtil.NetParam.getAdId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Ad_result>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        SpreadAdDis = disposable;
                    }

                    @Override
                    public void onNext(List<Ad_result> bean) {
                        if (!AdShowUtil.Util.isPageExist(context)) {
                            return;
                        }

                        //设置加载完成
                        viewBean.getOnAdSpreadShowListener().onLoadFinishAd();

                        if (bean == null || bean.size() == 0 || !bean.get(0).getResult().equals("1")) {
                            showWebSpreadAd(AdShowUtil.NetParam.AdType.show_web, null, null);
                            return;
                        }

                        if (BuildConfig.DEBUG){
                            ToastUtil.showToast(context,"请求完成，准备展示");
                        }

                        //显示广告
                        Ad_result showAdData = bean.get(0);
                        AdLogUtil.showDebug(TAG,Show_ad_name+"--"+showAdData.getData().getType());

                        //设置二次广告的类型
                        randomAdType = showAdData.getData().getTitle();
                        if (TextUtils.isEmpty(randomAdType)){
                            randomAdType = AdShowUtil.NetParam.AdType.show_web;
                        }
                        randomAdCount = 0;

                        //显示日志
                        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告接口加载完成-[当前广告："+AdShowUtil.Util.showAdName(showAdData.getData().getType())+"，二次广告："+AdShowUtil.Util.showAdName(randomAdType)+"]");

                        //分类展示
                        switch (showAdData.getData().getType()) {
                            case AdShowUtil.NetParam.AdType.show_youdao:
                                showYoudaoSpreadAd(showAdData.getData().getType(),showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                            case AdShowUtil.NetParam.AdType.show_ads1:
                            case AdShowUtil.NetParam.AdType.show_ads2:
                            case AdShowUtil.NetParam.AdType.show_ads3:
                            case AdShowUtil.NetParam.AdType.show_ads4:
                            case AdShowUtil.NetParam.AdType.show_ads5:
                            case AdShowUtil.NetParam.AdType.show_ads6:
                                showIyubaSdkSpreadAd(showAdData.getData().getType(),showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                            case AdShowUtil.NetParam.AdType.show_web:
                                showWebSpreadAd(showAdData.getData().getType(), showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                            default:
                                showWebSpreadAd(AdShowUtil.NetParam.AdType.show_other, showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        AdLogUtil.showDebug(TAG,Show_ad_name+"-广告接口加载异常");

                        showWebSpreadAd(AdShowUtil.NetParam.AdType.show_web, null, null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /******************************************广告显示**********************************************/
    //显示有道开屏广告
    private YouDaoNative youDaoNative = null;
    private void showYoudaoSpreadAd(String adType, String showPic, String jumpUrl) {
        if (!AdShowUtil.Util.isPageExist(context)) {
            return;
        }

        try {
            youDaoNative = new YouDaoNative(context, getSpreadAdKey(AdShowUtil.NetParam.AdType.show_youdao), new YouDaoNative.YouDaoNativeNetworkListener() {
                @Override
                public void onNativeLoad(NativeResponse nativeResponse) {
                    if (!AdShowUtil.Util.isPageExist(context)) {
                        return;
                    }

                    AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType));

                    //显示界面样式
                    viewBean.getAdView().setVisibility(View.VISIBLE);
                    viewBean.getTimeView().setVisibility(View.VISIBLE);
                    viewBean.getTipsView().setVisibility(View.VISIBLE);

                    String imageUrl = nativeResponse.getMainImageUrl();
                    Glide3Util.loadImg(context, imageUrl, 0, viewBean.getAdView());
                    nativeResponse.recordImpression(viewBean.getAdView());
                    adHandler.sendEmptyMessage(time_adDownTimer);
                    viewBean.getTimeView().setOnClickListener(v -> {
                        AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-广告关闭");
                        //广告回调
                        viewBean.getOnAdSpreadShowListener().onAdClose(adType);
                    });
                    viewBean.getAdView().setOnClickListener(v->{
                        AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-广告点击");
                        //点击操作
                        nativeResponse.handleClick(viewBean.getAdView());
                        //广告回调
                        viewBean.getOnAdSpreadShowListener().onAdClick(adType,false,null);
                    });
                }

                @Override
                public void onNativeFail(NativeErrorCode nativeErrorCode) {
                    AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-广告错误-("+nativeErrorCode.getCode()+")");
                    //广告回调
                    viewBean.getOnAdSpreadShowListener().onAdError(adType);
                    //加载二次广告
                    showSecondAdShow(AdShowUtil.NetParam.AdType.show_web, showPic, jumpUrl);
                }
            });
            RequestParameters requestParameters = new RequestParameters.RequestParametersBuilder().build();
            youDaoNative.makeRequest(requestParameters);
        } catch (Exception e) {
            showWebSpreadAd(AdShowUtil.NetParam.AdType.show_web,showPic, jumpUrl);
        }
    }

    //显示爱语吧sdk开屏广告
    private YdSpread ydSpread = null;
    private void showIyubaSdkSpreadAd(String adType, String showPic, String jumpUrl) {
        if (!AdShowUtil.Util.isPageExist(context)) {
            return;
        }

        //需要显示的广告key
        String showAdKey = getSpreadAdKey(adType);
        AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType));

        ydSpread = new YdSpread.Builder(context)
                .setKey(showAdKey)
                .setContainer(viewBean.getAdLayout())
                .setCountdownSeconds(5)
                .setSkipViewVisibility(true)
                .setSpreadListener(new AdViewSpreadListener() {
                    @Override
                    public void onAdDisplay() {
                        if (!AdShowUtil.Util.isPageExist(context)){
                            stopSpreadAd();
                            return;
                        }

                        //显示日志
                        AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-onAdDisplay");

                        //广告回调
                        viewBean.getOnAdSpreadShowListener().onAdShow(adType);
                    }

                    @Override
                    public void onAdClose() {
                        //显示日志
                        AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-onAdClose");
                        //广告回调
                        viewBean.getOnAdSpreadShowListener().onAdClose(adType);
                    }

                    @Override
                    public void onAdClick(String s) {
                        //显示日志
                        AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-onAdClick");
                        //广告回调
                        viewBean.getOnAdSpreadShowListener().onAdClick(adType,false,null);
                    }

                    @Override
                    public void onAdFailed(YdError ydError) {
                        //显示日志
                        AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-onAdFailed-("+ydError.getCode()+"--"+ydError.getErrorType()+"--"+ydError.getMsg()+")");
                        //广告回调
                        viewBean.getOnAdSpreadShowListener().onAdError(adType);
                        //加载二次广告
                        if (ydError.getMsg().equals("ad no fill")){
                            showSecondAdShow(AdShowUtil.NetParam.AdType.show_web,showPic, jumpUrl);
                        }else {
                            showWebSpreadAd(adType, showPic, jumpUrl);
                        }

                        //增加测试信息展示
                        if (BuildConfig.DEBUG){
                            ToastUtil.showToast(context,Show_ad_name+"--"+AdShowUtil.Util.showAdName(adType)+"--展示错误--("+ydError.getCode()+"--"+ydError.getErrorType()+")");
                        }
                    }
                }).build();
        ydSpread.requestSpread();
    }

    //显示web开屏广告
    private void showWebSpreadAd(String adType, String showPic, String jumpUrl) {
        if (!AdShowUtil.Util.isPageExist(context)) {
            return;
        }

        AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType));

        //合成新的图片和链接
        if (!TextUtils.isEmpty(showPic)) {
            showPic = AdDataUtil.AdUrl.fixPicUrl(showPic);

            if (!TextUtils.isEmpty(jumpUrl)) {
                jumpUrl = AdDataUtil.AdUrl.fixJumpUrl(jumpUrl);
            }
        }else {
            showPic = AdDataUtil.AdUrl.localSpreadAdPicUrl();
            jumpUrl = AdDataUtil.AdUrl.localSpreadAdJumpUrl();
        }

        //固定数据
        String showPicUrl = showPic;
        String showJumpUrl = jumpUrl;
        AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-展示链接："+showPicUrl+"--跳转链接："+showJumpUrl);

        //显示广告回调
        viewBean.getOnAdSpreadShowListener().onAdShow(adType);

        //显示样式
        viewBean.getAdLayout().setVisibility(View.VISIBLE);
        viewBean.getAdView().setVisibility(View.VISIBLE);
        viewBean.getTimeView().setVisibility(View.VISIBLE);
        viewBean.getTipsView().setVisibility(View.VISIBLE);

        Glide3Util.loadImg(context, showPicUrl, AdDataUtil.AdUrl.localSpreadAdPic, viewBean.getAdView());
        adHandler.sendEmptyMessage(time_adDownTimer);
        viewBean.getAdView().setOnClickListener(v->{
            AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-广告点击");
            //广告回调
            viewBean.getOnAdSpreadShowListener().onAdClick(adType,true,showJumpUrl);
        });
        viewBean.getTimeView().setOnClickListener(v->{
            AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-广告关闭");
            //广告回调
            viewBean.getOnAdSpreadShowListener().onAdClose(adType);
        });
    }

    //关闭开屏
    public void stopSpreadAd() {
        AdLogUtil.showDebug(TAG,Show_ad_name+"-广告销毁");

        if (ydSpread != null) {
            ydSpread.destroy();
        }
        if (youDaoNative != null) {
            youDaoNative.destroy();
        }
        if (adHandler!=null){
            adHandler.removeMessages(time_adDownTimer);
            adHandler.removeMessages(time_operateDownTimer);
        }

        RxUtil.unDisposable(SpreadAdDis);
    }

    // TODO: 2024/7/26 增加二次广告显示
    private void showSecondAdShow(String adType, String showPic, String jumpUrl){
        //根据数据判断
        if (randomAdCount>=1){
            showWebSpreadAd(adType, showPic, jumpUrl);
        }else {
            randomAdCount++;

            AdLogUtil.showDebug(TAG,Show_ad_name+"-二次广告加载("+randomAdType+")-"+AdShowUtil.Util.showAdName(randomAdType));

            //根据类型判断二次广告显示
            switch (randomAdType){
                case AdShowUtil.NetParam.AdType.show_youdao:
                    showYoudaoSpreadAd(randomAdType,showPic, jumpUrl);
                    break;
                case AdShowUtil.NetParam.AdType.show_ads1:
                case AdShowUtil.NetParam.AdType.show_ads2:
                case AdShowUtil.NetParam.AdType.show_ads3:
                case AdShowUtil.NetParam.AdType.show_ads4:
                case AdShowUtil.NetParam.AdType.show_ads5:
                case AdShowUtil.NetParam.AdType.show_ads6:
                    showIyubaSdkSpreadAd(randomAdType,showPic, jumpUrl);
                    break;
                case AdShowUtil.NetParam.AdType.show_web:
                    showWebSpreadAd(randomAdType,showPic, jumpUrl);
                    break;
                default:
                    showWebSpreadAd(randomAdType,showPic, jumpUrl);
                    break;
            }
        }
    }

    /*******************************************广告接口*********************************************/
    //开屏广告回调接口
    public interface OnAdSpreadShowListener {
        //加载完成广告
        void onLoadFinishAd();

        //展示广告
        void onAdShow(String adType);

        //点击广告
        void onAdClick(String adType, boolean isJumpByUserClick,String jumpUrl);

        //关闭广告
        void onAdClose(String adType);

        //广告异常
        void onAdError(String adType);

        //广告计时器(只用于wb计时)
        void onAdShowTime(boolean isEnd,int lastTime);

        //操作计时器
        void onOperateTime(boolean isEnd,int lastTime);
    }

    /*******************************************相关方法********************************************/
    //根据类型获取开屏广告的key
    private String getSpreadAdKey(String adType) {
        if (TextUtils.isEmpty(adType)) {
            return null;
        }

        switch (adType) {
            case AdShowUtil.NetParam.AdType.show_youdao://有道
                return AdTestKeyData.KeyData.SpreadAdKey.spread_youdao;
            case AdShowUtil.NetParam.AdType.show_ads1://倍孜
                return AdTestKeyData.KeyData.SpreadAdKey.spread_beizi;
            case AdShowUtil.NetParam.AdType.show_ads2://穿山甲
                return AdTestKeyData.KeyData.SpreadAdKey.spread_csj;
            case AdShowUtil.NetParam.AdType.show_ads3://百度
                return AdTestKeyData.KeyData.SpreadAdKey.spread_baidu;
            case AdShowUtil.NetParam.AdType.show_ads4://优量汇
                return AdTestKeyData.KeyData.SpreadAdKey.spread_ylh;
            case AdShowUtil.NetParam.AdType.show_ads5://快手
                return AdTestKeyData.KeyData.SpreadAdKey.spread_ks;
            case AdShowUtil.NetParam.AdType.show_ads6://瑞狮
                return null;
            default:
                return null;
        }
    }

    //计时器方法
    private static final int time_adDownTimer = 0;//广告倒计时开启
    private int adMarkTime = 0;//广告计时时间
    private static final int time_operateDownTimer = 1;//计时器开启
    private int operateMarkTime = 0;//操作计时时间
    private Handler adHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case time_adDownTimer:
                    //广告的计时器
                    if (viewBean.getAdShowDownTime()<0){
                        return;
                    }

                    //剩余时间
                    int adLastTime = viewBean.getAdShowDownTime() - adMarkTime;
                    if (adLastTime<0){
                        //显示日志
                        AdLogUtil.showDebug(TAG,Show_ad_name+"-广告计时器-计时器停止");
                        //关闭计时器
                        adHandler.removeMessages(time_adDownTimer);
                        //接口回调
                        viewBean.getOnAdSpreadShowListener().onAdShowTime(true,adLastTime);
                    }else {
                        //显示日志
                        AdLogUtil.showDebug(TAG,Show_ad_name+"-广告计时器-"+adLastTime);
                        //接口回调
                        viewBean.getOnAdSpreadShowListener().onAdShowTime(false,adLastTime);
                        //计时增加
                        adMarkTime++;
                        //计时信息
                        adHandler.sendEmptyMessageDelayed(time_adDownTimer,1000L);
                    }

                    break;
                case time_operateDownTimer:
                    //其他操作的计时器
                    if (viewBean.getOperateDownTime()<0){
                        return;
                    }

                    //剩余时间
                    int operateLastTime = viewBean.getOperateDownTime() - operateMarkTime;
                    if (operateLastTime<0){
                        //显示日志
                        AdLogUtil.showDebug(TAG,Show_ad_name+"-操作计时器-计时器停止");
                        //接口回调
                        viewBean.getOnAdSpreadShowListener().onOperateTime(true,operateLastTime);
                        //关闭计时器
                        adHandler.removeMessages(time_operateDownTimer);
                    }else {
                        //显示日志
                        AdLogUtil.showDebug(TAG,Show_ad_name+"-操作计时器-"+operateLastTime);
                        //接口回调
                        viewBean.getOnAdSpreadShowListener().onOperateTime(false,operateLastTime);
                        //计时增加
                        operateMarkTime++;
                        //计时信息
                        adHandler.sendEmptyMessageDelayed(time_operateDownTimer,1000L);
                    }
                    break;
            }
        }
    };

    //关闭广告计时器
    public void stopAdTimer(){
        if (adHandler!=null){
            adHandler.removeMessages(time_adDownTimer);
        }
    }

    //关闭操作计时器
    public void stopOperateTimer(){
        if (adHandler!=null){
            adHandler.removeMessages(time_operateDownTimer);
        }
    }
}