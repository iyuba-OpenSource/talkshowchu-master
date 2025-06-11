package com.iyuba.wordtest.entity;

import java.util.List;

public class UpdateWordResponse {

    private int result;
    private List<TalkShowWords> data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<TalkShowWords> getData() {
        return data;
    }

    public void setData(List<TalkShowWords> data) {
        this.data = data;
    }

}
