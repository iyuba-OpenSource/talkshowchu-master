package com.iyuba.wordtest.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"bookId", "uid"})
public class NewBookLevels {

    @NonNull
    public int bookId ;
    public int level ;
    public int version ;
    public int download ;
    @NonNull
    public String uid ;

    public NewBookLevels(){

    }
    @Ignore
    public NewBookLevels(int bookId, int level , int version , int download) {
        this.bookId = bookId;
        this.level = level;
        this.version = version;
        this.download = download;
    }
    @Ignore
    public NewBookLevels(int bookId, int level , int version , int download, String uid) {
        this.bookId = bookId;
        this.level = level;
        this.version = version;
        this.download = download;
        this.uid = uid;
    }
}
