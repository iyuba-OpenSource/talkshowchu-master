package com.iyuba.talkshow.lil.help_fix.util.ad;

import android.util.Log;
import android.util.Pair;

import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.talkshow.data.ad.ADUtil;
import com.iyuba.talkshow.data.model.AdNativeResponse;
import com.iyuba.talkshow.data.model.RecyclerItem;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_stream_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.manager.AdRemoteManager;
import com.iyuba.talkshow.lil.help_fix.util.ad.bean.AdStreamWebBean;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.util.AdInfoFlowUtil;
import com.youdao.sdk.nativeads.NativeResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title: 广告-信息流
 * @date: 2023/9/13 11:07
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 * 这里这样处理：因为接口会返回三个数据，因此需要每个数据单独处理
 * 上个广告数据处理完成后即发送到adapter中，等到三个都存在后，则根据随机位置进行界面的处理显示
 */
public class AdStreamUtil {

    private static AdStreamUtil instance;
    //广告类型接口
    private Disposable adTypeDis;
    //web广告接口
    private Disposable adWebDis;

    //广告的排序数据
    private String[] adSortArray;
    //查询出的广告数据
    private List<RecyclerItem> adList = new ArrayList<>();

    //有道广告
    private AdInfoFlowUtil adInfoFlowUtil;

    //列表中显示的数量
    private int showListCount = 0;

    public static AdStreamUtil getInstance(){
        if (instance==null){
            synchronized (AdStreamUtil.class){
                if (instance==null){
                    instance = new AdStreamUtil();
                }
            }
        }
        return instance;
    }

    //加载广告
    public void loadAd(){
        if (!AdBlocker.getInstance().shouldBlockAd()&&!UserInfoManager.getInstance().isVip()){

            //取消操作
            onDestroy();

            RxUtil.unDisposable(adTypeDis);
            AdRemoteManager.getStreamAd(UserInfoManager.getInstance().getUserId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Ad_stream_result>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            adTypeDis = d;
                        }

                        @Override
                        public void onNext(List<Ad_stream_result> list) {
                            if (list!=null&&list.size()>0){
                                //取出第一个数据显示
                                Ad_stream_result result = list.get(0);
                                if (result!=null&&result.getData()!=null){
                                    //合并广告排序数据
                                    adSortArray = new String[]{result.getData().getFirstLevel(),result.getData().getSecondLevel(),result.getData().getThirdLevel()};
                                    Log.d("信息流广告", "信息流广告数据--"+adSortArray[0]+"--"+adSortArray[1]+"--"+adSortArray[2]);
                                    //顺序加载
                                    loadAllAd();
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("信息流广告", "广告加载失败");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    //销毁广告
    public void onDestroy(){
        if (adSortArray!=null){
            adSortArray = null;
        }
        adList.clear();
        RxUtil.unDisposable(adTypeDis);
        RxUtil.unDisposable(adWebDis);
        if (adInfoFlowUtil!=null){
            adInfoFlowUtil.destroy();
        }
    }

    /*******************有道广告********************/
    //加载有道的广告
    private void loadYoudaoAd(){
        if (adInfoFlowUtil!=null){
            adInfoFlowUtil.destroy();
        }
        adInfoFlowUtil = new AdInfoFlowUtil(ResUtil.getInstance().getContext(), UserInfoManager.getInstance().isVip(), new AdInfoFlowUtil.Callback() {
            @Override
            public void onADLoad(List ads) {
                try {
                    AdNativeResponse response = new AdNativeResponse();
                    response.setNativeResponse((NativeResponse) ads.get(0));

                    //保存数据
                    adList.add(response);
                    //继续加载
                    loadAllAd();
                }catch (Exception e){
                    adList.add(null);
                    //继续加载
                    loadAllAd();
                }
            }
        }).setAdRequestSize(1);
        //请求数据
        adInfoFlowUtil.refreshAd();
    }

    /********************网页广告*********************/
    //加载网页广告数据
    private void loadWebAd(){
        RxUtil.unDisposable(adWebDis);
        AdRemoteManager.getStreamWebAd(UserInfoManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Ad_result>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        adWebDis = d;
                    }

                    @Override
                    public void onNext(List<Ad_result> list) {
                        if (list!=null&&list.size()>0){
                            Ad_result result = list.get(0);
                            if (result!=null&&result.getResult().equals("1")){
                                Ad_result.DataBean dataBean = result.getData();
                                AdStreamWebBean webBean = new AdStreamWebBean(dataBean.getTitle(), ADUtil.AdUrl.fixPicUrl(dataBean.getStartuppic()),ADUtil.AdUrl.fixJumpUrl(dataBean.getStartuppic_Url()));
                                adList.add(webBean);

                                //继续加载
                                loadAllAd();
                            }else {
                                loadYoudaoAd();
                            }
                        }else {
                            loadYoudaoAd();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadYoudaoAd();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**********************辅助功能*******************/
    //最小广告显示的阈值
    private static final int MIN_AD_SHOW_LIMIT = 5;

    //获取随机位置
    //操作如下：
    //1.需要最少三个数据才能有一个广告，将所有数据分成三组
    //2.第一组的广告放在第一组倒数第二个
    //3.第二、三组放在第二、三组的中间位置
    private int[] getRandomPosition(int dataCount){
        int adShowCount = 0;

        if (dataCount>=3*MIN_AD_SHOW_LIMIT){
            adShowCount = 3;
        }else if (dataCount>=2*MIN_AD_SHOW_LIMIT){
            adShowCount = 2;
        }else if (dataCount>=MIN_AD_SHOW_LIMIT){
            adShowCount = 1;
        }

        //如果不需要显示广告，则直接为空即可
        if (adShowCount==0){
            return null;
        }

        //处理广告所在的位置
        switch (adShowCount){
            case 1://总数据在5-10之间
                //一个广告，放在数据中间
                int showAdIndex = dataCount/2;
                return new int[]{showAdIndex};
            case 2://总数据在10-15之间
                //两个广告，数据分为两组，一个根据随机数判断倒数第一还是倒数第二显示，一个放在第二组随机的位置
                int groupDataCount = dataCount/2;

                //第一组数据
                int firstIndex = groupDataCount-1;
                //随机数
                int firstRandom = (int) (Math.random()*groupDataCount);
                if (firstRandom>=groupDataCount/2){
                    firstIndex--;
                }

                //剩余的数据
                int lastData = dataCount-groupDataCount;
                //第二组数据
                int prefixSecond = ((int) (Math.random()*lastData));
                if (prefixSecond==0){
                    prefixSecond++;
                }
                if (prefixSecond==(lastData-1)){
                    prefixSecond--;
                }
                int secondIndex = prefixSecond+groupDataCount;
                return new int[]{firstIndex,secondIndex};
            case 3://总数据在15以上
                //三个广告，数据分成三组，一个根据随机数判断倒数第一还是倒数第二显示，一个放在第二组随机的位置，一个放在第三组随机的位置
                int groupNewDataCount = dataCount/3;

                //第一组数据
                int firstNewIndex = groupNewDataCount-1;
                //随机数
                int firstRandomNew = (int) (Math.random()*groupNewDataCount);
                if (firstRandomNew>=groupNewDataCount/2){
                    firstNewIndex--;
                }

                //第二组数据
                int prefixSecondNew = ((int) (Math.random()*groupNewDataCount));
                if (prefixSecondNew==0||prefixSecondNew==1){
                    prefixSecondNew++;
                }
                if (prefixSecondNew==(groupNewDataCount-1)){
                    prefixSecondNew--;
                }
                int secondNewIndex = prefixSecondNew+groupNewDataCount;

                //剩余数据
                int lastDataNew = dataCount - groupNewDataCount*2;
                //第三组数据
                int prefixThirdNew = ((int)(Math.random()*lastDataNew));
                if (prefixThirdNew==0||prefixThirdNew==1){
                    prefixThirdNew++;
                }
                if (prefixThirdNew==(lastDataNew-1)){
                    prefixThirdNew--;
                }

                int thirdNewIndex = prefixThirdNew+groupNewDataCount*2;
                return new int[]{firstNewIndex,secondNewIndex,thirdNewIndex};
        }

        return null;
    }

    //统一广告数据加载
    private void loadAllAd(){
        int adSize = adList.size();
        if (adSize>2){
            //这里三个广告加载完成，和随机数据合并进行回调
            int[] adCount = getRandomPosition(showListCount);
            if (adCount!=null&&adCount.length>0){
                List<Pair<Integer,RecyclerItem>> showList = new ArrayList<>();
                for (int i = 0; i < adCount.length; i++) {
                    //这里因为部分数据存在问题，需要判断空值
                    RecyclerItem recyclerItem = adList.get(i);
                    if (recyclerItem!=null){
                        showList.add(new Pair<>(adCount[i],adList.get(i)));
                    }
                }

                Log.d("信息流广告", "信息流广告展示--"+showList.toString());


                // TODO: 2023/9/14 这里根据视频模块的逻辑进行优化处理，每隔4个数据进行显示，上面的随机逻辑不再处理
                if (onAdStreamListener!=null){
                    onAdStreamListener.onShow(showList);
                }
            }
        }else {
            if (adSortArray!=null&&adSortArray.length>adSize){
                String adType = adSortArray[adSize];
                if (adType.equals("1")){
                    loadWebAd();
                }else {
                    loadYoudaoAd();
                }
            }
        }
    }

    //设置回调
    public AdStreamUtil setCallBack(OnAdStreamListener onAdStreamListener){
        this.onAdStreamListener = onAdStreamListener;
        return this;
    }

    //设置列表中的数量
    public AdStreamUtil setListCount(int dataCount){
        this.showListCount = dataCount;
        return this;
    }

    //加载完成回调接口
    private OnAdStreamListener onAdStreamListener;

    public interface OnAdStreamListener{
        void onShow(List<Pair<Integer,RecyclerItem>> list);
    }
}
