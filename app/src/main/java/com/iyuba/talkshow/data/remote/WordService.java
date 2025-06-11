package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.WordResponse;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;


import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

public interface WordService {
    String ENDPOINT = "http://word."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"words/";

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_WORD})
    @GET("apiWord.jsp")
    Observable<WordResponse> getNetWord(@Query("q") String key  );
//    private static Converter.Factory xmlConverterFactory = SimpleXmlConverterFactory.create();

//    @GET("words/updateWord.jsp")
//    Single<WordResponse.Update> updateWords(@Query("userId") int userId,
//                                            @Query("mod") String mode,
//                                            @Query("groupName") String groupName,
//                                            @Query("word") String wordsStr);


    class Creator {

        public static WordService newWordService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WordService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
//                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();
            return retrofit.create(WordService.class);
        }
    }
}
