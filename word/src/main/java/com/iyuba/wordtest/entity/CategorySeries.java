package com.iyuba.wordtest.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"Category", "uid"})
public class CategorySeries {
    public int Category;
    public String SeriesName;
    public String lessonName;
    public String SourceType;
    public String lessonType = "";
    public String isVideo;
    public int uid;

    public CategorySeries(){
    }
    @Ignore
    public CategorySeries(int bookId, String name , String lesson , String type, String video , int down) {
        this.Category = bookId;
        this.SeriesName = name;
        this.lessonName = lesson;
        SourceType = type;
        this.isVideo = video;
        this.uid = down;
    }
    @Ignore
    public CategorySeries(int bookId, String name , String lesson , String video , int down) {
        this.Category = bookId;
        this.SeriesName = name;
        this.lessonName = lesson;
        this.isVideo = video;
        this.uid = down;
    }
}
