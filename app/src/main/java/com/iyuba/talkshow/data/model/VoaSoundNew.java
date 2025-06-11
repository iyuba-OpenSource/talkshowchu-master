package com.iyuba.talkshow.data.model;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class VoaSoundNew implements Parcelable {
    @SerializedName("itemid")
    public abstract long itemid();
    @SerializedName("uid")
    public abstract int uid();
    @SerializedName("voa_id")
    public abstract int voa_id();
    @SerializedName("totalscore")
    public abstract int totalscore();
    @SerializedName("wordscore")
    public abstract String wordscore();
    @SerializedName("filepath")
    public abstract String filepath();
    @SerializedName("time")
    public abstract String time();
    @SerializedName("sound_url")
    public abstract String sound_url();
    @SerializedName("words")
    @Nullable
    public abstract String words();
    @SerializedName("rvc")
    @Nullable
    public abstract String rvc();

    public static Builder builder() {
        return new AutoValue_VoaSoundNew.Builder();
    }

    public static TypeAdapter<VoaSoundNew> typeAdapter(Gson gson) {
        return new AutoValue_VoaSoundNew.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setItemid(long itemid);
        public abstract Builder setUid(int uid);
        public abstract Builder setVoa_id(int voa_id);
        public abstract Builder setTotalscore(int totalscore);
        public abstract Builder setWordscore(String wordscore);
        public abstract Builder setFilepath(String filepath);
        public abstract Builder setTime(String time);
        public abstract Builder setSound_url(String sound_url);
        public abstract Builder setWords(String words);
        public abstract Builder setRvc(String rvc);
        public abstract VoaSoundNew build();
    }

}
