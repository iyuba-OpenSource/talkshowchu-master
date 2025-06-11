package com.iyuba.talkshow.data.model;

import java.util.List;

public class LessonNewResponse {

    public int result;
    public primaryData data;
    public String message;
    public List<String> lessonType;

    public class primaryData {
        public List<Series> junior;
    }
    public class Series {
        public List<SeriesDatas> SeriesData;
        public String SourceType;
    }
    public class SeriesDatas {
        public int Category;
        public String SeriesName;
        public String lessonName;
        public String isVideo;
    }

}
