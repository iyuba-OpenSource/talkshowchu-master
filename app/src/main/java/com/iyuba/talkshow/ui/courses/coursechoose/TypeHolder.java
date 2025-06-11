package com.iyuba.talkshow.ui.courses.coursechoose;

public class TypeHolder {

    public TypeHolder(int id, String value) {
        this.id = id;
        this.value = value;
    }

    private int id ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value ;
}
