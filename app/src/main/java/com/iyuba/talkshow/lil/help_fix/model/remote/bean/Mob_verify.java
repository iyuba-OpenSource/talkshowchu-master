package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

/**
 * @title: mob秒验后校验接口的回调数据
 * @date: 2023/8/25 15:44
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Mob_verify {

    /**
     * res : {"valid":true,"phone":"15965642050","isValid":1}
     * isLogin : 1
     * userinfo : {"Amount":"0","mobile":"15965642050","message":"success","result":"101","uid":14594877,"isteacher":"0","expireTime":0,"money":"38","credits":0,"jiFen":0,"nickname":"qqwer0987qwer","vipStatus":"0","imgSrc":"http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg","email":"null","username":"qqwer0987qwer"}
     */

    private ResBean res;
    private String isLogin;
    private UserinfoBean userinfo;

    public ResBean getRes() {
        return res;
    }

    public void setRes(ResBean res) {
        this.res = res;
    }

    public String getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(String isLogin) {
        this.isLogin = isLogin;
    }

    public UserinfoBean getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserinfoBean userinfo) {
        this.userinfo = userinfo;
    }

    public static class ResBean {
        /**
         * valid : true
         * phone : 15965642050
         * isValid : 1
         */

        private boolean valid;
        private String phone;
        private int isValid;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getIsValid() {
            return isValid;
        }

        public void setIsValid(int isValid) {
            this.isValid = isValid;
        }
    }

    public static class UserinfoBean {
        /**
         * Amount : 0
         * mobile : 15965642050
         * message : success
         * result : 101
         * uid : 14594877
         * isteacher : 0
         * expireTime : 0
         * money : 38
         * credits : 0
         * jiFen : 0
         * nickname : qqwer0987qwer
         * vipStatus : 0
         * imgSrc : http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg
         * email : null
         * username : qqwer0987qwer
         */

        private String Amount;
        private String mobile;
        private String message;
        private String result;
        private int uid;
        private String isteacher;
        private int expireTime;
        private String money;
        private int credits;
        private int jiFen;
        private String nickname;
        private String vipStatus;
        private String imgSrc;
        private String email;
        private String username;

        public String getAmount() {
            return Amount;
        }

        public void setAmount(String Amount) {
            this.Amount = Amount;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getIsteacher() {
            return isteacher;
        }

        public void setIsteacher(String isteacher) {
            this.isteacher = isteacher;
        }

        public int getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(int expireTime) {
            this.expireTime = expireTime;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public int getCredits() {
            return credits;
        }

        public void setCredits(int credits) {
            this.credits = credits;
        }

        public int getJiFen() {
            return jiFen;
        }

        public void setJiFen(int jiFen) {
            this.jiFen = jiFen;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getVipStatus() {
            return vipStatus;
        }

        public void setVipStatus(String vipStatus) {
            this.vipStatus = vipStatus;
        }

        public String getImgSrc() {
            return imgSrc;
        }

        public void setImgSrc(String imgSrc) {
            this.imgSrc = imgSrc;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
