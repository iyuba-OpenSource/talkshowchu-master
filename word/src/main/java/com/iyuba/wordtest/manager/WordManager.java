package com.iyuba.wordtest.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.BookLevels;
import com.iyuba.wordtest.entity.NewBookLevels;
import com.iyuba.wordtest.entity.TalkShowTests;
import com.iyuba.wordtest.entity.TalkShowWords;

import java.util.ArrayList;
import java.util.List;

public class WordManager {
    public static int WordDataVersion = 2;
    private static WordManager instance;
    public  String username ;
    public  String userid ;
    public  int appid ;
    public  String type ;
    public  int vip ;
    public  String appNameEn;


    public static WordManager getInstance(){
        if (instance == null){
            instance = new WordManager();
        }
        return instance ;
    }

    public void init(String name ,String id ,int appId , String appType ,int vipStatus ,String appNameEn){
        this.username = name ;
        this.userid = id ;
        this.appid = appId ;
        this.type = appType ;
        this.vip =  vipStatus ;
        this.appNameEn =  appNameEn ;
    }

    public void migrateData(Context context) {
        WordDataBase instance = WordDataBase.getInstance(context);
        Log.e("WordManager", "Migration userid " + userid);
        if ((instance != null) && !TextUtils.isEmpty(userid) && !"0".equals(userid)) {
            WordConfigManager.Instance(context).putInt(WordConfigManager.WORD_DB_NEW_LOADED + userid, 1);
            List<BookLevels> bookLevels = instance.getBookLevelDao().getAllLevel();
            if ((bookLevels == null) || bookLevels.size() < 1) {
                Log.e("WordManager", "Migration bookLevels is null, no need.");
            } else {
                Log.e("WordManager", "Migration bookLevels " + bookLevels.size());
                for (BookLevels levels: bookLevels) {
                    if ((levels != null) && (levels.bookId > 0)) {
                        Log.e("WordManager", "Migration bookId " + levels.bookId);
                        Log.e("WordManager", "Migration level " + levels.level);
                        instance.getNewBookLevelDao().updateBookLevel(level2New(levels));
                    }
                }
            }

            List<TalkShowWords> wordsList = instance.getTalkShowWordsDao().getAllTestWords();
            if ((wordsList == null) || wordsList.size() < 1) {
                Log.e("WordManager", "Migration wordsList is null, no need.");
            } else {
                Log.e("WordManager", "Migration wordsList " + wordsList.size());
                List<TalkShowTests> resultList = new ArrayList<>();
                for (TalkShowWords words : wordsList) {
                    if ((words != null) && !TextUtils.isEmpty(words.answer)) {
                        TalkShowTests tests = WordManager.getInstance().Words2Tests(words);
                        resultList.add(tests);
                    }
                }
                instance.getTalkShowTestsDao().insertWord(resultList);
                Log.e("WordManager", "Migration resultList " + resultList.size());
            }
            WordConfigManager.Instance(context).putInt(WordConfigManager.WORD_DB_NEW_LOADED + userid, 2);
        } else {
            Log.e("WordManager", "Migration why uid is null? ");
        }
    }

    public NewBookLevels level2New(BookLevels levels) {
        if (levels == null) {
            return null;
        }
        NewBookLevels newLevels = new NewBookLevels();
        newLevels.uid = userid;
        newLevels.bookId = levels.bookId;
        newLevels.level = levels.level;
        newLevels.version = levels.version;
        newLevels.download = levels.download;
        return newLevels;
    }

    public NewBookLevels level2New(BookLevels levels, String userid) {
        if (levels == null) {
            return null;
        }
        NewBookLevels newLevels = new NewBookLevels();
        newLevels.uid = userid;
        newLevels.bookId = levels.bookId;
        newLevels.level = levels.level;
        newLevels.version = levels.version;
        newLevels.download = levels.download;
        return newLevels;
    }

    public TalkShowTests Words2Tests(TalkShowWords words) {
        if (words == null) {
            return null;
        }
        TalkShowTests tests = new TalkShowTests();
        tests.uid = userid;
        tests.word = words.word;
        tests.version = words.version;
        tests.position = words.position;
        tests.updateTime = words.updateTime;
        tests.voa_id = words.voa_id;
        tests.book_id = words.book_id;
        tests.unit_id = words.unit_id;
        tests.idindex = words.idindex;
        tests.pic_url = words.pic_url;
        tests.audio = words.audio;
        tests.examples = words.examples;
        tests.answer = words.answer;
        tests.pron = words.pron;
        tests.def = words.def;
        tests.flag = words.flag;
        tests.Sentence = words.Sentence;
        tests.Sentence_cn = words.Sentence_cn;
        tests.Sentence_audio = words.Sentence_audio;
        tests.videoUrl = words.videoUrl;
        tests.wrong = words.wrong;
        return tests;
    }

    public TalkShowTests Words2Tests(TalkShowWords words, String userid) {
        if (words == null) {
            return null;
        }
        TalkShowTests tests = new TalkShowTests();
        tests.uid = userid;
        tests.word = words.word;
        tests.version = words.version;
        tests.position = words.position;
        tests.updateTime = words.updateTime;
        tests.voa_id = words.voa_id;
        tests.book_id = words.book_id;
        tests.unit_id = words.unit_id;
        tests.idindex = words.idindex;
        tests.pic_url = words.pic_url;
        tests.audio = words.audio;
        tests.examples = words.examples;
        tests.answer = words.answer;
        tests.pron = words.pron;
        tests.def = words.def;
        tests.flag = words.flag;
        tests.Sentence = words.Sentence;
        tests.Sentence_cn = words.Sentence_cn;
        tests.Sentence_audio = words.Sentence_audio;
        tests.videoUrl = words.videoUrl;
        tests.wrong = words.wrong;
        return tests;
    }

}
