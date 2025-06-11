package com.iyuba.wordtest.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = {"voaId", "paraId","idIndex"})
public class TalkshowTexts {

    public int voaId ;

    @NonNull
    @SerializedName(value = "ParaId")
    public int paraId ;

    @NonNull
    @SerializedName(value = "IdIndex")
    public String idIndex ;

    @SerializedName(value = "sentence_cn")
    public String sentenceCn;

    @SerializedName(value = "Sentence")
    public String sentence ;

    @SerializedName(value = "ImgWords")
    public String imgWords ;

    @SerializedName(value = "ImgPath")
    public String imgPath ;

    @SerializedName(value = "Timing")
    public String timing ;

    @SerializedName(value = "EndTiming")
    public String endTiming ;

    @NonNull
    @Override
    public String toString() {
        return
                "[voaid]:"+voaId+
                "[paraId]:"+paraId+
                "[idIndex]:"+idIndex+
                "[sentenceCn]:"+sentenceCn+
//                "[sentencePron]:"+sentencePron+
                "[sentence]:"+sentence+
                "[imgWords]:"+imgWords+
                "[imgPath]:"+imgPath+
                "[timing]:"+timing+
                "[endTiming]:"+endTiming;
    }
}
