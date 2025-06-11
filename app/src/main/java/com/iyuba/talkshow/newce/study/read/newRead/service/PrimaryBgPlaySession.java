package com.iyuba.talkshow.newce.study.read.newRead.service;

import com.iyuba.talkshow.data.model.Voa;

import java.util.List;

/**
 * @title: 新概念-后台播放会话
 * @date: 2023/10/27 11:08
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class PrimaryBgPlaySession {
    private static PrimaryBgPlaySession instance;

    public static PrimaryBgPlaySession getInstance(){
        if (instance==null){
            synchronized (PrimaryBgPlaySession.class){
                if (instance==null){
                    instance = new PrimaryBgPlaySession();
                }
            }
        }
        return instance;
    }

    //当前全部的章节列表数据
    private List<Voa> voaList;

    public List<Voa> getVoaList() {
        return voaList;
    }

    public void setVoaList(List<Voa> voaList) {
        this.voaList = voaList;
    }

    //当前选中的位置（将要去的位置）
    private int playPosition = -1;

    public int getPlayPosition() {
        return playPosition;
    }

    public void setPlayPosition(int playPosition) {
        this.playPosition = playPosition;
    }

    //获取当前的数据（只能在外边用）
    public Voa getCurData(){
        if (voaList!=null&&voaList.size()>0&&playPosition!=-1){
            return voaList.get(playPosition);
        }
        return null;
    }

    //这里增加临时数据位，表示当前数据为临时数据，仅仅播放一次
    private boolean tempData = false;

    public void setTempData(boolean tempData) {
        this.tempData = tempData;
    }

    public boolean isTempData() {
        return tempData;
    }

    //上一个已经播放的音频
    private int preVoaId = 0;

    public void setPreVoaId(int voaId){
        this.preVoaId = voaId;
    }

    public int getPreVoaId() {
        return preVoaId;
    }
}
