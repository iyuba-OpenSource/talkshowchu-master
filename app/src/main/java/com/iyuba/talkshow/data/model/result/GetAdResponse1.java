package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/29/029.
 */

@AutoValue
public abstract class GetAdResponse1 implements Parcelable {
    @SerializedName("result")
    public abstract String result();
    @SerializedName("data")
    @Nullable
    public abstract GetAdData1 data();

    public static TypeAdapter<GetAdResponse1> typeAdapter(Gson gson) {
        return new AutoValue_GetAdResponse1.GsonTypeAdapter(gson);
    }
}
