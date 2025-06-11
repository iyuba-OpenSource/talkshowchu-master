package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.iyuba.talkshow.data.model.Voa;

import java.util.List;

/**
 * Created by Administrator on 2016/12/5 0005.
 */

@AutoValue
public abstract class GetVoaResponse implements Parcelable {
    @SerializedName("total")
    public abstract int total();
    @SerializedName("data")
    @Nullable
    public abstract List<Voa> data();

    public static TypeAdapter<GetVoaResponse> typeAdapter(Gson gson) {
        return new AutoValue_GetVoaResponse.GsonTypeAdapter(gson);
    }
}
