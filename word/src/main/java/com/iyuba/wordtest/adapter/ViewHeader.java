package com.iyuba.wordtest.adapter;

public class ViewHeader {
    private String header;
    private int index;

    public ViewHeader(String header, int index) {
        this.header = header;
        this.index = index;
    }


    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
