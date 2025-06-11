package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/15/015.
 */

@AutoValue
public abstract class GetVerifyCodeResponse implements Parcelable {
    @SerializedName("result")
    public abstract int result();
    @SerializedName("res_code")
    @Nullable
    public abstract String resCode();
    @SerializedName("userphone")
    @Nullable
    public abstract String userPhone();
    @SerializedName("identifier")
    @Nullable
    public abstract String identifer();
    @SerializedName("message")
    @Nullable
    public abstract String message();

    public static GetVerifyCodeResponse create(int result, String message, String resCode,
                                               String userPhone, String identifier) {
        return new AutoValue_GetVerifyCodeResponse(result, message, resCode, userPhone, identifier);
    }

    public static TypeAdapter<GetVerifyCodeResponse> typeAdapter(Gson gson) {
        return new AutoValue_GetVerifyCodeResponse.GsonTypeAdapter(gson);
    }
}
