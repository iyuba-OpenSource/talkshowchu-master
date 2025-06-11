package com.iyuba.talkshow.lil.help_fix.model.local.entity;//package com.iyuba.talkshow.lil.fix.model.local.entity;
//
//import androidx.annotation.NonNull;
//import androidx.room.Entity;
//import androidx.room.Ignore;
//
///**
// * @title: 单词闯关进度表（已经通过的不做更新）
// * @date: 2023/5/26 13:18
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//@Entity(primaryKeys = {"types","bookId","userId"})
//public class WordBreakPassEntity {
//
//    @NonNull
//    public String types;//类型
//    @NonNull
//    public String bookId;//书籍id
//    public String id;//unitId或者voaId
//    @NonNull
//    public long userId;//用户id
//
//    public WordBreakPassEntity() {
//    }
//
//    @Ignore
//    public WordBreakPassEntity(String types, String bookId, String id, long userId) {
//        this.types = types;
//        this.bookId = bookId;
//        this.id = id;
//        this.userId = userId;
//    }
//}
