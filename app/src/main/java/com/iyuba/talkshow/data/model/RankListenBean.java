package com.iyuba.talkshow.data.model;

import java.util.List;

/**
 * Created by carl shen on 2021/7/26
 * New Primary English, new study experience.
 */
public class RankListenBean {
    public int result;
    public String myimgSrc;
    public int myid;
    public int myranking;
    public int totalTime;
    public int totalWord;
    public String myname;
    public int totalEssay;
    public String message;
    public List<DataBean> data;

    public class DataBean {
        public int uid;
        public int totalTime;
        public int totalWord;
        public String name;
        public int ranking;
        public int sort;
        public int totalEssay;
        public String imgSrc;
    }
}
