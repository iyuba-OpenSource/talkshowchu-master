package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.iyuba.talkshow.data.model.Ranking;

import java.util.List;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

@AutoValue
public abstract class GetRankingResponse implements Parcelable {
    @SerializedName("ResultCode")
    public abstract int resultCode();
    @SerializedName("Message")
    @Nullable
    public abstract String message();
    @SerializedName("PageNumber")
    @Nullable
    public abstract Integer pageNumber();
    @SerializedName("TotalPage")
    @Nullable
    public abstract Integer totalPage();
    @SerializedName("FirstPage")
    @Nullable
    public abstract Integer firstPage();
    @SerializedName("PrevPage")
    @Nullable
    public abstract Integer prevPage();
    @SerializedName("NextPage")
    @Nullable
    public abstract Integer nextPage();
    @SerializedName("LastPage")
    @Nullable
    public abstract Integer lastPage();
    @SerializedName("AddScore")
    @Nullable
    public abstract Integer addScore();
    @SerializedName("Counts")
    @Nullable
    public abstract Integer counts();
    @SerializedName("data")
    @Nullable
    public abstract List<Ranking> data();

    public static TypeAdapter<GetRankingResponse> typeAdapter(Gson gson) {
        return new AutoValue_GetRankingResponse.GsonTypeAdapter(gson);
    }
}
