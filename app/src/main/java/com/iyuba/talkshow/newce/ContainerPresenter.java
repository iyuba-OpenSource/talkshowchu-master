package com.iyuba.talkshow.newce;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.ui.user.login.UidBean;
import com.iyuba.talkshow.util.FailOpera;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.wordtest.ui.listen.singleWrite.model.MLKitManager;
import com.iyuba.wordtest.ui.listen.singleWrite.model.MlKitModelEvent;
import com.iyuba.wordtest.ui.listen.singleWrite.model.MlKitModelSession;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carl shen on 2020/8/7
 * New Junior English, new study experience.
 */
@ConfigPersistent
public class ContainerPresenter extends BasePresenter<ContainerMvpView> {

    private Subscription mUidSub;//根据token获取uid
    private Subscription mUserinfoSub;//获取用户的信息

    private DataManager mDataManager;

    @Inject
    public ContainerPresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unsubscribe(mUidSub);
        RxUtil.unsubscribe(mUserinfoSub);
    }

    void downloadApk(String versionCode, String appUrl) {
        String filename = App.APP_NAME_EN + App.UNDERLINE + versionCode + App.APK_SUFFIX;
        String dir = StorageUtil.getAppDir(TalkShowApplication.getInstance());
        File file = new File(dir, filename);
        if (file.exists()) {
            file.delete();
        }
//        mDLManager.dlStart(appUrl, dir, filename, listener);
        // init the download task
        Log.e("ContainerPresenter", "addImageDownloadList:file " + file.getAbsolutePath());
        FileDownloader.getImpl().create(appUrl).setTag(appUrl).
                setPath(file.getAbsolutePath(), false).setListener(listener).start();
    }

    private final FileDownloadListener listener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e("ContainerPresenter", "startFileDownload:pending soFarBytes = " + soFarBytes);
            Log.e("ContainerPresenter", "startFileDownload:pending totalBytes = " + totalBytes);
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            Log.e("ContainerPresenter", "startFileDownload:connected soFarBytes = " + soFarBytes);
            Log.e("ContainerPresenter", "startFileDownload:connected totalBytes = " + totalBytes);
            if (getMvpView() != null) {
                getMvpView().setDownloadMaxProgress(totalBytes);
            }
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e("ContainerPresenter", "startFileDownload:progress soFarBytes = " + soFarBytes);
            Log.e("ContainerPresenter", "startFileDownload:progress totalBytes = " + totalBytes);
            if (getMvpView() != null) {
                getMvpView().setDownloadMaxProgress(totalBytes);
                getMvpView().setDownloadProgress(soFarBytes);
            }
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
        }

        @Override
        protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
            Log.e("ContainerPresenter", "startFileDownload:progress soFarBytes = " + soFarBytes);
            Log.e("ContainerPresenter", "startFileDownload:progress retryingTimes = " + retryingTimes);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            Log.e("ContainerPresenter", "startFileDownload:completed" );
            if (getMvpView() != null) {
                File file = new File(task.getPath());
                getMvpView().setDownloadProgress((int) file.length());
                getMvpView().setProgressVisibility(View.INVISIBLE);
                //替换
//                FailOpera.openAPKFile((Context) getMvpView(), file);
                FailOpera.installApk((Context) getMvpView(), file);
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e("ContainerPresenter", "startFileDownload:paused soFarBytes = " + soFarBytes);
            Log.e("ContainerPresenter", "startFileDownload:paused totalBytes = " + totalBytes);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            Log.e("ContainerPresenter", "startFileDownload:error" );
            if (getMvpView() != null) {
                if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
                    getMvpView().showToast(R.string.please_check_network);
                } else {
                    getMvpView().showToast(R.string.request_fail);
                }
            }
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            Log.e("ContainerPresenter", "startFileDownload:warn" );
        }
    };


    /******************微信回调测试***************/
    //根据token获取uid
    public void getUidByToken(String token){
        checkViewAttached();
        RxUtil.unsubscribe(mUidSub);
        mUidSub = mDataManager.getUid(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UidBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showToastShort(TalkShowApplication.getInstance().getResources().getString(R.string.wxSmallLoginFail));
                        }
                    }

                    @Override
                    public void onNext(UidBean uidBean) {
                        if (getMvpView()!=null){
                            if (uidBean.getResult() == 200){
                                getUserInfo(uidBean.getUid());
                            }else {
                                getMvpView().showToastShort(TalkShowApplication.getInstance().getResources().getString(R.string.wxSmallLoginFail));
                            }
                        }
                    }
                });
    }

    //获取用户的信息
    public void getUserInfo(int uid){
        /*checkViewAttached();
        RxUtil.unsubscribe(mUserinfoSub);
        mUserinfoSub = mDataManager.getUserInfo(uid,uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showToastShort(TalkShowApplication.getInstance().getResources().getString(R.string.wxSmallLoginFail));
                        }
                    }

                    @Override
                    public void onNext(UserData useresult) {
                        if (getMvpView()!=null){

                            if (useresult!=null&&!TextUtils.isEmpty(useresult.username)){
                                User theuser = new User();
                                theuser.setUid(uid);
                                theuser.setUsername(useresult.username);
                                theuser.setNickname(useresult.nickname);
                                theuser.setAmount(useresult.amount);
                                theuser.setEmail(useresult.email);
                                theuser.setPhone(useresult.mobile);
                                theuser.setImgSrc(useresult.middle_url);
                                theuser.setVipStatus(Integer.parseInt(useresult.vipStatus));
                                theuser.setExpireTime(TimeUtil.tansDateFrom1970(useresult.expireTime * 1000));
                                mAccountManager.setUser(theuser);
                                mAccountManager.saveUser(theuser);

                                //刷新界面
                                EventBus.getDefault().post(new LoginEvent());
                            }else {
                                getMvpView().showToastShort(TalkShowApplication.getInstance().getResources().getString(R.string.wxSmallLoginFail));
                            }
                        }
                    }
                });*/

        UserInfoManager.getInstance().getRemoteUserInfo(uid, new UserinfoCallbackListener() {
            @Override
            public void onSuccess() {
                ((Activity)getMvpView()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //刷新界面
                        EventBus.getDefault().post(new LoginEvent());
                    }
                });
            }

            @Override
            public void onFail(String errorMsg) {
                ((Activity)getMvpView()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getMvpView().showToastShort(TalkShowApplication.getInstance().getResources().getString(R.string.wxSmallLoginFail));
                    }
                });
            }
        });
    }

    /*******************谷歌手写模型下载*************/
    //检查并下载手写模型
    public void checkAndDownloadMlKitModel(){
        Log.d("手写模型处理", "准备检测模型存在");

        MLKitManager.getInstance().checkModelDownload(new MLKitManager.OnDigitalCallbackListener<String>() {
            @Override
            public void onSuccess(String showMsg) {
                Log.d("手写模型处理", "模型存在");

                EventBus.getDefault().post(new MlKitModelEvent(MlKitModelEvent.tag_success,showMsg));
            }

            @Override
            public void onFail(String showMsg) {
                Log.d("手写模型处理", "模型不存在，准备下载");

                //设置下载中
                MlKitModelSession.getInstance().setModelDownloadState(true);
                //下载手写模型
                MLKitManager.getInstance().downloadModel(new MLKitManager.OnDigitalCallbackListener<String>() {
                    @Override
                    public void onSuccess(String showMsg) {
                        Log.d("手写模型处理", "下载完成");

                        //设置不再下载
                        MlKitModelSession.getInstance().setModelDownloadState(false);
                        //发送信息
                        EventBus.getDefault().post(new MlKitModelEvent(MlKitModelEvent.tag_success,showMsg));
                    }

                    @Override
                    public void onFail(String showMsg) {
                        Log.d("手写模型处理", "下载失败");

                        //设置不再下载
                        MlKitModelSession.getInstance().setModelDownloadState(false);
                        //发送信息
                        EventBus.getDefault().post(new MlKitModelEvent(MlKitModelEvent.tag_fail,showMsg));
                    }
                });
            }
        });
    }
}
