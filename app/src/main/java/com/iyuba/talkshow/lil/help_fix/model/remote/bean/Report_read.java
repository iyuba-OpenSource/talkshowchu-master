package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @title: 学习报告-阅读回调
 * @date: 2023/8/4 17:16
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Root(name = "data",strict = false)
public class Report_read {

    @Element(required = false)
    public String result;
    @Element(required = false)
    public String message;
    @Element(required = false)
    public String jifen;
    @Element(required = false)
    public String reward;
    @Element(required = false)
    public String rewardMessage;
}
