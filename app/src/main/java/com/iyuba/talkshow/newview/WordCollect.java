package com.iyuba.talkshow.newview;

import androidx.annotation.Keep;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * 从服务器获取 用户收藏的单词
 * Created by 10202 on 2015/10/8.
 */
@Keep
@Root(name = "response", strict = false)
public class WordCollect {


    @Element(required = false)
    public int counts;
    @Element(required = false)
    public int pageNumber;
    @Element(required = false)
    public int totalPage;
    @Element(required = false)
    public int firstPage;
    @Element(required = false)
    public int prevPage;
    @Element(required = false)
    public int nextPage;
    @Element(required = false)
    public int lastPage;

    @ElementList(required = true, inline = true, entry = "row")
    public ArrayList<Word> wordList;

    //<row>
//<Word>bench</Word>
//<Audio>
//    http://res.iciba.com/resource/amp3/0/0/1d/28/1d28d258250876fb7dde22a17436ef9c.mp3
//</Audio>
//<Pron>bentʃ</Pron>
//<Def>（木制）长凳，工作台；法官，法官席；（英国议会的）议员席；场边的运动员休息区；</Def>
//</row>
    public static class Word {
        public String user;
        public String createDate;

        public String Word ;
        public String Pron;
        public String Audio;
        public String Def ;

        @Override
        public boolean equals(Object o) {
            if(o==null||Word==null)
                return false;
            Word compare = (Word) o;
            if (this.Word.equals(compare.Word)) {
                return this.user.equals(compare.user);
            }
            return false;
        }
    }


}
