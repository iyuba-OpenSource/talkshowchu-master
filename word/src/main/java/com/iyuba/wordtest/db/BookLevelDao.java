package com.iyuba.wordtest.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.iyuba.wordtest.entity.BookLevels;

import java.util.List;


@Dao
public interface BookLevelDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void saveBookLevel(BookLevels... bookLevels);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void  updateBookLevel(BookLevels... bookLevels);

    @Query("UPDATE BookLevels set version=:version where bookId =:book_id")
    void updateBookVersion(int book_id ,int version);

    @Query("UPDATE BookLevels set download = :download where bookId =:book_id")
    void updateBookDownload(int book_id ,int download);

    @Delete
    void deleteBookLevel(BookLevels book);

    @Query("select * from BookLevels ")
    List<BookLevels> getAllLevel();

    @Query("select * from BookLevels where bookId = :bookId")
    BookLevels getBookLevel(int bookId);

    @Query("select bookId from BookLevels where download = 1")
    List<Integer> getDownloaded();

}
