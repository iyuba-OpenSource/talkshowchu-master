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
public abstract class SendDubbingResponse {
    @SerializedName("AddScore")
    @Nullable
    public abstract Integer addScore();
    @SerializedName("FilePath")
    @Nullable
    public abstract String filePath();
    @SerializedName("Message")
    @Nullable
    public abstract String message();
    @SerializedName("ResultCode")
    @Nullable
    public abstract String resultCode();
    @SerializedName("ShuoShuoId")
    @Nullable
    public abstract Integer shuoshuoId();

    public static SendDubbingResponse create(Integer addScore, String filePath,
                                             String message, String resultCode,
                                             Integer shuoshuoId) {
        return new AutoValue_SendDubbingResponse(addScore, filePath, message, resultCode, shuoshuoId);
    }

    public static TypeAdapter<SendDubbingResponse> typeAdapter(Gson gson) {
        return new AutoValue_SendDubbingResponse.GsonTypeAdapter(gson);
    }
}
