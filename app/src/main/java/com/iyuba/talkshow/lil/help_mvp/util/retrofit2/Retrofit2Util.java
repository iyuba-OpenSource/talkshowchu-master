package com.iyuba.talkshow.lil.help_mvp.util.retrofit2;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * @desction: retrofit2工具
 * @date: 2023/4/8 12:30
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class Retrofit2Util {

    private static Retrofit2Util instance;

    public static Retrofit2Util getInstance(OkHttpClient.Builder builder){
        if (instance==null){
            synchronized (Retrofit2Util.class){
                if (instance==null){
                    instance = new Retrofit2Util(builder);
                }
            }
        }
        return instance;
    }

    public Retrofit2Util(OkHttpClient.Builder builder){
        this.client = builder.build();
    }

    //client
    private OkHttpClient client;


    /********************创建类型*****************/
    //创建普通类型
    public <T> T create(Class<T> service,String baseUrl){
        if (service==null){
            throw new RuntimeException("Service is null");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(service);
    }

    //创建json类型
    public <T> T createJson(Class<T> service,String baseUrl){
        if (service==null){
            throw new RuntimeException("Service is null");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(service);
    }

    //创建xml类型
    public <T> T createXml(Class<T> service,String baseUrl){
        if (service==null){
            throw new RuntimeException("Service is null");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(service);
    }
}
