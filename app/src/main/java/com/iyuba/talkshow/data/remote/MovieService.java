package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import androidx.annotation.NonNull;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

public interface MovieService{
    String ENDPOINT = "http://apps."+Constant.Web.WEB_SUFFIX.replace("/","")+"/";

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("iyuba/updateCollect.jsp")
    Observable<UpdateCollect> updateCollect(@Query("groupName") String groupName,
                                            @Query("sentenceFlg") String sentenceFlg,
                                            @Query("appId") String appId,
                                            @Query("userId") String userId,
                                            @Query("topic") String topic,
                                            @Query("voaId") String voaId,
                                            @Query("sentenceId") String sentenceId,
                                            @Query("type") String type);

    @Root(name = "response", strict = false)
    class UpdateCollect {
        @Element(required = false)
        public String result;
        @Element(required = false)
        public String msg = "";
        @Element(required = false)
        public String type;
        @Element(required = false)
        public String topic;

        @NonNull
        @Override
        public String toString() {
            return "{result:" + result + ", msg:" + msg + ", type:" + type + ", topic:" + topic + "}";
        }
    }

    class Creator {
        public static MovieService newMovieService() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MovieService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();
            return retrofit.create(MovieService.class);
        }
    }
}
