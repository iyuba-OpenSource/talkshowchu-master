package com.iyuba.talkshow.lil.help_fix.ui.ad.util.upload;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_click_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_clock_submit;
import com.iyuba.talkshow.lil.help_fix.model.remote.manager.AdRemoteManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.upload.submit.AdLocalMarkBean;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.upload.submit.AdSubmitBean;
import com.iyuba.talkshow.lil.help_mvp.util.gson.GsonUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.youdao.sdk.common.OAIDHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AdUploadManager {
    private static final String TAG = "AdUploadManager";

    private static AdUploadManager instance;

    public static AdUploadManager getInstance(){
        if (instance==null){
            synchronized (AdUploadManager.class){
                if (instance==null){
                    instance = new AdUploadManager();
                }
            }
        }
        return instance;
    }

    /**********************************************点击广告获取奖励*************************************/
    //点击广告
    private Disposable clickAdForRewardDis;

    /**
     * 点击广告上传
     * @param showType {@link AdShowUtil.NetParam 的AdShowPosition}
     * @param adType {@link AdShowUtil.NetParam 的AdType}
     * @param onAdClickCallBackListener
     */
    public void clickAdForReward(String showType, String adType, OnAdClickCallBackListener onAdClickCallBackListener){
        RxUtil.unDisposable(clickAdForRewardDis);

        //如果没有登录，则不调用
        if (!UserInfoManager.getInstance().isLogin()){
            return;
        }

        //当前web类型不能获取奖励
        if (adType.equals(AdShowUtil.NetParam.AdType.show_web)){
            return;
        }

        //展示类型
        int showPositionType = AdShowUtil.Util.getNetAdPosition(showType);
        //广告类型
        int adShowType = AdShowUtil.Util.getNetAdType(adType);

        //判断哪些内容需要上传数据
        if (showPositionType<0 || adShowType <0){
            return;
        }

        AdRemoteManager.getAdClickReward(UserInfoManager.getInstance().getUserId(), showPositionType,adShowType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Ad_click_result>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        clickAdForRewardDis = d;
                    }

                    @Override
                    public void onNext(Ad_click_result bean) {
                        if (onAdClickCallBackListener!=null){
                            if (bean!=null){
                                if (bean.getResult()==200){
                                    onAdClickCallBackListener.showClickAdResult(true,bean.getMessage());
                                }else {
                                    onAdClickCallBackListener.showClickAdResult(false,bean.getMessage());
                                }
                            }else {
                                onAdClickCallBackListener.showClickAdResult(false,"点击广告结果失败");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (onAdClickCallBackListener!=null){
                            onAdClickCallBackListener.showClickAdResult(false,"点击广告结果异常("+e.getMessage()+")");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //点击广告接口
    public interface OnAdClickCallBackListener{
        void showClickAdResult(boolean isSuccess,String showMsg);
    }

    /********************************************提交广告数据****************************************/
    //提交广告信息
    private Disposable submitAdMsgDis;
    public void submitAdMsgData(String packageName, List<AdLocalMarkBean> localAdList, OnAdSubmitCallbackListener onAdSubmitCallbackListener){
        RxUtil.unDisposable(submitAdMsgDis);

        //如果没有登录，则不调用
        if (!UserInfoManager.getInstance().isLogin()){
            return;
        }

        //判断数据是否存在
        if (localAdList==null || localAdList.size()==0){
            return;
        }

        //数据处理(web类型的广告不能被提交)
        List<AdLocalMarkBean> tempAdList = new ArrayList<>();
        for (int i = 0; i < localAdList.size(); i++) {
            AdLocalMarkBean markBean = localAdList.get(i);
            if (markBean.getAdType().equals(AdShowUtil.NetParam.AdType.show_web)){
                continue;
            }
            tempAdList.add(markBean);
        }

        if (tempAdList.size()==0){
            return;
        }

        //设备类型
        String device = Build.BRAND;
        //设备id
        TelephonyManager manager = (TelephonyManager) TalkShowApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                deviceId = manager.getMeid();
            } else {
                deviceId = manager.getDeviceId();
            }
        }catch (Exception e){
            deviceId = OAIDHelper.getInstance().getOAID();
        }
        //转换提交的数据
        List<AdSubmitBean> submitList = new ArrayList<>();
        for (int i = 0; i < tempAdList.size(); i++) {
            AdLocalMarkBean markBean = tempAdList.get(i);
            //转换数据
            submitList.add(new AdSubmitBean(
                    String.valueOf(AdShowUtil.Util.getNetAdPosition(markBean.getAdPosition())),
                    String.valueOf(markBean.getCreateTime()),
                    "",
                    String.valueOf(AdShowUtil.Util.getNetAdType(markBean.getAdType())),
                    String.valueOf(AdShowUtil.Util.getNetAdOperation(markBean.getAdOperation()))
            ));
        }
        String submitAdData = GsonUtil.toJson(submitList);

        //请求数据
        AdRemoteManager.submitAdData(UserInfoManager.getInstance().getUserId(), device,deviceId,packageName,submitAdData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Ad_clock_submit>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        submitAdMsgDis = d;
                    }

                    @Override
                    public void onNext(Ad_clock_submit result) {
                        if (onAdSubmitCallbackListener!=null){
                            if (result!=null){
                                if (result.getResult()==200){
                                    onAdSubmitCallbackListener.showSubmitAdResult(true,"提交广告结果成功");
                                }else {
                                    onAdSubmitCallbackListener.showSubmitAdResult(false,"提交广告结果失败("+result.getMessage()+")");
                                }
                            }else {
                                onAdSubmitCallbackListener.showSubmitAdResult(false,"提交广告结果失败");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (onAdSubmitCallbackListener!=null){
                            onAdSubmitCallbackListener.showSubmitAdResult(false,"提交广告结果异常("+e.getMessage()+")");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //提交广告接口
    public interface OnAdSubmitCallbackListener{
        void showSubmitAdResult(boolean isSuccess,String showMsg);
    }
}
