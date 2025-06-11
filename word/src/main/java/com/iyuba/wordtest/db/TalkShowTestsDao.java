package com.iyuba.wordtest.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.iyuba.wordtest.entity.TalkShowTests;
import com.iyuba.wordtest.entity.TalkShowWords;

import java.util.List;

@Dao
public interface TalkShowTestsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[]  insertWord(TalkShowTests... word);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[]  insertWord(List<TalkShowTests> words);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[]  insertWordIgnore(List<TalkShowTests> words);

    @Query("select * from TalkShowTests where book_id = :bookId ")
    List<TalkShowTests> getBookWords(int bookId) ;

    @Query("select * from TalkShowTests where book_id = :bookId and unit_id = :unit and uid = :uid order by position asc")
    List<TalkShowTests> getUnitWords(int bookId , int unit, String uid) ;

    @Query("select * from TalkShowTests where book_id = :bookId and unit_id = :unit and wrong = :wrong and uid = :uid and answer=:answer order by position asc")
    List<TalkShowTests> getWrongWords(int bookId , int unit, int wrong, String uid, String answer) ;

    @Query("select * from TalkShowTests where book_id = :bookId and unit_id = :unit and answer = :answer and uid = :uid order by position asc")
    List<TalkShowTests> getAnswerWords(int bookId , int unit, String answer, String uid) ;

    @Query("select * from TalkShowTests where uid = :uid ")
    List<TalkShowTests> getUidWords(String uid) ;
    @Query("select * from TalkShowTests where book_id = :bookId and voa_id = :voa and uid = :uid")
    List<TalkShowTests> getUnitByVoa(int bookId , int voa, String uid) ;

    @Query("delete from TalkShowTests where uid = :uid ")
    void deleteWordTest(String uid);

    @Delete
    void deleteWordTest(TalkShowTests words);

    @Query("select * from TalkShowTests where book_id = :bookId and unit_id = :unit and position = :position  and uid = :uid")
    TalkShowTests getUnitWord(int bookId , int unit , int position, String uid) ;

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateSingleWord(TalkShowTests word);

    //删除某个单元下的单词测试信息
    @Query("delete from TalkShowTests where uid=:uid and book_id=:bookId and unit_id=:unitId")
    void deleteWordTest(int uid,int bookId, int unitId);

    //更新某个单元下的单词的测试正确和错误信息
    @Query("update TalkShowTests set wrong=:wrong where book_id=:bookId and unit_id=:unitId and uid=:uid")
    void updateWordTest(int wrong,int bookId,int unitId,int uid);
}
