package com.iyuba.wordtest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.iyuba.wordtest.entity.WordEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取文章列表数据
 */
public class WordOp {
    public static final String TABLE_NAME = "word_book";

    public static final String USERID = "userId";
    public static final String WORD = "word";
    public static final String DEF = "def";
    public static final String PRON = "pron";
    public static final String AUDIO = "audio";
    public static final String VOA = "voaId";
    public static final String BOOK = "bookId";
    public static final String UNIT = "unitId";

    private SQLiteDatabase database = null;
    private final Context mContext;

    public WordOp(Context context) {
        mContext = context;
        database = new UserDBHelper(context).getWritableDatabase();
    }


    /**
     * 查询用户保存单词
     *
     * @return
     */
    public synchronized List<WordEntity> findWordByUser(int userId) {


        List<WordEntity> wordEntities = new ArrayList<WordEntity>();

        Cursor cursor = database.rawQuery(
                "select " + WORD + "," + DEF + "," + PRON + "," + AUDIO + "," + VOA  + "," + BOOK  + "," + UNIT  + " from " + TABLE_NAME
                        + " where " + USERID + "=" + userId + " order by " + WORD + " asc ", new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            wordEntities.add(fillIn(cursor));
        }
        if (cursor != null) {
            cursor.close();
        }

        return wordEntities;
    }

    public synchronized List<WordEntity> findWordByUser(int userId, int voa, int unitid) {
        List<WordEntity> wordEntities = new ArrayList<WordEntity>();
        Cursor cursor = database.rawQuery(
                "select " + WORD + "," + DEF + "," + PRON + "," + AUDIO + "," + VOA  + "," + BOOK  + "," + UNIT  + " from " + TABLE_NAME
                        + " where " + USERID + "=" + userId + " and " + VOA + "=" + voa + " order by " + WORD + " asc ", new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            WordEntity wordEntity = fillIn(cursor);
            if ((wordEntity != null) && wordEntity.key.contains("" + voa)) {
                wordEntity.key = wordEntity.key.replace("" + voa, "");
                wordEntities.add(wordEntity);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return wordEntities;
    }

    public WordEntity findWordEntity(String word, int userId) {
        WordEntity wordEntity = null;
        boolean flag = false;
        Cursor cursor = database.rawQuery(
                "select " + WORD + "," + DEF + "," + PRON + "," + AUDIO + "," + VOA  + "," + BOOK  + "," + UNIT  + " from " + TABLE_NAME
                + " where " + USERID + "=" + userId, new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            wordEntity = fillIn(cursor);
            if ((wordEntity != null) && !TextUtils.isEmpty(wordEntity.key) && wordEntity.key.equals(word)) {
                flag = true;
                break;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        if (flag) {
            return wordEntity;
        } else {
            return  null;
        }
    }
    public boolean isFavorWord(String word, int voaid, int userId) {
        boolean isExits = false;
        String wordVoa = word + voaid;
        Cursor cursor = database.rawQuery("select " + WORD + "," + BOOK  + " from " + TABLE_NAME
                + " where " + USERID + " = " + userId, new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            if (word.equals(cursor.getString(0)) && (cursor.getInt(1))>0) {
                isExits = true;
                break;
            } else if (wordVoa.equals(cursor.getString(0)) && (cursor.getInt(1))>0) {
                isExits = true;
                break;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return isExits;
    }
    public boolean isExsitsWord(String word, int voaid, int userId) {
        boolean isExits = false;
        Cursor cursor = database.rawQuery("select " + WORD  + " from " + TABLE_NAME
                + " where " + USERID + " = " + userId + " and " + VOA + "=" + voaid, new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            if (word.equals(cursor.getString(0))) {
                isExits = true;
                break;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return isExits;
    }

    /**
     * 插入单个单词
     *
     * @param wordEntity
     * @param userId
     */
    public long insertWord(WordEntity wordEntity, int userId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(USERID, userId);
        contentValues.put(WORD, wordEntity.key);
        contentValues.put(DEF, wordEntity.def);
        contentValues.put(PRON, wordEntity.pron);
        contentValues.put(AUDIO, wordEntity.audio);
        contentValues.put(VOA, wordEntity.voa);
        contentValues.put(BOOK, wordEntity.book);
        contentValues.put(UNIT, wordEntity.unit);
        return database.insert(TABLE_NAME, null, contentValues);
    }


    /**
     * 删除单个单词
     *
     * @param word
     * @param userId
     */
    public void deleteWord(String word, int userId) {

        database.execSQL(" delete from " + TABLE_NAME + " where " + USERID + " = " + userId + " and " + WORD + " = '" + word + "'");
    }


    public boolean isExsitsWord(String word, int userId) {

        boolean isExits = false;
        Cursor cursor = database.rawQuery("select " + WORD + " from " + TABLE_NAME + " where " + USERID + " = " + userId, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            if (word.equals(cursor.getString(0))) {
                isExits = true;
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return isExits;
    }


    private WordEntity fillIn(Cursor cursor) {
        WordEntity wordEntity = new WordEntity();
        wordEntity.key = cursor.getString(0);
        wordEntity.def = cursor.getString(1);
        wordEntity.pron = cursor.getString(2);
        wordEntity.audio = cursor.getString(3);
        wordEntity.voa = cursor.getInt(4);
        wordEntity.book = cursor.getInt(5);
        wordEntity.unit = cursor.getInt(6);
        return wordEntity;
    }





}
