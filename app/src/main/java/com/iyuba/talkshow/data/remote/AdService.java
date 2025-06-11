package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.result.GetAdResponse;
import com.iyuba.talkshow.data.model.result.GetAdResponse1;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/12/26/026.
 */

public interface AdService {
    String ENDPOINT = "http://dev."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"";

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_DEV})
    @GET("getAdEntryAll.jsp?")
    Observable<GetAdResponse> getAd(@Query("appId") int appId, @Query("flag") int flag);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_DEV})
    @GET("getAdEntryAll.jsp?")
    Observable<List<GetAdResponse1>> getAd1(@Query("appId") int appId, @Query("flag") int flag);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_DEV})
    @GET("getAdEntryAll.jsp")
    Observable<List<GetAdResponse1>> getAdByUid(@Query("uid") int uid, @Query("appId") int appid,
                                                @Query("flag") int flag);


    interface GetAd {
        interface Param {
            interface Value {
                int FLAG = 2;
            }
        }

        interface Result {
            interface Code {
                String SUCCESS = "1";
            }
        }
    }

    class GetAd1 {
        public interface Param {
            interface Value {
                int FLAG = 1;
            }
        }

        public static String getAdFilename(String url) {
            return url.substring(8, url.length() - 4);
        }
    }

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static AdService newAdService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AdService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(AdService.class);
        }
    }
}
