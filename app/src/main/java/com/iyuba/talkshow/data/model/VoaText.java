package com.iyuba.talkshow.data.model;

import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.text.SpannableStringBuilder;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.iyuba.talkshow.newdata.EvaluateBean;

/**
 * Created by Administrator on 2016/11/22 0022.
 */

@AutoValue
public abstract class VoaText implements Parcelable {
    @SerializedName("ImgPath")
    @Nullable
    public abstract String imgPath();
    @SerializedName("EndTiming")
    public abstract float endTiming();  //停止时间(总的时间)
    @SerializedName("ParaId")
    public abstract int paraId();
    @SerializedName("IdIndex")
    public abstract int idIndex();
    @SerializedName("Start_x")
    public abstract int startX();
    @SerializedName("Start_y")
    public abstract int startY();
    @SerializedName("End_x")
    public abstract int endX();
    @SerializedName("End_y")
    public abstract int endY();
    @SerializedName("sentence_cn")
    public abstract String sentenceCn();
    @SerializedName("ImgWords")
    @Nullable
    public abstract String imgWords();
    @SerializedName("Timing")
    public abstract float timing();   // 开头的间隔时间(总的时间)
    @SerializedName("Sentence")
    public abstract String sentence();
    private int score = 0;
    private int voaId;
    private String filename;
    private boolean iscore = false;
    private SpannableStringBuilder parseData=null;
    private boolean isshowbq=false;
    public int getVoaId() {
        return voaId;
    }

    public void setIscore(boolean iscore) {
        this.iscore = iscore;
    }

    public boolean isIscore() {
        return iscore;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public boolean isIsshowbq() {
        return isshowbq;
    }

    public void setIsshowbq(boolean isshowbq) {
        this.isshowbq = isshowbq;
    }

    public SpannableStringBuilder getParseData() {
        return parseData;
    }

    public void setParseData(SpannableStringBuilder parseData) {
        this.parseData = parseData;
    }

    public void setVoaId(int voaId) {
        this.voaId = voaId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public static Builder builder() {
        return new AutoValue_VoaText.Builder();
    }

    public static TypeAdapter<VoaText> typeAdapter(Gson gson) {
        return new AutoValue_VoaText.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setImgPath(String imgPath);
        public abstract Builder setEndTiming(float endTiming);
        public abstract Builder setParaId(int paraId);
        public abstract Builder setIdIndex(int idIndex);
        public abstract Builder setStartX(int startX);
        public abstract Builder setStartY(int startY);
        public abstract Builder setEndX(int endX);
        public abstract Builder setEndY(int endY);
        public abstract Builder setSentenceCn(String sentenceCn);
        public abstract Builder setImgWords(String imgWords);
        public abstract Builder setTiming(float timing);
        public abstract Builder setSentence(String sentence);
        public abstract VoaText build();
    }

    public SpannableStringBuilder readResult;
    public boolean isRead = false;
    public int shuoshuoId;
    public EvaluateBean evaluateBean;
    public int readScore = 0;
    public String pathLocal;
}
