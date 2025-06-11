package com.iyuba.talkshow.lil.help_fix.model.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 章节详情表-小说
 * @date: 2023/5/7 22:05
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Entity(primaryKeys = {"types","voaid","paraid","indexId"})
public class ChapterDetailEntity_novel {

    //本地标记-类型信息
    @NonNull
    public String types;
    @NonNull
    public long voaid;
    @NonNull
    public int paraid;
    @NonNull
    public int indexId;

    public String BeginTiming;
    public String chapter_order;
    public String EndTiming;
    public String image;
    public String orderNumber;
    public String sentence_audio;
    @NonNull
    public int level;
    public String textCH;
    public String textEN;

    public ChapterDetailEntity_novel() {

    }

    @Ignore
    public ChapterDetailEntity_novel(String beginTiming, long voaid, String chapter_order, int paraid, String endTiming, String image, String orderNumber, String sentence_audio, int level, int indexId, String textCH, String textEN, @NonNull String types) {
        BeginTiming = beginTiming;
        this.voaid = voaid;
        this.chapter_order = chapter_order;
        this.paraid = paraid;
        EndTiming = endTiming;
        this.image = image;
        this.orderNumber = orderNumber;
        this.sentence_audio = sentence_audio;
        this.level = level;
        this.indexId = indexId;
        this.textCH = textCH;
        this.textEN = textEN;
        this.types = types;
    }
}
