package com.iyuba.wordtest.entity;

import androidx.annotation.Keep;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * xml 服务器获取单词的例句
 * Created by liuzhenli on 2017/5/5.
 */
@Keep
@Root(name = "sent", strict = false)
public class ExampleSentence implements Serializable {
    @Element(required = false)
    public int number;
    @Element(required = false)
    public String orig;
    @Element(required = false)
    public String trans;
}