package com.iyuba.talkshow.newce.kouyu;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.AdManager;
import com.iyuba.talkshow.data.model.CategoryFooter;
import com.iyuba.talkshow.data.model.LoopItem;
import com.iyuba.talkshow.data.model.TitleSeries;
import com.iyuba.talkshow.data.model.TitleSeriesResponse;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.result.GetAdData1;
import com.iyuba.talkshow.data.model.result.GetAdResponse1;
import com.iyuba.talkshow.data.remote.AdService;
import com.iyuba.talkshow.data.remote.LoopService;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newce.MainFragPresenter;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.StorageUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ConfigPersistent
public class KouyuPresenter extends BasePresenter<KouyuMvpView> {

    private final DataManager mDataManager;
    private final AdManager mAdManager;

    private Subscription mLoadNewSub;
    private Subscription mLoadSub;
    private Subscription mLoadLoopSub;
    private Subscription mGetWelcomeSub;
    private Subscription mGetVoaByIdSub;
    private Subscription mGetMoreVoaSub;
    private Subscription mLoginSub;

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat mSdf = new SimpleDateFormat("yyyy-MM-dd");

    @Inject
    public KouyuPresenter(DataManager dataManager,
                          AdManager adManager) {
        mDataManager = dataManager;
        mAdManager = adManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mLoadNewSub);
        RxUtil.unsubscribe(mLoadSub);
        RxUtil.unsubscribe(mLoadLoopSub);
        RxUtil.unsubscribe(mGetWelcomeSub);
        RxUtil.unsubscribe(mGetVoaByIdSub);
        RxUtil.unsubscribe(mGetMoreVoaSub);
        RxUtil.unsubscribe(mLoginSub);
    }

    /*public void registerToken(final String token, final String opTopken, String operator) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoginSub);
        mLoginSub = mDataManager.registerByMob(token, opTopken, operator)
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

    public void loadMoreVoas(final CategoryFooter category, int limit, String ids) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetMoreVoaSub);
        List<Voa> voas = new ArrayList<>();
        mGetMoreVoaSub = mDataManager.getVoasByCategoryNotWith(category.getCategory(), limit, ids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Voa>() {
                    @Override
                    public void onCompleted() {
                        if (voas.isEmpty()) {
                            getMvpView().showToast(R.string.no_data);
                        } else {
                            getMvpView().showVoasByCategory(voas, category);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("KouyuPresenter", "loadMoreVoas onError  " + e.getMessage());
                        }
                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Voa voa) {
                        voas.add(voa);
                    }
                });
    }

    public void loadVoas(int bookId) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoadSub);
        Log.e("KouyuPresenter", "loadVoas bookId " + bookId);
        mLoadSub = mDataManager.getXiaoxueHomeVoas(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Voa>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("KouyuPresenter", "loadMoreVoas onError " + e.getMessage());
                        }
//                        if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
//                            getMvpView().showToast(R.string.please_check_network);
//                        }
                        getVoaSeries(bookId);
                    }

                    @Override
                    public void onNext(List<Voa> voas) {
                        if ((voas == null) || voas.isEmpty()) {
                            getVoaSeries(bookId);
                            return;
                        }
                        List<Voa> showData = new ArrayList<>();
                        for (Voa series: voas) {
                            if ((series != null) && series.hotFlag() > 0) {
                                showData.add(series);
                            }
                        }
                        Log.e("KouyuPresenter", "loadVoas showData.size " + showData.size());
                        getMvpView().showMoreVoas(showData);
                    }
                });
    }


    public void loadLoop() {
        checkViewAttached();
        RxUtil.unsubscribe(mLoadLoopSub);
        String type = Constant.Apk.isChild() ? LoopService.GetLoopInfo.Param.Value.CHILD_LOOP_TYPE
                : LoopService.GetLoopInfo.Param.Value.LOOP_TYPE;
        mLoadLoopSub = mDataManager.getLunboItems(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Subscriber<List<LoopItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("KouyuPresenter", "loadMoreVoas onError " + e.getMessage());
                        }
                        if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
//                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }

                    @Override
                    public void onNext(List<LoopItem> loopItems) {
                        getMvpView().setBanner(loopItems);
                    }
                });
    }

    public void getWelcomePic() {
        checkViewAttached();
        RxUtil.unsubscribe(mGetWelcomeSub);
        mGetWelcomeSub = mDataManager.getAd1()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<GetAdResponse1>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
                            getMvpView().showToast(R.string.please_check_network);
                        }
                    }

                    @Override
                    public void onNext(List<GetAdResponse1> responseList) {
                        Date curDate = new Date();
                        GetAdResponse1 response = responseList.get(0);
                        if (response.result().equals(AdService.GetAd.Result.Code.SUCCESS)) {
                            try {
                                GetAdData1 data = response.data();
                                if (data != null) {
                                    if ("youdao".equals(data.type())) {

                                    }
//                                    if (!"web".equals(data.type())) {
//                                        return;
//                                    }
                                    Date startDate = mSdf.parse(data.startDate());
                                    if (startDate.getTime() <= curDate.getTime()) {
                                        GetAdData1 lastAd = mAdManager.getAdData1();
                                        String filename = AdService.GetAd1.getAdFilename(data.picUrl());
                                        if (lastAd != null) {
                                            String lastFilename = AdService.GetAd1.getAdFilename(lastAd.picUrl());
                                            if (TextUtils.equals(filename, lastFilename)
                                                    && !TextUtils.equals(lastAd.startDate(), data.startDate())) {
                                                StorageUtil.deleteAdFile((TalkShowApplication.getInstance()), filename);
                                            }
                                        }

                                        // 将改标识存下来，用与下次启动时显示
                                        mAdManager.saveAdData1(data);
                                        // 将图片的adStartTime存起来，如果图片表示一样，但adStartTime不一样，说明图片更换了
                                        String picUrl = Constant.Url.getAdPicUrl(data.picUrl());
//                                        mDLManager.dlStart(picUrl, StorageUtil.getAdDir(TalkShowApplication.getInstance()), filename, null);
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void getVoaById(int voaId) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetVoaByIdSub);
        mGetVoaByIdSub = mDataManager.getVoaById(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Subscriber<Voa>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (getMvpView() != null) {
                            getMvpView().showToast(R.string.database_error);
                        }
                    }

                    @Override
                    public void onNext(Voa voa) {
                        if (getMvpView() != null) {
                            getMvpView().startDetailActivity(voa);
                        }
                    }
                });
    }

    public void getVoaSeries(int series) {
        checkViewAttached();
        RxUtil.unsubscribe(mLoadNewSub);
        mLoadNewSub = mDataManager.getTitleSeriesList("" + series, UserInfoManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TitleSeriesResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("KouyuPresenter", "getVoaSeries onError  " + e.getMessage());
                        }
                        getMvpView().showVoas(null);
                        if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                            getMvpView().showToast(R.string.please_check_network);
                        }
                    }

                    @Override
                    public void onNext(TitleSeriesResponse response) {
                        if (response == null || response.getData() == null) {
                            Log.e("KouyuPresenter", "getVoaSeries onNext response is null. ");
                            getMvpView().showVoas(null);
                            return;
                        }
                        Log.e("KouyuPresenter", "getVoaSeries onNext getTotal " + response.getTotal());
                        List<TitleSeries> seriesData = response.getData();
                        List<Voa> voaData = new ArrayList<>();
                        List<Voa> showData = new ArrayList<>();
                        for (TitleSeries series: seriesData) {
                            try {
                                voaData.add(MainFragPresenter.Series2Voa(series));
                                if ((series != null) && series.HotFlg > 0) {
                                    showData.add(MainFragPresenter.Series2Voa(series));
                                }
                            } catch (Exception var2) {
                                Log.e("KouyuPresenter", "getVoaSeries onNext id " + series.Id);
                                var2.printStackTrace();
                            }
                        }
                        Log.e("KouyuPresenter", "getVoaSeries showData.size " + showData.size());
                        getMvpView().showVoas(showData);
                        TalkShowApplication.getSubHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                for (Voa series: voaData) {
                                    long result = mDataManager.insertVoaDB(series);
//                                    Log.e("KouyuPresenter", "getVoaSeries insertVoaDB result " + result);
                                }
                            }
                        });
                    }
                });
    }
}
