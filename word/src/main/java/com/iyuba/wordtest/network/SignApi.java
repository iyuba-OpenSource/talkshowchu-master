package com.iyuba.wordtest.network;

import com.iyuba.wordtest.bean.SignBean;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SignApi {

    //    String url = "http://api.iyuba.cn/credits/updateScore.jsp?srid=82&mobile=1&flag=" + time + "&uid=" + userID
//            + "&appid=" + appid+"&idindex="+level;
    @GET("credits/updateScore.jsp?")
    Observable<SignBean> getSign(
            @Query("srid") String srid,
            @Query("mobile") String mobile,
            @Query("flag") String flag,
            @Query("uid") String uid,
            @Query("appid") String appid,
            @Query("idindex") String level
    );
}
