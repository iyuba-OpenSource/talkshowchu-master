package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @title: 中小学-书籍数据
 * @date: 2023/5/19 13:56
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Junior_book implements Serializable {


    /**
     * Category : 313
     * CreateTime : 2020-02-15 06:02:14.0
     * isVideo : 1
     * pic : http://static2.iyuba.cn/images/voaseries/205.jpg
     * KeyWords : 小学英语
     * version : 123
     * DescCn : New starting point for grade 1
     * SeriesCount : 33
     * SeriesName : 1年级上(新起点)
     * UpdateTime : 2021-12-07 06:12:21.0
     * HotFlg : 0
     * haveMicro : 0
     * Id : 205
     */

    private String Category;
    private String CreateTime;
    private String isVideo;
    private String pic;
    private String KeyWords;
    private String version;
    private String DescCn;
    private String SeriesCount;
    private String SeriesName;
    private String UpdateTime;
    private String HotFlg;
    private String haveMicro;
    private String Id;

    public String getCategory() {
        return Category;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public String getIsVideo() {
        return isVideo;
    }

    public String getPic() {
        return pic;
    }

    public String getKeyWords() {
        return KeyWords;
    }

    public String getVersion() {
        return version;
    }

    public String getDescCn() {
        return DescCn;
    }

    public String getSeriesCount() {
        return SeriesCount;
    }

    public String getSeriesName() {
        return SeriesName;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public String getHotFlg() {
        return HotFlg;
    }

    public String getHaveMicro() {
        return haveMicro;
    }

    public String getId() {
        return Id;
    }
}
