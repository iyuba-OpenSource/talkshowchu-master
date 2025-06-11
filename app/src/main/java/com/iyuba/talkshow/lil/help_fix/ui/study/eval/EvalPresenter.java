package com.iyuba.talkshow.lil.help_fix.ui.study.eval;

import android.text.TextUtils;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.lil.help_fix.data.bean.BookChapterBean;
import com.iyuba.talkshow.lil.help_fix.data.bean.ChapterDetailBean;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.UrlLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.NetHostManager;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.NovelDataManager;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterDetailEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.EvalEntity_chapter;
import com.iyuba.talkshow.lil.help_fix.model.local.util.DBTransUtil;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_data;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Marge_eval;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Publish_eval;
import com.iyuba.talkshow.lil.help_fix.model.remote.util.RemoteTransUtil;
import com.iyuba.talkshow.lil.help_mvp.mvp.BasePresenter;
import com.iyuba.talkshow.lil.help_mvp.util.BigDecimalUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/5/24 00:05
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class EvalPresenter extends BasePresenter<EvalView> {

    //提交单个评测
    private Disposable submitSingleEvalDis;
    //发布单个评测
    private Disposable publishSingleEvalDis;
    //合成音频
    private Disposable margeAudioDis;
    //发布合成音频
    private Disposable publishMargeAudioDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(submitSingleEvalDis);
        RxUtil.unDisposable(publishSingleEvalDis);
        RxUtil.unDisposable(margeAudioDis);
        RxUtil.unDisposable(publishMargeAudioDis);
    }

    //加载章节数据
    public BookChapterBean getChapterData(String types, String voaId) {
        if (TextUtils.isEmpty(types)) {
            return null;
        }


        switch (types) {
//            case TypeLibrary.BookType.junior_primary:
//            case TypeLibrary.BookType.junior_middle:
//                //中小学
//                ChapterEntity_junior junior = JuniorDataManager.getSingleChapterFromDB(voaId);
//                return DBTransUtil.transJuniorSingleChapterData(types, junior);
            case TypeLibrary.BookType.bookworm:
            case TypeLibrary.BookType.newCamstory:
            case TypeLibrary.BookType.newCamstoryColor:
                //小说
                ChapterEntity_novel novel = NovelDataManager.searchSingleChapterFromDB(types, voaId);
                return DBTransUtil.novelToSingleChapterData(novel);
        }
        return null;
    }

    //获取章节详情数据
    public List<ChapterDetailBean> getChapterDetail(String types, String voaId) {
        List<ChapterDetailBean> detailList = new ArrayList<>();
        if (TextUtils.isEmpty(types)) {
            return detailList;
        }

        switch (types) {
//            case TypeLibrary.BookType.junior_primary:
//            case TypeLibrary.BookType.junior_middle:
//                //中小学
//                List<ChapterDetailEntity_junior> juniorList = JuniorDataManager.getMultiChapterDetailFromDB(voaId);
//                if (juniorList != null && juniorList.size() > 0) {
//                    detailList = DBTransUtil.transJuniorChapterDetailData(juniorList);
//                }
//                break;
            case TypeLibrary.BookType.bookworm:
            case TypeLibrary.BookType.newCamstory:
            case TypeLibrary.BookType.newCamstoryColor:
                //小说
                List<ChapterDetailEntity_novel> novelList = NovelDataManager.searchMultiChapterDetailFromDB(types, voaId);
                if (novelList != null && novelList.size() > 0) {
                    detailList = DBTransUtil.novelToChapterDetailData(novelList);
                }
                break;
        }
        return detailList;
    }

    //判断是否可以使用
    public boolean isEvalNext(String types, String voaId, String paraId, String idIndex) {
        boolean isVip = UserInfoManager.getInstance().isVip();
        boolean isThan3 = CommonDataManager.getEvalChapterSizeFromDB(types, voaId) >= 3;
        boolean isHasEval = CommonDataManager.getEvalChapterDataFromDB(types, voaId, paraId, idIndex) != null;

        if (isVip || isHasEval || !isThan3) {
            return true;
        }
        return false;
    }

    //获取当前合成音频的分数(平均分)
    public int getMargeAudioScore(String types, String voaId) {
        List<EvalEntity_chapter> list = CommonDataManager.getEvalChapterByVoaIdFromDB(types, voaId);
        int avgScore = 0;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                EvalEntity_chapter chapter = list.get(i);
                avgScore += chapter.total_score * 20;
            }

            avgScore = avgScore / list.size();
        }
        return avgScore;
    }

    /*************************************提交单个评测********************/
    //提交单个评测
    public void submitSingleEval(String types, boolean isSentence, String voaId, String paraId, String indexId, String sentence, String filePath) {
        if (TextUtils.isEmpty(types)) {
            ToastUtil.showToast(ResUtil.getInstance().getContext(), "暂无该类型的数据");
            return;
        }

        switch (types) {
//            case TypeLibrary.BookType.junior_primary://小学
//            case TypeLibrary.BookType.junior_middle://初中
//                submitJuniorSingleEval(types, isSentence, voaId, paraId, indexId, sentence, filePath);
//                break;
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥英语小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥英语小说馆彩绘
                submitNovelSingleEval(types, voaId, paraId, indexId, sentence, filePath);
                break;
        }
    }

    //中小学-提交单个评测
//    private void submitJuniorSingleEval(String types, boolean isSentence, String voaId, String paraId, String indexId, String sentence, String filePath) {
//        checkViewAttach();
//        RxUtil.unDisposable(submitSingleEvalDis);
//        JuniorDataManager.submitLessonSingleEval(types, isSentence, voaId, paraId, indexId, sentence, filePath)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<BaseBean_data<Eval_result>>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                        submitSingleEvalDis = d;
//                    }
//
//                    @Override
//                    public void onNext(@NonNull BaseBean_data<Eval_result> bean) {
//                        if (getMvpView() != null) {
//                            if (bean.getResult().equals("1")) {
//                                //保存在数据库
//                                CommonDataManager.saveEvalChapterDataToDB(RemoteTransUtil.transSingleEvalChapterData(types, voaId, paraId, indexId, filePath, bean.getData()));
//                                //从数据库取出
//                                EvalEntity_chapter chapter = CommonDataManager.getEvalChapterDataFromDB(types, voaId, paraId, indexId);
//                                //展示数据
//                                getMvpView().showSingleEval(DBTransUtil.transEvalSingleChapterData(chapter));
//                            } else {
//                                getMvpView().showSingleEval(null);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        if (getMvpView() != null) {
//                            getMvpView().showSingleEval(null);
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

    //小说-提交单个评测
    private void submitNovelSingleEval(String types, String voaId, String paraId, String indexId, String sentence, String filePath) {
        checkViewAttach();
        RxUtil.unDisposable(submitSingleEvalDis);
        NovelDataManager.submitLessonSingleEval(types, voaId, paraId, indexId, sentence, filePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<Eval_result>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        submitSingleEvalDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<Eval_result> bean) {
                        if (getMvpView() != null) {
                            if (bean.getResult().equals("1")) {
                                //保存在数据库
                                CommonDataManager.saveEvalChapterDataToDB(RemoteTransUtil.transSingleEvalChapterData(types, voaId, paraId, indexId, filePath, bean.getData()));
                                //从数据库取出
                                EvalEntity_chapter chapter = CommonDataManager.getEvalChapterDataFromDB(types, voaId, paraId, indexId);
                                //展示数据
                                getMvpView().showSingleEval(DBTransUtil.transEvalSingleChapterData(chapter));
                            } else {
                                getMvpView().showSingleEval(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView() != null) {
                            getMvpView().showSingleEval(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*****************************************发布单个评测*************************/
    //发布单个评测
    public void publishSingleEval(String bookType, String voaId, String idIndex, String paraId, int score, String content) {
        if (TextUtils.isEmpty(bookType)) {
            ToastUtil.showToast(ResUtil.getInstance().getContext(), "暂无该类型的数据");
            return;
        }

        switch (bookType) {
//            case TypeLibrary.BookType.junior_primary://小学
//            case TypeLibrary.BookType.junior_middle://初中
//                String juniorPlayPrefix = UrlLibrary.HTTP_USERSPEECH + NetHostManager.getInstance().getDomainShort() + "/voa/";
//                if (content.startsWith(juniorPlayPrefix)) {
//                    content = content.replace(juniorPlayPrefix, "");
//                }
//                publishJuniorSingleEval(bookType, voaId, idIndex, paraId, score, content);
//                break;
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥英语小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥英语小说馆彩绘
                String novelPlayPrefix = UrlLibrary.HTTP_IUSERSPEECH + NetHostManager.getInstance().getDomainShort() +UrlLibrary.SUFFIX_9001+ "/voa/";
                if (content.startsWith(novelPlayPrefix)) {
                    content = content.replace(novelPlayPrefix, "");
                }
                publishNovelSingleEval(bookType, voaId, idIndex, paraId, score, content);
                break;
        }
    }

    //中小学-发布单个评测
//    private void publishJuniorSingleEval(String bookType, String voaId, String idIndex, String paraId, int score, String content) {
//        checkViewAttach();
//        RxUtil.unDisposable(publishSingleEvalDis);
//        JuniorDataManager.publishSingleEval(bookType, voaId, paraId, idIndex, score, content)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Publish_eval>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        publishSingleEvalDis = d;
//                    }
//
//                    @Override
//                    public void onNext(Publish_eval bean) {
//                        if (getMvpView() != null) {
//                            if (bean.getMessage().toLowerCase().equals("ok")) {
//                                getMvpView().showPublishRank(true, bean);
//                            } else {
//                                getMvpView().showPublishRank(true, null);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (getMvpView() != null) {
//                            getMvpView().showPublishRank(true, null);
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

    //小说-发布单个评测
    private void publishNovelSingleEval(String bookType, String voaId, String idIndex, String paraId, int score, String content) {
        checkViewAttach();
        RxUtil.unDisposable(publishSingleEvalDis);
        NovelDataManager.publishSingleEval(bookType, voaId, paraId, idIndex, score, content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Publish_eval>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        publishSingleEvalDis = d;
                    }

                    @Override
                    public void onNext(Publish_eval bean) {
                        if (getMvpView() != null) {
                            if (bean.getMessage().toLowerCase().equals("ok")) {
                                getMvpView().showPublishRank(true, bean);
                            } else {
                                getMvpView().showPublishRank(true, null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView() != null) {
                            getMvpView().showPublishRank(true, null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*************************************合成音频***********************/
    //合成音频
    public void margeAudio(String types, String voaId) {
        if (TextUtils.isEmpty(types)) {
            ToastUtil.showToast(ResUtil.getInstance().getContext(), "暂无该类型的数据");
            return;
        }

        switch (types) {
//            case TypeLibrary.BookType.junior_primary://小学
//            case TypeLibrary.BookType.junior_middle://初中
//                margeJuniorAudio(types, voaId);
//                break;
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥英语小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥英语小说馆彩绘
                margeNovelAudio(types, voaId);
                break;
        }
    }

    //中小学-合成音频
//    public void margeJuniorAudio(String types, String voaId) {
//        checkViewAttach();
//        RxUtil.unDisposable(margeAudioDis);
//        JuniorDataManager.margeAudioEval(types, voaId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Marge_eval>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                        margeAudioDis = d;
//                    }
//
//                    @Override
//                    public void onNext(@NonNull Marge_eval bean) {
//                        if (getMvpView() != null) {
//                            if (bean.getResult().equals("1")) {
//                                getMvpView().showMargeAudio(bean.getUrl());
//                            } else {
//                                getMvpView().showMargeAudio(null);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        if (getMvpView() != null) {
//                            getMvpView().showMargeAudio(null);
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

    //小说-合成音频
    public void margeNovelAudio(String types, String voaId) {
        checkViewAttach();
        RxUtil.unDisposable(margeAudioDis);
        NovelDataManager.margeAudioEval(types, voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Marge_eval>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        margeAudioDis = d;
                    }

                    @Override
                    public void onNext(Marge_eval bean) {
                        if (getMvpView() != null) {
                            if (bean.getResult().equals("1")) {
                                getMvpView().showMargeAudio(bean.getUrl());
                            } else {
                                getMvpView().showMargeAudio(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView() != null) {
                            getMvpView().showMargeAudio(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /***************************************发布合成后的音频**************************/
    //发布合成音频
    public void publishMargeAudio(String types, String voaId, String margeAudioUrl) {
        if (TextUtils.isEmpty(types)) {
            ToastUtil.showToast(ResUtil.getInstance().getContext(), "暂无该类型的数据");
            return;
        }

        switch (types) {
//            case TypeLibrary.BookType.junior_primary://小学
//            case TypeLibrary.BookType.junior_middle://初中
//                String juniorPlayPrefix = UrlLibrary.HTTP_USERSPEECH + NetHostManager.getInstance().getDomainShort() + "/voa/";
//                if (margeAudioUrl.startsWith(juniorPlayPrefix)) {
//                    margeAudioUrl = margeAudioUrl.replace(juniorPlayPrefix, "");
//                }
//
//                publishJuniorMargeAudio(types, voaId, margeAudioUrl);
//                break;
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥英语小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥英语小说馆彩绘
                String novelPlayPrefix = UrlLibrary.HTTP_IUSERSPEECH + NetHostManager.getInstance().getDomainShort() + UrlLibrary.SUFFIX_9001+"/voa/";
                if (margeAudioUrl.startsWith(novelPlayPrefix)) {
                    margeAudioUrl = margeAudioUrl.replace(novelPlayPrefix, "");
                }

                publishNovelMargeAudio(types, voaId, margeAudioUrl);
                break;
        }
    }

    //中小学-发布合成后的音频
//    public void publishJuniorMargeAudio(String types, String voaId, String margeAudioUrl) {
//        checkViewAttach();
//        RxUtil.unDisposable(publishMargeAudioDis);
//        JuniorDataManager.publishMargeEval(types, voaId, margeAudioUrl)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Publish_eval>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                        publishMargeAudioDis = d;
//                    }
//
//                    @Override
//                    public void onNext(@NonNull Publish_eval bean) {
//                        if (getMvpView() != null) {
//                            if (bean.getMessage().toLowerCase().equals("ok")) {
//                                getMvpView().showPublishRank(false, bean);
//                            } else {
//                                getMvpView().showPublishRank(false, null);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        if (getMvpView() != null) {
//                            getMvpView().showPublishRank(false, null);
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

    //小说-发布合成后的音频
    public void publishNovelMargeAudio(String types, String voaId, String margeAudioUrl){
        checkViewAttach();
        RxUtil.unDisposable(publishMargeAudioDis);
        NovelDataManager.publishMargeEval(types, voaId, margeAudioUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Publish_eval>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        publishMargeAudioDis = d;
                    }

                    @Override
                    public void onNext(Publish_eval bean) {
                        if (getMvpView() != null) {
                            if (bean.getMessage().toLowerCase().equals("ok")) {
                                getMvpView().showPublishRank(false, bean);

                                //显示奖励的信息
                                double price = Integer.parseInt(bean.getReward())*0.01;
                                if (price>0){
                                    price = BigDecimalUtil.trans2Double(price);
                                    String showMsg = String.format(ResUtil.getInstance().getString(R.string.reward_show),price);
                                    EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_dialog,showMsg));
                                }
                            } else {
                                getMvpView().showPublishRank(false, null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView() != null) {
                            getMvpView().showPublishRank(false, null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*******************************辅助功能********************/
    //判断能否合成音频数据
    public boolean isCanMargeAudio(String types, String voaId){
        if (TextUtils.isEmpty(types)){
            return false;
        }

        switch (types){
            case TypeLibrary.BookType.bookworm:
            case TypeLibrary.BookType.newCamstory:
            case TypeLibrary.BookType.newCamstoryColor:
                List<EvalEntity_chapter> evalList = CommonDataManager.getEvalChapterByVoaIdFromDB(types, voaId);
                if (evalList!=null&&evalList.size()>1){
                    return true;
                }
        }
        return false;
    }
}
