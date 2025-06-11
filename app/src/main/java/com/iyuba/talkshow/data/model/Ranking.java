package com.iyuba.talkshow.data.model;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

@AutoValue
public abstract class Ranking implements Parcelable {

    @SerializedName("id")
    @Nullable
    public abstract Integer id();
    @SerializedName("backId")
    @Nullable
    public abstract Integer backId();
    @SerializedName("score")
    @Nullable
    public abstract Float score();
    @SerializedName("Userid")
    @Nullable
    public abstract Integer userId();
    @SerializedName("UserName")
    @Nullable
    public abstract String userName();
    @SerializedName("ImgSrc")
    @Nullable
    public abstract String imgSrc();
    @SerializedName("againstCount")
    @Nullable
    public abstract Integer againstCount();
    @SerializedName("agreeCount")
    @Nullable
    public abstract Integer agreeCount();
    @SerializedName("Title")
    @Nullable
    public abstract String title();
    @SerializedName("Title_cn")
    @Nullable
    public abstract String titleCn();
    @SerializedName("TopicId")
    @Nullable
    public abstract Integer topicId();
    @SerializedName("ShuoShuoType")
    @Nullable
    public abstract Integer shuoShuoType();
    @SerializedName("ShuoShuo")
    @Nullable
    public abstract String shuoShuo();
    @SerializedName("CreateDate")
    @Nullable
    public abstract String createDate();
    @SerializedName("videoUrl")
    @Nullable
    public abstract String videoUrl();

    public int agreeNum;
    public boolean isAudioCommentPlaying = false;

    public static Ranking create(Integer id, Integer backId, Float score, Integer userId,
                                 String userName, String imgSrc, int againstCount,
                                 Integer agreeCount, String title, String titleCn,
                                 Integer topicId, Integer shuoShuoType, String shuoShuo,
                                 String createDate,String videoUrl) {
        return new AutoValue_Ranking(id, backId, score, userId, userName, imgSrc, againstCount,
                agreeCount, title, titleCn, topicId, shuoShuoType, shuoShuo, createDate, videoUrl);
    }

    public static TypeAdapter<Ranking> typeAdapter(Gson gson) {
        return new AutoValue_Ranking.GsonTypeAdapter(gson);
    }
}
