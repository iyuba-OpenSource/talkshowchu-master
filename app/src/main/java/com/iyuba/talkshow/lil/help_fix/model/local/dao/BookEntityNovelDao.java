package com.iyuba.talkshow.lil.help_fix.model.local.dao;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.iyuba.talkshow.lil.help_fix.model.local.entity.BookEntity_novel;

import java.util.List;

/**
 * @title:
 * @date: 2023/7/4 09:48
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface BookEntityNovelDao {

    //保存数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveData(BookEntity_novel entity);

    //保存多个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMultiData(List<BookEntity_novel> entities);

    //获取单个数据
    @Query("select * from BookEntity_novel where types=:types and level=:level and orderNumber=:orderNumber")
    BookEntity_novel getSingleData(String types,String level,String orderNumber);

    //获取多个数据
    @Query("select * from BookEntity_novel where types=:types and level=:level order by orderNumber asc")
    List<BookEntity_novel> getMultiData(String types,String level);
}
