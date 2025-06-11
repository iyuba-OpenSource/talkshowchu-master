package com.iyuba.talkshow.ui.feedback;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.databinding.ActivityFeedbackBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.Util;
import com.umeng.analytics.MobclickAgent;

import javax.inject.Inject;


public class FeedbackActivity extends BaseActivity implements FeedBackMvpView {

    @Inject
    FeedbackPresenter mPresenter;

    ActivityFeedbackBinding binding ;

    LoadingDialog mDialogLoading;

    @Inject
    ConfigManager configManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activityComponent().inject(this);
        mPresenter.attachView(this);
        setSupportActionBar(binding.feedbackToolbar.listToolbar);
        mDialogLoading = new LoadingDialog(this);
        binding.feedbackSubmitBtn.setOnClickListener(v -> onClickSubmit());

        //隐藏爱语吧logo
        binding.logoIv.setVisibility(View.INVISIBLE);
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
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    public void onClickSubmit() {
        String content = binding.feedbackSuggestionEdittext.getText().toString().trim();
        String email = binding.feedbackContactEdittext.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            showToastShort("请输入反馈的内容");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            showToastShort(getString(R.string.register_check_email_1));
            return;
        }
        if (!Util.emailCheck(email)) {
            showToastShort(getString(R.string.register_check_email_2));
            return;
        }
        mPresenter.submit(content, email);
    }

    @Override
    public void setEdtEmailError(int resId) {
        binding.feedbackContactEdittext.setError(getString(resId));
    }

    @Override
    public void setEdtContentError(int resId) {
        binding.feedbackSuggestionEdittext.setError(getString(resId));
    }

    @Override
    public void showWaitingDialog() {
        mDialogLoading.show();
    }

    @Override
    public void dismissWaitingDialog() {
        mDialogLoading.dismiss();
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDialog() {
        new AlertDialog.Builder(FeedbackActivity.this)
                .setTitle(R.string.feedback_submit_success)
                .setNeutralButton(R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                }).show();
    }
}
