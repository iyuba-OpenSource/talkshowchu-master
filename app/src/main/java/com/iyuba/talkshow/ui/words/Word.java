package com.iyuba.talkshow.ui.words;

import androidx.annotation.Keep;
import android.text.TextUtils;

@Keep
public class Word {
    public String userid;
    public String key = "";
    public String lang = "";
    public String audioUrl = "";
    public String pron = ""; // 音标
    public String def = ""; // 解释
    public String examples = ""; // 例句

    public Word() {

    }

    public Word(String key, String audioUrl, String pron, String def) {
        this.key = key;
        this.audioUrl = audioUrl;
        this.pron = pron;
        this.def = def;
    }

    public boolean hasDefinition() {
        return !TextUtils.isEmpty(def);
    }

    public boolean hasPronunciation() {
        return !TextUtils.isEmpty(pron);

    }

    public boolean hasAudioUrl() {
        return !TextUtils.isEmpty(audioUrl);
    }
}
