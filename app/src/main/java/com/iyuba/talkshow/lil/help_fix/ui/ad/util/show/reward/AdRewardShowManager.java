package com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.reward;

import android.app.Activity;
import android.text.TextUtils;

import com.iyuba.talkshow.BuildConfig;
import com.iyuba.talkshow.constant.AdTestKeyData;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.manager.AdRemoteManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.AdLogUtil;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.yd.saas.base.interfaces.AdViewVideoListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdVideo;
import com.youdao.sdk.nativeads.YouDaoNative;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 广告显示管理类-激励视频广告
 */
public class AdRewardShowManager {
    private static final String TAG = "AdRewardShowManager";
    private static final String Show_ad_name = "激励视频广告";

    private static AdRewardShowManager instance;

    public static AdRewardShowManager getInstance() {
        if (instance == null) {
            synchronized (AdRewardShowManager.class) {
                if (instance == null) {
                    instance = new AdRewardShowManager();
                }
            }
        }
        return instance;
    }

    /*****************************************临时数据**********************************************/
    private Activity context = null;
    private AdRewardViewBean viewBean = null;

    public void setShowData(Activity context, AdRewardViewBean viewBean) {
        this.context = context;
        this.viewBean = viewBean;
    }

    /*****************************************广告接口**********************************************/
    //获取激励视频的广告
    private Disposable rewardVideoAdDis;

    public void showRewardVideoAd() {
        if (!AdShowUtil.Util.isPageExist(context) || viewBean == null) {
            return;
        }

        //显示日志
        AdLogUtil.showDebug(TAG, Show_ad_name + "-准备加载");
        //清除广告
        stopRewardVideoAd();
        //请求广告
        AdRemoteManager.getAd(UserInfoManager.getInstance().getUserId(), AdShowUtil.NetParam.Flag.net_spreadFlag, AdShowUtil.NetParam.getAdId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Ad_result>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        rewardVideoAdDis = disposable;
                    }

                    @Override
                    public void onNext(List<Ad_result> bean) {
                        if (!AdShowUtil.Util.isPageExist(context)) {
                            return;
                        }

                        if (bean == null || bean.size() == 0 || !bean.get(0).getResult().equals("1")) {
                            showWebRewardAd(AdShowUtil.NetParam.AdType.show_web, null, null);
                            return;
                        }

                        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告接口加载完成");

                        //设置加载完成
                        viewBean.getOnAdRewardShowListener().onLoadFinishAd();

                        //显示广告
                        Ad_result showAdData = bean.get(0);
                        AdLogUtil.showDebug(TAG, Show_ad_name + "--" + showAdData.getData().getType());

                        switch (showAdData.getData().getType()) {
                            case AdShowUtil.NetParam.AdType.show_youdao:
                                showYoudaoRewardAd(showAdData.getData().getType(), showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                            case AdShowUtil.NetParam.AdType.show_ads2:
                            case AdShowUtil.NetParam.AdType.show_ads3:
                            case AdShowUtil.NetParam.AdType.show_ads4:
                            case AdShowUtil.NetParam.AdType.show_ads5:
                            case AdShowUtil.NetParam.AdType.show_ads6:
                                showIyubaSdkRewardAd(showAdData.getData().getType(), showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                            case AdShowUtil.NetParam.AdType.show_web:
                                showWebRewardAd(showAdData.getData().getType(), showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                            default:
                                showWebRewardAd(AdShowUtil.NetParam.AdType.show_other, showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告接口加载异常");

                        showWebRewardAd(AdShowUtil.NetParam.AdType.show_web, null, null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //显示有道激励广告
    private YouDaoNative youDaoNative = null;

    private void showYoudaoRewardAd(String adType, String showPic, String jumpUrl) {
        viewBean.getOnAdRewardShowListener().onAdError(adType);
    }

    //显示爱语吧sdk激励广告
    private YdVideo ydVideo = null;
    //是否已经点击广告
    private boolean isVideoClicked = false;
    //是否已经达到激励视频要求
    private boolean isVideoReward = false;

    private void showIyubaSdkRewardAd(String adType, String showPic, String jumpUrl) {
        if (!AdShowUtil.Util.isPageExist(context)) {
            return;
        }

        //重置标识符
        isVideoClicked = false;
        isVideoReward = false;

        try {
            //需要显示的广告key
            String showAdKey = getRewardAdKey(adType);
            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType));

            ydVideo = new YdVideo.Builder(context)
                    .setKey(showAdKey)
                    .setVideoListener(new AdViewVideoListener() {
                        @Override
                        public void onAdShow() {
                            if (!AdShowUtil.Util.isPageExist(context)) {
                                stopRewardVideoAd();
                                return;
                            }

                            //显示日志
                            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-onAdShow");
                        }

                        @Override
                        public void onAdClose() {
                            //显示日志
                            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-onAdClose");
                            //广告回调
                            viewBean.getOnAdRewardShowListener().onAdClose(adType);
                            //关闭广告
                            stopRewardVideoAd();
                        }

                        @Override
                        public void onVideoPrepared() {
                            //显示日志
                            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-onVideoPrepared");
                            //显示广告
                            ydVideo.show();
                            //广告回调
                            viewBean.getOnAdRewardShowListener().onAdShow(adType);
                        }

                        @Override
                        public void onVideoReward(double v) {
                            //显示日志
                            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-onVideoReward");
                            //设置激励操作
                            isVideoReward = true;
                            //广告回调
                            /*if (isVideoReward && isVideoClicked){
                                //重置数据
                                isVideoReward = false;
                                isVideoClicked = false;
                                //回调接口
                                viewBean.getOnAdRewardShowListener().onAdClick(adType, false, null,true);
                            }*/
                        }

                        @Override
                        public void onVideoCompleted() {
                            //显示日志
                            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-onVideoCompleted");
                        }

                        @Override
                        public void onAdClick(String s) {
                            //显示日志
                            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-onAdClick");
                            //设置点击操作
                            isVideoClicked = true;
                            //广告回调
                            /*if (isVideoReward && isVideoClicked){
                                //重置数据
                                isVideoReward = false;
                                isVideoClicked = false;
                                //回调接口
                                viewBean.getOnAdRewardShowListener().onAdClick(adType, false, null,true);
                            }else {
                                //回调接口(这里没有获取激励，外面显示提示信息)
                                viewBean.getOnAdRewardShowListener().onAdClick(adType, false, null,false);
                            }*/
                            if (isVideoReward){
                                //回调接口
                                viewBean.getOnAdRewardShowListener().onAdClick(adType, false, null,true);
                            }else {
                                //回调接口
                                viewBean.getOnAdRewardShowListener().onAdClick(adType, false, null,false);
                            }
                        }

                        @Override
                        public void onSkipVideo() {
                            //显示日志
                            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-onSkipVideo");
                        }

                        @Override
                        public void onAdFailed(YdError ydError) {
                            //显示日志
                            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-onAdFailed-(" + ydError.getCode() + "--" + ydError.getErrorType() + "--" + ydError.getMsg() + ")");
                            //广告回调
                            viewBean.getOnAdRewardShowListener().onAdError(adType);
                            //加载网页广告
                            showWebRewardAd(AdShowUtil.NetParam.AdType.show_web, showPic, jumpUrl);

                            //增加测试信息展示
                            if (BuildConfig.DEBUG) {
                                ToastUtil.showToast(context, Show_ad_name + "--" + AdShowUtil.Util.showAdName(adType) + "--展示错误--(" + ydError.getCode() + "--" + ydError.getErrorType() + ")");
                            }
                        }
                    }).build();
            ydVideo.requestRewardVideo();
        } catch (Exception e) {
            //显示日志
            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-广告异常");
            //显示web开广告
            showWebRewardAd(AdShowUtil.NetParam.AdType.show_web, showPic, jumpUrl);
        }
    }

    //显示网页激励广告
    private void showWebRewardAd(String adType, String showPic, String jumpUrl) {
        viewBean.getOnAdRewardShowListener().onAdError(adType);
    }

    //关闭广告
    public void stopRewardVideoAd() {
        if (youDaoNative != null) {
            youDaoNative.destroy();
        }
        if (ydVideo != null) {
            ydVideo.destroy();
        }

        RxUtil.unDisposable(rewardVideoAdDis);
    }

    /*******************************************广告接口*********************************************/
    //激励广告回调接口
    public interface OnAdRewardShowListener {
        //加载完成广告
        void onLoadFinishAd();

        //展示广告
        void onAdShow(String adType);

        //点击广告
        void onAdClick(String adType, boolean isJumpByUserClick, String jumpUrl,boolean isFinishReward);

        //关闭广告
        void onAdClose(String adType);

        //广告异常
        void onAdError(String adType);
    }

    /*******************************************相关方法********************************************/
    //根据类型获取激励视频广告的key
    private String getRewardAdKey(String adType) {
        if (TextUtils.isEmpty(adType)) {
            return null;
        }

        switch (adType) {
            case AdShowUtil.NetParam.AdType.show_youdao://有道
                return null;
            case AdShowUtil.NetParam.AdType.show_ads2://穿山甲
                return AdTestKeyData.KeyData.IncentiveAdKey.incentive_csj;
            case AdShowUtil.NetParam.AdType.show_ads3://百度
                return AdTestKeyData.KeyData.IncentiveAdKey.incentive_baidu;
            case AdShowUtil.NetParam.AdType.show_ads4://优量汇
                return AdTestKeyData.KeyData.IncentiveAdKey.incentive_ylh;
            case AdShowUtil.NetParam.AdType.show_ads5://快手
                return AdTestKeyData.KeyData.IncentiveAdKey.incentive_ks;
            case AdShowUtil.NetParam.AdType.show_ads6://瑞狮
                return null;
            default:
                return null;
        }
    }
}
