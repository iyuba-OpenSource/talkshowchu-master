package com.iyuba.wordtest.entity;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 单词
 */
public class WordAiEntity implements Serializable {
    public int result;
    public String key = ""; // 单词
    public String audio = ""; // 多媒体网络路url
    public String pron = ""; // 音标
    public String proncode;
    public String def = ""; // 解释
    public String user_pron;
    public String ori_pron;
    @Nullable
    public List<List<Integer>> match_idx;
    @Nullable
    public List<List<Integer>> insert_id;
    @Nullable
    public List<List<Integer>> delete_id;
    @Nullable
    public List<List<Integer>> substitute_id;
    public ArrayList<ExampleSentence> sent;

}
