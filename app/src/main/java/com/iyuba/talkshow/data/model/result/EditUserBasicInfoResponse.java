package com.iyuba.talkshow.data.model.result;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/22/022.
 */

@AutoValue
public abstract class EditUserBasicInfoResponse implements Parcelable {
    @SerializedName("address")
    @Nullable
    public abstract String address();

    @SerializedName("affectivestatus")
    @Nullable
    public abstract String affectiveStatus();

    @SerializedName("bio")
    @Nullable
    public abstract String bio();

    @SerializedName("birthday")
    @Nullable
    public abstract String birthday();

    @SerializedName("birthLocation")
    @Nullable
    public abstract String birthLocation();

    @SerializedName("bloodtype")
    @Nullable
    public abstract String bloodType();

    @SerializedName("company")
    @Nullable
    public abstract String company();

    @SerializedName("constellation")
    @Nullable
    public abstract String constellation();

    @SerializedName("education")
    @Nullable
    public abstract String education();

    @SerializedName("gender")
    @Nullable
    public abstract String gender();

    @SerializedName("graduateschool")
    @Nullable
    public abstract String graduateSchool();

    @SerializedName("height")
    @Nullable
    public abstract String height();

    @SerializedName("idcard")
    @Nullable
    public abstract String idCard();

    @SerializedName("idcardtype")
    @Nullable
    public abstract String idCardType();

    @SerializedName("interest")
    @Nullable
    public abstract String interest();

    @SerializedName("jiFen")
    @Nullable
    public abstract String jiFen();

    @SerializedName("lookingfor")
    @Nullable
    public abstract String lookingFor();

    @SerializedName("message")
    @Nullable
    public abstract String message();

    @SerializedName("mobile")
    @Nullable
    public abstract String mobile();

    @SerializedName("nationality")
    @Nullable
    public abstract String nationality();

    @SerializedName("occupation")
    @Nullable
    public abstract String occupation();

    @SerializedName("position")
    @Nullable
    public abstract String position();

    @SerializedName("realname")
    @Nullable
    public abstract String realName();

    @SerializedName("resideLocation")
    @Nullable
    public abstract String resideLocation();

    @SerializedName("result")
    @Nullable
    public abstract Integer result();

    @SerializedName("revenue")
    @Nullable
    public abstract String revenue();

    @SerializedName("telephone")
    @Nullable
    public abstract String telephone();

    @SerializedName("weight")
    @Nullable
    public abstract String weight();

    @SerializedName("zipcode")
    @Nullable
    public abstract String zipcode();

    @SerializedName("zodiac")
    @Nullable
    public abstract String zodiac();

    public static EditUserBasicInfoResponse create(String address, String affectiveStatus, String bio,
                                                   String birthday, String birthLocation, String bloodType,
                                                   String company, String constellation, String education,
                                                   String gender, String graduateSchool, String height,
                                                   String idCard, String idCardType, String interest,
                                                   String jiFen, String lookingFor, String message,
                                                   String mobile, String nationality, String occupation,
                                                   String position, String realName, String resideLocation,
                                                   Integer result, String revenue, String telephone,
                                                   String weight, String zipcode, String zodiac) {
        return new AutoValue_EditUserBasicInfoResponse(address, affectiveStatus, bio, birthday, birthLocation,
                bloodType, company, constellation, education, gender, graduateSchool, height, idCard, idCardType,
                interest, jiFen, lookingFor, message, mobile, nationality, occupation, position, realName,
                resideLocation, result, revenue, telephone, weight, zipcode, zodiac);
    }

    public static TypeAdapter<EditUserBasicInfoResponse> typeAdapter(Gson gson) {
        return new AutoValue_EditUserBasicInfoResponse.GsonTypeAdapter(gson);
    }
}

