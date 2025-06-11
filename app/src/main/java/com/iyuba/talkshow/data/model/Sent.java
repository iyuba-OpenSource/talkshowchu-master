package com.iyuba.talkshow.data.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class  Sent{
    @Element(name = "orig" , required = false)
    public String orig ;

    @Element(name = "trans" , required = false)
    public String trans ;

    @Element(name = "number" , required = false)
    public String number ;
}