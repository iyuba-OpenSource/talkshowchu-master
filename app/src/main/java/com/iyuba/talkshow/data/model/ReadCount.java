package com.iyuba.talkshow.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/11/29 0029.
 */

@AutoValue
public abstract class ReadCount implements Parcelable {
    @SerializedName("VoaId")
    public abstract int voaId();
    @SerializedName("ReadCount")
    public abstract int readCount();

    public static Builder builder() {
        return new AutoValue_ReadCount.Builder();
    }

    public static TypeAdapter<ReadCount> typeAdapter(Gson gson) {
        return new AutoValue_ReadCount.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setVoaId(int voaId);
        public abstract Builder setReadCount(int readCount);
        public abstract ReadCount build();
    }
}
