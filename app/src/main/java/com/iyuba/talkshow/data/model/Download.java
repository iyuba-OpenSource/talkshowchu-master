package com.iyuba.talkshow.data.model;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Created by Administrator on 2016/12/27/027.
 */

@AutoValue
public abstract class Download {
    public abstract int uid();
    public abstract int voaId();
    public abstract String date();
    private Voa voa;

    public static Builder builder() {
        return new AutoValue_Download.Builder();
    }

    public static TypeAdapter<Download> typeAdapter(Gson gson) {
        return new AutoValue_Download.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setUid(int uid);
        public abstract Builder setVoaId(int voaId);
        public abstract Builder setDate(String date);
        public abstract Download build();
    }

    public Voa getVoa() {
        return voa;
    }

    public void setVoa(Voa voa) {
        this.voa = voa;
    }
}
