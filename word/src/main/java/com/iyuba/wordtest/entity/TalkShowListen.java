package com.iyuba.wordtest.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.io.Serializable;

/**
 * @desction:  单词听写类
 * @date: 2023/2/7 15:00
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
@Entity(primaryKeys = {"book_id","unit_id","position","uid"})
public class TalkShowListen implements Serializable {

    //定位点
    @NonNull
    public int book_id;//课程id
    @NonNull
    public int unit_id;//单元id
    @NonNull
    public int position;//位置
    @NonNull
    public String uid;//用户id

    public String word;//单词
    public String porn;//音标
    public String def;//释义
    public String audio;//音频

    public String spell;//拼写的内容
    @NonNull
    public int status;//是否正确(1-正确，0-错误)
    @NonNull
    public int error_count;//错误次数
    public String update_time;//更新时间
}
