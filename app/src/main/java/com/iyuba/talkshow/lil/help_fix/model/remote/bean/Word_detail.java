package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @title: 查询单词的内容
 * @date: 2023/6/12 17:55
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Root(name = "data",strict = false)
public class Word_detail {

    @Element(required = false)
    public String result;
    @Element(required = false)
    public String key;
    @Element(required = false)
    public String audio;
    @Element(required = false)
    public String pron;
    @Element(required = false)
    public String proncode;
    @Element(required = false)
    public String def;

    @Root(name = "sent",strict = false)
    public static class Sent{
        @Element(required = false)
        public int number;
        @Element(required = false)
        public String orig;
        @Element(required = false)
        public String trans;
    }
}
