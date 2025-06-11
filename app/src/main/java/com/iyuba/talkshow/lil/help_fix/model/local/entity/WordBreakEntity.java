package com.iyuba.talkshow.lil.help_fix.model.local.entity;//package com.iyuba.talkshow.lil.fix.model.local.entity;
//
//import androidx.annotation.NonNull;
//import androidx.room.Entity;
//import androidx.room.Ignore;
//
///**
// * @title: 单词闯关表
// * @date: 2023/5/26 11:30
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//@Entity(primaryKeys = {"types","id","position","showWord"})
//public class WordBreakEntity {
//
//    @NonNull
//    public String types;//类型
//    public String bookId;//书籍id
//    public String voaId;//章节id
//    @NonNull
//    public String id;//unitId或者章节id
//
//    @NonNull
//    public int position;//第几个位置的单词
//    @NonNull
//    public String showWord;//显示的单词
//    public String selectAnswer;//选择的答案
//    public String rightAnswer;//正确答案
//
//    public long userId;//用户的id
//
//    public WordBreakEntity() {
//    }
//
//    @Ignore
//    public WordBreakEntity(String types, String bookId, String voaId, String id, int position, String showWord, String selectAnswer, String rightAnswer, long userId) {
//        this.types = types;
//        this.bookId = bookId;
//        this.voaId = voaId;
//        this.id = id;
//        this.position = position;
//        this.showWord = showWord;
//        this.selectAnswer = selectAnswer;
//        this.rightAnswer = rightAnswer;
//        this.userId = userId;
//    }
//}
