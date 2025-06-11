package com.iyuba.talkshow.lil.help_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.iyuba.talkshow.lil.help_fix.model.local.entity.AgreeEntity;

/**
 * @title:
 * @date: 2023/5/25 18:05
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface AgreeEntityDao {

    //保存单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveData(AgreeEntity eval);

    //获取单个数据
    @Query("select * from AgreeEntity where userId=:userId and agreeUserId=:agreeUserId and types=:types and voaId=:voaId and evalSentenceId=:sentenceId")
    AgreeEntity getData(String userId, String agreeUserId, String types, String voaId, String sentenceId);
}
