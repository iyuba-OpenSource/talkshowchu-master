package com.iyuba.talkshow.lil.help_fix.data.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @title: 单词学习报告的提交数据
 * @date: 2023/6/27 10:28
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordStudyReportSubmitBean implements Serializable {


    /**
     * DeviceId : 02:00:00:00:00:00
     * appId : 260
     * format : json
     * lesson : 205
     * mode : 2
     * scoreList : []
     * sign : b7146b02bf8318aae46a6e1fb5451bae
     * testList : [{"AnswerResut":1,"BeginTime":"2023-06-27","Category":"单词闯关","LessonId":"1","RightAnswer":"schoolbag","TestId":4,"TestMode":"W","TestTime":"2023-06-27","UserAnswer":"schoolbag"},{"AnswerResut":1,"BeginTime":"2023-06-27","Category":"单词闯关","LessonId":"1","RightAnswer":"teacher","TestId":5,"TestMode":"W","TestTime":"2023-06-27","UserAnswer":"teacher"},{"AnswerResut":1,"BeginTime":"2023-06-27","Category":"单词闯关","LessonId":"1","RightAnswer":"have","TestId":7,"TestMode":"W","TestTime":"2023-06-27","UserAnswer":"have"},{"AnswerResut":1,"BeginTime":"2023-06-27","Category":"单词闯关","LessonId":"1","RightAnswer":"I","TestId":6,"TestMode":"W","TestTime":"2023-06-27","UserAnswer":"I"},{"AnswerResut":1,"BeginTime":"2023-06-27","Category":"单词闯关","LessonId":"1","RightAnswer":"book","TestId":1,"TestMode":"W","TestTime":"2023-06-27","UserAnswer":"book"},{"AnswerResut":1,"BeginTime":"2023-06-27","Category":"单词闯关","LessonId":"1","RightAnswer":"a","TestId":8,"TestMode":"W","TestTime":"2023-06-27","UserAnswer":"a"},{"AnswerResut":1,"BeginTime":"2023-06-27","Category":"单词闯关","LessonId":"1","RightAnswer":"ruler","TestId":2,"TestMode":"W","TestTime":"2023-06-27","UserAnswer":"ruler"},{"AnswerResut":1,"BeginTime":"2023-06-27","Category":"单词闯关","LessonId":"1","RightAnswer":"pencil","TestId":3,"TestMode":"W","TestTime":"2023-06-27","UserAnswer":"pencil"}]
     * uid : 12071118
     */

    private String DeviceId;
    private int appId;
    private String format;
    private String lesson;
    private int mode;
    private String sign;
    private String uid;
    private List<Score> scoreList;
    private List<TestListBean> testList;

    public WordStudyReportSubmitBean(String deviceId, int appId, String format, String lesson, int mode, String sign, String uid, List<Score> scoreList, List<TestListBean> testList) {
        DeviceId = deviceId;
        this.appId = appId;
        this.format = format;
        this.lesson = lesson;
        this.mode = mode;
        this.sign = sign;
        this.uid = uid;
        this.scoreList = scoreList;
        this.testList = testList;
    }

    public static class TestListBean {
        /**
         * AnswerResut : 1
         * BeginTime : 2023-06-27
         * Category : 单词闯关
         * LessonId : 1
         * RightAnswer : schoolbag
         * TestId : 4
         * TestMode : W
         * TestTime : 2023-06-27
         * UserAnswer : schoolbag
         */

        private int AnswerResut;
        private String BeginTime;
        private String Category;
        private String LessonId;
        private String RightAnswer;
        private int TestId;
        private String TestMode;
        private String TestTime;
        private String UserAnswer;

        public TestListBean(int answerResut, String beginTime, String category, String lessonId, String rightAnswer, int testId, String testMode, String testTime, String userAnswer) {
            AnswerResut = answerResut;
            BeginTime = beginTime;
            Category = category;
            LessonId = lessonId;
            RightAnswer = rightAnswer;
            TestId = testId;
            TestMode = testMode;
            TestTime = testTime;
            UserAnswer = userAnswer;
        }
    }

    public static class Score{

    }
}
