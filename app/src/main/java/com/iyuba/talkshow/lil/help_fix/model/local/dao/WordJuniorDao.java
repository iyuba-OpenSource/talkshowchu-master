package com.iyuba.talkshow.lil.help_fix.model.local.dao;//package com.iyuba.talkshow.lil.fix.model.local.dao;
//
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordProgressBean;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.WordEntity_junior;
//
//import java.util.List;
//
///**
// * @title:
// * @date: 2023/5/11 16:54
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//@Dao
//public interface WordJuniorDao {
//
//    //保存数据
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    List<Long> saveData(List<WordEntity_junior> list);
//
//    //保存单个数据
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long saveSingleData(WordEntity_junior concept);
//
//    //查询本单元下的单词数据
//    @Query("select * from WordEntity_junior where book_id=:bookId and unit_id=:unitId order by position asc")
//    List<WordEntity_junior> searchWordByUnitId(String bookId,String unitId);
//
//    //查询本书籍下的根据unitId分组的单词数据
//    @Query("select book_id as bookId,unit_id as id,voaId,unit_id as lessonName,count(*) as size from WordEntity_junior where book_id=:bookId group by unit_id order by unit_id asc")
//    List<WordProgressBean> searchWordByBookIdGroup(String bookId);
//}
