package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.iyuba.talkshow.data.model.LoopItem;

import java.util.List;

/**
 * Created by Administrator on 2016/11/16 0016.
 */

@AutoValue
public abstract class GetLoopResponse implements Parcelable {
    @SerializedName("result")
    public abstract String result();
    @SerializedName("data")
    public abstract List<LoopItem> data();

    public static TypeAdapter<GetLoopResponse> typeAdapter(Gson gson) {
        return new AutoValue_GetLoopResponse.GsonTypeAdapter(gson);
    }
}
