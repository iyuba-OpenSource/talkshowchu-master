package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.result.GetLoopResponse;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/11/16 0016.
 */

public interface LoopService {
    String ENDPOINT = "http://dev."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"";
    //http://dev."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"getScrollPicApi.jsp

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_DEV})
    @GET("getScrollPicApi.jsp")
    Observable<GetLoopResponse> getLoopInfo(@Query("type") String type);

    interface GetLoopInfo {
        interface Param {
            interface Value {
                String LOOP_TYPE = "class.childtalkshow";
                String CHILD_LOOP_TYPE = "class.childtalkshow";
            }
        }
    }

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static LoopService newLoopService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(LoopService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(LoopService.class);
        }
    }
}
