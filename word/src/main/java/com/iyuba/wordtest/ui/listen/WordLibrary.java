package com.iyuba.wordtest.ui.listen;

/**
 * @title: 单词界面类型库
 * @date: 2023/11/29 14:28
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface WordLibrary {

    //单词听写类型
    class WordListenType{
        public static final String Listen_word = "spell";//单词拼写
        public static final String Listen_audio = "audio";//音频听写
        public static final String Write_word = "write";//单词手写
    }
}
