package com.iyuba.talkshow.data.model.result;

import com.google.gson.annotations.SerializedName;

/**
 * Created by carl shen on 2021/2/25
 * New Primary English, new study experience.
 */
public class RegisterMobResponse {
    @SerializedName("isLogin")
    public int isLogin;
    @SerializedName("userinfo")
    public LoginResponse userinfo;
    @SerializedName("res")
    public MobBean res;

    public class MobBean {
        @SerializedName("valid")
        public String valid;
        @SerializedName("phone")
        public String phone;
        @SerializedName("isValid")
        public int isValid;
    }
}
