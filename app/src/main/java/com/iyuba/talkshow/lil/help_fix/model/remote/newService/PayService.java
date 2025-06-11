package com.iyuba.talkshow.lil.help_fix.model.remote.newService;

import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.UrlLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.NetHostManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Pay_alipay;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Pay_wx;

import io.reactivex.Observable;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 支付的服务
 */
public interface PayService {

    //获取支付宝的支付链接
    @Headers({StrLibrary.urlPrefix+":"+ UrlLibrary.HTTP_VIP,StrLibrary.urlHost+":"+ NetHostManager.domain_short})
    @POST("/alipay.jsp")
    Observable<Pay_alipay> getAliPayOrderLink(@Query("amount") int amount,
                                              @Query("app_id") int appId,
                                              @Query("userId") int userId,
                                              @Query("code") String sign,
                                              @Query("product_id") int productId,
                                              @Query("WIDsubject") String subject,
                                              @Query("WIDbody") String body,
                                              @Query("WIDtotal_fee") String price);

    //获取微信的支付链接
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_VIP,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @POST("/weixinPay.jsp")
    Observable<Pay_wx> getWxPayOrderLink(@Query("uid") int userId,
                                         @Query("amount") int amount,
                                         @Query("money") String price,
                                         @Query("productid") int productId,
                                         @Query("appid") int appId,
                                         @Query("sign") String sign,
                                         @Query("format") String json,
                                         @Query("weixinApp") String wxKey);
}
