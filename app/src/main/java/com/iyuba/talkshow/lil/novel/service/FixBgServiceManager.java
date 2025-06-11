package com.iyuba.talkshow.lil.novel.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.iyuba.talkshow.TalkShowApplication;

/**
 * @title:
 * @date: 2023/7/27 18:19
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class FixBgServiceManager {
    private static FixBgServiceManager instance;
    private static Context context;
    public FixBgService fixBgService;
    public ServiceConnection connection;

    public FixBgServiceManager(){
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                FixBgService.MyFixBinder myFixBinder = (FixBgService.MyFixBinder) service;
                fixBgService = myFixBinder.getService();
                fixBgService.init();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    public static synchronized FixBgServiceManager getInstance(){
        context = TalkShowApplication.getInstance();
        if (instance==null){
            instance = new FixBgServiceManager();
        }
        return instance;
    }
}
