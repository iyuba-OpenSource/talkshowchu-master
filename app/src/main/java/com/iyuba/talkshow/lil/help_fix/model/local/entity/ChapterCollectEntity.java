package com.iyuba.talkshow.lil.help_fix.model.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 章节收藏表
 * @date: 2023/5/24 09:23
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Entity(primaryKeys = {"types","voaId","userId"})
public class ChapterCollectEntity {

    @NonNull
    public String types;//类型
    @NonNull
    public String voaId;//章节id
    @NonNull
    public String userId;//用户id

    public String bookId;//书籍id

    public String picUrl;//图片链接
    public String title;//标题
    public String desc;//描述

    public ChapterCollectEntity() {
    }

    @Ignore
    public ChapterCollectEntity(@NonNull String types, @NonNull String voaId, @NonNull String userId, String bookId, String picUrl, String title, String desc) {
        this.types = types;
        this.voaId = voaId;
        this.userId = userId;
        this.bookId = bookId;
        this.picUrl = picUrl;
        this.title = title;
        this.desc = desc;
    }
}
