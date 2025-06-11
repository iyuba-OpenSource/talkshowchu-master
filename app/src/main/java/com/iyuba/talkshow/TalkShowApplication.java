package com.iyuba.talkshow;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.webkit.WebView;

import androidx.multidex.MultiDex;

import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.IHeadlineManager;
import com.iyuba.headlinelibrary.data.local.db.HLDBManager;
import com.iyuba.imooclib.IMooc;
import com.iyuba.module.dl.BasicDLDBManager;
import com.iyuba.module.favor.BasicFavor;
import com.iyuba.module.favor.data.local.BasicFavorDBManager;
import com.iyuba.module.privacy.IPrivacy;
import com.iyuba.module.privacy.PrivacyInfoHelper;
import com.iyuba.share.ShareExecutor;
import com.iyuba.share.mob.MobShareExecutor;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.injection.component.ApplicationComponent;
import com.iyuba.talkshow.injection.component.DaggerApplicationComponent;
import com.iyuba.talkshow.injection.module.ApplicationModule;
import com.iyuba.talkshow.lil.help_fix.util.OAIDNewHelper;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.newce.ContainActivity;
import com.iyuba.talkshow.newce.ContianerActivity;
import com.iyuba.talkshow.ui.welcome.WelcomeActivity;
import com.iyuba.talkshow.util.TimeUtil;
import com.iyuba.widget.unipicker.IUniversityPicker;
import com.liulishuo.filedownloader.FileDownloader;
import com.mob.MobSDK;
import com.tencent.vasdolly.helper.ChannelReaderUtil;
import com.umeng.commonsdk.UMConfigure;
import com.yd.saas.ydsdk.manager.YdConfig;
import com.youdao.sdk.common.YouDaoAd;
import com.youdao.sdk.common.YoudaoSDK;

import java.util.Date;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.PersonalType;
import timber.log.Timber;

//import com.iyuba.pushlib.PushApplication;
//import com.xuexiang.xpush.XPush;

public class TalkShowApplication extends Application {

    private static TalkShowApplication INSTANCE;
    ApplicationComponent mApplicationComponent;
    private HandlerThread mHandlerThread;
    private static Handler mSubHandler;
    private long startTime=0, endTime=0;
    public int mFinalCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        ResUtil.getInstance().setApplication(this);
        webViewSetPath(INSTANCE);

        //加载oaid的前置数据
        OAIDNewHelper.loadLibrary();

        //微课初始化
//        IMooc.init(getApplicationContext(), "" + App.APP_ID, Constant.LESSON_TYPE);
        IMooc.init(getApplicationContext(), String.valueOf(App.APP_ID), Constant.LESSON_TYPE);
        IMooc.setDebug(BuildConfig.DEBUG);
//        IMooc.setProductId(Constant.PRODUCT_ID + "");
//        IMooc.setMoocBanner(App.APP_TENCENT_MOOC);

        RetrofitUrlManager.getInstance().setDebug(true);
        RetrofitUrlManager.getInstance().setRun(true);
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true);
            UMConfigure.setEncryptEnabled(false);
        } else {
            UMConfigure.setLogEnabled(false);
            UMConfigure.setEncryptEnabled(true);
        }
        String channel = ChannelReaderUtil.getChannel(INSTANCE);
        Log.e("TalkShowApplication", "onCreate AnalyticsConfig.getChannel = " + channel);
        UMConfigure.preInit(INSTANCE, ConfigData.umeng_key, channel);
        // global thread to run some task
        mHandlerThread = new HandlerThread("TalkShowApplication");
        mHandlerThread.start();
        mSubHandler = new Handler(mHandlerThread.getLooper());
        mSubHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!App.APP_TENCENT_PRIVACY) {
                    initUMMob();
                }
                FileDownloader.setupOnApplicationOnCreate(INSTANCE);
                PersonalHome.init(getContext(), String.valueOf(App.APP_ID), Constant.EVAL_TYPE);
                //这里因为aar的问题暂时关闭，需要后续开启
//                PersonalHome.setDeletePhoto(false);
//                PersonalHome.setUserComplain(App.APP_HUAWEI_COMPLAIN);

                //开启昵称修改
                PersonalHome.setEnableEditNickname(true);
                PersonalHome.setCategoryType(PersonalType.VOA);
//                if ("初中英语背单词".equals(App.APP_NAME_CH)) {
                if ("初中英语背单词".equals(getResources().getString(R.string.app_name))) {
                    PersonalHome.setMainPath(ContainActivity.class.getName());
                } else {
                    PersonalHome.setMainPath(ContianerActivity.class.getName());
                }
                IUniversityPicker.init(getContext());
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
                MobShareExecutor mobShare = new MobShareExecutor();
//                if (App.APP_SHARE_PART > 0) {
//                    mobShare.setPlatformHidden(new String[]{ QQ.NAME, SinaWeibo.NAME });
//                } else {
//                    mobShare.setPlatformHidden(new String[]{ SinaWeibo.NAME });
//                }
                ShareExecutor.getInstance().setRealExecutor(mobShare);
                if (App.APP_NAME_PRIVACY.equalsIgnoreCase("隐私政策")) {
                    IPrivacy.init(getApplicationContext(), Constant.Url.PROTOCOL_BJIYB_USAGE + getResources().getString(R.string.app_name), Constant.Url.PROTOCOL_BJIYB_PRIVACY + getResources().getString(R.string.app_name));
                } else {
                    IPrivacy.init(getApplicationContext(), Constant.Url.PROTOCOL_BJIYY_USAGE + getResources().getString(R.string.app_name), Constant.Url.PROTOCOL_BJIYY_PRIVACY + getResources().getString(R.string.app_name));
                }
                PrivacyInfoHelper.getInstance().putApproved(true);
                DLManager.init(INSTANCE, 6);
            }
        });

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                mFinalCount++;
                if (mFinalCount == 1) {
                    //如果mFinalCount ==1，说明是从后台到前台
                    endTime = new Date().getTime();
                    long timeload = endTime - startTime;
                    Log.e("TalkShowApplication", "onActivityStarted time load = " + timeload + "--endTime = " + endTime);
                    if (!App.APP_TENCENT_PRIVACY && (startTime != 0) && (timeload / 1000 >= 180)) {
                        Log.e("TalkShowApplication", "onActivityStarted time load = " + timeload + "--start = " + startTime);
                        Intent intent = new Intent(activity, WelcomeActivity.class);
                        intent.putExtra("onActivityStarted", true);
                        activity.startActivity(intent);
                    }
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mFinalCount--;
                if (mFinalCount == 0) {
                    //如果mFinalCount ==0，说明是前台到后台
                    startTime = new Date().getTime();
                    Log.e("TalkShowApplication", "onActivityStopped startTime = " + startTime);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        YoudaoSDK.terminate();
        if (mSubHandler != null) {
            mSubHandler.removeCallbacks(null);
            mSubHandler = null;
        }
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
    }
    public static void initUMMob() {
//        XPush.init(INSTANCE);
//        PushApplication.initPush(INSTANCE);
        try {
            // TODO: 2025/1/13 因为有道sdk的问题，这里不使用手动处理的方式，但是上边的system需要保留
            //先初始化oaid
            /*OAIDHelper.getInstance().init(INSTANCE);
            //oaid升级版本(2024-3-20，每年都需要更换)
            OAIDNewHelper oaidNewHelper = new OAIDNewHelper(new OAIDNewHelper.AppIdsUpdater() {
                @Override
                public void onIdData(boolean isSupported, boolean isLimited, String oaid, String vaid, String aaid) {
                    if (isSupported && !isLimited){
                        OAIDHelper.getInstance().setOAID(oaid);
                    }
                }
            },"msaoaidsec",App.oaid_pem);
            oaidNewHelper.getDeviceIds(TalkShowApplication.getContext(),true,false,false);*/
            //设置有道sdk的一些功能
            YouDaoAd.getYouDaoOptions().setDebugMode(false);
            YouDaoAd.getNativeDownloadOptions().setConfirmDialogEnabled(true);
            YouDaoAd.getYouDaoOptions().setAppListEnabled(false);
            YouDaoAd.getYouDaoOptions().setPositionEnabled(false);
            YouDaoAd.getYouDaoOptions().setSdkDownloadApkEnabled(true);
            YouDaoAd.getYouDaoOptions().setDeviceParamsEnabled(false);
            YouDaoAd.getYouDaoOptions().setWifiEnabled(false);
            YouDaoAd.getYouDaoOptions().setCanObtainAndroidId(false);
            YoudaoSDK.init(INSTANCE);
            //设置广告时间
            String channel = ChannelReaderUtil.getChannel(INSTANCE);
            Date compileDate = TimeUtil.GLOBAL_SDF.parse("2000-01-01 00:00:00");
//            if (INSTANCE.getPackageName().equals(Constant.PackageName.Package_junior)&&channel.equals("huawei")){
//                 compileDate = TimeUtil.GLOBAL_SDF.parse(BuildConfig.COMPILE_DATETIME);
//            }
            //这里根据当前情况，扩展成3天
            Date adBlockDate = new Date(compileDate.getTime()+7*24*60*60*1000);
            AdBlocker.getInstance().setBlockStartDate(adBlockDate);
        } catch (Exception arg1) {
            arg1.printStackTrace();
        }

        MobSDK.submitPolicyGrantResult(true, null);
        MobSDK.init(INSTANCE, ConfigData.mob_key, ConfigData.mob_secret);
        UMConfigure.submitPolicyGrantResult(INSTANCE, true);

        String channel = ChannelReaderUtil.getChannel(INSTANCE);
        UMConfigure.init(INSTANCE, ConfigData.umeng_key, channel, UMConfigure.DEVICE_TYPE_PHONE, "");

        //视频模块
        IHeadline.init(INSTANCE,String.valueOf(App.APP_ID),App.APP_NAME_EN);
        //开启视频模块配音
        IHeadline.setEnableSmallVideoTalk(true);
        BasicDLDBManager.init(INSTANCE);
        BasicFavorDBManager.init(INSTANCE);
        BasicFavor.init(INSTANCE,String.valueOf(App.APP_ID));
        HLDBManager.init(INSTANCE);

        //爱语吧sdk广告
        YdConfig.getInstance().init(INSTANCE, String.valueOf(App.APP_ID));

    }
    public static Handler getSubHandler() {
        return mSubHandler;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getContext() {
        return INSTANCE.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

    /**
     * Needed to replace the component with a test specific one
     */
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }

    public static TalkShowApplication get(Context context) {
        return (TalkShowApplication) context.getApplicationContext();
    }


    public static TalkShowApplication getInstance() {
        return INSTANCE;
    }

    public void webViewSetPath(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(context);
            if (!"com.iyuba.talkshow.junior".equals(processName) || !"com.iyuba.talkshow.juniorenglish".equals(processName)
                || !"com.iyuba.primarypro".equals(processName) || !"com.iyuba.primaryenglish".equals(processName)
                || !"com.iyuba.xiaoxue".equals(processName) || !"com.iyuba.talkshow.childenglish".equals(processName)
                || !"com.iyuba.talkshow.childenglishnew".equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    public String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    public void exit() {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            System.exit(0);
        } else {// android2.1
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            am.restartPackage(getPackageName());
        }
    }

}
