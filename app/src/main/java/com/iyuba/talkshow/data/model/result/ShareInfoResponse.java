package com.iyuba.talkshow.data.model.result;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by carl shen on 2021/4/12
 * New Primary English, new study experience.
 */
public class ShareInfoResponse {
    @SerializedName("result")
    public String result;
    @SerializedName("message")
    public String message;
    @SerializedName("count")
    public int count;
    @SerializedName("record")
    public List<ShareInfoRecord> record;

}
