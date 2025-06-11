package com.iyuba.talkshow.data.model;

import java.util.List;

public class ExamWordResponse {
    public int result;
    public int mode;
    public int uid;
    public int totalRight;
    public int totalWrong;
    public String msg;
    public int lesson;
    public String testMode;
    public List<ExamWord> dataRight;
    public List<ExamWord> dataWrong;

}
