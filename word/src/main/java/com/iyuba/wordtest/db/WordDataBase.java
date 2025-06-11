package com.iyuba.wordtest.db;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.iyuba.wordtest.entity.BookLevels;
import com.iyuba.wordtest.entity.CategorySeries;
import com.iyuba.wordtest.entity.CetRootWord;
import com.iyuba.wordtest.entity.NewBookLevels;
import com.iyuba.wordtest.entity.TalkShowListen;
import com.iyuba.wordtest.entity.TalkShowTests;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.entity.TalkShowWrite;
import com.iyuba.wordtest.entity.TalkshowTexts;
import com.iyuba.wordtest.manager.WordConfigManager;
import com.iyuba.wordtest.utils.FileUtils;

@SuppressLint("StaticFieldLeak")
@Database(entities = { CetRootWord.class , TalkShowWords.class, TalkShowTests.class, TalkshowTexts.class, BookLevels.class, NewBookLevels.class, CategorySeries.class, OfficialAccount.class, TalkShowListen.class, TalkShowWrite.class}, version = 7,exportSchema = false)
public abstract class WordDataBase extends RoomDatabase {

    private static final String DB_NAME = "words.db";
    private static WordDataBase instance;
    private static Context mContext ;

    public static WordDataBase getInstance(Context context) {
        mContext = context ;
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), WordDataBase.class, DB_NAME)
                    .addCallback(sOnOpenCallback)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4,MIGRATION_4_5,MIGRATION_5_6,MIGRATION_6_7)
                    .allowMainThreadQueries() // 允许主线程
                    .build();
        }
        return instance;
    }

    private static final RoomDatabase.Callback sOnOpenCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);

                    //初始化插入数据
                    initializeData(db);
                }
    };

    private static void initializeData(SupportSQLiteDatabase db) {
        FileUtils.executeAssetsSQL(mContext,db,"preData_junior_wordAndText.sql");
    }
//    public static boolean loadDbData() {
//        return FileUtils.executeAssetsSQL(mContext, instance.getOpenHelper().getWritableDatabase(),"preData_junior_wordAndText.sql");
//    }

    public abstract TalkShowTextDao getTalkShowTextDao();
    public abstract TalkShowTestsDao getTalkShowTestsDao();
    public abstract NewBookLevelDao getNewBookLevelDao();
    public abstract TalkShowWordsDao getTalkShowWordsDao();
    public abstract BookLevelDao getBookLevelDao();
    public abstract CetRootWordDAO getCetRootWordDao();
    public abstract CategorySeriesDao getCategorySeriesDao();
    public abstract OfficialAccountDao getOfficialAccountDao();

    //单词听写
    public abstract TalkShowListenDao getTalkShowListenDao();
    //单词手写
    public abstract TalkShowWriteDao getTalkShowWriteDao();

    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `CategorySeries` (`Category` INTEGER NOT NULL, `SeriesName` TEXT, `lessonName` TEXT, `SourceType` TEXT, `lessonType` TEXT, `isVideo` TEXT, `uid` INTEGER NOT NULL, PRIMARY KEY(`Category`, `uid`))");
        }
    };
    static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `OfficialAccount` (`id` INTEGER NOT NULL, `newsfrom` TEXT, `createTime` TEXT, `image_url` TEXT, `title` TEXT, `url` TEXT, `count` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        }
    };

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `TalkShowTests` (`word` TEXT NOT NULL, `uid` TEXT, `version` INTEGER NOT NULL, `position` INTEGER NOT NULL, `updateTime` TEXT, `voa_id` INTEGER NOT NULL, `book_id` INTEGER NOT NULL, `unit_id` INTEGER NOT NULL, `idindex` INTEGER NOT NULL, `pic_url` TEXT, `audio` TEXT, `examples` TEXT, `answer` TEXT, `pron` TEXT, `def` TEXT, `flag` INTEGER NOT NULL, `Sentence` TEXT, `Sentence_cn` TEXT, `Sentence_audio` TEXT, `videoUrl` TEXT, `wrong` INTEGER NOT NULL, PRIMARY KEY(`unit_id`, `book_id`, `position`))");
            database.execSQL("CREATE TABLE IF NOT EXISTS `NewBookLevels` (`bookId` INTEGER NOT NULL, `level` INTEGER NOT NULL, `version` INTEGER NOT NULL, `download` INTEGER NOT NULL, `uid` TEXT NOT NULL, PRIMARY KEY(`bookId`, `uid`))");
            WordConfigManager.Instance(mContext).putInt(WordConfigManager.WORD_DB_NEW_LOADED, 1);
        }
    };

    //增加单词听写
    static final Migration MIGRATION_4_5 = new Migration(4,5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `TalkShowListen`(`book_id` INTEGER NOT NULL,`unit_id` INTEGER NOT NULL,`position` INTEGER NOT NULL,`uid` TEXT NOT NULL,`word` TEXT,`porn` TEXT,`def` TEXT,`audio` TEXT,`spell` TEXT,`status` INTEGER NOT NULL,`error_count` INTEGER NOT NULL,`update_time` TEXT,PRIMARY KEY(`book_id`,`unit_id`,`position`,`uid`))");
        }
    };

    //更新部分单词的数据(399、400、401、402)
    static final Migration MIGRATION_5_6 = new Migration(5,6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //删除对应的数据
            String sql = "DELETE FROM TalkShowWords WHERE book_id=";
            database.execSQL(sql+399);
            database.execSQL(sql+400);
            database.execSQL(sql+401);
            database.execSQL(sql+402);
            //将固定的数据操作
            FileUtils.executeAssetsSQL(mContext, database,"migration/juniorWord_20240420.sql");
        }
    };

    //更新全部的单词数据
    static final Migration MIGRATION_6_7 = new Migration(6,7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //更新单词数据
            initializeData(database);
            //预先增加手写练习的表
            database.execSQL("CREATE TABLE IF NOT EXISTS `TalkShowWrite`(`book_id` INTEGER NOT NULL,`unit_id` INTEGER NOT NULL,`position` INTEGER NOT NULL,`uid` TEXT NOT NULL,`word` TEXT,`porn` TEXT,`def` TEXT,`audio` TEXT,`spell` TEXT,`status` INTEGER NOT NULL,`error_count` INTEGER NOT NULL,`update_time` TEXT,PRIMARY KEY(`book_id`,`unit_id`,`position`,`uid`))");
        }
    };
}