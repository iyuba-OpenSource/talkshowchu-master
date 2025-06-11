package com.iyuba.talkshow.data.model;

import java.util.List;

public class TitleSeriesResponse {

    /**
     * result : 1
     * total : 1
     * data : [{"DescCn":"Pig Peggy Album","Category":"309","SeriesName":"小猪佩奇专辑","CreateTime":"2019-06-19 01:06:34.0","UpdateTime":"2019-06-19 01:06:34.0","HotFlg":"0","Id":"201","pic":"http://apps.iyuba.cn/iyuba/images/voaseries/201.jpg","KeyWords":"动画片"}]
     * message : success
     */

    private int result;
    private int total;
    private List<TitleSeries> data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }


    public List<TitleSeries> getData() {
        return data;
    }

    public void setData(List<TitleSeries> data) {
        this.data = data;
    }

}
