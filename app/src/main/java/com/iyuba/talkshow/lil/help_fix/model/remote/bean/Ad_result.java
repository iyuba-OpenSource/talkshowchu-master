package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

/**
 * @title:
 * @date: 2023/7/28 15:47
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Ad_result {

    /**
     * result : 1
     * data : {"id":"2003","adId":"新概念英语ads3banner上线版","startuppic_StartDate":"2023-03-22","startuppic_EndDate":"2023-12-31","startuppic":"upload/1679480764284.jpg","type":"ads3","startuppic_Url":"http://app.iyuba.cn?appid=259&uid=0","classNum":"0","title":""}
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
         * id : 2003
         * adId : 新概念英语ads3banner上线版
         * startuppic_StartDate : 2023-03-22
         * startuppic_EndDate : 2023-12-31
         * startuppic : upload/1679480764284.jpg
         * type : ads3
         * startuppic_Url : http://app.iyuba.cn?appid=259&uid=0
         * classNum : 0
         * title :
         */

        private String id;
        private String adId;
        private String startuppic_StartDate;
        private String startuppic_EndDate;
        private String startuppic;
        private String type;
        private String startuppic_Url;
        private String classNum;
        private String title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAdId() {
            return adId;
        }

        public void setAdId(String adId) {
            this.adId = adId;
        }

        public String getStartuppic_StartDate() {
            return startuppic_StartDate;
        }

        public void setStartuppic_StartDate(String startuppic_StartDate) {
            this.startuppic_StartDate = startuppic_StartDate;
        }

        public String getStartuppic_EndDate() {
            return startuppic_EndDate;
        }

        public void setStartuppic_EndDate(String startuppic_EndDate) {
            this.startuppic_EndDate = startuppic_EndDate;
        }

        public String getStartuppic() {
            return startuppic;
        }

        public void setStartuppic(String startuppic) {
            this.startuppic = startuppic;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStartuppic_Url() {
            return startuppic_Url;
        }

        public void setStartuppic_Url(String startuppic_Url) {
            this.startuppic_Url = startuppic_Url;
        }

        public String getClassNum() {
            return classNum;
        }

        public void setClassNum(String classNum) {
            this.classNum = classNum;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
