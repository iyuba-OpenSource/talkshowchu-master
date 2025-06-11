package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.iyuba.talkshow.data.model.VoaText;

import java.util.List;

@AutoValue
public abstract class VoaTextResponse implements Parcelable {
    @SerializedName("total")
    public abstract int total();
    @SerializedName("voatext")
    @Nullable public abstract List<VoaText> voaTexts();

    public static TypeAdapter<VoaTextResponse> typeAdapter(Gson gson) {
        return new AutoValue_VoaTextResponse.GsonTypeAdapter(gson);
    }
}
