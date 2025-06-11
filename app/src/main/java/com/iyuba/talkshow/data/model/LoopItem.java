package com.iyuba.talkshow.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/11/16 0016.
 */
@AutoValue
public abstract class LoopItem implements Parcelable {
    @SerializedName("id")
    public abstract int id();
    @SerializedName("price")
    public abstract float price();
    @SerializedName("name")
    public abstract String name();
    @SerializedName("pic")
    public abstract String pic();
    @SerializedName("ownerid")
    public abstract int ownerId();
    @SerializedName("desc1")
    public abstract String desc();

    public static LoopItem create(int id, float price, String name, String pic,
                                  int ownerId, String desc) {
        return new AutoValue_LoopItem(id, price, name, pic, ownerId, desc);
    }

    public static TypeAdapter<LoopItem> typeAdapter(Gson gson) {
        return new AutoValue_LoopItem.GsonTypeAdapter(gson);
    }

}
