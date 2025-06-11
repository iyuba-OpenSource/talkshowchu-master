//package com.iyuba.talkshow.ui.user.login.fix;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.SpannableStringBuilder;
//import android.text.Spanned;
//import android.text.TextPaint;
//import android.text.TextUtils;
//import android.text.method.LinkMovementMethod;
//import android.text.style.ClickableSpan;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.CompoundButton;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.iyuba.talkshow.Constant;
//import com.iyuba.talkshow.R;
//import com.iyuba.talkshow.constant.App;
//import com.iyuba.talkshow.constant.ConfigData;
//import com.iyuba.talkshow.data.manager.ConfigManager;
//import com.iyuba.talkshow.data.model.result.RegisterMobResponse;
//import com.iyuba.talkshow.databinding.ActivityLoginFixBinding;
//import com.iyuba.talkshow.lil.help_fix.manager.TempDataManager;
//import com.iyuba.talkshow.lil.help_fix.view.dialog.LoadingDialog;
//import com.iyuba.talkshow.newdata.SPconfig;
//import com.iyuba.talkshow.ui.base.BaseViewBindingActivity;
//import com.iyuba.talkshow.ui.user.edit.ImproveUserActivity;
//import com.iyuba.talkshow.ui.user.register.phone.RegisterByPhoneActivity;
//import com.iyuba.talkshow.ui.user.register.submit.RegisterSubmitActivity;
//import com.iyuba.talkshow.ui.web.WebActivity;
//import com.iyuba.talkshow.util.FastClickUtil;
//import com.iyuba.talkshow.util.ToastUtil;
//import com.iyuba.wordtest.data.LoginType;
//import com.mob.secverify.OAuthPageEventCallback;
//import com.mob.secverify.SecVerify;
//import com.mob.secverify.VerifyCallback;
//import com.mob.secverify.common.exception.VerifyException;
//import com.mob.secverify.datatype.VerifyResult;
//import com.tencent.mm.opensdk.constants.ConstantsAPI;
//import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
//import com.tencent.mm.opensdk.openapi.IWXAPI;
//import com.tencent.mm.opensdk.openapi.WXAPIFactory;
//
//import javax.inject.Inject;
//
///**
// * @title: 新的登录操作逻辑
// * @date: 2023/8/29 16:28
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//public class FixLoginActivity extends BaseViewBindingActivity<ActivityLoginFixBinding> implements FixLoginMvpView {
//
//    //参数
//    private static final String tag_loginType = "loginType";
//    private String loginType = "loginType";
//    //弹窗
//    private LoadingDialog loadingDialog;
//    //数据
//    @Inject
//    ConfigManager configManager;
//    @Inject
//    FixLoginPresenter presenter;
//    //登录类型
//    private String showLoginType = LoginType.loginByWXSmall;
//    //微信的token
//    private String wxToken = null;
//
//    public static void start(Context context,String loginType){
//        Intent intent = new Intent();
//        intent.setClass(context, FixLoginActivity.class);
//        intent.putExtra(tag_loginType,loginType);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        loginType = getIntent().getStringExtra(tag_loginType);
//        if (TextUtils.isEmpty(loginType)){
//            loginType = ConfigData.loginType;
//        }
//
//        activityComponent().inject(this);
//        presenter.attachView(this);
//    }
//
//    @Override
//    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        initToolbar();
//        initData();
//        initClick();
//
//        //根据类型切换样式
//        if (loginType.equals(LoginType.loginByWXSmall)){
//            //小程序登录
//            updateLoginType(LoginType.loginByWXSmall,true);
//
//            showSmallLogin();
//        }else if (loginType.equals(LoginType.loginByVerify)){
//            //秒验登录
//            updateLoginType(LoginType.loginByAccount,false);
//
//            showVerify();
//        }else if (loginType.equals(LoginType.loginByAccount)){
//            //账号登录
//            updateLoginType(LoginType.loginByAccount,false);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        closeLoading();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == android.R.id.home){
//            finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    /****************************初始化***********************/
//    //初始化toolbar
//    private void initToolbar(){
//        setSupportActionBar(binding.loginToolbar.listToolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }
//
//    private void initData(){
//        //全部屏蔽
//        binding.smallLayout.setVisibility(View.INVISIBLE);
//        binding.accountLayout.setVisibility(View.INVISIBLE);
//        binding.otherLogin.setVisibility(View.INVISIBLE);
//        //显示链接
//        showLink();
//        //设置忘记密码
//        binding.loginResetPwdTv.setText(getString(R.string.login_find_password));
//        binding.loginResetPwdTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//        //设置其他
//        configManager.setCheckAgree(false);
//        if (TextUtils.isEmpty(binding.loginUsernameEdt.getText().toString().trim()) && !TextUtils.isEmpty(configManager.getAdName())) {
//            binding.loginUsernameEdt.setText(configManager.getAdName());
//        }
//        if (TextUtils.isEmpty(binding.loginPwdEdt.getText().toString().trim()) && !TextUtils.isEmpty(configManager.getAdPass())) {
//            binding.loginPwdEdt.setText(configManager.getAdPass());
//        }
//    }
//
//    private void initClick(){
//        //设置切换
//        binding.otherLogin.setOnClickListener(v->{
//            configManager.setCheckAgree(false);
//
//            if (showLoginType.equals(LoginType.loginByWXSmall)){
//                showLoginType = LoginType.loginByAccount;
//            }else if (showLoginType.equals(LoginType.loginByAccount)){
//                showLoginType = LoginType.loginByWXSmall;
//            }
//
//            updateLoginType(showLoginType,true);
//        });
//        //设置小程序登陆
//        binding.smallLogin.setText("微信小程序登录");
//        binding.smallLogin.setOnClickListener(v->{
//            if (!binding.smallAgree.isChecked()){
//                showToastShort("请勾选同意使用条款和隐私政策");
//                return;
//            }
//
//            if (FastClickUtil.fastClick()){
//                return;
//            }
//
//            clickSmallLogin();
//        });
//        //设置注册
//        binding.loginRegisterBtn.setOnClickListener(v->{
//            Intent intent = new Intent(this, RegisterByPhoneActivity.class);
//            startActivity(intent);
//            finish();
//        });
//        //设置登陆
//        binding.loginLoginBtn.setOnClickListener(v->{
//            if (!binding.accountAgree.isChecked()){
//                showToastShort("请勾选同意使用条款和隐私政策");
//                return;
//            }
//
//            clickAccountLogin();
//        });
//        //设置小程序同意
//        binding.smallAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    configManager.setCheckAgree(true);
//                }else {
//                    configManager.setCheckAgree(false);
//                }
//            }
//        });
//        //设置账号同意
//        binding.accountAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    configManager.setCheckAgree(true);
//                }else {
//                    configManager.setCheckAgree(false);
//                }
//            }
//        });
//        //设置忘记密码
//        binding.loginResetPwdTv.setOnClickListener(v->{
//            Intent intent  = new Intent();
//            intent.setClass(mContext, WebActivity.class);
//            intent.putExtra("url", "http://m." + Constant.Web.WEB_SUFFIX + "m_login/inputPhonefp.jsp");
//            intent.putExtra("title", "密码找回");
//            startActivity(intent);
//        });
//    }
//
//    /*****************************登录类型**********************/
//    /***********小程序登录***********/
//    //显示小程序登录
//    private void showSmallLogin(){
//        showLoading("正在初始化登录信息～");
//
//        presenter.getSmallToken();
//    }
//
//    //小程序登录
//    private void clickSmallLogin(){
////        IWXAPI iwxapi = WXAPIFactory.createWXAPI(this,App.WX_KEY);
//        IWXAPI iwxapi = WXAPIFactory.createWXAPI(this,ConfigData.wx_key);
//        if (!iwxapi.isWXAppInstalled()){
//            showToastShort("您当前未安装微信客户端，请使用其他方式登陆");
//            return;
//        }
//
//        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
//        req.userName = ConfigData.wx_small_name;
//        req.path = "/packageA/pages/getphone/getphone?token="+wxToken+"&appid="+App.APP_ID;
//        req.miniprogramType = ConstantsAPI.WXMiniProgram.MINI_PROGRAM_TYPE_RELEASE;
//        iwxapi.sendReq(req);
//
//        //将token保存在临时会话中
//        FixLoginSession.getInstance().setWxSmallToken(wxToken);
//        //推出
//        finish();
//    }
//
//    /*************秒验登录***********/
//    //显示秒验界面
//    private void showVerify(){
//        if (SecVerify.isVerifySupport()&& TempDataManager.getInstance().getMobVerify()){
//            showLoading("正在初始化登录信息～");
//            SecVerify.OtherOAuthPageCallBack(new OAuthPageEventCallback() {
//                @Override
//                public void initCallback(OAuthPageEventResultCallback callback) {
//                    callback.pageOpenCallback(new PageOpenedCallback() {
//                        @Override
//                        public void handle() {
//                            closeLoading();
//                        }
//                    });
//                }
//            });
//            SecVerify.verify(new VerifyCallback() {
//                @Override
//                public void onOtherLogin() {
//                    //其他登录方式
//                    SecVerify.finishOAuthPage();
//                }
//
//                @Override
//                public void onUserCanceled() {
//                    //其他操作取消
//                    SecVerify.finishOAuthPage();
//                }
//
//                @Override
//                public void onComplete(VerifyResult result) {
//                    //调用成功
//                    if (result!=null){
//                        checkVerifyServer(result);
//                    }else {
//                        SecVerify.finishOAuthPage();
//                    }
//                }
//
//                @Override
//                public void onFailure(VerifyException e) {
//                    //调用失败
//                    SecVerify.finishOAuthPage();
//                }
//            });
//        }else {
//            updateLoginType(LoginType.loginByAccount,false);
//        }
//    }
//
//    //秒验服务器查询
//    private void checkVerifyServer(VerifyResult result){
//        showLoading("正在获取用户信息～");
//        presenter.verifyCheck(result);
//    }
//
//    /*************账号登录***********/
//    private void clickAccountLogin() {
//        String userName = binding.loginUsernameEdt.getText().toString();
//        String userPwd = binding.loginPwdEdt.getText().toString();
//        if (verification(userName, userPwd)) {
//            presenter.login(userName, userPwd);
//        }
//    }
//
//    //验证
//    private boolean verification(String userName, String userPwd) {
//        if (userName.length() < 3) {
//            binding.loginUsernameEdt.setError(getResources().getString(R.string.login_check_effective_user_id));
//            return false;
//        }
//        if (userPwd.length() == 0) {
//            binding.loginPwdEdt.setError(getResources().getString(R.string.login_check_user_pwd_null));
//            return false;
//        }
//        if (!(userPwd.length() >= 6 && userPwd.length() <= 20)) {
//            binding.loginPwdEdt.setError(getResources().getString(R.string.login_check_user_pwd_constraint));
//            return false;
//        }
//        return true;
//    }
//
//    /******************************辅助操作*************************/
//    //显示加载弹窗
//    private void showLoading(String message){
//        if (loadingDialog==null){
//            loadingDialog = new LoadingDialog(this);
//            loadingDialog.create();
//        }
//        loadingDialog.setMsg(message);
//        loadingDialog.show();
//    }
//
//    //关闭加载弹窗
//    private void closeLoading(){
//        if (loadingDialog!=null&&loadingDialog.isShowing()){
//            loadingDialog.dismiss();
//        }
//    }
//
//    //设置链接显示
//    private void showLink(){
//        SpannableStringBuilder showSpan = new SpannableStringBuilder();
//        String showText = getResources().getString(R.string.read_agree_policy);
//        showSpan.append(showText);
//        //隐私政策
//        String privacyText = "隐私政策";
//        int privacyIndex = showText.indexOf(privacyText);
//        ClickableSpan privacySpan = new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View widget) {
//                Intent intent = WebActivity.buildIntent(mContext, App.Url.PROTOCOL_URL + App.APP_NAME_CH, privacyText);
//                mContext.startActivity(intent);
//            }
//
//            @Override
//            public void updateDrawState(@NonNull TextPaint ds) {
//                ds.setColor(getResources().getColor(R.color.colorPrimaryDark));
//                ds.setUnderlineText(true);
//            }
//        };
//        //使用条款
//        String termText = "使用条款";
//        int termIndex = showText.indexOf(termText);
//        ClickableSpan termSpan = new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View widget) {
//                Intent intent = WebActivity.buildIntent(mContext, App.Url.PROTOCOL_USAGE + App.APP_NAME_CH, termText);
//                mContext.startActivity(intent);
//            }
//
//            @Override
//            public void updateDrawState(@NonNull TextPaint ds) {
//                ds.setColor(getResources().getColor(R.color.colorPrimaryDark));
//                ds.setUnderlineText(true);
//            }
//        };
//        //显示
//        showSpan.setSpan(privacySpan,privacyIndex,privacyIndex+privacyText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        showSpan.setSpan(termSpan,termIndex,termIndex+termText.length(),Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//
//        binding.smallAgree.setText(showSpan);
//        binding.smallAgree.setMovementMethod(LinkMovementMethod.getInstance());
//        binding.accountAgree.setText(showSpan);
//        binding.accountAgree.setMovementMethod(LinkMovementMethod.getInstance());
//    }
//
//    //切换登陆样式
//    private void updateLoginType(String type,boolean isOtherShow){
//        switch (type){
//            case LoginType.loginByWXSmall:
//                //微信小程序
//                getSupportActionBar().setTitle("微信小程序登录");
//                binding.smallLayout.setVisibility(View.VISIBLE);
//                binding.accountLayout.setVisibility(View.INVISIBLE);
//                binding.otherLogin.setText("切换为账号登录");
//                break;
//            case LoginType.loginByAccount:
//                //账号
//                getSupportActionBar().setTitle("账号登录");
//                binding.smallLayout.setVisibility(View.INVISIBLE);
//                binding.accountLayout.setVisibility(View.VISIBLE);
//                binding.otherLogin.setText("切换为微信小程序登录");
//                break;
//        }
//
//        if (isOtherShow){
//            binding.otherLogin.setVisibility(View.VISIBLE);
//        }else {
//            binding.otherLogin.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    /*********************************回调信息**************************/
//    @Override
//    public void showWxToken(String token) {
//        closeLoading();
//
//        if (TextUtils.isEmpty(token)){
//            //切换为账户登录，屏蔽微信登录
//            this.wxToken = null;
//
//            updateLoginType(LoginType.loginByAccount,false);
//        }else {
//            //显示微信登录
//            this.wxToken = token;
//
////            IWXAPI iwxapi = WXAPIFactory.createWXAPI(this, App.WX_KEY);
//            IWXAPI iwxapi = WXAPIFactory.createWXAPI(this, ConfigData.wx_key);
//            if (!iwxapi.isWXAppInstalled()){
//                updateLoginType(LoginType.loginByAccount,false);
//            }else {
//                updateLoginType(LoginType.loginByWXSmall,true);
//            }
//        }
//    }
//
//    @Override
//    public void showWxUserId(int uid) {
//
//    }
//
//    @Override
//    public void showUserInfo(int uid, boolean isSuccess) {
//
//    }
//
//    @Override
//    public void startToUser() {
//
//    }
//
//    @Override
//    public void startToImprover(int userId) {
//        if ((userId > 0) && SPconfig.Instance().loadBoolean("userId_" + userId, true)) {
//            Intent userInfo = new Intent(mContext, ImproveUserActivity.class);
//            userInfo.putExtra("register", false);
//            startActivity(userInfo);
//        }
//    }
//
//    @Override
//    public void showMobVerifyCheckMsg(boolean isSuccess,boolean isToRegister,RegisterMobResponse response) {
//        closeLoading();
//        SecVerify.finishOAuthPage();
//
//        if (isSuccess){
//            ToastUtil.showToast(this,"快速登录成功～");
//            finish();
//        }else {
//            if (isToRegister){
//                ToastUtil.showToast(this,"当前账号未注册，请注册后使用");
//                Intent intent = new Intent(this, RegisterSubmitActivity.class);
//                intent.putExtra("phoneNumb",response.res.phone);
//                startActivity(intent);
//                finish();
//            }else {
//                ToastUtil.showToast(this,"快速登录失败，请手动登录账号");
//                updateLoginType(LoginType.loginByAccount,false);
//            }
//        }
//    }
//}
