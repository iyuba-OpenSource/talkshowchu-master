package com.iyuba.talkshow.lil.user.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iyuba.module.headlinetalk.ui.widget.LoadingDialog;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.lil.help_fix.manager.NetHostManager;
import com.iyuba.talkshow.lil.help_fix.manager.TempDataManager;
import com.iyuba.talkshow.lil.help_fix.manager.UserDataManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Mob_verify;
import com.iyuba.talkshow.lil.help_mvp.base.BaseStackActivity;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.data.NewLoginType;
import com.iyuba.talkshow.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.talkshow.ui.user.register.phone.RegisterByPhoneActivity;
import com.iyuba.talkshow.ui.user.register.submit.RegisterSubmitActivity;
import com.iyuba.talkshow.ui.web.WebActivity;
import com.iyuba.wordtest.data.LoginType;
import com.mob.secverify.OAuthPageEventCallback;
import com.mob.secverify.PreVerifyCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.VerifyCallback;
import com.mob.secverify.common.exception.VerifyException;
import com.mob.secverify.datatype.UiSettings;
import com.mob.secverify.datatype.VerifyResult;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title: 登录界面
 * @date: 2023/8/25 09:42
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewLoginActivity extends BaseStackActivity {
    //登录类型
    private static final String tag_loginType = "loginType";
    private String loginType = "loginType";
    //控件
    private View wxLoginView, accountLoginView;
    private EditText etUserName, etPassword;
    private TextView tvTitle, tvForgetPassword, tvSmallLogin, tvWxLogin, tvAccount, tvPrivacy;
    private Button btnBack, btnRegister, btnLogin;
    private CheckBox cbLoginPrivacy;
    private TextView tvLoginPrivacy;
    private RadioButton rbCheck;
    //加载弹窗
    private LoadingDialog loadingDialog;
    //微信小程序的token
    private String wxMiniToken = null;

    /***************其他操作*************/
    //爱语吧标识
    private ImageView iyubaLogoIv;

    public static void start(Context context, String loginType) {
        Intent intent = new Intent();
        intent.setClass(context, NewLoginActivity.class);
        intent.putExtra(tag_loginType, loginType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        initView();
        initData();
        initClick();

        switchType();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /************************初始化********************************/
    private void initView() {
        //切换类型界面
        wxLoginView = findViewById(R.id.loginType);
        accountLoginView = findViewById(R.id.accountLoginLayout);

        //账号登录
        etUserName = findViewById(R.id.et_userName);
        etPassword = findViewById(R.id.et_password);
        tvTitle = findViewById(R.id.tv_title);
        tvForgetPassword = findViewById(R.id.tv_forgetPassword);
        tvSmallLogin = findViewById(R.id.tv_smallLogin);
        cbLoginPrivacy = findViewById(R.id.cb_login_privacy);
        tvLoginPrivacy = findViewById(R.id.tv_login_privacy);
        btnBack = findViewById(R.id.btn_back);
        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);

        //登录类型选择
        tvWxLogin = findViewById(R.id.vx_login);
        tvAccount = findViewById(R.id.account_login);
        rbCheck = findViewById(R.id.agree_other);
        tvPrivacy = findViewById(R.id.agree_tv);

        //设置账号和密码
        String userName = UserInfoManager.getInstance().getLoginAccount();
        if (!TextUtils.isEmpty(userName)) {
            etUserName.setText(userName);
        }
        String password = UserInfoManager.getInstance().getLoginPassword();
        if (!TextUtils.isEmpty(password)) {
            etPassword.setText(password);
        }

        //隐藏爱语吧标识
        iyubaLogoIv = findViewById(R.id.imageView1);
        iyubaLogoIv.setVisibility(View.INVISIBLE);
    }

    private void initData() {
        loginType = getIntent().getStringExtra(tag_loginType);
        if (TextUtils.isEmpty(loginType)) {
            loginType = NewLoginType.loginByAccount;
        }

        //隐私政策显示
        tvPrivacy.setText(setPrivacySpan());
        tvPrivacy.setMovementMethod(new LinkMovementMethod());

        //账号登录界面的隐私政策显示
        tvLoginPrivacy.setText(setPrivacySpan());
        tvLoginPrivacy.setMovementMethod(new LinkMovementMethod());
    }

    private void initClick() {
        btnBack.setOnClickListener(v -> {
            finish();
        });
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(this, RegisterByPhoneActivity.class);
            startActivity(intent);
            finish();
        });
        btnLogin.setOnClickListener(v -> {
            if (!cbLoginPrivacy.isChecked()) {
                showToast("请先阅读并同意隐私政策和用户协议");
                return;
            }

            if (verifyAccountAndPsd()) {
                String userName = etUserName.getText().toString().trim();
                String userPwd = etPassword.getText().toString().trim();

                accountLogin(userName, userPwd);
            }
        });
        /*tvSmallLogin.setOnClickListener(v->{
            if (!cbPrivacy.isChecked()){
                ToastUtil.showToast(this,"请先阅读并同意隐私政策和用户协议");
                return;
            }

            toWxLogin();
        });*/
        tvForgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(this, WebActivity.class);
            intent.putExtra("url", "http://m." + NetHostManager.getInstance().getDomainShort() + "/m_login/inputPhonefp.jsp");
            intent.putExtra("title", "重置密码");
            startActivity(intent);
        });
        /*tvWxLogin.setOnClickListener(v->{
            if (!rbCheck.isChecked()){
                ToastUtil.showToast(this,"请先阅读并同意隐私政策和用户协议");
                return;
            }

            toWxLogin();
        });*/
        tvAccount.setOnClickListener(v -> {
            wxLoginView.setVisibility(View.GONE);
            accountLoginView.setVisibility(View.VISIBLE);
        });
    }

    private SpannableStringBuilder setPrivacySpan() {
        String childPrivacyStr = "《儿童隐私政策》";
        String privacyStr = "《隐私政策》";
        String termStr = "《用户协议》";
        String showMsg = "我已阅读并同意" + childPrivacyStr + "、" + privacyStr + "、" + termStr;

        SpannableStringBuilder spanStr = new SpannableStringBuilder();
        spanStr.append(showMsg);
        //儿童隐私政策
        int childPrivacyIndex = showMsg.indexOf(childPrivacyStr);
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String url = App.Url.CHILD_PROTOCOL_URL + App.APP_NAME_CH;
                Intent intent = WebActivity.buildIntent(NewLoginActivity.this,url,childPrivacyStr);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        }, childPrivacyIndex, childPrivacyIndex + childPrivacyStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //隐私政策
        int privacyIndex = showMsg.indexOf(privacyStr);
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String url = App.Url.PROTOCOL_URL + App.APP_NAME_CH;
                Intent intent = WebActivity.buildIntent(NewLoginActivity.this,url,privacyStr);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        }, privacyIndex, privacyIndex + privacyStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //用户协议
        int termIndex = showMsg.indexOf(termStr);
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String url = App.Url.PROTOCOL_USAGE + App.APP_NAME_CH;
                Intent intent = WebActivity.buildIntent(NewLoginActivity.this,url,termStr);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        }, termIndex, termIndex + termStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanStr;
    }

    /**************************样式显示*****************************/
    //切换样式
    private void switchType() {
        if (loginType.equals(NewLoginType.loginByWXSmall)) {
            //微信登录
            /*wxLoginView.setVisibility(View.VISIBLE);
            accountLoginView.setVisibility(View.GONE);

            getWXSmallToken();*/
        } else if (loginType.equals(NewLoginType.loginByVerify)) {
            //秒验登录
            wxLoginView.setVisibility(View.GONE);
            tvSmallLogin.setVisibility(View.GONE);
            accountLoginView.setVisibility(View.GONE);

            showVerify();
        } else {
            //账号登录
            wxLoginView.setVisibility(View.GONE);
            tvSmallLogin.setVisibility(View.GONE);
            accountLoginView.setVisibility(View.VISIBLE);
        }
    }

    /******************************登录方式************************/
    /*************微信登录***********/
    //获取微信小程序的token
    /*private void getWXSmallToken(){
        startLoading("正在加载登录信息～");
        LoginPresenter.getWXSmallToken(new Observer<VXTokenResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(VXTokenResponse bean) {
                closeLoading();

                if (bean.getResult().equals("200")){
                    wxMiniToken = bean.getToken();

                    //这里判断微信是否已经安装
                    IWXAPI wxapi = WXAPIFactory.createWXAPI(NewLoginActivity.this, Constant.getWxKey());
                    if (!wxapi.isWXAppInstalled()){
                        wxMiniToken = null;

                        loginType = LoginType.loginByAccount;
                        switchType();
                    }
                }else {
                    wxMiniToken = null;

                    loginType = LoginType.loginByAccount;
                    switchType();
                }
            }

            @Override
            public void onError(Throwable e) {
                closeLoading();
                wxMiniToken = null;

                loginType = LoginType.loginByAccount;
                switchType();
            }

            @Override
            public void onComplete() {

            }
        });
    }*/

    //跳转到微信登录
    /*private void toWxLogin(){
        IWXAPI wxapi = WXAPIFactory.createWXAPI(NewLoginActivity.this,Constant.getWxKey());
        if (!wxapi.isWXAppInstalled()){
            ToastUtil.showToast(NewLoginActivity.this,"您还未安装微信客户端");
            return;
        }

        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName="gh_a8c17ad593be";
        req.path="/subpackage/getphone/getphone?token="+wxMiniToken+"&appid="+Constant.APPID;
        req.miniprogramType=WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
        wxapi.sendReq(req);

        //放在临时框架中，后面要用
        WxLoginSession.getInstance().setWxSmallToken(wxMiniToken);

        finish();
    }*/

    /*************秒验登录***********/
    //展示秒验功能
    private void showVerify() {
        //设置秒验的样式
        UiSettings uiSettings = new UiSettings.Builder()
                .setLoginBtnImgId(R.drawable.shape_round_theme_10)
                .setCusAgreementNameId1("隐私政策")
                .setCusAgreementUrl1(App.Url.PROTOCOL_URL+ App.APP_NAME_CH)
                .setCusAgreementNameId2("用户协议")
                .setCusAgreementUrl2(App.Url.PROTOCOL_USAGE+ App.APP_NAME_CH)
                .build();
        SecVerify.setUiSettings(uiSettings);

        if (SecVerify.isVerifySupport() && TempDataManager.getInstance().getMobVerify()) {
            startLoading("正在获取登录信息~");
            SecVerify.OtherOAuthPageCallBack(new OAuthPageEventCallback() {
                @Override
                public void initCallback(OAuthPageEventResultCallback callback) {
                    callback.pageOpenCallback(new PageOpenedCallback() {
                        @Override
                        public void handle() {
                            closeLoading();
                        }
                    });
                }
            });
            SecVerify.verify(new VerifyCallback() {
                @Override
                public void onOtherLogin() {
                    //点击其他登录方式
                    closeLoading();
                    loginType = NewLoginType.loginByAccount;
                    switchType();
                }

                @Override
                public void onUserCanceled() {
                    //用户取消
                    closeLoading();
                    SecVerify.finishOAuthPage();
                    NewLoginActivity.this.finish();
                }

                @Override
                public void onComplete(VerifyResult result) {
                    //调用完成
                    if (result != null) {
                        //这里调用接口，从服务器获取数据展示
                        checkMobDataFromServer(result);
                    } else {
                        closeLoading();
                        loginType = NewLoginType.loginByAccount;
                        switchType();
                    }
                }

                @Override
                public void onFailure(VerifyException e) {
                    //调用失败
                    closeLoading();
                    loginType = NewLoginType.loginByAccount;
                    switchType();
                }
            });
        } else {
            closeLoading();
            loginType = NewLoginType.loginByAccount;
            switchType();
        }
    }

    //秒验和服务器查询
    private Disposable mobVerifyDis;

    private void checkMobDataFromServer(VerifyResult result) {
        startLoading("正在获取用户信息～");
        RxUtil.unDisposable(mobVerifyDis);
        UserDataManager.mobVerifyFromServer(result.getToken(), result.getOpToken(), result.getOperator())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Mob_verify>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mobVerifyDis = d;
                    }

                    @Override
                    public void onNext(Mob_verify bean) {
                        //存在数据
                        if (bean != null) {
                            //存在账号数据
                            if (bean.getIsLogin().equals("1") && bean.getUserinfo() != null) {
                                //根据20001重新获取数据
                                UserInfoManager.getInstance().getRemoteUserInfo(bean.getUserinfo().getUid(), new UserinfoCallbackListener() {
                                    @Override
                                    public void onSuccess() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                closeLoading();
                                                SecVerify.finishOAuthPage();

                                                NewLoginActivity.this.finish();
                                                EventBus.getDefault().post(new LoginEvent());
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(String errorMsg) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                closeLoading();
                                                SecVerify.finishOAuthPage();

                                                showToast(errorMsg);
                                                loginType = NewLoginType.loginByAccount;
                                                switchType();
                                            }
                                        });
                                    }
                                });
                                return;
                            }

                            //不存在账号数据，但是存在电话号
                            if (bean.getRes() != null && !TextUtils.isEmpty(bean.getRes().getPhone())) {
                                closeLoading();
                                SecVerify.finishOAuthPage();

                                //跳转界面
                                RegisterSubmitActivity.start(NewLoginActivity.this,bean.getRes().getPhone(),getRandomByPhone(bean.getRes().getPhone()),getPasswordByPhone(bean.getRes().getPhone()));
                                //界面关闭
                                NewLoginActivity.this.finish();
                                return;
                            }
                        }

                        closeLoading();
                        SecVerify.finishOAuthPage();

                        showToast("获取登录信息失败，请手动登录");
                        loginType = NewLoginType.loginByAccount;
                        switchType();
                    }

                    @Override
                    public void onError(Throwable e) {
                        SecVerify.finishOAuthPage();
                        showToast("获取登录信息失败，请手动登录");
                        loginType = NewLoginType.loginByAccount;
                        switchType();
                    }

                    @Override
                    public void onComplete() {
                        RxUtil.unDisposable(mobVerifyDis);
                    }
                });
    }

    /*************账号登录************/
    //验证账号和密码
    private boolean verifyAccountAndPsd() {
        String userName = etUserName.getText().toString().trim();
        String userPwd = etPassword.getText().toString().trim();

        if (userName.length() < 3) {
            showToast(getResources().getString(R.string.login_check_effective_user_data));
            return false;
        }

        if (userPwd.length() == 0) {
            showToast(getResources().getString(R.string.login_check_user_pwd_null));
            return false;
        }

        if (userPwd.length() < 6 || userPwd.length() > 20) {
            showToast(getResources().getString(R.string.login_check_user_pwd_constraint));
            return false;
        }

        return true;
    }

    //账号登录
    private Disposable accountLoginDis;

    private void accountLogin(String userName, String userPwd) {
        //设置不可点击操作
        etUserName.setEnabled(false);
        etPassword.setEnabled(false);

        startLoading("正在登录～");
        UserInfoManager.getInstance().postRemoteAccountLogin(userName, userPwd, new UserinfoCallbackListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        EventBus.getDefault().post(new LoginEvent());
                        isToFulfillInfo();
                    }
                });
            }

            @Override
            public void onFail(String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        showToast(errorMsg);
                    }
                });
            }
        });
    }

    //判断是否跳转到用户信息完善界面
    private void isToFulfillInfo() {
        NewLoginActivity.this.finish();
    }

    /*******************************辅助功能***********************/
    //开启加载弹窗
    private void startLoading(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }

        if (loadingDialog.isShowing()) {
            return;
        }

        loadingDialog.setMessage(msg);
        loadingDialog.show();
    }

    //关闭加载弹窗
    private void closeLoading() {
        etUserName.setEnabled(true);
        etPassword.setEnabled(true);

        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }


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

    /*************************************设置键盘显示和隐藏********************************/
    //隐藏键盘(用于处理鸿蒙手机上的显示问题)
    private void hideKeyBoard() {
        if (isKeyBoardOpen()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //判断键盘是否弹窗
    private boolean isKeyBoardOpen() {
        int height = getWindow().getDecorView().getHeight();
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return height * 2 / 3 > rect.bottom;
    }

    //显示toast消息
    private void showToast(String showMsg) {
        ToastUtil.showToast(this, showMsg);
        hideKeyBoard();
    }
}
