package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @title:
 * @date: 2023/6/5 18:54
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 *
 *
<response>
<msg>Success</msg>
<result>1</result>
<type>insert</type>
<topic>primary</topic>
</response>
 */
@Root(name = "response",strict = false)
public class Collect_chapter {

    @Element(required = false)
    public String msg;
    @Element(required = false)
    public String result;
    @Element(required = false)
    public String type;
    @Element(required = false)
    public String topic;
}
