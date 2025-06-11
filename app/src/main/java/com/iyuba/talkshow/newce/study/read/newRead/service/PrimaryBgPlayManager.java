package com.iyuba.talkshow.newce.study.read.newRead.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;

/**
 * @title: 小学-后台播放管理
 * @date: 2023/10/27 11:07
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class PrimaryBgPlayManager {
    private static PrimaryBgPlayManager instance;
    private PrimaryBgPlayService playService;
    private ServiceConnection connection;

    public static PrimaryBgPlayManager getInstance(){
        if (instance==null){
            synchronized (PrimaryBgPlayManager.class){
                if (instance==null){
                    instance = new PrimaryBgPlayManager();
                }
            }
        }
        return instance;
    }

    public PrimaryBgPlayManager(){
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PrimaryBgPlayService.MyPrimaryPlayBinder binder = (PrimaryBgPlayService.MyPrimaryPlayBinder) service;
                playService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    //获取服务
    public PrimaryBgPlayService getPlayService(){
        if (playService==null){
            ToastUtil.showToast(ResUtil.getInstance().getContext(), "服务未进行初始化");
            return null;
        }

        return playService;
    }

    //获取链接
    public ServiceConnection getConnection(){
        return connection;
    }
}
