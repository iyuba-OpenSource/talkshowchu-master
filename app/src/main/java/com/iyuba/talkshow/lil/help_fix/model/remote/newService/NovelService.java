package com.iyuba.talkshow.lil.help_fix.model.remote.newService;

import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.UrlLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.NetHostManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_bookInfo_texts;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_data;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_novelChapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Collect_chapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Marge_eval;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_book;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_chapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_chapter_collect;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_chapter_detail;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Pdf_url;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Publish_eval;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Study_report;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @title: 服务-小说
 * @date: 2023/7/4 16:53
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface NovelService {

    /***************************书籍*******************************/
    //小说-书籍数据
    //http://apps.iyuba.cn/book/getStroryInfo.jsp?types=home&level=2&from=bookworm
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_APPS, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET(UrlLibrary.novel_book)
    Observable<BaseBean_data<List<Novel_book>>> getBookData(@Query(StrLibrary.types) String types,
                                                            @Query(StrLibrary.level) int level,
                                                            @Query(StrLibrary.from) String from);

    /***************************章节*******************************/
    //小说-章节数据
    //http://apps.iyuba.cn/book/getStroryInfo.jsp?types=book&level=1&orderNumber=4&from=bookworm
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_APPS, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET(UrlLibrary.novel_chapter)
    Observable<BaseBean_novelChapter<Novel_book, List<Novel_chapter>>> getChapterData(@Query(StrLibrary.types) String types,
                                                                                      @Query(StrLibrary.level) String level,
                                                                                      @Query(StrLibrary.orderNumber) String orderNumber,
                                                                                      @Query(StrLibrary.from) String from);

    /***************************章节详情*******************************/
    //接口可行，暂时不用
    /*//小说-章节的详情内容
    //http://apps.iyuba.cn/book/getStroryInfo.jsp?types=detail&level=3&orderNumber=1&chapterOrder=3&from=bookworm
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_APPS, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET(UrlLibrary.novel_chapter_detail)
    Observable<BaseBean_novelChapterDetail<List<Novel_chapter_detail>>> getNovelChapterDetailData(@Query(StrLibrary.types) String types,
                                                                                                  @Query(StrLibrary.level) int level,
                                                                                                  @Query(StrLibrary.orderNumber) String orderNumber,
                                                                                                  @Query(StrLibrary.chapterOrder) String chapterOrder,
                                                                                                  @Query(StrLibrary.from) String from);*/

    //小说-章节详情数据
    //http://apps.iyuba.cn/book/getStroryInfo.jsp?types=detail&from=bookworm&voaid=20101
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_APPS, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET(UrlLibrary.novel_chapter_detail)
    Observable<BaseBean_bookInfo_texts<Novel_book, List<Novel_chapter_detail>>> getNovelChapterDetailData(@Query(StrLibrary.types) String types,
                                                                                                          @Query(StrLibrary.from) String from,
                                                                                                          @Query(StrLibrary.voaid) String voaId);

    /************************************************pdf************************************/
    //获取小说的pdf下载链接--(双语：cn，英文：en)
    //http://apps.iyuba.cn/iyuba/getBookWormPdf.jsp?voaid=10101&type=bookworm&language=cn
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_APPS, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET(UrlLibrary.novel_pdf_url)
    Observable<Pdf_url> getNovelPdfDownloadUrl(@Query(StrLibrary.type) String type,
                                               @Query(StrLibrary.voaid) String voaId,
                                               @Query(StrLibrary.language) String language);


    /*************************************************评测*******************************/
    /**
     * 原文评测
     * http://iuserspeech.iyuba.cn:9001/test/ai/
     * type			bookworm
     * userId			14243581
     * newsId			10101
     * paraId			1
     * IdIndex			1
     * sentence			Hank Morgan works in a machine factory in Connecticut USA. The year is 1879.
     * file	application/octet-stream	record142435811010111	7.46 KB (7,640 bytes)
     * wordId			0
     * flg			0
     * appId			285
     */
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_IUSERSPEECH, StrLibrary.urlHost + ":" + NetHostManager.domain_short, StrLibrary.urlSuffix + ":" + UrlLibrary.SUFFIX_9001})
    @POST(UrlLibrary.novel_lesson_eval)
    Observable<BaseBean_data<Eval_result>> submitLessonSingleEval(@Body RequestBody body);

    /**
     * 发布单个评测数据
     * http://voa.iyuba.cn/voa/UnicomApi?topic=bookworm&platform=android&protocol=60002&format=json&userid=14243581&voaid=10101&username=iyuppoojhg&shuoshuotype=2&paraid=2&idIndex=1&score=33&content=wav8%2F202307%2Fbookworm%2F20230705%2F16885207937304418.mp3
     */
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_VOA, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @FormUrlEncoded
    @POST(UrlLibrary.novel_eval_publish)
    Observable<Publish_eval> publishSingleEval(@Field(StrLibrary.topic) String topic,
                                               @Field(StrLibrary.platform) String platform,
                                               @Field(StrLibrary.protocol) int protocol,
                                               @Field(StrLibrary.format) String format,
                                               @Field(StrLibrary.userid) int userId,
                                               @Field(StrLibrary.voaid) String voaId,
                                               @Field(StrLibrary.username) String userName,
                                               @Field(StrLibrary.shuoshuotype) int shuoshuoType,
                                               @Field(StrLibrary.paraid) String paraid,
                                               @Field(StrLibrary.idIndex) String idIndex,
                                               @Field(StrLibrary.score) int score,
                                               @Field(StrLibrary.content) String content);

    /**
     * 合并评测的音频
     * http://iuserspeech.iyuba.cn:9001/test/merge/?audios=wav8%2F202307%2Fbookworm%2F20230705%2F16885203974587634.mp3%2Cwav8%2F202307%2Fbookworm%2F20230705%2F16885207075995644.mp3&type=bookworm
     */
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_IUSERSPEECH, StrLibrary.urlHost + ":" + NetHostManager.domain_short, StrLibrary.urlSuffix + ":" + UrlLibrary.SUFFIX_9001})
    @GET(UrlLibrary.EVAL_MARGE)
    Observable<Marge_eval> margeAudioEval(@Query(StrLibrary.audios) String audios,
                                          @Query(StrLibrary.type) String type);

    /**
     * 发布合成后的音频
     * http://voa.iyuba.cn/voa/UnicomApi?topic=bookworm&platform=android&protocol=60003&format=json&userid=12071118&voaid=10101&shuoshuotype=4&score=26&content=wav6%2F202307%2Fbookworm%2F20230705%2F16885537751062200.mp3
     */
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_VOA,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @FormUrlEncoded
    @POST(UrlLibrary.EVAL_PUBLISH)
    Observable<Publish_eval> publishMargeAudio(@Field(StrLibrary.topic) String topic,
                                               @Field(StrLibrary.platform) String platform,
                                               @Field(StrLibrary.protocol) int protocol,
                                               @Field(StrLibrary.format) String format,
                                               @Field(StrLibrary.userid) int userId,
                                               @Field(StrLibrary.voaid) String voaId,
                                               @Field(StrLibrary.shuoshuotype) int shuoshuoType,
                                               @Field(StrLibrary.score) int score,
                                               @Field(StrLibrary.content) String content,
                                               @Field(StrLibrary.appid) int appId,
                                               @Field(StrLibrary.rewardVersion) int version);

    /***********************************************学习报告*******************************/
    /**
     * 提交课程学习报告-小说
     * http://daxue.iyuba.cn/ecollege/updateStudyRecordNew.jsp?format=json&Lesson=%E4%B9%A6%E8%99%AB&platform=android&appId=285&BeginTime=2023-07-06%2017%3A06%3A50&EndTime=2023-07-06%2017%3A07%3A08&EndFlg=0&LessonId=10201&TestNumber=17709&TestWords=0&TestMode=1&UserAnswer=&Score=0&DeviceId=8832ae35308e694c&uid=12071118&sign=efab632e149cd99df7797423ad483ccd
     */
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_DAXUE,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(UrlLibrary.update_listen_study_report)
    Observable<Study_report> updateReadReport(@Query(StrLibrary.format) String format,
                                              @Query(StrLibrary.Lesson) String lesson,
                                              @Query(StrLibrary.platform) String platform,
                                              @Query(StrLibrary.appId) int appId,
                                              @Query(StrLibrary.BeginTime) String beginTime,
                                              @Query(StrLibrary.EndTime) String endTime,
                                              @Query(StrLibrary.EndFlg) int endFlag,
                                              @Query(StrLibrary.LessonId) String lessonId,
                                              @Query(StrLibrary.TestNumber) int testNumber,
                                              @Query(StrLibrary.TestWords) int testWords,
                                              @Query(StrLibrary.TestMode) int testMode,
                                              @Query(StrLibrary.UserAnswer) String userAnswer,
                                              @Query(StrLibrary.Score) int Score,
                                              @Query(StrLibrary.DeviceId) String deviceId,
                                              @Query(StrLibrary.uid) int uid,
                                              @Query(StrLibrary.sign) String sign,
                                              @Query(StrLibrary.rewardVersion) int rewardVersion);

    /****************************************收藏*************************************/
    /**
     * 接口-收藏/取消收藏文章
     * http://apps.iyuba.cn/iyuba/updateCollect.jsp?groupName=Iyuba&sentenceFlg=0&appId=285&voaId=10901&userId=12071118&type=insert&topic=bookworm
     */
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_APPS,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(UrlLibrary.COLLECT_ARTICLE)
    Observable<Collect_chapter> collectArticle(@Query(StrLibrary.groupName) String groupName,
                                               @Query(StrLibrary.sentenceFlg) String sentenceFlg,
                                               @Query(StrLibrary.appId) int appId,
                                               @Query(StrLibrary.userId) String userId,
                                               @Query(StrLibrary.topic) String topic,
                                               @Query(StrLibrary.voaId) String voaId,
                                               @Query(StrLibrary.type) String type);

    /**
     * 接口-获取收藏的文章
     * http://cms.iyuba.cn/dataapi/jsp/getCollect.jsp?userId=12071118&sign=6d9455fe9e341be707cc01902cb2c9ce&topic=bookworm&appid=285&format=json&sentenceFlg=0
     */
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_CMS,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(UrlLibrary.COURSE_COLLECT)
    Observable<BaseBean_data<List<Novel_chapter_collect>>> getArticleCollect(@Query(StrLibrary.userId) int userId,
                                                                             @Query(StrLibrary.sign) String sign,
                                                                             @Query(StrLibrary.topic) String topic,
                                                                             @Query(StrLibrary.appid) int appId,
                                                                             @Query(StrLibrary.format) String format,
                                                                             @Query(StrLibrary.sentenceFlg) int flag);
}
