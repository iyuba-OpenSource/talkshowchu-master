package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.DownLoadJFResult;
import com.iyuba.talkshow.data.model.IntegralBean;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * IntegralService
 *
 * @author wayne
 * @date 2018/2/8
 */
public interface IntegralService {
      /*
    http://api."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"credits/updateScore.jsp?srid=40&mobile=1" +
            "&flag=%s=&uid=%s&appid=%s&idindex=%s", flag, AccountManager.Instace(mContext).getId(), Constant.APPID, idIndex
    */

    //这里修改baseurl
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_API})
    @GET
    Observable<IntegralBean> deductIntegral(
            @Url String url,
            @Query("flag") String flag,
            @Query("uid") int uid,
            @Query("appid") int appid,
            @Query("idindex") int idIndex
    );

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_APIS})
    @GET("credits/updateScore.jsp")
    Observable<DownLoadJFResult> integral(@Query("srid") String srid,
                                          @Query("mobile") int mobile,
                                          @Query("flag") String flag,
                                          @Query("uid") int uid,
                                          @Query("appid") int appid,
                                          @Query("idindex") int idindex);

    class Creator {

        public static IntegralService newIntegralService() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FeedbackService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(IntegralService.class);
        }
    }
}
