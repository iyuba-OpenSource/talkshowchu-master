package com.iyuba.talkshow.lil.help_fix.model.local.dao;//package com.iyuba.talkshow.lil.fix.model.local.dao;
//
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.ChapterEntity_junior;
//
//import java.util.List;
//
///**
// * @title: 章节表-中小学操作
// * @date: 2023/5/19 17:01
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//@Dao
//public interface ChapterEntityJuniorDao {
//
//    //保存数据
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    List<Long> saveData(List<ChapterEntity_junior> list);
//
//    //获取当前书籍的数据
//    @Query("select * from ChapterEntity_junior where series=:bookId order by publishTime asc")
//    List<ChapterEntity_junior> getDataByBookId(String bookId);
//
//    //获取当前章节的数据
//    @Query("select * from ChapterEntity_junior where voaId=:voaId")
//    ChapterEntity_junior getDataByVoaId(String voaId);
//}
