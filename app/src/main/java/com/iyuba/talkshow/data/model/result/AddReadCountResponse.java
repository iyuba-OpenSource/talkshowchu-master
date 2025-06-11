package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.iyuba.talkshow.data.model.ReadCount;

/**
 * Created by Administrator on 2016/11/29 0029.
 */

@AutoValue
public abstract class AddReadCountResponse implements Parcelable {
    @SerializedName("ResultCode")
    public abstract int ResultCode();
    @SerializedName("total")
    public abstract int total();
    @SerializedName("data")
    public abstract ReadCount data();

    public static TypeAdapter<AddReadCountResponse> typeAdapter(Gson gson) {
        return new AutoValue_AddReadCountResponse.GsonTypeAdapter(gson);
    }
}
