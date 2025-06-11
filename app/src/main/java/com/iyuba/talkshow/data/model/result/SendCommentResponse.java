package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/11/29 0029.
 */

@AutoValue
public abstract class SendCommentResponse implements Parcelable {
    @SerializedName("ResultCode")
    @Nullable
    public abstract String resultCode();
    @SerializedName("AddScore")
    @Nullable
    public abstract Float addScore();
    @SerializedName("Message")
    @Nullable
    public abstract String message();

    public static SendCommentResponse create(String resultCode, Float addScore, String message) {
        return new AutoValue_SendCommentResponse(resultCode, addScore, message);
    }

    public static TypeAdapter<SendCommentResponse> typeAdapter(Gson gson) {
        return new AutoValue_SendCommentResponse.GsonTypeAdapter(gson);
    }
}
