package com.iyuba.talkshow.ui.user.register.email;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.databinding.ActivityRegisterBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.user.image.UploadImageActivity;
import com.iyuba.talkshow.ui.user.register.phone.RegisterByPhoneActivity;
import com.iyuba.talkshow.ui.web.WebActivity;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.talkshow.util.Util;
import com.umeng.analytics.MobclickAgent;

import javax.inject.Inject;


public class RegisterActivity extends BaseActivity implements RegisterMvpView {
    @Inject
    RegisterPresenter mPresenter;

    LoadingDialog mLoadingDialog;

    private final boolean send = false;
    private boolean isChecked;
    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activityComponent().inject(this);
        setSupportActionBar(binding.registerToolbar.listToolbar);
        setupProtocol();
        mPresenter.attachView(this);
        setClickListener();
        mLoadingDialog = new LoadingDialog(this);
    }

    private void setupProtocol() {
        setTextDrawable(binding.registerProtocolTv,R.drawable.checkbox_unchecked,50,50);
        String remindString = getResources().getString(R.string.read_agree_policy);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(remindString);
        spannableStringBuilder.setSpan(clickableSpan, remindString.indexOf("使用条款和隐私政策"), remindString.indexOf("使用条款和隐私政策") + 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.registerProtocolTv.setText(spannableStringBuilder);
        binding.registerProtocolTv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    ClickableSpan clickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            Intent intent = WebActivity.buildIntent(mContext, App.Url.PROTOCOL_URL+ App.APP_NAME_CH, "用户隐私协议");
            mContext.startActivity(intent);
//            new GridView().setAdapter(new SimpleAdapter());
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.colorPrimary));
            ds.setUnderlineText(true);
        }
    };


    public void setTextDrawable(TextView textView, int resId, int width, int height) {
        Drawable drawable = ContextCompat.getDrawable(this, resId);
        drawable.setBounds(0, 0, width, height);
        textView.setCompoundDrawables(drawable, null, null, null);
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

//    @OnClick(R.id.register_protocol_tv)
    public void setChecked(){
        isChecked = !isChecked;
        if (isChecked){
            setTextDrawable(binding.registerProtocolTv,R.drawable.checkbox_checked,50,50);
        }else {
            setTextDrawable(binding.registerProtocolTv,R.drawable.checkbox_unchecked,50,50);
        }
    }

//    @OnClick(R.id.register_submit_btn)
    void clickSubmit() {
        if (isChecked){
            String username = binding.registerUsernameEdt.getText().toString();
            String password = binding.registerPwdEdt.getText().toString();
            String repeatPwd = binding.registerRepeatPwdEdt.getText().toString();
            String email = binding.registerEmailEdt.getText().toString();
            if (verification(username, password, repeatPwd, email)) {
                mPresenter.register(username, password, email);
            }
        }else {
            ToastUtil.showToast(mContext, "请同意~");
        }

    }

//    @OnClick(R.id.register_by_phone_link)
    void clickToPhone() {
        Intent intent = new Intent();
        intent.setClass(RegisterActivity.this, RegisterByPhoneActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void setClickListener(){
        binding.registerByPhoneLink.setOnClickListener(v -> clickToPhone());
        binding.registerSubmitBtn.setOnClickListener(v -> clickSubmit());
        binding.registerProtocolTv.setOnClickListener(v -> setChecked());
    }

    private boolean verification(String username, String userpwd, String repeatpwd, String email) {
        if (!checkUsername(username)) {
            binding.registerUsernameEdt.setError(getString(R.string.register_check_username_1));
            return false;
        }
        if (!checkUserPwd(userpwd)) {
            binding.registerPwdEdt.setError(getResources()
                    .getString(R.string.register_check_pwd_1));
            return false;
        }
        if (!repeatpwd.equals(userpwd)) {
            binding.registerRepeatPwdEdt.setError(getString(R.string.register_check_pwd_diff));
            return false;
        }
        if (!Util.emailCheck(email)) {
            binding.registerEmailEdt.setError(getString(R.string.register_check_email_2));
            return false;
        }
        return true;
    }

    private boolean checkUsername(String username) {
        return (username.length() >= 3 && username.length() <= 15);
    }

    private boolean checkUserPwd(String userPwd) {
        return (userPwd.length() >= 6 && userPwd.length() <= 20);
    }

    @Override
    public void startUploadImageActivity() {
        Intent intent = new Intent(this, UploadImageActivity.class);
        startActivity(intent);
    }

    @Override
    public void finishRegisterActivity() {
        finish();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showWaitingDialog() {
        mLoadingDialog.show();
    }

    @Override
    public void dismissWaitingDialog() {
        mLoadingDialog.dismiss();
    }
}
