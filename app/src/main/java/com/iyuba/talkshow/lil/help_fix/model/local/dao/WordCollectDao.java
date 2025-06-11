package com.iyuba.talkshow.lil.help_fix.model.local.dao;//package com.iyuba.talkshow.lil.help_fix.model.local.dao;
//
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import com.iyuba.talkshow.lil.help_fix.model.local.entity.WordCollectEntity;
//
//import java.util.List;
//
///**
// * @title: 单词收藏的操作
// * @date: 2023/7/17 11:32
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//@Dao
//public interface WordCollectDao {
//
//    //保存单个数据
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void saveSingleData(WordCollectEntity entity);
//
//    //保存多个数据
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void saveMultiData(List<WordCollectEntity> list);
//
//    //删除单个数据
//    @Query("delete from WordCollectEntity where userId=:uid and word=:key")
//    void deleteSingleData(int uid,String key);
//
//    //获取当前账号下的单词数据
//    @Query("select * from WordCollectEntity where userId=:uid order by updateTime asc")
//    List<WordCollectEntity> searchWordByUserId(int uid);
//
//    //获取单个单词数据
//    @Query("select * from WordCollectEntity where userId=:uid and word=:key")
//    WordCollectEntity searchSingleWord(int uid,String key);
//}
