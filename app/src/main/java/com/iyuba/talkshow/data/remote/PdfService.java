package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.result.PdfResponse;
import com.iyuba.talkshow.ui.user.edit.GetIpResponse;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

public interface PdfService {

    String ENDPOINT = "http://apps."+Constant.Web.WEB_SUFFIX.replace("/","")+"/";

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("minutes/doCheckIP.jsp")
    Observable<GetIpResponse> getIpRequest(@Query("uid") String uid,
                                           @Query("appid") String appid);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APPS})
    @GET("iyuba/getVoapdfFile_new.jsp")
    Observable<PdfResponse> getPdf(@Query("type")String type ,@Query("voaid") int voaId , @Query("isenglish") int isEnglish);

    class Creator {

        public static PdfService newPdfServcie() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(PdfService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
//                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            return retrofit.create(PdfService.class);
        }
    }
}
