package com.iyuba.talkshow.data.remote;


import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.UploadRecordResult;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * updateStudyRecordNew.jsp参数
 * format：	要求返回的格式，json或xml、默认为json
 * uid:（非空） 爱语吧用户id，没有登录用户：0
 * DeviceId(非空)  应用的设备ID或网卡ID
 * appId: 应用的ID， 在http://bigdata.iyuba.com 中存在
 * BeginTime:（非空） 开始时间 格式：2014-02-19 15:33:00
 * EndTime:	结束时间 格式：2014-02-19 15:33:00
 * Lesson:（非空,encode一次）课程名称：voa,BBC,听歌学英语，英语四级，英语六级
 * LessonId:（非空） 课程id 默认值为0（songid,voaid,bbcid,四级题的年月id等）
 * TestNumber:（可空）	测试题号 默认值为0，
 * TestWords:（可空）	测试单词数 默认值为0，一般是本次学习内容中的单词总数
 * TestMode:（可空）	测试模试 1：听力 2：口语 3：阅读 4：写作 0：未确定（VOA，BBC，听歌学学习整篇内容可以设置为0）
 * UserAnswer:（可空）		用户的答案，一般是指本次听力中答题内容。（A，B，C，D）或者听说练习中听不懂的单词或说不准的单词，多个单词用逗号分开
 * Score:（可空）	 默认值为0（对应的做题的成绩，）
 * EndFlg:	完成标志：0：只开始听，点暂停提交；1：听力完成；2：做题完成,
 * Device:	做题设备：android手机，iphone手机，firefox,ie等
 * platform:	ios,android,air等
 * sign: Md5(uid+BeginTime+"YYYY-MM-DD"); YYYY-MM-DD是系统日期
 */
public interface UploadStudyRecordService {

    /*
         HeadlinesConstantManager.FORMAT_JSON,
                            HeadlinesConstantManager.APP_ID,
                            HeadlinesConstantManager.APP_NAME,
                            studyRecord.getLesson(),
                            studyRecord.getVoaid(),
                            userid,
                            getDeviceInfo.getLocalDeviceType(),
                            getDeviceInfo.getLocalMACAddress(),
                            studyRecord.getStarttime(),
                            studyRecord.getEndtime(),
                            studyRecord.getFlag(),
                            wordCount,
                            testMode,
                            HeadlinesConstantManager.PLATFORM,
                            HeadlineMD5.getMD5ofStr(userid+studyRecord.getStarttime()+ssdf.format(new Date()))
     */

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_DAXUE})
    @FormUrlEncoded
    @POST("ecollege/updateStudyRecordNew.jsp")
    Observable<UploadRecordResult> uploadStudyRecord(
            @Field("format") String format,
            @Field("appId") String appId,
            @Field("appName") String appName,
            @Field(encoded = true, value = "Lesson") String Lesson,
            @Field("LessonId") String LessonId,
            @Field("uid") String uid,
            @Field(encoded = true, value = "Device") String Device,
            @Field("DeviceId") String DeviceId,
            @Field(encoded = true, value = "BeginTime") String BeginTime,
            @Field(encoded = true, value = "EndTime") String EndTime,
            @Field("EndFlg") String EndFlg,
            @Field("TestWords") int TestWords,
            @Field("TestMode") String TestMode,
            @Field("platform") String platform,
            @Field("TestNumber") String testNumber,
            @Field("sign") String sign,
            @Field(StrLibrary.rewardVersion) int version
    );

    class Creator {
        public static UploadStudyRecordService newUploadStudyRecordService() {
            String baseUrl = "http://daxue."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(UploadStudyRecordService.class);
        }
    }
}
