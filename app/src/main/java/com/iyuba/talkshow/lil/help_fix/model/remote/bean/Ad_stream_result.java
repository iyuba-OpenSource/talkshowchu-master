package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

/**
 * @title: 广告回调(信息流)
 * @date: 2023/9/13 13:14
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Ad_stream_result {

    /**
     * result : 1
     * data : {"adId":"IOS voa常速 voa慢速信息流","firstLevel":"2","secondLevel":"2","thirdLevel":"2","title":"All Information Ad Test"}
     */

    private String result;
    private DataBean data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * adId : IOS voa常速 voa慢速信息流
         * firstLevel : 2
         * secondLevel : 2
         * thirdLevel : 2
         * title : All Information Ad Test
         */

        private String adId;
        private String firstLevel;
        private String secondLevel;
        private String thirdLevel;
        private String title;

        public String getAdId() {
            return adId;
        }

        public void setAdId(String adId) {
            this.adId = adId;
        }

        public String getFirstLevel() {
            return firstLevel;
        }

        public void setFirstLevel(String firstLevel) {
            this.firstLevel = firstLevel;
        }

        public String getSecondLevel() {
            return secondLevel;
        }

        public void setSecondLevel(String secondLevel) {
            this.secondLevel = secondLevel;
        }

        public String getThirdLevel() {
            return thirdLevel;
        }

        public void setThirdLevel(String thirdLevel) {
            this.thirdLevel = thirdLevel;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
