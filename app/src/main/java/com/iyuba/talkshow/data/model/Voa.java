package com.iyuba.talkshow.data.model;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/11/10 0010.
 */
@AutoValue
public abstract class Voa implements Parcelable, RecyclerItem {
    @SerializedName("IntroDesc")
    @Nullable public abstract String introDesc();
    @SerializedName("CreatTime")
    public abstract String createTime();
    @SerializedName("Category")
    public abstract int category();
    @SerializedName("Keyword")
    @Nullable public abstract String keyword();
    @SerializedName("Title")
    public abstract String title();
    @SerializedName("Sound")
    public abstract String sound();
    @SerializedName("Pic")
    public abstract String pic();
    @SerializedName("VoaId")
    public abstract int voaId();
    @SerializedName("Pagetitle")
    @Nullable public abstract String pageTitle();
    @SerializedName("Url")
    public abstract String url();
    @SerializedName("DescCn")
    public abstract String descCn();
    @SerializedName("Title_cn")
    public abstract String titleCn();
    @SerializedName("PublishTime")
    public abstract String publishTime();
    @SerializedName("HotFlg")
    public abstract int hotFlag();
    @SerializedName("ReadCount")
    public abstract int readCount();
    @SerializedName("ClickRead")
    public abstract int clickRead();
    @SerializedName("TotalTime")
    public abstract int totalTime();
    @SerializedName("PercentId")
    public abstract int percentId();
    @SerializedName("OutlineId")
    public abstract int outlineId();
    @SerializedName("PackageId")
    public abstract int packageId();
    @SerializedName("CategoryId")
    public abstract int categoryId();
    @SerializedName("ClassId")
    @Nullable public abstract String classId();
    @SerializedName("Series")
    public abstract int series();
    public int UnitId = 0;

    // TODO: 2022/7/13  增加video字段
    @SerializedName("Video")
    @Nullable public abstract String video();

    public static Builder builder() {
        return new AutoValue_Voa.Builder();
    }

    public static TypeAdapter<Voa> typeAdapter(Gson gson) {
        return new AutoValue_Voa.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setIntroDesc(String introDesc);
        public abstract Builder setCreateTime(String createTime);
        public abstract Builder setCategory(int category);
        public abstract Builder setKeyword(String keyword);
        public abstract Builder setTitle(String title);
        public abstract Builder setSound(String sound);
        public abstract Builder setPic(String title);
        public abstract Builder setVoaId(int voaId);
        public abstract Builder setPageTitle(String pageTitle);
        public abstract Builder setUrl(String url);
        public abstract Builder setDescCn(String descCn);
        public abstract Builder setTitleCn(String titleCn);
        public abstract Builder setPublishTime(String publishTime);
        public abstract Builder setHotFlag(int hotFlag);
        public abstract Builder setReadCount(int readCount);
        public abstract Builder setClickRead(int clickRead);
        public abstract Builder setTotalTime(int totalTime);
        public abstract Builder setPercentId(int percentId);
        public abstract Builder setOutlineId(int outlineId);
        public abstract Builder setPackageId(int packageId);
        public abstract Builder setCategoryId(int categoryId);
        public abstract Builder setClassId(String classId);
        public abstract Builder setSeries(int series);

        //增加video字段
        public abstract Builder setVideo(String video);
        public abstract Voa build();

    }
}
