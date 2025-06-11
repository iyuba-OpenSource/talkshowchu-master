package com.iyuba.wordtest.bean;


import java.util.List;

/**
 * @author yq QQ:1032006226
 * @name talkshow
 * @class name：com.iyuba.talkshow.data.model.result
 * @class describe
 * @time 2018/12/19 19:54
 * @change
 * @chang time
 * @class describe
 */
//@Root(name = "response", strict = false)
public class SendEvaluateResponse {

    /**
     * data : {"sentence":"take care of everyone when they got sick.","total_score":"0.37157287157287155","word_count":8,"URL":"http://ai."+WebConstant.WEB_SUFFIX+"data/projects/speech/wav/type/50166753094991.wav","words":[{"content":"TAKE","index":0,"score":1.9696969696969693},{"content":"CARE","index":1,"score":0},{"content":"OF","index":2,"score":0},{"content":"EVERYONE","index":3,"score":1.002886002886003},{"content":"WHEN","index":4,"score":0},{"content":"THEY","index":5,"score":0},{"content":"GOT","index":6,"score":0},{"content":"SICK","index":7,"score":0}]}
     */

    private String result;
    private String message;


    private DataBean data;
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * sentence : take care of everyone when they got sick.
         * total_score : 0.37157287157287155
         * word_count : 8
         * URL : http://ai."+WebConstant.WEB_SUFFIX+"data/projects/speech/wav/type/50166753094991.wav
         * words : [{"content":"TAKE","index":0,"score":1.9696969696969693},{"content":"CARE","index":1,"score":0},{"content":"OF","index":2,"score":0},{"content":"EVERYONE","index":3,"score":1.002886002886003},{"content":"WHEN","index":4,"score":0},{"content":"THEY","index":5,"score":0},{"content":"GOT","index":6,"score":0},{"content":"SICK","index":7,"score":0}]
         */

        private String sentence;
        private String total_score;
        private int word_count;
        private String URL;
        private List<WordsBean> words;

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
             * content : TAKE
             * index : 0
             * score : 1.9696969696969693
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
}
