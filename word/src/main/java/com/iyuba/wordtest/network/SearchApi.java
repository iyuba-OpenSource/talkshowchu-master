package com.iyuba.wordtest.network;

import com.iyuba.wordtest.bean.SearchVoaResult;
import com.iyuba.wordtest.entity.UpdateWordResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchApi {

    @GET("iyuba/searchApiNew.jsp")
    Observable<SearchVoaResult> getSearchResult(@Query("format") String format,
                                                @Query("key") String key,
                                                @Query("pages") int pages,
                                                @Query("pageNum") int pageNum,
                                                @Query("parentId") int parentId,
                                                @Query("type") String type,
                                                @Query("flg") String flg);

    @GET("iyuba/updateWords.jsp?")
    Observable<UpdateWordResponse> updateWords(
            @Query("bookid") int bookId ,
            @Query("version") int version
    );

    @GET("iyuba/textExamApi.jsp")
    Observable<SearchTextResult> getTexts(@Query("format") String format,
                                                @Query("voaid") int voaId);
}

