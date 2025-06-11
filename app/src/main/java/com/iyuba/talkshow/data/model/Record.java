package com.iyuba.talkshow.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class Record implements Parcelable {
    @SerializedName("timestamp")
    public abstract long timestamp();
    @SerializedName("VoaId")
    public abstract int voaId();
    @SerializedName("title")
    public abstract String title();
    @SerializedName("titleCn")
    public abstract String titleCn();
    @SerializedName("Img")
    public abstract String img();
    @SerializedName("totalNum")
    public abstract int totalNum();
    @SerializedName("finishNum")
    public abstract int finishNum();
    @SerializedName("date")
    public abstract String date();
    @SerializedName("score")
    public abstract String score();
    @SerializedName("audio")
    public abstract String audio();

    public static Builder builder() {
        return new AutoValue_Record.Builder();
    }

    public static TypeAdapter<Record> typeAdapter(Gson gson) {
        return new AutoValue_Record.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setTimestamp(long timestamp);
        public abstract Builder setVoaId(int voaId);
        public abstract Builder setTitle(String title);
        public abstract Builder setTitleCn(String titleCn);
        public abstract Builder setImg(String img);
        public abstract Builder setTotalNum(int totalNum);
        public abstract Builder setFinishNum(int finishNum);
        public abstract Builder setDate(String date);
        public abstract Builder setScore(String score);
        public abstract Builder setAudio(String audio);
        public abstract Record build();
    }

}
