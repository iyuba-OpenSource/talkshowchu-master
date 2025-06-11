package com.iyuba.wordtest.network;

import com.google.gson.Gson;
import com.iyuba.module.commonvar.CommonVars;
import com.iyuba.wordtest.bean.UploadTestBean;

import java.io.File;
import java.util.concurrent.TimeUnit;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class HttpManager {
    private static  OkHttpClient okHttpClient ;

    private static Retrofit retrofit ;
    private static SearchApi searchApi;
    private static SignApi signApi;

    public static EvaluateApi getEvaluateApi(){
        buildOkHttpClent();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)

                .baseUrl("http://iuserspeech." + CommonVars.domain.replace("/","") + ":9001/test/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(EvaluateApi.class);
    }

    public static WordApi getWordAi(){
        buildOkHttpClent();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://word." + CommonVars.domain + "/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WordApi.class);
    }

    public static WordApi getWordApi(){
        buildOkHttpClent();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://word." + CommonVars.domain + "/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        return retrofit.create(WordApi.class);
    }

    public static UploadExamApi getUploadExamApi(){
        buildOkHttpClent();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://daxue." + CommonVars.domain + "/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(UploadExamApi.class);
    }

    public static SearchApi getSearchApi() {
        if (searchApi == null) {
            buildOkHttpClent();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl("http://apps." + CommonVars.domain + "/")
                    .build();
            searchApi = retrofit.create(SearchApi.class);
        }
        return searchApi;
    }
    private static Interceptor getInterceptor() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    private static void buildOkHttpClent() {
        okHttpClient = RetrofitUrlManager.getInstance().with(new OkHttpClient.Builder())

                .addInterceptor(getInterceptor())
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }
    public static RequestBody fromString(String text) {
        return RequestBody.create(MediaType.parse("text/plain"), text);
    }

    public static MultipartBody.Part fromFile(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }


    public static SignApi getSignApi() {
        if (signApi == null) {
            buildOkHttpClent();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl("http://apps." + CommonVars.domain + "/")
                    .build();
            signApi = retrofit.create(SignApi.class);
        }
        return signApi;
    }

     public static  RequestBody  getBody(UploadTestBean item) {
        Gson gson = new Gson();
        String json = gson.toJson(item);
         return RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json);
    }
}
