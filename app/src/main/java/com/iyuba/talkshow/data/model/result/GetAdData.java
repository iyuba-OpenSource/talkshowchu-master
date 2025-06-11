package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;


@AutoValue
public abstract class GetAdData implements Parcelable {
    @SerializedName("adId")
    public abstract String adId();
    @SerializedName("picUrl")
    public abstract String picUrl();
    @SerializedName("classNum")
    public abstract int classNum();
    @SerializedName("beginTime")
    public abstract String beginTime();
    @SerializedName("desc1")
    public abstract String desc();
    @SerializedName("url")
    public abstract String url();
    @SerializedName("firstLevel")
    public abstract int firstLevel();
    @SerializedName("secondLevel")
    public abstract int secondLevel();
    @SerializedName("thirdLevel")
    public abstract int thirdLevel();
    @SerializedName("title")
    public abstract String title();

    private String adFileName;

    public String getAdFileName() {
        return adFileName;
    }

    public void setAdFileName(String adFileName) {
        this.adFileName = adFileName;
    }

    public static Builder builder() {
        return new AutoValue_GetAdData.Builder();
    }

    public static TypeAdapter<GetAdData> typeAdapter(Gson gson) {
        return new AutoValue_GetAdData.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setAdId(String adId);
        public abstract Builder setPicUrl(String picUrl);
        public abstract Builder setClassNum(int classNum);
        public abstract Builder setBeginTime(String beginTime);
        public abstract Builder setDesc(String desc);
        public abstract Builder setUrl(String url);
        public abstract Builder setFirstLevel(int firstLevel);
        public abstract Builder setSecondLevel(int secondLevel);
        public abstract Builder setThirdLevel(int thirdLevel);
        public abstract Builder setTitle(String title);
        public abstract GetAdData build();
    }
}
