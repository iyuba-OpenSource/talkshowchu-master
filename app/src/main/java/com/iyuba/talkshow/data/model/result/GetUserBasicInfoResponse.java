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
public abstract class GetUserBasicInfoResponse implements Parcelable {
    @SerializedName("result")
    public abstract int result();
    @SerializedName("message")
    @Nullable
    public abstract String message();
    @SerializedName("realname")
    @Nullable
    public abstract String realName();
    @SerializedName("gender")
    @Nullable
    public abstract String gender();
    @SerializedName("birthday")
    @Nullable
    public abstract String birthday();
    @SerializedName("constellation")
    @Nullable
    public abstract String constellation();
    @SerializedName("zodiac")
    @Nullable
    public abstract String zodiac();
    @SerializedName("telephone")
    @Nullable
    public abstract String telephone();
    @SerializedName("mobile")
    @Nullable
    public abstract String mobile();
    @SerializedName("idcardtype")
    @Nullable
    public abstract String idCardType();
    @SerializedName("idcard")
    @Nullable
    public abstract String idCard();
    @SerializedName("address")
    @Nullable
    public abstract String address();
    @SerializedName("zipcode")
    @Nullable
    public abstract String zipCode();
    @SerializedName("nationality")
    @Nullable
    public abstract String nationality();
    @SerializedName("birthLocation")
    @Nullable
    public abstract String birthLocation();
    @SerializedName("resideLocation")
    @Nullable
    public abstract String resideLocation();
    @SerializedName("graduateschool")
    @Nullable
    public abstract String graduateSchool();
    @SerializedName("company")
    @Nullable
    public abstract String company();
    @SerializedName("education")
    @Nullable
    public abstract String education();
    @SerializedName("occupation")
    @Nullable
    public abstract String occupation();
    @SerializedName("postion")
    @Nullable
    public abstract String position();
    @SerializedName("revenue")
    @Nullable
    public abstract String revenue();
    @SerializedName("affectivestatus")
    @Nullable
    public abstract String affectiveStatus();
    @SerializedName("bloodType")
    @Nullable
    public abstract String bloodType();
    @SerializedName("lookingfor")
    @Nullable
    public abstract String lookingFor();
    @SerializedName("height")
    @Nullable
    public abstract String height();
    @SerializedName("weight")
    @Nullable
    public abstract String weight();
    @SerializedName("bio")
    @Nullable
    public abstract String bio();
    @SerializedName("interest")
    @Nullable
    public abstract String interest();
    @SerializedName("age")
    @Nullable
    public abstract String age();

    public static GetUserBasicInfoResponse.Builder builder() {
        return new AutoValue_GetUserBasicInfoResponse.Builder();
    }

    public static TypeAdapter<GetUserBasicInfoResponse> typeAdapter(Gson gson) {
        return new AutoValue_GetUserBasicInfoResponse.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setResult(int result);
        public abstract Builder setPosition(String position);
        public abstract Builder setBirthday(String birthday);
        public abstract Builder setConstellation(String constellation);
        public abstract Builder setWeight(String weight);
        public abstract Builder setLookingFor(String lookingFor);
        public abstract Builder setGraduateSchool(String graduateSchool);
        public abstract Builder setEducation(String education);
        public abstract Builder setHeight(String height);
        public abstract Builder setZodiac(String zodiac);
        public abstract Builder setInterest(String interest);
        public abstract Builder setResideLocation(String resideLocation);
        public abstract Builder setGender(String gender);
        public abstract Builder setRevenue(String revenue);
        public abstract Builder setOccupation(String occupation);
        public abstract Builder setZipCode(String zipCode);
        public abstract Builder setIdCardType(String idCardType);
        public abstract Builder setIdCard(String idCard);
        public abstract Builder setBloodType(String bloodType);
        public abstract Builder setMessage(String message);
        public abstract Builder setBirthLocation(String birthLocation);
        public abstract Builder setNationality(String nationality);
        public abstract Builder setBio(String bio);
        public abstract Builder setAddress(String address);
        public abstract Builder setCompany(String company);
        public abstract Builder setRealName(String realName);
        public abstract Builder setTelephone(String telephone);
        public abstract Builder setMobile(String mobile);
        public abstract Builder setAffectiveStatus(String affectiveStatus);
        public abstract Builder setAge(String age);
        public abstract GetUserBasicInfoResponse build();
    }
}
