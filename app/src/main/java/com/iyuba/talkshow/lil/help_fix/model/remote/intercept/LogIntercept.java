package com.iyuba.talkshow.lil.help_fix.model.remote.intercept;

import android.util.Log;

import com.iyuba.talkshow.lil.help_mvp.util.gson.GsonUtil;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @desction: 日志拦截器
 * @date: 2023/4/10 23:25
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 *
 * 将请求的数据格式化显示
 */
public class LogIntercept implements Interceptor {
    private static final String TAG = "LogIntercept";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        printInfo(request,response);
        return response;
    }

    //输出信息
    private void printInfo(Request request,Response response){
        if (request!=null&&response!=null){
            String logInfo = "远程接口数据内容输出：".concat(" \r\n ")
                    .concat("Request Url -->: ")
                    .concat(request.method())
                    .concat(" ")
                    .concat(request.url().toString())
                    .concat(" \r\n ")
                    .concat("Request Headers -->: ")
                    .concat(getRequestHeaders(request))
                    .concat(" \r\n ")
                    .concat("Request Params -->: ")
                    .concat(getRequestParams(request))
                    .concat(" \r\n ")
                    .concat("Response Result -->: ")
                    .concat(getResponseText(response));

            Log.w("接口拦截器日志数据", logInfo);
        }
    }

    /***********************分类获取方法********************/
    //获取请求的header
    private String getRequestHeaders(Request request){
        Headers headers = request.headers();
        if (headers.size()>0){
            return headers.toString();
        }else {
            return "Empty!";
        }
    }

    //获取请求的参数
    private String getRequestParams(Request request){
        String params = "Empty!";
        try {
            RequestBody body = request.body();
            if (body!=null){
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                MediaType mediaType = body.contentType();
                if (mediaType!=null){
                    Charset charset = mediaType.charset(Charset.forName("UTF-8"));
                    if (charset!=null){
                        params = buffer.readString(charset);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return params;
    }

    //获取响应的结果
    private String getResponseText(Response response){
        String text = "Empty!";
        try {
            ResponseBody body = response.body();
            if (body!=null&&body.contentLength()!=0){
                BufferedSource source = body.source();
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.buffer();
                MediaType mediaType = body.contentType();
                if (mediaType!=null){
                    Charset charset = mediaType.charset(Charset.forName("UTF-8"));
                    if (charset!=null){
                        text = buffer.clone().readString(charset);
                    }
                }
            }

            //格式化输出内容
            text = GsonUtil.formatJson(text);
        }catch (Exception e){
            e.printStackTrace();
            text = "";
        }
        return text;
    }
}
