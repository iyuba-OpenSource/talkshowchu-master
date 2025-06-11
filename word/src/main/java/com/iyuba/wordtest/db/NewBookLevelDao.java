package com.iyuba.wordtest.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.iyuba.wordtest.entity.NewBookLevels;

import java.util.List;

@Dao
public interface NewBookLevelDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void saveBookLevel(NewBookLevels... bookLevels);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void  updateBookLevel(NewBookLevels... bookLevels);

    @Query("UPDATE NewBookLevels set version=:version where bookId =:book_id")
    void updateBookVersion(int book_id ,int version);

    @Query("UPDATE NewBookLevels set download = :download where bookId =:book_id and uid = :uid")
    void updateBookDownload(int book_id, String uid ,int download);

    @Query("delete from NewBookLevels where uid = :uid")
    void deleteBookLevel(String uid);

    @Delete
    void deleteBookLevel(NewBookLevels book);

    @Query("select * from NewBookLevels ")
    List<NewBookLevels> getAllLevel();

    @Query("select * from NewBookLevels where uid = :uid")
    List<NewBookLevels> getUidLevel(String uid);

    @Query("select * from NewBookLevels where bookId = :bookId and uid = :uid")
    NewBookLevels getBookLevel(int bookId, String uid);

    @Query("select bookId from NewBookLevels where download = 1")
    List<Integer> getDownloaded();

}
