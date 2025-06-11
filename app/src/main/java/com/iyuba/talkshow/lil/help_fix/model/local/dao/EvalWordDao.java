package com.iyuba.talkshow.lil.help_fix.model.local.dao;//package com.iyuba.talkshow.lil.fix.model.local.dao;
//
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.EvalEntity_word;
//
///**
// * @title: 单词评测的操作类
// * @date: 2023/6/5 15:41
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//@Dao
//public interface EvalWordDao {
//
//    //保存数据
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void saveData(EvalEntity_word entity);
//
//    //获取当前单词或句子的数据
//    @Query("select * from EvalEntity_word where types=:types and bookId=:bookId and voaId=:voaId and position=:position and sentence=:sentence")
//    EvalEntity_word searchSingleData(String types, String bookId, String voaId, String position, String sentence);
//}
