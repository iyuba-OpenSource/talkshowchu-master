package com.iyuba.wordtest.entity;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity
public class BookLevels {

    @PrimaryKey
    public int bookId ;
    public int level ;
    public int version ;
    public int download ;

    public BookLevels(){

    }
    @Ignore
    public BookLevels(int bookId, int level , int version , int download) {
        this.bookId = bookId;
        this.level = level;
        this.version = version;
        this.download = download;
    }
}
