package com.iyuba.talkshow.newce;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.IHeadlineManager;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivityNew;
import com.iyuba.headlinelibrary.ui.content.TextContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivityNew;
import com.iyuba.imooclib.IMooc;
import com.iyuba.imooclib.event.ImoocBuyIyubiEvent;
import com.iyuba.imooclib.event.ImoocBuyVIPEvent;
import com.iyuba.imooclib.event.ImoocPayCourseEvent;
import com.iyuba.imooclib.ui.mobclass.MobClassActivity;
import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.manager.AbilityControlManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.manager.VersionManager;
import com.iyuba.talkshow.data.model.User;
import com.iyuba.talkshow.databinding.ActivityContainerBinding;
import com.iyuba.talkshow.event.HelpEvent;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.event.MyBookEvent;
import com.iyuba.talkshow.event.RefreshWordEvent;
import com.iyuba.talkshow.event.SelectBookEvent;
import com.iyuba.talkshow.event.WordStepEvent;
import com.iyuba.talkshow.event.WxLoginEvent;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.NovelBookChooseManager;
import com.iyuba.talkshow.lil.help_fix.manager.TempDataManager;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.NovelDataManager;
import com.iyuba.talkshow.lil.help_fix.ui.main.MainBottomAdapter;
import com.iyuba.talkshow.lil.help_fix.ui.main.MainBottomBean;
import com.iyuba.talkshow.lil.help_fix.ui.main.ui.moc.MocShowFragment;
import com.iyuba.talkshow.lil.help_fix.ui.main.ui.video.VideoShowFragment;
import com.iyuba.talkshow.lil.help_mvp.util.SPUtil;
import com.iyuba.talkshow.lil.help_mvp.util.StackUtil;
import com.iyuba.talkshow.lil.junior.ui.JuniorFragment;
import com.iyuba.talkshow.lil.novel.service.FixBgService;
import com.iyuba.talkshow.lil.novel.service.FixBgServiceManager;
import com.iyuba.talkshow.lil.novel.ui.NovelFragment;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.data.NewLoginType;
import com.iyuba.talkshow.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newce.me.MeFragment;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryBgPlayEvent;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryBgPlayManager;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryBgPlayService;
import com.iyuba.talkshow.newce.wordstep.WordstepFragment;
import com.iyuba.talkshow.newdata.Config;
import com.iyuba.talkshow.newdata.SPconfig;
import com.iyuba.talkshow.newview.SendBookPop;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.user.login.fix.FixLoginSession;
import com.iyuba.talkshow.ui.vip.buyiyubi.BuyIyubiActivity;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.talkshow.ui.vip.payorder.PayOrderActivity;
import com.iyuba.talkshow.ui.words.WordNoteFragment;
import com.iyuba.talkshow.util.DialogUtil;
import com.iyuba.talkshow.util.LogUtil;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.event.GetSoundEvent;
import com.iyuba.wordtest.manager.WordConfigManager;
import com.iyuba.wordtest.manager.WordManager;
import com.mob.secverify.PreVerifyCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.common.exception.VerifyException;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.codec.binary.Base64;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.data.model.HeadlineTopCategory;
import personal.iyuba.personalhomelibrary.data.model.Voa;
import personal.iyuba.personalhomelibrary.event.ArtDataSkipEvent;

/**
 * Created by carl shen on 2020/7/31
 * New Primary English, new study experience.
 */

//@RuntimePermissions
public class ContianerActivity extends BaseActivity implements ContainerMvpView {
    public static final String TAG = "ContianerActivity";
    private static final String VERSION_CODE = "versionCode";
    private static final String APP_URL = "appUrl";
    @Inject
    VersionManager mVersionManager;
    @Inject
    ContainerPresenter mPresenter;
    @Inject
    ConfigManager configManager;

    //界面标记
    private final String JUNIOR = "junior";
    private final String MAIN = "main";
    private final String WORD_STEP = "word_step";
    private final String WORD_NOTE = "word_note";
    private final String VIDEO = "video";
    private final String MOC = "moc";
    private final String NOVEL = "database/novel";
    private final String ME = "me";

    //中小学合并界面
    private JuniorFragment juniorFragment;
    //中小学-列表界面
    private MainFragment mainFragment;
    //中小学-单词界面
    private WordstepFragment wordstepFragment;
    //中小学-生词界面
    private WordNoteFragment wordNoteFragment;
    //视频界面
    private VideoShowFragment videoShowFragment;
    //微课界面
    private MocShowFragment mocShowFragment;
    //故事界面
    private NovelFragment novelFragment;
    //我的界面
    private MeFragment meFragment;

    private boolean isExit;
    private ProgressBar aboutDownloadProgressbar;
    private SendBookPop sendBookPop;

    private RecyclerView bottomView;

    //布局样式
    private ActivityContainerBinding binding;

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

        binding = ActivityContainerBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
//        bottomBar = findViewById(R.id.bottom_bar);
        bottomView = findViewById(R.id.bottomView);

        aboutDownloadProgressbar = findViewById(R.id.about_download_progressbar);
        activityComponent().inject(this);
        mPresenter.attachView(this);

        //初始化界面
//        initFragment();
        //初始化显示
        initContent();

        //使用审核接口控制好评送会员
        //设置随机数进行处理
        int random = (int) (Math.random()*15+10);
        LogUtil.d("随机数测试", "保存数据--"+configManager.getSendFlag()+"--随机数--"+random);
        if (!configManager.isSendBook()
                &&configManager.getSendFlag()>=random) {
            findViewById(R.id.container).postDelayed(() -> {
//                sendBookPop = new SendBookPop(this, findViewById(R.id.container));
                DialogUtil.showSendBookDialog(this, DialogUtil.DIALOG);
            }, 500);
            configManager.setSendFlag(0);
        }

        //针对分享功能操作
        if (ConfigData.openShare) {
            PersonalHome.setEnableShare(true);
            IMooc.setEnableShare(true);
            IHeadline.setEnableShare(true);
            IHeadlineManager.enableShare=true;
        } else {
            PersonalHome.setEnableShare(false);
            IMooc.setEnableShare(false);
            IHeadline.setEnableShare(false);
            IHeadlineManager.enableShare=false;
        }

        //预加载秒验
        preloadMobVerify();

        //激活room的预存
        preSaveRoom();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        EventBus.getDefault().register(this);
//        bottomBar.setOnTabSelectListener(mTabSelectListener);

        // TODO: 2025/2/26 暂时关闭这种方式操作
        /*TalkShowApplication.getSubHandler().post(() -> {
            EventBus.getDefault().post(new HelpEvent(1));
            int wordLoad = WordConfigManager.Instance(mContext).loadInt(WordConfigManager.WORD_DB_LOADED, 0);
            if (wordLoad == 0) {
                boolean result = WordDataBase.getInstance(TalkShowApplication.getContext()).loadDbData();
                if (result) {
                    WordConfigManager.Instance(mContext).putInt(WordConfigManager.WORD_DB_LOADED, 1);
                }
            }
        });*/

        Intent intent = getIntent();
        String versionCode = intent.getStringExtra(VERSION_CODE);
        String appUrl = intent.getStringExtra(APP_URL);
        if (!TextUtils.isEmpty(versionCode) && !TextUtils.isEmpty(appUrl)) {
            aboutDownloadProgressbar.setVisibility(View.VISIBLE);
            mPresenter.downloadApk(versionCode, appUrl);
        } else if (App.APP_CHECK_UPGRADE) {
            long curTime = System.currentTimeMillis();
            long oldTime = SPconfig.Instance().loadLong(Config.VERSION_CHECK);
            Log.e("ContianerActivity", "checkNewApp oldTime " + oldTime);
            if ((curTime - oldTime) > 1000 * 60 * 60 * 24) {
                mVersionManager.checkVersion(callBack);
                SPconfig.Instance().putLong(Config.VERSION_CHECK, curTime);
            }
        }

        //绑定后台服务
        startBindService();

        //获取用户信息
        initUserInfo();

        //预加载数据
        preLoadData();
    }

    /*    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mMainFragment == null) {
            mMainFragment = new MainFragment();
        }
        transaction.add(R.id.container, mMainFragment, MAIN).show(mMainFragment).commitNowAllowingStateLoss();
    }*/

    private void initPersonHome() {
        UserInfoManager.getInstance().initUserInfo();
    }

    /*private final OnTabSelectListener mTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelected(@IdRes int tabId) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment1 = manager.findFragmentByTag(MAIN);
            Fragment fragment2 = manager.findFragmentByTag(WORD);
            Fragment fragment3 = manager.findFragmentByTag(MOC);
            Fragment fragment5 = manager.findFragmentByTag(VIDEO);
            Fragment fragment4 = manager.findFragmentByTag(ME);
            if (fragment1 != null) {
                mMainFragment = (MainFragment) fragment1;
            }
            if (fragment2 != null) {
                mPassFragment = (WordstepFragment) fragment2;
            }
            if (fragment3 != null) {
                mMocFragment = (MobClassFragment) fragment3;
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
                case R.id.tab_home:
                    if (mMainFragment == null) {
                        mMainFragment = new MainFragment();
                        hideFragments(transaction,mMocFragment,mMobFragment,mPassFragment,mMeFragment);
                        transaction.add(R.id.container, mMainFragment, MAIN);
                    } else {
                        hideFragments(transaction,mMocFragment,mMobFragment,mPassFragment,mMeFragment);
                        transaction.show(mMainFragment);
                    }
                    break;
                case R.id.tab_word:
                    if (mPassFragment == null) {
                        mPassFragment = new WordstepFragment().build(configManager.getWordId(), configManager.getWordTitle());
                        hideFragments(transaction, mMainFragment, mMocFragment, mMobFragment,mMeFragment);
                        transaction.add(R.id.container, mPassFragment, WORD);
                    } else {
                        hideFragments(transaction, mMainFragment, mMocFragment, mMobFragment,mMeFragment);
                        transaction.show(mPassFragment);
                    }
                    if (Constant.PlayerService && (mMainFragment != null)) {
                        mMainFragment.checkPlayBar(false);
                    }
                    break;
                case R.id.tab_moc:
                    if (mMocFragment == null) {
                        ArrayList<Integer> typeIdFilter = new ArrayList<>();
//                        typeIdFilter.add(-2);//全部
//                        typeIdFilter.add(-1);//最新
//                        typeIdFilter.add(2);//四级
                        typeIdFilter.add(3);//VOA
                        typeIdFilter.add(27);// disney
//                        typeIdFilter.add(4);//六级
//                        typeIdFilter.add(7);//托福
//                        typeIdFilter.add(8);//考研
//                        typeIdFilter.add(9);//BBC
                        if (App.APP_TENCENT_MOOC) {
                            typeIdFilter.add(21);//新概念
                        }
//                        typeIdFilter.add(22);//走遍美国
//                        typeIdFilter.add(28);//学位
//                        typeIdFilter.add(52);//考研二
//                        typeIdFilter.add(61);//雅思
//                        typeIdFilter.add(91);//中职
                        typeIdFilter.add(Constant.PRODUCT_ID);//初中
//                        typeIdFilter.add(25);//小学
                        //typeIdFilter.add(1);//N1
                        //typeIdFilter.add(5);//N2
                        //typeIdFilter.add(6);//N3
                        Bundle args = MobClassFragment.buildArguments(Constant.PRODUCT_ID, false,typeIdFilter);
                        mMocFragment = MobClassFragment.newInstance(args);
                        hideFragments(transaction, mMainFragment,mPassFragment,mMobFragment,mMeFragment);
                        transaction.add(R.id.container, mMocFragment, MOC);
                    } else {
                        hideFragments(transaction, mMainFragment, mPassFragment,mMobFragment,mMeFragment);
                        transaction.show(mMocFragment);
                    }
                    if (Constant.PlayerService && (mMainFragment != null)) {
                        mMainFragment.checkPlayBar(false);
                    }
                    break;
                case R.id.tab_talk:
                    if (mMobFragment == null) {
                        if (App.APP_WORD_BOTTOM) {
                            mMobFragment = WordNoteFragment.build();
                        } else {
                            mMobFragment = KouyuFragment.build(21, false);
                        }
                        hideFragments(transaction, mMainFragment,mPassFragment,mMocFragment,mMeFragment);
                        transaction.add(R.id.container, mMobFragment, VIDEO);
                    } else {
                        hideFragments(transaction, mMainFragment, mPassFragment,mMocFragment,mMeFragment);
                        transaction.show(mMobFragment);
                        if (App.APP_WORD_BOTTOM) {
                            ((WordNoteFragment) mMobFragment).onClickReload();
                        } else {
                            ((KouyuFragment) mMobFragment).onClickReload();
                        }
                    }
                    if (Constant.PlayerService && (mMainFragment != null)) {
                        mMainFragment.checkPlayBar(false);
                    }
                    break;
                case R.id.tab_me:
                    if (mMeFragment == null) {
                        mMeFragment = new MeFragment();
                        hideFragments(transaction, mMainFragment, mMobFragment,mPassFragment,mMocFragment);
                        transaction.add(R.id.container, mMeFragment, ME);
                    } else {
                        hideFragments(transaction, mMainFragment, mMobFragment,mPassFragment,mMocFragment);
                        transaction.show(mMeFragment);
                    }
                    if (Constant.PlayerService && (mMainFragment != null)) {
                        mMainFragment.checkPlayBar(false);
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
    };*/

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

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        EventBus.getDefault().unregister(this);

        //关闭后台播放
        stopBindService();

        super.onDestroy();
    }

    /**
     * 双击返回键退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExit) {

                //关闭后台播放
                stopBindService();

                TalkShowApplication.getInstance().exit();
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

    /*****************重整界面样式*****************/
    //适配器
    private MainBottomAdapter bottomAdapter;
    //初始化界面和数据
    private void initContent(){
        //底部数据和界面数据
        List<MainBottomBean> bottomList = new ArrayList<>();
        //课文
        bottomList.add(new MainBottomBean(R.drawable.ic_home_no_selected,R.drawable.ic_home_selected, "课文", MAIN));
        //单词
        bottomList.add(new MainBottomBean(R.drawable.ic_word_no_selected,R.drawable.ic_word_selected,"单词",WORD_STEP));

        if (App.APP_WORD_BOTTOM){
            //生词本
            bottomList.add(new MainBottomBean(R.drawable.ic_note_no_selected,R.drawable.ic_note_selected,"生词本",WORD_NOTE ));
        }else {

            // TODO: 2025/4/2 李沁蕊私聊：应用宝的暂时删除微课的内容，这里处理下
            //微课
//            if (!AbilityControlManager.getInstance().isLimitMoc()){
//                bottomList.add(new MainBottomBean(R.drawable.ic_moc_no_selected,R.drawable.ic_moc_selected, "微课",MOC ));
//            }
        }

        //故事
        if (!AbilityControlManager.getInstance().isLimitNovel()){
            bottomList.add(new MainBottomBean(R.drawable.ic_novel_no_selected,R.drawable.ic_novel_selected,"阅读",NOVEL ));
        }
        //我的
        bottomList.add(new MainBottomBean(R.drawable.ic_me_no_selected,R.drawable.ic_me_selected,"我",ME ));

        bottomAdapter = new MainBottomAdapter(this,bottomList);
        GridLayoutManager manager = new GridLayoutManager(this,bottomList.size());
        bottomView.setLayoutManager(manager);
        bottomView.setAdapter(bottomAdapter);
        bottomAdapter.setOnClickListener(new MainBottomAdapter.OnClickListener() {
            @Override
            public void onClick(String showTag) {
                updateFragmentUI(showTag);
            }
        });

        //显示第一个界面
        updateFragmentUI(bottomList.get(0).getTag());
    }

    //底部ui更新
    private void updateFragmentUI(String showTag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment juniorF = manager.findFragmentByTag(JUNIOR);
        Fragment mainF = manager.findFragmentByTag(MAIN);
        Fragment wordStepF = manager.findFragmentByTag(WORD_STEP);
        Fragment wordNoteF = manager.findFragmentByTag(WORD_NOTE);
        Fragment videoF = manager.findFragmentByTag(VIDEO);
        Fragment mocF = manager.findFragmentByTag(MOC);
        Fragment novelF = manager.findFragmentByTag(NOVEL);
        Fragment meF = manager.findFragmentByTag(ME);

        if (juniorF != null) {
            juniorFragment = (JuniorFragment) juniorFragment;
        }
        if (mainF != null) {
            mainFragment = (MainFragment) mainF;
        }
        if (wordStepF != null) {
            wordstepFragment = (WordstepFragment) wordStepF;
        }
        if (wordNoteF != null) {
            wordNoteFragment = (WordNoteFragment) wordNoteF;
        }
        if (videoF != null) {
            videoShowFragment = (VideoShowFragment) videoF;
        }
        if (mocF != null) {
            mocShowFragment = (MocShowFragment) mocF;
        }
        if (novelF != null) {
            novelFragment = (NovelFragment) novelF;
        }
        if (meF != null) {
            meFragment = (MeFragment) meF;
        }


        switch (showTag) {
            //中小学合并界面
            case JUNIOR:
                if (juniorFragment == null) {
                    juniorFragment = JuniorFragment.getInstance();
                    hideFragments(transaction, mainFragment, wordstepFragment, wordNoteFragment, videoShowFragment,mocShowFragment,novelFragment,meFragment);
                    transaction.add(R.id.container, juniorFragment, JUNIOR);
                } else {
                    hideFragments(transaction, mainFragment, wordstepFragment, wordNoteFragment, videoShowFragment,mocShowFragment,novelFragment,meFragment);
                    transaction.show(juniorFragment);
                }
                break;
                //中小学-列表界面
            case MAIN:
                if (mainFragment == null) {
                    mainFragment = MainFragment.getInstance();
                    hideFragments(transaction, juniorFragment, wordstepFragment, wordNoteFragment, videoShowFragment,mocShowFragment,novelFragment,meFragment);
                    transaction.add(R.id.container, mainFragment, MAIN);
                } else {
                    hideFragments(transaction, juniorFragment, wordstepFragment, wordNoteFragment, videoShowFragment,mocShowFragment,novelFragment,meFragment);
                    transaction.show(mainFragment);
                }
                break;
                //中小学-单词界面
            case WORD_STEP:
                if (wordstepFragment == null) {
                    wordstepFragment = WordstepFragment.getInstance();
                    hideFragments(transaction, juniorFragment, mainFragment, wordNoteFragment, videoShowFragment,mocShowFragment,novelFragment,meFragment);
                    transaction.add(R.id.container, wordstepFragment, WORD_STEP);
                } else {
                    hideFragments(transaction, juniorFragment, mainFragment, wordNoteFragment, videoShowFragment,mocShowFragment,novelFragment,meFragment);
                    transaction.show(wordstepFragment);
                }

                //暂停播放音频
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_pause));
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_pause));
                break;
                //中小学-生词本界面
            case WORD_NOTE:
                if (wordNoteFragment == null) {
                    wordNoteFragment = WordNoteFragment.build();
                    hideFragments(transaction, juniorFragment, mainFragment, wordstepFragment, videoShowFragment,mocShowFragment,novelFragment,meFragment);
                    transaction.add(R.id.container, wordNoteFragment, WORD_NOTE);
                } else {
                    hideFragments(transaction, juniorFragment, mainFragment, wordstepFragment, videoShowFragment,mocShowFragment,novelFragment,meFragment);
                    transaction.show(wordNoteFragment);
                }

                if (wordNoteFragment!=null){
                    wordNoteFragment.onClickReload();
                }

                //暂停播放音频
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_pause));
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_pause));
                break;
                //视频界面
            case VIDEO:
                if (videoShowFragment == null) {
                    videoShowFragment = VideoShowFragment.getInstance();
                    hideFragments(transaction, juniorFragment, mainFragment, wordstepFragment, wordNoteFragment,mocShowFragment,novelFragment,meFragment);
                    transaction.add(R.id.container, videoShowFragment, VIDEO);
                } else {
                    hideFragments(transaction, juniorFragment, mainFragment, wordstepFragment, wordNoteFragment,mocShowFragment,novelFragment,meFragment);
                    transaction.show(videoShowFragment);
                }
                //暂停播放音频
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_pause));
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_pause));
                break;
                //微课界面
            case MOC:
                if (mocShowFragment == null) {
                    mocShowFragment = MocShowFragment.getInstance();
                    hideFragments(transaction, juniorFragment, mainFragment, wordstepFragment, wordNoteFragment,videoShowFragment,novelFragment,meFragment);
                    transaction.add(R.id.container, mocShowFragment, MOC);
                } else {
                    hideFragments(transaction, juniorFragment, mainFragment, wordstepFragment, wordNoteFragment,videoShowFragment,novelFragment,meFragment);
                    transaction.show(mocShowFragment);
                }

                //暂停播放音频
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_pause));
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_pause));
                break;
                //故事界面
            case NOVEL:
                if (novelFragment==null){
                    novelFragment = NovelFragment.getInstance();
                    hideFragments(transaction, juniorFragment, mainFragment, wordstepFragment, wordNoteFragment,videoShowFragment,mocShowFragment,meFragment);
                    transaction.add(R.id.container,novelFragment,NOVEL);
                }else {
                    hideFragments(transaction, juniorFragment, mainFragment, wordstepFragment, wordNoteFragment,videoShowFragment,mocShowFragment,meFragment);
                    transaction.show(novelFragment);
                }
                //暂停播放音频
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_pause));
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_pause));
                break;
                //我的界面
            case ME:
                if (meFragment == null) {
                    meFragment = new MeFragment();
                    hideFragments(transaction, juniorFragment, mainFragment, wordstepFragment, wordNoteFragment,videoShowFragment,mocShowFragment,novelFragment);
                    transaction.add(R.id.container, meFragment, ME);
                } else {
                    hideFragments(transaction, juniorFragment, mainFragment, wordstepFragment, wordNoteFragment,videoShowFragment,mocShowFragment,novelFragment);
                    transaction.show(meFragment);
                }
                //暂停播放音频
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_pause));
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_pause));
                break;
        }
        transaction.commitNowAllowingStateLoss();

        //显示样式
        bottomAdapter.setIndex(showTag);
    }

    //隐藏fragment
    private void hideFragments(FragmentTransaction transaction, Fragment... fragments) {
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                transaction.hide(fragment);
            }
        }
    }

    /***************************接口回调*************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event) {
        Log.e("ContianerActivity", "onEvent LoginEvent");
        initPersonHome();
        TalkShowApplication.getSubHandler().post(new Runnable() {
            @Override
            public void run() {
                int wordLoad = WordConfigManager.Instance(mContext).loadInt(WordConfigManager.WORD_DB_NEW_LOADED, 0);
                if (wordLoad == 1) {
                    int uidLoad = WordConfigManager.Instance(mContext).loadInt(WordConfigManager.WORD_DB_NEW_LOADED + UserInfoManager.getInstance().getUserId(), 0);
                    Log.e("ContianerActivity", "LoginEvent uidLoad " + uidLoad);
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
        if (UserInfoManager.getInstance().isLogin()) {
            Intent intent = new Intent(mContext, NewVipCenterActivity.class);
            intent.putExtra(NewVipCenterActivity.HUI_YUAN, NewVipCenterActivity.HUANGJIN);
            startActivity(intent);
        } else {
            NewLoginUtil.startToLogin(this);
        }
    }

    //微课跳转爱语币购买界面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocBuyIyubiEvent event){
        if (UserInfoManager.getInstance().isLogin()){
            startActivity(new Intent(this, BuyIyubiActivity.class));
        }else {
            NewLoginUtil.startToLogin(this);
        }
    }

    //选书的回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MyBookEvent event) {
        Log.e("ContianerActivity", "MyBookEvent event.bookId " + event.bookId);
//        bottomBar.selectTabAtPosition(0);
        updateFragmentUI(MAIN);
        EventBus.getDefault().post(new SelectBookEvent(event.bookId, 0));
    }

    //这里是点击跳转到单词界面(处理的时候记得使用)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WordStepEvent event) {
//        bottomBar.selectTabAtPosition(1);
        updateFragmentUI(WORD_STEP);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetSoundEvent event) {
        ArrayList<Integer> typeIdFilter = new ArrayList<>();
        typeIdFilter.add(3);
        startActivity(MobClassActivity.buildIntent(mContext, 3, true, typeIdFilter));
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

    //接受微信登录回调
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

    //微课购买操作回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocPayCourseEvent event){
        if (!UserInfoManager.getInstance().isLogin()){
            NewLoginUtil.startToLogin(this);
            return;
        }

        String desc = "花费"+event.price+"购买微课("+event.body+")";
        String subject = "微课直购";
        Intent intent = PayOrderActivity.buildIntent(this,desc,event.price,subject,event.body,event.courseId,event.productId,PayOrderActivity.Order_moc);
        startActivity(intent);
    }

    /***************************mob秒验*******************************/
    //预加载秒验信息
    private void preloadMobVerify(){
        //判断秒验是否可用
        NewLoginType.getInstance().setCurLoginType(ConfigData.loginType);
        if (ConfigData.loginType.equals(NewLoginType.loginByVerify)){
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
            //先加载当前的数据
            UserInfoManager.getInstance().initUserInfo();
            //然后加载远程数据
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
                        LogUtil.d("刷新s2", "刷新头像");
                    }
                });
            }

            @Override
            public void onFail(String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //这里直接刷新信息
                        UserInfoManager.getInstance().initUserInfo();
                    }
                });
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

    /**********************************小学后台****************************/
    //是否已经绑定了服务
    private boolean isBindService = false;

    //开启后台
    private void startBindService(){
        if (!isBindService) {
            isBindService = true;

            //小学的后台服务
            Intent intent = new Intent();
            intent.setClass(this, PrimaryBgPlayService.class);
            bindService(intent, PrimaryBgPlayManager.getInstance().getConnection(), Context.BIND_AUTO_CREATE);

            //小说的后台
            Intent serviceIntent = new Intent();
            serviceIntent.setClass(this, FixBgService.class);
            bindService(serviceIntent, FixBgServiceManager.getInstance().connection, Context.BIND_AUTO_CREATE);
        }
    }

    //关闭后台
    private void stopBindService(){
        unbindService(PrimaryBgPlayManager.getInstance().getConnection());

        unbindService(FixBgServiceManager.getInstance().connection);
    }

    /**********************************数据刷新回调************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.existApp)){
            //停止后台
            stopBindService();
            //退出app
            StackUtil.getInstance().existApp();
        }else if (event.getType().equals(TypeLibrary.RefreshDataType.userInfo)){
            //用户信息刷新
            UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), null);
        }
    }

    /**************************************辅助方法******************************/
    private void preSaveRoom(){
        //这里做一个操作，先激活room的操作，将预存的数据存到本地数据库中
        //不清楚有无其他的好的方法是否可以操作
        //下面的操作没有实际意义，只是激活room
        NovelDataManager.searchMultiChapterFromDB(NovelBookChooseManager.getInstance().getBookType(),String.valueOf(NovelBookChooseManager.getInstance().getBookLevel()), NovelBookChooseManager.getInstance().getBookId());
    }

    //预加载数据
    private void preLoadData(){
        //进入就加载谷歌的mlkit的手写模型
        if (NetworkUtil.isConnected(this)){
            mPresenter.checkAndDownloadMlKitModel();
        }
    }
}
