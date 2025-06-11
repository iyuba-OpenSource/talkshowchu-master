package com.iyuba.talkshow.lil.help_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.iyuba.talkshow.lil.help_fix.model.local.entity.BookEntity_junior;

import java.util.List;

/**
 * @title: 数据表-中小学内容
 * @date: 2023/5/22 11:18
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface BookEntityJuniorDao {

    //保存数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> saveData(List<BookEntity_junior> list);

    //根据类型id获取数据
    @Query("select * from BookEntity_junior where typeId=:typeId order by bookId asc")
    List<BookEntity_junior> getDataByTypeId(String typeId);

    //根据书籍id获取数据
    @Query("select * from BookEntity_junior where bookId=:bookId")
    BookEntity_junior getDataByBookId(String bookId);
}
