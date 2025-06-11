package com.iyuba.talkshow.newdata;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.iyuba.talkshow.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by carl shen on 2020/9/18
 * New Primary English, new study experience.
 */
public interface UpdateEvalDataApi {
    String BASEURL = "http://iuserspeech." + Constant.Web.WEB_SUFFIX.replace("/","") + ":9001/management/";
    String url = "http://iuserspeech." + Constant.Web.WEB_SUFFIX.replace("/","") + ":9001/management/getVoaTestRecord.jsp";
//    http://ai.iyuba.cn/management/getVoaTestRecord.jsp?userId=5492787&newstype=concept
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_AI})
    @GET
    Call<UpdateEvalDataBean> getVoaTestRecord(
            @Url String url,
            @Query("userId") String userId,
            @Query("newstype") String newstype
    );
}
