package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.result.GetCommentResponse;
import com.iyuba.talkshow.data.model.result.SendCommentResponse;
import com.iyuba.talkshow.data.model.result.SendDubbingResponse;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface CommentService {
    String ENDPOINT = "http://voa."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"voa/";


    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VOA})
    @GET("UnicomApi?")
    Observable<GetCommentResponse> getComments(@QueryMap Map<String, String> options);


    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VOA})
    @Multipart
    @POST("UnicomApi?")
    Observable<GetCommentResponse> sendTextComment(@PartMap Map<String, RequestBody> options);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VOA})
    @Multipart
    @POST("UnicomApi?")//@QueryMap Map<String, String> options,
    Observable<SendCommentResponse> sendVoiceComment(@PartMap Map<String, RequestBody> optionMap,
                                                    @Part MultipartBody.Part file);
//                                             @Part("file\"; filename=\"voice.amr") RequestBody body);

//    @Multipart
@Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VOA})
    @POST("UnicomApi2?")//@QueryMap Map<String, String> options,
    Observable<SendDubbingResponse> sendDubbingComment(@QueryMap  Map<String, String> params  ,
                                                       @Body RequestBody body);

    class SendComment {
        public interface Param {
            interface Key {
                String PLATFORM = "platform";
                String FORMAT = "format";
                String PROTOCOL = "protocol";
                String USER_ID = "userid";
                String VOA_ID = "voaid";
                String BACK_ID = "backId";
                String USER_NAME = "username";
                String SHUOSHUO_TYPE = "shuoshuotype";
                String CONTENT = "content";
                String TOPIC = "topic";
                String SHOW = "show";
                String FLAG = "flag";
                String SOUND = "sound";
                String WAVLIST = "wavList";
                String SCORE = "score";
                String BEGINTIME   = "beginTime";
                String DURATION  = "duration ";
                String ENDTIME  = "endTime";
                String INDEX  = "index";
                String URL  = "URL";
                String APPNAME  = "appName";
            }
            

            interface Value {
                String PLATFORM = "android";
                String FORMAT = "json";
                String TOPIC = Constant.EVAL_TYPE;
                int PROTOCOL = 60002;
                int SHUOSHUO_TYPE_VOICE = 1;
                int SHUOSHUO_TYPE_WORDS = 0;
                int SHUOSHUO_TYPE_RELEASE = 3;
                int ERROR = Integer.MIN_VALUE;
                int SORT_BY_DATE = 0;
                int SORT_BY_SCORE = 1;//
                int SORT_BY_THUMBS = 2;
                int SORT_BY_COMPREHENSIVE = 3;
                String SHOW = "yes";
                int FLAG = 1;
            }
        }

        public interface Result {
            String COMMENT_SUCCESS = "OK";
        }

        public static class Part {
            private static final String FILENAME_PREFIX = "file\"; filename=\"";

            public static String getFilePartKey() {
                return FILENAME_PREFIX + System.currentTimeMillis() + ".amr";
            }
        }
    }

    interface GetComment {
        interface Param {
            interface Key {
                String PLATFORM = "platform";
                String FORMAT = "format";
                String PROTOCOL = "protocol";
                String VOA_ID = "voaid";
                String PAGE_NUM = "pageNumber";
                String PAGE_COUNT = "pageCounts";
                String BACK_ID = "backId";
                String SORT = "sort";
                String TOPIC = "topic";
            }

            interface Value {
                String PLATFORM = "android";
                String FORMAT = "json";
                String TOPIC = Constant.EVAL_TYPE;
                int PROTOCOL = 60001;
                int AGAINST_PROTOCOL = 60002;
                int SORT_BY_DATE = 0;
                int SORT_BY_SCORE = 1;//
                int SORT_BY_THUMBS = 2;
                int SORT_BY_COMPREHENSIVE = 3;
            }
        }

        interface Result {
            int VIP_STATUS = 1;
        }
    }

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static CommentService newCommentService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CommentService.ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(HttpUtil.getOkHttpClient())
                    .build();
            return retrofit.create(CommentService.class);
        }
    }
}
