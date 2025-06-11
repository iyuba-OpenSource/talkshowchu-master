package com.iyuba.talkshow.data.model.result;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/1/18/018.
 */

@AutoValue
public abstract class BackData {
    @SerializedName("againstCount")
    @Nullable
    public abstract Integer againstCount();
    @SerializedName("agreeCount")
    @Nullable
    public abstract Integer agreeCount();
    @SerializedName("backId")
    @Nullable
    public abstract Integer backId();
    @SerializedName("CreateDate")
    @Nullable
    public abstract String createDate();
    @SerializedName("id")
    @Nullable
    public abstract Integer id();
    @SerializedName("ImgSrc")
    @Nullable
    public abstract String imgSrc();
    @SerializedName("ShuoShuo")
    @Nullable
    public abstract String shuoshuo();
    @SerializedName("ShuoShuoType")
    @Nullable
    public abstract Integer shuoshuoType();
    @SerializedName("Userid")
    @Nullable
    public abstract Integer userId();
    @SerializedName("userName")
    @Nullable
    public abstract String username();

    public static BackData create(Integer againstCount, Integer agreeCount, Integer backId,
                                  String createDate, Integer id, String imgSrc, String shuoShuo,
                                  Integer shuoShuoType, Integer userId, String username) {
        return new AutoValue_BackData(againstCount, agreeCount, backId, createDate,
                id, imgSrc, shuoShuo, shuoShuoType, userId, username);
    }

    public static TypeAdapter<BackData> typeAdapter(Gson gson) {
        return new AutoValue_BackData.GsonTypeAdapter(gson);
    }
}
