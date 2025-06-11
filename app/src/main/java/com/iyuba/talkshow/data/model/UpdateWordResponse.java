package com.iyuba.talkshow.data.model;

import com.iyuba.wordtest.entity.TalkShowWords;

import java.util.List;

public class UpdateWordResponse {


    /**
     * result : 201
     * data : []
     */

    private int result;
    private List<TalkShowWords> data;
    private int bookVersion;

    public int getBookVersion() {
        return bookVersion;
    }

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
