package com.iyuba.talkshow.lil.help_fix.model.remote.intercept;

import android.text.TextUtils;

import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.NetHostManager;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @desction: 网络拦截器
 * @date: 2023/4/10 22:18
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 *
 * 这里拦截header，识别数据并进行组合
 * 组合的数据分为：前缀(prefix)-可变数据(dynamics)-后缀(suffix)
 */
public class NetIntercept implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        //获取url请求数据
        Request request = chain.request();
        HttpUrl httpUrl = request.url();
        Request.Builder builder = request.newBuilder();
        //获取前缀数据
        String urlPrefix = null;//示例代码：http://ai.
        List<String> prefixData = request.headers(StrLibrary.urlPrefix);
        if (prefixData!=null&&prefixData.size()>0){
            builder.removeHeader(StrLibrary.urlPrefix);
            urlPrefix = prefixData.get(0);
        }
        //获取动态数据
        String urlHost = null;//示例代码：iyuba.cn
        List<String> hostData = request.headers(StrLibrary.urlHost);
        if (hostData!=null&&hostData.size()>0){
            builder.removeHeader(StrLibrary.urlHost);
            urlHost = getDynamicHost(hostData.get(0));
        }
        //获取后缀数据
        String urlSuffix = null;//示例代码：:9001
        List<String> suffixData = request.headers(StrLibrary.urlSuffix);
        if (suffixData!=null&&suffixData.size()>0){
            builder.removeHeader(StrLibrary.urlSuffix);
            urlSuffix = suffixData.get(0);
        }
        //组合新的数据
        if (!TextUtils.isEmpty(urlPrefix)&&!TextUtils.isEmpty(urlHost)) {
            //获取scheme（示例代码：http://ai. -> http）
            int schemeIndex = urlPrefix.indexOf(":");
            String scheme = urlPrefix.substring(0, schemeIndex);

            //获取host（以 http://ai.---iyuba.cn----:9001为例）
            //流程：先获取ai.  之后组合成http://ai.iyuba.cn  最后组合成http://ai.iyuba.cn:9001
            String hostPrefix = urlPrefix.substring(schemeIndex + 3);
            String host = hostPrefix + urlHost;

            //获取端口
            int hostPort = httpUrl.port();
            if (!TextUtils.isEmpty(urlSuffix)){
                if (urlSuffix.startsWith(":")){
                    hostPort = Integer.parseInt(urlSuffix.replace(":",""));
                }
            }

            HttpUrl newUrl = httpUrl
                    .newBuilder()
                    .scheme(scheme)
                    .host(host)
                    .port(hostPort)
                    .build();

            return chain.proceed(builder.url(newUrl).build());
        }

        return chain.proceed(request);
    }

    //获取动态数据
    private String getDynamicHost(String key){
        if (TextUtils.isEmpty(key)){
            return NetHostManager.getInstance().getDomainShort();
        }

        switch (key){
            case NetHostManager.domain_name:
                return NetHostManager.getInstance().getDomainName();
            case NetHostManager.domain_short:
                return NetHostManager.getInstance().getDomainShort();
            case NetHostManager.domain_long:
                return NetHostManager.getInstance().getDomainLong();
        }

        return key;
    }
}
