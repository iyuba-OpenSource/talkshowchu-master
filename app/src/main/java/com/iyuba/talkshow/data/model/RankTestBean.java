package com.iyuba.talkshow.data.model;

import java.util.List;

/**
 * @desction:
 * @date: 2023/2/9 15:58
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class RankTestBean {

    /**
     * totalRight : 0
     * result : 3
     * totalTest : 0
     * myimgSrc : http://static1.iyuba.cn/uc_server/head/2023/1/3/17/48/38/29b191cb-36ee-4b40-9b57-3776684e543d-m.jpg
     * myid : 12230749
     * myranking : 0
     * data : [{"totalRight":1232,"uid":9689580,"totalTest":1269,"name":"逢考必过123456","ranking":1,"sort":1,"imgSrc":"http://static1.iyuba.cn/uc_server/head/2023/0/30/21/31/8/af3f7e5f-4be8-4521-b6db-361049e18f7e-m.jpg"},{"totalRight":585,"uid":13464499,"totalTest":586,"name":"yuanwm","ranking":2,"sort":2,"imgSrc":"http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg"},{"totalRight":572,"uid":13650368,"totalTest":588,"name":"tanyunlian","ranking":3,"sort":3,"imgSrc":"http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg"}]
     * myname : qwer123hgf
     * message : Success
     */

    private int totalRight;
    private int result;
    private int totalTest;
    private String myimgSrc;
    private int myid;
    private int myranking;
    private String myname;
    private String message;
    private List<DataBean> data;

    public int getTotalRight() {
        return totalRight;
    }

    public void setTotalRight(int totalRight) {
        this.totalRight = totalRight;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getTotalTest() {
        return totalTest;
    }

    public void setTotalTest(int totalTest) {
        this.totalTest = totalTest;
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
         * totalRight : 1232
         * uid : 9689580
         * totalTest : 1269
         * name : 逢考必过123456
         * ranking : 1
         * sort : 1
         * imgSrc : http://static1.iyuba.cn/uc_server/head/2023/0/30/21/31/8/af3f7e5f-4be8-4521-b6db-361049e18f7e-m.jpg
         */

        private int totalRight;
        private int uid;
        private int totalTest;
        private String name;
        private int ranking;
        private int sort;
        private String imgSrc;

        public int getTotalRight() {
            return totalRight;
        }

        public void setTotalRight(int totalRight) {
            this.totalRight = totalRight;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getTotalTest() {
            return totalTest;
        }

        public void setTotalTest(int totalTest) {
            this.totalTest = totalTest;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public String getImgSrc() {
            return imgSrc;
        }

        public void setImgSrc(String imgSrc) {
            this.imgSrc = imgSrc;
        }
    }
}
