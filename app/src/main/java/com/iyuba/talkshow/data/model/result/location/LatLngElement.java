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
public abstract class LatLngElement implements Parcelable {
    @SerializedName("lat")
    public abstract String latitude();
    @SerializedName("lng")
    public abstract String longitude();

    public static LatLngElement create(String latitude, String longitude) {
        return new AutoValue_LatLngElement(latitude, longitude);
    }

    public static TypeAdapter<LatLngElement> typeAdapter(Gson gson) {
        return new AutoValue_LatLngElement.GsonTypeAdapter(gson);
    }
}
