package com.iyuba.talkshow.newdata;

import androidx.annotation.NonNull;

import java.util.List;

public class UpdateEvalDataBean {

    private String result;
    private int size;
    private List<DataBean> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * sentence : Nice to meet you.
         * paraid : 1
         * score : 5.0
         * newsid : 10050
         * idindex : 20
         * userid : 5492787
         * url : wav6/202002/concept/20200203/15807181587392926.mp3
         * newstype : concept
         */

        private String sentence;
        private int paraid;
        private String score;
        private int newsid;
        private int idindex;
        private int userid;
        private String url;
        private String newstype;

        public String getSentence() {
            return sentence;
        }

        public void setSentence(String sentence) {
            this.sentence = sentence;
        }

        public int getParaid() {
            return paraid;
        }

        public void setParaid(int paraid) {
            this.paraid = paraid;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public int getNewsid() {
            return newsid;
        }

        public void setNewsid(int newsid) {
            this.newsid = newsid;
        }

        public int getIdindex() {
            return idindex;
        }

        public void setIdindex(int idindex) {
            this.idindex = idindex;
        }

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
            this.userid = userid;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getNewstype() {
            return newstype;
        }

        public void setNewstype(String newstype) {
            this.newstype = newstype;
        }

        @NonNull
        @Override
        public String toString() {
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append("sentence=").append(sentence);
            stringBuffer.append(",paraid=").append(paraid);
            stringBuffer.append(",score=").append(score);
            stringBuffer.append(",newsid=").append(newsid);
            stringBuffer.append(",idindex=").append(idindex);
            stringBuffer.append(",userid=").append(userid);
            stringBuffer.append(",url=").append(url);
            stringBuffer.append(",newstype=").append(newstype);
            stringBuffer.append(";\n");
            return stringBuffer.toString();
        }
    }
}