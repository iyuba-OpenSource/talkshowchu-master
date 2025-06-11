package com.iyuba.wordtest.bean;

import java.util.List;

public class SearchVoaResult {

    /**
     * textData : [{"EndTiming":"25.1","ParaId":"1","IdIndex":"3","SoundText":"http://staticvip.iyuba.cn/sounds/voa/sentence/201904/7840/7840_1_3.wav","Sentence_cn":"他们在东南亚的热带森林里寻找水果、坚果、鸟类和昆虫来过日子。","Timing":"14.9","VoaId":"7840","Sentence":"They spend their days in search of fruit, nuts, birds and insects in Southeast Asia\u2019s tropical forests."}]
     * English : true
     * titleData : []
     * titleToal : 0
     * textToal : 1
     */

    private boolean English;
    private int titleToal;
    private int textToal;
    private List<TextDataBean> textData;
    private List<?> titleData;

    public boolean isEnglish() {
        return English;
    }

    public void setEnglish(boolean English) {
        this.English = English;
    }

    public int getTitleToal() {
        return titleToal;
    }

    public void setTitleToal(int titleToal) {
        this.titleToal = titleToal;
    }

    public int getTextToal() {
        return textToal;
    }

    public void setTextToal(int textToal) {
        this.textToal = textToal;
    }

    public List<TextDataBean> getTextData() {
        return textData;
    }

    public void setTextData(List<TextDataBean> textData) {
        this.textData = textData;
    }

    public List<?> getTitleData() {
        return titleData;
    }

    public void setTitleData(List<?> titleData) {
        this.titleData = titleData;
    }

    public static class TextDataBean {
        /**
         * EndTiming : 25.1
         * ParaId : 1
         * IdIndex : 3
         * SoundText : http://staticvip.iyuba.cn/sounds/voa/sentence/201904/7840/7840_1_3.wav
         * Sentence_cn : 他们在东南亚的热带森林里寻找水果、坚果、鸟类和昆虫来过日子。
         * Timing : 14.9
         * VoaId : 7840
         * Sentence : They spend their days in search of fruit, nuts, birds and insects in Southeast Asia’s tropical forests.
         */

        private String EndTiming;
        private String ParaId;
        private String IdIndex;
        private String SoundText;
        private String Sentence_cn;
        private String Timing;
        private String VoaId;
        private String Sentence;

        public String getEndTiming() {
            return EndTiming;
        }

        public void setEndTiming(String EndTiming) {
            this.EndTiming = EndTiming;
        }

        public String getParaId() {
            return ParaId;
        }

        public void setParaId(String ParaId) {
            this.ParaId = ParaId;
        }

        public String getIdIndex() {
            return IdIndex;
        }

        public void setIdIndex(String IdIndex) {
            this.IdIndex = IdIndex;
        }

        public String getSoundText() {
            return SoundText;
        }

        public void setSoundText(String SoundText) {
            this.SoundText = SoundText;
        }

        public String getSentence_cn() {
            return Sentence_cn;
        }

        public void setSentence_cn(String Sentence_cn) {
            this.Sentence_cn = Sentence_cn;
        }

        public String getTiming() {
            return Timing;
        }

        public void setTiming(String Timing) {
            this.Timing = Timing;
        }

        public String getVoaId() {
            return VoaId;
        }

        public void setVoaId(String VoaId) {
            this.VoaId = VoaId;
        }

        public String getSentence() {
            return Sentence;
        }

        public void setSentence(String Sentence) {
            this.Sentence = Sentence;
        }
    }
}
