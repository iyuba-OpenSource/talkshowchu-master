package com.iyuba.talkshow.lil.help_fix.manager.dataManager;

import com.iyuba.talkshow.lil.help_fix.model.remote.RemoteManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_data;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Collect_chapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Junior_chapter_collect;
import com.iyuba.talkshow.lil.help_fix.model.remote.newService.ConceptService;
import com.iyuba.talkshow.lil.help_fix.util.FixUtil;
import com.iyuba.talkshow.lil.help_fix.util.SignUtil;

import java.util.List;

import io.reactivex.Observable;

/**
 * @title: 数据操作-新概念
 * @date: 2023/7/4 16:22
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ConceptDataManager {

    /****************书籍*********************/

    /****************章节*********************/

    /******************章节详情****************/

    /*******************pdf******************/

    /*****************排行*******************/

    /*******************评测*****************/

    /************************************收藏**************************/
    //接口-收藏/取消收藏文章
    public static Observable<Collect_chapter> collectArticle(String types, String userId, String voaId, boolean isCollect){
        String groupName = "Iyuba";
        String sentenceFlag = "0";

        int appId = FixUtil.getAppId(types);
        String topic = FixUtil.getTopic(types);
        String sentenceId = "0";
        String type = "del";
        if (isCollect){
            type = "insert";
        }

        ConceptService commonService = RemoteManager.getInstance().createXml(ConceptService.class);
        return commonService.collectArticle(groupName,sentenceFlag,appId,userId,topic,voaId,sentenceId,type);
    }

    //接口-获取收藏的文章数据
    public static Observable<BaseBean_data<List<Junior_chapter_collect>>> getArticleCollect(String types, int userId){
        int appId = FixUtil.getAppId(types);
        String topic = FixUtil.getTopic(types);
        int flag = 0;
        String sign = SignUtil.getJuniorArticleCollectSign(topic,userId,appId);

        ConceptService commonService = RemoteManager.getInstance().createJson(ConceptService.class);
        return commonService.getArticleCollect(userId,sign,topic,appId,flag);
    }
}
