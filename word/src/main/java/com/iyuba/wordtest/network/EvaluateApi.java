package com.iyuba.wordtest.network;

import com.iyuba.wordtest.bean.SendEvaluateResponse;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface EvaluateApi {
    @Multipart
    @POST("eval/")
    Observable<SendEvaluateResponse> sendVoice(@PartMap Map<String, RequestBody> optionMap,
                                               @Part MultipartBody.Part file);

    @Multipart
    @POST("ai/")
    Observable<SendEvaluateResponse> evalAi(@PartMap Map<String, RequestBody> optionMap,
                                      @Part MultipartBody.Part file);

    //单词评测接口修改，与句子进行拆分(2022-12-08)
    @Multipart
    @POST("ai10/")
    Observable<SendEvaluateResponse> evalAi10(@PartMap Map<String, RequestBody> optionMap,
                                            @Part MultipartBody.Part file);

    interface GetVoa {
        interface Param {
            interface Key {
                String TYPE = "type";
                String SENTENCE = "sentence";
                String USERID = "userId";
                String NEWSID = "newsId";
                String PARAID = "paraId";
                String IDINDEX = "IdIndex";
            }

            interface Value {
                String TYPE = "android";
                String FORMAT = "json";

                int PAGE_NUM = 1;
                //                int PAGE_SIZE = 10000;
                int PAGE_SIZE = 600; //减少请求个数
                int RECENT_VOA_ID = 0;
            }
        }
    }



//    @FormUrlEncoded
//    @POST("merge/")
//    Observable<EvaMixBean> audioComposeApi(@Field(value = "audios", encoded = true) String audios, @Field("type") String type);

}
