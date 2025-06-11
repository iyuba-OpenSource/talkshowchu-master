package com.iyuba.talkshow.data.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.iyuba.module.toolbox.DBUtil;
import com.iyuba.talkshow.BuildConfig;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.University;
import com.iyuba.talkshow.injection.ApplicationContext;
import com.iyuba.talkshow.util.FileUtils;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import javax.inject.Inject;


public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "kouyu_show.db";
    private static final int DATABASE_VERSION = 18;
    private final Context mContext;

    @Inject
    public DbOpenHelper(@ApplicationContext Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(Db.VoaTable.CREATE);
            db.execSQL(Db.VoaTextTable.CREATE);
            db.execSQL(Db.RecordTable.CREATE);
            db.execSQL(Db.CollectTable.CREATE);
            db.execSQL(Db.UniversityTable.CREATE);
            db.execSQL(Db.ThumbTable.CREATE);
            db.execSQL(Db.DownloadTable.CREATE);
            db.execSQL(Db.SeriesTable.CREATE);
            db.execSQL(Db.VoaSoundTable.CREATE);
            db.execSQL(Db.ArticleRecordTable.CREATE);

            //预存大学的数据
            insertUniversities(db);
            //预存书籍数据
            executeBookSql(db);
            //预存课程和课程文本数据
            executeVoasSql(db);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void insertUniversities(SQLiteDatabase db) {
        JsonParser jsonParser = new JsonParser();
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
        JsonArray jsonArray = (JsonArray) jsonParser
                .parse(FileUtils.getReaderFromRaw(mContext, R.raw.universities));
        TypeAdapter<University> typeAdapter = University.typeAdapter(gson);

        for (JsonElement jsonElement : jsonArray) {
            University university = typeAdapter.fromJsonTree(jsonElement);
            db.insert(Db.UniversityTable.TABLE_NAME, null,
                    Db.UniversityTable.toContentValues(university));
        }
    }

    //预存书籍数据
    public void executeBookSql(SQLiteDatabase db) {
        FileUtils.executeAssetsSQL(mContext, db, "preData_junior_book.sql");
    }

    //预存课程+课程文本数据
    public void executeVoasSql(SQLiteDatabase db) {
        FileUtils.executeAssetsSQL(mContext, db, "preData_junior_lessonAndText.sql");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                onCreate(db);
                db.execSQL("ALTER TABLE  '" + Db.RecordTable.TABLE_NAME + "'  ADD 'score' TEXT ");
            case 2:
                db.execSQL("ALTER TABLE  '" + Db.VoaTable.TABLE_NAME + "'  ADD 'series' INTEGER ");
                db.execSQL(Db.SeriesTable.CREATE);
            case 3:
                db.execSQL("ALTER TABLE  '" + Db.RecordTable.TABLE_NAME + "'  ADD 'audio' TEXT ");
            case 4:
                executeVoasSql(db);
            case 5:
                db.execSQL(Db.VoaSoundTable.CREATE);
            case 6:
                db.execSQL(Db.ArticleRecordTable.CREATE);
            case 7:
                if (!DBUtil.isColumnExist(db, Db.SeriesTable.TABLE_NAME, Db.SeriesTable.COLUMN_SERIES_VIDEO)) {
                    db.execSQL("ALTER TABLE  '" + Db.SeriesTable.TABLE_NAME + "'  ADD 'series_video' INTEGER ");
                }
            case 8:
                if (!DBUtil.isColumnExist(db, Db.VoaSoundTable.TABLE_NAME, Db.VoaSoundTable.RVC)) {
                    db.execSQL("ALTER TABLE  '" + Db.VoaSoundTable.TABLE_NAME + "'  ADD 'rvc' TEXT ");
                }
                if (!DBUtil.isColumnExist(db, Db.DownloadTable.TABLE_NAME, Db.DownloadTable.COLUMN_UID)) {
                    db.execSQL("ALTER TABLE  '" + Db.DownloadTable.TABLE_NAME + "'  ADD 'uid' TEXT ");
                }
            case 9:
                if (!DBUtil.isColumnExist(db, Db.VoaTable.TABLE_NAME, Db.VoaTable.COLUMN_TIME_TOTAL)) {
                    db.execSQL("ALTER TABLE  '" + Db.VoaTable.TABLE_NAME + "'  ADD 'totalTime' INTEGER ");
                }
                if (!DBUtil.isColumnExist(db, Db.VoaTable.TABLE_NAME, Db.VoaTable.COLUMN_PERCENT_ID)) {
                    db.execSQL("ALTER TABLE  '" + Db.VoaTable.TABLE_NAME + "'  ADD 'percentId' INTEGER ");
                }
                if (!DBUtil.isColumnExist(db, Db.VoaTable.TABLE_NAME, Db.VoaTable.COLUMN_OUTLINE_ID)) {
                    db.execSQL("ALTER TABLE  '" + Db.VoaTable.TABLE_NAME + "'  ADD 'outlineId' INTEGER ");
                }
                if (!DBUtil.isColumnExist(db, Db.VoaTable.TABLE_NAME, Db.VoaTable.COLUMN_PACKAGE_ID)) {
                    db.execSQL("ALTER TABLE  '" + Db.VoaTable.TABLE_NAME + "'  ADD 'packageId' INTEGER ");
                }
                if (!DBUtil.isColumnExist(db, Db.VoaTable.TABLE_NAME, Db.VoaTable.COLUMN_CATEGORY_ID)) {
                    db.execSQL("ALTER TABLE  '" + Db.VoaTable.TABLE_NAME + "'  ADD 'categoryId' INTEGER ");
                }
                if (!DBUtil.isColumnExist(db, Db.VoaTable.TABLE_NAME, Db.VoaTable.COLUMN_CLASS_ID)) {
                    db.execSQL("ALTER TABLE  '" + Db.VoaTable.TABLE_NAME + "'  ADD 'classId' TEXT ");
                }
            case 10:
                if (!DBUtil.isColumnExist(db, Db.SeriesTable.TABLE_NAME, Db.SeriesTable.COLUMN_SERIES_MICRO)) {
                    db.execSQL("ALTER TABLE  '" + Db.SeriesTable.TABLE_NAME + "'  ADD 'series_micro' INTEGER ");
                }
                if (!DBUtil.isColumnExist(db, Db.SeriesTable.TABLE_NAME, Db.SeriesTable.COLUMN_SERIES_COUNT)) {
                    db.execSQL("ALTER TABLE  '" + Db.SeriesTable.TABLE_NAME + "'  ADD 'series_count' INTEGER ");
                }
            case 11:
                if (!DBUtil.isColumnExist(db, Db.VoaTable.TABLE_NAME, Db.VoaTable.COLUMN_CLICK_READ)) {
                    db.execSQL("ALTER TABLE  '" + Db.VoaTable.TABLE_NAME + "'  ADD 'clickRead' INTEGER ");
                }
                if (!DBUtil.isColumnExist(db, Db.VoaTextTable.TABLE_NAME, Db.VoaTextTable.COLUMN_START_X)) {
                    db.execSQL("ALTER TABLE  '" + Db.VoaTextTable.TABLE_NAME + "'  ADD 'startX' INTEGER ");
                }
                if (!DBUtil.isColumnExist(db, Db.VoaTextTable.TABLE_NAME, Db.VoaTextTable.COLUMN_START_Y)) {
                    db.execSQL("ALTER TABLE  '" + Db.VoaTextTable.TABLE_NAME + "'  ADD 'startY' INTEGER ");
                }
                if (!DBUtil.isColumnExist(db, Db.VoaTextTable.TABLE_NAME, Db.VoaTextTable.COLUMN_END_X)) {
                    db.execSQL("ALTER TABLE  '" + Db.VoaTextTable.TABLE_NAME + "'  ADD 'endX' INTEGER ");
                }
                if (!DBUtil.isColumnExist(db, Db.VoaTextTable.TABLE_NAME, Db.VoaTextTable.COLUMN_END_Y)) {
                    db.execSQL("ALTER TABLE  '" + Db.VoaTextTable.TABLE_NAME + "'  ADD 'endY' INTEGER ");
                }
            case 12:
                if (!DBUtil.isColumnExist(db, Db.VoaSoundTable.TABLE_NAME, Db.VoaSoundTable.COLUMN_WORDS)) {
                    db.execSQL("ALTER TABLE  '" + Db.VoaSoundTable.TABLE_NAME + "'  ADD 'words' TEXT ");
                }
            case 13:
                if (!DBUtil.isColumnExist(db,Db.VoaTable.TABLE_NAME,Db.VoaTable.COLUMN_VIDEO)){
                    db.execSQL("ALTER TABLE '"+ Db.VoaTable.TABLE_NAME +"' ADD 'video' TEXT ");
                }
            case 14:
                //新增新概念的数据预存
                //修改时间：2023-04-10
                executeVoasSql(db);
            case 15:
                //修复新概念的课程数据顺序显示错误问题
                //修改时间：2023-04-11
                executeVoasSql(db);
            case 16:
                //增加部分人教版的新增内容
                //修改时间：2023-09-19
                executeVoasSql(db);
            case 17:
                //更新全部的预存书籍、课程和课程文本数据
                //修改时间：2025-2-26
                executeBookSql(db);
                executeVoasSql(db);
        }
    }

}