package com.iyuba.talkshow.lil.help_fix.model.local.dao;//package com.iyuba.talkshow.lil.fix.model.local.dao;
//
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.WordBreakEntity;
//
//import java.util.List;
//
///**
// * @title:
// * @date: 2023/5/26 11:38
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//@Dao
//public interface WordBreakEntityDao {
//
//    //保存数据
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    List<Long> saveData(List<WordBreakEntity> list);
//
//    //保存单个数据
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long saveSingleData(WordBreakEntity entity);
//
//    //获取单个数据结果
//
//    //获取本id下的数据结果
//    @Query("select * from WordBreakEntity where types=:types and bookId=:bookId and id=:id and userId=:userId order by position asc")
//    List<WordBreakEntity> searchAllDataById(String types,String bookId,String id,long userId);
//
//    //获取本id下正确的数据结果
//    @Query("select * from WordBreakEntity where types=:types and bookId=:bookId and id=:id and userId=:userId and selectAnswer=rightAnswer order by position asc")
//    List<WordBreakEntity> searchRightDataById(String types,String bookId,String id,long userId);
//
//    //获取本书籍下的数据结果
//}
