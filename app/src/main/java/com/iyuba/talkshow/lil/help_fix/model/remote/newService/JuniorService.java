package com.iyuba.talkshow.lil.help_fix.model.remote.newService;

import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.UrlLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.NetHostManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_data;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_data_junior;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_data_primary;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_dubbing_rank;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_voatext;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Collect_chapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Dubbing_rank;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_rank_agree;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Junior_book;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Junior_chapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Junior_chapter_collect;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Junior_chapter_detail;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Junior_eval;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Junior_type;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Junior_word;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Marge_eval;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Pdf_url;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Publish_eval;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Publish_preview;
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
 * @title: 服务-中小学
 * @date: 2023/7/4 16:53
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface JuniorService {

    /************************************************出版社***********************************************/
    //小学-出版社数据
    //http://apps.iyuba.cn/iyuba/chooseLessonNew.jsp?&appid=260&uid=0&type=primary&version=3
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_APPS, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET(UrlLibrary.junior_type)
    Observable<BaseBean_data<BaseBean_data_primary<List<Junior_type>>>> getPrimaryTypeData(@Query(StrLibrary.appid) int appId,
                                                                                           @Query(StrLibrary.uid) String uid,
                                                                                           @Query(StrLibrary.type) String type,
                                                                                           @Query(StrLibrary.version) int version);

    //初中-出版社数据
    //http://apps.iyuba.cn/iyuba/chooseLessonNew.jsp?&appid=259&uid=12071118&type=junior&version=3
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_APPS, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET(UrlLibrary.junior_type)
    Observable<BaseBean_data<BaseBean_data_junior<List<Junior_type>>>> getMiddleTypeData(@Query(StrLibrary.appid) int appId,
                                                                                         @Query(StrLibrary.uid) String uid,
                                                                                         @Query(StrLibrary.type) String type,
                                                                                         @Query(StrLibrary.version) int version);

    /*************************************************书籍**********************************************/
    //中小学-书籍数据
    //http://apps.iyuba.cn/iyuba/getTitleBySeries.jsp?type=category&category=316&uid=12071118&appid=259&sign=b1930851a7b7531e87d75828b6c0844a&format=json
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_APPS, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET(UrlLibrary.junior_book)
    Observable<BaseBean_data<List<Junior_book>>> getJuniorBookData(@Query(StrLibrary.type) String type,
                                                                   @Query(StrLibrary.category) String category,
                                                                   @Query(StrLibrary.uid) String uid,
                                                                   @Query(StrLibrary.appid) int appId,
                                                                   @Query(StrLibrary.sign) String sign,
                                                                   @Query(StrLibrary.format) String format);

    /**********************************************章节****************************************/
    //小学-章节数据
    //http://apps.iyuba.cn/iyuba/getTitleBySeries.jsp?type=title&seriesid=205&uid=0&appid=260&sign=b1930851a7b7531e87d75828b6c0844a&format=json&version=1
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_APPS, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET(UrlLibrary.junior_chapter)
    Observable<BaseBean_data<List<Junior_chapter>>> getPrimaryChapterData(@Query(StrLibrary.type) String type,
                                                                          @Query(StrLibrary.seriesid) String seriesId,
                                                                          @Query(StrLibrary.uid) String uid,
                                                                          @Query(StrLibrary.appid) int appId,
                                                                          @Query(StrLibrary.sign) String sign,
                                                                          @Query(StrLibrary.format) String format,
                                                                          @Query(StrLibrary.version) int version);

    //初中-章节数据
    //http://apps.iyuba.cn/iyuba/getTitleBySeries.jsp?type=title&seriesid=453&uid=12071118&appid=259&sign=b1930851a7b7531e87d75828b6c0844a&format=json
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_APPS, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET(UrlLibrary.junior_chapter)
    Observable<BaseBean_data<List<Junior_chapter>>> getMiddleChapterData(@Query(StrLibrary.type) String type,
                                                                         @Query(StrLibrary.seriesid) String seriesId,
                                                                         @Query(StrLibrary.uid) String uid,
                                                                         @Query(StrLibrary.appid) int appId,
                                                                         @Query(StrLibrary.sign) String sign,
                                                                         @Query(StrLibrary.format) String format);

    /**************************************************章节详情************************************/
    //中小学-章节详情数据
    //http://apps.iyuba.cn/iyuba/textExamApi.jsp?format=json&voaid=3371001
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_APPS, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET(UrlLibrary.junior_chapter_detail)
    Observable<BaseBean_voatext<List<Junior_chapter_detail>>> getJuniorChapterDetailData(@Query(StrLibrary.voaid) String voaId,
                                                                                         @Query(StrLibrary.format) String format);

    /****************************************************单词************************************/
    //中小学-单词数据
    //http://apps.iyuba.cn/iyuba/getWordByUnit.jsp?bookid=205
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_APPS, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET(UrlLibrary.junior_word)
    Observable<BaseBean_data<List<Junior_word>>> getJuniorWordData(@Query(StrLibrary.bookid) String bookId);

    /****************************************************评测*****************************************/
    /**
     * 中小学-课程单句评测
     * http://iuserspeech.iyuba.cn:9001/test/ai/
     * sentence			I%20have%20a%20book.
     * flg			0
     * paraId			3
     * newsId			313002
     * protocol			60003
     * IdIndex			1
     * wordId			0
     * appId			260
     * type			primaryenglish
     * userId			12071118
     * platform			android
     * file	application/octet-stream	/storage/emulated/0/Android/data/com.iyuba.talkshow.childenglish/files/313002/1687830155526/3.aac	8.09 KB (8,280 bytes)
     */
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_IUSERSPEECH, StrLibrary.urlHost + ":" + NetHostManager.domain_short, StrLibrary.urlSuffix + ":" + UrlLibrary.SUFFIX_9001})
    @POST(UrlLibrary.junior_word_eval)
    Observable<BaseBean_data<Eval_result>> submitLessonSingleEval(@Body RequestBody body);

    /**
     * 中小学-发布单个评测数据
     * http://voa.iyuba.cn/voa/UnicomApi
     * topic	primaryenglish
     * topicid	313027
     * paraid	4
     * idIndex	1
     * platform	android
     * format	json
     * protocol	60003
     * userid	12071118
     * username	aiyuba_lil
     * voaid	313027
     * score	74
     * shuoshuotype	2
     * content	wav8/202306/primaryenglish/20230607/16861261070600446.mp3
     */
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_VOA, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @FormUrlEncoded
    @POST(UrlLibrary.EVAL_PUBLISH)
    Observable<Publish_eval> publishSingleEval(@Field(StrLibrary.topic) String topic,
                                               @Field(StrLibrary.topicid) String topicid,
                                               @Field(StrLibrary.paraid) String paraId,
                                               @Field(StrLibrary.idIndex) String idIndex,
                                               @Field(StrLibrary.platform) String platform,
                                               @Field(StrLibrary.format) String format,
                                               @Field(StrLibrary.protocol) int protocol,
                                               @Field(StrLibrary.userid) long uId,
                                               @Field(StrLibrary.username) String userName,
                                               @Field(StrLibrary.voaid) String voaId,
                                               @Field(StrLibrary.score) int score,
                                               @Field(StrLibrary.shuoshuotype) int shuoshuotype,
                                               @Field(StrLibrary.content) String content);


    //中小学-合成配音的数据
    //http://iuserspeech.iyuba.cn:9001/test/merge/
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_IUSERSPEECH, StrLibrary.urlHost + ":" + NetHostManager.domain_short, StrLibrary.urlSuffix + ":" + UrlLibrary.SUFFIX_9001})
    @POST(UrlLibrary.EVAL_MARGE)
    Observable<Marge_eval> margeAudioEval(@Body RequestBody body);

    /**
     * 发布合成的评测到排行榜
     * http://voa.iyuba.cn/voa/UnicomApi
     * topic	concept
     * platform	android
     * format	json
     * protocol	60003
     * userid	13883503
     * username
     * voaid	3002
     * score	1
     * shuoshuotype	4
     * content	wav6/202303/concept/20230303/16778226389594980.mp3
     */
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_VOA, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @FormUrlEncoded
    @POST(UrlLibrary.EVAL_PUBLISH)
    Observable<Publish_eval> publishMargeAudio(@Field(StrLibrary.topic) String topic,
                                               @Field(StrLibrary.platform) String platform,
                                               @Field(StrLibrary.format) String format,
                                               @Field(StrLibrary.protocol) int protocol,
                                               @Field(StrLibrary.userid) long uid,
                                               @Field(StrLibrary.username) String userName,
                                               @Field(StrLibrary.voaid) String voaId,
                                               @Field(StrLibrary.score) int score,
                                               @Field(StrLibrary.shuoshuotype) int shuoshuoType,
                                               @Field(StrLibrary.content) String content);


    /**
     * 中小学-单词评测
     * http://iuserspeech.iyuba.cn:9001/test/ai/
     * sentence	text/plain; charset=utf-8		I have a book.
     * flg	text/plain; charset=utf-8		0
     * paraId	text/plain; charset=utf-8		205
     * newsId	text/plain; charset=utf-8		0
     * IdIndex	text/plain; charset=utf-8		1
     * wordId	text/plain; charset=utf-8		0
     * appId	text/plain; charset=utf-8		260
     * type	text/plain; charset=utf-8		primaryenglish
     * userId	text/plain; charset=utf-8		13865961
     * file	multipart/form-data	book.amr	2.60 KB (2,662 bytes)
     */
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_IUSERSPEECH, StrLibrary.urlHost + ":" + NetHostManager.domain_short, StrLibrary.urlSuffix + ":" + UrlLibrary.SUFFIX_9001})
    @POST(UrlLibrary.junior_word_eval)
    Observable<BaseBean_data<Junior_eval>> submitWordEval(@Body RequestBody body);

    /***************************************************pdf**************************************/
    //获取中小学的pdf下载链接--(双语-0，英文-1)
    //http://apps.iyuba.cn/iyuba/getVoapdfFile_new.jsp?type=juniorenglish&voaid=316023&isenglish=1
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_APPS, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET(UrlLibrary.junior_pdf_url)
    Observable<Pdf_url> getJuniorPdfDownloadUrl(@Query(StrLibrary.type) String type,
                                                @Query(StrLibrary.voaid) String voaId,
                                                @Query(StrLibrary.isenglish) String englishTag);


    /******************************************配音*******************************************/
    /**
     * 中小学-发布配音的预览内容
     *     http://voa.iyuba.cn/voa/UnicomApi2?&protocol=60002&userid=12071118&content=3
     *     {
     *     	"appName": "primaryEnglish",
     *     	"category": 313,
     *     	"flag": 1,
     *     	"format": "json",
     *     	"idIndex": 0,
     *     	"paraId": 0,
     *     	"platform": "android",
     *     	"score": 62,
     *     	"shuoshuotype": 3,
     *     	"sound": "/202002/313027.mp3",
     *     	"topic": "primaryenglish",
     *     	"username": "aiyuba_lil",
     *     	"voaid": 313027,
     *     	"wavList": [{
     *     		"URL": "wav8/202306/primaryenglish/20230606/16859811256556334.mp3",
     *     		"beginTime": 6.1,
     *     		"duration": 1.8,
     *     		"endTime": 8.0,
     *     		"index": 2
     *                }, {
     *     		"URL": "wav8/202306/primaryenglish/20230607/16861261070600446.mp3",
     *     		"beginTime": 9.9,
     *     		"duration": 2.6,
     *     		"endTime": 12.1,
     *     		"index": 4
     *        }]
     *     }
     */
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_VOA,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @POST(UrlLibrary.junior_dubbing_publish)
    Observable<Publish_preview> publishTalkPreview(@Query(StrLibrary.protocol) int protocol,
                                                   @Query(StrLibrary.userid) int userId,
                                                   @Query(StrLibrary.content) String content,
                                                   @Body RequestBody body);

    /********************************************配音排行******************************************/
    //中小学-口语秀排行信息
    //http://voa.iyuba.cn/voa/UnicomApi?voaid=313026&protocol=60001&pageNumber=1&pageCounts=20&format=json&topic=primaryenglish&selectType=3&sort=1&platform=android
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_VOA,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(UrlLibrary.talk_rank)
    Observable<BaseBean_dubbing_rank<List<Dubbing_rank>>> dubbingRank(@Query(StrLibrary.voaid) String voaId,
                                                                      @Query(StrLibrary.protocol) int protocol,
                                                                      @Query(StrLibrary.pageNumber) int index,
                                                                      @Query(StrLibrary.pageCounts) int count,
                                                                      @Query(StrLibrary.format) String format,
                                                                      @Query(StrLibrary.topic) String topic,
                                                                      @Query(StrLibrary.selectType) int selectType,
                                                                      @Query(StrLibrary.sort) int sort,
                                                                      @Query(StrLibrary.platform) String platform);

    //中小学-口语秀预览排行点赞
    //http://voa.iyuba.cn/voa/UnicomApi?protocol=61001&id=19495192
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_VOA,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(UrlLibrary.EVAL_RANK_AGREE)
    Observable<Eval_rank_agree> agreeDubbingRankDetail(@Query(StrLibrary.protocol) int protocol,
                                                       @Query(StrLibrary.id) String id);

    /*************************************************学习报告********************************/
    /**
     * 提交课程学习报告-中小学
     *     http://daxue.iyuba.cn/ecollege/updateStudyRecordNew.jsp
     *     format	json
     *     appId	260
     *     appName	小学英语
     *     Lesson	primaryEnglish
     *     LessonId	313002
     *     uid	12071118
     *     Device	PCT-AL10
     *     DeviceId	02:00:00:00:00:00
     *     BeginTime	2023-06-26 15:39:21
     *     EndTime	2023-06-26 15:39:26
     *     EndFlg	0
     *     TestWords	10
     *     TestMode	1
     *     platform	android
     *     TestNumber	1
     *     sign	a8fd9737213972e58e0f5bcab027bbd9
     */
    @Headers({StrLibrary.urlPrefix+":"+ UrlLibrary.HTTP_DAXUE,StrLibrary.urlHost+":"+ NetHostManager.domain_short})
    @FormUrlEncoded
    @POST(UrlLibrary.update_listen_study_report)
    Observable<Study_report> updateReadReport(@Field(StrLibrary.format) String format,
                                              @Field(StrLibrary.appId) int appid,
                                              @Field(StrLibrary.appName) String appNameCn,
                                              @Field(StrLibrary.Lesson) String lesson,
                                              @Field(StrLibrary.LessonId) String lessonId,
                                              @Field(StrLibrary.uid) int uid,
                                              @Field(StrLibrary.Device) String device,
                                              @Field(StrLibrary.DeviceId) String deviceId,
                                              @Field(StrLibrary.BeginTime) String beginTime,
                                              @Field(StrLibrary.EndTime) String endTime,
                                              @Field(StrLibrary.EndFlg) int endFlag,
                                              @Field(StrLibrary.TestWords) int testWords,
                                              @Field(StrLibrary.TestMode) int testMode,
                                              @Field(StrLibrary.platform) String platform,
                                              @Field(StrLibrary.TestNumber) int testNumber,
                                              @Field(StrLibrary.sign) String sign);

    /**
     * 提交学习报告-单词
     * http://daxue.iyuba.cn/ecollege/updateExamRecordNew.jsp
     */
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_DAXUE,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @POST(UrlLibrary.update_word_study_report)
    Observable<Study_report> updateWordReport(@Body RequestBody body);

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

