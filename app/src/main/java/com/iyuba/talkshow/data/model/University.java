package com.iyuba.talkshow.data.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class University {
	@SerializedName("id")
	public abstract int id();
	@SerializedName("uni_id")
	public abstract int uniId();
	@SerializedName("uni_type")
	public abstract int uniType();
	@SerializedName("uni_name")
	public abstract String uniName();
	@SerializedName("province")
	public abstract String province();

	public static Builder builder() {
		return new AutoValue_University.Builder();
	}

	public static TypeAdapter<University> typeAdapter(Gson gson) {
		return new AutoValue_University.GsonTypeAdapter(gson);
	}

	@AutoValue.Builder
	public abstract static class Builder {
		public abstract Builder setId(int id);
		public abstract Builder setUniType(int uniType);
		public abstract Builder setUniId(int uniId);
		public abstract Builder setUniName(String uniName);
		public abstract Builder setProvince(String province);
		public abstract University build();
	}
}
