package com.iyuba.talkshow.lil.novel.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.lil.help_fix.data.bean.BookChapterBean;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.ui.study.StudyActivity;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;

/**
 * @title:
 * @date: 2023/7/27 17:34
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class FixBgService extends Service {

    private static final int NOTIFICATION_ID = App.APP_ID;
    public static final String APP_NAME = ResUtil.getInstance().getString(R.string.app_name);
    //消息类型
    private static final String NOTIFICATION_NAME = APP_NAME+"通知";

    private final MyFixBinder myFixBinder = new MyFixBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myFixBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public class MyFixBinder extends Binder{
        public FixBgService getService(){
            return FixBgService.this;
        }
    }

    public void init(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerNotificationReceiver();

        showNotification(true,false,null,null,null,null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNotificationReceiver();
    }

    //真正的刷新通知
    public void updateNotification(String title, boolean isPlaying,String types,String bookId,String voaId){
        if (TextUtils.isEmpty(title)){
            return;
        }

        String playText = "正在播放";
        if (!isPlaying){
            playText = "暂停播放";
        }

//        if (isPlaying){
//            stopTimer();
//        }else {
//            startTimer();
//        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT > 26) {
            NotificationChannel channel = new NotificationChannel(String.valueOf(App.APP_ID), "初中英语通知", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("初中英语的通知消息");
            channel.setSound(null,null);
            channel.enableLights(false);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(this, String.valueOf(App.APP_ID))
                    .setOngoing(true)
                    .setContentTitle(playText)
                    .setContentText(title)
                    .setContentIntent(startStudy(types, bookId, voaId))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setTicker(playText)
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "关闭", receiveCloseIntent())
                    .setDeleteIntent(receiveCancelIntent())
                    .build();
            startForeground(App.APP_ID, notification);
        } else {
            Notification.Builder localBuilder = new Notification.Builder(this);
            localBuilder.setOngoing(true)
                    .setContentTitle(playText)
                    .setContentText(title)
                    .setContentIntent(startStudy(types, bookId, voaId))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setTicker(playText)
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "关闭", receiveCloseIntent())
                    .setDeleteIntent(receiveCancelIntent());

            startForeground(App.APP_ID, localBuilder.build());
        }
    }

    public void showNotification(boolean isInit,boolean isPlay,String title,String types,String bookId,String voaId){
        String showText = "";
        PendingIntent pendingIntent = null;

        if (isInit){
            title = APP_NAME;
            showText = title+"正在运行";
            pendingIntent = null;
        }else {
            if (isPlay){
                showText = "正在播放";
            }else {
                showText = "暂停播放";
            }
            pendingIntent = startStudy(types,bookId,voaId);
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(String.valueOf(NOTIFICATION_ID),NOTIFICATION_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setDescription(APP_NAME+"的通知消息");
            channel.setSound(null,null);
            manager.createNotificationChannel(channel);

            builder = new Notification.Builder(this,String.valueOf(NOTIFICATION_ID));
        }else {
            builder = new Notification.Builder(this);
        }

        builder.setOngoing(true);
        builder.setContentTitle(title);
        builder.setContentText(showText);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setTicker(showText);
        if (!isInit){
//            if (isPlay){
//                builder.addAction(android.R.drawable.ic_menu_close_clear_cancel,"暂停",getPlayCloseIntent(false));
//            }else {
//                builder.addAction(android.R.drawable.ic_menu_close_clear_cancel,"播放",getPlayCloseIntent(true));
//            }
            builder.addAction(android.R.drawable.ic_menu_close_clear_cancel,"关闭",receiveCloseIntent());
        }
        builder.build();
        startForeground(NOTIFICATION_ID,builder.build());
    }

    //跳转到学习界面
    private PendingIntent startStudy(String types,String bookId,String voaId){
        Intent intent = StudyActivity.buildIntent(this,types,bookId,voaId);
        return PendingIntent.getActivity(this,0,intent,getPendFlag());
    }

    private int getPendFlag(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
            return PendingIntent.FLAG_IMMUTABLE;
        }else {
            return PendingIntent.FLAG_UPDATE_CURRENT;
        }
    }

    /*************************回调操作*****************************/
    //新的接收器
    private FixBgServiceCancelReceiver cancelReceiver;
    private FixBgServiceCloseReceiver closeReceiver;

    private PendingIntent receiveCloseIntent() {
        Intent intent = new Intent("iyuhead.close");
        return PendingIntent.getBroadcast(this, 0, intent, getPendFlag());
    }

    private PendingIntent receiveCancelIntent() {
        Intent intent = new Intent("iyuhead.cancel");
        return PendingIntent.getBroadcast(this, 0, intent, getPendFlag());
    }

    //注册接收器
    private void registerNotificationReceiver(){
        IntentFilter intentFilter = new IntentFilter("iyuhead.close");
        this.closeReceiver = new FixBgServiceCloseReceiver();
        registerReceiver(closeReceiver, intentFilter);

        intentFilter = new IntentFilter("iyuhead.cancel");
        this.cancelReceiver = new FixBgServiceCancelReceiver();
        registerReceiver(cancelReceiver,intentFilter);
    }

    //取消接收器
    private void unregisterNotificationReceiver(){
        try {
            unregisterReceiver(closeReceiver);
            unregisterReceiver(cancelReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
