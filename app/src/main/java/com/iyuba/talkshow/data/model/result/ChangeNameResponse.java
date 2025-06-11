package com.iyuba.talkshow.data.model.result;

import com.google.gson.annotations.SerializedName;

/**
 * Created by carl shen on 2021/2/25
 * New Primary English, new study experience.
 */
public class ChangeNameResponse {
    @SerializedName("result")
    public String result;
    @SerializedName("message")
    public String message;
    @SerializedName("uid")
    public String uid;
    @SerializedName("username")
    public String username;

}
