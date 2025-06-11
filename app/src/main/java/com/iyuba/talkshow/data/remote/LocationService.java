package com.iyuba.talkshow.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.result.location.GetLocationResponse;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/12/23/023.
 */

public interface LocationService {
    String ENDPOINT = "http://maps.google.cn/maps/api/geocode/";

    @GET("json?")
    Observable<GetLocationResponse> getLocation(@Query("latlng") String latLng,
                                                @Query("sensor") boolean sensor,
                                                @Query("language") String language);

    interface GetLocation {
        interface Param {
            class Value {
                public static boolean SENSOR = true;
                public static String LANGUAGE = "zh-CN";
                private static final String COMMA = ",";

                public static String getLatLng(String lat, String lng) {
                    return lat + COMMA + lng;
                }
            }
        }

        interface Result {
            interface Code {
                String SUCCESS = "OK";
            }

            class Value {
                public static final String PROVINCE = "administrative_area_level_1";
                public static final String CITY = "locality";
                public static final String SUB_CITY = "sublocality";
                private static final String SEP = " ";

                public static String getLocation(String province, String city, String subCity) {
                    return province + SEP + city + SEP + subCity;
                }
            }
        }
    }

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static LocationService newLocationService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(LocationService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(LocationService.class);
        }
    }
}
