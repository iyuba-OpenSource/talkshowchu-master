package com.iyuba.wordtest.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.iyuba.wordtest.entity.TalkShowWrite;

import java.util.List;

/**
 * @desction: 单词手写操作
 * @date: 2023/2/7 15:21
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
@Dao
public interface TalkShowWriteDao {

    //查询听写的数据
    @Query("select * from TalkShowWrite where book_id=:bookId and unit_id=:unitId and uid=:uid")
    List<TalkShowWrite> getSpellWordData(int bookId, int unitId, String uid);

    //查询正确的听写数据
    @Query("select * from TalkShowWrite where book_id=:bookId and unit_id=:unitId and uid=:uid and status=1")
    List<TalkShowWrite> getRightSpellWordData(int bookId,int unitId,String uid);

    //插入听写的数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSpellWord(TalkShowWrite listen);

    //清空听写的数据
    @Query("delete from TalkShowWrite where book_id=:bookId and unit_id=:unitId and uid=:uid")
    void deleteSpellWord(int bookId,int unitId,String uid);
}
