package com.iyuba.talkshow.lil.help_fix.ui.study.read;

import android.text.TextUtils;

import com.iyuba.talkshow.lil.help_fix.data.bean.BookChapterBean;
import com.iyuba.talkshow.lil.help_fix.data.bean.ChapterDetailBean;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.NovelDataManager;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterDetailEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.util.DBTransUtil;
import com.iyuba.talkshow.lil.help_mvp.mvp.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @title:
 * @date: 2023/5/22 19:06
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ReadPresenter extends BasePresenter<ReadView> {

    @Override
    public void detachView() {
        super.detachView();
    }

    //加载章节数据
    public BookChapterBean getChapterData(String types, String voaId){
        if (TextUtils.isEmpty(types)){
            return null;
        }

        switch (types){
//            case TypeLibrary.BookType.junior_primary://小学
//            case TypeLibrary.BookType.junior_middle://初中
//                //中小学
//                ChapterEntity_junior junior = JuniorDataManager.getSingleChapterFromDB(voaId);
//                return DBTransUtil.transJuniorSingleChapterData(types,junior);
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                ChapterEntity_novel novel = NovelDataManager.searchSingleChapterFromDB(types, voaId);
                return DBTransUtil.novelToSingleChapterData(novel);
        }
        return null;
    }

    //加载章节详情数据
    public List<ChapterDetailBean> getChapterDetail(String types, String voaId){
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
//                break;
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                //小说
                List<ChapterDetailEntity_novel> novelList = NovelDataManager.searchMultiChapterDetailFromDB(types, voaId);
                if (novelList!=null&&novelList.size()>0){
                    detailList = DBTransUtil.novelToChapterDetailData(novelList);
                }
                break;
        }
        return detailList;
    }
}
