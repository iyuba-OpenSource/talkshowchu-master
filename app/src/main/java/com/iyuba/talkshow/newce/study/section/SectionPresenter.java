package com.iyuba.talkshow.newce.study.section;

import android.util.Pair;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.injection.ConfigPersistent;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Report_read;
import com.iyuba.talkshow.lil.help_mvp.util.BigDecimalUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BasePresenter;
import com.iyuba.talkshow.util.RxUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/8/31 18:10
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@ConfigPersistent
public class SectionPresenter extends BasePresenter<SectionView> {

    private final DataManager mDataManager;

    private Subscription getVoaDetailDis;
    private Disposable submitReadReportDis;

    //点击广告获取奖励
    private Disposable clickAdDis;
    //提交广告数据
    private Disposable submitAdDis;

    @Inject
    public SectionPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unsubscribe(getVoaDetailDis);
        RxUtil.unsubscribe(submitReadReportDis);
        RxUtil.unsubscribe(clickAdDis);
        RxUtil.unsubscribe(submitAdDis);
    }

    //获取当前章节的详情数据
    public void getVoaTexts(final int voaId) {
        checkViewAttached();
        RxUtil.unsubscribe(getVoaDetailDis);
        getVoaDetailDis = mDataManager.getVoaTexts(voaId)
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
                            getMvpView().showVoaText(getMargeSameSectionDetail(voaTextList));
                        }
                    }
                });
    }

    public void syncVoaTexts(final int voaId) {
        checkViewAttached();
        RxUtil.unsubscribe(getVoaDetailDis);
        getVoaDetailDis = mDataManager.syncVoaTexts(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VoaText>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showVoaText(null);
                    }

                    @Override
                    public void onNext(List<VoaText> voaTextList) {
                        if (voaTextList == null || voaTextList.size() == 0) {
                            getMvpView().showVoaText(null);
                        } else {
                            getMvpView().showVoaText(getMargeSameSectionDetail(voaTextList));
                        }
                    }
                });
    }

    //合并同一个段落的数据(因为直接从数据库中取出来的时候就是paraId和idIndex排序的，因此不用处理)
    public List<Pair<String,String>> getMargeSameSectionDetail(List<VoaText> list){
        if (list==null||list.size()==0){
            return null;
        }

//        Map<Integer,Pair<String,String>> detailMap = new HashMap<>();
//        for (int i = 0; i < list.size(); i++) {
//            VoaText detailBean = list.get(i);
//            int paraId = detailBean.paraId();
//
//            //判断下是否存在
//            if (detailMap.get(paraId)==null){
//                //没有直接保存
//                detailMap.put(paraId,new Pair<>(detailBean.sentence(),detailBean.sentenceCn()));
//            }else {
//                //有的话获取数据，拼接后保存
//                Pair<String,String> pair = detailMap.get(paraId);
//                //英文
//                String enText = pair.first;
//                //中文
//                String cnText = pair.second;
//
//                enText = enText+detailBean.sentence();
//                cnText = cnText+detailBean.sentenceCn();
//
//                //保存数据
//                detailMap.put(paraId,new Pair<>(enText,cnText));
//            }
//        }
//
//        //转换数据
//        List<Pair<Integer,Pair<String,String>>> detailList = new ArrayList<>();
//        if (detailMap!=null&&detailMap.keySet().size()>0){
//            for (int paraId:detailMap.keySet()){
//                detailList.add(new Pair<>(paraId,detailMap.get(paraId)));
//            }
//        }
//
//        //排序处理
//        Collections.sort(detailList, (o1, o2) -> o1.first - o2.first);
//
//        //数据处理
//        List<Pair<String,String>> pairList = new ArrayList<>();
//        for (int i = 0; i < detailList.size(); i++) {
//            pairList.add(detailList.get(i).second);
//        }

        List<Pair<String,String>> pairList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            VoaText voaText  = list.get(i);
            pairList.add(new Pair<>(voaText.sentence(),voaText.sentenceCn()));
        }

        return pairList;
    }

    //获取当前文章的单词数量
    public long getWordCount(List<Pair<String,String>> list){
        if (list!=null&&list.size()>0){
            //进行分段处理
            long wordCount = 0;
            for (int i = 0; i < list.size(); i++) {
                String sentence = list.get(i).first;
                //分解数据
                String[] array = sentence.split(" ");
                //合并数据
                wordCount+=array.length;
            }
            return wordCount;
        }
        return 0;
    }

    //提交阅读的学习报告数据
    public void submitReadReport(String types,String voaId,String lessonName,long wordCount,long startTime,long endTime){
        checkViewAttached();
        com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil.unDisposable(submitReadReportDis);
        CommonDataManager.submitReportRead(types, UserInfoManager.getInstance().getUserId(), lessonName,voaId,wordCount,startTime,endTime)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Observer<Report_read>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        submitReadReportDis = d;
                    }

                    @Override
                    public void onNext(Report_read bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.result.equals("1")){
                                getMvpView().showReadReportResult(true);

                                //显示奖励信息
                                double price = Integer.parseInt(bean.reward)*0.01;
                                if (price>0){
                                    price = BigDecimalUtil.trans2Double(price);
                                    String showMsg = String.format(ResUtil.getInstance().getString(R.string.reward_show),price);
                                    EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_dialog,showMsg));
                                }
                            }else {
                                getMvpView().showReadReportResult(false);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showReadReportResult(false);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
