package com.iyuba.talkshow.data.model.result;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/26/026.
 */

@AutoValue
public abstract class GetAdResponse {
    @SerializedName("result")
    public abstract int result();
    @SerializedName("data")
    @Nullable
    public abstract GetAdData data();

    public static TypeAdapter<GetAdResponse> typeAdapter(Gson gson) {
        return new AutoValue_GetAdResponse.GsonTypeAdapter(gson);
    }
}
