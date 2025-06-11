package com.iyuba.talkshow.lil.help_fix.model.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 点赞表
 * @date: 2023/5/25 18:02
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description: 用于评测排行榜详情点赞和配音排行榜详情的点赞
 */
@Entity(primaryKeys = {"userId","agreeUserId","types","voaId","evalSentenceId"})
public class AgreeEntity {

    @NonNull
    public String userId;//本用户的id
    @NonNull
    public String agreeUserId;//点赞用户的id
    @NonNull
    public String types;//类型
    @NonNull
    public String voaId;//章节id

    @NonNull
    public String evalSentenceId;//评测句子的id

    public AgreeEntity() {
    }

    @Ignore
    public AgreeEntity(@NonNull String userId, @NonNull String agreeUserId, @NonNull String types, @NonNull String voaId, @NonNull String evalSentenceId) {
        this.userId = userId;
        this.agreeUserId = agreeUserId;
        this.types = types;
        this.voaId = voaId;
        this.evalSentenceId = evalSentenceId;
    }
}
