package com.iyuba.talkshow.data.model;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.iyuba.talkshow.data.model.result.BackData;

import java.util.List;

/**
 * Created by Administrator on 2016/12/5 0005.
 */

@AutoValue
public abstract class Comment {
    @SerializedName("id")
    @Nullable
    public abstract Integer id();
    @SerializedName("ImgSrc")
    @Nullable
    public abstract String imgSrc();
    @SerializedName("score")
    @Nullable
    public abstract Float score();
    @SerializedName("Userid")
    @Nullable
    public abstract Integer userId();
    @SerializedName("UserName")
    @Nullable
    public abstract String userName();
    @SerializedName("vip")
    @Nullable
    public abstract Integer vip();
    @SerializedName("againstCount")
    @Nullable
    public abstract Integer againstCount();
    @SerializedName("agreeCount")
    @Nullable
    public abstract Integer agreeCount();
    @SerializedName("ShuoShuoType")
    @Nullable
    public abstract Integer shuoShuoType();
    @SerializedName("ShuoShuo")
    @Nullable
    public abstract String shuoShuo();
    @SerializedName("CreateDate")
    @Nullable
    public abstract String createDate();
    @SerializedName("backId")
    @Nullable
    public abstract Integer backId();
    @SerializedName("backList")
    @Nullable
    public abstract List<BackData> backList();

    public static Comment create(Integer id, String imgSrc, float score, Integer userId,
                                 String userName, Integer vip, Integer againstCount,
                                 Integer agreeCount, Integer shuoShuoType, String shuoShuo,
                                 String createDate, Integer backId, List<BackData> backList) {
        return new AutoValue_Comment(id, imgSrc, score, userId, userName, vip,
                againstCount, agreeCount, shuoShuoType, shuoShuo, createDate,
                backId, backList);
    }

    public static TypeAdapter<Comment> typeAdapter(Gson gson) {
        return new AutoValue_Comment.GsonTypeAdapter(gson);
    }
}
