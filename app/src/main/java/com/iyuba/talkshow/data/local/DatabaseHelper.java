package com.iyuba.talkshow.data.local;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.iyuba.module.toolbox.DBUtil;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.model.ArticleRecord;
import com.iyuba.talkshow.data.model.Category;
import com.iyuba.talkshow.data.model.Collect;
import com.iyuba.talkshow.data.model.Download;
import com.iyuba.talkshow.data.model.Level;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.data.model.Thumb;
import com.iyuba.talkshow.data.model.University;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaBookResponse;
import com.iyuba.talkshow.data.model.VoaBookText;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.util.FileUtils;
import com.iyuba.talkshow.util.SqlUtil;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Emitter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class DatabaseHelper {

    private final BriteDatabase mDb;

    @Inject
    public DatabaseHelper(DbOpenHelper dbOpenHelper) {
        SqlBrite.Builder briteBuilder = new SqlBrite.Builder();
        mDb = briteBuilder.build().wrapDatabaseHelper(dbOpenHelper, Schedulers.immediate());
    }


    public BriteDatabase getBriteDb() {
        return mDb;
    }

    public Observable<Voa> setVoas(final Collection<Voa> newVoas) {
        return Observable.create(new Observable.OnSubscribe<Voa>() {
            @Override
            public void call(Subscriber<? super Voa> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                BriteDatabase.Transaction transaction = mDb.newTransaction();
                try {
                    if (newVoas != null) {
                        for (Voa voa : newVoas) {
                            long result = mDb.insert(Db.VoaTable.TABLE_NAME,
                                    Db.VoaTable.toContentValues(voa),
                                    SQLiteDatabase.CONFLICT_REPLACE);
                            if (result >= 0) {
                                subscriber.onNext(voa);
                            }
                        }
                    }
                    transaction.markSuccessful();
                    subscriber.onCompleted();
                } finally {
                    transaction.end();
                }
            }
        });
    }

    private Observable<Voa> getTypeNumVoas(final int type, final int size) {
        return Observable.create(new Observable.OnSubscribe<Voa>() {
            @Override
            public void call(Subscriber<? super Voa> subscriber) {
                Cursor cursor = mDb.query(
                        "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                                + " WHERE " + Db.VoaTable.COLUMN_CATEGORY + " =? "
                                + " order by " + Db.VoaTable.COLUMN_CREATE_TIME + " desc "
                                + "LIMIT 0, ?",
                        String.valueOf(type), String.valueOf(size));
                while (cursor.moveToNext()) {
                    subscriber.onNext(Db.VoaTable.parseCursor(cursor));
                }
                cursor.close();
                subscriber.onCompleted();
            }
        });
    }

    private Observable<Voa> getNineHotVideoVoas() {
        return Observable.create(new Observable.OnSubscribe<Voa>() {
            @Override
            public void call(Subscriber<? super Voa> subscriber) {
                String sql =
                        "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                                + " WHERE " + Db.VoaTable.COLUMN_CATEGORY + " NOT IN("
                                + Category.Value.DONG_MAN + ", " + Category.Value.TING_GE + ")"
                                + " GROUP BY " + Db.VoaTable.COLUMN_CATEGORY;
                Timber.e(sql);
                Cursor cursor = mDb.query(sql);
                while (cursor.moveToNext()) {
                    subscriber.onNext(Db.VoaTable.parseCursor(cursor));
                }
                cursor.close();
                subscriber.onCompleted();
            }
        });
    }

    private Observable<Voa> getLastHotVideoVoas() {
        return Observable.create(new Observable.OnSubscribe<Voa>() {
            @Override
            public void call(Subscriber<? super Voa> subscriber) {
                Cursor cursor = mDb.query(
                        "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                                + " WHERE " + Db.VoaTable.COLUMN_CATEGORY + " = ?"
                                + " LIMIT 1, 1", String.valueOf(Category.Value.FILM));
                while (cursor.moveToNext()) {
                    subscriber.onNext(Db.VoaTable.parseCursor(cursor));
                }
                cursor.close();
                subscriber.onCompleted();
            }
        });
    }

    public Observable<VoaText> getVoaTextByIndex(int voaId, String index) {
        return Observable.create(new Observable.OnSubscribe<VoaText>() {
            @Override
            public void call(Subscriber<? super VoaText> subscriber) {
                Cursor cursor = mDb.query(
                        "SELECT * FROM " + Db.VoaTextTable.TABLE_NAME
                                + " WHERE " + Db.VoaTextTable.COLUMN_VOA_ID + " = ? "
                                + " AND " + Db.VoaTextTable.COLUMN_PARA_ID + " = ?"
                                + " LIMIT 1, 1", String.valueOf(voaId), index);
                if (cursor.moveToNext()) {

//                    int maxId = 0;
                    subscriber.onNext(Db.VoaTextTable.parseCursor(cursor, voaId));
                } else {
                    subscriber.onNext(null);
                }
                subscriber.onCompleted();
            }
        });
    }


    public Observable<Integer> getMaxVoaId() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Cursor cursor = mDb.query(
                        "SELECT MAX( " + Db.VoaTable.COLUMN_VOA_ID + ") FROM "
                                + Db.VoaTable.TABLE_NAME);
                if (cursor.moveToNext()) {
                    int maxId = cursor.getInt(0);
                    subscriber.onNext(maxId);
                } else {
                    subscriber.onNext(0);
                }
                subscriber.onCompleted();
            }
        });
    }

    public static final int DONG_MAN_SIZE = 4;
    public static final int TING_GE_SIZE = 4;
    public Observable<List<Voa>> getVoas() {
        return Observable.merge(getNineHotVideoVoas(), getLastHotVideoVoas(),
                getTypeNumVoas(Category.Value.DONG_MAN, DONG_MAN_SIZE),
                getTypeNumVoas(Category.Value.TING_GE, TING_GE_SIZE)).toList();
    }

    public Observable<List<Voa>> getVoas(int pageNum, int pageCount,
                                         String type1, String type2,
                                         String type3, String type4,
                                         String type5, String type6, String type7) {
        return Observable.merge(getVoaByLevelOne(pageNum, pageCount),
                getVoasByCategoryNotWith(false, type1, pageCount, "0"),
                getVoasByCategoryNotWith(false, type2, pageCount, "0"),
                getVoasByCategoryNotWith(false, type3, pageCount, "0"),
                getVoasByCategoryNotWith(false, type4, pageCount, "0"),
                getVoasByCategoryNotWith(false, type5, pageCount, "0"),
                getVoasByCategoryNotWith(false, type6, pageCount, "0"),
                getVoasByCategoryNotWith(false, type7, pageCount, "0")
        ).toList();
    }

    public Observable<List<Voa>> getChildHomeVoas(String type) {
        return Observable.merge(getVoasByCategoryNotWith(false, type, 4, "0"),
                getVoaByBothOne(313, Level.Value.ALL, 1, 10)
        ).toList();
    }

    public Observable<List<Voa>> getXiaoxueHomeVoas(int seriesId) {
        List<Voa> list = new ArrayList<>();
        //这里增加对于video的判断，如果有数据不存在video，则从网络获取
        List<Voa> tempList = new ArrayList<>();

        return Observable.create(emitter -> {
            String sql = "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                    + " WHERE " + Db.VoaTable.COLUMN_SERIES + " =  " + seriesId
                    + " AND " + Db.VoaTable.COLUMN_HOT_FLG + " > 0 "
                    + " ORDER BY " + Db.VoaTable.COLUMN_VOA_ID + " ASC ";
            Cursor cursor = mDb.query(sql);
            while (cursor.moveToNext()) {
                Voa temp = Db.VoaTable.parseCursor(cursor);
                tempList.add(temp);
                if (!TextUtils.isEmpty(temp.video())){
                    list.add(Db.VoaTable.parseCursor(cursor));
                }
            }

            if (tempList.size()!=list.size()){
                emitter.onNext(new ArrayList<>());
            }else {
                emitter.onNext(list);
            }
            cursor.close();
            emitter.onCompleted();

        }, Emitter.BackpressureMode.NONE);
    }

    public Observable<Voa> getVoaById(int voaId) {
        return mDb.createQuery(Db.VoaTable.TABLE_NAME,
                "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                        + " WHERE " + Db.VoaTable.COLUMN_VOA_ID + " = ?",
                String.valueOf(voaId)).mapToOne(new Func1<Cursor, Voa>() {
            @Override
            public Voa call(Cursor cursor) {
                if (cursor != null) {
                    return Db.VoaTable.parseCursor(cursor);
                } else {
                    return null;
                }
            }
        });
    }

    public Observable<List<Voa>> getVoa(int category, String level, int pageNum, int pageSize) {
        if (category == Category.Value.ALL) {
            return getVoaByLevel(level, pageNum, pageSize);
        } else if (category != 5000) {
            return getVoaByBoth(category, level, pageNum, pageSize);
        } else {
            return getVoaXiaoxue(category, level, pageNum, pageSize);
        }
    }

    public Observable<List<Voa>> getVoa(String category, String level, int pageNum, int pageSize) {

        return getVoaByBoth(category, level, pageNum, pageSize);
    }

    private Observable<List<Voa>> getVoaByBoth(String category, String level, int pageNum, int pageSize) {
        int offSize = (pageNum - 1) * pageSize;
        return mDb.createQuery(Db.VoaTable.TABLE_NAME,
                "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                        + " WHERE (" + SqlUtil.handleIn(Db.VoaTable.COLUMN_HOT_FLG, level.split(Level.Value.SEP))
                        + ") AND " + Db.VoaTable.COLUMN_CATEGORY + " in  ( ? ) "
                        + " order by " + Db.VoaTable.COLUMN_CREATE_TIME + " desc "
                        + " LIMIT ?, ?"
                , String.valueOf(category), String.valueOf(offSize), String.valueOf(pageSize))
                .mapToList(new Func1<Cursor, Voa>() {
                    @Override
                    public Voa call(Cursor cursor) {
                        return Db.VoaTable.parseCursor(cursor);
                    }
                });
    }

    private Observable<List<Voa>> getVoaByBoth(int category, String level, int pageNum, int pageSize) {
        int offSize = (pageNum - 1) * pageSize;
        return mDb.createQuery(Db.VoaTable.TABLE_NAME,
                "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                        + " WHERE (" + SqlUtil.handleIn(Db.VoaTable.COLUMN_HOT_FLG, level.split(Level.Value.SEP))
                        + ") AND " + Db.VoaTable.COLUMN_CATEGORY + " = ? "
                        + " order by " + Db.VoaTable.COLUMN_CREATE_TIME + " desc "
                        + " LIMIT ?, ?"
                , String.valueOf(category), String.valueOf(offSize), String.valueOf(pageSize))
                .mapToList(new Func1<Cursor, Voa>() {
                    @Override
                    public Voa call(Cursor cursor) {
                        return Db.VoaTable.parseCursor(cursor);
                    }
                });
    }


    private Observable<List<Voa>> getVoaXiaoxue(int category, String level, int pageNum, int pageSize) {
        int offSize = (pageNum - 1) * pageSize;
        return mDb.createQuery(Db.VoaTable.TABLE_NAME,
                "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                        + " WHERE (" + SqlUtil.handleIn(Db.VoaTable.COLUMN_HOT_FLG, level.split(Level.Value.SEP))
                        + ") AND " + Db.VoaTable.COLUMN_CATEGORY + " > ? "
                        + " order by " + Db.VoaTable.COLUMN_CREATE_TIME + " desc "
                        + " LIMIT ?, ?"
                , "311", String.valueOf(offSize), String.valueOf(pageSize))
                .mapToList(new Func1<Cursor, Voa>() {
                    @Override
                    public Voa call(Cursor cursor) {
                        return Db.VoaTable.parseCursor(cursor);
                    }
                });
    }

    public void insertToSeries(SeriesData bean) {
        mDb.insert(Db.SeriesTable.TABLE_NAME,
                Db.SeriesTable.toContentValues(bean),
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<SeriesData> getSeriesCategory(int category) {
        Log.e("DatabaseHelper", "getSeriesId category = " + category);
        List<SeriesData> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.SeriesTable.TABLE_NAME
                + " WHERE " + Db.SeriesTable.COLUMN_SERIES_CATEGORY + " = " + category;
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            SeriesData record = Db.SeriesTable.parseCursor(cursor);
            list.add(record);
        }
        cursor.close();
        return list;
    }

    public Observable<List<SeriesData>> getSeriesList(String category) {
        return mDb.createQuery(Db.SeriesTable.TABLE_NAME,
                "SELECT * FROM " + Db.SeriesTable.TABLE_NAME
                        + " WHERE  " + Db.SeriesTable.COLUMN_SERIES_CATEGORY + " = ?"
                , String.valueOf(category))
                .mapToList(new Func1<Cursor, SeriesData>() {
                    @Override
                    public SeriesData call(Cursor cursor) {
                        return Db.SeriesTable.parseCursor(cursor);
                    }
                });
    }

    public long insertToVoa(Voa bean){
        return mDb.insert(Db.VoaTable.TABLE_NAME, Db.VoaTable.toContentValues(bean), SQLiteDatabase.CONFLICT_REPLACE);
    }

    private Observable<List<Voa>> getVoaByLevel(String level, int pageNum, int pageSize) {
        int offSize = (pageNum - 1) * pageSize;
        Timber.e("*******offSize: " + offSize);
        return mDb.createQuery(Db.VoaTable.TABLE_NAME,
                "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                        + " WHERE " + SqlUtil.handleIn(Db.VoaTable.COLUMN_HOT_FLG, level.split(Level.Value.SEP))
                        + " order by " + Db.VoaTable.COLUMN_CREATE_TIME + " desc " +
                        "LIMIT ?, ?"
                , String.valueOf(offSize), String.valueOf(pageSize))
                .mapToList(new Func1<Cursor, Voa>() {
                    @Override
                    public Voa call(Cursor cursor) {
                        return Db.VoaTable.parseCursor(cursor);
                    }
                });
    }

    public Observable<Voa> getVoaByLevelOne(int pageNum, final int pageSize) {
        final int offSize = (pageNum - 1) * pageSize;
        Timber.e("*******offSize: " + offSize);
        return Observable.create(new Observable.OnSubscribe<Voa>() {
            @Override
            public void call(Subscriber<? super Voa> subscriber) {
                String sql = "SELECT * FROM 'VOA' "
                        + " ORDER BY " + Db.VoaTable.COLUMN_CREATE_TIME + " DESC "
                        + " LIMIT " + offSize + ", " + pageSize + " ;";
                Log.e("create", sql);
                Cursor cursor = mDb.query(sql);
                while (cursor.moveToNext()) {
                    Timber.e("---");
                    subscriber.onNext(Db.VoaTable.parseCursor(cursor));
                }
                cursor.close();
                subscriber.onCompleted();
            }
        });
    }

    public Observable<List<Voa>> getRecommendList(int category, int pageNum, int pageSize) {
        int offSize = (pageNum - 1) * pageSize;
        return mDb.createQuery(Db.VoaTable.TABLE_NAME,
                "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                        + " WHERE " + Db.VoaTable.COLUMN_CATEGORY + " = ? "
                        + " ORDER BY " + Db.VoaTable.COLUMN_READ_COUNT + " DESC "
                        + " LIMIT ?, ?"
                , String.valueOf(category), String.valueOf(offSize), String.valueOf(pageSize))
                .mapToList(new Func1<Cursor, Voa>() {
                    @Override
                    public Voa call(Cursor cursor) {
                        return Db.VoaTable.parseCursor(cursor);
                    }
                });
    }

    public Observable<List<Voa>> getSeriesList(int series, int pageNum, int pageSize) {
        int offSize = (pageNum - 1) * pageSize;
        return mDb.createQuery(Db.VoaTable.TABLE_NAME,
                "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                        + " WHERE " + Db.VoaTable.COLUMN_SERIES + " = ? "
                        + " ORDER BY " + Db.VoaTable.COLUMN_VOA_ID + " ASC "
                        + " LIMIT ?, ?"
                , String.valueOf(series), String.valueOf(offSize), String.valueOf(pageSize))
                .mapToList(new Func1<Cursor, Voa>() {
                    @Override
                    public Voa call(Cursor cursor) {
                        return Db.VoaTable.parseCursor(cursor);
                    }
                });
    }

    public Observable<VoaText> setVoaTexts(final Collection<VoaText> voaTexts, final int voaId) {
        return Observable.create(new Observable.OnSubscribe<VoaText>() {
            @Override
            public void call(Subscriber<? super VoaText> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                BriteDatabase.Transaction transaction = mDb.newTransaction();
                try {
                    if ((voaTexts != null) && (voaTexts.size() > 0)) {
                        mDb.delete(Db.VoaTextTable.TABLE_NAME,
                                Db.VoaTextTable.COLUMN_VOA_ID + " = ?", String.valueOf(voaId));
                        // need insert new items
                        Log.e("DatabaseHelper", "setVoaTexts ");
//                        List<VoaText> oldText = getVoaTextbyVoaId(voaId);
//                        List<VoaText> needInsert = new ArrayList<>();
//                        if (oldText != null && oldText.size() > 0) {
//                            for (VoaText voaText : voaTexts) {
//                                boolean flag = false;
//                                for (VoaText old : oldText) {
//                                    if (old.getVoaId() == voaText.getVoaId() && old.paraId() == voaText.paraId()) {
//                                        flag = true;
//                                        break;
//                                    }
//                                }
//                                if (!flag) {
//                                    needInsert.add(voaText);
//                                }
//                            }
//                        } else {
//                            needInsert.addAll(voaTexts);
//                        }
                        for (VoaText voaText : voaTexts) {
                            long result = mDb.insert(Db.VoaTextTable.TABLE_NAME,
                                    Db.VoaTextTable.toContentValues(voaText, String.valueOf(voaId)),
                                    SQLiteDatabase.CONFLICT_REPLACE);
                            if (result >= 0) {
                                voaText.setVoaId(voaId);
                                subscriber.onNext(voaText);
                            }
                        }
                    } else {
                        subscriber.onNext(null);
                    }
                    transaction.markSuccessful();
                    subscriber.onCompleted();
                } finally {
                    transaction.end();
                }
            }
        });
    }

    public Observable<VoaBookText> setVoaBook(VoaBookResponse voaResponse) {
        return Observable.create(new Observable.OnSubscribe<VoaBookText>() {
            @Override
            public void call(Subscriber<? super VoaBookText> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                if (voaResponse == null || voaResponse.textinfo == null || voaResponse.textinfo.size() < 1) {
                    Log.e("DatabaseHelper", "setVoaBook is null.");
                    return;
                }
                Log.e("DatabaseHelper", "setVoaBook textsize = " + voaResponse.textsize);
                HashMap<Integer, List<VoaBookText>> parseTexts = new HashMap();
                for (VoaBookText series: voaResponse.textinfo) {
                    if (parseTexts.containsKey(series.voaid)) {
                        parseTexts.get(series.voaid).add(series);
                    } else {
                        ArrayList<VoaBookText> texts = new ArrayList<>();
                        texts.add(series);
                        parseTexts.put(series.voaid, texts);
                    }
                }
                BriteDatabase.Transaction transaction = mDb.newTransaction();
                try {
                    for (Integer voaId : parseTexts.keySet()) {
                        List<VoaBookText> voaTexts = parseTexts.get(voaId);
                        mDb.delete(Db.VoaTextTable.TABLE_NAME,
                                Db.VoaTextTable.COLUMN_VOA_ID + " = ?", String.valueOf(voaId));
                        // need insert new items
                        Log.e("DatabaseHelper", "setVoaBook voaId = " + voaId);
                        for (VoaBookText voaText : voaTexts) {
                            long result = mDb.insert(Db.VoaTextTable.TABLE_NAME,
                                    Db.VoaTextTable.toContentValues(voaText),
                                    SQLiteDatabase.CONFLICT_REPLACE);
                            if (result >= 0) {
                                subscriber.onNext(voaText);
                            }
                        }
                    }
                    transaction.markSuccessful();
                    subscriber.onCompleted();
                } finally {
                    transaction.end();
                }
            }
        });
    }

    public Observable<VoaBookText> setBookTexts(final Collection<VoaBookText> voaTexts) {
        return Observable.create(new Observable.OnSubscribe<VoaBookText>() {
            @Override
            public void call(Subscriber<? super VoaBookText> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                if ((voaTexts == null) || (voaTexts.size() < 1)) {
                    Log.e("DatabaseHelper", "setBookTexts is null, no need set.");
                    subscriber.onNext(new VoaBookText());
                    return;
                }
                Log.e("DatabaseHelper", "setBookTexts size = " + voaTexts.size());
                HashMap<Integer, List<VoaBookText>> parseTexts = new HashMap();
                for (VoaBookText series: voaTexts) {
                    if (parseTexts.containsKey(series.voaid)) {
                        parseTexts.get(series.voaid).add(series);
                    } else {
                        ArrayList<VoaBookText> texts = new ArrayList<>();
                        texts.add(series);
                        parseTexts.put(series.voaid, texts);
                    }
                }
                BriteDatabase.Transaction transaction = mDb.newTransaction();
                try {
                    for (Integer voaId : parseTexts.keySet()) {
                        List<VoaBookText> voaTexts = parseTexts.get(voaId);
                        mDb.delete(Db.VoaTextTable.TABLE_NAME,
                                Db.VoaTextTable.COLUMN_VOA_ID + " = ?", String.valueOf(voaId));
                        // need insert new items
                        Log.e("DatabaseHelper", "setBookTexts voaId = " + voaId);
                        for (VoaBookText voaText : voaTexts) {
                            long result = mDb.insert(Db.VoaTextTable.TABLE_NAME,
                                    Db.VoaTextTable.toContentValues(voaText),
                                    SQLiteDatabase.CONFLICT_REPLACE);
                            if (result >= 0) {
                                subscriber.onNext(voaText);
                            }
                        }
                    }
                    transaction.markSuccessful();
                    subscriber.onCompleted();
                } finally {
                    transaction.end();
                }
            }
        });
    }

    public List<VoaText> getVoaTextbyVoaIndex(int vaoid, int index) {
        Log.e("DatabaseHelper", "getVoaTextbyVoaIndex ---id1");
        List<VoaText> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.VoaTextTable.TABLE_NAME
                + " WHERE " + Db.VoaTextTable.COLUMN_VOA_ID + " = " + vaoid
                + " AND " + Db.VoaTextTable.COLUMN_ID_INDEX + " = " + index
                + " ORDER BY " + Db.VoaTextTable.COLUMN_PARA_ID + " ASC";
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            VoaText record = Db.VoaTextTable.parseCursor(cursor, vaoid);
            list.add(record);
        }
        return list;
    }

    public List<VoaText> getVoaTextByParaId(int vaoid, int index) {
        Log.e("DatabaseHelper", "getVoaTextByParaId ---id1");
        List<VoaText> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.VoaTextTable.TABLE_NAME
                + " WHERE " + Db.VoaTextTable.COLUMN_VOA_ID + " = " + vaoid
                + " AND " + Db.VoaTextTable.COLUMN_PARA_ID + " = " + index
                + " ORDER BY " + Db.VoaTextTable.COLUMN_PARA_ID + " ASC";
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            VoaText record = Db.VoaTextTable.parseCursor(cursor, vaoid);
            list.add(record);
        }
        return list;
    }

    public List<VoaText> getVoaTextbyVoaId(int vaoid) {
//        Log.e("DatabaseHelper", "getVoaTextbyVoaId ---id1");
        List<VoaText> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.VoaTextTable.TABLE_NAME
                + " WHERE " + Db.VoaTextTable.COLUMN_VOA_ID + " = " + vaoid
                + " ORDER BY " + Db.VoaTextTable.COLUMN_PARA_ID + " ASC";
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            VoaText record = Db.VoaTextTable.parseCursor(cursor, vaoid);
            list.add(record);
        }
        cursor.close();
        return list;
    }

    public Observable<List<VoaText>> getVoaTexts(final int voaId) {
        return mDb.createQuery(Db.VoaTextTable.TABLE_NAME,
                "SELECT * FROM " + Db.VoaTextTable.TABLE_NAME
                        + " WHERE " + Db.VoaTextTable.COLUMN_VOA_ID + " = ?"
                        + " ORDER BY " + Db.VoaTextTable.COLUMN_ID_INDEX
                        + " AND "+ Db.VoaTextTable.COLUMN_PARA_ID
                        + " ASC",
                String.valueOf(voaId))
                .mapToList(new Func1<Cursor, VoaText>() {
                    @Override
                    public VoaText call(Cursor cursor) {
                        return Db.VoaTextTable.parseCursor(cursor, voaId);
                    }
                });
    }

    public Observable<Boolean> deleteRecord(final long timestamp) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                long result = mDb.delete(Db.RecordTable.TABLE_NAME,
                        Db.RecordTable.COLUMN_TIMESTAMP + " = ?",
                        String.valueOf(timestamp));
                subscriber.onNext(result > 0);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Boolean> saveRecord(final Record record) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                ContentValues contentValues = Db.RecordTable.toContentValues(record);
                long result = mDb.insert(Db.RecordTable.TABLE_NAME, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                subscriber.onNext(result > 0);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<List<Record>> getDraftRecord() {
        return mDb.createQuery(Db.RecordTable.TABLE_NAME,
                "SELECT * FROM " + Db.RecordTable.TABLE_NAME
//                        + " WHERE " + Db.RecordTable.COLUMN_FINISH_NUM + " < "
//                        + Db.RecordTable.COLUMN_TOTAL_NUM
                        + " ORDER BY " + Db.RecordTable.COLUMN_DATE + " DESC")
                .mapToList(new Func1<Cursor, Record>() {
                    @Override
                    public Record call(Cursor cursor) {
                        Record record = Db.RecordTable.parseCursor(cursor);
                        Timber.e("call***" + record.titleCn());
                        return record;
                    }
                });
    }

    public List<Record> getRecordbyVoaId(int vaoid) {
        Log.e("DatabaseHelper", "getRecordbyVoaId ---id1");
        List<Record> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.RecordTable.TABLE_NAME
                + " WHERE " + Db.RecordTable.COLUMN_VOA_ID + " = " + vaoid
                + " ORDER BY " + Db.RecordTable.COLUMN_DATE + " ASC";
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            Record record = Db.RecordTable.parseCursor(cursor);
            list.add(record);
        }
        return list;
    }

    public Observable<Boolean> saveVoaSound(final VoaSoundNew record) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                long result = mDb.delete(Db.VoaSoundTable.TABLE_NAME, Db.VoaSoundTable.COLUMN_ITEMID + " = ?", String.valueOf(record.itemid()));
                Log.e("DatabaseHelper", "delete result :  " + result);
                ContentValues contentValues = Db.VoaSoundTable.toContentValues(record);
                result = mDb.insert(Db.VoaSoundTable.TABLE_NAME, contentValues);
                Log.e("DatabaseHelper", "saveVoaSound result :  " + result);
                subscriber.onNext(result > 0);
                subscriber.onCompleted();
            }
        });
    }

    public Boolean saveVoaSoundNew(final VoaSoundNew record) {
        long result = mDb.delete(Db.VoaSoundTable.TABLE_NAME, Db.VoaSoundTable.COLUMN_ITEMID + " = ?", String.valueOf(record.itemid()));
//        Log.e("DatabaseHelper", "saveVoaSoundNew delete result :  " + result);
        ContentValues contentValues = Db.VoaSoundTable.toContentValues(record);
        result = mDb.insert(Db.VoaSoundTable.TABLE_NAME, contentValues);
        Log.e("DatabaseHelper", "saveVoaSoundNew result :  " + result);
        return (result > 0);
    }

    public List<VoaSoundNew> getVoaSoundVoaId(int vaoid) {
        Log.e("DatabaseHelper", "getVoaSoundVoaId ---id1");
        List<VoaSoundNew> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.VoaSoundTable.TABLE_NAME
                + " WHERE " + Db.VoaSoundTable.COLUMN_VOA_ID + " = " + vaoid
                + " ORDER BY " + Db.VoaSoundTable.COLUMN_ITEMID + " ASC";
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            VoaSoundNew record = Db.VoaSoundTable.parseCursor(cursor);
            list.add(record);
        }
        cursor.close();
        return list;
    }

    public List<VoaSoundNew> getVoaSoundVoaUid(int uid, int vaoid) {
        Log.e("DatabaseHelper", "getVoaSoundVoaId ---id1");
        List<VoaSoundNew> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.VoaSoundTable.TABLE_NAME
                + " WHERE " + Db.VoaSoundTable.COLUMN_VOA_ID + " = " + vaoid
                + " AND " + Db.VoaSoundTable.COLUMN_UID + " = " + uid
                + " ORDER BY " + Db.VoaSoundTable.COLUMN_ITEMID + " ASC";
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            VoaSoundNew record = Db.VoaSoundTable.parseCursor(cursor);
            list.add(record);
        }
        cursor.close();
        return list;
    }

    public List<VoaSoundNew> getVoaSoundItemUid(int uid, long itemid) {
        Log.e("DatabaseHelper", "getVoaSoundVoaTime ---id1");
        List<VoaSoundNew> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.VoaSoundTable.TABLE_NAME
                + " WHERE " + Db.VoaSoundTable.COLUMN_UID + " = " + uid
                + " AND " + Db.VoaSoundTable.COLUMN_ITEMID + " = " + itemid
                + " ORDER BY " + Db.VoaSoundTable.COLUMN_ITEMID + " ASC";
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            VoaSoundNew record = Db.VoaSoundTable.parseCursor(cursor);
            list.add(record);
        }
        return list;
    }

    public int checkDbUpgrade() {
        SQLiteDatabase db = mDb.getWritableDatabase();
        if (db == null) {
            return 0;
        }
        try {
            db.beginTransaction();
            if (!DBUtil.isColumnExist(db, Db.VoaSoundTable.TABLE_NAME, Db.VoaSoundTable.COLUMN_WORDS)) {
                db.execSQL("ALTER TABLE  '" + Db.VoaSoundTable.TABLE_NAME + "'  ADD 'words' TEXT ");
            }
            db.execSQL("drop table if exists '" + Db.VoaTextTable.TABLE_NAME + "'");
            db.execSQL(Db.VoaTextTable.CREATE);
            db.setTransactionSuccessful();
        } catch (Exception var2) {
            return 0;
        } finally {
            db.endTransaction();
        }
        Log.e("DatabaseHelper", "checkDbUpgrade and store data --- ");
        // TODO: 2025/2/25 暂时用不到这里的功能了，直接关闭掉 
//        FileUtils.executeAssetsSQL(TalkShowApplication.getContext(), db, "preData_junior_lessonAndText.sql");
        return 1;
    }

    public Observable<List<Record>> getDraftRecord(long mTimeStamp) {
        return mDb.createQuery(Db.RecordTable.TABLE_NAME,
                "SELECT * FROM " + Db.RecordTable.TABLE_NAME
                        + " WHERE " + Db.RecordTable.COLUMN_TIMESTAMP + " = " + mTimeStamp
                        + " ORDER BY " + Db.RecordTable.COLUMN_DATE + " DESC")
                .mapToList(new Func1<Cursor, Record>() {
                    @Override
                    public Record call(Cursor cursor) {
                        Record record = Db.RecordTable.parseCursor(cursor);
                        Timber.e("call***" + record.title());
                        return record;
                    }
                });
    }

    public Observable<List<Record>> getFinishedRecord() {
//        Thread.sleep(1000);
//        new Object().wait();
        return mDb.createQuery(Db.RecordTable.TABLE_NAME,
                "SELECT * FROM " + Db.RecordTable.TABLE_NAME
                        + " WHERE " + Db.RecordTable.COLUMN_FINISH_NUM
                        + " >= " + Db.RecordTable.COLUMN_TOTAL_NUM
                        + " ORDER BY " + Db.RecordTable.COLUMN_DATE + " DESC")
                .mapToList(new Func1<Cursor, Record>() {
                    @Override
                    public Record call(Cursor cursor) {
                        return Db.RecordTable.parseCursor(cursor);
                    }
                });
    }

    public Observable<Boolean> deleteRecord(final List<String> timestamps) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                BriteDatabase.Transaction transaction = mDb.newTransaction();
                try {
                    if (timestamps != null) {
                        for (String timestamp : timestamps) {
                            mDb.delete(Db.RecordTable.TABLE_NAME,
                                    Db.RecordTable.COLUMN_TIMESTAMP + " = ?",
                                    String.valueOf(timestamp));
                        }
                    }
                    transaction.markSuccessful();
                    subscriber.onNext(true);
                } finally {
                    transaction.end();
                }
            }
        });
    }

    public Observable<Boolean> saveArticleRecord(final ArticleRecord bean) {
//        Log.e("DatabaseHelper", "saveArticleRecord ---  ");
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                ContentValues contentValues = Db.ArticleRecordTable.toContentValues(bean);
                long result = mDb.insert(Db.ArticleRecordTable.TABLE_NAME, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                subscriber.onNext(result > 0);
                Log.e("DatabaseHelper", "saveArticleRecord result :  " + result);
                subscriber.onCompleted();
            }
        });
    }
    public Boolean saveArticleRecordNew(final ArticleRecord bean) {
        ContentValues contentValues = Db.ArticleRecordTable.toContentValues(bean);
        long result = mDb.insert(Db.ArticleRecordTable.TABLE_NAME, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        Log.e("DatabaseHelper", "saveArticleRecord result :  " + result);
        return (result > 0);
    }

    public List<ArticleRecord> getArticlebyVoaId(int vaoid) {
//        Log.e("DatabaseHelper", "getArticlebyVoaId ---id1");
        List<ArticleRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.ArticleRecordTable.TABLE_NAME
                + " WHERE " + Db.ArticleRecordTable.COLUMN_VOA_ID + " = " + vaoid
                + " ORDER BY " + Db.ArticleRecordTable.COLUMN_VOA_ID + " ASC";
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            ArticleRecord record = Db.ArticleRecordTable.parseCursor(cursor);
            list.add(record);
        }
        return list;
    }

    public List<ArticleRecord> getArticleByUid(int uid, int vaoid) {
//        Log.e("DatabaseHelper", "getArticlebyVoaId ---id1");
        List<ArticleRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.ArticleRecordTable.TABLE_NAME
                + " WHERE " + Db.ArticleRecordTable.COLUMN_VOA_ID + " = " + vaoid
                + " AND " + Db.ArticleRecordTable.COLUMN_UID + " = " + uid
                + " ORDER BY " + Db.ArticleRecordTable.COLUMN_VOA_ID + " ASC";
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            ArticleRecord record = Db.ArticleRecordTable.parseCursor(cursor);
            list.add(record);
        }
        return list;
    }

    public Observable<List<ArticleRecord>> getArticleRecords(int vaoid) {
//        Log.e("DatabaseHelper", "getArticleRecords ---id1");
        return mDb.createQuery(Db.ArticleRecordTable.TABLE_NAME,
                "SELECT * FROM " + Db.ArticleRecordTable.TABLE_NAME
                        + " WHERE " + Db.ArticleRecordTable.COLUMN_VOA_ID + " = " + vaoid
                        + " ORDER BY " + Db.ArticleRecordTable.COLUMN_VOA_ID + " ASC")
                .mapToList(new Func1<Cursor, ArticleRecord>() {
                    @Override
                    public ArticleRecord call(Cursor cursor) {
                        ArticleRecord record = Db.ArticleRecordTable.parseCursor(cursor);
                        return record;
                    }
                });
    }

    public Observable<Boolean> saveCollect(final Collect collect) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Db.CollectTable.COLUMN_UID, collect.uid());
                contentValues.put(Db.CollectTable.COLUMN_VOA_ID, collect.voaId());
                contentValues.put(Db.CollectTable.COLUMN_DATE, collect.date());
                long result = mDb.insert(Db.CollectTable.TABLE_NAME, contentValues);
                subscriber.onNext(result > 0);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Boolean> deleteCollect(final int uid, final List<String> list) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                BriteDatabase.Transaction transaction = mDb.newTransaction();
                try {
                    if (list != null) {
                        for (String voaId : list) {
                            mDb.delete(Db.CollectTable.TABLE_NAME,
                                    Db.CollectTable.COLUMN_VOA_ID + " = ? AND "
                                            + Db.CollectTable.COLUMN_UID + " = ?",
                                    voaId, String.valueOf(uid));
                        }
                    }
                    transaction.markSuccessful();
                    subscriber.onNext(true);
                } finally {
                    transaction.end();
                }
            }
        });
    }

    public List<Collect> getCollect(final int uid, final int vaoid) {
        List<Collect> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.CollectTable.TABLE_NAME
                + " WHERE " + Db.CollectTable.COLUMN_VOA_ID + " = " + vaoid
                + " AND " + Db.CollectTable.COLUMN_UID + " = " + uid
                + " ORDER BY " + Db.CollectTable.COLUMN_VOA_ID + " ASC";
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            Collect record = Db.CollectTable.parseCursor(cursor);
            list.add(record);
        }
        return list;
    }
    public Observable<Boolean> deleteCollect(final int uid, final int voaId) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                long result = mDb.delete(Db.CollectTable.TABLE_NAME,
                        Db.CollectTable.COLUMN_VOA_ID + " = ? AND "
                                + Db.CollectTable.COLUMN_UID + " = ?",
                        String.valueOf(voaId), String.valueOf(uid));
                subscriber.onNext(result > 0);
                subscriber.onCompleted();
            }
        });
    }

    public Boolean deleteUidCollect(final int uid) {
        BriteDatabase.Transaction transaction = mDb.newTransaction();
        try {
            mDb.delete(Db.CollectTable.TABLE_NAME,
                    Db.CollectTable.COLUMN_UID + " = ?", String.valueOf(uid));
            transaction.markSuccessful();
        } catch (Exception var1) {
            return false;
        } finally {
            transaction.end();
        }
        return true;
    }

    public Observable<List<Collect>> getCollect(final int uid) {
        return Observable.create(new Observable.OnSubscribe<Collect>() {
            @Override
            public void call(Subscriber<? super Collect> subscriber) {
                Cursor cursor = mDb.query(
                        "SELECT * FROM " + Db.CollectTable.TABLE_NAME
                                + " WHERE " + Db.CollectTable.COLUMN_UID + " = ?",
                        String.valueOf(uid));
                while (cursor.moveToNext()) {
                    subscriber.onNext(Db.CollectTable.parseCursor(cursor));
                }
                cursor.close();
                subscriber.onCompleted();
            }
        }).doOnNext(new Action1<Collect>() {
            @Override
            public void call(Collect collect) {
                Cursor voaCursor = mDb.query(
                        "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                                + " WHERE " + Db.VoaTable.COLUMN_VOA_ID + " = ?",
                        String.valueOf(collect.voaId()));
                if (voaCursor.moveToNext()) {
                    collect.setVoa(Db.VoaTable.parseCursor(voaCursor));
                }
            }
        }).toList();
    }

    public Observable<Integer> getCollectByVoaId(final int voaId) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Cursor cursor = mDb.query(
                        "SELECT " + Db.CollectTable.COLUMN_VOA_ID
                                + " FROM " + Db.CollectTable.TABLE_NAME
                                + " WHERE " + Db.CollectTable.COLUMN_VOA_ID + " = ?",
                        String.valueOf(voaId));
                if (cursor.moveToNext()) {
                    subscriber.onNext(cursor.getInt(0));
                } else {
                    subscriber.onNext(-1);
                }
                cursor.close();
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Boolean> saveDownload(final Download download) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                ContentValues contentValues = Db.DownloadTable.toContentValues(download);
                long result = mDb.insert(Db.DownloadTable.TABLE_NAME, contentValues);
                subscriber.onNext(result > 0);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Boolean> deleteDownload(final List<String> list) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                BriteDatabase.Transaction transaction = mDb.newTransaction();
                try {
                    if (list != null) {
                        for (String voaId : list) {
                            mDb.delete(Db.DownloadTable.TABLE_NAME,
                                    Db.DownloadTable.COLUMN_VOA_ID + " = ?",
                                    String.valueOf(voaId));
                        }
                    }
                    transaction.markSuccessful();
                    subscriber.onNext(true);
                } finally {
                    transaction.end();
                }
            }
        });
    }

    public Observable<List<Download>> getDownload() {
        return Observable.create(new Observable.OnSubscribe<Download>() {
            @Override
            public void call(Subscriber<? super Download> subscriber) {
                Cursor cursor = mDb.query(
                        "SELECT * FROM " + Db.DownloadTable.TABLE_NAME
                                + " GROUP BY " + Db.DownloadTable.COLUMN_VOA_ID);
                while (cursor.moveToNext()) {
                    subscriber.onNext(Db.DownloadTable.parseCursor(cursor));
                }
                cursor.close();
                subscriber.onCompleted();
            }
        }).doOnNext(new Action1<Download>() {
            @Override
            public void call(Download download) {
                Cursor voaCursor = mDb.query(
                        "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                                + " WHERE " + Db.VoaTable.COLUMN_VOA_ID + " = ?",
                        String.valueOf(download.voaId()));
                if (voaCursor.moveToNext()) {
                    Voa tempVoa = Db.VoaTable.parseCursor(voaCursor);
                    download.setVoa(tempVoa);
                }
            }
        }).toList();
    }

    public Observable<Boolean> deleteDownload(int uid, final List<String> list) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                BriteDatabase.Transaction transaction = mDb.newTransaction();
                try {
                    if (list != null) {
                        for (String voaId : list) {
                            mDb.delete(Db.DownloadTable.TABLE_NAME,
                                    Db.DownloadTable.COLUMN_VOA_ID + " = ? AND "
                                            + Db.DownloadTable.COLUMN_UID + " = ?",
                                    voaId, String.valueOf(uid));
                        }
                    }
                    transaction.markSuccessful();
                    subscriber.onNext(true);
                } finally {
                    transaction.end();
                }
            }
        });
    }

    public Boolean deleteUidDownload(int uid) {
        BriteDatabase.Transaction transaction = mDb.newTransaction();
        try {
            mDb.delete(Db.DownloadTable.TABLE_NAME,
                    Db.DownloadTable.COLUMN_UID + " = ?", String.valueOf(uid));
            transaction.markSuccessful();
        } catch (Exception var1) {
            return false;
        } finally {
            transaction.end();
        }
        return true;
    }

    public Observable<List<Download>> getDownload(final int uid) {
        return Observable.create(new Observable.OnSubscribe<Download>() {
            @Override
            public void call(Subscriber<? super Download> subscriber) {
                Cursor cursor = mDb.query(
                        "SELECT * FROM " + Db.DownloadTable.TABLE_NAME
                                + " WHERE " + Db.DownloadTable.COLUMN_UID + " = ?",
                        String.valueOf(uid));
                while (cursor.moveToNext()) {
                    subscriber.onNext(Db.DownloadTable.parseCursor(cursor));
                }
                cursor.close();
                subscriber.onCompleted();
            }
        }).doOnNext(new Action1<Download>() {
            @Override
            public void call(Download download) {
                Cursor voaCursor = mDb.query(
                        "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                                + " WHERE " + Db.VoaTable.COLUMN_VOA_ID + " = ?",
                        String.valueOf(download.voaId()));
                if (voaCursor.moveToNext()) {
                    download.setVoa(Db.VoaTable.parseCursor(voaCursor));
                }
            }
        }).toList();
    }

    public Observable<List<University>> getUniversity(final String keyword, final int size) {
        return Observable.create(new Observable.OnSubscribe<University>() {
            @Override
            public void call(Subscriber<? super University> subscriber) {
                Cursor cursor = mDb.query(
                        "SELECT * FROM " + Db.UniversityTable.TABLE_NAME
                                + " WHERE " + Db.UniversityTable.COLUMN_UNI_NAME
                                + " LIKE ? ",
                        "%" + keyword + "%");
                while (cursor.moveToNext()) {
                    subscriber.onNext(Db.UniversityTable.parseCursor(cursor));
                }
                cursor.close();
                subscriber.onCompleted();
            }
        }).toList();
    }

    public Observable<List<University>> getAllUniversity() {
        return Observable.create(new Observable.OnSubscribe<University>() {
            @Override
            public void call(Subscriber<? super University> subscriber) {
                Cursor cursor = mDb.query(
                        "SELECT * FROM " + Db.UniversityTable.TABLE_NAME);
                while (cursor.moveToNext()) {
                    subscriber.onNext(Db.UniversityTable.parseCursor(cursor));
                }
                cursor.close();
                subscriber.onCompleted();
            }
        }).toList();
    }

    public List<Thumb> getCommentById(final int commentId) {
        Log.e("DatabaseHelper", "getCommentThumb ---id1");
        Cursor cursor = mDb.query(
                "SELECT * FROM " + Db.ThumbTable.TABLE_NAME
                        + " WHERE " + Db.ThumbTable.COLUMN_COMMENT_ID + " = " + commentId);
        List<Thumb> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Thumb record = Db.ThumbTable.parseCursor(cursor);
            list.add(record);
        }
        return list;
    }

    public List<Thumb> getCommentThumb(final int uid, final int commentId) {
        Log.e("DatabaseHelper", "getCommentThumb ---id1");
        Cursor cursor = mDb.query(
                "SELECT * FROM " + Db.ThumbTable.TABLE_NAME
                        + " WHERE " + Db.ThumbTable.COLUMN_UID + " = " + uid
                        + " AND " + Db.ThumbTable.COLUMN_COMMENT_ID + " = " + commentId);
        List<Thumb> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Thumb record = Db.ThumbTable.parseCursor(cursor);
            list.add(record);
        }
        return list;
    }

    public Observable<Thumb> getThumb(final int uid, final int commentId) {
        return Observable.create(new Observable.OnSubscribe<Thumb>() {
            @Override
            public void call(Subscriber<? super Thumb> subscriber) {
                Cursor cursor = mDb.query(
                        "SELECT * FROM " + Db.ThumbTable.TABLE_NAME
                                + " WHERE " + Db.ThumbTable.COLUMN_UID + " = ? "
                                + " AND " + Db.ThumbTable.COLUMN_COMMENT_ID + " = ?",
                        String.valueOf(uid), String.valueOf(commentId));
                if (cursor.moveToNext()) {
                    subscriber.onNext(Db.ThumbTable.parseCursor(cursor));
                } else {
                    subscriber.onNext(null);
                }
                cursor.close();
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Boolean> insertThumb(final Thumb thumb) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Db.ThumbTable.COLUMN_UID, thumb.uid());
                contentValues.put(Db.ThumbTable.COLUMN_COMMENT_ID, thumb.commentId());
                contentValues.put(Db.ThumbTable.COLUMN_ACTION, thumb.getAction());
                long result = mDb.insert(Db.ThumbTable.TABLE_NAME, contentValues);
                subscriber.onNext(result > 0);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Boolean> updateThumb(final Thumb thumb) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                Cursor cursor = mDb.query(
                        "UPDATE " + Db.ThumbTable.TABLE_NAME
                                + " SET " + Db.ThumbTable.COLUMN_ACTION + " = ? "
                                + " WHERE " + Db.ThumbTable.COLUMN_UID + " = ? "
                                + " AND " + Db.ThumbTable.COLUMN_COMMENT_ID + " = ?",
                        String.valueOf(thumb.getAction()), String.valueOf(thumb.uid()),
                        String.valueOf(thumb.commentId()));
                if (cursor.moveToNext()) {
                    subscriber.onNext(cursor.getInt(0) > 0);
                }
                cursor.close();
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Boolean> deleteThumb(final int uid, final int id) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                long result = mDb.delete(Db.ThumbTable.TABLE_NAME,
                        Db.ThumbTable.COLUMN_UID + " = ? AND "
                                + Db.ThumbTable.COLUMN_COMMENT_ID + " = ?",
                        String.valueOf(uid), String.valueOf(id));
                subscriber.onNext(result > 0);
                subscriber.onCompleted();
            }
        });
    }
    public Boolean deleteUidThumb(final int uid) {
        BriteDatabase.Transaction transaction = mDb.newTransaction();
        try {
            mDb.delete(Db.ThumbTable.TABLE_NAME,
                Db.ThumbTable.COLUMN_UID + " = ?", String.valueOf(uid));
            transaction.markSuccessful();
        } catch (Exception var1) {
            return false;
        } finally {
            transaction.end();
        }
        return true;
    }
    public Boolean deleteUidArticleRecord(final int uid) {
        BriteDatabase.Transaction transaction = mDb.newTransaction();
        try {
            mDb.delete(Db.ArticleRecordTable.TABLE_NAME,
                Db.ArticleRecordTable.COLUMN_UID + " = ?", String.valueOf(uid));
            transaction.markSuccessful();
        } catch (Exception var1) {
            return false;
        } finally {
            transaction.end();
        }
        return true;
    }

    public Boolean deleteUidVoaSound(final int uid) {
        BriteDatabase.Transaction transaction = mDb.newTransaction();
        try {
            mDb.delete(Db.VoaSoundTable.TABLE_NAME,
                Db.VoaSoundTable.COLUMN_UID + " = ?", String.valueOf(uid));
            transaction.markSuccessful();
        } catch (Exception var1) {
            return false;
        } finally {
            transaction.end();
        }
        return true;
    }

    public boolean isTrial(Voa voa){
        int tempVoaId = voa.voaId()-3;
        int series = 0 ;
        String sql = String.format("SELECT SERIES from VOA WHERE VOAID = (%s)", tempVoaId);
        Cursor cursor = mDb.query(sql);
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            series = cursor.getInt(0);
        }
        return series != voa.series();
    }

    public Observable<Voa> getVoasByCategoryNotWith(Boolean refreshSimple, String category, int limit, String ids) {

        String selectSeries;
        if (ids.contains(",")) {
            selectSeries = String.format("SELECT SERIES from VOA WHERE VOAID IN (%s)", ids);
        } else {
            selectSeries = "0";
        }
        final String selectSeriesSql = String.format(
                "SELECT * FROM VOA WHERE VOAID IN ( " +
                        "  SELECT  VOAID  FROM VOA WHERE CATEGORY in (%s) AND VOAID NOT IN (%s)) AND SERIES NOT IN (%s)" +
                        "   " +
                        " ORDER BY RANDOM() LIMIT %s; ", category, ids, selectSeries, 1);
        Log.e("category sql", selectSeriesSql);


        final String sql = String.format("SELECT * FROM VOA WHERE VOAID IN ( " +
                "  SELECT  VOAID  FROM VOA WHERE CATEGORY in (%s) AND VOAID NOT IN (%s) " +
                ") " +
                " ORDER BY RANDOM() LIMIT %s; ", category, ids, limit);
        Log.e("category sql", sql);

        return Observable.create(new Action1<Emitter<Voa>>() {
            @Override
            public void call(Emitter<Voa> emitter) {
                Cursor cursor1 = mDb.query(selectSeriesSql);
                int count = 0;

                Cursor cursor = mDb.query(sql);
                while (cursor.moveToNext()) {
//                    Timber.e("---");
                    emitter.onNext(Db.VoaTable.parseCursor(cursor));
                    if (count++ == 2) {
                        break;
                    }
                }
                if (cursor1.getCount() > 0) {
                    Log.d("com.iyuba.talkshow", "count:" + cursor1.getCount() + "");
                    cursor1.moveToNext();
                    emitter.onNext(Db.VoaTable.parseCursor(cursor1));
                } else {
                    cursor.moveToNext();
                    emitter.onNext(Db.VoaTable.parseCursor(cursor));
                }
                cursor.close();
                cursor1.close();
                emitter.onCompleted();
            }
        }, Emitter.BackpressureMode.NONE);
    }

    public Observable<Voa> getVoaByBothOne(int category, String level, int pageNum, int pageSize) {
        int offSize = (pageNum - 1) * pageSize;

        return Observable.create(emitter -> {
            String sql = "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                    + " WHERE (" + SqlUtil.handleIn(Db.VoaTable.COLUMN_HOT_FLG, level.split(Level.Value.SEP))
                    + ") AND " + Db.VoaTable.COLUMN_CATEGORY + " = ? "
                    + " order by " + Db.VoaTable.COLUMN_CREATE_TIME + " desc "
                    + " LIMIT ?, ?";
            Cursor cursor = mDb.query(sql, String.valueOf(category), String.valueOf(offSize), String.valueOf(pageSize));
            while (cursor.moveToNext()) {
//                Timber.e("---");
                emitter.onNext(Db.VoaTable.parseCursor(cursor));
            }
            cursor.close();
            emitter.onCompleted();
        }, Emitter.BackpressureMode.NONE);
    }

    public Observable<Voa> getVoasBySeries(int category) {

        return Observable.create(emitter -> {
            String sql = "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                    + " WHERE " + Db.VoaTable.COLUMN_SERIES + " = ? "
                    + " order by " + Db.VoaTable.COLUMN_VOA_ID + " asc ";
            Cursor cursor = mDb.query(sql, String.valueOf(category));
            while (cursor.moveToNext()) {
//                Timber.e("---");
                emitter.onNext(Db.VoaTable.parseCursor(cursor));
            }
            cursor.close();
            emitter.onCompleted();
        }, Emitter.BackpressureMode.NONE);
    }

    public List<SeriesData> getSeriesId(int seriesId) {
        Log.e("DatabaseHelper", "getSeriesId seriesId = " + seriesId);
        List<SeriesData> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.SeriesTable.TABLE_NAME
                + " WHERE " + Db.SeriesTable.COLUMN_SERIES_ID + " = " + seriesId;
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            SeriesData record = Db.SeriesTable.parseCursor(cursor);
            list.add(record);
        }
        cursor.close();
        return list;
    }

    public Observable<SeriesData> getSeriesById(int seriesId) {
        Timber.e("---id1");
        return Observable.create(emitter -> {
            String sql = "SELECT * FROM " + Db.SeriesTable.TABLE_NAME
                    + " WHERE " + Db.SeriesTable.COLUMN_SERIES_ID + " = " + seriesId;
            Cursor cursor = mDb.query(sql);
            while (cursor.moveToNext()) {
//                Timber.e("---id");
                emitter.onNext(Db.SeriesTable.parseCursor(cursor));
            }
            cursor.close();
            emitter.onCompleted();
        }, Emitter.BackpressureMode.NONE);
    }

    public List<SeriesData> getAllSeries() {
        List<SeriesData> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.SeriesTable.TABLE_NAME
                + " ORDER BY " + Db.SeriesTable.COLUMN_SERIES_ID + " ASC ";
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            SeriesData record = Db.SeriesTable.parseCursor(cursor);
            list.add(record);
        }
        cursor.close();
        return list;
    }

    public Observable<List<SeriesData>> getAllLocalSeries() {
        Timber.e("getAllLocalSeries ---id1");
        return mDb.createQuery(Db.SeriesTable.TABLE_NAME,
                "SELECT * FROM " + Db.SeriesTable.TABLE_NAME
                        + " ORDER BY " + Db.SeriesTable.COLUMN_SERIES_ID + " ASC ")
                .mapToList(new Func1<Cursor, SeriesData>() {
                    @Override
                    public SeriesData call(Cursor cursor) {
                        return Db.SeriesTable.parseCursor(cursor);
                    }
                });
    }

    public Observable<List<Voa>> getVoasBySeriesId(String seriesId) {
        return mDb.createQuery(Db.VoaTable.TABLE_NAME,
                "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                        + " WHERE " + Db.VoaTable.COLUMN_SERIES + " = ? "
                        + " ORDER BY " + Db.VoaTable.COLUMN_VOA_ID + " ASC "
                , seriesId)
                .mapToList(new Func1<Cursor, Voa>() {
                    @Override
                    public Voa call(Cursor cursor) {
                        return Db.VoaTable.parseCursor(cursor);
                    }
                });
    }

    public Observable<List<Voa>> getXiaoxueByBookId(int seriesId) {
        Timber.e("---id1");
        List<Voa> list = new ArrayList<>();
        return Observable.create(emitter -> {

            String sql = "";
            if (seriesId>=450){
                sql = "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                        + " WHERE " + Db.VoaTable.COLUMN_SERIES + " =  " + seriesId
                        + " ORDER BY " + Db.VoaTable.COLUMN_VOA_ID + " ASC ";
            }else {
                sql = "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                        + " WHERE " + Db.VoaTable.COLUMN_SERIES + " =  " + seriesId
                        + " ORDER BY " + Db.VoaTable.COLUMN_PUBLISH_TIME + " ASC ";
            }
            Cursor cursor = mDb.query(sql);
            while (cursor.moveToNext()) {
//                Timber.e("---id");
                list.add(Db.VoaTable.parseCursor(cursor));
            }
            emitter.onNext(list);
            cursor.close();
            emitter.onCompleted();
        }, Emitter.BackpressureMode.NONE);
    }

    public List<Integer> getXiaoxueVoaIdsByBookId(int seriesId) {
        Timber.e("---id1");
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT DISTINCT voaId FROM " + Db.VoaTable.TABLE_NAME
                + " WHERE " + Db.VoaTable.COLUMN_SERIES + " =  " + seriesId
                + " ORDER BY " + Db.VoaTable.COLUMN_VOA_ID + " ASC ";
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
//            Timber.e("---id");
            list.add(cursor.getInt(0));
        }
        return list;
    }

    public List<Voa> getVoaXiaoxueByBookId(int bookId) {
        Log.e("DatabaseHelper", "getVoaXiaoxueByBookId ---  ");
        List<Voa> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                + " WHERE " + Db.VoaTable.COLUMN_SERIES + " =  " + bookId
                + " ORDER BY " + Db.VoaTable.COLUMN_VOA_ID + " ASC ";
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            list.add(Db.VoaTable.parseCursor(cursor));
        }
        return list;
    }

    public List<Voa> getVoaByVoaId(int vaoId) {
        List<Voa> list = new ArrayList<>();
        String sql = "SELECT * FROM " + Db.VoaTable.TABLE_NAME
                + " WHERE " + Db.VoaTable.COLUMN_VOA_ID + " =  " + vaoId;
        Cursor cursor = mDb.query(sql);
        while (cursor.moveToNext()) {
            list.add(Db.VoaTable.parseCursor(cursor));
        }
        return list;
    }

    public Observable<List<SeriesData>> getSeriesListFromIds(List<Integer> idList) {
        StringBuffer queryString = new StringBuffer("(");
        for (int i : idList) {
            queryString.append(i);
            queryString.append(",");
        }
        queryString.delete(queryString.length() - 1, queryString.length());
        queryString.append(")");
        Log.d("TAG", "getSeriesListFromIds: " + queryString);
        return Observable.create(new Observable.OnSubscribe<SeriesData>() {
            @Override
            public void call(Subscriber<? super SeriesData> subscriber) {
                Cursor cursor = mDb.query(
                        "SELECT * FROM " + Db.SeriesTable.TABLE_NAME
                                + " WHERE " + Db.SeriesTable.COLUMN_SERIES_ID + " in "
                                + queryString
                );
                if (cursor.getCount() == 0) {
                    subscriber.onNext(null);
                }
                else {
                    while (cursor.moveToNext()) {
                        subscriber.onNext(Db.SeriesTable.parseCursor(cursor));
                    }
                }

                cursor.close();
                subscriber.onCompleted();
            }
        }).toList();
//        while (cursor.moveToNext()) {
//            subscriber.onNext(Db.VoaTable.parseCursor(cursor));
//        }
//        cursor.close();
//        subscriber.onCompleted();

//                .mapToList(new Func1<Cursor, SeriesData>() {
//                    @Override
//                    public SeriesData call(Cursor cursor) {
//                        return Db.SeriesTable.parseCursor(cursor);
//                    }
//                });
    }

    /****本地搜索****/
    //查询单词
    public List<TalkShowWords> searchWordFromDB(String keyWord) {
        List<TalkShowWords> tempList = new ArrayList<>();
        if (TextUtils.isEmpty(keyWord)) {
            return tempList;
        }

        tempList = WordDataBase.getInstance(TalkShowApplication.getInstance()).getTalkShowWordsDao().searchWords(keyWord);
        return tempList;
    }

    //查询单词
    public List<TalkShowWords> searchWordFromDB(int voaId){
        List<TalkShowWords> tempList = new ArrayList<>();
        if (voaId == 0) {
            return tempList;
        }

        tempList = WordDataBase.getInstance(TalkShowApplication.getInstance()).getTalkShowWordsDao().searchWords(voaId);
        return tempList;
    }

    //查询文章
    public List<Voa> searchVoaFromDB(String keyWord){
        List<Voa> tempList = new ArrayList<>();
        if (TextUtils.isEmpty(keyWord)) {
            return tempList;
        }

        String sql = "select * from "+Db.VoaTable.TABLE_NAME+" where "+Db.VoaTable.COLUMN_TITLE_CN+" like ? order by "+Db.VoaTable.COLUMN_VOA_ID+" asc";
        Cursor cursor = mDb.query(sql,"%"+keyWord+"%");

        while (cursor.moveToNext()){
            Voa voa = Db.VoaTable.parseCursor(cursor);
            if (voa!=null){
                tempList.add(voa);
            }
        }
        cursor.close();

        return tempList;
    }

    //查询句子
    public List<VoaText> searchSentenceFromDB(String keyWord,int voaId){
        List<VoaText> tempList = new ArrayList<>();
        if (TextUtils.isEmpty(keyWord)){
            return tempList;
        }

        String sql = "select * from "+Db.VoaTextTable.TABLE_NAME+" where "+Db.VoaTextTable.COLUMN_SENTENCE+" like ? order by "+Db.VoaTextTable.COLUMN_VOA_ID+" asc";
        Cursor cursor = mDb.query(sql,"%"+keyWord+"%");

        while (cursor.moveToNext()){
            VoaText voaText = Db.VoaTextTable.parseCursor(cursor,voaId);
            if (voaText!=null){
                tempList.add(voaText);
            }
        }
        cursor.close();
        return tempList;
    }

    //查询句子
    public List<VoaText> searchSentenceFromDB(String keyWord){
        List<VoaText> tempList = new ArrayList<>();
        if (TextUtils.isEmpty(keyWord)){
            return tempList;
        }

        String sql = "select * from "+Db.VoaTextTable.TABLE_NAME+" where "+Db.VoaTextTable.COLUMN_SENTENCE+" like ? order by "+Db.VoaTextTable.COLUMN_VOA_ID+" asc";
        Cursor cursor = mDb.query(sql,"%"+keyWord+"%");

        while (cursor.moveToNext()){
            VoaText voaText = Db.VoaTextTable.parseCursor(cursor);
            if (voaText!=null){
                tempList.add(voaText);
            }
        }
        cursor.close();
        return tempList;
    }
}
