package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.iyuba.talkshow.data.model.Comment;

import java.util.List;

/**
 * Created by Administrator on 2016/12/5 0005.
 */

@AutoValue
public abstract class GetCommentResponse implements Parcelable {
    @SerializedName("ResultCode")
    public abstract int resultCode();
    @SerializedName("Message")
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
    @Nullable
    @SerializedName("NextPage")
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
    public abstract List<Comment> data();

    public static TypeAdapter<GetCommentResponse> typeAdapter(Gson gson) {
        return new AutoValue_GetCommentResponse.GsonTypeAdapter(gson);
    }
}
