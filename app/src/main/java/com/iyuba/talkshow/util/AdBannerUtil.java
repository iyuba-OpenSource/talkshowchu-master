package com.iyuba.talkshow.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.youdao.sdk.nativeads.ImageService;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YouDaoNative;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 加载banner广告
 */
public class AdBannerUtil {
    private static final String TAG = "Ad-Banner";
    private final Context context;
    private final YouDaoNative youdaoNative;
    private View adView;
    private ImageView adImageView;
    //    private AddamBanner addamBanner;
    private ViewGroup adMiaozeParent;
//    private AdView miaozeBannerAdView;

    private String lastAD;
    private final String TYPE_DAM = "addam";
    private final String TYPE_YOUDAO = "youdao";
    private final String TYPE_IYUBA = "web";
    private final String TYPE_MIAOZE = "ssp";
    // iyuba广告一分钟加载一次
    private final int intervalTime = 60 * 1000;

    // 广告自己切换的时间
    private final int adIntervalTime = 10;

    private TextView close;
    private final boolean isIyubaAdTimerStarted = false;
    private final Handler iyubaAdHandler = new Handler();

    public AdBannerUtil(Context context) {
        this.context = context;
        youdaoNative = new YouDaoNative(context, ConfigData.YOUDAO_AD_BANNER_CODE, youDaoAdListener);
    }

    public void setView(View view, ImageView imageView, TextView textView) {
        this.adImageView = imageView;
        this.adView = view;
        this.close = textView;
    }


    public void setMiaozeView(ViewGroup viewGroup) {
        this.adMiaozeParent = viewGroup;
    }


    public void loadYouDaoAD() {
        if (context == null) {
            return;
        }
        Log.e(TAG, "加载有道广告");
//        adView.setVisibility(View.VISIBLE);
        RequestParameters requestParameters = new RequestParameters.RequestParametersBuilder().build();
        youdaoNative.makeRequest(requestParameters);
    }


    private final YouDaoNative.YouDaoNativeNetworkListener youDaoAdListener = new YouDaoNative.YouDaoNativeNetworkListener() {
        @Override
        public void onNativeLoad(final NativeResponse nativeResponse) {
            Log.e(TAG, "有道广告加载成功");
            if ((context == null) || (nativeResponse == null)) {
                return;
            }

            List<String> imageUrls = new ArrayList<>();
            imageUrls.add(nativeResponse.getMainImageUrl());
            adImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nativeResponse.handleClick(adImageView);
                }
            });
            ImageService.get(context, imageUrls, new ImageService.ImageServiceListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onSuccess(final Map<String, Bitmap> bitmaps) {
                    if (nativeResponse.getMainImageUrl() != null) {
                        Bitmap bitMap = bitmaps.get(nativeResponse.getMainImageUrl());
                        if (bitMap != null) {
                            adView.setVisibility(View.VISIBLE);
                            adImageView.setImageBitmap(bitMap);
                            adImageView.setVisibility(View.VISIBLE);
                            nativeResponse.recordImpression(adImageView);
                        }
                    }
                }

                @Override
                public void onFail() {
                    adView.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onNativeFail(NativeErrorCode nativeErrorCode) {
            Log.e(TAG, "有道广告加载失败onNativeFail:  " + nativeErrorCode.name());
            adView.setVisibility(View.GONE);
        }
    };

    public void releaseYoudao() {
        if (youdaoNative != null) {
            youdaoNative.destroy();
        }
    }

}
