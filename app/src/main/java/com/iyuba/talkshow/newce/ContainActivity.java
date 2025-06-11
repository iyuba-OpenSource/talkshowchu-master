package com.iyuba.talkshow.newce;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.headlinelibrary.event.HeadlineGoVIPEvent;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivityNew;
import com.iyuba.headlinelibrary.ui.content.TextContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivityNew;
import com.iyuba.imooclib.event.ImoocBuyVIPEvent;
import com.iyuba.imooclib.ui.mobclass.MobClassActivity;
import com.iyuba.module.dl.BasicDLPart;
import com.iyuba.module.dl.DLItemEvent;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.manager.VersionManager;
import com.iyuba.talkshow.data.model.User;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.event.MyBookEvent;
import com.iyuba.talkshow.event.RefreshWordEvent;
import com.iyuba.talkshow.event.SelectBookEvent;
import com.iyuba.talkshow.event.WordStepEvent;
import com.iyuba.talkshow.event.WxLoginEvent;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.TempDataManager;
import com.iyuba.talkshow.lil.help_mvp.util.SPUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newce.kouyu.KouyuFragment;
import com.iyuba.talkshow.newce.me.MeFragment;
import com.iyuba.talkshow.newce.wordstep.WordstepFragment;
import com.iyuba.talkshow.newdata.Config;
import com.iyuba.talkshow.newdata.SPconfig;
import com.iyuba.talkshow.newview.SendBookPop;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.user.login.fix.FixLoginSession;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.talkshow.ui.web.WebActivity;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.ui.words.WordNoteFragment;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.data.LoginType;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.event.GetSoundEvent;
import com.iyuba.wordtest.manager.WordConfigManager;
import com.iyuba.wordtest.manager.WordManager;
import com.mob.secverify.PreVerifyCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.common.exception.VerifyException;
import com.roughike.bottombar.OnTabSelectListener;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.codec.binary.Base64;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.inject.Inject;

import personal.iyuba.personalhomelibrary.data.model.HeadlineTopCategory;
import personal.iyuba.personalhomelibrary.data.model.Voa;
import personal.iyuba.personalhomelibrary.event.ArtDataSkipEvent;

/**
 * Created by carl shen on 2020/7/31
 * New Primary English, new study experience.
 */

//@RuntimePermissions
public class ContainActivity extends BaseActivity implements ContainerMvpView {
    public static final String TAG = "ContianerActivity";
    private static final String VERSION_CODE = "versionCode";
    private static final String APP_URL = "appUrl";
    @Inject
    VersionManager mVersionManager;
    @Inject
    ContainerPresenter mPresenter;
    private final String WORD = "WORD";
    private final String ME = "ME";
    private final String VIDEO = "video";
//    private InitPush mInitPush;
    @Inject
    ConfigManager configManager;
    private BaseFragment mMobFragment;
    private WordstepFragment mPassFragment;
    private MeFragment mMeFragment;
    private boolean isExit;

    private ProgressBar aboutDownloadProgressbar;
    private SendBookPop sendBookPop;
    private LoadingDialog mLoadingDialog;
    //修改底部样式
//    private BottomBar bottomBar;
    private LinearLayout llWord;
    private ImageView ivWord;
    private TextView tvWord;
    private LinearLayout llTalk;
    private ImageView ivTalk;
    private TextView tvTalk;
    private LinearLayout llMe;
    private ImageView ivMe;
    private TextView tvMe;

    public void showLoadingDialog() {
        if ((mLoadingDialog == null) || !mLoadingDialog.isShowing()) {
            mLoadingDialog = new LoadingDialog(mContext);
            mLoadingDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean isSwipeBackEnable() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contain);
//        bottomBar = findViewById(R.id.bottom_bar);
        llWord = findViewById(R.id.ll_word);
        llWord.setOnClickListener(onClickListener);
        ivWord = findViewById(R.id.iv_word);
        tvWord = findViewById(R.id.tv_word);
        llTalk = findViewById(R.id.ll_talk);
        llTalk.setOnClickListener(onClickListener);
        ivTalk = findViewById(R.id.iv_talk);
        tvTalk = findViewById(R.id.tv_talk);
        llMe = findViewById(R.id.ll_me);
        llMe.setOnClickListener(onClickListener);
        ivMe = findViewById(R.id.iv_me);
        tvMe = findViewById(R.id.tv_me);

        aboutDownloadProgressbar = findViewById(R.id.about_download_progressbar);
        activityComponent().inject(this);
        mPresenter.attachView(this);
        if (App.APP_WORD_BOTTOM) {
//            bottomBar.getTabWithId(R.id.tab_talk).setTitle("生词本");
//            bottomBar.getTabWithId(R.id.tab_talk).setIconResId(R.mipmap.ic_score);
            llTalk.setVisibility(View.VISIBLE);
            tvTalk.setText("生词本");
            ivTalk.setBackgroundResource(R.drawable.selector_bottom_note);
        }
        initFragment();
        //关闭好评送书
//        if (!configManager.isSendBook() && configManager.getSendFlag() == 20) {
//            findViewById(R.id.container).postDelayed(() -> {
//                sendBookPop = new SendBookPop(this, findViewById(R.id.container));
//            }, 500);
//            configManager.setSendFlag(0);
//        }


        //预加载秒验
        preloadMobVerify();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        EventBus.getDefault().register(this);
//        bottomBar.setOnTabSelectListener(mTabSelectListener);
        // init person home, word, etc.

        //加载所有数据
        loadAllData();
    }

    //加载所有数据
    private void loadAllData(){
        //加载单词数据(暂时不使用这个功能了)
        /*int wordLoad = WordConfigManager.Instance(mContext).loadInt(WordConfigManager.WORD_DB_LOADED, 0);
        if (wordLoad == 0) {
            showLoadingDialog();
            TalkShowApplication.getSubHandler().post(() -> {
                boolean result = WordDataBase.getInstance(TalkShowApplication.getContext()).loadDbData();
                if (result) {
                    WordConfigManager.Instance(mContext).putInt(WordConfigManager.WORD_DB_LOADED, 1);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                    }
                });
            });
        }*/

        Intent intent = getIntent();
        String versionCode = intent.getStringExtra(VERSION_CODE);
        String appUrl = intent.getStringExtra(APP_URL);
        if (!TextUtils.isEmpty(versionCode) && !TextUtils.isEmpty(appUrl)) {
            aboutDownloadProgressbar.setVisibility(View.VISIBLE);
            mPresenter.downloadApk(versionCode, appUrl);
        } else if (App.APP_CHECK_UPGRADE) {
            long curTime = System.currentTimeMillis();
            long oldTime = SPconfig.Instance().loadLong(Config.VERSION_CHECK);
            if ((curTime - oldTime) > 1000 * 60 * 60 * 24) {
                mVersionManager.checkVersion(callBack);
                SPconfig.Instance().putLong(Config.VERSION_CHECK, curTime);
            }
        }

        initPersonHome();

        //绑定后台服务
//        startBindService();

        //增加进入app获取用户信息的功能
        initUserInfo();

        //预登陆
        preVerify();
    }

    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mPassFragment == null) {
            mPassFragment = WordstepFragment.getInstance();
        }
        transaction.add(R.id.container, mPassFragment, WORD);
        transaction.commit();

        ivWord.setSelected(true);
        tvWord.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void initPersonHome() {
        UserInfoManager.getInstance().initUserInfo();
    }

    private final OnTabSelectListener mTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelected(@IdRes int tabId) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment2 = manager.findFragmentByTag(WORD);
            Fragment fragment5 = manager.findFragmentByTag(VIDEO);
            Fragment fragment4 = manager.findFragmentByTag(ME);
            if (fragment2 != null) {
                mPassFragment = (WordstepFragment) fragment2;
            }
            if (fragment5 != null) {
                if (App.APP_WORD_BOTTOM) {
                    mMobFragment = (WordNoteFragment) fragment5;
                } else {
                    mMobFragment = (KouyuFragment) fragment5;
                }
            }
            if (fragment4 != null) {
                mMeFragment = (MeFragment) fragment4;
            }
            switch (tabId) {
                case R.id.tab_word:
                    if (mPassFragment == null) {
                        mPassFragment = WordstepFragment.getInstance();
                        hideFragments(transaction, mMobFragment,mMeFragment);
                        transaction.add(R.id.container, mPassFragment, WORD);
                    } else {
                        hideFragments(transaction, mMobFragment,mMeFragment);
                        transaction.show(mPassFragment);
                    }
                    break;
                case R.id.tab_talk:
                    if (mMobFragment == null) {
                        if (App.APP_WORD_BOTTOM) {
                            mMobFragment = WordNoteFragment.build();
                        } else {
                            mMobFragment = KouyuFragment.build(21, false);
                        }
                        hideFragments(transaction, mPassFragment, mMeFragment);
                        transaction.add(R.id.container, mMobFragment, VIDEO);
                    } else {
                        hideFragments(transaction, mPassFragment, mMeFragment);
                        transaction.show(mMobFragment);
                        if (App.APP_WORD_BOTTOM) {
                            ((WordNoteFragment) mMobFragment).onClickReload();
                        } else {
                            ((KouyuFragment) mMobFragment).onClickReload();
                        }
                    }
                    break;
                case R.id.tab_me:
                    if (mMeFragment == null) {
                        mMeFragment = new MeFragment();
                        hideFragments(transaction, mMobFragment,mPassFragment);
                        transaction.add(R.id.container, mMeFragment, ME);
                    } else {
                        hideFragments(transaction, mMobFragment,mPassFragment);
                        transaction.show(mMeFragment);
                    }
                    break;
            }
            transaction.commitAllowingStateLoss();
        }

        private void hideFragments(FragmentTransaction transaction, Fragment... fragments) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    transaction.hide(fragment);
                }
            }
        }
    };

    private void showAlertDialog(String msg, DialogInterface.OnClickListener ocl) {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle(R.string.alert_title);
        alert.setMessage(msg);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alert_btn_ok), ocl);
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.alert_btn_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        aboutDownloadProgressbar.setVisibility(View.GONE);
                    }
                });
        alert.show();
    }

    VersionManager.AppUpdateCallBack callBack = new VersionManager.AppUpdateCallBack() {
        @Override
        public void appUpdateSave(final String versionCode, final String appUrl) {
            aboutDownloadProgressbar.setVisibility(View.VISIBLE);
            showAlertDialog(
                    MessageFormat.format(getString(R.string.about_update_alert), versionCode),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPresenter.downloadApk(versionCode, appUrl);
                        }
                    });
        }

        @Override
        public void appUpdateFailed() {
            aboutDownloadProgressbar.setVisibility(View.GONE);
//            showToast(App.APP_NAME_CH + getString(R.string.about_update_isnew));
        }
    };

    @Override
    public void setDownloadProgress(int progress) {
        aboutDownloadProgressbar.setProgress(progress);
    }

    @Override
    public void setDownloadMaxProgress(int maxProgress) {
        aboutDownloadProgressbar.setMax(maxProgress);
    }

    @Override
    public void setProgressVisibility(int visible) {
        aboutDownloadProgressbar.setVisibility(visible);
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event) {
        Log.e("ContainActivity", "onEvent LoginEvent");
        initPersonHome();
        TalkShowApplication.getSubHandler().post(new Runnable() {
            @Override
            public void run() {
                int wordLoad = WordConfigManager.Instance(mContext).loadInt(WordConfigManager.WORD_DB_NEW_LOADED, 0);
                if (wordLoad == 1) {
                    int uidLoad = WordConfigManager.Instance(mContext).loadInt(WordConfigManager.WORD_DB_NEW_LOADED + UserInfoManager.getInstance().getUserId(), 0);
                    Log.e("ContainActivity", "LoginEvent uidLoad " + uidLoad);
                    if (uidLoad == 0) {
                        WordManager.getInstance().init(UserInfoManager.getInstance().getUserName(), String.valueOf(UserInfoManager.getInstance().getUserId()),
                                App.APP_ID, Constant.EVAL_TYPE, UserInfoManager.getInstance().isVip() ? 1 : 0, App.APP_NAME_EN);
                        WordManager.getInstance().migrateData(TalkShowApplication.getContext());
                        EventBus.getDefault().post(new RefreshWordEvent(configManager.getCourseId(), 0));
                    }
                }
            }
        });
    }

    //微课跳转黄金会员购买界面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocBuyVIPEvent event) {
        Log.e("ContainActivity", "onEvent ImoocBuyVIPEvent");
        if (UserInfoManager.getInstance().isLogin()) {
            Intent intent = new Intent(mContext, NewVipCenterActivity.class);
            intent.putExtra(NewVipCenterActivity.HUI_YUAN, NewVipCenterActivity.HUANGJIN);
            startActivity(intent);
        } else {
            NewLoginUtil.startToLogin(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MyBookEvent event) {
        Log.e("ContainActivity", "MyBookEvent event.bookId " + event.bookId);
//        bottomBar.selectTabAtPosition(0);
        updateBottomUI(R.id.ll_word);
        EventBus.getDefault().post(new SelectBookEvent(event.bookId, 0));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WordStepEvent event) {
//        bottomBar.selectTabAtPosition(1);
        updateBottomUI(R.id.ll_talk);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetSoundEvent event) {
        ArrayList<Integer> typeIdFilter = new ArrayList<>();
        typeIdFilter.add(3);
        startActivity(MobClassActivity.buildIntent(mContext, 3, true, typeIdFilter));
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        EventBus.getDefault().unregister(this);

        //关闭后台播放
//        stopBindService();

        super.onDestroy();
    }

    /**
     * 双击返回键退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExit) {
                TalkShowApplication.getInstance().exit();

                //关闭后台播放
//                stopBindService();
            } else {
//                Toast.makeText(this, ResourceUtil.getString(mContext, R.string.press_exit), Toast.LENGTH_SHORT).show();
                isExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    /**
     * 预登录
     * 建议提前调用预登录接口，可以加快免密登录过程，提高用户体验
     */
    private void preVerify() {
        //移动的debug tag 是CMCC-SDK,电信是CT_ 联通是PriorityAsyncTask
        SecVerify.preVerify(new PreVerifyCallback() {
            @Override
            public void onComplete(Void data) {
                if (Constant.User.devMode) {
                    Toast.makeText(mContext, "预登录成功", Toast.LENGTH_LONG).show();
                }
                Constant.User.isPreVerifyDone = true;
                Log.e(TAG, "onComplete.isPreVerifyDone  " + Constant.User.isPreVerifyDone);
                SecVerify.autoFinishOAuthPage(false);
//                SecVerify.setUiSettings(CustomizeUtils.customizeUi());
            }

            @Override
            public void onFailure(VerifyException e) {
                Constant.User.isPreVerifyDone = false;
                Log.e(TAG, "onFailure.isPreVerifyDone  " + Constant.User.isPreVerifyDone);
                String errDetail = null;
                if (e != null){
                    errDetail = e.getMessage();
                }
                Log.e(TAG, "onFailure errDetail " + errDetail);
                if (Constant.User.devMode) {
                    // 登录失败
                    Log.e(TAG, "preVerify failed", e);
                    // 错误码
                    int errCode = e.getCode();
                    // 错误信息
                    String errMsg = e.getMessage();
                    // 更详细的网络错误信息可以通过t查看，请注意：t有可能为null
                    String msg = "错误码: " + errCode + "\n错误信息: " + errMsg;
                    if (!TextUtils.isEmpty(errDetail)) {
                        msg += "\n详细信息: " + errDetail;
                    }
                    Log.e(TAG,msg);
                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //点击事件
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateBottomUI(v.getId());
        }
    };

    //底部ui更新
    private void updateBottomUI(int pageId){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment2 = manager.findFragmentByTag(WORD);
        Fragment fragment5 = manager.findFragmentByTag(VIDEO);
        Fragment fragment4 = manager.findFragmentByTag(ME);
        if (fragment2 != null) {
            mPassFragment = (WordstepFragment) fragment2;
        }
        if (fragment5 != null) {
            if (App.APP_WORD_BOTTOM) {
                mMobFragment = (WordNoteFragment) fragment5;
            } else {
                mMobFragment = (KouyuFragment) fragment5;
            }
        }
        if (fragment4 != null) {
            mMeFragment = (MeFragment) fragment4;
        }

        resetBottom();

        switch (pageId){
            case R.id.ll_word:
                if (mPassFragment == null) {
                    mPassFragment = WordstepFragment.getInstance();
                    hideFragmentsNew(transaction, mMobFragment,mMeFragment);
                    transaction.add(R.id.container, mPassFragment, WORD);
                } else {
                    hideFragmentsNew(transaction, mMobFragment,mMeFragment);
                    transaction.show(mPassFragment);
                }

                ivWord.setSelected(true);
                tvWord.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case R.id.ll_talk:
                if (mMobFragment == null) {
                    if (App.APP_WORD_BOTTOM) {
                        mMobFragment = WordNoteFragment.build();
                    } else {
                        mMobFragment = KouyuFragment.build(21, false);
                    }
                    hideFragmentsNew(transaction, mPassFragment,mMeFragment);
                    transaction.add(R.id.container, mMobFragment, VIDEO);
                } else {
                    hideFragmentsNew(transaction, mPassFragment,mMeFragment);
                    transaction.show(mMobFragment);
                    if (App.APP_WORD_BOTTOM) {
                        ((WordNoteFragment) mMobFragment).onClickReload();
                    } else {
                        ((KouyuFragment) mMobFragment).onClickReload();
                    }
                }

                ivTalk.setSelected(true);
                tvTalk.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case R.id.ll_me:
                if (mMeFragment == null) {
                    mMeFragment = new MeFragment();
                    hideFragmentsNew(transaction, mMobFragment,mPassFragment);
                    transaction.add(R.id.container, mMeFragment, ME);
                } else {
                    hideFragmentsNew(transaction, mMobFragment,mPassFragment);
                    transaction.show(mMeFragment);
                }

                ivMe.setSelected(true);
                tvMe.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
        }

        transaction.commitAllowingStateLoss();
    }

    //隐藏fragment
    private void hideFragmentsNew(FragmentTransaction transaction, Fragment... fragments) {
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                transaction.hide(fragment);
            }
        }
    }

    //将所有的底部样式重置
    private void resetBottom(){
        ivWord.setSelected(false);
        tvWord.setTextColor(getResources().getColor(R.color.bottom_text_color));

        ivTalk.setSelected(false);
        tvTalk.setTextColor(getResources().getColor(R.color.bottom_text_color));

        ivMe.setSelected(false);
        tvMe.setTextColor(getResources().getColor(R.color.bottom_text_color));
    }


    /**
     * 视频下载后点击
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DLItemEvent dlEvent) {
        BasicDLPart dlPart = dlEvent.items.get(dlEvent.position);

        switch (dlPart.getType()) {
            case "voa":
            case "csvoa":
            case "bbc":
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(this,
                        dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(),
                        dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivity.getIntent2Me(this,
                        dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(),
                        dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
        }

    }

    /**
     * 获取视频模块“现在升级的点击”
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HeadlineGoVIPEvent headlineGoVIPEvent) {
        Intent intent = new Intent(mContext, NewVipCenterActivity.class);
        startActivity(intent);
    }

    //消息中心中部分item的跳转拦截处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ArtDataSkipEvent event) {
        Voa voa = event.voa;
        //文章跳转
        switch (event.type) {
            case "news":
                HeadlineTopCategory topCategory = event.headline;
                startActivity(TextContentActivity.getIntent2Me(mContext,
                        topCategory.id, topCategory.Title, topCategory.TitleCn, topCategory.type
                        , topCategory.Category, topCategory.CreatTime, topCategory.getPic(), topCategory.Source));
                break;
            case "voa":
            case "csvoa":
            case "bbc":
            case "song":
                startActivity(AudioContentActivityNew.getIntent2Me(mContext,
                        voa.categoryString, voa.title, voa.title_cn,
                        voa.getPic(),event.type, String.valueOf(voa.voaid), voa.sound));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivityNew.getIntent2Me(mContext,
                        voa.categoryString, voa.title, voa.title_cn, voa.getPic(),
                        event.type, String.valueOf(voa.voaid), voa.sound));//voa.getVipAudioUrl()
                break;
        }
    }

    /***********微信登陆的回调操作*************/
    //接受微信回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WxLoginEvent event){
        if (FixLoginSession.getInstance().getWxSmallToken()==null){
            return;
        }

        if (event.getErrCode()==0){
            mPresenter.getUidByToken(FixLoginSession.getInstance().getWxSmallToken());
        }else {
            showToastShort(getResources().getString(R.string.wxSmallLoginFail));
        }
    }

    /*************************刷新用户信息***************************/
    //刷新用户信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.userInfo)){
            UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(),null);
        }
    }

    /***************************mob秒验*******************************/
    //预加载秒验信息
    private void preloadMobVerify(){
        //判断秒验是否可用
        if (ConfigData.loginType.equals(LoginType.loginByVerify)){
            SecVerify.preVerify(new PreVerifyCallback() {
                @Override
                public void onComplete(Void unused) {
                    TempDataManager.getInstance().setMobVerify(true);
                }

                @Override
                public void onFailure(VerifyException e) {
                    TempDataManager.getInstance().setMobVerify(false);
                }
            });
        }
    }

    /************************新的登录信息******************************/
    //初始化账号信息
    private void initUserInfo(){
        if (UserInfoManager.getInstance().isLogin()){
            getUserinfo(UserInfoManager.getInstance().getUserId());
        }else {
            //获取原来的信息进行处理
            int oldUserId = getOldUserData();
            if (oldUserId>0){
                getUserinfo(oldUserId);
            }
        }
    }

    //获取用户信息
    private void getUserinfo(int userId){
        UserInfoManager.getInstance().getRemoteUserInfo(userId, new UserinfoCallbackListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new LoginEvent());
                        Log.d("刷新s2", "刷新头像");
                    }
                });
            }

            @Override
            public void onFail(String errorMsg) {

            }
        });
    }

    //获取原来的id信息
    private int getOldUserData(){
        SharedPreferences preferences = SPUtil.getPreferences(this,"kouyu_show_file");
        String oldData = preferences.getString("mUser","");

        if (TextUtils.isEmpty(oldData)){
            return 0;
        }

        try {
            byte[] b = Base64.decodeBase64(oldData.getBytes());
            InputStream bis = new ByteArrayInputStream(b);
            ObjectInputStream ois = new ObjectInputStream(bis); // something wrong
            User user = (User) ois.readObject();
            ois.close();

            //这里获取到数据后不要删除，只删除用户信息就行了，因为还有其他的数据需要使用
            preferences.edit().putString("mUser","").apply();

            return user.getUid();
        }catch (Exception e){
            return 0;
        }
    }
}
