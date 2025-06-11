package com.iyuba.talkshow.lil.help_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterEntity_novel;

import java.util.List;

/**
 * @title: 章节表-小说
 * @date: 2023/5/7 18:33
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface ChapterNovelDao {

    //保存单个章节数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveSingleData(ChapterEntity_novel novel);

    //保存多个章节数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> saveMultiData(List<ChapterEntity_novel> list);

    //获取书籍下的章节数据
    @Query("select * from ChapterEntity_novel where types=:types and level=:level and orderNumber=:bookId order by voaid asc")
    List<ChapterEntity_novel> searchChapterDataByBookId(String types, String level, String bookId);

    //获取本章节的数据
    @Query("select * from ChapterEntity_novel where types=:types and voaid=:voaId")
    ChapterEntity_novel searchSingleChapterDataByBookId(String types,String voaId);

    //删除单个章节数据

    //删除书籍下的章节数据
}
