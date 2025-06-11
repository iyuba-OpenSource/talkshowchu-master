package com.iyuba.talkshow.lil.help_fix.model.local.entity;//package com.iyuba.talkshow.lil.fix.model.local.entity;
//
//import androidx.annotation.NonNull;
//import androidx.room.Entity;
//import androidx.room.Ignore;
//
///**
// * @title: 章节详情表-中小学
// * @date: 2023/5/22 16:46
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//@Entity(primaryKeys = {"voaId","paraId","idIndex"})
//public class ChapterDetailEntity_junior {
//
//    public String imgPath;
//    @NonNull
//    public double endTiming;
//    @NonNull
//    public int paraId;
//    @NonNull
//    public int idIndex;
//    public String sentence_cn;
//    public String imgWords;
//    public String start_x;
//    public String end_y;
//    @NonNull
//    public double timing;
//    public String end_x;
//    public String sentence;
//    public String start_y;
//
//    //本地数据
//    public String types;
//    public String bookId;
//    @NonNull
//    public String voaId;
//
//    public ChapterDetailEntity_junior() {
//    }
//
//    @Ignore
//    public ChapterDetailEntity_junior(String imgPath, double endTiming, int paraId, int idIndex, String sentence_cn, String imgWords, String start_x, String end_y, double timing, String end_x, String sentence, String start_y, String types, String bookId, String voaId) {
//        this.imgPath = imgPath;
//        this.endTiming = endTiming;
//        this.paraId = paraId;
//        this.idIndex = idIndex;
//        this.sentence_cn = sentence_cn;
//        this.imgWords = imgWords;
//        this.start_x = start_x;
//        this.end_y = end_y;
//        this.timing = timing;
//        this.end_x = end_x;
//        this.sentence = sentence;
//        this.start_y = start_y;
//        this.types = types;
//        this.bookId = bookId;
//        this.voaId = voaId;
//    }
//}
