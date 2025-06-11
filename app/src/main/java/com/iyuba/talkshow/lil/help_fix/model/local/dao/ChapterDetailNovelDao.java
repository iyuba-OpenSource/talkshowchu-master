package com.iyuba.talkshow.lil.help_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterDetailEntity_novel;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/7 22:41
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface ChapterDetailNovelDao {

    //保存详情数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> saveMultiData(List<ChapterDetailEntity_novel> list);

    //获取课程下的详情数据
    @Query("select * from ChapterDetailEntity_novel where types=:types and voaid=:voaId order by paraid,indexId asc")
    List<ChapterDetailEntity_novel> searchMultiDataByVoaId(String types,String voaId);
}
