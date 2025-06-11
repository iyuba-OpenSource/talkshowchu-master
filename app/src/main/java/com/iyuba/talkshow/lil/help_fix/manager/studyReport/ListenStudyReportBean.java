package com.iyuba.talkshow.lil.help_fix.manager.studyReport;

import com.iyuba.talkshow.lil.help_fix.data.bean.ChapterDetailBean;

import java.util.List;

/**
 * 本地学习报告(听力)
 */
public class ListenStudyReportBean {

    private long startTime;//开始时间
    private int wordCount;//单词数量（这里第一次进入为全部的数量，之后数据为0）

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public int showWordCount(String content){
        int count = 0;
        try {
            if (content.contains("!")){
                content = content.replace("!"," ");
            }
            if (content.contains(",")){
                content = content.replace(","," ");
            }

            count = content.split(" ").length;
        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    public void setWordCount(List<ChapterDetailBean> list){
        if (list==null||list.size()==0){
            setWordCount(0);
            return;
        }

        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            ChapterDetailBean detailBean = list.get(i);
            count+=showWordCount(detailBean.getSentence());
        }

        setWordCount(count);
    }
}
