package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.result.FeedbackResponse;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public interface FeedbackService {
    String ENDPOINT = "http://apis."+Constant.Web.WEB_SUFFIX.replace("/","")+"/v2/";

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APIS})
    @GET("api.iyuba?")
    Observable<FeedbackResponse> doFeedback(@QueryMap Map<String, String> options);

    interface DoFeedback {
        interface Param {
            interface Key {
                String EMAIL = "email";
                String CONTENT = "content";
                String UID = "uid";
                String FORMAT = "format";
            }

            interface Value {
                String FORMAT = "json";
            }
        }
    }

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static FeedbackService newFeedbackService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FeedbackService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(FeedbackService.class);
        }
    }
}
