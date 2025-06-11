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
public abstract class LoginResponse implements Parcelable {
    @SerializedName("result")
    public abstract int result();
    @SerializedName("message")
    @Nullable
    public abstract String message();
    @SerializedName("uid")
    @Nullable
    public abstract Integer uid();
    @SerializedName("username")
    @Nullable
    public abstract String username();
    @SerializedName("nickname")
    @Nullable
    public abstract String nickname();
    @SerializedName("email")
    @Nullable
    public abstract String email();
    @SerializedName("imgSrc")
    @Nullable
    public abstract String imageSrc();
    @SerializedName("money")
    @Nullable
    public abstract String money();
    @SerializedName("mobile")
    @Nullable
    public abstract String mobile();
    @SerializedName("isteacher")
    @Nullable
    public abstract Integer isTeacher();
    @SerializedName("expireTime")
    @Nullable
    public abstract Long expireTime();
    @SerializedName("Amount")
    @Nullable
    public abstract Integer amount();
    @SerializedName("vipStatus")
    @Nullable
    public abstract Integer vipStatus();
    @SerializedName("credits")
    @Nullable
    public abstract Integer integral();

    public static Builder builder() {
        return new AutoValue_LoginResponse.Builder();
    }

    public static TypeAdapter<LoginResponse> typeAdapter(Gson gson) {

        return new AutoValue_LoginResponse.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setResult(int result);
        public abstract Builder setMessage(String message);
        public abstract Builder setUid(Integer uid);
        public abstract Builder setUsername(String username);
        public abstract Builder setNickname(String nickname);
        public abstract Builder setEmail(String email);
        public abstract Builder setImageSrc(String imageSrc);
        public abstract Builder setMoney(String money);
        public abstract Builder setMobile(String mobile);
        public abstract Builder setIsTeacher(Integer isTeacher);
        public abstract Builder setExpireTime(Long expireTime);
        public abstract Builder setAmount(Integer amount);
        public abstract Builder setVipStatus(Integer vipStatus);
        public abstract Builder setIntegral(Integer integral);
        public abstract LoginResponse build();
    }
}
