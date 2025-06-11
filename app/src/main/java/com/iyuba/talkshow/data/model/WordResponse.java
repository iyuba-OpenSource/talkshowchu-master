package com.iyuba.talkshow.data.model;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "data", strict = false)
public class WordResponse {

    // @Root(strict = false) @注解(不严格检查)

    public List<Sent> getSents() {
        return sents;
    }

    public void setSents(List<Sent> sents) {
        this.sents = sents;
    }

    @Element(name = "def")

    private String def;
    @Element(name = "pron")
    private String pron;
    @Element(name = "key")
    private String key;
    @Element(name = "audio")
    private String audio;

    @ElementList(entry = "sent", inline = true)
    private List<Sent> sents;




    public WordResponse() {
    }

    public WordResponse(String key, String def, String pron, String audio) {
        this.key = key;
        this.def = def;
        this.pron = pron;
        this.audio = audio;
    }

    public String getDef() {
        return def == null ? "" : def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public String getPron() {
        return pron;
    }

    public void setPron(String pron) {
        this.pron = pron;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }


//        @Override
//        public String toString() {
//            return "Note{" +
//                    "from='" + from + '\'' +
//                    ", to='" + to + '\'' +
//                    ", message='" + message + '\'' +
//                    '}';
//        }
}
