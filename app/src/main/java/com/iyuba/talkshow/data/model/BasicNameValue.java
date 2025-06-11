package com.iyuba.talkshow.data.model;

/**
 * Created by Administrator on 2016/11/19 0019.
 */

public class BasicNameValue {
    private final String name;
    private final Object value;


    public BasicNameValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

}
