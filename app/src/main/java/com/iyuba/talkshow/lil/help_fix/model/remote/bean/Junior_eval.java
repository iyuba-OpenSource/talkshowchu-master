package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @title: 中小学-评测-单词
 * @date: 2023/5/13 10:50
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Junior_eval implements Serializable {
    private static final long serialVersionUID = -5380739261308348671L;

    /**
     * sentence : I have a book.
     * words : [{"index":"0","content":"I","pron":"AY","pron2":"aɪ","user_pron":"AY","user_pron2":"aɪ","score":"5.0","insert":"","delete":"","substitute_orgi":"","substitute_user":""},{"index":"1","content":"have","pron":"HH AE V","pron2":"hæv","user_pron":"AE V","user_pron2":"æv","score":"3.33","insert":"","delete":"HH","substitute_orgi":"","substitute_user":""},{"index":"2","content":"a","pron":"AH","pron2":"ə","user_pron":"AH","user_pron2":"ə","score":"5.0","insert":"","delete":"","substitute_orgi":"","substitute_user":""},{"index":"3","content":"book.","pron":"B UH K","pron2":"bʊk","user_pron":"B UH","user_pron2":"bʊ","score":"3.33","insert":"","delete":"K","substitute_orgi":"","substitute_user":""}]
     * scores : 75
     * total_score : 3.75
     * filepath : /data/projects/voa/mp34/202305/primaryenglish/20230513/16839420845282366.mp3
     * URL : wav8/202305/primaryenglish/20230513/16839420845282366.mp3
     */

    private String sentence;
    private int scores;
    private double total_score;
    private String filepath;
    @SerializedName("URL")
    private String url;
    private List<WordsBean> words;

    public String getSentence() {
        return sentence;
    }

    public int getScores() {
        return scores;
    }

    public double getTotal_score() {
        return total_score;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getUrl() {
        return url;
    }

    public List<WordsBean> getWords() {
        return words;
    }

    public static class WordsBean {
        /**
         * index : 0
         * content : I
         * pron : AY
         * pron2 : aɪ
         * user_pron : AY
         * user_pron2 : aɪ
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

        public String getContent() {
            return content;
        }

        public String getPron() {
            return pron;
        }

        public String getPron2() {
            return pron2;
        }

        public String getUser_pron() {
            return user_pron;
        }

        public String getUser_pron2() {
            return user_pron2;
        }

        public String getScore() {
            return score;
        }

        public String getInsert() {
            return insert;
        }

        public String getDelete() {
            return delete;
        }

        public String getSubstitute_orgi() {
            return substitute_orgi;
        }

        public String getSubstitute_user() {
            return substitute_user;
        }
    }
}
