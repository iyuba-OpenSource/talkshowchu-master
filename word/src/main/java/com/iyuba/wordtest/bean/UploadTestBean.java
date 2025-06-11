package com.iyuba.wordtest.bean;

import java.util.List;

public class UploadTestBean {
    public String DeviceId ;
    public int appId ;
    public String format ;
    public String lesson ;
    public int mode ;
    public String sign ;
    public String uid ;

    public List<TestListBean> testList ;

    public List<Score> scoreList;

    public static class TestListBean {
        public int AnswerResut;
        public String BeginTime ;
        public String Category ;
        public String LessonId ;
        public String RightAnswer ;
        public int TestId ;
        public String TestMode ;
        public String TestTime ;
        public String UserAnswer ;
    }

    public  static class Score {
    }
}
