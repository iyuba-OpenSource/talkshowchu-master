package com.iyuba.talkshow.newdata;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.iyuba.talkshow.Constant;

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
public interface AudioComposeApi {

    String BASEURL = "http://iuserspeech." + Constant.Web.WEB_SUFFIX.replace("/","") + ":9001/test/merge/";


    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_AI})
    @FormUrlEncoded
    @POST
    Call<EvaMixBean> audioComposeApi(
            @Url String url,
            @Field(value = "audios", encoded = true) String audios,
            @Field("type") String type);

}
