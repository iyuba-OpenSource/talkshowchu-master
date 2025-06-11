package com.iyuba.talkshow.data.model.result;


import java.util.Arrays;
import java.util.List;


public class Series {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private  String name;
    private  String id;

    public Series(String name, String id) {
        this.name = name ;
        this.id  = id ;
    }

    public  static List<Series> getSeries(){
        return Arrays.asList(
                new Series("六年级下", getId(1)),
                new Series("六年级上", getId(2)),
                new Series("五年级下", getId(3)),
                new Series("五年级上", getId(4)),
                new Series("四年级下", getId(5)),
                new Series("四年级上", getId(6)),
                new Series("三年级下", getId(7)),
                new Series("三年级上", getId(8)),
                new Series("二年级下", getId(9)),
                new Series("二年级上", getId(10)),
                new Series("一年级下", getId(11)),
                new Series("一年级上", getId(12))
                );
    }

    private static String getId(int i) {
        return String.valueOf(217-i);
    }
}
