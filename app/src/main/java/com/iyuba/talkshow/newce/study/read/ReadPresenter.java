package com.iyuba.talkshow.newce.study.read;

import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.ArticleRecord;
import com.iyuba.talkshow.data.model.UploadRecordResult;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.remote.UploadStudyRecordService;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.help_mvp.util.BigDecimalUtil;
import com.iyuba.talkshow.lil.help_mvp.util.DateUtil;
import com.iyuba.talkshow.lil.help_mvp.util.EncodeUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newce.study.read.newRead.ui.NewReadListenEvent;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.RxUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carl shen on 2020/7/28
 * New Primary English, new study experience.
 */
@ConfigPersistent
public class ReadPresenter extends BasePresenter<ReadMvpView> {

    private final DataManager mDataManager;

    private Subscription mGetVoaSub;
    private Subscription mSyncVoaSub;
    private Subscription mSaveRecordSub;
    private Subscription mDelete1RecordSub;

    @Inject
    public ReadPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetVoaSub);
        RxUtil.unsubscribe(mSyncVoaSub);
        RxUtil.unsubscribe(mSaveRecordSub);
        RxUtil.unsubscribe(mDelete1RecordSub);
    }

    public void getVoaTexts(final int voaId) {
        checkViewAttached();
        RxUtil.unsubscribe(mGetVoaSub);
        mGetVoaSub = mDataManager.getVoaTexts(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VoaText>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        syncVoaTexts(voaId);
                    }

                    @Override
                    public void onNext(List<VoaText> voaTextList) {
                        if (voaTextList == null || voaTextList.isEmpty()) {
                            syncVoaTexts(voaId);
                        } else {
                            getMvpView().showVoaTexts(voaTextList);
                        }
                    }
                });
    }

    public void syncVoaTexts(final int voaId) {
        checkViewAttached();
        RxUtil.unsubscribe(mSyncVoaSub);
        mSyncVoaSub = mDataManager.syncVoaTexts(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VoaText>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showEmptyTexts();
                    }

                    @Override
                    public void onNext(List<VoaText> voaTextList) {
                        if (voaTextList == null || voaTextList.size() == 0) {
                            getMvpView().showEmptyTexts();
                        } else {
                            getMvpView().showVoaTexts(voaTextList);
                        }
                    }
                });
    }

    UploadStudyRecordService getUploadStudyRecordService() {
        return mDataManager.getUploadStudyRecordService();
    }

    public void saveArticleRecord(final ArticleRecord record) {
//        checkViewAttached();
        RxUtil.unsubscribe(mSaveRecordSub);
        mSaveRecordSub = mDataManager.saveArticleRecord(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
    }

    public void deleteRecord(long timestamp) {
        checkViewAttached();
        RxUtil.unsubscribe(mDelete1RecordSub);
        mDelete1RecordSub = mDataManager.deleteRecord(timestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
//                        ((Fragment) getMvpView()).onDestroy();
                    }
                });
    }

    public int getUnitId4Voa(Voa voa) {
        return mDataManager.getUnitId4Voa(voa);
    }

    //处理单词数据
    public String filterWord(String selectText) {
        if (selectText.startsWith(".")||selectText.endsWith(".")){
            selectText = selectText.replace(".", "");
        }
        if (selectText.startsWith(",")||selectText.endsWith(",")){
            selectText = selectText.replace(",", "");
        }
        if (selectText.startsWith("!")||selectText.endsWith("!")){
            selectText = selectText.replace("!", "");
        }
        if (selectText.startsWith("?")||selectText.endsWith("?")){
            selectText = selectText.replace("?", "");
        }
        if (selectText.startsWith("'")||selectText.endsWith("?")){
            selectText = selectText.replace("'", "");
        }
        if (selectText.startsWith("\"")||selectText.endsWith("\"")){
            selectText = selectText.replace("\"", "");
        }

        return selectText;
    }

    /****************************新增-提交听力学习报告****************************/
    public Observable<UploadRecordResult> submitListenReportNew(long startTime, long endTime, boolean isEnd, int wordCount, int voaId){
        String format = "json";
        String platform = "android";
        int appId = App.APP_ID;
        String appName = App.APP_NAME_EN;
        int userId = UserInfoManager.getInstance().getUserId();
        String device = "";
        String deviceId = "";
        int rewardVersion = 1;

        int testMode = 1;
        int testNumber = 1;

        int flag = 0;
        if (isEnd){
            flag = 1;
        }

        String startDate = DateUtil.toDateStr(startTime,DateUtil.YMDHMS);
        String endDate = DateUtil.toDateStr(endTime,DateUtil.YMDHMS);

        String sign = EncodeUtil.md5(userId+startDate+DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD));

        return mDataManager.getUploadStudyRecordService().uploadStudyRecord(
                        format,String.valueOf(appId),appName,appName,String.valueOf(voaId),
                        String.valueOf(userId),device,deviceId,startDate,endDate,
                        String.valueOf(flag),wordCount,String.valueOf(testMode),platform,
                        String.valueOf(testNumber),sign,rewardVersion);
    }

    public void submitListenReport(long startTime,long endTime,boolean isEnd,int wordCount,int voaId){
        String format = "json";
        String platform = "android";
        int appId = App.APP_ID;
        String appName = App.APP_NAME_EN;
        int userId = UserInfoManager.getInstance().getUserId();
        String device = "";
        String deviceId = "";
        int rewardVersion = 1;

        int testMode = 1;
        int testNumber = 1;

        int flag = 0;
        if (isEnd){
            flag = 1;
        }

        String startDate = DateUtil.toDateStr(startTime,DateUtil.YMDHMS);
        String endDate = DateUtil.toDateStr(endTime,DateUtil.YMDHMS);

        String sign = EncodeUtil.md5(userId+startDate+DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD));

        mDataManager.getUploadStudyRecordService()
                .uploadStudyRecord(
                        format,String.valueOf(appId),appName,appName,String.valueOf(voaId),
                        String.valueOf(userId),device,deviceId,startDate,endDate,
                        String.valueOf(flag),wordCount,String.valueOf(testMode),platform,
                        String.valueOf(testNumber),sign,rewardVersion)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UploadRecordResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!isEnd){
                            return;
                        }

                        EventBus.getDefault().post(new NewReadListenEvent(NewReadListenEvent.type_showReport,""));
                    }

                    @Override
                    public void onNext(UploadRecordResult bean) {
                        if (!isEnd){
                            return;
                        }

                        if (bean!=null&&bean.getResult().equals("1")){
                            //获取数据显示
                            double price = Integer.parseInt(bean.getReward())*0.01;
                            if (price>0){
                                price = BigDecimalUtil.trans2Double(price);

                                //显示学习报告
                                EventBus.getDefault().post(new NewReadListenEvent(NewReadListenEvent.type_showReport,String.valueOf(price)));
                                return;
                            }
                        }

                        EventBus.getDefault().post(new NewReadListenEvent(NewReadListenEvent.type_showReport,""));
                    }
                });
    }

    //获取当前的单词数据(文本数据分割形成)
    public int getWordByIndex(List<VoaText> list,int index){
        int wordCount = 0;

        if (list!=null&&list.size()>0&&index<=list.size()-1){
            for (int i = 0; i < list.size(); i++) {
                //获取数据并进行分割
                if (index<i){
                    break;
                }

                String sentence = list.get(i).sentence();
                sentence = sentence.replace("!"," ");
                sentence = sentence.replace("."," ");
                sentence = sentence.replace("?"," ");
                sentence = sentence.trim();

                wordCount+=sentence.split(" ").length;
            }
        }
        return wordCount;
    }
}
