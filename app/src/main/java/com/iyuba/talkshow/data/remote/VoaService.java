package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.LessonNewResponse;
import com.iyuba.talkshow.data.model.OfficialResponse;
import com.iyuba.talkshow.data.model.SeriesResponse;
import com.iyuba.talkshow.data.model.TextBookResponse;
import com.iyuba.talkshow.data.model.TitleSeriesResponse;
import com.iyuba.talkshow.data.model.UpdateWordResponse;
import com.iyuba.talkshow.data.model.VoaBookResponse;
import com.iyuba.talkshow.data.model.result.GetVoaResponse;
import com.iyuba.talkshow.data.model.result.VoaTextResponse;
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
 * Created by Administrator on 2016/11/11 0011.
 */

public interface VoaService {
    String ENDPOINT = "http://apps."+Constant.Web.WEB_SUFFIX.replace("/","")+"/iyuba/";

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("getTitleBySeries.jsp")
    Observable<TitleSeriesResponse> getTitleSeries(@Query("type") String type,
                                                   @Query("seriesid") String series,
                                                   @Query("uid") int uid,
                                                   @Query("appid") int appid,
                                                   @Query("sign") String sign,
                                                   @Query("format") String format,
                                                   @Query("version") int version);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("getTitleBySeries.jsp")
    Observable<SeriesResponse> getCategorySeries(@Query("type") String type,
                                                 @Query("category") String series,
                                                 @Query("uid") int uid,
                                                 @Query("appid") int appid,
                                                 @Query("sign") String sign,
                                                 @Query("format") String format);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("titlePeiYinApi.jsp")
    Observable<GetVoaResponse> getVoas(@QueryMap Map<String, String> options);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("titlePeiYinApi.jsp?type=ios&format=json&pages=1&pageNum=100&maxid=0")
    Observable<GetVoaResponse> getVoas();

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("titleOneApi.jsp")
    Observable<GetVoaResponse> getVoaById(@Query("type") String type, @Query("parentID") String parentID, @Query("voaId") int voaId);

    ////http://apps."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"iyuba/textExamApi.jsp?format=json&voaid=
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("textExamApi.jsp")
    Observable<VoaTextResponse> getVoaTexts(@Query("format") String format, @Query("voaid") int voaid);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("textExamApiBySeries.jsp")
    Observable<TextBookResponse> getVoaTexts4Book(@Query("category") String category, @Query("series") int series,
                                                  @Query("userid") int userid, @Query("appid") int appid);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("getTitleByBook.jsp?")
    Observable<VoaBookResponse> getVoaForBook(
            @Query("bookid") int bookid,
            @Query("sign") String sign
    );

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("updateWords.jsp?")
    Observable<UpdateWordResponse> updateWords(
            @Query("bookid") int bookId ,
            @Query("version") int version
    );

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("getWordByUnit.jsp?")
    Observable<UpdateWordResponse> getWords(
            @Query("bookid") int bookId
    );

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("getOfficialAccount.jsp?")
    Observable<OfficialResponse> getOfficialAccount(
            @Query("pageNumber") int pageNumber ,
            @Query("pageCount") int pageCount,
            @Query("newsfrom") String newsfrom
    );

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("chooseLessonNew.jsp?")
    Observable<LessonNewResponse> chooseLessonNew(
            @Query("appid") int appid,
            @Query("uid") int uid,
            @Query("type") String type,
            @Query("version") int version
    );

    interface GetVoa {
        interface Param {
            interface Key {
                String TYPE = "type";
                String FORMAT = "format";
                String PAGES = "pages";
                String PAGE_NUM = "pageNum";
                String MAX_ID = "maxid";
            }

            interface Value {
                String TYPE = "android";
                String FORMAT = "json";

                int PAGE_NUM = 1;
//                int PAGE_SIZE = 10000;
                int PAGE_SIZE = 3500; //减少请求个数
                int RECENT_VOA_ID = 0;
            }
        }
    }

    interface GetVoaText {
        interface Param {
            interface Value {
                String FORMAT = "json";
            }
        }
    }

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static VoaService newVoaService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(VoaService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(VoaService.class);
        }
    }
}
