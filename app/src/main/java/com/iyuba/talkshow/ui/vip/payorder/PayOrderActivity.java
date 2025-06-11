package com.iyuba.talkshow.ui.vip.payorder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.imooclib.IMooc;
import com.iyuba.talkshow.BuildConfig;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.databinding.ActivityPayOrderBinding;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.event.WXPayResultEvent;
import com.iyuba.talkshow.lil.help_fix.ui.me_wallet.WalletListActivity;
import com.iyuba.talkshow.lil.help_fix.view.dialog.LoadingDialog;
import com.iyuba.talkshow.lil.help_mvp.util.BigDecimalUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxTimer;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.web.WebActivity;
import com.iyuba.wordtest.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import cn.qqtheme.framework.picker.OptionPicker;


/**
 * 支付界面(正在使用)
 */
public class PayOrderActivity extends BaseActivity implements PayOrderMvpView {

    public static final String DESCRIPTION = "description";
    public static final String AMOUNT = "amount";
    public static final String PRICE = "price";
    public static final String SUBJECT = "subject";
    public static final String BODY = "body";
    public static final String PRODUCT_ID = "product_id";
    public static final String ORDER_TYPE = "order_type";

    //订单类型
    public static final String Order_vip = "vipOrder";
    public static final String Order_iyubi = "iyubiOrder";
    public static final String Order_moc = "mocOrder";

    //详细描述
    private String mDescription;
    //价格
    private String mPrice;
    //类型
    private String mSubject;
    //简要描述
    private String mBody;
    //月数或爱语币数量
    private int mAmount;
    //爱语币或会员的类型
    private int mProductId;
    //抵扣的价格(分为单位)
    private long mDeduction = 0;
    //购买物品的类型
    private String mOrderType;

    @Inject
    PayOrderPresenter mPresenter;
    @Inject
    PayMethodAdapter mAdapter;

    //布局样式
    private ActivityPayOrderBinding binding;

    public static Intent buildIntent(Context context, String description, String price,
                                     String subject, String body, int amount,
                                     int productId,String orderType) {
        Intent intent = new Intent(context, PayOrderActivity.class);
        intent.putExtra(DESCRIPTION, description);
        intent.putExtra(AMOUNT, amount);
        intent.putExtra(PRICE, price);
        intent.putExtra(SUBJECT, subject);
        intent.putExtra(BODY, body);
        intent.putExtra(PRODUCT_ID, productId);
        intent.putExtra(ORDER_TYPE,orderType);
        return intent;
    }

    private void parseIntent(Intent intent) {
        mDescription = intent.getStringExtra(DESCRIPTION);
        mPrice = intent.getStringExtra(PRICE);
        if (BuildConfig.DEBUG) {
            mPrice = "0.01";
        }
        mSubject = intent.getStringExtra(SUBJECT);
        mBody = intent.getStringExtra(BODY);
        mAmount = intent.getIntExtra(AMOUNT, 0);
        mProductId = intent.getIntExtra(PRODUCT_ID, -1);
        mOrderType = intent.getStringExtra(ORDER_TYPE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        binding = ActivityPayOrderBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        activityComponent().inject(this);
        setSupportActionBar(binding.payOrderToolbar.listToolbar);

        mPresenter.attachView(this);
        parseIntent(getIntent());
//        mWXAPI = WXAPIFactory.createWXAPI(this, mWeiXinKey, false);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        binding.usernameTv.setText(UserInfoManager.getInstance().getUserName());
        binding.orderValueTv.setText(mDescription);
        binding.amountValueTv.setText(MessageFormat.format(getString(R.string.price), mPrice));
        binding.payOrderMethodsLv.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.payOrderMethodsLv.setLayoutManager(layoutManager);
        binding.submitBtn.setOnClickListener(v -> clickSubmit());

        //设置会员的服务协议
        binding.vipAgreement.setText(setVipAgreement());
        binding.vipAgreement.setMovementMethod(new LinkMovementMethod());

        //显示抵扣金额
        showDeduction();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);

        dismissWaitingDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.detachView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    //设置会员服务协议的样式
    private SpannableStringBuilder setVipAgreement() {
        String vipStr = "《会员服务协议》";
        String showMsg = "点击支付即代表您已充分阅读并同意" + vipStr;

        SpannableStringBuilder spanStr = new SpannableStringBuilder();
        spanStr.append(showMsg);
        //会员服务协议
        int termIndex = showMsg.indexOf(vipStr);
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(PayOrderActivity.this, WebActivity.class);
                String url = App.Url.VIP_AGREEMENT_URL;
                intent.putExtra("url", url);
                intent.putExtra("title", "会员服务协议");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        }, termIndex, termIndex + vipStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanStr;
    }

    @Override
    public void finishActivity() {
        finish();
    }

    void clickSubmit() {
        switch (mAdapter.getSelectMethod()) {
            // TODO: 2024/3/1 增加抵扣的信息
            case PayMethod.ALIPAY:
                startLoading("正在获取支付信息～");
//                mPresenter.aliPay(mAmount, mProductId, getOutTradeNo(), mSubject, mPrice, mBody);
                mPresenter.aliPayNew(mAmount, mProductId, mSubject, mPrice, mBody, mDeduction);
                break;
            case PayMethod.WEIXIN:
                startLoading("正在获取支付信息～");
                mPresenter.wxPay(mAmount, mProductId, getOutTradeNo(), mSubject, mPrice, mBody, mDeduction);
                break;
            case PayMethod.BANKCARD:
                startLoading("正在获取支付信息～");
                payByWeb(mAmount);
                break;
            default:
                ToastUtil.showToast(this, "暂无当前支付功能");
                break;
        }
    }

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        Random r = new Random();
        key = key + Math.abs(r.nextInt());
        key = key.substring(0, 15);
        return key;
    }

    private void payByWeb(int amount) {
        String url = mPresenter.getWebPayUrl(amount);
        Intent intent = WebActivity.buildIntent(this, url, getString(R.string.order_pay));
        startActivity(intent);
        finish();
    }

    @Override
    public void showToast(String text) {
        stopLoading();
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showOrderUnusualDialog() {
        stopLoading();

        new AlertDialog.Builder(PayOrderActivity.this)
                .setTitle(R.string.order_unusual)
                .setMessage(R.string.order_verify_fail)
                .setPositiveButton(R.string.dialog_action_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                PayOrderActivity.this.finish();
                            }
                        })
                .show();
    }

    @Override
    public void showWeixinNotInstallDialog() {
        stopLoading();

        new AlertDialog.Builder(this)
                .setTitle(R.string.alert_title)
                .setMessage(R.string.weixin_not_install)
                .setPositiveButton(R.string.dialog_action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void showWaitingDialog() {
        startLoading("正在加载中～");
    }

    @Override
    public void dismissWaitingDialog() {
        stopLoading();
    }

    @Override
    public void showToast(int resId) {
        stopLoading();
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    //微信支付回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WXPayResultEvent event) {
        int errCode = event.getResultCode();
        switch (errCode) {
            case -2:
                //取消支付
                showPayFailDialog("取消支付");
                break;
            case 0://支付成功
                showPayUnknownDialog();
                break;
            default://其他情况
                showPayUnknownDialog();
                break;
        }
    }

    //支付宝支付回调
    @Override
    public void showAliPayStatus(String payStatus) {
        stopLoading();

        switch (payStatus) {
            case PayOrderPresenter.AliPayResult.Code.SUCCESS:
                //成功
                showPayUnknownDialog();
                break;
            case PayOrderPresenter.AliPayResult.Code.IN_CONFIRMATION:
                showPayFailDialog(PayOrderPresenter.AliPayResult.Message.IN_CONFIRMATION);
                break;
            case PayOrderPresenter.AliPayResult.Code.CANCELED:
                showPayFailDialog(PayOrderPresenter.AliPayResult.Message.CANCELED);
                break;
            case PayOrderPresenter.AliPayResult.Code.NET_ERROR:
                showPayFailDialog(PayOrderPresenter.AliPayResult.Message.NET_ERROR);
                break;
            default:
                showPayUnknownDialog();
                break;
        }
    }

    /************************支付功能操作***********************/
    //加载弹窗
    private LoadingDialog loadingDialog;

    private void startLoading(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
            loadingDialog.create();
        }
        loadingDialog.setMsg(msg);
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

        if (!TextUtils.isEmpty(showMsg)) {
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
    private void getUserInfo() {
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
                                EventBus.getDefault().post(new LoginEvent());
                                //刷新微课信息
                                if (mSubject.equals("微课直购")) {
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

    /*****************************抵扣功能操作**************************/
    //显示抵扣内容
    private void showDeduction() {
        //如果当前为调试模式，则直接不使用抵扣操作
        double showPrice = Double.parseDouble(mPrice);
        if (BuildConfig.DEBUG && showPrice < 2) {
            //因为价格低于2块钱的话，抵扣钱数为0，无法抵扣
            binding.deductionLayout.setVisibility(View.GONE);
            return;
        }

        //根据要求，爱语币暂时不增加抵扣功能，微课直购也不支持，仅支持会员
        if (!mOrderType.equals(Order_vip)) {
            binding.deductionLayout.setVisibility(View.GONE);
            return;
        }

        //最大不超过价格的一半，数据要为整数
        long walletMoney = (long) (BigDecimalUtil.trans2Double(0, UserInfoManager.getInstance().getMoney()) * 100L);
        //价格的一半(取整)
        long halfPrice = (long) (BigDecimalUtil.trans2Double(0, Double.parseDouble(mPrice)) * 100L / 2);
        //判断这两个，然后选择其中一个抵扣
        long deductionPrice = 0;
        if (walletMoney > 0 && halfPrice > 0) {
            if (walletMoney > halfPrice) {
                deductionPrice = halfPrice;
            } else {
                deductionPrice = walletMoney;
            }
        }
        //显示可用钱包
        int showWalletMoney = (int) (deductionPrice / 100L);
        binding.userMoneyTv.setText("(可用金额:" + showWalletMoney + "元)");
        binding.userMoneyTv.setOnClickListener(v -> {
            WalletListActivity.start(this);
        });
        binding.deductionTv.setText("0元");
        binding.clickDeductionLayout.setOnClickListener(v -> {
            if (showWalletMoney <= 0) {
                ToastUtil.showToast(this, "您当前可用金额为：" + showWalletMoney + "元，不足以进行抵扣");
                return;
            }

            showDeductionDialog(showWalletMoney);
        });
    }

    private void showDeductionDialog(int deductionMoney) {
        //价格数据
        deductionMoney += 1;
        //设置显示数据
        String[] showMoneyArray = new String[deductionMoney];
        for (int i = 0; i < deductionMoney; i++) {
            showMoneyArray[i] = String.valueOf(i);
        }

        OptionPicker optionPicker = new OptionPicker(this, showMoneyArray);
        //设置标题
        optionPicker.setTitleText("请选择抵扣金额(元)");
        optionPicker.setTitleTextColor(getResources().getColor(R.color.black));
        optionPicker.setTitleTextSize(16);
        optionPicker.setTopLineColor(getResources().getColor(R.color.gray));
        //设置按钮
        optionPicker.setSubmitTextSize(16);
        optionPicker.setSubmitTextColor(getResources().getColor(R.color.colorPrimary));
        optionPicker.setCancelTextSize(16);
        optionPicker.setCancelTextColor(getResources().getColor(R.color.colorPrimary));
        //设置item
        optionPicker.setTextSize(20);
        optionPicker.setTextColor(getResources().getColor(R.color.black));
        optionPicker.setDividerColor(getResources().getColor(R.color.gray));
        //设置回调
        optionPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int which, String s) {
                long showDeduction = Long.parseLong(showMoneyArray[which]);
                binding.deductionTv.setText(showDeduction + "元");
                //设置抵扣数据
                mDeduction = showDeduction * 100L;
            }
        });
        optionPicker.show();
    }
}
