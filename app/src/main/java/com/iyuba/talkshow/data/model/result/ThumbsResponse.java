package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/5 0005.
 */

@AutoValue
public abstract class ThumbsResponse implements Parcelable {
    @SerializedName("ResultCode")
    public abstract String resultCode();
    @SerializedName("Message")
    public abstract String message();

    public static ThumbsResponse create(String resultCode, String message) {
        return new AutoValue_ThumbsResponse(resultCode, message);
    }

    public static TypeAdapter<ThumbsResponse> typeAdapter(Gson gson) {
        return new AutoValue_ThumbsResponse.GsonTypeAdapter(gson);
    }
}
