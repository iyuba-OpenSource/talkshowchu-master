package com.iyuba.talkshow.lil.help_fix.model.remote.newService;

import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.UrlLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.NetHostManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_data;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Collect_chapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Junior_chapter_collect;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Pdf_url;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * @title: 服务-新概念
 * @date: 2023/7/4 16:53
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface ConceptService {

    /******************************pdf************************************/
    //获取新概念的pdf下载链接-英文
    //http://apps.iyuba.cn/iyuba/getConceptPdfFile_eg.jsp?type=concept&voaid=1002
    @Headers({StrLibrary.urlPrefix+":"+ UrlLibrary.HTTP_APPS,StrLibrary.urlHost+":"+ NetHostManager.domain_short})
    @GET(UrlLibrary.concept_pdf_url_eg)
    Observable<Pdf_url> getConceptEnPdfDownloadUrl(@Query(StrLibrary.type) String type,
                                                   @Query(StrLibrary.voaid) String voaId);

    //获取新概念的pdf下载链接-双语
    //http://apps.iyuba.cn/iyuba/getConceptPdfFile.jsp?type=concept&voaid=1002
    @Headers({StrLibrary.urlPrefix+":"+ UrlLibrary.HTTP_APPS,StrLibrary.urlHost+":"+ NetHostManager.domain_short})
    @GET(UrlLibrary.concept_pdf_url)
    Observable<Pdf_url> getConceptPdfDownloadUrl(@Query(StrLibrary.type) String type,
                                                 @Query(StrLibrary.voaid) String voaId);

    /****************************************收藏*****************************/
    //收藏/取消收藏文章
    //http://apps.iyuba.cn/iyuba/updateCollect.jsp?groupName=Iyuba&sentenceFlg=0&appId=260&userId=12071118&topic=primary&voaId=313026&sentenceId=0&type=insert
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_APPS,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(UrlLibrary.COLLECT_ARTICLE)
    Observable<Collect_chapter> collectArticle(@Query(StrLibrary.groupName) String groupName,
                                               @Query(StrLibrary.sentenceFlg) String sentenceFlg,
                                               @Query(StrLibrary.appId) int appId,
                                               @Query(StrLibrary.userId) String userId,
                                               @Query(StrLibrary.topic) String topic,
                                               @Query(StrLibrary.voaId) String voaId,
                                               @Query(StrLibrary.sentenceId) String sentenceId,
                                               @Query(StrLibrary.type) String type);

    //获取收藏的文章数据
    //http://cms.iyuba.cn/dataapi/jsp/getCollect.jsp?userId=12071118&sign=a9f0a998cf149fd187145a3abb176a30&topic=primary&appid=260&sentenceFlg=0
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_CMS,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(UrlLibrary.COURSE_COLLECT)
    Observable<BaseBean_data<List<Junior_chapter_collect>>> getArticleCollect(@Query(StrLibrary.userId) int userId,
                                                                              @Query(StrLibrary.sign) String sign,
                                                                              @Query(StrLibrary.topic) String topic,
                                                                              @Query(StrLibrary.appid) int appId,
                                                                              @Query(StrLibrary.sentenceFlg) int flag);
}
