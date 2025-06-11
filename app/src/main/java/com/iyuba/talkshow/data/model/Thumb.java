package com.iyuba.talkshow.data.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Created by Administrator on 2017/1/7/007.
 */

@AutoValue
public abstract class Thumb {
    public abstract int uid();
    public abstract int commentId();
    public int action;

    public static Builder builder() {
        return new AutoValue_Thumb.Builder();
    }

    public static TypeAdapter<Thumb> typeAdapter(Gson gson) {
        return new AutoValue_Thumb.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setUid(int uid);
        public abstract Builder setCommentId(int commentId);
        public abstract Thumb build();
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }
}
