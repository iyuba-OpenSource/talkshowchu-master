package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/14/014.
 */

@AutoValue
public abstract class GetUserResponse implements Parcelable {
    @SerializedName("result")
    public abstract String result();
    @SerializedName("message")
    @Nullable
    public abstract String message();
    @SerializedName("uid")
    @Nullable
    public abstract String uid();
    @SerializedName("username")
    @Nullable
    public abstract String username();
    @SerializedName("expireTime")
    @Nullable
    public abstract String expireTime();
    @SerializedName("Amount")
    @Nullable
    public abstract String amount();
    @SerializedName("vipStatus")
    @Nullable
    public abstract String vipStatus();
    @SerializedName("email")
    @Nullable
    public abstract String email();
    @SerializedName("imgSrc")
    @Nullable
    public abstract String imageSrc();
    @SerializedName("money")
    @Nullable
    public abstract String money();
    @SerializedName("isteacher")
    @Nullable
    public abstract String isTeacher();
    @SerializedName("mobile")
    @Nullable
    public abstract String phone();

    public static Builder builder() {
        return new AutoValue_GetUserResponse.Builder();
    }

    public static TypeAdapter<GetUserResponse> typeAdapter(Gson gson) {
        return new AutoValue_GetUserResponse.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setResult(String result);
        public abstract Builder setMessage(String message);
        public abstract Builder setUid(String uid);
        public abstract Builder setUsername(String username);
        public abstract Builder setExpireTime(String expireTime);
        public abstract Builder setAmount(String amount);
        public abstract Builder setVipStatus(String vipStatus);
        public abstract Builder setEmail(String email);
        public abstract Builder setImageSrc(String imageSrc);
        public abstract Builder setMoney(String money);
        public abstract Builder setIsTeacher(String isTeacher);
        public abstract Builder setPhone(String phone);
        public abstract GetUserResponse build();
    }

}
