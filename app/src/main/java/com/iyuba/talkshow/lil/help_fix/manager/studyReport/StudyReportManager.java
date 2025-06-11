package com.iyuba.talkshow.lil.help_fix.manager.studyReport;

import android.text.TextUtils;
import android.util.Log;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.lil.help_fix.data.bean.ChapterDetailBean;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.NovelDataManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Study_report;
import com.iyuba.talkshow.lil.help_mvp.util.BigDecimalUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title: 学习报告管理
 * @date: 2023/6/26 16:41
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class StudyReportManager {

    private static StudyReportManager instance;
    //听力学习报告记录
    private ListenStudyReportBean listenBean;

    public static StudyReportManager getInstance(){
        if (instance==null){
            synchronized (StudyReportManager.class){
                if (instance==null){
                    instance = new StudyReportManager();
                }
            }
        }
        return instance;
    }

    /**********************听力学习报告***********************/
    //保存听力学习报告内容
    public void saveListenReportData(long startTime,List<ChapterDetailBean> list){
        listenBean = new ListenStudyReportBean();
        listenBean.setWordCount(list);
        listenBean.setStartTime(startTime);
    }

    //提交听力学习报告
    public void submitListenReportData(String types,long endTime,boolean hasFinish,String voaId){
        if (listenBean!=null){
            if (endTime - listenBean.getStartTime() < 3*1000){
                return;
            }

            int endFlag = 0;
            if (hasFinish){
                endFlag = 1;
            }

            Log.d("学习报告", "submitListenReportData: --"+types+"--"+voaId+"--"+listenBean.getWordCount());

//            if (types.equals(TypeLibrary.BookType.junior_primary)
//                    ||types.equals(TypeLibrary.BookType.junior_middle)){
//                //中小学
//                uploadJuniorListenStudReportData(types, UserManager.getInstance().getUserId(),voaId, listenBean.getStartTime(), endTime,endFlag, listenBean.getWordCount());
//            }else
                if (types.equals(TypeLibrary.BookType.bookworm)
                    ||types.equals(TypeLibrary.BookType.newCamstory)
                    ||types.equals(TypeLibrary.BookType.newCamstoryColor)){
                //小说
                updateNovelListenStudyReportData(types, UserInfoManager.getInstance().getUserId(), voaId, listenBean.getStartTime(), endTime,endFlag, listenBean.getWordCount());
            }
            listenBean = null;
        }
    }

    //中小学-提交听力学习报告-接口
    /*private void uploadJuniorListenStudReportData(String types,int uid,String voaId,long startTime,long endTime,int endFlag,int wordCount){
        JuniorDataManager.uploadListenStudyReportData(types, voaId, uid, startTime, endTime, wordCount, endFlag)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Study_report>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Study_report study_report) {
                        Log.d("学习报告", "onError: 听力学习报告提交完成--"+study_report.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("学习报告", "onError: 听力学习报告提交失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }*/

    //小说-提交听力学习报告-接口
    private void updateNovelListenStudyReportData(String types,int uid,String voaId,long startTime,long endTime,int endFlag,int wordCount){
        NovelDataManager.uploadListenStudyReportData(types, voaId, uid, startTime, endTime, wordCount, endFlag)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Study_report>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Study_report bean) {
                        Log.d("学习报告", "onError: 听力学习报告提交完成--"+bean.getMessage());
                        if (bean!=null&&!TextUtils.isEmpty(bean.getReward())){
                            double price = Integer.parseInt(bean.getReward());
                            if (price>0){
                                price = BigDecimalUtil.trans2Double(price*0.01f);
                                String showMsg = String.format(ResUtil.getInstance().getString(R.string.reward_show),price);
                                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_toast,showMsg));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("学习报告", "onError: 听力学习报告提交失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*************************单词学习报告********************/
    //提交单词学习报告
//    public void submitWordReportData(String bookType, String bookId, long enterStartTime,Map<WordBean,WordBean> saveMap){
//        List<WordStudyReportSubmitBean.TestListBean> testList = new ArrayList<>();
//
//        String type = "单词闯关";
//        String mode = "W";
//        String startTime = DateUtil.toDateStr(enterStartTime,DateUtil.YMD);
//
//        if (saveMap!=null&&saveMap.keySet().size()>0){
//            for (WordBean bean:saveMap.keySet()){
//                //当前正确的单词
//                String rightWord = bean.getWord();
//                //当前选中的单词
//                String selectWord = saveMap.get(bean).getWord();
//                //是否正确
//                int result = 0;
//                if (rightWord.equals(selectWord)){
//                    result = 1;
//                }
//                //测试时间
//                String testTime = DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD);
//                //单元id
//                String unitId = bean.getId();
//                //位置
//                String position = bean.getPosition();
//
//                testList.add(new WordStudyReportSubmitBean.TestListBean(
//                        result,
//                        startTime,
//                        type,
//                        unitId,
//                        rightWord,
//                        Integer.parseInt(position),
//                        mode,
//                        testTime,
//                        selectWord
//                ));
//            }
//        }
//
//        uploadWordStudyReportData(bookType,bookId,testList);
//    }
//
//    //提交单词学习报告数据
//    private void uploadWordStudyReportData(String bookType,String bookId,List<WordStudyReportSubmitBean.TestListBean> testListBeanList){
//        JuniorDataManager.uploadWordStudyReportData(bookType, bookId, testListBeanList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(new Observer<Study_report>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(Study_report study_report) {
//                        Log.d("学习报告", "onError: 单词学习报告提交完成--"+study_report.getMessage());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("学习报告", "onError: 单词学习报告提交失败");
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }
}
