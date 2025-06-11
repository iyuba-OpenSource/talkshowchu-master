package com.iyuba.talkshow.lil.help_fix.model.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 章节表-小说
 * @date: 2023/4/28 10:53
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description: 这里同一个类型下没有相同的voaid，但是不同的类型下肯可能存在相同的voaid
 */
@Entity(primaryKeys = {"voaid","types"})
public class ChapterEntity_novel {

    @NonNull
    public String voaid;
    public String orderNumber;
    public String level;
    public String chapterOrder;
    public String sound;
    @NonNull
    public int show;
    public String cname_cn;
    public String cname_en;

    //本地标记-类型信息
    @NonNull
    public String types;

    public ChapterEntity_novel() {

    }

    @Ignore
    public ChapterEntity_novel(String voaid, String orderNumber, String level, String chapterOrder, String sound, int show, String cname_cn, String cname_en, String types) {
        this.voaid = voaid;
        this.orderNumber = orderNumber;
        this.level = level;
        this.chapterOrder = chapterOrder;
        this.sound = sound;
        this.show = show;
        this.cname_cn = cname_cn;
        this.cname_en = cname_en;
        this.types = types;
    }
}
