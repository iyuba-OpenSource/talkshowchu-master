package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.EnterGroup;
import com.iyuba.talkshow.data.model.result.SendCommentResponse;
import com.iyuba.talkshow.data.model.result.SendEvaluateResponse;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author yq QQ:1032006226
 * @name talkshow
 * @class name：com.iyuba.talkshow.data.remote
 * @class describe
 * @time 2018/12/19 19:51
 * @change
 * @chang time
 * @class describe
 */
public interface EvalServiece {

//    String ENDPOINT = "http://ai.iyuba.cn/";
    String ENDPOINT = "http://iuserspeech."+Constant.Web.WEB_SUFFIX.replace("/","")+":9001/";

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_AI})
    @Multipart
    @POST("test/eval/")//@QueryMap Map<String, String> options,
    Observable<SendEvaluateResponse> sendVoiceComment(@PartMap Map<String, RequestBody> optionMap,
                                                      @Part MultipartBody.Part file);
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_AI})
    @Multipart
    @POST("test/ai/")
    Observable<SendEvaluateResponse> sendVoiceEval(@PartMap Map<String, RequestBody> optionMap,
                                                      @Part MultipartBody.Part file);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_AI})
    @GET("nlp/entergroup/")
    Observable<EnterGroup> enterGroup(@Query("uid") int uid, @Query("apptype") String type);

    interface GetVoa {
        interface Param {
            interface Key {
                String TYPE = "type";
                String SENTENCE = "sentence";
                String USERID = "userId";
                String NEWSID = "newsId";
                String PARAID = "paraId";
                String IDINDEX = "IdIndex";
            }

            interface Value {
                String TYPE = "android";
                String FORMAT = "json";

                int PAGE_NUM = 1;
                //                int PAGE_SIZE = 10000;
                int PAGE_SIZE = 600; //减少请求个数
                int RECENT_VOA_ID = 0;
            }
        }
    }

    class Creator {

        public static EvalServiece newCommentService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(EvalServiece.ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(HttpUtil.getOkHttpClient())
                    .build();
            return retrofit.create(EvalServiece.class);
        }
    }

}
