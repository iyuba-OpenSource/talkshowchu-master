package com.iyuba.talkshow.newdata;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by carl shen on 2020/8/3
 * New Primary English, new study experience.
 */
public class RetrofitUtils {


    private static RetrofitUtils retrofitUtils;

    private RetrofitUtils() {
    }

    public static RetrofitUtils getInstance() {

        if (retrofitUtils == null) {
            synchronized (RetrofitUtils.class) {
                if (retrofitUtils == null) {
                    retrofitUtils = new RetrofitUtils();
                }
            }
        }
        return retrofitUtils;
    }


    private static Retrofit retrofit;

    private static synchronized Retrofit getRetrofit(String url) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {

                Log.d("RetrofitUtils", message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient ok = RetrofitUrlManager.getInstance().with(new OkHttpClient.Builder()).addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS).build();

        retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create())

                .client(ok).build();
        return retrofit;
    }

    private static synchronized Retrofit getRetrofit(String url, String xml) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {

//                Log.d("RetrofitUtils", message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient ok = RetrofitUrlManager.getInstance().with(new OkHttpClient.Builder()).addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(SimpleXmlConverterFactory.create()).client(ok).build();
        return retrofit;
    }


    public <T> T getApiService(String url, Class<T> cl) {
        Retrofit retrofit = getRetrofit(url);
        return retrofit.create(cl);
    }

    public <T> T getApiService(String url, String xml, Class<T> cl) {
        Retrofit retrofit = getRetrofit(url, xml);
        return retrofit.create(cl);
    }

}
