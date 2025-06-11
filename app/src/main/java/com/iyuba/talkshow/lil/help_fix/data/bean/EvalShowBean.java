package com.iyuba.talkshow.lil.help_fix.data.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @title: 评测展示结果
 * @date: 2023/5/13 10:06
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class EvalShowBean implements Serializable {
    private static final long serialVersionUID = 4324050021323059619L;

    private String sentence;//句子或单词
    private String evalUrl;//音频链接
    private String localPath;//本地文件
    private String totalScore;//总分
    private List<WordResultBean> words;//单词数据

    public EvalShowBean(String sentence, String evalUrl, String localPath, String totalScore, List<WordResultBean> words) {
        this.sentence = sentence;
        this.evalUrl = evalUrl;
        this.localPath = localPath;
        this.totalScore = totalScore;
        this.words = words;
    }

    public String getSentence() {
        return sentence;
    }

    public String getEvalUrl() {
        return evalUrl;
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public List<WordResultBean> getWords() {
        return words;
    }

    public static class WordResultBean{
        private String word;//单词
        private String score;//单词分数
        private int index;//单词位置

        private String wordPron;//单词音标
        private String userPorn;//用户音标

        public WordResultBean(String word, String score, int index, String wordPron, String userPorn) {
            this.word = word;
            this.score = score;
            this.index = index;
            this.wordPron = wordPron;
            this.userPorn = userPorn;
        }

        public String getWord() {
            return word;
        }

        public String getScore() {
            return score;
        }

        public int getIndex() {
            return index;
        }

        public String getWordPron() {
            return wordPron;
        }

        public String getUserPorn() {
            return userPorn;
        }
    }
}
