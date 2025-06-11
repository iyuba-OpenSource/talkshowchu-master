package com.iyuba.talkshow.data.model;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Created by Administrator on 2016/12/3 0003.
 */

@AutoValue
public abstract class Collect implements Parcelable {
    public abstract int uid();
    public abstract int voaId();
    @Nullable
    public abstract String date();
    private Voa voa;

    public static Builder builder() {
        return new AutoValue_Collect.Builder();
    }

    public static TypeAdapter<Collect> typeAdapter(Gson gson) {
        return new AutoValue_Collect.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setUid(int uid);
        public abstract Builder setVoaId(int voaId);
        public abstract Builder setDate(String date);
        public abstract Collect build();
    }

    public Voa getVoa() {
        return voa;
    }

    public void setVoa(Voa voa) {
        this.voa = voa;
    }
}
