package com.iyuba.talkshow.newce.search.util;

import com.iyuba.wordtest.utils.RecordManager;

import java.io.File;

/**
 * 针对评测等进行播放和录音的单例操作
 */
public class ExerciseSingle {

    private static ExerciseSingle instance;

    private RecordManager manager;

    //当前的大题号(用于处理音频播放)
    private int testId = 0;
    //当前的小题号(用于处理音频播放)
    private int textIndex = 0;
    //是否正在录音
    private boolean record = false;
    //是否正在评测
    private boolean eval = false;
    //开启试题的次数（用于无网络下获取试题数据）
    private int openExerciseCount = 0;
    //是否需要重置数据后才可以提交
    private boolean resetData = false;

    //结束时间(用于处理在提交习题数据时，由于联合主键的问题，数据插入覆盖的情况)
    private long endTime = 0;

    public static ExerciseSingle getInstance(){
        if (instance==null){
            synchronized (ExerciseSingle.class){
                if (instance==null){
                    instance =new ExerciseSingle();
                }
            }
        }
        return instance;
    }

    /********录音器**********/
    public RecordManager setManager(File file){
        manager = new RecordManager(file);
        return manager;
    }

    public RecordManager getManager(){
        return manager;
    }

    /********其他数据*********/
    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public int getTextIndex() {
        return textIndex;
    }

    public void setTextIndex(int textIndex) {
        this.textIndex = textIndex;
    }

    public boolean isRecord() {
        return record;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }

    public boolean isEval() {
        return eval;
    }

    public void setEval(boolean eval) {
        this.eval = eval;
    }

    public int getOpenExerciseCount() {
        return openExerciseCount;
    }

    public void setOpenExerciseCount(int openExerciseCount) {
        this.openExerciseCount = openExerciseCount;
    }

    public void addOpenExerciseCount(int addCount){
        this.openExerciseCount+=addCount;
    }

    public boolean isResetData() {
        return resetData;
    }

    public void setResetData(boolean resetData) {
        this.resetData = resetData;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
