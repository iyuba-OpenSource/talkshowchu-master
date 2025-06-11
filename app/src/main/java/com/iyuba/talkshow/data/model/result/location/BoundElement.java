package com.iyuba.talkshow.data.model.result.location;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/23/023.
 */

@AutoValue
public abstract class BoundElement implements Parcelable {
    @SerializedName("northeast")
    public abstract LatLngElement northEast();

    @SerializedName("southwest")
    public abstract LatLngElement southWest();

    public static TypeAdapter<BoundElement> typeAdapter(Gson gson) {
        return new AutoValue_BoundElement.GsonTypeAdapter(gson);
    }
}
