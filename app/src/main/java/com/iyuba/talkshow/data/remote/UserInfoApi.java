package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.UserData;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by iyuba on 2018/1/8.
 */

public interface UserInfoApi {
    String URL = "http://api.iyuba.com.cn";
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @GET("/v2/api.iyuba")
    Call<UserData> userInfoApi(
            @Query("platform") String platform,
            @Query("format") String format,
            @Query("protocol") String protocol,
            @Query("appid") String appid,
            @Query("id") String uid,
            @Query("myid") String myuid,
            @Query("sign") String sign
    );
    interface Params{
        String PLATFORM = "android";
        String FORMAT = "json";
        String PROTOCOL = "20001";
        String APPID = "249";
        // String sign = MD5.getMD5ofStr("20001" + AccountManager.Instace(mContext).userId + "iyubaV2");
    }
    class Creator {
        public static UserInfoApi newUserInfoApi() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return retrofit.create(UserInfoApi.class);
        }
    }
}
