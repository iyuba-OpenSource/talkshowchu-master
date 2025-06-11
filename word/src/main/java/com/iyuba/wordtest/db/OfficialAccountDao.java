package com.iyuba.wordtest.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OfficialAccountDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void saveOfficialAccount(OfficialAccount... articles);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateOfficialAccount(OfficialAccount... articles);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateOfficialAccount(List<OfficialAccount> articles);

    @Query("delete from OfficialAccount where id = :id")
    void deleteOfficialAccount(int id);

    @Delete
    void deleteOfficialAccount(OfficialAccount book);

    @Query("select * from OfficialAccount order by id desc")
    List<OfficialAccount> getAllOfficialAccount();

    @Query("select * from OfficialAccount where id = :id ")
    OfficialAccount getOfficialAccount(int id);

    @Query("select * from OfficialAccount order by id desc limit :size ")
    List<OfficialAccount> getSizeOfficialAccount(int size);

    @Query("select * from OfficialAccount where id < :MaxId order by id desc limit :size ")
    List<OfficialAccount> getMaxOfficialAccount(int MaxId, int size);

    @Query("select * from OfficialAccount where id > :MinId order by id asc limit :size ")
    List<OfficialAccount> getMinOfficialAccount(int MinId, int size);

}
