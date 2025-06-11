package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class GetWXPayInfoResponse  implements Parcelable {
    @SerializedName("result")
    public abstract int result();
    @SerializedName("timestamp")
    public abstract String timestamp();
    @SerializedName("mch_id")
    public abstract String mch_id();
    @SerializedName("noncestr")
    public abstract String noncestr();
    @SerializedName("prepayid")
    public abstract String prepayid();
    @SerializedName("sign")
    public abstract String sign();
    @SerializedName("mch_key")
    public abstract String mch_key();
    @SerializedName("orderInfo")
    @Nullable
    public abstract String orderInfo();


    public static GetWXPayInfoResponse create(int result, String timestamp, String mch_id, String noncestr,
                                              String prepayid, String sign, String mch_key, @Nullable String orderInfo) {
        return new AutoValue_GetWXPayInfoResponse(result, timestamp, mch_id, noncestr, prepayid, sign, mch_key, orderInfo);
    }

    public static TypeAdapter<GetWXPayInfoResponse> typeAdapter(Gson gson) {
        return new AutoValue_GetWXPayInfoResponse.GsonTypeAdapter(gson);
    }
}
