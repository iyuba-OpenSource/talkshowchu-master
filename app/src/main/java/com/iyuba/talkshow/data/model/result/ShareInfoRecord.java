package com.iyuba.talkshow.data.model.result;

import com.google.gson.annotations.SerializedName;

/**
 * Created by carl shen on 2021/4/12
 * New Primary English, new study experience.
 */
public class ShareInfoRecord {
    @SerializedName("uid")
    public String uid;
    @SerializedName("createtime")
    public String createtime;
    @SerializedName("appid")
    public String appid;
    @SerializedName("scan")
    public int scan;
    @SerializedName("shareId")
    public String shareId;

}
