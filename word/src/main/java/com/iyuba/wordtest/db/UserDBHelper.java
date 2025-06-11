package com.iyuba.wordtest.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.iyuba.module.toolbox.DBUtil;

public class UserDBHelper extends SQLiteOpenHelper {
    private static final String TAG = UserDBHelper.class.getSimpleName();

    private static final String DB_NAME = "concept_user.db";
    private static final int DB_VERSION = 2;

    private static UserDBHelper sInstance = null;

    public static UserDBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new UserDBHelper(context);
        }
        return sInstance;
    }

    public UserDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        // create table article
        sb.append("create table if not exists word_book ")
                .append("(userId integer,word text,pron text,def text,audio text,")
                .append("primary key(userId,word))");
        db.execSQL(sb.toString());
        sb.setLength(0);

        addVoaColumns(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.e(TAG, "upgrade database from version " + oldVersion + " to version " + newVersion);
        switch (oldVersion) {
            case 1:
                addVoaColumns(db);
        }
    }

    private void addVoaColumns(SQLiteDatabase db) {
        if (!DBUtil.isColumnExist(db, "word_book", "voaId")) {
            db.execSQL("ALTER TABLE  'word_book'  ADD 'voaId' integer ");
        }
        if (!DBUtil.isColumnExist(db, "word_book", "bookId")) {
            db.execSQL("ALTER TABLE  'word_book'  ADD 'bookId' integer ");
        }
        if (!DBUtil.isColumnExist(db, "word_book", "unitId")) {
            db.execSQL("ALTER TABLE  'word_book'  ADD 'unitId' integer ");
        }
    }

}
