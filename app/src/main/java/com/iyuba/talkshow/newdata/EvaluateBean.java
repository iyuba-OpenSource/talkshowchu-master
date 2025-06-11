package com.iyuba.talkshow.newdata;

import java.util.List;

/**
 * Created by carl shen on 2020/7/28
 * New Primary English, new study experience.
 */
public class EvaluateBean {

    /**
     * sentence : Sophie is a new student.
     * total_score : 3.2642857142857147
     * word_count : 5
     * URL : http://ai." + Constant.IYUBA_CN + "data/projects/speech/wav/concept/549278710051005.wav
     * words : [{"content":"SOPHIE","index":0,"score":3.75},{"content":"IS","index":1,"score":0},{"content":"A","index":2,"score":5},{"content":"NEW","index":3,"score":4},{"content":"STUDENT","index":4,"score":3.5714285714285716}]
     */

    private String sentence;
    private String total_score;
    private int word_count;
    private String URL;
    private List<WordsBean> words;

    public EvaluateBean(String URL) {
        this.URL = URL;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getTotal_score() {
        return total_score;
    }

    public void setTotal_score(String total_score) {
        this.total_score = total_score;
    }

    public int getWord_count() {
        return word_count;
    }

    public void setWord_count(int word_count) {
        this.word_count = word_count;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public List<WordsBean> getWords() {
        return words;
    }

    public void setWords(List<WordsBean> words) {
        this.words = words;
    }

    public static class WordsBean {
        /**
         * content : SOPHIE
         * index : 0
         * score : 3.75
         */

        private String content;
        private int index;
        private double score;
        public String pron;
        public String pron2;
        public String user_pron;
        public String user_pron2;
        public String insert;
        public String delete;
        public String substitute_orgi;
        public String substitute_user;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }
}



