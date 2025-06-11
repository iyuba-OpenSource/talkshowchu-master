package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/21/021.
 */
@AutoValue
public abstract class GetAliPayInfoResponse implements Parcelable {
    @SerializedName("result")
    public abstract int result();
    @SerializedName("message")
    public abstract String message();
    @SerializedName("orderInfo")
    @Nullable
    public abstract String orderInfo();
    @SerializedName("sign")
    public abstract String sign();

    public static GetAliPayInfoResponse create(int result, String message,
                                               String orderInfo, String sign) {
        return new AutoValue_GetAliPayInfoResponse(result, message, orderInfo, sign);
    }

    public static TypeAdapter<GetAliPayInfoResponse> typeAdapter(Gson gson) {
        return new AutoValue_GetAliPayInfoResponse.GsonTypeAdapter(gson);
    }
}
