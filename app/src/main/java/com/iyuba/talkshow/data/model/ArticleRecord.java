package com.iyuba.talkshow.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class ArticleRecord implements Parcelable {
    @SerializedName("uid")
    public abstract int uid();
    @SerializedName("voa_id")
    public abstract int voa_id();
    @SerializedName("curr_time")
    public abstract int curr_time();
    @SerializedName("total_time")
    public abstract int total_time();
    @SerializedName("is_finish")
    public abstract int is_finish();
    @SerializedName("type")
    public abstract int type();
    @SerializedName("percent")
    public abstract int percent();

    public static ArticleRecord.Builder builder() {
        return new AutoValue_ArticleRecord.Builder();
    }

    public static TypeAdapter<ArticleRecord> typeAdapter(Gson gson) {
        return new AutoValue_ArticleRecord.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract ArticleRecord.Builder setUid(int uid);
        public abstract ArticleRecord.Builder setVoa_id(int voa_id);
        public abstract ArticleRecord.Builder setCurr_time(int curr_time);
        public abstract ArticleRecord.Builder setTotal_time(int total_time);
        public abstract ArticleRecord.Builder setIs_finish(int is_finish);
        public abstract ArticleRecord.Builder setType(int type);
        public abstract ArticleRecord.Builder setPercent(int percent);
        public abstract ArticleRecord build();
    }

}
