package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.result.CheckAmountResponse;
import com.iyuba.talkshow.data.model.result.ShareInfoResponse;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/12/19/019.
 */

public interface PayService {
    String ENDPOINT = "http://app."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX;

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APP})
    @GET("pay/checkApi.jsp?")
    Observable<CheckAmountResponse> checkAmount(@Query("userId") int uid);
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APP})
    @GET("getShareInfo.jsp?")
    Observable<ShareInfoResponse> getShareInfo(@Query("uid") int uid, @Query("appId") int appId,
                                               @Query("pageNum") int pageNum, @Query("pageCount") int pageCount);
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APP})
    @GET("getShareInfoShow.jsp?")
    Observable<ShareInfoResponse> getCalendar(@Query("uid") int uid, @Query("appId") int appId, @Query("time") String time);

    interface CheckAmount {
        interface Result {
            interface Code {
                int SUCCESS = 1;
            }
        }
    }

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static PayService newPayService() {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(PayService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(PayService.class);
        }
    }
}
