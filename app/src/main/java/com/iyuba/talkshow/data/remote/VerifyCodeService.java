package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.result.GetVerifyCodeResponse;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/12/15/015.
 */

public interface VerifyCodeService {
    String ENDPOINT = "http://api.iyuba.com.cn/";

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @GET("sendMessage3.jsp?")
    Observable<GetVerifyCodeResponse> getCode(@Query("userphone") String userPhone,
                                                    @Query("format") String format);

    interface GetCode {
        interface Param {
            interface Value {
                String FORMAT = "json";
            }
        }

        interface Result {
            interface Code {
                int SUCCESS = 1;
                int REGISTERED = -1;
            }

            interface Message {
                String REGISTERED = "手机号已注册，请使用手机号登陆或换一个号码试试~";
                String REGISTERS = "手机号重试次数过于频繁，请稍后再试或换一个号码试试~";
                String FAILURE = "出错了!";
            }
        }
    }

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static VerifyCodeService newVerifyCodeService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(VerifyCodeService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(VerifyCodeService.class);
        }
    }
}
