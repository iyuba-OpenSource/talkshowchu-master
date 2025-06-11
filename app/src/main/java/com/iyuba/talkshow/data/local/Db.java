package com.iyuba.talkshow.data.local;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iyuba.module.toolbox.GsonUtils;
import com.iyuba.talkshow.data.model.ArticleRecord;
import com.iyuba.talkshow.data.model.Collect;
import com.iyuba.talkshow.data.model.Download;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.data.model.Thumb;
import com.iyuba.talkshow.data.model.University;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaBookText;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.lil.help_fix.util.FixUtil;
import com.iyuba.talkshow.newdata.EvaluateBean;

import java.util.ArrayList;


public class Db {

    public Db() {
    }

    abstract static class VoaTable {
        static final String TABLE_NAME = "voa";

        static final String COLUMN_VOA_ID = "voaId";
        static final String COLUMN_INTRO_DESC = "introDesc";
        static final String COLUMN_CREATE_TIME = "createTime";
        static final String COLUMN_CATEGORY = "category";
        static final String COLUMN_KEYWORD = "keyword";
        static final String COLUMN_TITLE = "title";
        static final String COLUMN_SOUND = "sound";
        static final String COLUMN_PIC = "pic";
        static final String COLUMN_PAGE_TITLE = "pageTitle";
        static final String COLUMN_URL = "url";
        static final String COLUMN_DESC_CN = "descCn";
        static final String COLUMN_TITLE_CN = "titleCn";
        static final String COLUMN_PUBLISH_TIME = "publishTime";
        static final String COLUMN_HOT_FLG = "hotFlg";
        static final String COLUMN_READ_COUNT = "readCount";
        static final String COLUMN_CLICK_READ = "clickRead";
        static final String COLUMN_TIME_TOTAL = "totalTime";
        static final String COLUMN_PERCENT_ID = "percentId";
        static final String COLUMN_OUTLINE_ID = "outlineId";
        static final String COLUMN_PACKAGE_ID = "packageId";
        static final String COLUMN_CATEGORY_ID = "categoryId";
        static final String COLUMN_CLASS_ID = "classId";
        static final String COLUMN_SERIES = "series";

        // TODO: 2022/7/13 增加video字段
        static final String COLUMN_VIDEO = "video";

        static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_VOA_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_INTRO_DESC + " TEXT, " +
                        COLUMN_CREATE_TIME + " TEXT, " +
                        COLUMN_CATEGORY + " INTEGER, " +
                        COLUMN_KEYWORD + " TEXT, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_SOUND + " TEXT, " +
                        COLUMN_PIC + " TEXT, " +
                        COLUMN_PAGE_TITLE + " TEXT, " +
                        COLUMN_URL + " TEXT, " +
                        COLUMN_DESC_CN + " TEXT, " +
                        COLUMN_TITLE_CN + " TEXT, " +
                        COLUMN_PUBLISH_TIME + " TEXT, " +
                        COLUMN_HOT_FLG + " INTEGER, " +
                        COLUMN_READ_COUNT + " INTEGER, " +
                        COLUMN_CLICK_READ + " INTEGER, " +
                        COLUMN_TIME_TOTAL + " INTEGER, " +
                        COLUMN_PERCENT_ID + " INTEGER, " +
                        COLUMN_OUTLINE_ID + " INTEGER, " +
                        COLUMN_PACKAGE_ID + " INTEGER, " +
                        COLUMN_CATEGORY_ID + " INTEGER, " +
                        COLUMN_CLASS_ID + " TEXT, " +
                        COLUMN_SERIES + " INTEGER, " +
                        //增加video字段
                        COLUMN_VIDEO + " TEXT" +
                        " ); ";

        static ContentValues toContentValues(Voa voa) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VOA_ID, voa.voaId());
            values.put(COLUMN_INTRO_DESC, voa.introDesc());
            values.put(COLUMN_CREATE_TIME, voa.createTime());
            values.put(COLUMN_CATEGORY, voa.category());
            values.put(COLUMN_KEYWORD, voa.keyword());
            values.put(COLUMN_TITLE, voa.title());
            values.put(COLUMN_SOUND, voa.sound());
            values.put(COLUMN_PIC, voa.pic());
            values.put(COLUMN_PAGE_TITLE, voa.pageTitle());
            values.put(COLUMN_URL, voa.url());
            values.put(COLUMN_DESC_CN, voa.descCn());
            values.put(COLUMN_TITLE_CN, voa.titleCn());
            values.put(COLUMN_PUBLISH_TIME, voa.publishTime());
            values.put(COLUMN_HOT_FLG, voa.hotFlag());
            values.put(COLUMN_READ_COUNT, voa.readCount());
            values.put(COLUMN_CLICK_READ, voa.clickRead());
            values.put(COLUMN_TIME_TOTAL, voa.totalTime());
            values.put(COLUMN_PERCENT_ID, voa.percentId());
            values.put(COLUMN_OUTLINE_ID, voa.outlineId());
            values.put(COLUMN_PACKAGE_ID, voa.packageId());
            values.put(COLUMN_CATEGORY_ID, voa.categoryId());
            values.put(COLUMN_CLASS_ID, voa.classId());
            values.put(COLUMN_SERIES, voa.series());
            //增加video字段
            values.put(COLUMN_VIDEO,voa.video());
            return values;
        }

        static Voa parseCursor(Cursor cursor) {
            //特殊符号处理下
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            title = FixUtil.transToStandardText(title);
            String titleCn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE_CN));
            titleCn = FixUtil.transToStandardText(titleCn);
            String descCn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESC_CN));
            descCn = FixUtil.transToStandardText(descCn);

            return Voa.builder()
                    .setVoaId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VOA_ID)))
                    .setIntroDesc(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INTRO_DESC)))
                    .setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATE_TIME)))
                    .setCategory(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)))
                    .setKeyword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KEYWORD)))
                    .setTitle(title)
                    .setSound(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SOUND)))
                    .setPic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PIC)))
                    .setPageTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAGE_TITLE)))
                    .setUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL)))
                    .setDescCn(descCn)
                    .setTitleCn(titleCn)
                    .setPublishTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUBLISH_TIME)))
                    .setHotFlag(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HOT_FLG)))
                    .setReadCount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_READ_COUNT)))
                    .setClickRead(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CLICK_READ)))
                    .setTotalTime(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIME_TOTAL)))
                    .setPercentId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERCENT_ID)))
                    .setOutlineId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_OUTLINE_ID)))
                    .setPackageId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PACKAGE_ID)))
                    .setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)))
                    .setClassId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_ID)))
                    .setSeries(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SERIES)))
                    //增加video字段
                    .setVideo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO)))
                    .build();
        }
    }


    //这个表中的部分字段不正确，需要重新创建新的替换
    /*abstract static class VoaTextTable {
        static final String TABLE_NAME = "voaText";

        static final String COLUMN_VOA_ID = "voaId";
        static final String COLUMN_PARA_ID = "paraId";
        static final String COLUMN_ID_INDEX = "idIndex";
        static final String COLUMN_START_X = "startX";
        static final String COLUMN_START_Y = "startY";
        static final String COLUMN_END_X = "endX";
        static final String COLUMN_END_Y = "endY";
        static final String COLUMN_SENTENCE_CN = "sentenceCn";
        static final String COLUMN_SENTENCE = "sentence";
        static final String COLUMN_IMG_WORDS = "imgWords";
        static final String COLUMN_IMG_PATH = "imgPath";
        static final String COLUMN_TIMING = "timing";
        static final String COLUMN_END_TIMING = "endTiming";

        static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_VOA_ID + " INTEGER, " +
                        COLUMN_PARA_ID + " INTEGER, " +
                        COLUMN_ID_INDEX + " TEXT, " +
                        COLUMN_START_X + " INTEGER, " +
                        COLUMN_START_Y + " INTEGER, " +
                        COLUMN_END_X + " INTEGER, " +
                        COLUMN_END_Y + " INTEGER, " +
                        COLUMN_SENTENCE_CN + " TEXT, " +
                        COLUMN_SENTENCE + " TEXT, " +
                        COLUMN_IMG_WORDS + " TEXT, " +
                        COLUMN_IMG_PATH + " TEXT, " +
                        COLUMN_TIMING + " TEXT, " +
                        COLUMN_END_TIMING + " TEXT" +
                        ", PRIMARY KEY (" + COLUMN_VOA_ID + "," + COLUMN_PARA_ID + "," + COLUMN_ID_INDEX + ")" +
                        " ); ";

        static ContentValues toContentValues(VoaText voaText, String voaId) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VOA_ID, voaId);
            values.put(COLUMN_PARA_ID, voaText.paraId());
            values.put(COLUMN_ID_INDEX, voaText.idIndex());
            values.put(COLUMN_START_X, voaText.startX());
            values.put(COLUMN_START_Y, voaText.startY());
            values.put(COLUMN_END_X, voaText.endX());
            values.put(COLUMN_END_Y, voaText.endY());
            values.put(COLUMN_SENTENCE_CN, voaText.sentenceCn());
            values.put(COLUMN_SENTENCE, voaText.sentence());
            values.put(COLUMN_IMG_WORDS, voaText.imgWords());
            values.put(COLUMN_IMG_PATH, voaText.imgPath());
            values.put(COLUMN_TIMING, voaText.timing());
            values.put(COLUMN_END_TIMING, voaText.endTiming());
            return values;
        }
        static ContentValues toContentValues(VoaBookText voaText) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VOA_ID, voaText.voaid);
            values.put(COLUMN_PARA_ID, voaText.ParaId);
            values.put(COLUMN_ID_INDEX, voaText.IdIndex);
            values.put(COLUMN_START_X, voaText.Start_x);
            values.put(COLUMN_START_Y, voaText.Start_y);
            values.put(COLUMN_END_X, voaText.End_x);
            values.put(COLUMN_END_Y, voaText.End_y);
            values.put(COLUMN_SENTENCE_CN, voaText.sentence_cn);
            values.put(COLUMN_SENTENCE, voaText.Sentence);
            values.put(COLUMN_IMG_WORDS, voaText.ImgWords);
            values.put(COLUMN_IMG_PATH, voaText.ImgPath);
            values.put(COLUMN_TIMING, voaText.Timing);
            values.put(COLUMN_END_TIMING, voaText.EndTiming);
            return values;
        }

        static VoaText parseCursor(Cursor cursor, int voaId) {
            VoaText voaText = VoaText.builder()
                    .setParaId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PARA_ID)))
                    .setIdIndex(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_INDEX)))
                    .setStartX(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_START_X)))
                    .setStartY(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_START_Y)))
                    .setEndX(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_END_X)))
                    .setEndY(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_END_Y)))
                    .setSentenceCn(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENTENCE_CN)))
                    .setSentence(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENTENCE)))
                    .setImgWords(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMG_WORDS)))
                    .setImgPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMG_PATH)))
                    .setTiming(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_TIMING)))
                    .setEndTiming(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_END_TIMING)))
                    .build();
            voaText.setVoaId(voaId);
            return voaText;
        }
    }*/
    abstract static class VoaTextTable {
        static final String TABLE_NAME = "voaText";

        static final String COLUMN_VOA_ID = "voaId";
        static final String COLUMN_PARA_ID = "paraId";
        static final String COLUMN_ID_INDEX = "idIndex";
        static final String COLUMN_START_X = "startX";
        static final String COLUMN_START_Y = "startY";
        static final String COLUMN_END_X = "endX";
        static final String COLUMN_END_Y = "endY";
        static final String COLUMN_SENTENCE_CN = "sentenceCn";
        static final String COLUMN_SENTENCE = "sentence";
        static final String COLUMN_IMG_WORDS = "imgWords";
        static final String COLUMN_IMG_PATH = "imgPath";
        static final String COLUMN_TIMING = "timing";
        static final String COLUMN_END_TIMING = "endTiming";

        static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_VOA_ID + " INTEGER, " +
                        COLUMN_PARA_ID + " INTEGER, " +
                        COLUMN_ID_INDEX + " INTEGER, " +
                        COLUMN_START_X + " INTEGER, " +
                        COLUMN_START_Y + " INTEGER, " +
                        COLUMN_END_X + " INTEGER, " +
                        COLUMN_END_Y + " INTEGER, " +
                        COLUMN_SENTENCE_CN + " TEXT, " +
                        COLUMN_SENTENCE + " TEXT, " +
                        COLUMN_IMG_WORDS + " TEXT, " +
                        COLUMN_IMG_PATH + " TEXT, " +
                        COLUMN_TIMING + " TEXT, " +
                        COLUMN_END_TIMING + " TEXT" +
                        ", PRIMARY KEY (" + COLUMN_VOA_ID + "," + COLUMN_PARA_ID + "," + COLUMN_ID_INDEX + ")" +
                        " ); ";

        static ContentValues toContentValues(VoaText voaText, String voaId) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VOA_ID, voaId);
            values.put(COLUMN_PARA_ID, voaText.paraId());
            values.put(COLUMN_ID_INDEX, voaText.idIndex());
            values.put(COLUMN_START_X, voaText.startX());
            values.put(COLUMN_START_Y, voaText.startY());
            values.put(COLUMN_END_X, voaText.endX());
            values.put(COLUMN_END_Y, voaText.endY());
            values.put(COLUMN_SENTENCE_CN, voaText.sentenceCn());
            values.put(COLUMN_SENTENCE, voaText.sentence());
            values.put(COLUMN_IMG_WORDS, voaText.imgWords());
            values.put(COLUMN_IMG_PATH, voaText.imgPath());
            values.put(COLUMN_TIMING, voaText.timing());
            values.put(COLUMN_END_TIMING, voaText.endTiming());
            return values;
        }
        static ContentValues toContentValues(VoaBookText voaText) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VOA_ID, voaText.voaid);
            values.put(COLUMN_PARA_ID, voaText.ParaId);
            values.put(COLUMN_ID_INDEX, voaText.IdIndex);
            values.put(COLUMN_START_X, voaText.Start_x);
            values.put(COLUMN_START_Y, voaText.Start_y);
            values.put(COLUMN_END_X, voaText.End_x);
            values.put(COLUMN_END_Y, voaText.End_y);
            values.put(COLUMN_SENTENCE_CN, voaText.sentence_cn);
            values.put(COLUMN_SENTENCE, voaText.Sentence);
            values.put(COLUMN_IMG_WORDS, voaText.ImgWords);
            values.put(COLUMN_IMG_PATH, voaText.ImgPath);
            values.put(COLUMN_TIMING, voaText.Timing);
            values.put(COLUMN_END_TIMING, voaText.EndTiming);
            return values;
        }

        static VoaText parseCursor(Cursor cursor, int voaId) {
            //转换特殊符号
            String sentence = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENTENCE));
            sentence = FixUtil.transToStandardText(sentence);
            String sentenceCn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENTENCE_CN));
            sentenceCn = FixUtil.transToStandardText(sentenceCn);

            VoaText voaText = VoaText.builder()
                    .setParaId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PARA_ID)))
                    .setIdIndex(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_INDEX)))
                    .setStartX(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_START_X)))
                    .setStartY(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_START_Y)))
                    .setEndX(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_END_X)))
                    .setEndY(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_END_Y)))
                    .setSentenceCn(sentenceCn)
                    .setSentence(sentence)
                    .setImgWords(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMG_WORDS)))
                    .setImgPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMG_PATH)))
                    .setTiming(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_TIMING)))
                    .setEndTiming(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_END_TIMING)))
                    .build();
            voaText.setVoaId(voaId);
            return voaText;
        }

        //已经存在的数据进行操作
        static VoaText parseCursor(Cursor cursor) {
            VoaText voaText = VoaText.builder()
                    .setParaId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PARA_ID)))
                    .setIdIndex(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_INDEX)))
                    .setStartX(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_START_X)))
                    .setStartY(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_START_Y)))
                    .setEndX(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_END_X)))
                    .setEndY(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_END_Y)))
                    .setSentenceCn(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENTENCE_CN)))
                    .setSentence(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENTENCE)))
                    .setImgWords(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMG_WORDS)))
                    .setImgPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMG_PATH)))
                    .setTiming(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_TIMING)))
                    .setEndTiming(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_END_TIMING)))
                    .build();
            voaText.setVoaId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VOA_ID)));
            return voaText;
        }
    }

    abstract static class RecordTable {
        static final String TABLE_NAME = "record";

        static final String COLUMN_ID = "id";
        static final String COLUMN_VOA_ID = "voaId";
        static final String COLUMN_TITLE = "title";
        static final String COLUMN_TITLE_CN = "title_cn";
        static final String COLUMN_IMG = "img";
        static final String COLUMN_TIMESTAMP = "timestamp";
        static final String COLUMN_TOTAL_NUM = "totalNum";
        static final String COLUMN_FINISH_NUM = "finishNum";
        static final String COLUMN_DATE = "date";
        static final String COLUMN_SCORE = "score";
        static final String COLUMN_AUDIO = "audio";

        static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_TIMESTAMP + " LONG PRIMARY KEY, " +
                        COLUMN_ID + " INTEGER, " +
                        COLUMN_VOA_ID + " INTEGER, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_TITLE_CN + " TEXT, " +
                        COLUMN_IMG + " TEXT, " +
                        COLUMN_DATE + " TEXT, " +
                        COLUMN_TOTAL_NUM + " INTEGER, " +
                        COLUMN_SCORE + " Text, " +
                        COLUMN_FINISH_NUM + " INTEGER, " +
                        COLUMN_AUDIO + " TEXT" +
                        " ); ";

        static ContentValues toContentValues(Record record) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VOA_ID, record.voaId());
            values.put(COLUMN_IMG, record.img());
            values.put(COLUMN_TITLE, record.title());
            values.put(COLUMN_TITLE_CN, record.titleCn());
            values.put(COLUMN_TIMESTAMP, record.timestamp());
            values.put(COLUMN_TOTAL_NUM, record.totalNum());
            values.put(COLUMN_FINISH_NUM, record.finishNum());
            values.put(COLUMN_DATE, record.date());
            values.put(COLUMN_SCORE, record.score());
            values.put(COLUMN_AUDIO, record.audio());
            return values;
        }

        static Record parseCursor(Cursor cursor) {
            return Record.builder()
                    .setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)))
                    .setVoaId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VOA_ID)))
                    .setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)))
                    .setTitleCn(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE_CN)))
                    .setImg(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMG)))
                    .setTotalNum(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_NUM)))
                    .setFinishNum(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FINISH_NUM)))
                    .setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)))
                    .setScore(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCORE)))
                    .setAudio(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUDIO)))
                    .build();
        }
    }

    abstract static class CollectTable {
        static final String TABLE_NAME = "collect";

        static final String COLUMN_UID = "uid";
        static final String COLUMN_VOA_ID = "voaId";
        static final String COLUMN_DATE = "date";

        static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_UID + " TEXT, " +
                        COLUMN_VOA_ID + " TEXT, " +
                        COLUMN_DATE + " TEXT" +
                        " ); ";

        static ContentValues toContentValues(Collect collect) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_UID, collect.uid());
            values.put(COLUMN_VOA_ID, collect.voaId());
            values.put(COLUMN_DATE, collect.date());
            return values;
        }

        static Collect parseCursor(Cursor cursor) {
            return Collect.builder()
                    .setUid(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_UID)))
                    .setVoaId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VOA_ID)))
                    .setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)))
                    .build();
        }
    }

    abstract static class DownloadTable {
        static final String TABLE_NAME = "download";

        static final String COLUMN_UID = "uid";
        static final String COLUMN_VOA_ID = "voaId";
        static final String COLUMN_DATE = "date";

        static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_UID + " TEXT, " +
                        COLUMN_VOA_ID + " TEXT, " +
                        COLUMN_DATE + " TEXT" +
                        " ); ";

        static ContentValues toContentValues(Download download) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_UID, download.uid());
            values.put(COLUMN_VOA_ID, download.voaId());
            values.put(COLUMN_DATE, download.date());
            return values;
        }

        static Download parseCursor(Cursor cursor) {
            return Download.builder()
                    .setUid(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_UID)))
                    .setVoaId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VOA_ID)))
                    .setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)))
                    .build();
        }
    }

    abstract static class UniversityTable {
        static final String TABLE_NAME = "university";

        static final String COLUMN_ID = "id";
        static final String COLUMN_UNI_ID = "uniId";
        static final String COLUMN_UNI_TYPE = "uniType";
        static final String COLUMN_UNI_NAME = "uniName";
        static final String COLUMN_PROVINCE = "province";

        static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_UNI_ID + " INTEGER, " +
                        COLUMN_UNI_TYPE + " INTEGER, " +
                        COLUMN_UNI_NAME + " TEXT, " +
                        COLUMN_PROVINCE + " TEXT" +
                        " ); ";

        static ContentValues toContentValues(University university) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, university.id());
            values.put(COLUMN_UNI_ID, university.uniId());
            values.put(COLUMN_UNI_TYPE, university.uniType());
            values.put(COLUMN_UNI_NAME, university.uniName());
            values.put(COLUMN_PROVINCE, university.province());
            return values;
        }

        static University parseCursor(Cursor cursor) {
            return University.builder()
                    .setId(cursor.getInt(cursor.getColumnIndexOrThrow(UniversityTable.COLUMN_ID)))
                    .setUniType(cursor.getInt(cursor.getColumnIndexOrThrow(UniversityTable.COLUMN_UNI_TYPE)))
                    .setUniId(cursor.getInt(cursor.getColumnIndexOrThrow(UniversityTable.COLUMN_UNI_ID)))
                    .setUniName(cursor.getString(cursor.getColumnIndexOrThrow(UniversityTable.COLUMN_UNI_NAME)))
                    .setProvince(cursor.getString(cursor.getColumnIndexOrThrow(UniversityTable.COLUMN_PROVINCE)))
                    .build();
        }

//        sql.append("create table if not exists university (id integer not null primary key, ")
//                .append("uni_id integer, province varchar(255), uni_type integer, uni_name varchar(255) )");
    }

    abstract static class ThumbTable {
        static final String TABLE_NAME = "thumb";

        static final String COLUMN_UID = "uid";
        static final String COLUMN_COMMENT_ID = "commentId";
        static final String COLUMN_ACTION = "action";

        static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_UID + " INTEGER, " +
                        COLUMN_COMMENT_ID + " INTEGER, " +
                        COLUMN_ACTION + " INTEGER" +
                        " ); ";

        static ContentValues toContentValues(Thumb thumb) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_UID, thumb.uid());
            values.put(COLUMN_COMMENT_ID, thumb.commentId());
            values.put(COLUMN_ACTION, thumb.getAction());
            return values;
        }

        static Thumb parseCursor(Cursor cursor) {
            Thumb thumb = Thumb.builder()
                    .setUid(cursor.getInt(cursor.getColumnIndexOrThrow(ThumbTable.COLUMN_UID)))
                    .setCommentId(cursor.getInt(cursor.getColumnIndexOrThrow(ThumbTable.COLUMN_COMMENT_ID)))
                    .build();
            thumb.setAction(cursor.getInt(cursor.getColumnIndexOrThrow(ThumbTable.COLUMN_ACTION)));
            return thumb;
        }
    }

    abstract static class SeriesTable {
        static final String TABLE_NAME = "series";

        static final String COLUMN_SERIES_ID = "series_id";
        static final String COLUMN_SERIES_NAME = "series_name";
        static final String COLUMN_SERIES_PIC = "series_pic";
        static final String COLUMN_SERIES_DESC = "series_desc";
        static final String COLUMN_SERIES_CREATETIME = "create_time";
        static final String COLUMN_SERIES_CATEGORY = "series_category";
        static final String COLUMN_SERIES_HOTFLAG = "series_hotflag";
        static final String COLUMN_SERIES_UPDATETIME = "series_updatetime";
        static final String COLUMN_SERIES_VIDEO = "series_video";
        static final String COLUMN_SERIES_MICRO = "series_micro";
        static final String COLUMN_SERIES_COUNT = "series_count";

        static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_SERIES_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_SERIES_VIDEO + " INTEGER, " +
                        COLUMN_SERIES_MICRO + " INTEGER, " +
                        COLUMN_SERIES_COUNT + " INTEGER, " +
                        COLUMN_SERIES_NAME + " TEXT, " +
                        COLUMN_SERIES_PIC + " TEXT, " +
                        COLUMN_SERIES_DESC + " TEXT," +
                        COLUMN_SERIES_CREATETIME + " TEXT," +
                        COLUMN_SERIES_CATEGORY + " TEXT," +
                        COLUMN_SERIES_HOTFLAG + " TEXT," +
                        COLUMN_SERIES_UPDATETIME + " TEXT" +
                        " ); ";

//        static ContentValues toContentValues(Voa voa) {
//            ContentValues values = new ContentValues();
//            values.put(COLUMN_SERIES_ID, voa.series());
//            values.put(COLUMN_SERIES_PIC, voa.pic());
//            values.put(COLUMN_SERIES_NAME, voa.getSeriesName());
//            values.put(COLUMN_SERIES_DESC, voa.getDescCn());
//            values.put(COLUMN_SERIES_CATEGORY, voa.getCategory());
//            values.put(COLUMN_SERIES_CREATETIME, voa.getCreateTime());
//            values.put(COLUMN_SERIES_HOTFLAG, voa.getHotFlg());
//            values.put(COLUMN_SERIES_UPDATETIME, voa.getUpdateTime());
//            values.put(COLUMN_SERIES_DESC, series.getDescCn());
//            values.put(COLUMN_SERIES_ID, series.getCreateTime());
//            values.put(COLUMN_SERIES_NAME, series.getDescCn());
//            values.put(COLUMN_SERIES_NAME, series.getDescCn());
//            values.put(COLUMN_SERIES_NAME, series.getDescCn());
//            values.put(COLUMN_SERIES_NAME, series.getDescCn());
//            return values;
//        }

        static ContentValues toContentValues(SeriesData series) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SERIES_ID, series.getId());
            values.put(COLUMN_SERIES_VIDEO, series.getVideo());
            values.put(COLUMN_SERIES_PIC, series.getPic());
            values.put(COLUMN_SERIES_NAME, series.getSeriesName());
            values.put(COLUMN_SERIES_DESC, series.getDescCn());
            values.put(COLUMN_SERIES_CATEGORY, series.getCategory());
            values.put(COLUMN_SERIES_CREATETIME, series.getCreateTime());
            values.put(COLUMN_SERIES_HOTFLAG, series.getHotFlg());
            values.put(COLUMN_SERIES_UPDATETIME, series.getUpdateTime());
            values.put(COLUMN_SERIES_MICRO, series.getHaveMicro());
            values.put(COLUMN_SERIES_COUNT, series.getSeriesCount());
//            values.put(COLUMN_SERIES_NAME, series.getDescCn());
//            values.put(COLUMN_SERIES_NAME, series.getDescCn());
//            values.put(COLUMN_SERIES_NAME, series.getDescCn());
//            values.put(COLUMN_SERIES_NAME, series.getDescCn());
            return values;
        }

        static SeriesData parseCursor(Cursor cursor) {
            SeriesData bean = new SeriesData();
            bean.setId(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_ID)));
            bean.setVideo(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_VIDEO)));
            bean.setHaveMicro(cursor.getInt(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_MICRO)));
            bean.setSeriesCount(cursor.getInt(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_COUNT)));
            bean.setSeriesName(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_NAME)));
            bean.setPic(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_PIC)));
            bean.setDescCn(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_DESC)));
            bean.setHotFlg(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_HOTFLAG)));
            bean.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_CREATETIME)));
            bean.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_CATEGORY)));
            bean.setUpdateTime(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_UPDATETIME)));
            return bean;
        }
    }

    abstract static class VoaSoundTable {
        static final String TABLE_NAME = "voa_sound_new";

        static final String COLUMN_ID = "id";
        static final String COLUMN_ITEMID = "itemid";
        static final String COLUMN_UID = "uid";
        static final String COLUMN_VOA_ID = "voa_id";
        static final String COLUMN_TOTALSCORE = "totalscore";
        static final String COLUMN_WORDSCORE = "wordscore";
        static final String COLUMN_FILEPATH = "filepath";
        static final String COLUMN_TIME = "time";
        static final String COLUMN_SOUNDURL = "sound_url";
        static final String COLUMN_WORDS = "words";
        static final String RVC = "rvc";

        static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_ITEMID + " INTEGER, " +
                        COLUMN_UID + " INTEGER, " +
                        COLUMN_VOA_ID + " INTEGER, " +
                        COLUMN_TOTALSCORE + " INTEGER, " +
                        COLUMN_WORDSCORE + " TEXT, " +
                        COLUMN_FILEPATH + " TEXT, " +
                        COLUMN_TIME + " TEXT, " +
                        RVC + " TEXT, " +
                        COLUMN_WORDS + " TEXT, " +
                        COLUMN_SOUNDURL + " TEXT " +
                        " ); ";

        static ContentValues toContentValues(VoaSoundNew record) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ITEMID, record.itemid());
            values.put(COLUMN_UID, record.uid());
            values.put(COLUMN_VOA_ID, record.voa_id());
            values.put(COLUMN_TOTALSCORE, record.totalscore());
            values.put(COLUMN_WORDSCORE, record.wordscore());
            values.put(COLUMN_FILEPATH, record.filepath());
            values.put(COLUMN_TIME, record.time());
            values.put(COLUMN_SOUNDURL, record.sound_url());
            values.put(COLUMN_WORDS, record.words());
            values.put(RVC, record.rvc());
            return values;
        }

        static VoaSoundNew parseCursor(Cursor cursor) {
            return VoaSoundNew.builder()
                    .setItemid(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEMID)))
                    .setUid(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_UID)))
                    .setVoa_id(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VOA_ID)))
                    .setTotalscore(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTALSCORE)))
                    .setWordscore(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORDSCORE)))
                    .setFilepath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILEPATH)))
                    .setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)))
                    .setSound_url(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SOUNDURL)))
                    .setWords(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORDS)))
                    .setRvc(cursor.getString(cursor.getColumnIndexOrThrow(RVC)))
                    .build();
        }
    }

    abstract static class ArticleRecordTable {
        static final String TABLE_NAME = "article_record";

        public static final String COLUMN_UID = "uid";
        public static final String COLUMN_VOA_ID = "voa_id";
        public static final String COLUMN_CURR_TIME = "curr_time";
        public static final String COLUMN_TOTAL_TIME = "total_time";
        public static final String COLUMN_IS_FINISH = "is_finish";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_PERCENT = "percent";

        static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_UID + " INTEGER, " +
                        COLUMN_VOA_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_CURR_TIME + " INTEGER, " +
                        COLUMN_TOTAL_TIME + " INTEGER, " +
                        COLUMN_IS_FINISH + " INTEGER, " +
                        COLUMN_TYPE + " INTEGER, " +
                        COLUMN_PERCENT + " INTEGER " +
                        " ); ";

        static ContentValues toContentValues(ArticleRecord record) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_UID, record.uid());
            values.put(COLUMN_VOA_ID, record.voa_id());
            values.put(COLUMN_CURR_TIME, record.curr_time());
            values.put(COLUMN_TOTAL_TIME, record.total_time());
            values.put(COLUMN_IS_FINISH, record.is_finish());
            values.put(COLUMN_TYPE, record.type());
            values.put(COLUMN_PERCENT, record.percent());
            return values;
        }

        static ArticleRecord parseCursor(Cursor cursor) {
            return ArticleRecord.builder()
                    .setUid(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_UID)))
                    .setVoa_id(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VOA_ID)))
                    .setCurr_time(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CURR_TIME)))
                    .setTotal_time(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_TIME)))
                    .setIs_finish(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FINISH)))
                    .setType(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TYPE)))
                    .setPercent(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERCENT)))
                    .build();
        }
    }

    abstract static class WordTable {
        static final String TABLE_NAME = "word";

        static final String COLUMN_VOA_ID = "voa_id";
        static final String COLUMN_BOOK_ID = "book_id";
        static final String COLUMN_UNIT_ID = "unit_id";
        static final String COLUMN_IDINDEX = "idindex";
        static final String COLUMN_PIC_URL = "pic_url";
        static final String COLUMN_WORD = "word";
        static final String COLUMN_DEF = "def";
        static final String COLUMN_PRON = "pron";
        static final String COLUMN_EXAMPLES = "examples";
        static final String COLUMN_AUDIO = "audio";
        static final String COLUMN_ANSWER = "answer";
        static final String COLUMN_VERSION = "version";
        static final String COLUMN_POSION = "position";
        static final String COLUMN_UPDATETIME = "updateTime";
        static final String COLUMN_FLAG = "flag";
//        static final String COLUMN_SERIES_DESC = "series_desc";

        static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_WORD + " TEXT PRIMARY KEY, " +
                        COLUMN_VOA_ID + " TEXT, " +
                        COLUMN_BOOK_ID + " TEXT, " +
                        COLUMN_UNIT_ID + " TEXT," +
                        COLUMN_IDINDEX + " TEXT," +
                        COLUMN_PIC_URL + " TEXT," +
                        COLUMN_DEF + " TEXT," +
                        COLUMN_PRON + " TEXT" +
                        COLUMN_EXAMPLES + " TEXT" +
                        COLUMN_AUDIO + " TEXT" +
                        COLUMN_ANSWER + " TEXT" +
                        COLUMN_VERSION + " TEXT" +
                        COLUMN_POSION + " TEXT" +
                        COLUMN_UPDATETIME + " TEXT" +
                        COLUMN_FLAG + " TEXT" +
                        " ); ";

//        static ContentValues toContentValues(Voa voa) {
//            ContentValues values = new ContentValues();
//            values.put(COLUMN_SERIES_ID, voa.series());
//            values.put(COLUMN_SERIES_PIC, voa.pic());
//            values.put(COLUMN_SERIES_NAME, voa.getSeriesName());
//            values.put(COLUMN_SERIES_DESC, voa.getDescCn());
//            values.put(COLUMN_SERIES_CATEGORY, voa.getCategory());
//            values.put(COLUMN_SERIES_CREATETIME, voa.getCreateTime());
//            values.put(COLUMN_SERIES_HOTFLAG, voa.getHotFlg());
//            values.put(COLUMN_SERIES_UPDATETIME, voa.getUpdateTime());
//            values.put(COLUMN_SERIES_DESC, series.getDescCn());
//            values.put(COLUMN_SERIES_ID, series.getCreateTime());
//            values.put(COLUMN_SERIES_NAME, series.getDescCn());
//            values.put(COLUMN_SERIES_NAME, series.getDescCn());
//            values.put(COLUMN_SERIES_NAME, series.getDescCn());
//            values.put(COLUMN_SERIES_NAME, series.getDescCn());
//            return values;
//        }

//        static ContentValues toContentValues(SeriesResponse.DataBean series) {
//            ContentValues values = new ContentValues();
//            values.put(COLUMN_SERIES_ID, series.getId());
//            values.put(COLUMN_SERIES_PIC, series.getPic());
//            values.put(COLUMN_SERIES_NAME, series.getSeriesName());
//            values.put(COLUMN_SERIES_DESC, series.getDescCn());
//            values.put(COLUMN_SERIES_CATEGORY, series.getCategory());
//            values.put(COLUMN_SERIES_CREATETIME, series.getCreateTime());
//            values.put(COLUMN_SERIES_HOTFLAG, series.getHotFlg());
//            values.put(COLUMN_SERIES_UPDATETIME, series.getUpdateTime());
////            values.put(COLUMN_SERIES_DESC, series.getDescCn());
////            values.put(COLUMN_SERIES_ID, series.getCreateTime());
////            values.put(COLUMN_SERIES_NAME, series.getDescCn());
////            values.put(COLUMN_SERIES_NAME, series.getDescCn());
////            values.put(COLUMN_SERIES_NAME, series.getDescCn());
////            values.put(COLUMN_SERIES_NAME, series.getDescCn());
//            return values;
//        }
//
//        static SeriesResponse.DataBean parseCursor(Cursor cursor) {
//            SeriesResponse.DataBean bean = new SeriesResponse.DataBean();
//            bean.setId(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_ID)));
//            bean.setSeriesName(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_NAME)));
//            bean.setPic(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_PIC)));
//            bean.setDescCn(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_DESC)));
//            bean.setHotFlg(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_HOTFLAG)));
//            bean.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_CREATETIME)));
//            bean.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_CATEGORY)));
//            bean.setUpdateTime(cursor.getString(cursor.getColumnIndexOrThrow(SeriesTable.COLUMN_SERIES_UPDATETIME)));
//            return bean;
//        }
    }
}
