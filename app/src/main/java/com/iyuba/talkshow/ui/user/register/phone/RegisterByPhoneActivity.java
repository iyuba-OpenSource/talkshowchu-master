package com.iyuba.talkshow.ui.user.register.phone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.databinding.ActivityRegisterByPhoneBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.user.register.email.RegisterActivity;
import com.iyuba.talkshow.ui.user.register.submit.RegisterSubmitActivity;
import com.iyuba.talkshow.ui.web.WebActivity;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.TelNumMatch;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.talkshow.util.VerifyCodeSmsObserver;
import com.iyuba.wordtest.utils.LibRxTimer;
import com.mob.MobSDK;
import com.umeng.analytics.MobclickAgent;

import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 手机号注册界面
 */
public class RegisterByPhoneActivity extends BaseActivity implements RegisterByPhoneMvpView {


    ActivityRegisterByPhoneBinding binding ;
    @Inject
    RegisterByPhonePresenter mPresenter;
    @Inject
    ConfigManager configManager;

    VerifyCodeSmsObserver.OnReceiveVerifyCodeSMSListener listener;
    private LoadingDialog mLoadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityRegisterByPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activityComponent().inject(this);
        setSupportActionBar(binding.registerToolbar.listToolbar);
        setupProtocol();
        mPresenter.attachView(this);
        mLoadingDialog = new LoadingDialog(this);
        setClick();

        //隐藏爱语吧的标识
        binding.logoIv.setVisibility(View.INVISIBLE);
    }

    private void setClick() {
        binding.registerByEmailTv.setVisibility(View.GONE);
        binding.registerByEmailTv.setOnClickListener(v -> clickToEmail());
        binding.registerNextBtn.setOnClickListener(v -> clickNext());
        binding.registerGetCodeBtn.setOnClickListener(v -> clickGetVCode());
    }

    private void setupProtocol() {
        String childPrivacyStr = "《儿童隐私政策》";
        String privacyStr = "《隐私政策》";
        String termStr = "《用户协议》";
        String showMsg = "我已阅读并同意"+childPrivacyStr+"、"+privacyStr+"、"+termStr;

        SpannableStringBuilder spanStr = new SpannableStringBuilder();
        spanStr.append(showMsg);
        //儿童隐私政策
        int childPrivacyIndex = showMsg.indexOf(childPrivacyStr);
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(RegisterByPhoneActivity.this, WebActivity.class);
                String url = App.Url.CHILD_PROTOCOL_URL + App.APP_NAME_CH;
                intent.putExtra("url", url);
                intent.putExtra("title", childPrivacyStr);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        },childPrivacyIndex,childPrivacyIndex+childPrivacyStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //隐私政策
        int privacyIndex = showMsg.indexOf(privacyStr);
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(RegisterByPhoneActivity.this, WebActivity.class);
                String url = App.Url.PROTOCOL_URL + App.APP_NAME_CH;
                intent.putExtra("url", url);
                intent.putExtra("title", privacyStr);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        },privacyIndex,privacyIndex+privacyStr.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //用户协议
        int termIndex = showMsg.indexOf(termStr);
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(RegisterByPhoneActivity.this, WebActivity.class);
                String url = App.Url.PROTOCOL_USAGE + App.APP_NAME_CH;
                intent.putExtra("url", url);
                intent.putExtra("title", termStr);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        },termIndex,termIndex+termStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.tvPrivacy.setText(spanStr);
        binding.tvPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        MobSDK.init(getApplicationContext(), ConfigData.mob_key, ConfigData.mob_secret);
        MobSDK.submitPolicyGrantResult(true, null);
        SMSSDK.registerEventHandler(mEventHandler);
        listener = new VerifyCodeSmsObserver.OnReceiveVerifyCodeSMSListener() {
            @Override
            public void onReceive(String vcodeContent) {
                binding.registerCodeEdt.setText(vcodeContent);
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭计时器
        stopVerifyCodeTimer();
        SMSSDK.unregisterEventHandler(mEventHandler);
        mPresenter.detachView();
        listener = null ;
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

    void clickToEmail() {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    void clickNext() {
        if (!binding.cbPrivacy.isChecked()){
            ToastUtil.showToast(mContext, "请选中下方的阅读并同意~");
            hideKeyBoard();
            return;
        }

        configManager.setCheckAgree(true);
        String phoneNum = binding.registerPhoneNumEdt.getText().toString();
        String vcode = binding.registerCodeEdt.getText().toString();
        if (verification(phoneNum, vcode)) {
            requestSubmitCodePermissionSuccess();
        } else {
            showToast(R.string.verify_code_not_null);
        }
    }

    void clickGetVCode() {
        String phoneNum = binding.registerPhoneNumEdt.getText().toString();
        if (verifyPhoneNumber(phoneNum)) {
            requestGetCodePermissionSuccess();
        } else {
            hideKeyBoard();
            ToastUtil.show(this,getString(R.string.phone_num_error));
        }
    }

    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
        hideKeyBoard();
    }

    public void requestGetCodePermissionSuccess() {
//        mLoadingDialog.show();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        String phoneNum = binding.registerPhoneNumEdt.getText().toString();
        mPresenter.getVerifyCode(phoneNum);
//        SMSSDK.getVerificationCode("86", phoneNum);
        Log.e("RegisterByPhoneActivity", "requestGetCodePermissionSuccess phoneNum " + phoneNum);
    }

    public void requestSubmitCodePermissionSuccess() {
        String phoneNumString = binding.registerPhoneNumEdt.getText().toString().trim();
        SMSSDK.submitVerificationCode("86", phoneNumString, binding.registerCodeEdt.getText().toString().trim());
    }


    private boolean verification(String phoneNum, String vcode) {
        if (!verifyPhoneNumber(phoneNum)) {
            hideKeyBoard();
            ToastUtil.show(this,getString(R.string.phone_num_error));
            return false;
        }
        if (TextUtils.isEmpty(vcode)) {
            hideKeyBoard();
            ToastUtil.show(this,getString(R.string.verify_code_not_null));
            return false;
        }
        return true;
    }

    private boolean verifyPhoneNumber(String phoneNum) {
        return TelNumMatch.isPhonenumberLegal(phoneNum);
    }

    @Override
    public void updateGetCodeBtn() {
        SMSSDK.getVerificationCode("86", binding.registerPhoneNumEdt.getText().toString());
        binding.registerGetCodeBtn.setText(getString(R.string.register_input_code));
        binding.registerGetCodeBtn.setTextColor(Color.WHITE);
        setGetCodeBtn(false);

        //开启计时
        startVerifyCodeTimer();
    }

    @Override
    public void dismissWaitingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void registerSmsObserver() {
//        getContentResolver().registerContentObserver(Uri.parse("mContentEdt://sms/"), true, smsObserver);
    }

    private final EventHandler mEventHandler = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                switch (event) {
                    case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast(R.string.register_code_sent);
                                binding.registerNextBtn.setEnabled(true);
                            }
                        });
                        break;
                    case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast(R.string.register_verify_success);
                            }
                        });
                        //关闭计时器
                        stopVerifyCodeTimer();
                        //跳转界面
                        String phoneNumString = binding.registerPhoneNumEdt.getText().toString();
                        RegisterSubmitActivity.start(RegisterByPhoneActivity.this,phoneNumString,getRandomByPhone(phoneNumString),getPasswordByPhone(phoneNumString));
                        finish();
                        break;
                    default:
                        break;
                }
            } else {
                if (data != null) {
                    Log.e("RegisterByPhoneActivity", "mEventHandler " + ((Throwable)data).getMessage());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            //重置显示
                            showToast(R.string.input_correct_code);

                            //防止多次刷新验证码
//                            binding.registerGetCodeBtn.setText(getString(R.string.get_verify_code));
//                            binding.registerGetCodeBtn.setEnabled(true);
                        }
                    }
                });
            }
        }
    };

    //验证码计时器
    private static final String timer_getVerifyCode = "timer_getVerifyCode";
    //倒计时时间
    private static final long downTime = 60*1000L;

    private void startVerifyCodeTimer(){
        LibRxTimer.getInstance().multiTimerInMain(timer_getVerifyCode, 0, 1000L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                long curTime = number*1000L;
                int showDownTime = (int) ((downTime-curTime)/1000L);
                binding.registerGetCodeBtn.setText(MessageFormat.format(getString(R.string.seconds_for_get_code), showDownTime));

                if (curTime>=downTime){
                    stopVerifyCodeTimer();

                    //重置显示样式
                    binding.registerGetCodeBtn.setText(getString(R.string.get_verify_code));
                    setGetCodeBtn(true);
                }
            }
        });
    }

    private void stopVerifyCodeTimer(){
        LibRxTimer.getInstance().cancelTimer(timer_getVerifyCode);
    }

    /*********************************键盘设置***************************/
    //隐藏键盘(用于处理鸿蒙手机上的显示问题)
    private void hideKeyBoard(){
        if (isKeyBoardOpen()){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //判断键盘是否弹窗
    private boolean isKeyBoardOpen(){
        int height = getWindow().getDecorView().getHeight();
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return height*2/3 > rect.bottom;
    }

    /**********************************切换获取验证码按钮*********************************/
    private void setGetCodeBtn(boolean isViewEnable){
        if (isViewEnable){
            binding.registerGetCodeBtn.setEnabled(true);
            binding.registerGetCodeBtn.setBackgroundResource(R.drawable.shape_green_button);
        }else {
            binding.registerGetCodeBtn.setEnabled(false);
            binding.registerGetCodeBtn.setBackgroundResource(R.drawable.shape_gray_button);
        }
    }

    /***************************************其他功能***********************************/
    //根据手机号随机生成用户名称
    private String getRandomByPhone(String phone) {
        if (TextUtils.isEmpty(phone)){
            return "";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("iyuba");

        //随机数
        for (int i = 0; i < 4; i++) {
            int randomInt = (int) (Math.random() * 10);
            builder.append(randomInt);
        }

        String lastPhone = null;
        if (phone.length() > 4) {
            lastPhone = phone.substring(phone.length() - 4);
        } else {
            String time = String.valueOf(System.currentTimeMillis());
            lastPhone = time.substring(time.length() - 4);
        }
        builder.append(lastPhone);
        return builder.toString();
    }

    //根据手机号生成密码
    private String getPasswordByPhone(String phone){
        if (TextUtils.isEmpty(phone)){
            return "";
        }

        if (phone.length()<6){
            return "";
        }

        return phone.substring(phone.length() - 6);
    }
}
