package com.iyuba.talkshow.lil.help_fix.ui.study.rank.rank_detail;

import android.text.TextUtils;

import com.iyuba.talkshow.lil.help_fix.data.bean.ChapterDetailBean;
import com.iyuba.talkshow.lil.help_fix.data.bean.EvalRankDetailBean;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.NovelDataManager;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.AgreeEntity;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterDetailEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.util.DBTransUtil;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_data;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_rank_agree;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_rank_detail;
import com.iyuba.talkshow.lil.help_fix.model.remote.util.RemoteTransUtil;
import com.iyuba.talkshow.lil.help_mvp.mvp.BasePresenter;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/5/25 14:48
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RankDetailPresenter extends BasePresenter<RankDetailView> {

    //排行详情
    private Disposable rankDetailDis;
    //点赞数据
    private Disposable agreeEvalDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(rankDetailDis);
        RxUtil.unDisposable(agreeEvalDis);
    }

    //判断是否已经点赞
    public boolean isAgreeEvalSentence(String agreeUserId,String types,String voaId,String sentenceId){
        AgreeEntity agreeData = CommonDataManager.getAgreeDataFromDB(String.valueOf(UserInfoManager.getInstance().getUserId()), agreeUserId,types,voaId,sentenceId);
        return agreeData!=null;
    }

    //获取章节详情数据
    public List<ChapterDetailBean> getChapterDetail(String types, String voaId){
        List<ChapterDetailBean> detailList = new ArrayList<>();
        if (TextUtils.isEmpty(types)){
            return detailList;
        }

        switch (types){
//            case TypeLibrary.BookType.junior_primary:
//            case TypeLibrary.BookType.junior_middle:
//                //中小学
//                List<ChapterDetailEntity_junior> juniorList = JuniorDataManager.getMultiChapterDetailFromDB(voaId);
//                if (juniorList!=null&&juniorList.size()>0){
//                    detailList = DBTransUtil.transJuniorChapterDetailData(juniorList);
//                }
//                break;
            case TypeLibrary.BookType.bookworm:
            case TypeLibrary.BookType.newCamstory:
            case TypeLibrary.BookType.newCamstoryColor:
                List<ChapterDetailEntity_novel> novelList = NovelDataManager.searchMultiChapterDetailFromDB(types, voaId);
                if (novelList!=null&&novelList.size()>0){
                    detailList = DBTransUtil.novelToChapterDetailData(novelList);
                }
                break;
        }

        return detailList;
    }

    //获取排行详情数据
    public void getRankDetailData(String types,String voaId,String showUserId){
        checkViewAttach();
        RxUtil.unDisposable(rankDetailDis);
        CommonDataManager.getEvalRankDetailData(types, voaId, showUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<List<Eval_rank_detail>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        rankDetailDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<List<Eval_rank_detail>> bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.getResult().equals("true")){
                                //转换数据
                                List<EvalRankDetailBean> list = RemoteTransUtil.transEvalRankDetailData(types,voaId,bean.getData());
                                //展示数据
                                getMvpView().showRankEvalDetailData(list);
                            }else {
                                getMvpView().showRankEvalDetailData(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showRankEvalDetailData(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //点赞评测数据
    public void agreeEvalData(String userId,String agreeId,String types,String voaId,String evalSentenceId){
        checkViewAttach();
        RxUtil.unDisposable(agreeEvalDis);
        CommonDataManager.agreeEvalRankDetailData(userId, evalSentenceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Eval_rank_agree>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        agreeEvalDis = d;
                    }

                    @Override
                    public void onNext(Eval_rank_agree bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.getMessage().toLowerCase().equals("ok")){
                                //保存在数据库
                                CommonDataManager.saveAgreeDataToDB(RemoteTransUtil.transSingleAgreeEvalData(userId, agreeId, types, voaId, evalSentenceId));
                                //获取数据
                                AgreeEntity agreeData = CommonDataManager.getAgreeDataFromDB(userId, agreeId, types, voaId, evalSentenceId);
                                getMvpView().refreshAgreeData(agreeData!=null);
                            }else {
                                getMvpView().refreshAgreeData(false);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().refreshAgreeData(false);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
