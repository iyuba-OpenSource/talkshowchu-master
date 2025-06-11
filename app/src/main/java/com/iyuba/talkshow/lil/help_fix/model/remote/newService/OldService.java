package com.iyuba.talkshow.lil.help_fix.model.remote.newService;

import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_data;
import com.iyuba.talkshow.newdata.EvaluateBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * 旧的服务功能接口
 */
public interface OldService {

    //评测课程句子接口
    @POST
    Observable<BaseBean_data<EvaluateBean>> evalSentence(@Url String url,
                                                         @Body RequestBody body);
}
