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
public abstract class GeometryElement implements Parcelable {
    @SerializedName("bounds")
    public abstract BoundElement bounds();
    @SerializedName("location")
    public abstract LatLngElement location();
    @SerializedName("location_type")
    public abstract String locationType();
    @SerializedName("viewport")
    public abstract ViewPortElement viewport();

    public static TypeAdapter<GeometryElement> typeAdapter(Gson gson) {
        return new AutoValue_GeometryElement.GsonTypeAdapter(gson);
    }
}
