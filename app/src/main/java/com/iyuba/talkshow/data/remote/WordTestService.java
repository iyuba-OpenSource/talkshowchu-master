package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.AllWordsRespons;
import com.iyuba.talkshow.data.model.UpdateWordResponse;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

public interface WordTestService {
    String ENDPOINT = "http://m."+Constant.Web.WEB_SUFFIX;

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_M})
    @GET("jlpt1/getConceptWordOver.jsp ")
    Observable<AllWordsRespons> getAllWords();

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_M})
    @GET("jlpt1/getConceptWordOver.jsp ")
    Observable<AllWordsRespons> getAllWordsByType(@Query("app") String type );

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_M})
    @GET("jlpt1/getConceptWordOver.jsp")
    Observable<AllWordsRespons> getWordsByBookId(@Query("bookID") String id );

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_M})
    @GET("jlpt1/getConceptWordOver.jsp")
    Observable<AllWordsRespons> getWordsByBookUnit(@Query("boodID") String id ,@Query("unitID") String unitId);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_M})
    @GET("jlpt1/getConceptWordOver.jsp")
    Observable<UpdateWordResponse> updateWordByBookId(int bookId, int version);

    class Creator {

        public static WordTestService newWordTestService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WordTestService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(WordTestService.class);
        }
    }
}
