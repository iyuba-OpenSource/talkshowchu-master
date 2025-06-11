package com.iyuba.talkshow.lil.help_fix.model.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * 中小学英语的配音操作辅助功能
 */
@Entity(primaryKeys = {"itemId","userId"})
public class JuniorDubbingHelpEntity {

    @NonNull
    public long itemId;//id(voaId+""+paraId+""+idIndex转换成long)
    @NonNull
    public int userId;


    @NonNull
    public long recordTime;//录音时间
    public String sentence;
    @NonNull
    public double scores;
    @NonNull
    public double total_score;
    public String filepath;
    public String url;
    public String wordList;

    public JuniorDubbingHelpEntity() {
    }

    @Ignore
    public JuniorDubbingHelpEntity(long itemId, int userId, long recordTime, String sentence, double scores, double total_score, String filepath, String url, String wordList) {
        this.itemId = itemId;
        this.userId = userId;
        this.recordTime = recordTime;
        this.sentence = sentence;
        this.scores = scores;
        this.total_score = total_score;
        this.filepath = filepath;
        this.url = url;
        this.wordList = wordList;
    }
}
