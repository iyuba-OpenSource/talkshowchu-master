package com.iyuba.talkshow.lil.help_fix.ui.study.section;

import android.text.TextUtils;
import android.util.Pair;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.lil.help_fix.data.bean.BookChapterBean;
import com.iyuba.talkshow.lil.help_fix.data.bean.ChapterDetailBean;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.NovelDataManager;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterDetailEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.util.DBTransUtil;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Report_read;
import com.iyuba.talkshow.lil.help_mvp.mvp.BasePresenter;
import com.iyuba.talkshow.lil.help_mvp.util.BigDecimalUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/7/7 09:11
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class SectionPresenter extends BasePresenter<SectionView> {

    //提交阅读的学习报告
    private Disposable readReportDis;
    //点击广告操作
    private Disposable clickAdDis;
    //提交广告信息操作
    private Disposable submitAdDis;

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unDisposable(readReportDis);
        RxUtil.unDisposable(clickAdDis);
        RxUtil.unDisposable(submitAdDis);
    }

    //加载章节数据
    public BookChapterBean getChapterData(String types, String voaId){
        if (TextUtils.isEmpty(types)){
            return null;
        }

        switch (types){
            case TypeLibrary.BookType.junior_primary://小学
            case TypeLibrary.BookType.junior_middle://初中
                //中小学

            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                ChapterEntity_novel novel = NovelDataManager.searchSingleChapterFromDB(types, voaId);
                return DBTransUtil.novelToSingleChapterData(novel);
            case TypeLibrary.BookType.conceptFour:
                //新概念全四册

            case TypeLibrary.BookType.conceptJunior:
                //新概念青少版

        }
        return null;
    }

    //加载章节详情数据
    private List<ChapterDetailBean> getChapterDetail(String types, String voaId){
        List<ChapterDetailBean> detailList = new ArrayList<>();
        if (TextUtils.isEmpty(types)){
            return detailList;
        }

        switch (types){
//            case TypeLibrary.BookType.junior_primary://小学
//            case TypeLibrary.BookType.junior_middle://初中
//                //中小学
//                List<ChapterDetailEntity_junior> juniorList = JuniorDataManager.getMultiChapterDetailFromDB(voaId);
//                if (juniorList!=null&&juniorList.size()>0){
//                    detailList = DBTransUtil.transJuniorChapterDetailData(juniorList);
//                }
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                //小说
                List<ChapterDetailEntity_novel> novelList = NovelDataManager.searchMultiChapterDetailFromDB(types, voaId);
                if (novelList!=null&&novelList.size()>0){
                    detailList = DBTransUtil.novelToChapterDetailData(novelList);
                }
        }
        return detailList;
    }

    //合并同一个段落的数据(因为直接从数据库中取出来的时候就是paraId和idIndex排序的，因此不用处理)
    public List<Pair<String,String>> getMargeSameSectionDetail(String types,String voaId){
        List<ChapterDetailBean> list = getChapterDetail(types, voaId);
        if (list==null||list.size()==0){
            return null;
        }

        Map<String,Pair<String,String>> detailMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            ChapterDetailBean detailBean = list.get(i);
            String paraId = detailBean.getParaId();

            //判断下是否存在
            if (detailMap.get(paraId)==null){
                //没有直接保存
                detailMap.put(paraId,new Pair<>(detailBean.getSentence(),detailBean.getSentenceCn()));
            }else {
                //有的话获取数据，拼接后保存
                Pair<String,String> pair = detailMap.get(paraId);
                //英文
                String enText = pair.first;
                //中文
                String cnText = pair.second;

                enText = enText+detailBean.getSentence();
                cnText = cnText+detailBean.getSentenceCn();

                //保存数据
                detailMap.put(paraId,new Pair<>(enText,cnText));
            }
        }

        //转换数据
        List<Pair<Integer,Pair<String,String>>> detailList = new ArrayList<>();
        if (detailMap!=null&&detailMap.keySet().size()>0){
            for (String key:detailMap.keySet()){
                int paraId = Integer.parseInt(key);

                detailList.add(new Pair<>(paraId,detailMap.get(key)));
            }
        }

        //排序处理
        Collections.sort(detailList, (o1, o2) -> o1.first - o2.first);

        //数据处理
        List<Pair<String,String>> pairList = new ArrayList<>();
        for (int i = 0; i < detailList.size(); i++) {
            pairList.add(detailList.get(i).second);
        }
        return pairList;
    }

    //获取当前文章的单词数量
    public long getWordCount(String types, String voaId){
        List<ChapterDetailBean> list = getChapterDetail(types, voaId);
        if (list!=null&&list.size()>0){
            //进行分段处理
            long wordCount = 0;
            for (int i = 0; i < list.size(); i++) {
                String sentence = list.get(i).getSentence();
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
        checkViewAttach();
        RxUtil.unDisposable(readReportDis);
        CommonDataManager.submitReportRead(types, UserInfoManager.getInstance().getUserId(),lessonName,voaId,wordCount,startTime,endTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Report_read>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        readReportDis = d;
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
