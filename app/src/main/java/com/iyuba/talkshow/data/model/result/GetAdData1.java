package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/29/029.
 */

@AutoValue
public abstract class GetAdData1 implements Serializable {
    @SerializedName("id")
    public abstract int id();

    @SerializedName("adId")
    public abstract String adId();

    @SerializedName("startuppic_StartDate")
    public abstract String startDate();

    @SerializedName("startuppic_EndDate")
    public abstract String endDate();

    @SerializedName("startuppic")
    public abstract String picUrl();

    @SerializedName("startuppic_Url")
    public abstract String linkUrl();

    @SerializedName("type")
    public abstract String type();

    @SerializedName("classNum")
    public abstract String classNum();

    public static Builder builder() {
        return new AutoValue_GetAdData1.Builder();
    }

    public static TypeAdapter<GetAdData1> typeAdapter(Gson gson) {
        return new AutoValue_GetAdData1.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setId(int id);

        public abstract Builder setType(String type);

        public abstract Builder setClassNum(String classNum);

        public abstract Builder setAdId(String adId);

        public abstract Builder setStartDate(String startDate);

        public abstract Builder setEndDate(String endDate);

        public abstract Builder setPicUrl(String picUrl);

        public abstract Builder setLinkUrl(String linkUrl);

        public abstract GetAdData1 build();
    }

}
