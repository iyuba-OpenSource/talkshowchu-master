package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @title: 积分扣除模型
 * @date: 2023/7/4 15:53
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Integral_deduct implements Serializable {


    /**
     * result : 200
     * addcredit : -20
     * totalcredit : 415
     */

    private String result;
    private String addcredit;
    private String totalcredit;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAddcredit() {
        return addcredit;
    }

    public void setAddcredit(String addcredit) {
        this.addcredit = addcredit;
    }

    public String getTotalcredit() {
        return totalcredit;
    }

    public void setTotalcredit(String totalcredit) {
        this.totalcredit = totalcredit;
    }
}
