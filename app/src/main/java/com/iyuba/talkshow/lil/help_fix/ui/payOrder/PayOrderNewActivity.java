package com.iyuba.talkshow.lil.help_fix.ui.payOrder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MenuItem;
import com.iyuba.talkshow.BuildConfig;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.iyuba.imooclib.IMooc;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.databinding.ActivityPayOrderBinding;
import com.iyuba.talkshow.event.VIpChangeEvent;
import com.iyuba.talkshow.event.WXPayResultEvent;
import com.iyuba.talkshow.lil.help_fix.view.dialog.LoadingDialog;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingActivity;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxTimer;
import com.iyuba.talkshow.lil.help_mvp.view.NoScrollLinearLayoutManager;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.talkshow.util.ToastUtil;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 新的支付界面--没有被使用
 */
public class PayOrderNewActivity extends BaseViewBindingActivity<ActivityPayOrderBinding> implements PayOrderNewView {

    //相关参数
    private static final String DESCRIPTION = "description";
    private static final String AMOUNT = "amount";
    private static final String PRICE = "price";
    private static final String SUBJECT = "subject";
    private static final String BODY = "body";
    private static final String OTN = "out_trade_no";
    private static final String PRODUCT_ID = "product_id";

    //相关内容
    private int amount;//爱语币、月份
    private int productId;//类型id、课程id
    private String price;//价格
    private String description;//类型
    private String subject;//类型
    private String body;//详细描述

    //支付类型
    private static final String pay_aliPay = "pay_aliPay";
    private static final String pay_wxPay = "pay_wxPay";

    private PayOrderNewAdapter newAdapter;
    private PayOrderNewPresenter newPresenter;

    public static void start(Context context,int amount, int productId, String price, String subject, String description, String body) {
        Intent intent = new Intent();
        intent.setClass(context, PayOrderNewActivity.class);
        intent.putExtra(AMOUNT, amount);
        intent.putExtra(PRODUCT_ID, productId);
        intent.putExtra(PRICE, price);
        intent.putExtra(SUBJECT, subject);
        intent.putExtra(DESCRIPTION, description);
        intent.putExtra(BODY, body);
        context.startActivity(intent);
    }

    public static Intent buildIntent(Context context,int amount, int productId, String price, String subject, String description, String body) {
        Intent intent = new Intent();
        intent.setClass(context, PayOrderNewActivity.class);
        intent.putExtra(AMOUNT, amount);
        intent.putExtra(PRODUCT_ID, productId);
        intent.putExtra(PRICE, price);
        intent.putExtra(SUBJECT, subject);
        intent.putExtra(DESCRIPTION, description);
        intent.putExtra(BODY, body);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        //设置数据
        amount = getIntent().getIntExtra(AMOUNT, 0);
        productId = getIntent().getIntExtra(PRODUCT_ID, 0);
        price = getIntent().getStringExtra(PRICE);
        if (BuildConfig.DEBUG) {
            price = "0.01";
        }
        subject = getIntent().getStringExtra(SUBJECT);
        description = getIntent().getStringExtra(DESCRIPTION);
        body = getIntent().getStringExtra(BODY);

        newPresenter = new PayOrderNewPresenter();
        newPresenter.attachView(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initList();
        initData();
        initClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        newPresenter.detachView();
    }

    /*************************初始化*************************/
    private void initToolbar() {
        setSupportActionBar(binding.payOrderToolbar.listToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initList() {
        newAdapter = new PayOrderNewAdapter(this, new ArrayList<>());
        binding.payOrderMethodsLv.setLayoutManager(new NoScrollLinearLayoutManager(this, false));
        binding.payOrderMethodsLv.setAdapter(newAdapter);
    }

    private void initData() {
        binding.usernameTv.setText(UserInfoManager.getInstance().getUserName());
        binding.orderValueTv.setText(body);
        binding.amountValueTv.setText(price + "元");

        List<Pair<String, Pair<Integer, Pair<String, String>>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(pay_aliPay, new Pair<>(R.drawable.ic_pay_alipay, new Pair<>("支付宝支付", "推荐有支付宝账号的用户使用"))));

        //判断微信是否存在
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(this,null);
        iwxapi.registerApp(ConfigData.wx_key);
        if (iwxapi.isWXAppInstalled()){
            pairList.add(new Pair<>(pay_wxPay, new Pair<>(R.drawable.ic_pay_wxpay, new Pair<>("微信支付", "推荐安装微信5.0及以上版本的使用"))));
        }
        newAdapter.refreshData(pairList);
    }

    private void initClick() {
        binding.submitBtn.setOnClickListener(v -> {
            //获取当前类型，进行操作
            String payMethod = newAdapter.getPayMethod();
            showPay(payMethod);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    /***************************操作************************/
    //进行支付操作
    private void showPay(String payMethod) {
        switch (payMethod) {
            case pay_aliPay:
                //支付宝支付
                startLoading("正在进行支付～");
                newPresenter.getAliPayOrderLink(amount, productId, subject, body, price);
                break;
            case pay_wxPay:
                //微信支付
                startLoading("正在进行支付～");
                newPresenter.getWXPayOrderLink(amount, productId, subject, body, price);
                break;
            default:
                ToastUtil.showToast(this, "暂无当前支付方式");
                break;
        }
    }

    /*****************************回调************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWxPayResult(WXPayResultEvent event) {
        stopLoading();

        switch (event.getResultCode()){
            case -2:
                //取消支付
                showPayStatusDialog(true,"取消支付");
                break;
            case 0:
                //支付成功
                showPayUnknownDialog();
                break;
            default:
                //未知错误，直接刷新数据
                showPayUnknownDialog();
                break;
        }
    }

    @Override
    public void showPayLinkStatus(boolean isError, String showMsg) {
        stopLoading();

        showPayLinkStatusDialog(showMsg);
    }

    @Override
    public void showPayFinishStatus(boolean isFinish, String payStatus) {
        stopLoading();

        showPayStatusDialog(isFinish,payStatus);
    }

    /***************************其他功能***********************/
    //加载弹窗
    private LoadingDialog loadingDialog;

    private void startLoading(String showMsg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
            loadingDialog.create();
        }
        loadingDialog.setMsg(showMsg);
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    private void stopLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    //支付链接状态弹窗
    private void showPayLinkStatusDialog(String statusMsg) {
        new AlertDialog.Builder(this)
                .setMessage(statusMsg)
                .setPositiveButton("确定", null)
                .create().show();
    }

    //支付状态弹窗
    private void showPayStatusDialog(boolean isFinish, String showMsg) {
        if (!isFinish) {
            showPayLinkStatusDialog(showMsg);
            return;
        }

        if (!TextUtils.isEmpty(showMsg)){
            showPayFailDialog(showMsg);
            return;
        }

        showPayUnknownDialog();
    }

    //支付失败状态弹窗
    private void showPayFailDialog(String showMsg) {
        new AlertDialog.Builder(this)
                .setMessage(showMsg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false)
                .create().show();
    }

    //支付中间状态弹窗
    private void showPayUnknownDialog() {
        new AlertDialog.Builder(this)
                .setMessage("是否支付完成\n\n(如会员、课程未生效，请退出后重新登录)")
                .setPositiveButton("已完成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getUserInfo();
                    }
                }).setNegativeButton("未完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getUserInfo();
            }
        }).setCancelable(false)
                .create().show();
    }

    //获取用户信息
    private void getUserInfo(){
        startLoading("正在更新用户信息～");

        //这里延迟1s后刷新用户信息，便于服务端合并数据
        RxTimer.getInstance().timerInMain("delayTime", 1000L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                RxTimer.getInstance().cancelTimer("delayTime");
                UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), new UserinfoCallbackListener() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopLoading();
                                //回调信息
                                EventBus.getDefault().post(new VIpChangeEvent());
                                //刷新微课信息
                                if (subject.equals("微课直购")){
                                    IMooc.notifyCoursePurchased();
                                }

                                finish();
                            }
                        });
                    }

                    @Override
                    public void onFail(String errorMsg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopLoading();
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }
}
