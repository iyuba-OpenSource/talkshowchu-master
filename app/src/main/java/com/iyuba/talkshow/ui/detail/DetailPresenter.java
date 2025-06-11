package com.iyuba.talkshow.ui.detail;

import android.net.Uri;
import android.util.Log;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Collect;
import com.iyuba.talkshow.data.model.TitleSeries;
import com.iyuba.talkshow.data.model.TitleSeriesResponse;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.result.PdfResponse;
import com.iyuba.talkshow.data.remote.IntegralService;
import com.iyuba.talkshow.data.remote.MovieService;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newce.MainFragPresenter;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.TimeUtil;
import com.iyuba.talkshow.util.VoaMediaUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ConfigPersistent
public class DetailPresenter extends BasePresenter<DetailMvpView> {

    private final DataManager mDataManager;

    private Subscription mSaveCollectSub;
    private Subscription mCheckCollectedSub;
    private Subscription mDeleteCollectSub;
    private Subscription mUpdateCollectSub;
    //下载视频
    private Subscription mDownloadVideoSub;

    private Voa mVoa;
    private Subscription mLoadNewSub;
    private Subscription mGetPdfSub;

    @Inject
    public DetailPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    public Uri getVideoUri() {
        File videoFile = StorageUtil.getVideoDubbingFile(TalkShowApplication.getContext(), mVoa.voaId());
        if (videoFile.exists()) {
            return Uri.fromFile(videoFile);
        } else {
            String videoUrl = VoaMediaUtil.getVideoUrl(mVoa.video());
            if (UserInfoManager.getInstance().isVip()){
                videoUrl = VoaMediaUtil.getVideoVipUrl(mVoa.video());
            }
            return Uri.parse(videoUrl);
        }
    }

    public void getPdf(int voaId , int type ){
        RxUtil.unsubscribe(mGetPdfSub);
        mGetPdfSub = mDataManager.getPdf(Constant.EVAL_TYPE , voaId, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PdfResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("DetailPresenter", "getPdf onError " + e.getMessage());
                        }
                        getMvpView().showToast("生成pdf失败");
                    }

                    @Override
                    public void onNext(PdfResponse pdfResponse) {
                        if (null != pdfResponse.exists){
                            getMvpView().showPdfFinishDialog(pdfResponse.path);
                        }
                    }
                });
    }

    public void getVoaSeries(String series) {
        Log.e("DetailPresenter", "getVoaSeries " + series);
        checkViewAttached();
        RxUtil.unsubscribe(mLoadNewSub);
        mLoadNewSub = mDataManager.getTitleSeriesList(series, UserInfoManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TitleSeriesResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("DetailPresenter", "getVoaSeries onError  " + e.getMessage());
                        }
                        getMvpView().showToast("更新失败");
                    }

                    @Override
                    public void onNext(TitleSeriesResponse response) {
                        getMvpView().showToast("更新成功");
                        if (response == null || response.getData() == null) {
                            Log.e("DetailPresenter", "getVoaSeries onNext response is null. ");
                            return;
                        }
                        Log.e("DetailPresenter", "getVoaSeries onNext getTotal " + response.getTotal());
                        List<TitleSeries> seriesData = response.getData();
                        List<Voa> voaData = new ArrayList<>();
                        for (TitleSeries series: seriesData) {
                            try {
                                voaData.add(MainFragPresenter.Series2Voa(series));
                            } catch (Exception var2) {
                                Log.e("DetailPresenter", "getVoaSeries onNext id " + series.Id);
                                var2.printStackTrace();
                            }
                        }
                        Log.e("DetailPresenter", "getVoaSeries onNext voaData " + voaData.size());
                        TalkShowApplication.getSubHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                for (Voa series: voaData) {
                                    long result = mDataManager.insertVoaDB(series);
                                    Log.e("DetailPresenter", "getVoaSeries insertVoaDB result " + result);
                                }
                            }
                        });
                    }
                });
    }

    public void setVoa(Voa voa) {
        this.mVoa = voa;
    }

    public void safeUpdate(int voaId) {
        int uid = UserInfoManager.getInstance().getUserId();
        checkViewAttached();
        RxUtil.unsubscribe(mUpdateCollectSub);
        mUpdateCollectSub = mDataManager.updateCollect(uid + "", voaId + "", "insert")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MovieService.UpdateCollect>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("DetailPresenter", "safeUpdate onError " + e.getMessage());
                        }
                        getMvpView().showToast("收藏失败");
                    }

                    @Override
                    public void onNext(MovieService.UpdateCollect response) {
                        if (response == null) {
                            getMvpView().showToast("收藏失败");
                            return;
                        }
                        Log.e("DetailPresenter", "safeUpdate " + response);
                        if ("Success".equalsIgnoreCase(response.msg)) {
                            getMvpView().showToast("收藏成功");
                            getMvpView().setIsCollected(true);
                            saveCollect(voaId);
                        } else {
                            getMvpView().showToast("收藏失败");
                        }
                    }
                });
    }

    public void safeDelete(int voaId) {
        int uid = UserInfoManager.getInstance().getUserId();
        checkViewAttached();
        RxUtil.unsubscribe(mDeleteCollectSub);
        mDeleteCollectSub = mDataManager.updateCollect(uid + "", voaId + "", "del")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MovieService.UpdateCollect>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("DetailPresenter", "safeDelete onError " + e.getMessage());
                        }
                        getMvpView().showToast("取消收藏失败");
                    }

                    @Override
                    public void onNext(MovieService.UpdateCollect response) {
                        if (response == null) {
                            getMvpView().showToast("取消收藏失败");
                            return;
                        }
                        Log.e("DetailPresenter", "safeDelete " + response);
                        if ("Success".equalsIgnoreCase(response.msg)) {
                            getMvpView().showToast("取消收藏成功");
                            getMvpView().setIsCollected(false);
                            deleteCollect(voaId);
                        } else {
                            getMvpView().showToast("取消收藏失败");
                        }
                    }
                });
    }

    public void saveCollect(int voaId) {
        int uid = UserInfoManager.getInstance().getUserId();
        checkViewAttached();
        RxUtil.unsubscribe(mSaveCollectSub);
        mSaveCollectSub = mDataManager.saveCollect(
                Collect.builder()
                        .setUid(uid)
                        .setVoaId(voaId)
                        .setDate(TimeUtil.getCurDate())
                        .build())
                .compose(RxUtil.io2main())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("DetailPresenter", "saveCollect onError " + e.getMessage());
                        }
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        //                            getMvpView().setCollectTvText(R.string.have_collected);
                        //                            getMvpView().setCollectTvText(R.string.collect);
                        getMvpView().setIsCollected(aBoolean);
                    }
                });
    }

    public void deleteCollect(int voaId) {
        checkViewAttached();
        RxUtil.unsubscribe(mDeleteCollectSub);
        int uid = UserInfoManager.getInstance().getUserId();
        mDeleteCollectSub = mDataManager.deleteCollect(uid, voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("DetailPresenter", "deleteCollect onError " + e.getMessage());
                        }
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        //                            getMvpView().setCollectTvText(R.string.collect);
                        //                            getMvpView().setCollectTvText(R.string.have_collected);
                        getMvpView().setIsCollected(!aBoolean);
                    }
                });
    }

    public void checkCollected(int voaId) {
        checkViewAttached();
        RxUtil.unsubscribe(mCheckCollectedSub);
        mCheckCollectedSub = mDataManager.getCollectByVoaId(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("DetailPresenter", "checkCollected onError " + e.getMessage());
                        }
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Integer voaId) {
                        //                            getMvpView().setCollectTvText(R.string.have_collected);
                        //                            getMvpView().setCollectTvText(R.string.collect);
                        getMvpView().setIsCollected(voaId == mVoa.voaId());

                    }
                });
    }


    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSaveCollectSub);
        RxUtil.unsubscribe(mCheckCollectedSub);
        RxUtil.unsubscribe(mDeleteCollectSub);
        RxUtil.unsubscribe(mDownloadVideoSub);
    }

    public IntegralService getInregralService() {
        return mDataManager.getIntegralService();
    }

    //增加mob一键登录操作
    /*public void registerToken(final String token, final String opTopken, String operator) {
        checkViewAttached();
        RxUtil.unsubscribe(mDownloadVideoSub);
        mDownloadVideoSub = mDataManager.registerByMob(token, opTopken, operator)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RegisterMobResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("MainFragPresenter", "registerToken onError  " + e.getMessage());
                        }
                        getMvpView().goResultActivity(null);
                    }

                    @Override
                    public void onNext(RegisterMobResponse response) {
                        if (response == null) {
                            Log.e("MainFragPresenter", "registerToken onNext response is null.");
                            getMvpView().goResultActivity(null);
                            return;
                        }
                        Log.e("KouyuPresenter", "registerToken onNext isLogin " + response.isLogin);
                        if (1 == response.isLogin) {
                            getMvpView().goResultActivity(new LoginResult());
                            getMvpView().showToast("您已经登录成功，可以进行学习了。");
                            // already login, need update user info
                            if (response.userinfo != null) {
                                mAccountManager.setUser(response.userinfo, "");
                                mAccountManager.saveUser();
                            } else {
                                Log.e("KouyuPresenter", "registerToken onNext response.userinfo is null.");
                            }
                            EventBus.getDefault().post(new LoginEvent());
                        } else {
                            if (response.res != null) {
                                // register by this phone
                                RegisterMobResponse.MobBean mobBean = response.res;
                                LoginResult loginResult = new LoginResult();
                                loginResult.setPhone(mobBean.phone);
                                getMvpView().goResultActivity(loginResult);
                            } else {
                                Log.e("KouyuPresenter", "registerToken onNext response.res is null.");
                                getMvpView().goResultActivity(null);
                            }
                        }
                    }
                });
    }*/
}
