package com.iyuba.talkshow.newce.study;

import android.util.Base64;
import android.util.Log;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.ad.ADUtil;
import com.iyuba.talkshow.data.model.Collect;
import com.iyuba.talkshow.data.model.IntegralBean;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.model.result.GetAdData1;
import com.iyuba.talkshow.data.model.result.GetAdResponse1;
import com.iyuba.talkshow.data.model.result.PdfResponse;
import com.iyuba.talkshow.data.remote.AdService;
import com.iyuba.talkshow.data.remote.IntegralService;
import com.iyuba.talkshow.data.remote.MovieService;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.RxUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.TimeUtil;
import com.iyuba.wordtest.entity.TalkShowWords;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carl shen on 2020/7/31
 * New Primary English, new study experience.
 */
@ConfigPersistent
public class StudyPresenter extends BasePresenter<StudyMvpView> {

    private final DataManager mDataManager;

    private Subscription mGetPdfSub;
    private Subscription mCheckCollectedSub;
    private Subscription mSaveCollectSub;
    private Subscription mUpdateCollectSub;
    private Subscription mGetAdSub;

    @Inject
    public StudyPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    public void getPdf(int voaId , int type ){
        checkViewAttached();
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
                            Log.e("StudyPresenter", "getPdf onError " + e.getMessage());
                        }
                        getMvpView().showToast("生成pdf失败");
                    }

                    @Override
                    public void onNext(PdfResponse pdfResponse) {
                        if (null != pdfResponse.exists){
                            getMvpView().showPdfFinishDialog(pdfResponse.path);
                            //增加免费次数处理
                            mDataManager.addVoaDownloadNumber();
                        }
                    }
                });
    }

    public void safeUpdate(int voaId) {
        int uid = UserInfoManager.getInstance().getUserId();
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
                            Log.e("StudyPresenter", "safeUpdate onError " + e.getMessage());
                        }
                        getMvpView().showToast("收藏失败");
                    }

                    @Override
                    public void onNext(MovieService.UpdateCollect response) {
                        if (response == null) {
                            getMvpView().showToast("收藏失败");
                            return;
                        }
                        Log.e("StudyPresenter", "safeUpdate " + response);
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
        RxUtil.unsubscribe(mUpdateCollectSub);
        mUpdateCollectSub = mDataManager.updateCollect(uid + "", voaId + "", "del")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MovieService.UpdateCollect>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("StudyPresenter", "safeDelete onError " + e.getMessage());
                        }
                        getMvpView().showToast("取消收藏失败");
                    }

                    @Override
                    public void onNext(MovieService.UpdateCollect response) {
                        if (response == null) {
                            getMvpView().showToast("取消收藏失败");
                            return;
                        }
                        Log.e("StudyPresenter", "safeDelete " + response);
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
                            Log.e("StudyPresenter", "checkCollected onError " + e.getMessage());
                        }
//                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Integer Id) {
                        getMvpView().setIsCollected(Id == voaId);

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
                            Log.e("StudyPresenter", "saveCollect onError " + e.getMessage());
                        }
//                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.e("StudyPresenter", "saveCollect onNext " + aBoolean);
//                        if (aBoolean) {
//                            getMvpView().showToast("收藏成功");
//                            getMvpView().setIsCollected(true);
//                        } else {
//                            getMvpView().showToast("收藏失败");
//                            getMvpView().setIsCollected(false);
//                        }
                    }
                });
    }

    public void deleteCollect(int voaId) {
        int uid = UserInfoManager.getInstance().getUserId();
        checkViewAttached();
        RxUtil.unsubscribe(mSaveCollectSub);
        //这里参数的顺序错了
//        mSaveCollectSub = mDataManager.deleteCollect(voaId, uid)
        mSaveCollectSub = mDataManager.deleteCollect(uid, voaId)
                .compose(RxUtil.io2main())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e("StudyPresenter", "deleteCollect onError " + e.getMessage());
                        }
//                        getMvpView().showToast(R.string.database_error);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.e("StudyPresenter", "deleteCollect onNext " + aBoolean);
//                        if (aBoolean) {
//                            getMvpView().showToast("取消收藏成功");
//                            getMvpView().setIsCollected(false);
//                        } else {
//                            getMvpView().showToast("取消收藏失败");
//                            getMvpView().setIsCollected(true);
//                        }
                    }
                });
    }

    public int getUnitId4Voa(Voa voa) {
        return mDataManager.getUnitId4Voa(voa);
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetPdfSub);
        RxUtil.unsubscribe(mCheckCollectedSub);
        RxUtil.unsubscribe(mSaveCollectSub);
        RxUtil.unsubscribe(mUpdateCollectSub);
        RxUtil.unsubscribe(mGetAdSub);

        RxUtil.unsubscribe(mIntegralSub);
    }

    public IntegralService getInregralService() {
        return mDataManager.getIntegralService();
    }

    //增加方法
    //增加pdf下载的东西，之前的有点错误
    private Voa mVoa;
    private String mDir;
    private Subscription mIntegralSub;

    public void init(Voa voa) {
        this.mVoa = voa;
        mDir = StorageUtil
                .getMediaDir(TalkShowApplication.getContext(), voa.voaId())
                .getAbsolutePath();
    }

    public boolean checkIsFree() {
        if (UserInfoManager.getInstance().isVip()) {
            return true;
        }

//        return mDataManager.getVoaDownloadNumber() < 5;
        return false;
    }

    //这里怎么处理的，mvoa数据为null都没有测试出来，服气了
    //在上边有个初始化init的方法，在其他界面中调用即可
    public void deductIntegral(int type) {
        checkViewAttached();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        String flag = null;
        try {
            flag = Base64.encodeToString(
                    URLEncoder.encode(df.format(new Date(System.currentTimeMillis())), "UTF-8").getBytes(), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mIntegralSub = mDataManager.deductIntegral(flag, UserInfoManager.getInstance().getUserId(), App.APP_ID, mVoa.voaId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<IntegralBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        //这里部分界面没有绑定，无法调用这个方法
                        getMvpView().showToast(" 积分扣取失败！"+e.getMessage());
//                        ToastUtil.showToast(TalkShowApplication.getInstance()," 积分扣取失败！"+e.getMessage());
                    }

                    @Override
                    public void onNext(IntegralBean bean) {
                        if ("200".equals(bean.result)) {
                            getMvpView().onDeductIntegralSuccess(type);
                        } else {
//                            getMvpView().showToast("Code：" + bean.result + " 积分扣取失败！");
                            getMvpView().showToast("积分不足，扣取失败！");
//                            ToastUtil.showToast(TalkShowApplication.getInstance(),"Code：" + bean.result + " 积分扣取失败！");
//                            ToastUtil.showToast(TalkShowApplication.getInstance(),"积分不足，扣取失败！");
                        }
                    }
                });
    }

    //获取本单元的单词
    public List<TalkShowWords> getCurUnitWords(int voaId){
        List<TalkShowWords> wordsList = mDataManager.searchWords(voaId);
        return wordsList;
    }

    //获取本单元的原文
    public List<VoaText> getCurVoaText(int voaId){
        List<VoaText> voaTextList = mDataManager.getVoaTextbyVoaId(voaId);
        return voaTextList;
    }
}
