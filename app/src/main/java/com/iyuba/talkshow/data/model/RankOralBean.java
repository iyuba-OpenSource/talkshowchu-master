package com.iyuba.talkshow.data.model;

import android.text.TextUtils;

import java.util.List;

/**
 * RankingListBean
 *
 * @author wayne
 * @date 2018/2/6
 */
public class RankOralBean {
    /**
     * result : 20
     * myimgSrc : http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/head/2018/0/30/18/17/12/755abbaf-9f5a-4c07-9750-6007589066c8-m.jpg
     * myid : 4768385
     * myranking : 113
     * data : [{"uid":3722319,"scores":95,"name":"Do天未晴","count":1,"ranking":1,"sort":1,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/head/2016/10/10/15/29/52/922c3f2b-bf9a-42ca-92ee-c5ec3bbd2d77-m.jpg"},{"uid":51033929,"scores":95,"name":"none","count":1,"ranking":2,"sort":2,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/images/noavatar_middle.jpg"},{"uid":2912379,"scores":95,"name":"pcxy","count":1,"ranking":3,"sort":3,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/images/noavatar_middle.jpg"},{"uid":3945101,"scores":95,"name":"宵征1997","count":1,"ranking":4,"sort":4,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/head/2018/1/6/2/3/33/1dbb8e08-62ad-4b55-9169-24d55459f454-m.jpg"},{"uid":5226912,"scores":94,"name":"小天cherub","count":1,"ranking":5,"sort":5,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/images/noavatar_middle.jpg"},{"uid":4791853,"scores":93,"name":"dnnnnnn1079","count":1,"ranking":6,"sort":6,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/images/noavatar_middle.jpg"},{"uid":5226348,"scores":93,"name":"yyyty彤","count":1,"ranking":7,"sort":7,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/images/noavatar_middle.jpg"},{"uid":52697312,"scores":93,"name":"none","count":1,"ranking":8,"sort":8,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/images/noavatar_middle.jpg"},{"uid":5226652,"scores":93,"name":"聖芮wr","count":1,"ranking":9,"sort":9,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/head/2018/1/5/11/21/56/9b4edfcc-1467-415a-adc4-2a27706f610d-m.jpg"},{"uid":5226631,"scores":93,"name":"522813255","count":1,"ranking":10,"sort":10,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/images/noavatar_middle.jpg"},{"uid":5220335,"scores":93,"name":"阿米果","count":1,"ranking":11,"sort":11,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/head/2018/1/2/19/25/23/11281563-d325-44d7-8c75-1de692c1368a-m.jpg"},{"uid":4749959,"scores":92,"name":"xyp96676","count":1,"ranking":12,"sort":12,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/head/2017/9/5/21/1/59/2c738738-bbbe-4118-a94d-d131fb1fbc12-m.jpg"},{"uid":4742442,"scores":92,"name":"Chesh","count":1,"ranking":13,"sort":13,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/head/2018/1/1/9/46/7/43da403f-145a-42c6-a4ed-9b95aea69115-m.jpg"},{"uid":5215875,"scores":92,"name":"shycan","count":1,"ranking":14,"sort":14,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/head/2018/0/31/19/50/23/d685f3b8-b7cd-4fe1-8f14-cefe1239322b-m.jpg"},{"uid":51423848,"scores":92,"name":"none","count":1,"ranking":15,"sort":15,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/images/noavatar_middle.jpg"},{"uid":5208120,"scores":92,"name":"夜海云声","count":1,"ranking":16,"sort":16,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/head/2018/0/28/8/38/59/684b5d92-1f87-4035-8975-ddbbf6f422e5-m.jpg"},{"uid":5227157,"scores":92,"name":"Dark fantasy","count":1,"ranking":17,"sort":17,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/head/2018/1/5/15/16/8/6cc5533b-43b0-45b5-a191-a93e9dfaca42-m.jpg"},{"uid":50323345,"scores":92,"name":"none","count":1,"ranking":18,"sort":18,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/images/noavatar_middle.jpg"},{"uid":3786446,"scores":91,"name":"liuchichi","count":1,"ranking":19,"sort":19,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/head/2017/7/3/10/34/37/8c81174a-214a-457f-a852-f43381e5e7a2-m.jpg"},{"uid":5218021,"scores":91,"name":"Leafyjam","count":1,"ranking":20,"sort":20,"vip":"0","imgSrc":"http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/images/noavatar_middle.jpg"}]
     * myname : 5367
     * myscores : 0
     * mycount : 0
     * vip : 2
     * message : Success
     */

    private int result;
    private String myimgSrc;
    private int myid;
    private int myranking;
    private String myname;
    private int myscores;
    private int mycount;
    private String vip;
    private String message;
    private List<DataBean> data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMyimgSrc() {
        return myimgSrc;
    }

    public void setMyimgSrc(String myimgSrc) {
        this.myimgSrc = myimgSrc;
    }

    public int getMyid() {
        return myid;
    }

    public void setMyid(int myid) {
        this.myid = myid;
    }

    public int getMyranking() {
        return myranking;
    }

    public void setMyranking(int myranking) {
        this.myranking = myranking;
    }

    public String getMyname() {
        return myname;
    }

    public void setMyname(String myname) {
        this.myname = myname;
    }

    public int getMyscores() {
        return myscores;
    }

    public void setMyscores(int myscores) {
        this.myscores = myscores;
    }

    public int getMycount() {
        return mycount;
    }

    public void setMycount(int mycount) {
        this.mycount = mycount;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * uid : 3722319
         * scores : 95
         * name : Do天未晴
         * count : 1
         * ranking : 1
         * sort : 1
         * vip : 0
         * imgSrc : http://static1."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"uc_server/head/2016/10/10/15/29/52/922c3f2b-bf9a-42ca-92ee-c5ec3bbd2d77-m.jpg
         */

        private int uid;
        private int scores;
        private String name;
        private int count;
        private int ranking;
        private int sort;
        private String vip;
        private String imgSrc;

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getScores() {
            return scores;
        }

        public void setScores(int scores) {
            this.scores = scores;
        }

        public String getName() {
            if (TextUtils.isEmpty(name)) {
                return uid + "";
            }
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getRanking() {
            return ranking;
        }

        public void setRanking(int ranking) {
            this.ranking = ranking;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getVip() {
            return vip;
        }

        public void setVip(String vip) {
            this.vip = vip;
        }

        public String getImgSrc() {
            return imgSrc;
        }

        public void setImgSrc(String imgSrc) {
            this.imgSrc = imgSrc;
        }
    }
}
