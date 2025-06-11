package com.iyuba.talkshow.lil.help_fix.data.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @title:
 * @date: 2023/6/7 16:39
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingPreviewSubmitBean implements Serializable {

    /**
     * appName : primaryEnglish
     * category : 313
     * flag : 1
     * format : json
     * idIndex : 0
     * paraId : 0
     * platform : android
     * score : 62
     * shuoshuotype : 3
     * sound : /202002/313027.mp3
     * topic : primaryenglish
     * username : aiyuba_lil
     * voaid : 313027
     * wavList : [{"URL":"wav8/202306/primaryenglish/20230606/16859811256556334.mp3","beginTime":6.1,"duration":1.8,"endTime":8,"index":2},{"URL":"wav8/202306/primaryenglish/20230607/16861261070600446.mp3","beginTime":9.9,"duration":2.6,"endTime":12.1,"index":4}]
     */

    private String appName;
    private int category;
    private int flag;
    private String format;
    private int idIndex;
    private int paraId;
    private String platform;
    private int score;
    private int shuoshuotype;
    private String sound;
    private String topic;
    private String username;
    private int voaid;
    private List<WavListBean> wavList;

    public DubbingPreviewSubmitBean(String appName, int category, int flag, String format, int idIndex, int paraId, String platform, int score, int shuoshuotype, String sound, String topic, String username, int voaid, List<WavListBean> wavList) {
        this.appName = appName;
        this.category = category;
        this.flag = flag;
        this.format = format;
        this.idIndex = idIndex;
        this.paraId = paraId;
        this.platform = platform;
        this.score = score;
        this.shuoshuotype = shuoshuotype;
        this.sound = sound;
        this.topic = topic;
        this.username = username;
        this.voaid = voaid;
        this.wavList = wavList;
    }

    public static class WavListBean {
        /**
         * URL : wav8/202306/primaryenglish/20230606/16859811256556334.mp3
         * beginTime : 6.1
         * duration : 1.8
         * endTime : 8.0
         * index : 2
         */

        @SerializedName("URL")
        private String url;
        private double beginTime;
        private double duration;
        private double endTime;
        private int index;

        public WavListBean(String url, double beginTime, double duration, double endTime, int index) {
            this.url = url;
            this.beginTime = beginTime;
            this.duration = duration;
            this.endTime = endTime;
            this.index = index;
        }

        public String getUrl() {
            return url;
        }

        public double getBeginTime() {
            return beginTime;
        }

        public double getDuration() {
            return duration;
        }

        public double getEndTime() {
            return endTime;
        }

        public int getIndex() {
            return index;
        }
    }
}
