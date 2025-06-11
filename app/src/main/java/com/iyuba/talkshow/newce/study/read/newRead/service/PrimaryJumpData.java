package com.iyuba.talkshow.newce.study.read.newRead.service;

import com.iyuba.talkshow.data.model.Voa;

public class PrimaryJumpData {
    private static PrimaryJumpData instance;

    public static PrimaryJumpData getInstance(){
        if (instance==null){
            synchronized (PrimaryJumpData.class){
                if (instance==null){
                    instance = new PrimaryJumpData();
                }
            }
        }
        return instance;
    }

    //参数数据
    private Voa voa;
    private String jumpTitle;
    private int unit;
    private boolean isAuto;
    private int positionInList;

    public void setData(Voa voa, String jumpTitle, int unit, boolean isAuto, int positionInList) {
        this.voa = voa;
        this.jumpTitle = jumpTitle;
        this.unit = unit;
        this.isAuto = isAuto;
        this.positionInList = positionInList;
    }

    public Voa getVoa() {
        return voa;
    }

    public String getJumpTitle() {
        return jumpTitle;
    }

    public int getUnit() {
        return unit;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public int getPositionInList() {
        return positionInList;
    }
}
