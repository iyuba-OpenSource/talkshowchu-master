package com.iyuba.talkshow.lil.help_fix.model.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 书籍表-中小学
 * @date: 2023/5/22 11:18
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Entity(primaryKeys = {"bookId"})
public class BookEntity_junior {

    public String typeId;//类型id
    public String createTime;
    public String isVideo;
    public String pic;
    public String KeyWords;
    public String version;
    public String descCn;
    public String seriesCount;
    public String seriesName;
    public String updateTime;
    public String hotFlg;
    public String haveMicro;
    @NonNull
    public String bookId;//书籍id

    //本地数据-类型
    public String types;

    public BookEntity_junior() {
    }

    @Ignore
    public BookEntity_junior(String typeId, String createTime, String isVideo, String pic, String keyWords, String version, String descCn, String seriesCount, String seriesName, String updateTime, String hotFlg, String haveMicro, String bookId, String types) {
        this.typeId = typeId;
        this.createTime = createTime;
        this.isVideo = isVideo;
        this.pic = pic;
        KeyWords = keyWords;
        this.version = version;
        this.descCn = descCn;
        this.seriesCount = seriesCount;
        this.seriesName = seriesName;
        this.updateTime = updateTime;
        this.hotFlg = hotFlg;
        this.haveMicro = haveMicro;
        this.bookId = bookId;
        this.types = types;
    }
}
