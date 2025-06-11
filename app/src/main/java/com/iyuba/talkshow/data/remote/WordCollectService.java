package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.ui.words.WordCollectResponse;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface WordCollectService {

    String ENDPOINT = "http://word."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX;

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_WORD})
    @GET("words/updateWord.jsp")
    Single<WordCollectResponse.Update> updateWords(@Query("userId") int userId,
                                                   @Query("mod") String mode,
                                                   @Query("groupName") String groupName,
                                                   @Query("word") String wordsStr);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_WORD})
    @GET("words/wordListService.jsp")
    Single<WordCollectResponse.GetNoteWords> getNoteWords(@Query("u") int userId,
                                                       @Query("pageNumber") int pageNumber,
                                                       @Query("pageCounts") int pageCount);


    class Creator {

        public static WordCollectService newWordCollectService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WordCollectService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
//                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();
            return retrofit.create(WordCollectService.class);
        }
    }

}
