package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/17/017.
 */

@AutoValue
public abstract class UploadImageResponse implements Parcelable {
    @SerializedName("status")
    public abstract int status();
    @SerializedName("jiFen")
    @Nullable
    public abstract String integral();
    @SerializedName("middleUrl")
    @Nullable
    public abstract String middleUrl();
    @SerializedName("smallUrl")
    @Nullable
    public abstract String smallUrl();
    @SerializedName("bigUrl")
    @Nullable
    public abstract String bigUrl();

    public static UploadImageResponse create(int status, String integral, String middleUrl,
                                             String smallUrl, String bigUrl) {
        return new AutoValue_UploadImageResponse(status, integral, middleUrl, smallUrl, bigUrl);
    }

    public static TypeAdapter<UploadImageResponse> typeAdapter(Gson gson) {
        return new AutoValue_UploadImageResponse.GsonTypeAdapter(gson);
    }

}
