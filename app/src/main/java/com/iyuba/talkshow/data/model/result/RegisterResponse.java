package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class RegisterResponse implements Parcelable {
    @SerializedName("result")
    public abstract int result();
    @SerializedName("message")
    @Nullable
    public abstract String message();
    @SerializedName("uid")
    @Nullable
    public abstract String uid();
    @SerializedName("username")
    @Nullable
    public abstract String username();
    @SerializedName("imgSrc")
    @Nullable
    public abstract String imageSrc();
    @SerializedName("email")
    @Nullable
    public abstract String email();
    @SerializedName("credits")
    @Nullable
    public abstract String integral();

    public static Builder builder() {
        return new AutoValue_RegisterResponse.Builder();
    }

    public static TypeAdapter<RegisterResponse> typeAdapter(Gson gson) {
        return new AutoValue_RegisterResponse.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setResult(int result);
        public abstract Builder setMessage(String message);
        public abstract Builder setUid(String uid);
        public abstract Builder setUsername(String username);
        public abstract Builder setImageSrc(String imageSrc);
        public abstract Builder setEmail(String email);
        public abstract Builder setIntegral(String integral);
        public abstract RegisterResponse build();
    }
}

