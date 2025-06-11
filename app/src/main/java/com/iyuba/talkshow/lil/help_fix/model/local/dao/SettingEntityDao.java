package com.iyuba.talkshow.lil.help_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.iyuba.talkshow.lil.help_fix.model.local.entity.SettingEntity;

import java.util.List;


/**
 * @title:
 * @date: 2023/5/23 19:00
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface SettingEntityDao {

    //插入数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveData(SettingEntity entity);

    //获取本用户的单个类型数据
    @Query("select * from SettingEntity where userId=:userId and type=:type")
    SettingEntity getDataByUser(String userId,String type);

    //获取本用户的所有类型数据
    @Query("select * from SettingEntity where userId=:userId")
    List<SettingEntity> getAllDataByUser(String userId);
}
