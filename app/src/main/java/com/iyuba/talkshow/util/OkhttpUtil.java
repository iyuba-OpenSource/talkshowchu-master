package com.iyuba.talkshow.util;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkhttpUtil {

    //下载文件
    public static void downloadFile(String downUrl, Callback callback){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .proxy(Proxy.NO_PROXY)
                .connectionPool(new ConnectionPool(10,30,TimeUnit.SECONDS))
                .build();

        Request request = new Request.Builder()
                .url(downUrl)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }
}
