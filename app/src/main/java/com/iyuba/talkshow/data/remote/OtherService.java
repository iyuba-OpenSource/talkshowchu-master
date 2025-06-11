package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.result.AddReadCountResponse;
import com.iyuba.talkshow.data.model.AppCheckResponse;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2016/11/29 0029.
 */

public interface OtherService {
    String ENDPOINT = "http://voa."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"voa/";

    //http://voa."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"voa/UnicomApi?protocol=70001&counts=1&format=xml&voaids=6995
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VOA})
    @GET("UnicomApi?")
    Observable<AddReadCountResponse> addReadCount(@Query("protocol") int protocol,
                                                  @Query("counts") int counts,
                                                  @Query("format") String format,
                                                  @Query("voaids") int voaid);

    /**
     * http://voa."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"voa/UnicomApi?id=xxxx&protocol=61003
     */
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VOA})
    @GET("UnicomApi?protocol=61003")
    Observable<Object> deleteReleaseRecord(@Query("id") int id);

    //下载文件
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

    //控制当前app是否处于审核状态
    //网址：http://m.iyuba.cn/m_login/apple/apple_app_list.jsp
    //接口：http://api.qomolama.cn/getRegisterAll.jsp?appId=222&appVersion=2.5.0620
    @GET
    Observable<AppCheckResponse> getAppCheckStatus(@Url String url, @Query("appId") int appId, @Query("appVersion") String version);

    interface AddReadCount {
        interface Param {
            interface Value {
                int PROTOCOL = 70001;
                int COUNT = 1;
                String FORMAT = "json";
            }
        }
    }

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static OtherService newOtherService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(OtherService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(OtherService.class);
        }
    }
}
