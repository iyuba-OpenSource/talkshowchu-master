package com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.banner;

import android.app.Activity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.talkshow.BuildConfig;
import com.iyuba.talkshow.constant.AdTestKeyData;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.manager.AdRemoteManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.AdDataUtil;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.AdLogUtil;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_fix.util.ScreenUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.yd.saas.base.interfaces.AdViewBannerListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdBanner;
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
 * 广告显示管理类-banner广告
 */
public class AdBannerShowManager {
    public static final String TAG = "AdBannerShowManager";
    private static final String Show_ad_name = "banner广告";

    private static AdBannerShowManager instance;

    public static AdBannerShowManager getInstance() {
        if (instance == null) {
            synchronized (AdBannerShowManager.class) {
                if (instance == null) {
                    instance = new AdBannerShowManager();
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
    private AdBannerViewBean viewBean = null;

    public void setShowData(Activity context, AdBannerViewBean viewBean) {
        this.context = context;
        this.viewBean = viewBean;
    }

    /*****************************************广告接口**********************************************/
    //获取banner广告
    private Disposable bannerAdDis;

    public void showBannerAd() {
        if (!AdShowUtil.Util.isPageExist(context) || viewBean == null) {
            return;
        }

        //显示日志
        AdLogUtil.showDebug(TAG, Show_ad_name + "-准备加载");
        //清除广告
        stopBannerAd();
        //清除视图
        viewBean.getIyubaSdkAdLayout().removeAllViews();
        viewBean.getIyubaSdkAdLayout().setVisibility(View.GONE);
        viewBean.getWebAdLayout().setVisibility(View.GONE);
        //请求广告
        AdRemoteManager.getAd(UserInfoManager.getInstance().getUserId(), AdShowUtil.NetParam.Flag.net_bannerFlag, AdShowUtil.NetParam.getAdId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Ad_result>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        bannerAdDis = disposable;
                    }

                    @Override
                    public void onNext(List<Ad_result> bean) {
                        if (!AdShowUtil.Util.isPageExist(context)) {
                            return;
                        }

                        if (bean == null || bean.size() == 0 || !bean.get(0).getResult().equals("1")) {
                            showWebBannerAd(AdShowUtil.NetParam.AdType.show_web, null, null);
                            return;
                        }

                        //设置加载完成
                        viewBean.getOnAdBannerShowListener().onLoadFinishAd();

                        //显示广告
                        Ad_result showAdData = bean.get(0);
                        AdLogUtil.showDebug(TAG, Show_ad_name + "--" + showAdData.getData().getType());

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
                                showYoudaoBannerAd(showAdData.getData().getType(), showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                            case AdShowUtil.NetParam.AdType.show_ads2:
                            case AdShowUtil.NetParam.AdType.show_ads3:
                            case AdShowUtil.NetParam.AdType.show_ads4:
                            case AdShowUtil.NetParam.AdType.show_ads5:
                            case AdShowUtil.NetParam.AdType.show_ads6:
                                showIyubaSdkBannerAd(showAdData.getData().getType(), showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                            case AdShowUtil.NetParam.AdType.show_web:
                                showWebBannerAd(showAdData.getData().getType(), showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                            default:
                                showWebBannerAd(AdShowUtil.NetParam.AdType.show_other, showAdData.getData().getStartuppic(), showAdData.getData().getStartuppic_Url());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告接口加载异常");

                        showWebBannerAd(AdShowUtil.NetParam.AdType.show_web, null, null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /******************************************广告显示**********************************************/
    //显示有道广告
    private YouDaoNative youDaoNative = null;
    private void showYoudaoBannerAd(String adType, String showPic, String jumpUrl) {
        if (!AdShowUtil.Util.isPageExist(context)) {
            return;
        }

        try {
            youDaoNative = new YouDaoNative(context, getBannerAdKey(AdShowUtil.NetParam.AdType.show_youdao), new YouDaoNative.YouDaoNativeNetworkListener() {
                @Override
                public void onNativeLoad(NativeResponse nativeResponse) {
                    if (!AdShowUtil.Util.isPageExist(context)) {
                        return;
                    }

                    //显示日志
                    AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType));

                    //显示界面样式
                    viewBean.getWebAdLayout().setVisibility(View.VISIBLE);
                    viewBean.getWebAdClose().setVisibility(View.VISIBLE);
                    if (viewBean.getWebAdTips()!=null){
                        viewBean.getWebAdTips().setVisibility(View.VISIBLE);
                    }

                    //设置宽高
                    ViewGroup.LayoutParams params = viewBean.getWebAdLayout().getLayoutParams();
                    params.width = ScreenUtil.getScreenW(context);
                    params.height = ScreenUtil.getScreenH(context)*7/40;
                    viewBean.getWebAdLayout().setLayoutParams(params);

                    //显示广告
                    String showPicUrl = nativeResponse.getMainImageUrl();
                    Glide3Util.loadImg(context, showPicUrl, 0, viewBean.getWebAdImage());
                    nativeResponse.recordImpression(viewBean.getWebAdImage());
                    viewBean.getWebAdImage().setOnClickListener(v -> {
                        AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-广告点击");
                        nativeResponse.handleClick(viewBean.getWebAdImage());

                        //广告回调
                        viewBean.getOnAdBannerShowListener().onAdClick(AdShowUtil.NetParam.AdType.show_youdao, false, null);
                    });
                    viewBean.getWebAdClose().setOnClickListener(v -> {
                        AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-广告关闭");
                        //广告回调
                        viewBean.getOnAdBannerShowListener().onAdClose(AdShowUtil.NetParam.AdType.show_youdao);
                    });
                }

                @Override
                public void onNativeFail(NativeErrorCode nativeErrorCode) {
                    AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-广告错误-(" + nativeErrorCode.getCode() + ")");
                    //广告回调
                    viewBean.getOnAdBannerShowListener().onAdError(adType);
                    //显示二次广告
                    showSecondAdShow(AdShowUtil.NetParam.AdType.show_web, showPic, jumpUrl);
                }
            });
            RequestParameters requestParameters = new RequestParameters.RequestParametersBuilder().build();
            youDaoNative.makeRequest(requestParameters);
        } catch (Exception e) {
            //显示日志
            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-广告异常");
            //显示网页广告
            showWebBannerAd(AdShowUtil.NetParam.AdType.show_web, showPic, jumpUrl);
        }
    }


    //显示爱语吧sdk广告
    private YdBanner ydBanner = null;
    private void showIyubaSdkBannerAd(String adType, String showPic, String jumpUrl) {
        if (!AdShowUtil.Util.isPageExist(context)) {
            return;
        }

        try {
            //需要显示的广告key
            String showAdKey = getBannerAdKey(adType);
            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType));

            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int width = dm.widthPixels;
            int height = dm.widthPixels * 7 / 40;
            //设置宽高
            ViewGroup.LayoutParams params = viewBean.getIyubaSdkAdLayout().getLayoutParams();
            params.width = width;
            params.height = height;
            viewBean.getIyubaSdkAdLayout().setLayoutParams(params);

            ydBanner = new YdBanner.Builder(context)
                    .setKey(showAdKey)
                    .setWidth(width)
                    .setHeight(height)
                    .setMaxTimeoutSeconds(5)
                    .setBannerListener(new AdViewBannerListener() {
                        @Override
                        public void onReceived(View view) {
                            if (!AdShowUtil.Util.isPageExist(context)){
                                stopBannerAd();
                                return;
                            }

                            //显示日志
                            AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-onReceived");
                            //添加到界面中
                            viewBean.getIyubaSdkAdLayout().removeAllViews();
                            viewBean.getIyubaSdkAdLayout().addView(view);
                            viewBean.getIyubaSdkAdLayout().setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdExposure() {
                            //显示日志
                            AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-onAdExposure");
                            //广告回调
                            viewBean.getOnAdBannerShowListener().onAdShow(adType);
                        }

                        @Override
                        public void onAdClick(String s) {
                            //显示日志
                            AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-onAdClick");
                            //广告回调
                            viewBean.getOnAdBannerShowListener().onAdClick(adType,false,null);
                        }

                        @Override
                        public void onClosed() {
                            //显示日志
                            AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-onAdClose");
                            //广告回调
                            viewBean.getOnAdBannerShowListener().onAdClose(adType);
                        }

                        @Override
                        public void onAdFailed(YdError ydError) {
                            //显示日志
                            AdLogUtil.showDebug(TAG,Show_ad_name+"-"+AdShowUtil.Util.showAdName(adType)+"-onAdFailed-("+ydError.getCode()+"--"+ydError.getErrorType()+"--"+ydError.getMsg()+")");
                            //广告回调
                            viewBean.getOnAdBannerShowListener().onAdError(adType);
                            //判断是否显示二次广告
                            if (ydError.getMsg().equals("ad no fill")){
                                showSecondAdShow(AdShowUtil.NetParam.AdType.show_web,showPic, jumpUrl);
                            }else {
                                showWebBannerAd(AdShowUtil.NetParam.AdType.show_web,showPic, jumpUrl);
                            }


                            //增加测试信息展示
                            if (BuildConfig.DEBUG){
                                ToastUtil.showToast(context,Show_ad_name+"--"+AdShowUtil.Util.showAdName(adType)+"--展示错误--("+ydError.getCode()+"--"+ydError.getErrorType()+")");
                            }
                        }
                    }).build();
            ydBanner.requestBanner();
        } catch (Exception e) {
            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-广告异常");
            showWebBannerAd(AdShowUtil.NetParam.AdType.show_web, showPic, jumpUrl);
        }
    }

    //显示web广告
    private void showWebBannerAd(String adType, String showPic, String jumpUrl) {
        if (!AdShowUtil.Util.isPageExist(context)) {
            return;
        }

        AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType));

        //合成新的图片和链接
        if (!TextUtils.isEmpty(showPic)) {
            showPic = AdDataUtil.AdUrl.fixPicUrl(showPic);

            if (!TextUtils.isEmpty(jumpUrl)) {
                jumpUrl = AdDataUtil.AdUrl.fixJumpUrl(jumpUrl);
            }
        }else {
            showPic = AdDataUtil.AdUrl.localBannerAdPicUrl();
            jumpUrl = AdDataUtil.AdUrl.localBannerAdJumpUrl();
        }


        //固定数据
        String showPicUrl = showPic;
        String showJumpUrl = jumpUrl;
        AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-展示链接：" + showPicUrl + "--跳转链接：" + showJumpUrl);

        //显示广告回调
        viewBean.getOnAdBannerShowListener().onAdShow(adType);

        //开启样式
        viewBean.getWebAdLayout().setVisibility(View.VISIBLE);
        viewBean.getWebAdClose().setVisibility(View.VISIBLE);
        if (viewBean.getWebAdTips()!=null){
            viewBean.getWebAdTips().setVisibility(View.VISIBLE);
        }
        //设置宽高
        ViewGroup.LayoutParams params = viewBean.getWebAdLayout().getLayoutParams();
        params.width = ScreenUtil.getScreenW(context);
        params.height = ScreenUtil.dip2px(context,60);
        viewBean.getWebAdLayout().setLayoutParams(params);
        //显示界面
        Glide3Util.loadImg(context, showPicUrl, AdDataUtil.AdUrl.localBannerAdPic, viewBean.getWebAdImage());
        viewBean.getWebAdImage().setOnClickListener(v -> {
            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-广告点击");
            //广告回调
            viewBean.getOnAdBannerShowListener().onAdClick(adType, true, showJumpUrl);
        });
        viewBean.getWebAdClose().setOnClickListener(v -> {
            AdLogUtil.showDebug(TAG, Show_ad_name + "-" + AdShowUtil.Util.showAdName(adType) + "-广告关闭");
            //广告回调
            viewBean.getOnAdBannerShowListener().onAdClose(adType);
        });
    }

    //关闭开屏
    public void stopBannerAd() {
        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告销毁");

        if (ydBanner != null) {
            ydBanner.destroy();
        }
        if (youDaoNative != null) {
            youDaoNative.destroy();
        }

        RxUtil.unDisposable(bannerAdDis);
    }

    // TODO: 2024/7/26 增加二次广告显示
    private void showSecondAdShow(String adType, String showPic, String jumpUrl){
        //根据数据判断
        if (randomAdCount>=1){
            showWebBannerAd(adType, showPic, jumpUrl);
        }else {
            randomAdCount++;

            AdLogUtil.showDebug(TAG,Show_ad_name+"-二次广告加载-"+AdShowUtil.Util.showAdName(randomAdType));

            //根据类型判断二次广告显示
            switch (randomAdType){
                case AdShowUtil.NetParam.AdType.show_youdao:
                    showYoudaoBannerAd(randomAdType,showPic, jumpUrl);
                    break;
                case AdShowUtil.NetParam.AdType.show_ads2:
                case AdShowUtil.NetParam.AdType.show_ads3:
                case AdShowUtil.NetParam.AdType.show_ads4:
                case AdShowUtil.NetParam.AdType.show_ads5:
                case AdShowUtil.NetParam.AdType.show_ads6:
                    showIyubaSdkBannerAd(randomAdType,showPic, jumpUrl);
                    break;
                case AdShowUtil.NetParam.AdType.show_web:
                    showWebBannerAd(randomAdType,showPic, jumpUrl);
                    break;
                default:
                    showWebBannerAd(randomAdType,showPic, jumpUrl);
                    break;
            }
        }
    }

    /*******************************************广告接口*********************************************/
    //banner广告回调接口
    public interface OnAdBannerShowListener {
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
    //根据类型获取banner广告的key
    private String getBannerAdKey(String adType) {
        if (TextUtils.isEmpty(adType)) {
            return null;
        }

        switch (adType) {
            case AdShowUtil.NetParam.AdType.show_youdao://有道
                return AdTestKeyData.KeyData.BannerAdKey.banner_youdao;
            case AdShowUtil.NetParam.AdType.show_ads2://穿山甲
                return AdTestKeyData.KeyData.BannerAdKey.banner_csj;
            case AdShowUtil.NetParam.AdType.show_ads3://百度
                return null;
            case AdShowUtil.NetParam.AdType.show_ads4://优量汇
                return AdTestKeyData.KeyData.BannerAdKey.banner_ylh;
            case AdShowUtil.NetParam.AdType.show_ads5://快手
                return null;
            case AdShowUtil.NetParam.AdType.show_ads6://瑞狮
                return null;
            default:
                return null;
        }
    }
}
