package com.iyuba.wordtest.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.io.Serializable;

@Entity(primaryKeys = {"unit_id","book_id","position"})
public class TalkShowWords extends Words implements Serializable {

    @NonNull
    public String word;

    public int version ;
    public int position ;
    public String updateTime ;
    @NonNull
    public int voa_id ;
    @NonNull
    public int book_id ;
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

    @Override
    public String toString() {
        return "TalkShowWords{" +
                "word='" + word + '\'' +
                ", version=" + version +
                ", position=" + position +
                ", updateTime='" + updateTime + '\'' +
                ", voa_id=" + voa_id +
                ", book_id=" + book_id +
                ", unit_id=" + unit_id +
                ", idindex=" + idindex +
                ", pic_url='" + pic_url + '\'' +
                ", audio='" + audio + '\'' +
                ", examples='" + examples + '\'' +
                ", answer='" + answer + '\'' +
                ", pron='" + pron + '\'' +
                ", def='" + def + '\'' +
                ", flag=" + flag +
                ", Sentence='" + Sentence + '\'' +
                ", Sentence_cn='" + Sentence_cn + '\'' +
                ", Sentence_audio='" + Sentence_audio + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", wrong=" + wrong +
                '}';
    }
}
