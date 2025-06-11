package com.iyuba.talkshow.lil.help_mvp.util.okhttp3;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @desction: okhttp3工具
 * @date: 2023/4/8 12:30
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class OkHttp3Util {

    private static OkHttp3Util instance;

    public static OkHttp3Util getInstance(){
        if (instance==null){
            synchronized (OkHttp3Util.class){
                if (instance==null){
                    instance = new OkHttp3Util();
                }
            }
        }

        return instance;
    }

    public OkHttp3Util(){
        if (executorService==null){
            //如果是CPU密集型应用，则线程池大小设置为N+1
            //如果是IO密集型应用，则线程池大小设置为2N+1
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
        }
    }

    /**************线程****************/
    private ExecutorService executorService;

    /**************超时****************/
    private static final int TIMEOUT_READ = 10;//读取超时
    private static final int TIMEOUT_WRITE = 10;//写入超时
    private static final int TIMEOUT_CONNECT = 10;//链接超时


    /**************类型****************/
    //表单提交(键值对形式，上传文件时需要使用这个)
    public static final MediaType FORM_DATA = MediaType.parse("multipart/form-data");
    //表单提交(会被url编码)
    public static final MediaType X_FORM_DATA = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    //json类型的字符串
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //文本(空格转化为+号，但是不对特殊字符编码)
    public static final MediaType TEXT = MediaType.parse("text/plain");

    //二进制(将数据转换成二进制，只能转换一个数据)
    public static final MediaType STEAM = MediaType.parse("application/octet-stream");



    //获取okhttpClient
    public OkHttpClient.Builder getClient(){
        return new OkHttpClient.Builder()
//                .proxy(Proxy.NO_PROXY)
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_WRITE,TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT_CONNECT,TimeUnit.SECONDS);
    }


    /***********************转换数据**************************/
    //将数据转换为requestBody类型
    public RequestBody transRequestBody(String data){
        return RequestBody.create(TEXT,data);
    }

    //将文件转换为requestBody类型
    public RequestBody transRequestBody(File file){
        return MultipartBody.create(STEAM,file);
    }

    //将数据转换成multipartBody类型
    public MultipartBody transMultipartBody(Map<String,Object> map){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (String key:map.keySet()){
            Object obj = map.get(key);

            if (obj instanceof String){
                //文本类型
                String text = (String) obj;
                builder.addFormDataPart(key, text);
            }else if (obj instanceof File){
                //文件类型
                File file = (File) obj;
                builder.addFormDataPart(key,file.getName(),transRequestBody(file));
            }
        }

        return builder.build();
    }


    /************************请求数据************************/
    //get类型
    public void get(String url, Map<String,String> map, Callback callback){
        List<String> tempList = new ArrayList<>();
        for (String key:map.keySet()){
            tempList.add(key+"="+map.get(key));
        }

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < tempList.size(); i++) {
            if (i!=0){
                buffer.append("&");
            }
            buffer.append(tempList.get(i));
        }

        String suffix = buffer.toString();
        if (!TextUtils.isEmpty(suffix)){
            url = url+"?"+suffix;
        }

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        //切入子线程
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                getClient().build().newCall(request).enqueue(callback);
            }
        });
    }

    //get类型
    public void get(String url,Map<String,String> headerMap,Map<String,String> map,Callback callback){
        List<String> temp = new ArrayList<>();
        for (String key:map.keySet()){
            temp.add(key+"="+map.keySet());
        }

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < temp.size(); i++) {
            if (i!=0){
                buffer.append("&");
            }

            buffer.append(temp.get(i));
        }

        //url
        String suffix = buffer.toString();
        if (!TextUtils.isEmpty(suffix)){
            url = url+"?"+suffix;
        }

        //header
        Request.Builder builder = new Request.Builder()
                .url(url)
                .get();
        for (String key:headerMap.keySet()){
            builder.addHeader(key,headerMap.get(key));
        }

        //切到子线程
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                getClient().build().newCall(builder.build()).enqueue(callback);
            }
        });
    }

    //post类型
    public void post(String url,Map<String,Object> map,Callback callback){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        //参数
        for (String key:map.keySet()){
            Object obj = map.get(key);

            if (obj instanceof String){
                builder.addPart(transRequestBody((String) obj));
            }else if (obj instanceof File){
                builder.addPart(transRequestBody((File) obj));
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();

        //切换到子线程
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                getClient().build().newCall(request).enqueue(callback);
            }
        });
    }
}
