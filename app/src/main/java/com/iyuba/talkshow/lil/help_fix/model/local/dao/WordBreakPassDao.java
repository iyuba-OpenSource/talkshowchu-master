package com.iyuba.talkshow.lil.help_fix.model.local.dao;//package com.iyuba.talkshow.lil.fix.model.local.dao;
//
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.WordBreakPassEntity;
//
///**
// * @title:
// * @date: 2023/5/26 13:27
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//@Dao
//public interface WordBreakPassDao {
//
//    //保存数据
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long saveData(WordBreakPassEntity entity);
//
//    //获取数据
//    @Query("select id from WordBreakPassEntity where types=:types and bookId=:bookId and userId=:userId")
//    String searchPassId(String types,String bookId,long userId);
//}
