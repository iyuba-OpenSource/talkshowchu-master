package com.iyuba.wordtest.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.io.Serializable;

@Entity(primaryKeys = {"unit_id","book_id","position"})
public class TalkShowTests extends Words implements Serializable {

    @NonNull
    public String word;
    public String uid ;
    public int version ;
    public int position ;
    public String updateTime ;
    public int voa_id ;
    @NonNull
    public int book_id ;
    @NonNull
    public int unit_id ;
    public int idindex ;
    public String pic_url ;
    public String audio ;
    public String examples ;
    public String answer ;
    public String pron ;
    public String def ;
    public int flag ;

    public String Sentence;
    public String Sentence_cn;
    public String Sentence_audio;
    public String videoUrl;
    public int wrong;

}
