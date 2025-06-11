package com.iyuba.talkshow.lil.help_fix.model.remote;

import com.iyuba.talkshow.lil.help_fix.data.library.UrlLibrary;
import com.iyuba.talkshow.lil.help_fix.model.remote.intercept.NetIntercept;
import com.iyuba.talkshow.lil.help_mvp.util.okhttp3.OkHttp3Util;
import com.iyuba.talkshow.lil.help_mvp.util.retrofit2.Retrofit2Util;

import okhttp3.OkHttpClient;

/**
 * @title:
 * @date: 2023/5/19 11:26
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RemoteManager {

    private static RemoteManager instance;

    public static RemoteManager getInstance(){
        if (instance==null){
            synchronized (RemoteManager.class){
                if (instance==null){
                    instance = new RemoteManager();
                }
            }
        }
        return instance;
    }

    //获取client
    //addInterceptor：在请求之前先触发，可以替换请求内容进行处理
    //addNetworkInterceptor：在请求之后触发，可以更改重定向功能等
    private OkHttpClient.Builder getBuilder(){
        OkHttpClient.Builder builder = OkHttp3Util.getInstance().getClient();
        builder.addInterceptor(new NetIntercept());//增加网络拦截器
//        builder.addInterceptor(new LogIntercept());//增加日志拦截器
        return builder;
    }

    //普通类型
    public <T> T create(Class<T> service){
        return Retrofit2Util.getInstance(getBuilder())
                .create(service, UrlLibrary.BASE_URL);
    }

    //json类型
    public <T> T createJson(Class<T> service){
        return Retrofit2Util.getInstance(getBuilder())
                .createJson(service, UrlLibrary.BASE_URL);
    }

    //xml类型
    public <T> T createXml(Class<T> service){
        return Retrofit2Util.getInstance(getBuilder())
                .createXml(service, UrlLibrary.BASE_URL);
    }
}
