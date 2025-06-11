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
public abstract class LocationElement {
    @SerializedName("long_name")
    public abstract String longName();
    @SerializedName("short_name")
    public abstract String shortName();
    @SerializedName("types")
    public abstract List<String> types();

    public static LocationElement create(String longName, String shortName, List<String> types) {
        return new AutoValue_LocationElement(longName, shortName, types);
    }

    public static TypeAdapter<LocationElement> typeAdapter(Gson gson) {
        return new AutoValue_LocationElement.GsonTypeAdapter(gson);
    }
}
