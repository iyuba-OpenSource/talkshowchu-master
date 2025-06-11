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
public abstract class GetLocationElement {
    @SerializedName("address_components")
    public abstract List<LocationElement> addressComponents();
    @SerializedName("formatted_address")
    public abstract String formattedAddress();
    @SerializedName("place_id")
    public abstract String placeId();
    @SerializedName("types")
    public abstract List<String> types();

    public static TypeAdapter<GetLocationElement> typeAdapter(Gson gson) {
        return new AutoValue_GetLocationElement.GsonTypeAdapter(gson);
    }
}
