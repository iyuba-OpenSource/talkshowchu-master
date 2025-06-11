package com.iyuba.talkshow.data.model;

/**
 * Created by carl shen on 2020/9/18.
 */
public class StudyResponse {
    public String Lesson;
    public int LessonId;
    public String BeginTime;
    public String EndTime;
    public String Title;
    public int EndFlg;
    public int TestWords;
    public int TestNumber;

    @Override
    public String toString() {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Lesson=").append(Lesson);
        stringBuffer.append(",LessonId=").append(LessonId);
        stringBuffer.append(",BeginTime=").append(BeginTime);
        stringBuffer.append(",EndTime=").append(EndTime);
        stringBuffer.append(",Title=").append(Title);
        stringBuffer.append(",EndFlg=").append(EndFlg);
        stringBuffer.append(",TestWords=").append(TestWords);
        stringBuffer.append(",TestNumber=").append(TestNumber);
        stringBuffer.append(";\n");
        return stringBuffer.toString();
    }
}
