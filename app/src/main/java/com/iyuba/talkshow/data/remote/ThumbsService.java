package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.RankWorkResponse;
import com.iyuba.talkshow.data.model.result.GetMyDubbingResponse;
import com.iyuba.talkshow.data.model.result.GetRankingResponse;
import com.iyuba.talkshow.data.model.result.ThumbsResponse;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public interface ThumbsService {
    String ENDPOINT = "http://voa."+Constant.Web.WEB_SUFFIX.replace("/","")+"/voa/";

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VOA})
    @GET("getWorksByUserId.jsp")
    Observable<RankWorkResponse> getWorkRanking(@QueryMap Map<String, String> options);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VOA})
    @GET("UnicomApi")
    Observable<GetRankingResponse> getThumbRanking(@QueryMap Map<String, String> options);

    //http://voa."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"voa/UnicomApi?id=850811&protocol=61001
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VOA})
    @GET("UnicomApi")
    Observable<ThumbsResponse> doThumbs(@Query("protocol") int protocol, @Query("uid") int uid, @Query("id") int id);
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VOA})
    @GET("UnicomApi")
    Observable<ThumbsResponse> deleteThumbs(@Query("protocol") int protocol, @Query("uid") int uid, @Query("id") int id);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VOA})
    @GET("getTalkShowOtherWorks.jsp")
    Observable<GetMyDubbingResponse> getMyDubbing(@Query("uid") int uid, @Query("appid") int appid, @Query("appname") String appname);

    interface GetRanking {
        interface Param {
            interface Key {
                String PLATFORM = "platform";
                String FORMAT = "format";
                String PROTOCOL = "protocol";
                String VOA_ID = "voaid";
                String PAGE_NUM = "pageNumber";
                String PAGE_COUNT ="pageCounts";
                String SORT = "sort";
                String TOPIC = "topic";
                String SELECT_TYPE = "selectType";
            }

            interface Value {
                String PLATFORM = "android";
                String FORMAT = "json";
                String TOPIC = Constant.EVAL_TYPE;
                int PROTOCOL = 60001;
                int SORT_BY_DATE = 0;
                int SORT_BY_SCORE = 1;//
                int SORT_BY_THUMBS = 2;
                int SORT_BY_COMPREHENSIVE = 3;
                String SELECT_TYPE = "3";
            }
        }

        interface Result {
            int SUCCESS = 511;
        }
    }

    interface DoThumbs {
        interface Param {
            interface Value {
                int AGREE_PROTOCOL = 61001;
                int AGAINST_PROTOCOL = 61002;
                int DELETE_PROTOCOL = 61003;
            }
        }

        interface Result {
            String THUMBS_SUCCESS = "OK";
        }
    }

    interface GetMyDubbing {
        interface Result {
            boolean SUCCESS = true;
        }
    }

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static ThumbsService newThumbsService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ThumbsService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(ThumbsService.class);
        }
    }
}
