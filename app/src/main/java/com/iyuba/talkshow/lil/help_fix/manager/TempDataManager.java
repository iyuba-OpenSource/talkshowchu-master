package com.iyuba.talkshow.lil.help_fix.manager;

/**
 * @title: 临时数据管理
 * @date: 2023/8/28 09:56
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class TempDataManager {
    private static TempDataManager instance;

    public static TempDataManager getInstance(){
        if (instance==null){
            synchronized (TempDataManager.class){
                if (instance==null){
                    instance = new TempDataManager();
                }
            }
        }
        return instance;
    }

    //秒验可用性检测
    private boolean isMobVerify = false;

    public void setMobVerify(boolean isVerify){
        this.isMobVerify = isVerify;
    }

    public boolean getMobVerify(){
        return isMobVerify;
    }
}
