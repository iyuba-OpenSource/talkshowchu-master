package com.iyuba.wordtest.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.iyuba.wordtest.entity.TalkshowTexts;

import java.util.List;

@Dao
public interface TalkShowTextDao {

    // 根据paraId 查询例句
    @Query("select * from TalkshowTexts where voaId = :voaid and  paraId = :paraId")
    TalkshowTexts getSentenceByWord(int voaid  , int paraId ) ;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void intserTexts(List<TalkshowTexts> texts);
}
