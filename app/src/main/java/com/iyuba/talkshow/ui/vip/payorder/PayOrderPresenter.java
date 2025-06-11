package com.iyuba.talkshow.ui.vip.payorder;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.iyuba.imooclib.IMooc;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.result.GetAliPayResponse;
import com.iyuba.talkshow.data.model.result.GetWXPayInfoResponse;
import com.iyuba.talkshow.data.model.result.NotifyAliPayResponse;
import com.iyuba.talkshow.event.VIpChangeEvent;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.ui.vip.payorder.alipay.PayResult;
import com.iyuba.talkshow.util.MD5;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ConfigPersistent
public class PayOrderPresenter extends BasePresenter<PayOrderMvpView> {

    private final DataManager mDataManager;

    private Subscription mAliPaySub;
    private Subscription mAliNotifySub;
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x111:
                    getMvpView().dismissWaitingDialog();
                    handlePayStatus((Map<String, String>) msg.obj);
                    break;
                case 0x112:
                    getMvpView().dismissWaitingDialog();
                    getMvpView().showOrderUnusualDialog();
                    break;
                case 0x113:
                    getMvpView().dismissWaitingDialog();
                    if(!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                        getMvpView().showToast(R.string.please_check_network);
                    } else {
                        getMvpView().showToast(R.string.request_fail);
                    }
                    break;
            }

        }
    };
    private IWXAPI mWXAPI;
    private final String  mWeiXinKey = ConfigData.wx_key;

    @Inject
    public PayOrderPresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mAliPaySub);
        RxUtil.unsubscribe(mAliNotifySub);
    }

    public String getWebPayUrl(int amount) {
        return Constant.Url.getWebPayUrl(UserInfoManager.getInstance().getUserId(), amount);
    }

    public void handlePayStatus(Map<String, String> result) {
        PayResult payResult = new PayResult(result);
        String status = payResult.getResultStatus();
        Log.e("PayOrderPresenter", "handlePayStatus status " + status);
        if (getMvpView() == null) {
            Log.e("PayOrderPresenter", "handlePayStatus getMvpView is null.");
            return;
        }
        /*switch (status) {
            case AliPayResult.Code.SUCCESS:
                getMvpView().showToast(AliPayResult.Message.SUCCESS);
                getMvpView().finishActivity();
                break;
            case AliPayResult.Code.IN_CONFIRMATION:
                getMvpView().showToast(AliPayResult.Message.IN_CONFIRMATION);
                break;
            case AliPayResult.Code.CANCELED:
                getMvpView().showToast(AliPayResult.Message.CANCELED);
                break;
            case AliPayResult.Code.NET_ERROR:
                getMvpView().showToast(AliPayResult.Message.NET_ERROR);
                break;
            default:
                getMvpView().showToast(AliPayResult.Message.FAILURE);
                break;
        }*/

        getMvpView().showAliPayStatus(status);
    }

    public void aliPayNew(int amount, int productId, String subject, String price, String body,long deduction) {
        checkViewAttached();
        RxUtil.unsubscribe(mAliPaySub);
        getMvpView().showWaitingDialog();
        Log.e("PayOrderPresenter", "aliPayNew called subject " + subject);
        mAliPaySub = mDataManager.getAliPay(UserInfoManager.getInstance().getUserId(), amount, productId,
                subject, price, body,deduction)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<GetAliPayResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("PayOrderPresenter", "aliPayNew " + e.getMessage());
                        }
                        handler.sendEmptyMessage(0x113);
                    }

                    @Override
                    public void onNext(GetAliPayResponse response) {
                        if (response == null) {
                            Log.e("PayOrderPresenter", "aliPayNew response is null.");
                            handler.sendEmptyMessage(0x112);
                            return;
                        }
                        if ((getMvpView() != null) && (response.result == 200)) {
                            TalkShowApplication.getSubHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    String payInfo = response.alipayTradeStr;
                                    Log.e("PayOrderPresenter", "onNext payInfo " + payInfo);
                                    PayTask alipay = new PayTask((Activity) getMvpView());
                                    Map<String, String> result = alipay.payV2(payInfo, true);
                                    PayResult payResult = new PayResult(result);
                                    if ((payResult != null) && AliPayResult.Code.SUCCESS.equals(payResult.getResultStatus())) {
                                        notifyPay(result.toString());
                                        //如果是微课，则刷新微课显示
                                        /*if (subject.equals("微课直购")){
                                            IMooc.notifyCoursePurchased();
                                        }

                                        //刷新20001接口
                                        UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), new UserinfoCallbackListener() {
                                            @Override
                                            public void onSuccess() {
                                                EventBus.getDefault().post(new VIpChangeEvent());
                                            }

                                            @Override
                                            public void onFail(String errorMsg) {

                                            }
                                        });*/
                                    }
                                    Message message = Message.obtain();
                                    message.what = 0x111;
                                    message.obj = result;
                                    handler.sendMessage(message);
                                }
                            });
                        } else {
                            Log.e("PayOrderPresenter", "aliPayNew response.result " + response.result);
                            handler.sendEmptyMessage(0x112);
                        }
                    }
                });
    }

    private void notifyPay(String payOrder) {
        Log.e("PayOrderPresenter", "notifyPay is called.");
        RxUtil.unsubscribe(mAliNotifySub);
        mAliNotifySub = mDataManager.NotifyAliPay(payOrder)
                .subscribe(new Subscriber<NotifyAliPayResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("PayOrderPresenter", "notifyPay onError " + e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(NotifyAliPayResponse response) {
                        if (response == null) {
                            Log.e("PayOrderPresenter", "notifyPay response is null.");
                        } else {
                            Log.e("PayOrderPresenter", "notifyPay response code " + response.code);
                            Log.e("PayOrderPresenter", "notifyPay response msg " + response.msg);
                        }
                    }
                });
    }

    /*public void aliPay(int amount, int productId, String outTradeNo, String subject,
                       String price, String body) {
        checkViewAttached();
        RxUtil.unsubscribe(mAliPaySub);
        getMvpView().showWaitingDialog();
        mAliPaySub = mDataManager.getAliPayInfo(UserInfoManager.getInstance().getUserId(), amount, productId,
                outTradeNo, subject, price, body)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<GetAliPayInfoResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(0x113);
                    }

                    @Override
                    public void onNext(GetAliPayInfoResponse response) {
                        if (response.result() == VipService.GetAliPayInfo.Result.Code.SUCCESS) {
                            String payInfo = AliPayUtil.getPayInfo(decode(response.orderInfo()), response.sign());
                            Timber.e(payInfo);
//                            payInfo = decode("partner%3D%222088701694845815%22%26seller_id%3D%22iyuba%40sina.com%22%26out_trade_no%3D%22010614080143718%22%26subject%3D%22VIP%22%26body%3D%22%E8%B4%AD%E4%B9%B0%E4%B8%80%E6%9C%88%E5%85%A8%E7%AB%99VIP%22%26total_fee%3D%2219.9%22%26notify_url%3D%22http%3A%2F%2Fvip.iyuba.com%2Fnotify.jsp%22%26service%3D%22mobile.securitypay.pay%22%26payment_type%3D%221%22%26_input_charset%3D%22utf-8%22%26it_b_pay%3D%2230m%22%26return_url%3D%22http%3A%2F%2Fvip.iyuba.com%2Freturn_url.jsp%22&sign=PRnladeZrZ0kMHH%2FhSo6a7zbIZe4U6jwIX6GSI%2FUB5L7beOxZ8Fod7XWU%2BtcRdPqr5ubWnc3p4T1lcXdcgfb%2FIZ8skGt6RZyQWjnoOuP13afl8aAmjO6kBG7rczV%2FAvoF5tcjq1LPiQFdF8q%2B1J03a99p%2FjvLnYXAJ%2BF%2B5maAfQ%3D");
//                            payInfo = "partner=\"2088701694845815\"&seller_id=\"iyuba@sina.com\"&out_trade_no=\"010613475827212\"&subject=\"VIP\"&body=\"购买一月全站VIP\"&total_fee=\"19.9\"&notify_url=\"http://vip."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"notify.jsp\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&return_url=\"http://vip."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"return_url.jsp\"&sign=\"B9INILHQOgt%2FjONGLJjUyt4qafjVHNaJBAYANs%2BLIJgAlxHgVos6CoXF%2FJCIJRblXnjowrRtlLFQEeigQaoZiTwtzZGoZqfKs%2BK1TN26IE78hDKBuc4zDWLubaCI84VAOboTcAOsDmMYQ9%2BDK7LiZYNnuzQo9zE%2BtSmr%2B00alb4%3D\"&sign_type=\"RSA\"";
                            PayTask alipay = new PayTask((Activity) getMvpView());
                            String result = alipay.pay(payInfo, true);
                            PayResult payResult = new PayResult(result);
                            //handlePayStatus(payResult.getResultStatus());
                            Message message = Message.obtain();
                            message.what = 0x111;
                            message.obj = payResult.getResultStatus();
                            handler.sendMessage(message);
                            if ("爱语币".equals(subject)) {
                                mAccountManager.getUser().setAmount(amount);
                                mAccountManager.getSaveUser().setAmount(amount);
                            }
                        } else {
                            handler.sendEmptyMessage(0x112);
                        }
                    }
                });
    }*/

    /*private String decode(String str) {
        String result;
        try {
            result = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            result = ((Context) getMvpView()).getString(R.string.iyubi);
        }
        return result;
    }*/

    /*public void handlePayStatus(String status) {
        switch (status) {
            case AliPayResult.Code.SUCCESS:
                getMvpView().showToast(AliPayResult.Message.SUCCESS);
                getMvpView().finishActivity();
                break;
            case AliPayResult.Code.IN_CONFIRMATION:
                getMvpView().showToast(AliPayResult.Message.IN_CONFIRMATION);
                break;
            case AliPayResult.Code.CANCELED:
                getMvpView().showToast(AliPayResult.Message.CANCELED);
                break;
            case AliPayResult.Code.NET_ERROR:
                getMvpView().showToast(AliPayResult.Message.NET_ERROR);
                break;
            default:
                getMvpView().showToast(AliPayResult.Message.FAILURE);
                break;
        }
    }*/

    public void wxPay(int mAmount, int mProductId, String mOutTradeNo, String mSubject, String mPrice, String mBody,long deduction) {
//        mWXAPI = WXAPIFactory.createWXAPI( true);
        mWXAPI = WXAPIFactory.createWXAPI((Activity)getMvpView(), null);
        mWXAPI.registerApp(mWeiXinKey);

        if (mWXAPI.isWXAppInstalled()) {
            payByWeiXin(mAmount,  mProductId,  mOutTradeNo,  mSubject,  mPrice,  mBody,deduction);
        } else {
            getMvpView().showWeixinNotInstallDialog();
        }
    }

    private void payByWeiXin(int mAmount, int mProductId, String mOutTradeNo, String mSubject,
                             String mPrice, String mBody,long deduction) {
        mAliPaySub = mDataManager.getWechatPayInfo(UserInfoManager.getInstance().getUserId(), mAmount, mProductId,
                mOutTradeNo, mSubject, mPrice, mBody,deduction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetWXPayInfoResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(0x113);
                    }

                    @Override
                    public void onNext(GetWXPayInfoResponse response) {
                        sendWxReq(response.mch_id(),response.prepayid(),response.noncestr(),response.timestamp(),
                                response.mch_key());
                    }
                });
    }



    private void sendWxReq(String partnerId , String prepayId , String nonceStr , String timeStamp ,
                           String mchkey){
        PayReq req = new PayReq();
        req.appId = mWeiXinKey;
        req.partnerId =partnerId;
        req.prepayId = prepayId;
        req.nonceStr = nonceStr;
        req.timeStamp = timeStamp;
        req.packageValue = "Sign=WXPay";
        req.sign = buildWeixinSign(req, mchkey);
        mWXAPI.sendReq(req);
    }

    private String buildWeixinSign(PayReq payReq, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildWeixinStringA(payReq));
        sb.append("&key=").append(key);
        Log.i("sign", sb.toString());
        return MD5.getMD5ofStr(sb.toString()).toUpperCase();
    }

    private String buildWeixinStringA(PayReq payReq) {
        StringBuilder sb = new StringBuilder();
        sb.append("appid=").append(payReq.appId);
        sb.append("&noncestr=").append(payReq.nonceStr);
        sb.append("&package=").append(payReq.packageValue);
        sb.append("&partnerid=").append(payReq.partnerId);
        sb.append("&prepayid=").append(payReq.prepayId);
        sb.append("&timestamp=").append(payReq.timeStamp);
        return sb.toString();
    }



    interface AliPayResult {
        interface Code {
            String SUCCESS = "9000";
            String IN_CONFIRMATION = "8000";
            String CANCELED = "6001";
            String NET_ERROR = "6002";
        }

        interface Message {
            String SUCCESS = "支付成功";
            String IN_CONFIRMATION = "支付结果确认中";
            String CANCELED = "您已取消支付";
            String NET_ERROR = "网络连接出错";
            String FAILURE = "支付失败";
        }
    }

}
