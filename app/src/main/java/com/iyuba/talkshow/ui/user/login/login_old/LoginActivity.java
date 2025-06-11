//package com.iyuba.talkshow.ui.user.login.login_old;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Html;
//import android.text.TextUtils;
//import android.text.method.LinkMovementMethod;
//import android.view.MenuItem;
//import android.widget.CompoundButton;
//import android.widget.Toast;
//
//import com.iyuba.talkshow.Constant;
//import com.iyuba.talkshow.R;
//import com.iyuba.talkshow.data.manager.ConfigManager;
//import com.iyuba.talkshow.databinding.ActivityLoginBinding;
//import com.iyuba.talkshow.newdata.SPconfig;
//import com.iyuba.talkshow.ui.base.BaseViewBindingActivity;
//import com.iyuba.talkshow.ui.user.edit.ImproveUserActivity;
//import com.iyuba.talkshow.ui.user.register.phone.RegisterByPhoneActivity;
//import com.iyuba.talkshow.ui.web.WebActivity;
//import com.iyuba.talkshow.ui.widget.LoadingDialog;
//import com.umeng.analytics.MobclickAgent;
//
//import javax.inject.Inject;
//
//
///**
// * 当前已经被替换，原始的登录界面，暂无用处
// */
//public class LoginActivity extends BaseViewBindingActivity<ActivityLoginBinding> implements LoginMvpView {
//
//    @Inject
//    LoginPresenter mPresenter;
//    @Inject
//    ConfigManager mConfigManager;
//
//
//    LoadingDialog mDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        activityComponent().inject(this);
//        setSupportActionBar(binding.loginToolbar.listToolbar);
//        mPresenter.attachView(this);
//        mDialog = new LoadingDialog(this);
//        setClick();
//    }
//
//    private void setClick() {
//        binding.loginRegisterBtn.setOnClickListener(v -> clickRegister());
//        binding.loginLoginBtn.setOnClickListener(v -> clickLogin());
//        binding.loginResetPwdTv.setOnClickListener(v -> startWeb());
//        binding.loginAutoCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                mConfigManager.setAutoLogin(isChecked);
//            }
//        });
//    }
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        binding.loginAutoCheckbox.setChecked(mConfigManager.isAutoLogin());
//        binding.loginResetPwdTv.setText(Html.fromHtml("<a href=\"http://m."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"m_login/inputPhonefp.jsp\">"
//                + getString(R.string.login_find_password) + "</a>"));
//        binding.loginResetPwdTv.setMovementMethod(LinkMovementMethod.getInstance());
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        MobclickAgent.onPause(this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
////        binding.loginAutoCheckbox.setChecked(true);
//        MobclickAgent.onResume(this);
//        if (TextUtils.isEmpty(binding.loginUsernameEdt.getText().toString().trim()) && !TextUtils.isEmpty(mConfigManager.getAdName())) {
//            binding.loginUsernameEdt.setText(mConfigManager.getAdName());
//        }
//        if (TextUtils.isEmpty(binding.loginPwdEdt.getText().toString().trim()) && !TextUtils.isEmpty(mConfigManager.getAdPass())) {
//            binding.loginPwdEdt.setText(mConfigManager.getAdPass());
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mPresenter.detachView();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//        }
//        return true;
//    }
//
////    @PermissionGrant(REQUEST_LOCATION_PERMISSION)
//    public void login() {
//        String userName = binding.loginUsernameEdt.getText().toString();
//        String password = binding.loginPwdEdt.getText().toString();
//        mPresenter.login(userName, password);
//    }
//
////    @PermissionDenied(REQUEST_LOCATION_PERMISSION)
//    public void requestLocationFail() {
//        Toast.makeText(this, "权限不足,无法登录", Toast.LENGTH_SHORT).show();
//    }
//
////    @OnCheckedChanged(R.id.login_auto_checkbox)
////    void checkAutoLogin(boolean checked) {
////        mConfigManager.setAutoLogin(checked);
////    }
//
////    @OnClick(R.id.login_register_btn)
//    void clickRegister() {
//        Intent intent = new Intent(this, RegisterByPhoneActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
////    @OnClick(R.id.login_login_btn)
//    void clickLogin() {
//        String userName = binding.loginUsernameEdt.getText().toString();
//        String userPwd = binding.loginPwdEdt.getText().toString();
//        if (verification(userName, userPwd)) {
//            login();
//        }
//    }
//
//    private boolean verification(String userName, String userPwd) {
//        if (userName.length() < 3) {
////            YoYo.with(Techniques.Shake).duration(200)
////                    .interpolate(new AccelerateInterpolator())
////                    .playOn(findViewById(R.id.login_username_phone_ll));
//            binding.loginUsernameEdt.setError(getResources().getString(R.string.login_check_effective_user_id));
//            return false;
//        }
//        if (userPwd.length() == 0) {
////            YoYo.with(Techniques.Shake).duration(200)
////                    .interpolate(new AccelerateInterpolator())
////                    .playOn(findViewById(R.id.login_pwd_ll));
//            binding.loginPwdEdt.setError(getResources().getString(R.string.login_check_user_pwd_null));
//            return false;
//        }
//        if (!checkUserPwd(userPwd)) {
////            YoYo.with(Techniques.Shake).duration(200)
////                    .interpolate(new AccelerateInterpolator())
////                    .playOn(findViewById(R.id.login_pwd_ll));
//            binding.loginPwdEdt.setError(getResources().getString(R.string.login_check_user_pwd_constraint));
//            return false;
//        }
//        return true;
//    }
//
//    private boolean checkUserPwd(String userPwd) {
//        return userPwd.length() >= 6 && userPwd.length() <= 20;
//    }
//
//    @Override
//    public void showWaitingDialog() {
//        mDialog.show();
//    }
//
//    @Override
//    public void dismissWaitingDialog() {
//        mDialog.dismiss();
//    }
//
//    @Override
//    public void showToast(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void startImproveUser(int userId){
//        if ((userId > 0) && SPconfig.Instance().loadBoolean("userId_" + userId, true)) {
//            Intent userInfo = new Intent(mContext, ImproveUserActivity.class);
//            userInfo.putExtra("register", false);
//            startActivity(userInfo);
//        }
//    }
//
//    public void startWeb(){
//        Intent intent  = new Intent();
//        intent.setClass(mContext, WebActivity.class);
//        intent.putExtra("url", "http://m." + Constant.Web.WEB_SUFFIX + "m_login/inputPhonefp.jsp");
//        intent.putExtra("title", "密码找回");
//        startActivity(intent);
//    }
//}
