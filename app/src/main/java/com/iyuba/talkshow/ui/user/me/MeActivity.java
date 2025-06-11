package com.iyuba.talkshow.ui.user.me;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.iyuba.talkshow.BuildConfig;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.databinding.ActivityMeBinding;
import com.iyuba.talkshow.event.LoginOutEvent;
import com.iyuba.talkshow.lil.help_fix.ui.collect.chapter.ChapterCollectActivity;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.deletlesson.LessonDeleteActivity;
import com.iyuba.talkshow.ui.user.download.DownloadActivity;
import com.iyuba.talkshow.ui.user.me.dubbing.MyDubbingActivity;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.talkshow.ui.web.WebActivity;
import com.iyuba.talkshow.ui.words.WordNoteActivity;
import com.iyuba.talkshow.util.MD5;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;
import personal.iyuba.personalhomelibrary.ui.message.MessageActivity;

public class MeActivity extends BaseActivity implements MeMvpView {

    @Inject
    MePresenter mPresenter;

    //布局样式
    private ActivityMeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar.listToolbar);
        activityComponent().inject(this);
        mPresenter.attachView(this);
        setClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(mContext);
        /*Glide.with(mContext)
                .load(mPresenter.getUserImageUrl())
                .asBitmap()
                .signature(new StringSignature(System.currentTimeMillis()+""))
                .transform(new CircleTransform(mContext))
                .placeholder(R.drawable.default_avatar)
                .into(binding.meUserImage);*/
        Glide3Util.loadCircleImg(mContext,mPresenter.getUserImageUrl(),R.drawable.default_avatar,binding.meUserImage);
        if (UserInfoManager.getInstance().isLogin()) {
            binding.meUsernameTv.setText(UserInfoManager.getInstance().getUserName());
            binding.meUserinfoContainer.setVisibility(View.VISIBLE);
            binding.meUnloginRl.setVisibility(View.GONE);
            binding.meLogoutBtn.setVisibility(View.VISIBLE);
            binding.meClearUserBtn.setVisibility(View.VISIBLE);
        } else {
            binding.meUsernameTv.setText("未登录");
            binding.meUserinfoContainer.setVisibility(View.GONE);
            binding.meUnloginRl.setVisibility(View.VISIBLE);
            binding.meLogoutBtn.setVisibility(View.GONE);
            binding.meClearUserBtn.setVisibility(View.GONE);
        }
        if (UserInfoManager.getInstance().isVip()) {
            binding.meVipstateIv.setImageResource(R.drawable.vip);
        } else {
            binding.meVipstateIv.setImageResource(R.drawable.no_vip);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);
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

    void clickUser() {
        startActivity(PersonalHomeActivity.buildIntent (this,
                Integer.parseInt(String.valueOf(UserInfoManager.getInstance().getUserId())),
                UserInfoManager.getInstance().getUserName(), 0));
    }

    void clickMessage(){
        startActivity(new Intent(this, MessageActivity.class));//需要登录

    }

    void clickDownload() {
        Intent intent = new Intent(mContext, DownloadActivity.class);
        startActivity(intent);
    }

    void clickCollect() {
//        Intent intent = new Intent(mContext, CollectionActivity.class);
//        startActivity(intent);
        ChapterCollectActivity.start(this);
    }

    void clickDubbing() {
        Intent intent = new Intent(mContext, MyDubbingActivity.class);
        startActivity(intent);
    }

    void startMyWors(){
        WordNoteActivity.start(this);
    }

    void integralClick() {
        String url = "http://m."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"mall/index.jsp?"
                + "&uid=" + UserInfoManager.getInstance().getUserId()
                + "&sign=" + MD5.getMD5ofStr("iyuba" + UserInfoManager.getInstance().getUserId() + "camstory")
                + "&username=" + UserInfoManager.getInstance().getUserName()
                + "&platform=android&appid=" + App.APP_ID;

        startActivity(WebActivity.buildIntent(mContext, url,
                "积分明细",
                "http://api."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"credits/useractionrecordmobileList1.jsp?uid=" + UserInfoManager.getInstance().getUserId())
        );
    }

    void clickLogin() {
        NewLoginUtil.startToLogin(this);
    }

    void clickLogout() {
        new AlertDialog.Builder(mContext).setTitle(getString(R.string.alert_title))
                .setMessage(getString(R.string.logout_alert))
                .setPositiveButton(getString(R.string.alert_btn_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
//                                mPresenter.loginOut();
                                UserInfoManager.getInstance().clearUserInfo();
                                showToast(R.string.login_out_success);
                                EventBus.getDefault().post(new LoginOutEvent());
                                finish();
                            }
                        })
                .setNeutralButton(getString(R.string.alert_btn_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
    }

    void clickClearUser() {
        new AlertDialog.Builder(mContext).setTitle(getString(R.string.alert_title))
                .setMessage(getString(R.string.clear_user_alert))
                .setPositiveButton(getString(R.string.alert_btn_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mPresenter.clearUser();
//                                showToast(R.string.login_out_success);
                                EventBus.getDefault().post(new LoginOutEvent());
                                finish();
                            }
                        })
                .setNeutralButton(getString(R.string.alert_btn_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
    }

    void clickVIP() {
        startActivity(new Intent(mContext, NewVipCenterActivity.class));
    }

    public void showToast(int resId) {
        Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
    }


    void setClick(){
        binding.meVipRl.setOnClickListener(v -> clickVIP());
        binding.meClearUserBtn.setOnClickListener(v -> clickClearUser());
        binding.meLogoutBtn.setOnClickListener(v -> clickLogout());
        binding.meLoginBtn.setOnClickListener(v -> clickLogin());
        binding.integral.setOnClickListener(v -> integralClick());
        binding.meWordsRl.setOnClickListener(v -> startMyWors());
        binding.meDownloadBooksRl.setOnClickListener(v -> startDeleteDownloads());
        //  口语秀不显示课本 ， 小学 初中 显示课本
        if (BuildConfig.APPLICATION_ID.contains("xiaoxue") || BuildConfig.APPLICATION_ID.contains("childenglish") ||
                BuildConfig.APPLICATION_ID.contains("talkshow.junior") || BuildConfig.APPLICATION_ID.contains("primarypro") ||
                BuildConfig.APPLICATION_ID.contains("juniorenglish") || BuildConfig.APPLICATION_ID.contains("primaryenglish")) {
            binding.meDownloadBooksRl.setVisibility(View.VISIBLE);
        }else {
            binding.meDownloadBooksRl.setVisibility(View.GONE);
        }
        binding.meDubbingRl.setOnClickListener(v -> clickDubbing());
        binding.meCollectRl.setOnClickListener(v -> clickCollect());
        binding.meDownloadRl.setOnClickListener(v -> clickDownload());
        binding.meMessage.setOnClickListener(v -> clickMessage());
        binding.meUserImage.setOnClickListener(v -> clickUser());
        binding.userProtocl.setOnClickListener(v -> clickProtocol());
    }

    private void clickProtocol() {
        Intent intent = WebActivity.buildIntent(mContext, App.Url.PROTOCOL_URL+ App.APP_NAME_CH, "用户隐私协议");
        mContext.startActivity(intent);
    }

    private void startDeleteDownloads() {
        LessonDeleteActivity.start(this);
    }
}
