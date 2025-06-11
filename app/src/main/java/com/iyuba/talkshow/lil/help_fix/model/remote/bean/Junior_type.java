package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @title: 中小学-类型分类
 * @date: 2023/5/19 13:58
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Junior_type implements Serializable {

    /**
     * SeriesData : [{"Category":"337","SeriesName":"新概念","isVideo":"0","lessonName":"concept"}]
     * SourceType : 新概念
     */

    private String SourceType;
    private List<SeriesDataBean> SeriesData;

    public String getSourceType() {
        return SourceType;
    }

    public List<SeriesDataBean> getSeriesData() {
        return SeriesData;
    }

    public static class SeriesDataBean {
        /**
         * Category : 337
         * SeriesName : 新概念
         * isVideo : 0
         * lessonName : concept
         */

        private String Category;
        private String SeriesName;
        private String isVideo;
        private String lessonName;

        public String getCategory() {
            return Category;
        }

        public String getSeriesName() {
            return SeriesName;
        }

        public String getIsVideo() {
            return isVideo;
        }

        public String getLessonName() {
            return lessonName;
        }
    }
}
