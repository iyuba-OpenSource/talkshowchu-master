package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.result.GetAliPayInfoResponse;
import com.iyuba.talkshow.data.model.result.GetAliPayResponse;
import com.iyuba.talkshow.data.model.result.GetWXPayInfoResponse;
import com.iyuba.talkshow.data.model.result.NotifyAliPayResponse;
import com.iyuba.talkshow.util.MD5;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import java.text.SimpleDateFormat;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Administrator on 2016/12/21/021.
 */

public interface VipService {
    //http://vip."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"chargeapinew.jsp?
    String ENDPOINT = "http://vip."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"";

    // http://vip."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"chargeapinew.jsp?WIDdefaultbank=&WIDshow_url=&WIDseller_email=iyuba@sina.com&WIDout_trade_no=010320395319384&WIDsubject=VIP&WIDtotal_fee=19.9&WIDbody=%E8%B4%AD%E4%B9%B0%E4%B8%80%E6%9C%88%E5%85%A8%E7%AB%99VIP&app_id=213&userId=1039140&amount=1&product_id=0&code=155129507e80e3d60d0b22b87841758c

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VIP})
    @FormUrlEncoded
    @POST("chargeapinew.jsp?")
    Observable<GetAliPayInfoResponse> getAliPayInfo(@QueryMap Map<String, String> map, @Field("xx") String xx);

    //支付宝的订单信息
    //新的接口增加了抵扣参数：deduction
    //http://vip.iyuba.cn/weixinPay.jsp?wxkey=wxf98d71b9618b19f3&appid=222&weixinApp=wxf98d71b9618b19f3&uid=13801378&money=0.01&amount=1&productid=2&sign=bb78474026d59cd9a44ec5d1afdacb0d&body=%E8%8A%B1%E8%B4%B90.01%E5%85%83%E8%B4%AD%E4%B9%B0%E9%BB%84%E9%87%91%E4%BC%9A%E5%91%98&format=json
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VIP})
    @POST("alipay.jsp?")
    Observable<GetAliPayResponse> getAliPay(@QueryMap Map<String, String> map);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VIP})
    @POST("notifyAliNew.jsp?")
    Observable<NotifyAliPayResponse> NotifyAliPay(@QueryMap Map<String, String> map);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_VIP})
    @FormUrlEncoded
    @POST("weixinPay.jsp?")
    Observable<GetWXPayInfoResponse> getWechatPayInfo(@QueryMap Map<String, String> map, @Field("xx") String xx);


    class GetAliPayInfo {
        public interface Param {
            interface Key {
                String WID_DEFAULT_BANK = "WIDdefaultbank";
                String WID_SHOW_URL = "WIDshow_url";
                String WID_SELLER_EMAIL = "WIDseller_email";
                String WID_OUT_TRADE_NO = "WIDout_trade_no";
                String WID_SUBJECT = "WIDsubject";
                String WID_TOTAL_FEE = "WIDtotal_fee";
                String WID_BODY = "WIDbody";
                String APP_ID = "app_id";
                String USER_ID = "userId";
                String AMOUNT = "amount";
                String PRODUCT_ID = "product_id";
                String CODE = "code";
                String DEDUCATION = "deduction";
            }

            interface Value {
                String WID_DEFAULT_BANK = "";
                String WID_SHOW_URL = "";
                String WID_SELLER_EMAIL = "iyuba@sina.com";
            }
        }

        public interface Result {
            interface Code {
                int SUCCESS = 1;
            }
        }

        private static final String SECRET = "iyuba";
        private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        public static String getCode(int uid) {
            return MD5.getMD5ofStr(uid + SECRET + sdf.format(System.currentTimeMillis()));
        }
    }

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static VipService newVipService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(VipService.ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(VipService.class);
        }
    }


}
