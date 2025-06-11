package com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.template;

import android.app.Activity;
import android.text.TextUtils;
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
import com.iyuba.sdk.nativeads.NativeAdPositioning;
import com.iyuba.sdk.nativeads.NativeEventListener;
import com.iyuba.sdk.nativeads.NativeRecyclerAdapter;
import com.iyuba.sdk.nativeads.NativeResponse;
import com.iyuba.talkshow.BuildConfig;
import com.iyuba.talkshow.constant.AdTestKeyData;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_stream_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.manager.AdRemoteManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.AdLogUtil;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_fix.util.ScreenUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.youdao.sdk.nativeads.RequestParameters;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 广告显示管理类-模版广告
 */
public class AdTemplateShowManager {
    private static final String TAG = AdTemplateShowManager.class.getName();
    private static final String Show_ad_name = "信息流广告";

    private static AdTemplateShowManager instance;

    public static AdTemplateShowManager getInstance(){
        if (instance==null){
            synchronized (AdTemplateShowManager.class){
                if (instance==null){
                    instance = new AdTemplateShowManager();
                }
            }
        }
        return instance;
    }

    //保存数据(分类型处理数据)
    private Map<String, AdTemplateTypeBean> adTemplateMap = new HashMap<>();

    /*****************************************临时数据**********************************************/
    public void setShowData(String key, AdTemplateViewBean viewBean) {
        //这里只添加一次，如果存在重复添加则需要重复删除
        adTemplateMap.put(key,new AdTemplateTypeBean(viewBean,null,null));
    }

    /*****************************************广告接口**********************************************/
    //加载信息流广告
    // TODO: 2024/8/27 李涛在小学英语群里已确认：下拉刷新后重新获取接口进行数据刷新，排序按照接口来
    public void showTemplateAd(String key,Activity context){
        if (!AdShowUtil.Util.isPageExist(context) || adTemplateMap.get(key) == null || adTemplateMap.get(key).getViewBean()==null) {
            return;
        }

        //获取对应的模型
        AdTemplateViewBean viewBean = adTemplateMap.get(key).getViewBean();
        //获取对应的广告适配器
        NativeRecyclerAdapter nativeAdapter = adTemplateMap.get(key).getAdAdapter();

        //显示日志
        AdLogUtil.showDebug(TAG, Show_ad_name + "-准备加载");
        //清除广告
        clearAd(key);
        //请求广告
        if (nativeAdapter!=null){
            AdLogUtil.showDebug(TAG, Show_ad_name + "-刷新广告");

            refreshTemplateAd(context,key,viewBean);
        }else {
            AdLogUtil.showDebug(TAG, Show_ad_name + "-加载完整的广告");

            loadTemplateAd(context,key,viewBean);
        }
    }

    //关闭广告
    public void stopTemplateAd(String key){
        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告销毁");

        //清除广告
        clearAd(key);
        //销毁广告
        destroyTemplateAd(key);
        //关闭网络操作
        if (adTemplateMap.get(key)!=null){
            RxUtil.unDisposable(adTemplateMap.get(key).getAdDisposable());
        }
    }

    //清除广告
    public void clearAd(String key){
        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告清除");

        if (adTemplateMap.get(key)!=null){
            if (adTemplateMap.get(key).getAdAdapter()!=null){
                adTemplateMap.get(key).getAdAdapter().clearAds();
            }
        }
    }

    //销毁广告
    private void destroyTemplateAd(String key){
        if (adTemplateMap.get(key)!=null){
            if (adTemplateMap.get(key).getAdAdapter()!=null){
                adTemplateMap.get(key).getAdAdapter().destroy();
            }
            adTemplateMap.get(key).setAdAdapter(null);
        }
    }

    //刷新广告
    public void refreshTemplateAd(Activity context,String key,AdTemplateViewBean viewBean){
        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告刷新");

//        if (adTemplateMap.get(key).getAdAdapter()!=null){
//            adTemplateMap.get(key).getAdAdapter().refreshAds();
//        }
        //先清除广告
        clearAd(key);
        //再加载广告
        loadTemplateAd(context, key, viewBean);
    }

    //加载广告(因为删除广告会导致错误，这里切换为如下逻辑：会员时，加载错误的广告类型，这样就不显示广告了)
    public void loadTemplateAd(Activity context,String key,AdTemplateViewBean viewBean){
        AdRemoteManager.getTemplateAd(UserInfoManager.getInstance().getUserId(), AdShowUtil.NetParam.Flag.net_templateFlag, UserInfoManager.getInstance().isVip()?-100:AdShowUtil.NetParam.getAdId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Ad_stream_result>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        adTemplateMap.get(key).setAdDisposable(disposable);
                    }

                    @Override
                    public void onNext(List<Ad_stream_result> bean) {
                        if (!AdShowUtil.Util.isPageExist(context)) {
                            return;
                        }

                        if (bean == null || bean.size() == 0 || !bean.get(0).getResult().equals("1")) {
                            return;
                        }

                        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告接口加载完成");

                        //设置加载完成
                        viewBean.getOnAdTemplateShowListener().onLoadFinishAd();

                        //根据类型处理
                        Ad_stream_result.DataBean templateAdData = bean.get(0).getData();
                        //转换类型
                        int firstLevel = TextUtils.isEmpty(templateAdData.getFirstLevel())?StreamType.YOUDAO:Integer.parseInt(templateAdData.getFirstLevel());
                        int secondLevel = TextUtils.isEmpty(templateAdData.getSecondLevel())?StreamType.YOUDAO:Integer.parseInt(templateAdData.getSecondLevel());
                        int thirdLevel = TextUtils.isEmpty(templateAdData.getThirdLevel())?StreamType.YOUDAO:Integer.parseInt(templateAdData.getThirdLevel());
                        //显示日志
                        AdLogUtil.showDebug(TAG, Show_ad_name+"-广告展示界面-"+key);
                        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告展示的类型--"+showAdTypeAndSort(String.valueOf(firstLevel),String.valueOf(secondLevel),String.valueOf(thirdLevel)));

                        //合并需要显示的类型
                        int[] showAdArray = new int[]{firstLevel,secondLevel,thirdLevel};
                        showAd(context,key,showAdArray);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        AdLogUtil.showDebug(TAG, Show_ad_name + "-广告接口加载异常");

                        //vip不进行展示
                        if (UserInfoManager.getInstance().isVip()){
                            return;
                        }

                        //默认加载有道
                        int[] showAdArray = new int[]{StreamType.YOUDAO};
                        showAd(context,key,showAdArray);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /******************************************广告显示**********************************************/
    //显示详细的广告设置
    private void showAd(Activity context,String key,int[] showAdArray){
        //获取需要的控件数据
        AdTemplateViewBean viewBean = adTemplateMap.get(key).getViewBean();

        //设置广告
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

        //设置广告的信息
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        IyuNative iyuNative = new IyuNative(context, String.valueOf(App.APP_ID),client);

        //设置有道广告内容
        String youdaoAdKey = getTemplateAdKey(AdShowUtil.NetParam.AdType.show_youdao);
        YDNative ydNative = new YDNative(context,youdaoAdKey,requestParameters);

        //设置相应的爱语吧sdk广告内容
        //穿山甲
        String csjAdKey = getTemplateAdKey(AdShowUtil.NetParam.AdType.show_ads2);
        YDSDKTemplateNative csjTemplateNative = new YDSDKTemplateNative(context,csjAdKey);
        //优量汇
        String ylhAdKey = getTemplateAdKey(AdShowUtil.NetParam.AdType.show_ads4);
        YDSDKTemplateNative ylhTemplateNative = new YDSDKTemplateNative(context,ylhAdKey);
        //百度
        String baiduAdKey = getTemplateAdKey(AdShowUtil.NetParam.AdType.show_ads3);
        YDSDKTemplateNative baiduTemplateNative = new YDSDKTemplateNative(context,baiduAdKey);
        //快手
        String ksAdKey = getTemplateAdKey(AdShowUtil.NetParam.AdType.show_ads5);
        YDSDKTemplateNative ksTemplateNative = new YDSDKTemplateNative(context,ksAdKey);
        //瑞狮
        String vlionAdKey = getTemplateAdKey(AdShowUtil.NetParam.AdType.show_ads6);
        YDSDKTemplateNative vlionTemplateNative = new YDSDKTemplateNative(context,vlionAdKey);
        //将这些内容放在表中
        HashMap<Integer,YDSDKTemplateNative> ydsdkMap = new HashMap<>();
        ydsdkMap.put(StreamType.TT,csjTemplateNative);
        ydsdkMap.put(StreamType.GDT,ylhTemplateNative);
        ydsdkMap.put(StreamType.BAIDU,baiduTemplateNative);
        ydsdkMap.put(StreamType.KS,ksTemplateNative);
        ydsdkMap.put(StreamType.VLION,vlionTemplateNative);
        MixNative mixNative = new MixNative(ydNative,iyuNative,ydsdkMap);
        PositionLoadWay loadWay = new PositionLoadWay();
        mixNative.setLoadWay(loadWay);

        //增加调试时错误信息的展示位置(正式打包时关闭)
        if (!UserInfoManager.getInstance().isVip()){
            mixNative.setUseFakeStub(BuildConfig.DEBUG);
        }

        //设置广告的宽高(这里好像不好处理，暂时关闭)
        /*int dp8px = ScreenUtil.dip2px(context, 10.0f);
        int dp16px = ScreenUtil.dip2px(context,16.0f);
        int screenWidth = ScreenUtil.getScreenW(context);
        int simpleViewWidth = screenWidth-(2*dp8px);
        int viewWidth = screenWidth-(2*dp16px);
        int adWidth = viewWidth;

        int simplePicWidth = (int) (simpleViewWidth*0.5);
        int simplePicHeight = (simplePicWidth*5)/ 8;
        int picWidth = (int) (viewWidth*0.43);
        int picHeight = (picWidth*1)/3;
        int adPicWidth = picWidth;
        int adHeight = picHeight;*/
        // TODO: 2024/8/26 这里是从视频模块中找到的方法，暂时这样使用，后期处理下吧
        // TODO: 2024/10/24 这里最新的要求上传高度为0，暂时按照高度为0处理
        //先计算宽度(屏幕宽度-两侧的边距)
        int adWidth = ScreenUtil.getScreenW(context);
        //再计算高度(穿山甲文档中显示需要设置高度)
//        int picHeight = (int) (adWidth*0.43);
//        int adHeight = picHeight*7/10;
        int adHeight = ScreenUtil.dip2px(context,100+2*16);//动态高度有点问题，这里使用固定高度132px
        mixNative.setWidthHeight(adWidth,0);

        //设置广告显示
        NativeAdPositioning.ClientPositioning positioning = new NativeAdPositioning.ClientPositioning();
        //设置起始位置
        positioning.addFixedPosition(AdShowUtil.NetParam.SteamAd_startIndex);
        //设置间隔位置
        positioning.enableRepeatingPositions(AdShowUtil.NetParam.SteamAd_intervalIndex);
        //设置适配器
        NativeRecyclerAdapter nativeAdapter = new NativeRecyclerAdapter(context, viewBean.getListAdapter(), positioning);
        nativeAdapter.setNativeEventListener(new NativeEventListener() {
            @Override
            public void onNativeImpression(View view, NativeResponse nativeResponse) {
                //广告展现
                StringBuffer buffer = new StringBuffer();
                buffer.append("标题名称："+nativeResponse.getTitle());
                buffer.append(",图片路径："+nativeResponse.getMainImageUrl());
                AdLogUtil.showDebug(TAG, Show_ad_name + "-广告展示-"+buffer.toString());

                viewBean.getOnAdTemplateShowListener().onAdShow(buffer.toString());
            }

            @Override
            public void onNativeClick(View view, NativeResponse nativeResponse) {
                //点击操作(好像只能有道点击回调，其他的不回调)
                AdLogUtil.showDebug(TAG, Show_ad_name + "-广告点击(尝试获取广告信息)[标题："+nativeResponse.getTitle()+"图片："+nativeResponse.getMainImageUrl()+"]");

                viewBean.getOnAdTemplateShowListener().onAdClick();
            }
        });
        nativeAdapter.setAdSource(mixNative);

        //保存在对应的key中
        adTemplateMap.get(key).setAdAdapter(nativeAdapter);
        //设置广告关闭(仅替换当前广告，不关闭广告位)
        mixNative.setYDSDKTemplateNativeClosedListener(new MixNative.YDSDKTemplateNativeClosedListener() {
            @Override
            public void onClosed(View view) {
                // TODO: 2024/9/2 当前关闭广告后会刷新新的广告，导致审核不通过，这里暂时清除整个页面的广告
//                stopTemplateAd(key);

                View itemView = (View) ((View) view.getParent()).getParent();
                RecyclerView.ViewHolder viewHolder= viewBean.getRecyclerView().getChildViewHolder(itemView);
                int position=viewHolder.getBindingAdapterPosition();
                nativeAdapter.removeAdsWithAdjustedPosition(position);

                AdLogUtil.showDebug(TAG, Show_ad_name + "-广告更换-"+position);
            }
        });

        //设置默认显示样式
        MixViewBinder mixViewBinder = new MixViewBinder.Builder(viewBean.getLayoutId())
                .templateContainerId(viewBean.getTemplateContainerId())
                .nativeContainerId(viewBean.getNativeContainerId())
                .nativeImageId(viewBean.getNativeImageId())
                .nativeTitleId(viewBean.getNativeTitleId())
                .build();
        MixAdRenderer mixAdRenderer = new MixAdRenderer(mixViewBinder);
        nativeAdapter.registerAdRenderer(mixAdRenderer);
        viewBean.getRecyclerView().setAdapter(nativeAdapter);

        //设置需要的广告类型
        loadWay.setStreamSource(showAdArray);
        nativeAdapter.loadAds();
    }

    /*******************************************相关方法********************************************/
    //根据类型获取信息流广告的key
    private String getTemplateAdKey(String adType) {
        if (TextUtils.isEmpty(adType)) {
            return null;
        }

        switch (adType) {
            case AdShowUtil.NetParam.AdType.show_youdao://有道
                return AdTestKeyData.KeyData.TemplateAdKey.template_youdao;
            case AdShowUtil.NetParam.AdType.show_ads2://穿山甲
                return AdTestKeyData.KeyData.TemplateAdKey.template_csj;
            case AdShowUtil.NetParam.AdType.show_ads3://百度
                return AdTestKeyData.KeyData.TemplateAdKey.template_baidu;
            case AdShowUtil.NetParam.AdType.show_ads4://优量汇
                return AdTestKeyData.KeyData.TemplateAdKey.template_ylh;
            case AdShowUtil.NetParam.AdType.show_ads5://快手
                return AdTestKeyData.KeyData.TemplateAdKey.template_ks;
            case AdShowUtil.NetParam.AdType.show_ads6://瑞狮
                return null;
            default:
                return null;
        }
    }

    //展示的广告类型和顺序
    private String showAdTypeAndSort(String firstLevel,String secondLevel,String thirdLevel){
        StringBuffer buffer = new StringBuffer();

        int firstAd = TextUtils.isEmpty(firstLevel)?-1:Integer.parseInt(firstLevel);
        int secondAd = TextUtils.isEmpty(secondLevel)?-1:Integer.parseInt(secondLevel);
        int tiredAd = TextUtils.isEmpty(thirdLevel)?-1:Integer.parseInt(thirdLevel);

        buffer.append(getTemplateAdName(firstAd)+"、");
        buffer.append(getTemplateAdName(secondAd)+"、");
        buffer.append(getTemplateAdName(tiredAd));

        return buffer.toString();
    }

    //根据类型获取广告的名称
    private String getTemplateAdName(int level){
        if (level<0){
            return "未知类型";
        }

        switch (level){
            case StreamType.YOUDAO:
                return "有道广告";
            case StreamType.IYUBA:
                return "iyuba广告";
            case StreamType.TT:
                return "穿山甲(头条)广告";
            case StreamType.GDT:
                return "优量汇(广点通)广告";
            case StreamType.BAIDU:
                return "百度广告";
            case StreamType.KS:
                return "快手广告";
            case StreamType.VLION:
                return "瑞狮广告";
            default:
                return level+"类型的广告";
        }
    }
}