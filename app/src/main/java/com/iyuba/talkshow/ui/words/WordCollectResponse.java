package com.iyuba.talkshow.ui.words;

import android.text.TextUtils;

import com.iyuba.module.toolbox.SingleParser;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

import io.reactivex.Single;

public interface WordCollectResponse {

    @Root(name = "data")
    class Dict implements SingleParser<Dict> {
        public static final Dict sEmpty = new Dict();

        @Element(name = "result")
        int result;
        @Element(name = "key")
        public String word;
        @Element(name = "audio", required = false)
        public String audio;
        @Element(name = "pron", required = false)
        String pron;
        @Element(name = "proncode", required = false)
        public String pronunciation;
        @Element(name = "def", required = false)
        public String definition;
        @ElementList(required = false, inline = true)
        public List<TempExample> examples;

        public boolean isEmpty() {
            return this == sEmpty;
        }

        @Root(name = "sent")
        public static class TempExample {
            @Element(name = "number")
            int id;
            @Element(name = "orig")
            public String original;
            @Element(name = "trans")
            public String translation;
        }

        public String getAudio() {
            return TextUtils.isEmpty(audio) ? "" : audio;
        }

        public String getPronunciation() {
            return TextUtils.isEmpty(pronunciation) ? "" : pronunciation;
        }

        public String getExamples() {
            StringBuilder sb = new StringBuilder();
            if (examples != null) {
                for (TempExample example : examples) {
                    sb.append(example.original).append("<br />")
                            .append(example.translation).append("<br />").append("<br />");
                }
            }
            return sb.toString();
        }

        @Override
        public Single<Dict> parse() {
            if (result == 1) {
                return Single.just(this);
            } else {
                return Single.error(new Throwable("request failed."));
            }
        }
    }

    @Root(name = "response")
    class Update implements SingleParser<Boolean> {
        @Element(name = "result")
        public int result;
        @Element(name = "word")
        public String word;

        @Override
        public Single<Boolean> parse() {
            return Single.just(result > 0);
        }
    }

    @Root(name = "response", strict = false)
    class GetNoteWords {
        @Element(name = "counts")
        public int counts;
        @Element(name = "pageNumber")
        public int pageNumber;
        @Element(name = "totalPage")
        public int totalPage;
        @Element(name = "firstPage")
        public int firstPage;
        @Element(name = "prevPage")
        public int prevPage;
        @Element(name = "nextPage")
        public int nextPage;
        @Element(name = "lastPage")
        public int lastPage;
        @ElementList(required = false, inline = true)
        public List<TempWord> tempWords;

        @Root(name = "row", strict = false)
        public static class TempWord {
            @Element(name = "Word",required = false )
            public String word;
            @Element(name = "Audio", required = false)
            public String audioUrl;
            @Element(name = "Pron", required = false)
            public String pronunciation;
            @Element(name = "Def", required = false)
            public String definition;
        }
    }

}
