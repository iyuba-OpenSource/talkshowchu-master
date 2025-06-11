package com.iyuba.talkshow.lil.help_fix.ui.payOrder;

import androidx.appcompat.app.AppCompatActivity;

import com.alipay.sdk.app.PayTask;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Pay_alipay;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Pay_wx;
import com.iyuba.talkshow.lil.help_mvp.mvp.BasePresenter;
import com.iyuba.talkshow.lil.help_mvp.util.EncodeUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.ui.vip.payorder.PayOrderPresenter;
import com.iyuba.talkshow.ui.vip.payorder.alipay.PayResult;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PayOrderNewPresenter extends BasePresenter<PayOrderNewView> {

    //获取支付链接
    private Disposable payDis;

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unDisposable(payDis);
    }

    //获取支付宝的支付链接
    public void getAliPayOrderLink(int amount,int productId,String subject,String body,String price){
        checkViewAttach();
        RxUtil.unDisposable(payDis);
        CommonDataManager.getAliPayOrder(amount,productId,subject,body,price)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pay_alipay>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        payDis = d;
                    }

                    @Override
                    public void onNext(Pay_alipay pay_alipay) {
                        showAliPay(pay_alipay.getAlipayTradeStr());
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showPayLinkStatus(true,"获取支付订单异常，请重试～");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取微信的支付链接
    public void getWXPayOrderLink(int amount,int productId,String subject,String body,String price){
        checkViewAttach();
        RxUtil.unDisposable(payDis);

        CommonDataManager.getWxPayOrder(amount, productId, subject, body, price)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pay_wx>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        payDis = d;
                    }

                    @Override
                    public void onNext(Pay_wx pay_wx) {
                        if (pay_wx!=null){
                            showWxPay(pay_wx.getMch_id(),pay_wx.getPrepayid(),pay_wx.getNoncestr(),pay_wx.getTimestamp(),pay_wx.getMch_key());
                        }else {
                            getMvpView().showPayLinkStatus(true,"获取支付订单失败，请重试～");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showPayLinkStatus(true,"获取支付订单异常，请重试～");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //调用支付宝支付
    private void showAliPay(String payOrderLink){
        checkViewAttach();

        Observable.create(new ObservableOnSubscribe<PayResult>() {
            @Override
            public void subscribe(ObservableEmitter<PayResult> emitter) throws Exception {
                PayTask payTask = new PayTask((PayOrderNewActivity)getMvpView());
                Map<String, String> result = payTask.payV2(payOrderLink, true);
                PayResult payResult = new PayResult(result);
                emitter.onNext(payResult);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PayResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PayResult payResult) {
                        //判断标识显示
                        if (payResult==null){
                            getMvpView().showPayFinishStatus(true,"未知错误，请重试～");
                            return;
                        }

                        if (payResult.getResultStatus().equals(AliPayResult.Code.SUCCESS)){
                            getMvpView().showPayFinishStatus(true,null);
                            return;
                        }

                        switch (payResult.getResultStatus()){
                            case AliPayResult.Code.CANCELED:
                                getMvpView().showPayFinishStatus(true,AliPayResult.Message.CANCELED);
                                break;
                            case AliPayResult.Code.IN_CONFIRMATION:
                                getMvpView().showPayFinishStatus(true,AliPayResult.Message.IN_CONFIRMATION);
                                break;
                            case AliPayResult.Code.NET_ERROR:
                                getMvpView().showPayFinishStatus(true,AliPayResult.Message.NET_ERROR);
                                break;
                            default:
                                //未知错误，直接刷新数据
                                getMvpView().showPayFinishStatus(true,null);
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showPayFinishStatus(false,"订单支付异常，请重试～");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //调用微信支付
    private void showWxPay(String partnerId,String prepayId,String nonceStr,String timeStamp,String mchkey){
        PayReq payReq = new PayReq();
        payReq.appId = ConfigData.wx_key;
        payReq.partnerId = partnerId;
        payReq.prepayId = prepayId;
        payReq.nonceStr = nonceStr;
        payReq.timeStamp = timeStamp;
        payReq.packageValue = "Sign=WXPay";

        //合成签名
        StringBuffer buffer = new StringBuffer();
        buffer.append("appid=").append(payReq.appId);
        buffer.append("&noncestr=").append(payReq.nonceStr);
        buffer.append("&package=").append(payReq.packageValue);
        buffer.append("&partnerid=").append(payReq.partnerId);
        buffer.append("&prepayid=").append(payReq.prepayId);
        buffer.append("&timestamp=").append(payReq.timeStamp);
        buffer.append("&key=").append(mchkey);
        payReq.sign = EncodeUtil.md5(buffer.toString()).toUpperCase();

        IWXAPI iwxapi = WXAPIFactory.createWXAPI((AppCompatActivity)getMvpView(),null);
        iwxapi.registerApp(ConfigData.wx_key);

        iwxapi.sendReq(payReq);
    }

    //调用网页支付


    //支付宝状态
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
