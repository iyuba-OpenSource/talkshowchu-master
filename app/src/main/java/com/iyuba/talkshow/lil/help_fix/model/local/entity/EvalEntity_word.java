package com.iyuba.talkshow.lil.help_fix.model.local.entity;//package com.iyuba.talkshow.lil.fix.model.local.entity;
//
//import androidx.annotation.NonNull;
//import androidx.room.Entity;
//import androidx.room.Ignore;
//
///**
// * @title: 评测数据-单词
// * @date: 2023/6/5 15:37
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description: 新概念单词评测和中小学单词评测都会使用这个表，数据不用管，sqlite3数据库可以存储上百万的数据，足够使用了
// */
//@Entity(primaryKeys = {"types","bookId","voaId","position","sentence"})
//public class EvalEntity_word {
//
//    @NonNull
//    public String types;//类型
//    @NonNull
//    public String bookId;//书籍id
//    @NonNull
//    public String voaId;//voaId
//    @NonNull
//    public String position;//单词数据中的位置
//    @NonNull
//    public String sentence;
//    public String totalScore;//这里是double类型数据，需要*20获取int类型数据
//    public String url;
//    public String words;
//
//    //本地数据
//    public String id;//青少版为unitId,其他的为voaId(用于保存哪里来的类型)
//    public String localPath;//本地路径
//
//    public EvalEntity_word() {
//    }
//
//    @Ignore
//    public EvalEntity_word(@NonNull String types, @NonNull String bookId, @NonNull String voaId, @NonNull String position, @NonNull String sentence, String totalScore, String url, String words, String id, String localPath) {
//        this.types = types;
//        this.bookId = bookId;
//        this.voaId = voaId;
//        this.position = position;
//        this.sentence = sentence;
//        this.totalScore = totalScore;
//        this.url = url;
//        this.words = words;
//        this.id = id;
//        this.localPath = localPath;
//    }
//}
