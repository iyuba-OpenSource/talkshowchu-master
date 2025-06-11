package com.iyuba.wordtest.network;

import com.iyuba.wordtest.bean.DownLoadJFResult;
import com.iyuba.wordtest.bean.UploadExamResult;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface UploadExamApi {


//    http://daxue."+ WebConstant.WEB_SUFFIX+"ecollege/updateTestRecordNew.jsp?

    @POST("ecollege/updateExamRecord.jsp")
    Observable<UploadExamResult> uploadWordExam(@Body RequestBody body);

    @POST("ecollege/updateExamRecordNew.jsp")
    Observable<UploadExamResult> uploadWordExamNew(@Body RequestBody body);

    //上传积分
    @GET
    Observable<DownLoadJFResult> integral(@Url String url,
                                          @Query("srid") String srid,
                                          @Query("mobile") int mobile,
                                          @Query("flag") String flag,
                                          @Query("uid") int uid,
                                          @Query("appid") int appid,
                                          @Query("idindex") int idindex);
}
