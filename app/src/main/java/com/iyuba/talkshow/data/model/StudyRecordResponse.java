package com.iyuba.talkshow.data.model;

import java.util.List;

/**
 * Created by carl shen on 2020/9/18.
 */
public class StudyRecordResponse {

    private int result;
    public String message;
    private List<StudyResponse> data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<StudyResponse> getData() {
        return data;
    }

    public void setData(List<StudyResponse> data) {
        this.data = data;
    }

}
