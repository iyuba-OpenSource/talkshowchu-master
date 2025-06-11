package com.iyuba.wordtest.db;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

import com.iyuba.wordtest.entity.CetRootWord;

import java.util.List;

@Dao
public interface CetRootWordDAO {

    @Query("select * from CetRootWord order by word COLLATE NOCASE asc")  // 不区分大小写
    List<CetRootWord> getAllRootWord() ;

    @Query("select * from CetRootWord where stage >=:i ")
    List<CetRootWord> getAllRootWord(int i) ;

    @Query("select * from CetRootWord where flag =1 ")
    List<CetRootWord> getWordsCollect() ;

    @Query("select * from CetRootWord where remembered = 3")
    List<CetRootWord> getTodayWords() ;

    @Query("select * from CetRootWord where remembered = :sign")
    List<CetRootWord> getWords(int sign) ;

    @Query("UPDATE CetRootWord SET remembered = :remembered WHERE word = :changword")
    void updateWord(int remembered, String changword);

    @Query("UPDATE CetRootWord SET remembered = :a WHERE remembered = :remembered")
    void updateTodayWord(int a, int remembered);


    @Query("select * from CetRootWord where remembered = :reme")
    List<CetRootWord> getToLearnRootWord(int reme) ;


    @Query("select def from CetRootWord")
    List<String> getAllAnswers();

    @Query("select * from CetRootWord where stage = :Stage")
    List<CetRootWord> getWordsByStage(int Stage);

    @Update()
    void updateWordSetStage(List<CetRootWord> word);

    @Update()
    void updateSingleWord(CetRootWord word);


}
