package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.result.AppUpdateResponse;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

public interface VersionService {
    String ENDPOINT = "http://api."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"mobile/android/";

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_API})
    @GET(App.SHARE_NAME_EN + "/islatest.plain?")
    Observable<AppUpdateResponse> checkVersion(
            @Query("currver") int version,
            @Query("format") String format
    );

    interface CheckVersion {
        interface Param {
            interface Value {
                String FORMAT = "xml";
            }
        }

        interface Result {
            interface Code {
                String LATEST_VERSION = "YES";
                String OLD_VERSION = "NO";
            }

        }
    }

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static VersionService newVersionService() {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(VersionService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(VersionService.class);
        }
    }
}
