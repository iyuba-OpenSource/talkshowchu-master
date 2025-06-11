package com.iyuba.talkshow.lil.help_fix.util.ad;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.sdk.data.iyu.IyuNative;
import com.iyuba.sdk.data.ydsdk.YDSDKTemplateNative;
import com.iyuba.sdk.data.youdao.YDNative;
import com.iyuba.sdk.mixnative.MixAdRenderer;
import com.iyuba.sdk.mixnative.MixNative;
import com.iyuba.sdk.mixnative.MixViewBinder;
import com.iyuba.sdk.mixnative.PositionLoadWay;
import com.iyuba.sdk.mixnative.StreamType;
import com.iyuba.sdk.nativeads.NativeAdLoadedListener;
import com.iyuba.sdk.nativeads.NativeAdPositioning;
import com.iyuba.sdk.nativeads.NativeAdSelectListener;
import com.iyuba.sdk.nativeads.NativeErrorCode;
import com.iyuba.sdk.nativeads.NativeEventListener;
import com.iyuba.sdk.nativeads.NativeRecyclerAdapter;
import com.iyuba.sdk.nativeads.NativeResponse;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.AdTestKeyData;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.youdao.sdk.nativeads.RequestParameters;

import java.util.EnumSet;
import java.util.HashMap;

/**
 * 使用廖洪雷的sdk进行处理
 */
public class AdNewStreamUtil {

    //展示模版广告
    public static void showStreamAd(Context context, RecyclerView recyclerView,RecyclerView.Adapter listAdapter,NativeRecyclerAdapter adAdapter,String ydTemplateKey,String sdkTemplateKey){
        EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
                RequestParameters.NativeAdAsset.TITLE,
                RequestParameters.NativeAdAsset.TEXT,
                RequestParameters.NativeAdAsset.ICON_IMAGE,
                RequestParameters.NativeAdAsset.MAIN_IMAGE,
                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT
        );
        RequestParameters requestParameters = new RequestParameters.RequestParametersBuilder()
                .location(null)
                .keywords(null)
                .desiredAssets(desiredAssets)
                .build();

        //混合有道和穿山甲的sdk
        YDNative ydNative = new YDNative(context, ydTemplateKey,requestParameters);
        YDSDKTemplateNative ydsdkTemplateNative = new YDSDKTemplateNative(context, sdkTemplateKey);
        IyuNative iyuNative = new IyuNative(context, String.valueOf(App.APP_ID));

        HashMap<Integer,YDSDKTemplateNative> templateMap = new HashMap<>();
        templateMap.put(StreamType.TT,ydsdkTemplateNative);
        MixNative mixNative = new MixNative(ydNative,iyuNative,templateMap);
        PositionLoadWay loadWay = new PositionLoadWay();
        mixNative.setLoadWay(loadWay);

        //设置开始间隔和每个的分隔
        int startPosition = 3;
        int positionInterval = 5;
        //设置到适配器上
        NativeAdPositioning.ClientPositioning positioning = new NativeAdPositioning.ClientPositioning();
        positioning.addFixedPosition(startPosition);
        positioning.enableRepeatingPositions(positionInterval);
        adAdapter = new NativeRecyclerAdapter(context,listAdapter,positioning);
        adAdapter.setAdSource(mixNative);
        //设置显示样式
        MixViewBinder mixViewBinder = new MixViewBinder.Builder(R.layout.item_ad_mix)
                .templateContainerId(R.id.template_container)
                .nativeContainerId(R.id.ad_whole_body)
                .nativeImageId(R.id.native_main_image)
                .nativeTitleId(R.id.native_title)
                .build();
        MixAdRenderer mixAdRenderer = new MixAdRenderer(mixViewBinder);
        adAdapter.registerAdRenderer(mixAdRenderer);
        recyclerView.setAdapter(adAdapter);

        loadWay.setStreamSource(new int[]{StreamType.YOUDAO,StreamType.YOUDAO,StreamType.YOUDAO});
        adAdapter.loadAds();
    }
}
