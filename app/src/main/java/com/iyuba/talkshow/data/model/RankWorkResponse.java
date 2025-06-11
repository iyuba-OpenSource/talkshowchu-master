package com.iyuba.talkshow.data.model;

import java.util.List;

public class RankWorkResponse {

    /**
     * result : 1
     * total : 1
     * data : [{"DescCn":"Pig Peggy Album","Category":"309","SeriesName":"小猪佩奇专辑","CreateTime":"2019-06-19 01:06:34.0","UpdateTime":"2019-06-19 01:06:34.0","HotFlg":"0","Id":"201","pic":"http://apps.iyuba.cn/iyuba/images/voaseries/201.jpg","KeyWords":"动画片"}]
     * message : success
     */

    public boolean result;
    public int count;
    public String message;
    public List<RankWork> data;

}
