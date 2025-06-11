package com.iyuba.talkshow.ui.user.register.submit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.ActivityRegisterSubmitBinding;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.user.edit.ImproveUserActivity;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;


public class RegisterSubmitActivity extends BaseActivity implements RegisterSubmitMvpView {
    public static String PhoneNum = "phoneNumb";
    public static String UserName = "username";
    public static String PassWord = "password";

    private String phoneNum;
    private String userName;
    private String passWord;

    @Inject
    RegisterSubmitPresenter mPresenter;

    ActivityRegisterSubmitBinding binding ;
    LoadingDialog mLoadingDialog;

    public static void start(Context context,String phone,String userName,String password){
        Intent intent = new Intent();
        intent.setClass(context, RegisterSubmitActivity.class);
        intent.putExtra(PhoneNum,phone);
        intent.putExtra(UserName,userName);
        intent.putExtra(PassWord,password);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterSubmitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activityComponent().inject(this);
        setSupportActionBar(binding.registsubmitToolbar.listToolbar);
        mPresenter.attachView(this);
        mLoadingDialog = new LoadingDialog(this);
        binding.registsubmitSubmitBtn.setOnClickListener(v -> clickSubmit());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        phoneNum = getIntent().getExtras().getString(PhoneNum);
        userName = getIntent().getExtras().getString(UserName);
        passWord = getIntent().getExtras().getString(PassWord);

        //显示默认的账号和密码
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(passWord)) {
            binding.registsubmitUsernameEdt.setText(userName);
            binding.registsubmitPwdEdt.setText(passWord);
            binding.registsubmitPwdEdt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            binding.registerTip.setText("这是默认的用户名，密码是手机号后6位，您可以修改。");
        }
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

    void clickSubmit() {
        String userName = binding.registsubmitUsernameEdt.getText().toString();
        String passWord = binding.registsubmitPwdEdt.getText().toString();
//        String phoneNum = getIntent().getExtras().getString("phoneNumb");
        if (verification(userName, passWord)) {
            mLoadingDialog.show();
            mPresenter.register(userName,  passWord, phoneNum);
            showToast(getString(R.string.register_operating));
        }
    }

    private boolean verification(String userName, String passWord) {
        if (!checkUsernameLength(userName)) {
            hideKeyBoard();
            ToastUtil.show(this,getString(R.string.register_check_username_1));
            return false;
        }
        if (!checkPasswordLength(passWord)) {
            hideKeyBoard();
            ToastUtil.show(this,getString(R.string.register_check_pwd_1));
            return false;
        }
        return true;
    }

    private boolean checkUsernameLength(String username) {
        return (username.length() >= 3 && username.length() <= 15);
    }

    private boolean checkPasswordLength(String userPwd) {
        return (userPwd.length() >= 6 || userPwd.length() <= 20);
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startUploadImageActivity() {
//        Intent intent = new Intent(this, UploadImageActivity.class);
//        startActivity(intent);
        Intent intent = new Intent(this, ImproveUserActivity.class);
        intent.putExtra("register", true);
        startActivity(intent);
    }
    @Override
    public void startLoginActivity() {
        NewLoginUtil.startToLogin(this);
    }

    @Override
    public void finishRegisterActivity() {
        EventBus.getDefault().post(new LoginEvent());
        finish();
    }

    @Override
    public void dismissWaitingDialog() {
        mLoadingDialog.dismiss();
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
}
