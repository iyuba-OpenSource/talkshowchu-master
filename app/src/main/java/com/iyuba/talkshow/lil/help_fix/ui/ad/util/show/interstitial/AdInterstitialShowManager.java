package com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.interstitial;

import android.app.Activity;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.iyuba.talkshow.BuildConfig;
import com.iyuba.talkshow.constant.AdTestKeyData;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.manager.AdRemoteManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.AdLogUtil;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.yd.saas.base.interfaces.AdViewInterstitialListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdInterstitial;
import com.youdao.sdk.nativeads.YouDaoNative;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 广告显示管理类-插屏广告
 */
public class AdInterstitialShowManager {
    private static final String TAG = "AdInterstitialShowManager";
    private static final String Show_ad_name = "插屏广告";

    private static AdInterstitialShowManager instance;

    public static AdInterstitialShowManager getInstance(){
        if (instance==null){
            synchronized (AdInterstitialShowManager.class){
                if (instance==null){
                    instance = new AdInterstitialShowManager();
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
    private AdInterstitialViewBean viewBean;

    public void setShowData(Activity context,AdInterstitialViewBean viewBean) {
        this.context = context;
        this.viewBean = viewBean;
    }

    /*****************************************广告接口**********************************************/
    private Disposable interstitialAdDis;

    public void showInterstitialAd(){
        if (!AdShowUtil.Util.isPageExist(context)) {
            return;
        }

        //显示日志
        AdLogUtil.showDebug(TAG, Show_ad_name + "-准备加载");
        //销毁广告
        stopInterstitialAd();
        //请求广告
        AdRemoteManager.getAd(UserInfoManager.getInstance().getUserId(), AdShowUtil.NetParam.Flag.net_spreadFlag, AdShowUtil.NetParam.getAdId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Ad_result>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        interstitialAdDis = disposable;
                    }

                    @Override
                    public void onNext(List<Ad_result> bean) {
                        if (!AdShowUtil.Util.isPageExist(context)) {
                            return;
                        }

                        if (bean == null || bean.size() == 0 || !bean.get(0).getResult().equals("1")) {
                            showWebInterstitialAd(AdShowUtil.NetParam.AdType.show_exception, null, null);
                            return;
                        }

                        if (BuildConfig.DEBUG){
                            ToastUtil.showToast(context,"请求完成，准备展示");
                        }

                        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告接口加载完成");

                        //设置加载完成
                        viewBean.getOnAdInterstitialShowListener().onLoadFinishAd();

                        //显示广告
                        Ad_result showAdData = bean.get(0);
                        AdLogUtil.showDebug(TAG, Show_ad_name + "--当前类型：" + showAdData.getData().getType()+"--二次类型："+showAdData.getData().getTitle());

                        //设置二次广告的类型
                        randomAdType = showAdData.getData().getTitle();
                        if (TextUtils.isEmpty(randomAdType)){
                            randomAdType = AdShowUtil.NetParam.AdType.show_web;
                        }
                        randomAdCount = 0;

                        //显示日志
                        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告接口加载完成-[当前广告："+AdShowUtil.Util.showAdName(showAdData.getData().getType())+"，二次广告："+AdShowUtil.Util.showAdName(randomAdType)+"]");

                        switch (showAdData.getData().getType()) {
                            case AdShowUtil.NetParam.AdType.show_youdao:
                                showYoudaoInterstitialAd(showAdData.getData().getType(), showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                            case AdShowUtil.NetParam.AdType.show_ads2:
                            case AdShowUtil.NetParam.AdType.show_ads3:
                            case AdShowUtil.NetParam.AdType.show_ads4:
                            case AdShowUtil.NetParam.AdType.show_ads5:
                            case AdShowUtil.NetParam.AdType.show_ads6:
                                showIyubaSdkInterstitialAd(showAdData.getData().getType(), showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                            case AdShowUtil.NetParam.AdType.show_web:
                                showWebInterstitialAd(showAdData.getData().getType(), showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                            default:
                                showWebInterstitialAd(showAdData.getData().getType(), showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告接口加载异常");

                        showWebInterstitialAd(AdShowUtil.NetParam.AdType.show_exception, null, null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //关闭广告
    public void stopInterstitialAd() {
        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告销毁");

        if (ydInterstitial != null) {
            ydInterstitial.destroy();
        }
        if (youDaoNative != null) {
            youDaoNative.destroy();
        }

        RxUtil.unDisposable(interstitialAdDis);
    }

    // TODO: 2024/7/26 增加二次广告显示
    private void showSecondAdShow(String adType, String showPic, String jumpUrl){
        //根据数据判断
        if (randomAdCount>=1){
            showWebInterstitialAd(adType, showPic, jumpUrl);
        }else {
            randomAdCount++;

            AdLogUtil.showDebug(TAG,Show_ad_name+"-二次广告加载-"+AdShowUtil.Util.showAdName(randomAdType));

            //根据类型判断二次广告显示
            switch (randomAdType){
                case AdShowUtil.NetParam.AdType.show_youdao:
                    showYoudaoInterstitialAd(randomAdType,showPic,jumpUrl);
                    break;
                case AdShowUtil.NetParam.AdType.show_ads1:
                case AdShowUtil.NetParam.AdType.show_ads2:
                case AdShowUtil.NetParam.AdType.show_ads3:
                case AdShowUtil.NetParam.AdType.show_ads4:
                case AdShowUtil.NetParam.AdType.show_ads5:
                case AdShowUtil.NetParam.AdType.show_ads6:
                    showIyubaSdkInterstitialAd(randomAdType,showPic,jumpUrl);
                    break;
                case AdShowUtil.NetParam.AdType.show_web:
                    showWebInterstitialAd(randomAdType,showPic, jumpUrl);
                    break;
                default:
                    showWebInterstitialAd(randomAdType,showPic, jumpUrl);
                    break;
            }
        }
    }

    /******************************************广告显示**********************************************/
    //显示有道插屏广告
    private YouDaoNative youDaoNative = null;
    private void showYoudaoInterstitialAd(String adType, String showPic, String jumpUrl){
        //显示日志
        AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-广告不属于显示的范围，加载二次广告("+AdShowUtil.Util.showAdName(randomAdType)+")");
        //加载二次广告
        showSecondAdShow(AdShowUtil.NetParam.AdType.show_web, showPic, jumpUrl);
    }

    //显示爱语吧sdk插屏广告
    private YdInterstitial ydInterstitial = null;
    private void showIyubaSdkInterstitialAd(String adType, String showPic, String jumpUrl){
        if (!AdShowUtil.Util.isPageExist(context)) {
            return;
        }

        try {
            //设置宽高
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int width = dm.widthPixels / 2;
            int height = (int) (dm.widthPixels * 0.6);

            AdLogUtil.showDebug(TAG, Show_ad_name + "-开始加载爱语吧sdk广告["+adType+"]");

            //展示广告
            String interstitialAdKey = getInterstitialAdKey(adType);
            ydInterstitial = new YdInterstitial.Builder(context)
                    .setKey(interstitialAdKey)
                    .setWidth(width)
                    .setHeight(height)
                    .setInterstitialListener(new AdViewInterstitialListener() {
                        @Override
                        public void onAdReady() {
                            if (!AdShowUtil.Util.isPageExist(context)){
                                stopInterstitialAd();
                                return;
                            }
                            //显示日志
                            AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-onAdReady");
                            //显示广告
                            ydInterstitial.show();
                        }

                        @Override
                        public void onAdDisplay() {
                            //显示日志
                            AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-onAdDisplay");
                        }

                        @Override
                        public void onAdClick(String s) {
                            //显示日志
                            AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-onAdClick");
                            //广告回调
                            viewBean.getOnAdInterstitialShowListener().onAdClick(adType,false,null);
                        }

                        @Override
                        public void onAdClosed() {
                            //显示日志
                            AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-onAdClosed");
                            //广告回调
                            viewBean.getOnAdInterstitialShowListener().onAdClose(adType);
                        }

                        @Override
                        public void onAdFailed(YdError ydError) {
                            //显示日志
                            AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-onAdFailed-("+ydError.getCode()+"--"+ydError.getErrorType()+"--"+ydError.getMsg()+")");
                            //广告回调
                            viewBean.getOnAdInterstitialShowListener().onAdError(adType);
                            //加载二次广告
                            if (ydError.getMsg().equals("ad no fill")){
                                showSecondAdShow(AdShowUtil.NetParam.AdType.show_web,showPic, jumpUrl);
                            }else {
                                showWebInterstitialAd(adType, showPic, jumpUrl);
                            }

                            //增加测试信息展示
                            if (BuildConfig.DEBUG){
                                ToastUtil.showToast(context,Show_ad_name+"--"+AdShowUtil.Util.showAdName(adType)+"--展示错误--("+ydError.getCode()+"--"+ydError.getErrorType()+")");
                            }
                        }
                    }).build();
            ydInterstitial.requestInterstitial();
        }catch (Exception e){
            //显示日志
            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-广告异常");
            //加载网页广告
            showWebInterstitialAd(AdShowUtil.NetParam.AdType.show_web, showPic, jumpUrl);
        }
    }

    //显示web插屏广告
    private void showWebInterstitialAd(String adType, String showPic, String jumpUrl){
        //显示日志
        AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-广告不属于显示的范围");
    }

    /*******************************************广告接口*********************************************/
    //插屏广告回调接口
    public interface OnAdInterstitialShowListener {
        //加载完成广告
        void onLoadFinishAd();

        //展示广告
        void onAdShow(String adType);

        //点击广告
        void onAdClick(String adType, boolean isJumpByUserClick, String jumpUrl);

        //关闭广告
        void onAdClose(String adType);

        //广告异常
        void onAdError(String adType);
    }

    /*******************************************相关方法********************************************/
    //根据类型获取插屏广告的key
    private String getInterstitialAdKey(String adType) {
        if (TextUtils.isEmpty(adType)) {
            return null;
        }

        switch (adType) {
            case AdShowUtil.NetParam.AdType.show_youdao://有道
                return null;
            case AdShowUtil.NetParam.AdType.show_ads2://穿山甲
                return AdTestKeyData.KeyData.InterstitialAdKey.interstitial_csj;
            case AdShowUtil.NetParam.AdType.show_ads3://百度
                return AdTestKeyData.KeyData.InterstitialAdKey.interstitial_baidu;
            case AdShowUtil.NetParam.AdType.show_ads4://优量汇
                return AdTestKeyData.KeyData.InterstitialAdKey.interstitial_ylh;
            case AdShowUtil.NetParam.AdType.show_ads5://快手
                return AdTestKeyData.KeyData.InterstitialAdKey.interstitial_ks;
            case AdShowUtil.NetParam.AdType.show_ads6://瑞狮
                return null;
            default:
                return null;
        }
    }
}
