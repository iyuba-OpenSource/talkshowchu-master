package com.iyuba.talkshow.ui.welcome;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.manager.AbilityControlManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.databinding.ActivityWelcomeBinding;
import com.iyuba.talkshow.event.HelpEvent;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.AdLogUtil;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.spread.AdSpreadShowManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.spread.AdSpreadViewBean;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.upload.AdUploadManager;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newce.ContainActivity;
import com.iyuba.talkshow.newce.ContianerActivity;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.courses.coursechoose.TypeHelper;
import com.iyuba.talkshow.ui.help.HelpUseActivity;
import com.iyuba.talkshow.ui.web.WebActivity;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.utils.LibRxTimer;
import com.umeng.analytics.MobclickAgent;
import com.youdao.sdk.nativeads.YouDaoNative;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;


public class WelcomeActivity extends BaseActivity implements WelcomeMvpView {

    private static final double RATIO = 0.86;
    @Inject
    ConfigManager configManager;
    @Inject
    WelcomePresenter mPresenter;
    private volatile int count_num = 5;
    private static final int HELP_CODE = 11;

    ScaleAnimation scaleAnimation;
    private YouDaoNative youdaoNative;
    private boolean onActivityStarted = false;

    //布局样式
    private ActivityWelcomeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_welcome);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityWelcomeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        initAnimation();
        activityComponent().inject(this);
        mPresenter.attachView(this);

    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mPresenter.checkDbUpgrade();
        if (App.APP_TENCENT_PRIVACY) {
            showFirstDialog();
        } else {
            setAdData();
        }

        //增加人教版审核检查
        if (ConfigData.renVerifyCheck){
            mPresenter.verifyCheck();
        }else {
            AbilityControlManager.getInstance().setLimitPep(false);
        }

        //增加微课审核检查
        if (ConfigData.mocVerifyCheck){
            mPresenter.verifyMoc();
        }else {
            AbilityControlManager.getInstance().setLimitMoc(false);
        }

        //增加小说审核检查
        if (ConfigData.novelVerifyCheck){
            mPresenter.verifyNovel();
        }else {
            AbilityControlManager.getInstance().setLimitNovel(false);
        }

        //增加视频审核检查
        if (ConfigData.videoVerifyCheck){
            mPresenter.verifyVideo();
        }else {
            AbilityControlManager.getInstance().setLimitVideo(false);
        }

        onActivityStarted = getIntent().getBooleanExtra("onActivityStarted", false);
        if (!onActivityStarted) {
            //这里使用广告工具控制
            if (AdBlocker.getInstance().shouldBlockAd()){
                //重置数据
                mPresenter.setSendFlag(0);
                return;
            }

            //保存app启动次数，一定次数后弹出好评送书弹框
            int newInt = mPresenter.getSendFlag() + 1;
            mPresenter.setSendFlag(newInt);
            Log.e("WelcomeActivity", "onActivityStarted newInt = " + newInt);
        }
    }

    private void setAdData() {
        mPresenter.setDefaultWeb();

        //设置广告显示
        loadAd();
    }


    private void showFirstDialog() {
        boolean isFirst = configManager.isFirstStart();
        Log.e("WelcomeActivity", "showFirstDialog isFirst " + isFirst);
        if (isFirst) {
            int bookId = App.getBookDefaultShowData().getBookId();
            String bookName = App.getBookDefaultShowData().getBookName();

            configManager.putCourseId(bookId);
            configManager.putWordId(bookId);

            configManager.putCourseTitle(bookName);
            configManager.putWordTitle(bookName);

            configManager.putKouId(bookId);
            configManager.putKouTitle(bookName);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NotNull View widget) {
                    Intent intent = WebActivity.buildIntent(mContext, App.Url.PROTOCOL_URL + App.APP_NAME_CH, App.APP_NAME_PRIVACY);
                    mContext.startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(getResources().getColor(R.color.colorPrimary));
                    ds.setUnderlineText(true);
                }
            };
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_privacy, null);
            TextView remindText = view.findViewById(R.id.remindText);
            String remindString = getResources().getString(R.string.user_protocol);
            if (App.APP_CHECK_PERMISSION) {
                remindString = getResources().getString(R.string.user_permission_protocol);
            }
            if (App.APP_MINI_PRIVACY) {
                remindString = getResources().getString(R.string.user_mini_protocol);
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(remindString);
            if (App.APP_NAME_PRIVACY.equalsIgnoreCase("隐私政策")) {
                spannableStringBuilder.setSpan(clickableSpan, remindString.indexOf(App.APP_NAME_PRIVACY), remindString.indexOf(App.APP_NAME_PRIVACY) + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ClickableSpan clickableUsage = new ClickableSpan() {
                    @Override
                    public void onClick(@NotNull View widget) {
                        Intent intent = WebActivity.buildIntent(mContext, App.Url.PROTOCOL_USAGE + App.APP_NAME_CH, "用户协议");
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(getResources().getColor(R.color.colorPrimary));
                        ds.setUnderlineText(true);
                    }
                };
                spannableStringBuilder.setSpan(clickableUsage, remindString.indexOf("用户协议"), remindString.indexOf("用户协议") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                spannableStringBuilder.setSpan(clickableSpan, remindString.indexOf("用户协议"), remindString.indexOf("用户协议") + 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (App.APP_MINI_PRIVACY) {
                // 友盟
                ClickableSpan clickUmeng = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = WebActivity.buildIntent(mContext, "https://www.umeng.com/page/policy", "隐私政策");
                        mContext.startActivity(intent);
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(getResources().getColor(R.color.colorPrimary));
                        ds.setUnderlineText(true);
                    }
                };
                spannableStringBuilder.setSpan(clickUmeng, remindString.indexOf("友盟隐私权政策链接"), remindString.indexOf("友盟隐私权政策链接") + 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // Mob
                ClickableSpan clickMob = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = WebActivity.buildIntent(mContext, "https://www.mob.com/about/policy", "隐私政策");
                        mContext.startActivity(intent);
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(getResources().getColor(R.color.colorPrimary));
                        ds.setUnderlineText(true);
                    }
                };
                spannableStringBuilder.setSpan(clickMob, remindString.indexOf("相关隐私权政策链接"), remindString.indexOf("相关隐私权政策链接") + 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            remindText.setText(spannableStringBuilder);
            remindText.setMovementMethod(LinkMovementMethod.getInstance());

            AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("个人信息保护政策")
                    .setView(view)
                    .setCancelable(false)
                    .create();
            dialog.show();
            TextView agreeNo = view.findViewById(R.id.text_no_agree);
            TextView agree = view.findViewById(R.id.text_agree);
            if (App.APP_CHECK_AGREE) {
                agreeNo.setText("不同意功能受限");
            }
            agreeNo.setOnClickListener(v -> {
                dialog.dismiss();
                if (App.APP_CHECK_AGREE) {
                    configManager.setFirstStart(false);
                    configManager.setCheckAgree(false);
                    mPresenter.setDefaultWeb();

                    if (configManager.isFirstHelp()) {
                        count_num = 10;
                        startHelpUseActivity(true);
                    }
                } else {
                    finishActivity();
                }
            });
            agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    configManager.setFirstStart(false);
                    configManager.setCheckAgree(true);

                    mPresenter.setDefaultWeb();
                    TalkShowApplication.initUMMob();


                    // TODO: 2024/3/29 展姐在中小学群里要求去掉引导页跳转
                    /*if (configManager.isFirstHelp()) {
                        count_num = 10;
                        startHelpUseActivity(true);
                    }*/
                    configManager.setFirstHelp(false);
                    skip();
                }
            });
        } else {
            if (App.APP_CHECK_AGREE) {
                if (configManager.isCheckAgree()) {
                    TalkShowApplication.initUMMob();
                }
            } else {
                TalkShowApplication.initUMMob();
            }

            setAdData();
        }
    }

    private void initAnimation() {
        scaleAnimation = new ScaleAnimation(1, 1.2f, 1, 1.2f, 0.5f, 0.5f);
        scaleAnimation.setDuration(3600);
        scaleAnimation.setRepeatCount(1);
    }

    @Override
    public boolean isSwipeBackEnable() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

        //这里比较奇怪，微信小程序登录后总是跳转到欢迎界面，这里监测到顶部是欢迎界面时直接finish掉
        //这里是个临时的处理方式，以后需要查找下问题的根源
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            String topClassName = "";
            String baseClassName = "";
            if (manager.getAppTasks()!=null&&manager.getAppTasks().size()>0){
                topClassName = manager.getAppTasks().get(0).getTaskInfo().topActivity.getClassName();
                baseClassName = manager.getAppTasks().get(0).getTaskInfo().baseActivity.getClassName();
            }
            if (topClassName.equals(WelcomeActivity.class.getName())
                    &&baseClassName.equals(ContianerActivity.class.getName())){
                finish();
            }
        }

        //点击广告返回后直接跳转
        if (isClickAd){
            isClickAd = false;
            skip();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        //关闭计时器
        closeTimer();
        //关闭广告
        AdSpreadShowManager.getInstance().stopSpreadAd();

        mPresenter.detachView();
    }

    void skip() {
        closeTimer();
        if ("初中英语背单词".equals(App.APP_NAME_CH)) {
            startActivity(new Intent(this, ContainActivity.class));
        } else {
            startActivity(new Intent(this, ContianerActivity.class));
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode != KeyEvent.KEYCODE_BACK && super.onKeyDown(keyCode, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HelpEvent event) {
        if (configManager.isFirstHelp()) {
            configManager.setFirstHelp(false);
        }
        if (event.state == 0) {
            Log.e("WelcomeActivity", "HelpEvent isFirstHelp = " + configManager.isFirstHelp());
            if (count_num > 1) {
                count_num = 1;
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("WelcomeActivity", "postDelayed isFirstHelp = " + configManager.isFirstHelp());
                    try {
                        finishActivity(HELP_CODE);
                    } catch (Exception var2) { }
                    finish();
                }
            }, 3000);
        }
    }

    @Override
    public void startHelpUseActivity(boolean flag) {
        Intent intent = HelpUseActivity.buildIntent(this, flag);
        startActivityForResult(intent, HELP_CODE);
    }

    @Override
    public void finishActivity() {
        finish();
    }


    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    /********************************无广告的计时器操作***************************/
    //计时器名称
    private static final String timer_adShow = "adShowTimer";
    //默认倒计时
    private int adTime = 5;

    private void openTimer(){
        closeTimer();

        LibRxTimer.getInstance().multiTimerInMain(timer_adShow,0, 1000L, number -> {
            AdLogUtil.showDebug("广告显示", "倒计时--"+number);

            long time = adTime - number;
            binding.adSkip.setText(String.format("跳过(%1$s秒)",time));

            if (time<=0){
                closeTimer();
                startActivity(new Intent(this, ContianerActivity.class));
                finish();
            }
        });
    }

    private void closeTimer(){
        LibRxTimer.getInstance().cancelTimer(timer_adShow);
    }

    /*****************************新的开屏广告操作************************/
    //开屏广告接口是否完成
    private boolean isSplashAdLoaded = false;
    //是否已经点击了广告
    private boolean isClickAd = false;
    //是否已经获取了奖励
    private boolean isGetRewardByClickAd = false;
    //广告倒计时时间
    private static final int AdDownTime = 5;
    //操作倒计时时间
    private static final int OperateTime = 5;
    //界面数据
    private AdSpreadViewBean spreadViewBean = null;

    //展示广告
    private void showSpreadAd(){
        if (spreadViewBean==null){
            spreadViewBean = new AdSpreadViewBean(binding.adImage, binding.adSkip, binding.adTips, binding.adLayout, new AdSpreadShowManager.OnAdSpreadShowListener() {
                @Override
                public void onLoadFinishAd() {
                    isSplashAdLoaded = true;
                    AdSpreadShowManager.getInstance().stopOperateTimer();
                }

                @Override
                public void onAdShow(String adType) {

                }

                @Override
                public void onAdClick(String adType, boolean isJumpByUserClick, String jumpUrl) {
                    if (isJumpByUserClick){
                        if (TextUtils.isEmpty(jumpUrl)){
                            ToastUtil.showToast(WelcomeActivity.this, "暂无内容");
                            return;
                        }

                        //设置点击
                        isClickAd = true;
                        //关闭计时器
                        AdSpreadShowManager.getInstance().stopAdTimer();
                        //跳转界面
                        Intent intent = new Intent();
                        intent.setClass(mContext, WebActivity.class);
                        intent.putExtra("url", jumpUrl);
                        startActivity(intent);
                    }

                    //点击广告获取奖励
                    if (!isGetRewardByClickAd){
                        isGetRewardByClickAd = true;


                        String fixShowType = AdShowUtil.NetParam.AdShowPosition.show_spread;
                        String fixAdType = adType;
                        AdUploadManager.getInstance().clickAdForReward(fixShowType, fixAdType, new AdUploadManager.OnAdClickCallBackListener() {
                            @Override
                            public void showClickAdResult(boolean isSuccess, String showMsg) {
                                //直接显示信息即可
                                ToastUtil.showToast(WelcomeActivity.this, showMsg);

                                if (isSuccess) {
                                    EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
                                }
                            }
                        });
                    }
                }

                @Override
                public void onAdClose(String adType) {
                    //关闭广告
                    AdSpreadShowManager.getInstance().stopSpreadAd();
                    //跳出
                    skip();
                }

                @Override
                public void onAdError(String adType) {

                }

                @Override
                public void onAdShowTime(boolean isEnd, int lastTime) {
                    if (isEnd){
                        //跳转
                        skip();
                    }else {
                        //开启广告计时器
                        binding.adSkip.setText("跳过("+lastTime+"s)");
                    }
                }

                @Override
                public void onOperateTime(boolean isEnd, int lastTime) {
                    if (isEnd){
                        //跳转到下一个
                        skip();
                        return;
                    }

                    if (isSplashAdLoaded){
                        AdSpreadShowManager.getInstance().stopOperateTimer();
                        return;
                    }

                    AdLogUtil.showDebug(AdSpreadShowManager.TAG,"操作定时器时间--"+lastTime);
                }
            },AdDownTime,OperateTime);
            AdSpreadShowManager.getInstance().setShowData(this,spreadViewBean);
        }
        AdSpreadShowManager.getInstance().showSpreadAd();
    }

    //加载广告
    private void loadAd(){
        if (!AdBlocker.getInstance().shouldBlockAd() && !UserInfoManager.getInstance().isVip()){
            showSpreadAd();
        }else {
            binding.adSkip.setVisibility(View.VISIBLE);
            binding.adSkip.setOnClickListener(view->{
                skip();
            });
            openTimer();
        }
    }
}