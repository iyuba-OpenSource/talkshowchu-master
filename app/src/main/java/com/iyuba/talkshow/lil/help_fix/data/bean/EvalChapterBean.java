package com.iyuba.talkshow.lil.help_fix.data.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @title: 章节评测的数据
 * @date: 2023/5/24 14:43
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class EvalChapterBean implements Serializable {

    private String sentence;
    private double scores;
    private double totalScore;
    private String filepath;
    private String url;

    private String types;//类型
    private String voaId;//章节的id
    private String paraId;
    private String indexId;

    private List<WordBean> wordList;

    public EvalChapterBean(String sentence, double scores, double totalScore, String filepath, String url, String types, String voaId, String paraId, String indexId, List<WordBean> wordList) {
        this.sentence = sentence;
        this.scores = scores;
        this.totalScore = totalScore;
        this.filepath = filepath;
        this.url = url;
        this.types = types;
        this.voaId = voaId;
        this.paraId = paraId;
        this.indexId = indexId;
        this.wordList = wordList;
    }

    public String getSentence() {
        return sentence;
    }

    public double getScores() {
        return scores;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getUrl() {
        return url;
    }

    public String getTypes() {
        return types;
    }

    public String getVoaId() {
        return voaId;
    }

    public String getParaId() {
        return paraId;
    }

    public String getIndexId() {
        return indexId;
    }

    public List<WordBean> getWordList() {
        return wordList;
    }

    public static class WordBean implements Serializable{
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

        public WordBean(String index, String content, String pron, String pron2, String user_pron, String user_pron2, String score, String insert, String delete, String substitute_orgi, String substitute_user) {
            this.index = index;
            this.content = content;
            this.pron = pron;
            this.pron2 = pron2;
            this.user_pron = user_pron;
            this.user_pron2 = user_pron2;
            this.score = score;
            this.insert = insert;
            this.delete = delete;
            this.substitute_orgi = substitute_orgi;
            this.substitute_user = substitute_user;
        }

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
