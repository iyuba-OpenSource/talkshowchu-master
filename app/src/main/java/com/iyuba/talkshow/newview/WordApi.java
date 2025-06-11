package com.iyuba.talkshow.newview;

import com.iyuba.wordtest.entity.WordAiEntity;
import com.iyuba.wordtest.entity.WordEntity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 单词  单个单词获取解释
 * Created by liuzhenli on 2017/5/5.
 * http://word.iyuba.cn/words/apiWord.jsp?q=" + word
 */

public interface WordApi {


    @GET("apiWord.jsp") //查询单词详情
    Call<WordEntity> getWordApi(@Query("q") String word);

    @GET("wordListService.jsp") //获取收藏单词列表
    Call<WordCollect> getWordList(@Query("u") String userId, @Query("pageCounts") int pageCounts);

    @GET("updateWord.jsp") //查询单词详情
    Call<WordEntity> updateWord(@Query("userId") String userId,
                                @Query("mod") String mod,
                                @Query("groupName") String groupName,
                                @Query("word") String word);

    @GET("apiWordAi.jsp")
    Call<WordAiEntity> getWordAi(@Query("q") String word, @Query("user_pron") String user_pron, @Query("ori_pron") String ori_pron,
                                 @Query("appid") int appid, @Query("uid") int uid);

}
