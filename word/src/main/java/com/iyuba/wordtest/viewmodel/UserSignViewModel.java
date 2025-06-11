package com.iyuba.wordtest.viewmodel;

import java.io.Serializable;

public class UserSignViewModel implements Serializable {

    String stage ;
    String totalWords ;
    String rate ;
    String username ;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    String userid ;

    public UserSignViewModel(String stage, String totalWords, String rate, String username,String userid) {
        this.stage = stage;
        this.totalWords = totalWords;
        this.rate = rate;
        this.username = username;
        this.userid = userid;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(String totalWords) {
        this.totalWords = totalWords;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



//    @BindingAdapter("userimage")
//    public static void getUserImage(ImageView iv, String userid) {
//      String useridString = "  http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&uid="
//                + "0" + "&size=middle";
//        Glide.with(iv.getContext()).load(useridString).into(iv);
//    }
}
