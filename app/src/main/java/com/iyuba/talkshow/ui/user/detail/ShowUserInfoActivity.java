package com.iyuba.talkshow.ui.user.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.result.GetUserBasicInfoResponse;
import com.iyuba.talkshow.data.remote.UserService;
import com.iyuba.talkshow.databinding.ActivityShowUserInfoBinding;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.user.edit.EditUserInfoActivity;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.umeng.analytics.MobclickAgent;

import javax.inject.Inject;


public class ShowUserInfoActivity extends BaseActivity implements ShowUserInfoMvpView {
    @Inject
    ShowUserInfoPresenter mPresenter;

    LoadingDialog mLoadingDialog;

    private GetUserBasicInfoResponse mResponse;
    ActivityShowUserInfoBinding binding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activityComponent().inject(this);
        mPresenter.attachView(this);
        setSupportActionBar(binding.toolbar.listToolbar);
        mLoadingDialog = new LoadingDialog(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
        mPresenter.getUserBasicInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.userinfo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.userinfomenu_edit:
                if (mResponse != null) {
                    Intent intent = EditUserInfoActivity.buildIntent(this, mResponse);
                    startActivity(intent);
                }
                break;
        }
        return true;
    }

    @Override
    public void setUserInfo(GetUserBasicInfoResponse response) {
        mResponse = response;

        binding.usernameTv.setText(UserInfoManager.getInstance().getUserName());
        setGender(response.gender());
        binding.locationTv.setText(response.resideLocation());
        binding.birthdayTv.setText(response.birthday());
        binding.constellationTv.setText(response.constellation());
        binding.zodiacTv.setText(response.zodiac());
        binding.bioTv.setText(response.graduateSchool());
        binding.companyTv.setText(response.company());
        binding.affectiveStatusTv.setText(response.affectiveStatus());
        binding.lookingForTv.setText(response.lookingFor());
        binding.bioTv.setText(response.bio());
        binding.interestTv.setText(response.interest());
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismissWaitingDialog() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void finishActivity() {
        finish();
    }

    public void setGender(String genderCode) {
        switch (genderCode) {
            case UserService.GetUserBasicInfo.Result.Code.MALE:
                binding.genderTv.setText(UserService.GetUserBasicInfo.Result.Message.MALE);
                break;
            case UserService.GetUserBasicInfo.Result.Code.FEMALE:
                binding.genderTv.setText(UserService.GetUserBasicInfo.Result.Message.FEMALE);
                break;
            case UserService.GetUserBasicInfo.Result.Code.SECRET:
                binding.genderTv.setText(UserService.GetUserBasicInfo.Result.Message.SECRET);
                break;
        }
    }

}
