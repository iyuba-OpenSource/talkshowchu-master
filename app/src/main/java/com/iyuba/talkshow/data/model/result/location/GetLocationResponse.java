package com.iyuba.talkshow.data.model.result.location;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2016/12/23/023.
 */

@AutoValue
public abstract class GetLocationResponse {
    @SerializedName("status")
    public abstract String status();
    @SerializedName("results")
    public abstract List<GetLocationElement> results();

    public static TypeAdapter<GetLocationResponse> typeAdapter(Gson gson) {
        return new AutoValue_GetLocationResponse.GsonTypeAdapter(gson);
    }
}
