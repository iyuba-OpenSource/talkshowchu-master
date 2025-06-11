package com.iyuba.wordtest.entity;

import androidx.annotation.Keep;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 单词  单个单词
 * @author 陈彤
 */
@Keep
@Root(name = "data", strict = false)
public class WordEntity implements Serializable {
    @Element(required = false)
    public int result;
    @Element(required = false)
    public String key = ""; // 单词
    @Element(required = false)
    public String audio = ""; // 多媒体网络路url
    @Element(required = false)
    public String pron = ""; // 音标
    @Element(required = false)
    public String proncode;
    @Element(required = false)
    public String def = ""; // 解释
    @Element(required = false)
    public String lang = "";
    @Element(required = false)
    public int voa = 0;
    @Element(required = false)
    public int book = 0;
    @Element(required = false)
    public int unit = 0;
    @ElementList(required = false, inline = true, entry = "sent")
    public ArrayList<ExampleSentence> sent;

}
