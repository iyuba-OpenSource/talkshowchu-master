package com.iyuba.talkshow.lil.help_fix.model.local.dao;//package com.iyuba.talkshow.lil.fix.model.local.dao;
//
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.ChapterDetailEntity_junior;
//
//import java.util.List;
//
///**
// * @title:
// * @date: 2023/5/22 17:00
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//@Dao
//public interface ChapterDetailEntityJuniorDao {
//
//    //保存数据
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    List<Long> saveData(List<ChapterDetailEntity_junior> list);
//
//    //获取单个数据
//    @Query("select * from ChapterDetailEntity_junior where voaId=:voaId and paraId=:paraId and idIndex=:idIndex")
//    ChapterDetailEntity_junior getSingleData(String voaId,String paraId,String idIndex);
//
//    //根据章节id获取数据
//    @Query("select * from ChapterDetailEntity_junior where voaId=:voaId order by paraId,idIndex asc")
//    List<ChapterDetailEntity_junior> getDataByVoaId(String voaId);
//}
