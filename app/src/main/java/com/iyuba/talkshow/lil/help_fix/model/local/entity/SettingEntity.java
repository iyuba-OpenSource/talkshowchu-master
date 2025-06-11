package com.iyuba.talkshow.lil.help_fix.model.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 用户设置表
 * @date: 2023/5/23 18:56
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Entity(primaryKeys = {"userId","type"})
public class SettingEntity {

    @NonNull
    public String userId;//用户id
    @NonNull
    public String type;//类型
    public String data;//数值

    public SettingEntity() {
    }

    @Ignore
    public SettingEntity(@NonNull String userId, @NonNull String type, String data) {
        this.userId = userId;
        this.type = type;
        this.data = data;
    }
}
