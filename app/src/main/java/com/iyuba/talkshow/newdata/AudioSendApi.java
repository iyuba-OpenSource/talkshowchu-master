package com.iyuba.talkshow.newdata;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by carl shen on 2020/7/28
 * New Primary English, new study experience.
 */
public interface AudioSendApi {

    String BASEURL = "http://voa." + Constant.Web.WEB_SUFFIX + "voa/";
    String platform = "android";
    String format = "json";
    String protocol = "60003";

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VOA})
    @FormUrlEncoded
    @POST
    Call<EvaSendBean> audioSendApi(
            @Url String url,
            @Field("topic") String topic,
            @Field("topicid") String topicid,
            @Field("platform") String platform,
            @Field("format") String format,
            @Field("protocol") String protocol,
            @Field("userid") String userid,
            @Field("username") String username,
            @Field("voaid") String voaid,
            @Field("score") String score,
            @Field("shuoshuotype") String shuoshuotype,
            @Field("content") String content,
            @Field(StrLibrary.appid) int appId,
            @Field(StrLibrary.rewardVersion) int version);

    @FormUrlEncoded
    @POST
    Call<EvaSendBean> evalSendApi(
            @Url String url,
            @Field("topic") String topic,
            @Field("topicid") String topicid,
            @Field("paraid") int paraid,
            @Field("idIndex") int idIndex,
            @Field("platform") String platform,
            @Field("format") String format,
            @Field("protocol") String protocol,
            @Field("userid") String userid,
            @Field("username") String username,
            @Field("voaid") String voaid,
            @Field("score") String score,
            @Field("shuoshuotype") String shuoshuotype,
            @Field("content") String content);
}
