package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @desction: 评测结果
 * @date: 2023/3/2 16:35
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class Eval_result implements Serializable {

    /**
     * sentence : --- lesson 1 A private conversation
     * words : [{"index":"0","content":"---","pron":"L","pron2":"l","user_pron":"L","user_pron2":"l","score":"5.0","insert":"","delete":"","substitute_orgi":"","substitute_user":""},{"index":"1","content":"lesson","pron":"EH S AH N W","pron2":"esənw","user_pron":"EH S AH AH N W","user_pron2":"esəənw","score":"4.0","insert":"AH","delete":"","substitute_orgi":"","substitute_user":""},{"index":"2","content":"1","pron":"AH N AH","pron2":"ənə","user_pron":"AH N","user_pron2":"ən","score":"3.33","insert":"","delete":"AH","substitute_orgi":"","substitute_user":""},{"index":"3","content":"A","pron":"P","pron2":"p","user_pron":"","user_pron2":"","score":"0.0","insert":"","delete":"P","substitute_orgi":"","substitute_user":""},{"index":"4","content":"private","pron":"R AY V AH T K","pron2":"raɪvətk","user_pron":"AH","user_pron2":"ə","score":"0.83","insert":"","delete":"R AY V T K","substitute_orgi":"","substitute_user":""},{"index":"5","content":"conversation","pron":"AA N V ER S EY SH AH N","pron2":"ɒnvɜːseɪʃən","user_pron":"N V","user_pron2":"nv","score":"1.5","insert":"","delete":"AA ER S EY SH AH N","substitute_orgi":"","substitute_user":""}]
     * scores : 42
     * total_score : 2.1
     * filepath : /data/projects/voa/mp34/202303/concept/20230302/16777456102709654.mp3
     * URL : wav8/202303/concept/20230302/16777456102709654.mp3
     */

    private String sentence;
    private double scores;
    private double total_score;
    private String filepath;
    @SerializedName("URL")
    private String url;
    private List<WordsBean> words;
    //新概念的单词评测接口，增加了word_count这个参数
    private int word_count;

    public Eval_result(String sentence, double scores, double total_score, String filepath, String url, List<WordsBean> words) {
        this.sentence = sentence;
        this.scores = scores;
        this.total_score = total_score;
        this.filepath = filepath;
        this.url = url;
        this.words = words;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public double getScores() {
        return scores;
    }

    public void setScores(double scores) {
        this.scores = scores;
    }

    public double getTotal_score() {
        return total_score;
    }

    public void setTotal_score(double total_score) {
        this.total_score = total_score;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<WordsBean> getWords() {
        return words;
    }

    public void setWords(List<WordsBean> words) {
        this.words = words;
    }

    public static class WordsBean {
        /**
         * index : 0
         * content : ---
         * pron : L
         * pron2 : l
         * user_pron : L
         * user_pron2 : l
         * score : 5.0
         * insert :
         * delete :
         * substitute_orgi :
         * substitute_user :
         */

        private String index;
        private String content;
        private String pron;
        private String pron2;
        private String user_pron;
        private String user_pron2;
        private String score;
        private String insert;
        private String delete;
        private String substitute_orgi;
        private String substitute_user;

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPron() {
            return pron;
        }

        public void setPron(String pron) {
            this.pron = pron;
        }

        public String getPron2() {
            return pron2;
        }

        public void setPron2(String pron2) {
            this.pron2 = pron2;
        }

        public String getUser_pron() {
            return user_pron;
        }

        public void setUser_pron(String user_pron) {
            this.user_pron = user_pron;
        }

        public String getUser_pron2() {
            return user_pron2;
        }

        public void setUser_pron2(String user_pron2) {
            this.user_pron2 = user_pron2;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getInsert() {
            return insert;
        }

        public void setInsert(String insert) {
            this.insert = insert;
        }

        public String getDelete() {
            return delete;
        }

        public void setDelete(String delete) {
            this.delete = delete;
        }

        public String getSubstitute_orgi() {
            return substitute_orgi;
        }

        public void setSubstitute_orgi(String substitute_orgi) {
            this.substitute_orgi = substitute_orgi;
        }

        public String getSubstitute_user() {
            return substitute_user;
        }

        public void setSubstitute_user(String substitute_user) {
            this.substitute_user = substitute_user;
        }
    }
}
