package com.iyuba.talkshow.ui.about;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.remote.ClearUserResponse;
import com.iyuba.talkshow.data.remote.UserService;
import com.iyuba.talkshow.event.LoginOutEvent;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.FailOpera;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.wordtest.db.NewBookLevelDao;
import com.iyuba.wordtest.db.TalkShowTestsDao;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.manager.WordManager;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;

@ConfigPersistent
public class AboutPresenter extends BasePresenter<AboutMvpView> {
    private final DataManager mDataManager;
    private Subscription mLoginSub;

    @Inject
    public AboutPresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mLoginSub);
    }
    /*註銷用戶*/
    public void clearUser(String password) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoginSub);
        mLoginSub = mDataManager.clearUser(UserInfoManager.getInstance().getUserName(), password)
                .compose(RxUtil.io2main())
                .subscribe(new Subscriber<ClearUserResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView() != null) {
                            if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                                getMvpView().showToast(R.string.please_check_network);
                            } else {
                                getMvpView().showToastShort("注销失败！请稍后再试。");
                            }
                        }
                    }

                    @Override
                    public void onNext(ClearUserResponse response) {
                        if (response == null) {
                            Log.e("AboutPresenter", "ClearUserResponse is null.");
                            getMvpView().showToastShort("注销失败！请稍后再试。");
                            return;
                        }
                        Log.e("AboutPresenter", "ClearUserResponse:getResult " + response.getResult());
                        if ("101".equals(response.getResult())) {
                            getMvpView().showToastShort("用户已经注销成功！");
                            TalkShowApplication.getSubHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("AboutPresenter", "ClearUserResponse to clear user data.");
                                    if (WordManager.WordDataVersion == 2) {
                                        NewBookLevelDao bookLevelDao  = WordDataBase.getInstance(TalkShowApplication.getContext()).getNewBookLevelDao();
                                        if (bookLevelDao != null) {
                                            bookLevelDao.deleteBookLevel(String.valueOf(UserInfoManager.getInstance().getUserId()));
                                        }
                                        TalkShowTestsDao talkShowDao  = WordDataBase.getInstance(TalkShowApplication.getContext()).getTalkShowTestsDao();
                                        if (talkShowDao != null) {
                                            talkShowDao.deleteWordTest(String.valueOf(UserInfoManager.getInstance().getUserId()));
                                        }
                                    }
                                    Boolean result = mDataManager.deleteUidDownload(UserInfoManager.getInstance().getUserId());
                                    Log.e("AboutPresenter", "deleteUidDownload result " + result);
                                    result = mDataManager.deleteUidCollect(UserInfoManager.getInstance().getUserId());
                                    Log.e("AboutPresenter", "deleteUidCollect result " + result);
                                    result = mDataManager.deleteUidThumb(UserInfoManager.getInstance().getUserId());
                                    Log.e("AboutPresenter", "deleteUidThumb result " + result);
                                    result = mDataManager.deleteUidArticleRecord(UserInfoManager.getInstance().getUserId());
                                    Log.e("AboutPresenter", "deleteUidArticleRecord result " + result);
                                    result = mDataManager.deleteUidVoaSound(UserInfoManager.getInstance().getUserId());
                                    Log.e("AboutPresenter", "deleteUidVoaSound result " + result);
                                    //使用新的方法
                                    UserInfoManager.getInstance().clearUserInfo();
                                    EventBus.getDefault().post(new LoginOutEvent());
                                }
                            });
                            if (getMvpView() != null) {
                                ((AboutActivity) getMvpView()).finish();
                            }
                        } else if (response.getResult().equals(UserService.Login.Result.Code.NOT_MATCHING + "")) {
                            getMvpView().showToastShort(UserService.Login.Result.Message.NOT_MATCHING);
                        } else {
                            getMvpView().showToastShort("注销失败！请稍后再试。");
                        }
                    }
                });
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
        Log.e("AboutPresenter", "addImageDownloadList:file " + file.getAbsolutePath());
        FileDownloader.getImpl().create(appUrl).setTag(appUrl).
                setPath(file.getAbsolutePath(), false).setListener(listener).start();
    }

    private final FileDownloadListener listener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e("AboutPresenter", "startFileDownload:pending soFarBytes = " + soFarBytes);
            Log.e("AboutPresenter", "startFileDownload:pending totalBytes = " + totalBytes);
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            Log.e("AboutPresenter", "startFileDownload:connected soFarBytes = " + soFarBytes);
            Log.e("AboutPresenter", "startFileDownload:connected totalBytes = " + totalBytes);
            if (getMvpView() != null) {
                getMvpView().setDownloadMaxProgress(totalBytes);
            }
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e("AboutPresenter", "startFileDownload:progress soFarBytes = " + soFarBytes);
            Log.e("AboutPresenter", "startFileDownload:progress totalBytes = " + totalBytes);
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
            Log.e("AboutPresenter", "startFileDownload:progress soFarBytes = " + soFarBytes);
            Log.e("AboutPresenter", "startFileDownload:progress retryingTimes = " + retryingTimes);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            Log.e("AboutPresenter", "startFileDownload:completed" );
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
            Log.e("AboutPresenter", "startFileDownload:paused soFarBytes = " + soFarBytes);
            Log.e("AboutPresenter", "startFileDownload:paused totalBytes = " + totalBytes);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            Log.e("AboutPresenter", "startFileDownload:error" );
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
            Log.e("AboutPresenter", "startFileDownload:warn" );
        }
    };
}
