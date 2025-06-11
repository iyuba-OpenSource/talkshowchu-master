package com.iyuba.talkshow.lil.help_fix.model.remote.newService;

import com.iyuba.talkshow.data.model.AppCheckResponse;
import com.iyuba.talkshow.data.model.result.GetAdResponse1;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.UrlLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.NetHostManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_data;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_click_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_reward_vip;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Ad_stream_result;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Collect_chapter;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_rank;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_rank_agree;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_rank_detail;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Integral_bean;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Integral_deduct;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Junior_chapter_collect;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Report_read;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Reward_history;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Word_detail;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Word_insert;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @title: 服务-通用
 * @date: 2023/7/4 16:53
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface CommonService {

    /**************************审核********************************/
    //审核接口处理(微课、视频、人教版等)
    //http://api.qomolama.cn/getRegisterAll.jsp
    @Headers({StrLibrary.urlPrefix+":"+ UrlLibrary.HTTP_API,StrLibrary.urlHost+":"+UrlLibrary.QOMLAMA_URL})
    @GET(UrlLibrary.verify_ability)
    Observable<AppCheckResponse> verify(@Query(StrLibrary.appId) int appId,
                                        @Query(StrLibrary.appVersion) String version);

    /**************************单词查询**************************/
    //查询单词
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_WORD,StrLibrary.urlHost+":"+ NetHostManager.domain_short})
    @GET(UrlLibrary.word_search)
    Observable<Word_detail> searchWord(@Query(StrLibrary.q) String word);

    //插入/删除单词
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_WORD,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(UrlLibrary.word_insert)
    Observable<Word_insert> insertWord(@Query(StrLibrary.userId) int userId,
                                       @Query(StrLibrary.mod) String mode,
                                       @Query(StrLibrary.groupName) String groupName,
                                       @Query(StrLibrary.word) String wordsStr);

    /****************************积分*****************************/
    //分享操作获取积分
    //http://api.iyuba.cn/credits/updateScore.jsp?srid=7&mobile=1&flag=123456789020230608184950&uid=12071118&appid=260&idindex=313027
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_API,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(UrlLibrary.integral_share)
    Observable<Integral_bean> getIntegralAfterShare(@Query(StrLibrary.srid) String srid,
                                                    @Query(StrLibrary.mobile) int mobile,
                                                    @Query(StrLibrary.flag) String flag,
                                                    @Query(StrLibrary.uid) int uid,
                                                    @Query(StrLibrary.appid) int appId,
                                                    @Query(StrLibrary.idindex) String voaId);

    //下载pdf扣除积分
    //http://api.iyuba.cn/credits/updateScore.jsp?srid=40&mobile=1&flag=MjAyMzA3MDQxNTUyMjE%3D%0A&uid=12071118&appid=222&idindex=1002
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_API,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(UrlLibrary.update_score)
    Observable<Integral_deduct> deductIntegral(@Query(StrLibrary.srid) int srid,
                                               @Query(StrLibrary.mobile) int mobile,
                                               @Query(StrLibrary.flag) String flag,
                                               @Query(StrLibrary.uid) String uid,
                                               @Query(StrLibrary.appid) int appId,
                                               @Query(StrLibrary.idindex) String idIndex);

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

    /********************************************评测的排行榜******************************/
    //获取评测的排行榜数据
    //http://daxue.iyuba.cn/ecollege/getTopicRanking.jsp?topic=concept&topicid=1001&uid=13865961&start=0&total=20&sign=9fa3a22a71ab907ab28cea127c06212e&type=D
    //start：起始数据-0，total：数据量-20，type：D-天，W-周，M-月
    @Headers({StrLibrary.urlPrefix+":"+ UrlLibrary.HTTP_DAXUE,StrLibrary.urlHost+":"+ NetHostManager.domain_short})
    @GET(UrlLibrary.COURSE_RANKING)
    Observable<Eval_rank> getEvalRankData(@Query(StrLibrary.topic) String topic,
                                          @Query(StrLibrary.topicid) String voaId,
                                          @Query(StrLibrary.uid) long uid,
                                          @Query(StrLibrary.start) int start,
                                          @Query(StrLibrary.total) int total,
                                          @Query(StrLibrary.sign) String sign,
                                          @Query(StrLibrary.type) String type);

    //获取评测的排行榜详情数据
    //http://voa.iyuba.cn/voa/getWorksByUserId.jsp?uid=13902175&topic=concept&shuoshuoType=2,4&sign=4ca1889ab3671ed91ab07f07d7970506&topicId=2001
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_VOA, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET(UrlLibrary.COURSE_RANK_DETAIL)
    Observable<BaseBean_data<List<Eval_rank_detail>>> getEvalRankDetailData(@Query(StrLibrary.uid) String showUserId,
                                                                            @Query(StrLibrary.topic) String topic,
                                                                            @Query(StrLibrary.shuoshuotype) String shuoshuoType,
                                                                            @Query(StrLibrary.sign) String sign,
                                                                            @Query(StrLibrary.topicId) String voaId);

    //点赞评测排行详情中的数据
    //http://voa.iyuba.cn/voa/UnicomApi?protocol=61001&uid=13865961&id=19495192
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_VOA,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(UrlLibrary.EVAL_RANK_AGREE)
    Observable<Eval_rank_agree> agreeEvalRankDetail(@Query(StrLibrary.protocol) int protocol,
                                                    @Query(StrLibrary.uid) String uid,
                                                    @Query(StrLibrary.id) String evalSentenceId);

    /***************************************学习报告****************************/
    //提交阅读的学习报告
    //http://daxue.iyuba.cn/ecollege/updateNewsStudyRecord.jsp?format=xml&uid=14524771&BeginTime=2023-08-04+16%3A56%3A18&EndTime=2023-08-04+16%3A57%3A23&appName=headline&Lesson=%25E8%258B%25B1%25E8%25AF%25AD%25E5%25A4%25B4%25E6%259D%25A1&LessonId=25218&appId=240&Device=HONORPCT-AL10HWPCT&DeviceId=02:00:00:00:00:00&EndFlg=1&wordcount=200&categoryid=127&platform=android
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_DAXUE,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @POST(UrlLibrary.REPORT_READ)
    Observable<Report_read> submitReadReport(@Query(StrLibrary.format) String format,
                                             @Query(StrLibrary.uid) int uid,
                                             @Query(StrLibrary.BeginTime) String beginTime,
                                             @Query(StrLibrary.EndTime) String endTime,
                                             @Query(StrLibrary.appName) String appName,
                                             @Query(StrLibrary.Lesson) String lesson,
                                             @Query(StrLibrary.LessonId) String lessonId,
                                             @Query(StrLibrary.appId) int appid,
                                             @Query(StrLibrary.Device) String device,
                                             @Query(StrLibrary.DeviceId) String deviceId,
                                             @Query(StrLibrary.EndFlg) int endFlag,
                                             @Query(StrLibrary.wordcount) long wordCount,
                                             @Query(StrLibrary.categoryid) String categoryid,
                                             @Query(StrLibrary.platform) String platform,
                                             @Query(StrLibrary.rewardVersion) int rewardVersion);

    /******************************************现金奖励*********************************/
    //获取现金奖励的历史记录
    //http://api.iyuba.cn/credits/getuseractionrecord.jsp?uid=6307010&pages=1&pageCount=20&sign=0fd32b5d167482f0cc3561b2abc70738
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_API,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(UrlLibrary.REWARD_HISTORY)
    Observable<BaseBean_data<List<Reward_history>>> getRewardHistory(@Query(StrLibrary.uid) int uid,
                                                                     @Query(StrLibrary.pages) int pages,
                                                                     @Query(StrLibrary.pageCount) int pageCount,
                                                                     @Query(StrLibrary.sign) String sign);
}
