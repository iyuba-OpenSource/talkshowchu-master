package com.iyuba.wordtest.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "OfficialAccount")
public class OfficialAccount {
    @PrimaryKey
    @NonNull
    public int id;
    public String newsfrom;
    public String createTime;
    public String image_url;
    public String title;
    public String url;
    public int count;
}
