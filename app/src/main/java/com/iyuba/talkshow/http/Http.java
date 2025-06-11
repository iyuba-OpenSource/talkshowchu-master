package com.iyuba.talkshow.http;

import java.util.concurrent.TimeUnit;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Http
 */
public class Http {
    private volatile static OkHttpClient okHttpClient;
    private static int timeout = 15;

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (Http.class) {
                if (okHttpClient == null) {

                    okHttpClient = RetrofitUrlManager.getInstance().with(new OkHttpClient.Builder())
                            .addInterceptor(getInterceptor())
                            .connectTimeout(timeout, TimeUnit.SECONDS)
                            .writeTimeout(timeout, TimeUnit.SECONDS)
                            .readTimeout(timeout, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return okHttpClient;
    }


    public static Interceptor getInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    public static void get(String url, Callback callback) {
        get(getOkHttpClient(), url, callback);
    }

    public static void get(OkHttpClient client, String url, Callback callback) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setClient(OkHttpClient okHttpClient) {
        Http.okHttpClient = okHttpClient;
    }

    public static void setTimeout(int timeout) {
        Http.timeout = timeout;
    }
}