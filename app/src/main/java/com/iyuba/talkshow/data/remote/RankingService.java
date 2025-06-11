package com.iyuba.talkshow.data.remote;


import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.RankListenBean;
import com.iyuba.talkshow.data.model.RankTestBean;
import com.iyuba.talkshow.data.model.StudyRecordResponse;
import com.iyuba.talkshow.data.model.ExamWordResponse;
import com.iyuba.talkshow.data.model.RankOralBean;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;


public interface RankingService {
    /**
     * http://daxue."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"ecollege/getTopicRanking.jsp?shuoshuotype=4&topic=voa&topicid=0
     * &uid=5219278&type=D&start=0&total=15&sign=85713c696f57275cef2e93bd03a1ec38
     * 功能：指定周期内的用户做题排行榜（目前以做题总数排序）
     * 参数：
     * uid：用户id 可以为0
     * topic：voa,bbc,concept…;
     * type: "D" 日榜,"W" 周榜,"M" 月榜;
     * shuoshuotype: 默认值："0" 全部  "2" 句子排行 "3" 口语秀排行 "4": 合成播音排行;
     * topicid：文章ID;
     * start：开始的排名 从0开台
     * total：取得的件数
     * sign: md5("uid+topic+topicid+start+total+YYYY-MM-DD")
     * <p>
     * 返回参数：
     * result: 取不到数据为0， 取到数据是数据的件数
     * message: 返回的错误信息。
     * myid: 我的uid
     * myname:我的用户名
     * imgSrc:用户头像地址
     * myscores:我的总得分
     * mycount:我的句子数
     * myranking: 我的排名
     * data: json的列表内容如下
     * sort：在这批排行榜数据中的排序
     * uid: 排行榜的用户id
     * name:用户名
     * imgSrc:用户头像地址
     * counts:句子总数
     * scores:总分数
     * ranking: 排名
     */
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_DAXUE})
    @GET("ecollege/getTopicRanking.jsp?shuoshuotype=3&topic=voa&topicid=0")
    Observable<RankOralBean> getRankinglList(
            @Query("uid") int uid,
//            @Query("topic") String topic,
            @Query("type") String type,
//            @Query("shuoshuotype") String type,
//            @Query("topicid") String appName,
            @Query("start") int start,
            @Query("total") int total,
            @Query("sign") String sign
            );

    //口语--http://daxue.iyuba.cn/ecollege/getTopicRanking.jsp?type=D&uid=12230749&topic=ted&topicid=0&start=0&total=3&sign=b0bd17be12816c10789dd9df75574a23&shuoshuotype=4
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_DAXUE})
    @GET("ecollege/getTopicRanking.jsp?")
    Observable<RankOralBean> getEvalRankList(
            @Query("uid") int uid,
            @Query("topic") String topic,
            @Query("topicid") int topicid,
            @Query("type") String type,
            @Query("start") int start,
            @Query("total") int total,
            @Query("sign") String sign
    );

    //听力--http://daxue.iyuba.cn/ecollege/getStudyRanking.jsp?uid=12230749&type=D&start=0&total=3&sign=b6c231b9209a01ac6019f7721583e91f&mode=listening
    //学习--http://daxue.iyuba.cn/ecollege/getStudyRanking.jsp?uid=12230749&type=D&start=0&total=3&sign=b6c231b9209a01ac6019f7721583e91f&mode=all
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_DAXUE})
    @GET("ecollege/getStudyRanking.jsp")
    Observable<RankListenBean> getSumListen(@Query("uid") int uid,
                                            @Query("mode") String mode,
                                            @Query("type") String type,
                                            @Query("start") int start,
                                            @Query("total") int total,
                                            @Query("sign") String sign);

    //测试--http://daxue.iyuba.cn/ecollege/getTestRanking.jsp?uid=12230749&type=D&start=0&total=3&sign=b6c231b9209a01ac6019f7721583e91f
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_DAXUE})
    @GET("ecollege/getTestRanking.jsp")
    Observable<RankTestBean> getRankTest(@Query("uid") int uid,
                                         @Query("type") String type,
                                         @Query("start") int start,
                                         @Query("total") int total,
                                         @Query("sign") String sign);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_DAXUE})
    @GET("ecollege/getExamDetailNew.jsp")
    Observable<ExamWordResponse> getExamWordDetail(@Query("uid") int uid,
                                                   @Query("appId") String appId,
                                                   @Query("lesson") String lesson,
                                                   @Query("TestMode") String TestMode,
                                                   @Query("mode") int mode,
                                                   @Query("sign") String sign);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_DAXUE})
    @GET("ecollege/getStudyRecordByTestMode.jsp")
    Observable<StudyRecordResponse> getStudyTestMode(@Query("uid") int uid,
                                                     @Query("Lesson") String lesson,
                                                     @Query("Pageth") String page,
                                                     @Query("NumPerPage") String numPerPage,
                                                     @Query("TestMode") String TestMode,
                                                     @Query("sign") String sign);
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_DAXUE})
    @GET("ecollege/getMicroStudyRecord.jsp")
    Observable<StudyRecordResponse> getMicroStudyRecord(@Query("uid") int uid,
                                                     @Query("Lesson") String lesson,
                                                     @Query("Pageth") String page,
                                                     @Query("NumPerPage") String numPerPage,
                                                     @Query("sign") String sign);

    class Creator {
        public static RankingService newRankingService() {
            String baseUrl = "http://daxue."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(RankingService.class);
        }
    }

}
