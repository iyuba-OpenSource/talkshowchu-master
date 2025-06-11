package com.iyuba.talkshow.lil.help_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.iyuba.talkshow.lil.help_fix.model.local.entity.JuniorDubbingHelpEntity;

@Dao
public interface JuniorDubbingHelpDao {

    //插入单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSingleData(JuniorDubbingHelpEntity entity);

    //获取当前item的数据
    @Query("select * from JuniorDubbingHelpEntity where itemId=:itemId and userId=:userId")
    JuniorDubbingHelpEntity getSingleData(long itemId,int userId);
}
