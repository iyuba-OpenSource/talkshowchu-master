package com.iyuba.wordtest.network;

import com.iyuba.wordtest.entity.WordAiEntity;
import com.iyuba.wordtest.entity.WordEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WordApi {

    /*setAbsoluteURI("http://word.iyuba.cn/words/updateWord.jsp?userId="
                           + this.userId + "&mod=" + update_mode + "&groupName="
                           + groupname + "&word=" + this.word);*/
    @GET("words/updateWord.jsp?")
    Observable<String>operateWord(
            @Query("word") String word,
            @Query("mod") String mod,
            @Query("groupName") String groupname,
            @Query("userId") String userId
    );

    @GET("words/apiWord.jsp?") //查询单词详情
    Observable<WordEntity> getWordApi(@Query("q") String word);

    @GET("words/apiWordAi.jsp?")
    Observable<WordAiEntity> getWordAi(@Query("q") String word, @Query("user_pron") String user_pron, @Query("ori_pron") String ori_pron,
                                       @Query("appid") String appid, @Query("uid") String uid);
}
