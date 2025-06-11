package com.iyuba.talkshow.lil.novel.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;

import org.greenrobot.eventbus.EventBus;

/**
 * @title:
 * @date: 2023/7/27 18:18
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class FixBgServiceCloseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //停止播放
        EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.read_stop));
        //停止计时

        //关闭服务
        FixBgServiceManager.getInstance().fixBgService.stopForeground(true);
        //关闭通知
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }
}
