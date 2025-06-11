package com.iyuba.talkshow.lil.help_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.iyuba.talkshow.lil.help_fix.model.local.entity.EvalEntity_chapter;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/4 18:26
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface EvalEntityChapterDao {

    //保存单个评测结果
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveData(EvalEntity_chapter entity);

    //查询单个评测结果
    @Query("select * from EvalEntity_chapter where types=:types and voaId=:voaId and paraId=:paraId and indexId=:index")
    EvalEntity_chapter searchSingleEvalResult(String types,String voaId,String paraId,String index);

    //查询当前章节下已经评测的数量
    @Query("select count(1) from EvalEntity_chapter where types=:types and voaId=:voaId")
    long searchEvalCountFromVoaId(String types,String voaId);

    //查询当前章节下的已经评测的数据
    @Query("select * from EvalEntity_chapter where types=:types and voaId=:voaId order by paraId,indexId asc")
    List<EvalEntity_chapter> searchEvalFromVoaId(String types,String voaId);
}
