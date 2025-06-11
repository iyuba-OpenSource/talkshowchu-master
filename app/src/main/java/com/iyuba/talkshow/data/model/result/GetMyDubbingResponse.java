package com.iyuba.talkshow.data.model.result;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.iyuba.talkshow.data.model.Ranking;

import java.util.List;

/**
 * Created by Administrator on 2017/1/17/017.
 */

@AutoValue
public abstract class GetMyDubbingResponse {
    @SerializedName("result")
    public abstract boolean result();
    @SerializedName("data")
    public abstract List<Ranking> data();

    public static TypeAdapter<GetMyDubbingResponse> typeAdapter(Gson gson) {
        return new AutoValue_GetMyDubbingResponse.GsonTypeAdapter(gson);
    }
}
