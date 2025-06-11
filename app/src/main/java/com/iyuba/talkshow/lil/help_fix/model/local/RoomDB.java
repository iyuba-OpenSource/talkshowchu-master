package com.iyuba.talkshow.lil.help_fix.model.local;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.model.local.dao.AgreeEntityDao;
import com.iyuba.talkshow.lil.help_fix.model.local.dao.BookEntityJuniorDao;
import com.iyuba.talkshow.lil.help_fix.model.local.dao.ChapterCollectEntityDao;
import com.iyuba.talkshow.lil.help_fix.model.local.dao.ChapterDetailNovelDao;
import com.iyuba.talkshow.lil.help_fix.model.local.dao.ChapterNovelDao;
import com.iyuba.talkshow.lil.help_fix.model.local.dao.EvalEntityChapterDao;
import com.iyuba.talkshow.lil.help_fix.model.local.dao.JuniorDubbingHelpDao;
import com.iyuba.talkshow.lil.help_fix.model.local.dao.SettingEntityDao;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.AgreeEntity;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.BookEntity_junior;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.BookEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterCollectEntity;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterDetailEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterEntity_novel;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.EvalEntity_chapter;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.JuniorDubbingHelpEntity;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.SettingEntity;
import com.iyuba.talkshow.lil.help_mvp.util.DateUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxTimer;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @title: 数据库操作
 * @date: 2023/5/19 11:25
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description: 在这个app中，只使用了小说部分的内容，中小学内容没有使用。暂时屏蔽，需要的时候在放开
 */
//@Database(entities = {BookEntity_junior.class,ChapterEntity_junior.class, ChapterEntity_novel.class,ChapterDetailEntity_junior.class, ChapterDetailEntity_novel.class,EvalEntity_chapter.class,SettingEntity.class, ChapterCollectEntity.class, AgreeEntity.class, EvalEntity_word.class, WordBreakEntity.class, WordBreakPassEntity.class, WordEntity_junior.class},exportSchema = false,version = 1)
@Database(entities = {BookEntity_junior.class, BookEntity_novel.class,ChapterEntity_novel.class, ChapterDetailEntity_novel.class, EvalEntity_chapter.class, SettingEntity.class, ChapterCollectEntity.class, AgreeEntity.class, JuniorDubbingHelpEntity.class},exportSchema = false,version = 2)
public abstract class RoomDB extends RoomDatabase {
    private static final String TAG = "RoomDB";

    private static RoomDB instance;
    //数据加载标志位
    private static final String dbDataLoadTag = "dbDataLoadTag";

    /**
     * 数据库初始化
     * 这里是直接拷贝过来的，可以删除
     */
    public static RoomDB getInstance(){
        if (instance==null){
            synchronized (RoomDB.class){
                if (instance==null){
                    instance = Room.databaseBuilder(ResUtil.getInstance().getApplication(),RoomDB.class,getDBName())
//                            .createFromAsset("database/novelFix_2_20230720.db")//直接使用db数据库的形式，room会将数据库复制到上边的名称中
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .addCallback(callback)
//                            .addMigrations(migration_1_2)
                            .build();
                }
            }
        }
        return instance;
    }

    //数据库名称
    private static String getDBName(){
        //这里设置为包名最后一个+db
        String packageName = ResUtil.getInstance().getApplication().getPackageName();
        int index = packageName.lastIndexOf(".");
        String dbName = packageName.substring(index+1);
        return dbName+".db";
    }

    //回调信息
    private static Callback callback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            //执行数据插入操作
            preData(db);
        }

        @Override
        public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
            super.onDestructiveMigration(db);
        }
    };

    /****************dao操作类***************/
    /*******中小学******/
    //书籍表-中小学书籍操作
//    public abstract BookEntityJuniorDao getBookEntityJuniorDao();
    //章节表-中小学章节操作
//    public abstract ChapterEntityJuniorDao getChapterEntityJuniorDao();
    //章节详情表-中小学章节详情操作
//    public abstract ChapterDetailEntityJuniorDao getChapterDetailEntityJuniorDao();
    //单词表-中小学单词操作
//    public abstract WordJuniorDao getWordJuniorDao();

    /****小说****/
    //书籍表-小说书籍操作
//    public abstract BookEntityNovelDao getBookEntityNovelDao();
    //小说-章节数据
    public abstract ChapterNovelDao getChapterNovelDao();
    //小说-章节详情数据
    public abstract ChapterDetailNovelDao getChapterDetailNovelDao();

    //评测表-章节
    public abstract EvalEntityChapterDao getEvalEntityChapterDao();

    //设置数据表
    public abstract SettingEntityDao getSettingEntityDao();
    //章节收藏表
    public abstract ChapterCollectEntityDao getChapterCollectEntityDao();
    //评测结果点赞表
    public abstract AgreeEntityDao getAgreeEntityEvalDao();
    //单词收藏表
//    public abstract WordCollectDao getWordCollectDao();

    //单词评测
    /*public abstract EvalWordDao getEvalWordDao();
    //单词闯关详情
    public abstract WordBreakEntityDao getWordBreakEntityDao();
    //单词闯关进度
    public abstract WordBreakPassDao getWordBreakPassDao();*/

    //配音或评测内容的结果
    public abstract JuniorDubbingHelpDao getJuniorDubbingHelpDao();

    /**************************迁移数据*************************/
    /*private static Migration migration_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //将数据导入本地数据库中
            RxTimer.timerInIO(dbDataLoadTag, 0, new RxTimer.RxActionListener() {
                @Override
                public void onAction(long number) {
                    String preDataPath = "database/PreData_2_20230710.sql";
                    insertDataByAssetsSql(database,preDataPath);
                }
            });
        }
    };*/

    /**********************辅助功能***************************/
    //判断表是否存在
    private static boolean isTableExist(SupportSQLiteDatabase db, String tabName) {
        boolean isTableExist = false;
        Cursor cursor = null;
        try {
            String sql = "select name from sqlite_master where type='table' and name='" + tabName + "'";
            cursor = db.query(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                isTableExist = true;
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isTableExist;
    }

    //判断列是否存在
    private static boolean isColumnExist(SupportSQLiteDatabase db, String tableName, String columnName) {
        boolean isColumnExist = false;
        Cursor cursor = null;
        try {
            String sql = "select " + columnName + " from " + tableName;
            cursor = db.query(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                isColumnExist = true;
            }
        } catch (Exception e) {
            isColumnExist = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isColumnExist;
    }

    //判断数据是否存在
    private static boolean isDataExist(SupportSQLiteDatabase db, String tableName, String columnName, String searchData) {
        boolean isColumnExist = false;
        Cursor cursor = null;
        try {
            String sql = "select * from " + tableName + " where " + columnName + " = " + searchData;
            cursor = db.query(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                isColumnExist = true;
            }
        } catch (Exception e) {
            isColumnExist = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isColumnExist;
    }

    //判断单词数据是否存在
    private static boolean isWordDataExist(SupportSQLiteDatabase db, String tableName, String columnName1, String searchData1, String columnName2, String searchData2) {
        boolean isColumnExist = false;
        Cursor cursor = null;
        try {
            String sql = "select * from " + tableName + " where " + columnName1 + " like '" + searchData1 + "' and " + columnName2 + " like '" + searchData2 + "'";
            cursor = db.query(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                isColumnExist = true;
            }
        } catch (Exception e) {
            isColumnExist = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isColumnExist;
    }

    //从assets中读取sql数据并且插入到数据库中(目前看来是可以的，如果存在问题请及时解决)
    private static void insertDataByAssetsSql(SupportSQLiteDatabase db, String sqlPath) {
        String startTime = DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMDHMSS);

        try {
            Log.d(TAG, "insertDataByAssetsSql: --start--" + startTime + "---" + sqlPath);
            //获取文件数据流
            InputStream is = ResUtil.getInstance().getApplication().getAssets().open(sqlPath);
            //读取并且插入
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("##")) {
                    //这个是自定义的标识符，不参与数据插入
                    continue;
                }

//                if (sqlPath.equals("database/junior/preData_junior_word.sql")){
//                    Log.d(TAG, "执行操作--"+line);
//                }
                db.execSQL(line);
            }

//            RxTimer.cancelTimer(dbDataLoadTag);
            String endTime = DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMDHMSS);
            Log.d(TAG, "insertDataByAssetsSql: --finish--" + endTime + "---" + sqlPath);
            //刷新数据显示
            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.junior));
            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.novel));
        } catch (Exception e) {
            Log.d(TAG, "Inserting data failed, using network data！！！" + sqlPath);
        }
    }

    //预存数据
    private static void preData(SupportSQLiteDatabase db) {
        //这里有两个数据表需要存储，先判断表是否存在，然后判断表中是否存在数据
        //小说-章节表
        if (isTableExist(db, ChapterEntity_novel.class.getSimpleName())) {
            String delayTag = "novelChapterTag";
            String filePath = "database/novel/preData_novel_chapter.sql";
            if (!isDataExist(db, ChapterEntity_novel.class.getSimpleName(), "voaid", "60116")) {
                RxTimer.getInstance().timerInIO(delayTag, 0, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        RxTimer.getInstance().cancelTimer(delayTag);
                        insertDataByAssetsSql(db, filePath);
                    }
                });
            }
        }
        //小说-章节详情表
        if (isTableExist(db, ChapterDetailEntity_novel.class.getSimpleName())) {
            String delayTag = "novelChapterDetailTag";
            String filePath = "database/novel/preData_novel_chapterDetail.sql";
            if (!isDataExist(db, ChapterDetailEntity_novel.class.getSimpleName(), "EndTiming", "456.48")) {
                RxTimer.getInstance().timerInIO(delayTag, 0, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        RxTimer.getInstance().cancelTimer(delayTag);
                        insertDataByAssetsSql(db, filePath);
                    }
                });
            }
        }
    }
}
