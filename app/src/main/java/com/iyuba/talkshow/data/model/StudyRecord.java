package com.iyuba.talkshow.data.model;

import androidx.annotation.Keep;

import java.util.List;

import timber.log.Timber;

@Keep
public class StudyRecord {
    private String lesson;
    private String lessonid;
    private long starttime;
    private long endtime;
    private String testmode;
    private String flag;
    private int wordCount;
    private String testNumber;

    public StudyRecord() {
        lesson = "";
        lessonid = "";
        starttime = System.currentTimeMillis();
        endtime = System.currentTimeMillis();
        testmode = "0";
        flag = "1";
        wordCount = 0;
        testNumber = "0";
    }

    public void setWordCount(String content) {
        int count = 0;
        try {
            count = content.split(" ").length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Timber.e("upload word count :: " + count);
        setWordCount(count);
    }

    public String getTestNumber() {
        return testNumber;
    }

    public void setTestNumber(String testNumber) {
        this.testNumber = testNumber;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getLessonid() {
        return lessonid;
    }

    public void setLessonid(String lessonid) {
        this.lessonid = lessonid;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public String getTestmode() {
        return testmode;
    }

    public void setTestmode(String testmode) {
        this.testmode = testmode;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setWordCount(List<VoaText> voaTextList) {
        StringBuilder builder = new StringBuilder();
        for (VoaText voaText : voaTextList) {
            Timber.e(voaText.toString());
            builder.append(voaText.sentence());
        }
        setWordCount(builder.toString());
    }
}
