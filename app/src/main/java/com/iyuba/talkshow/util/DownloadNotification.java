package com.iyuba.talkshow.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.widget.RemoteViews;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;

public class DownloadNotification extends ContextWrapper {

    private NotificationManager manager;
    String id = App.APP_NAME_EN;
    private RemoteViews remoteViews;
    private Notification notification;
    private PendingIntent contentIntent;
    private String name;
    private NotificationChannel channel;

    public DownloadNotification(Context context) {
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        name = id + "channel";
        channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableVibration(false);
        channel.setVibrationPattern(new long[]{0});
        manager.createNotificationChannel(channel);
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public void init() {
        getManager();
        remoteViews = createRemoteView();
        contentIntent = PendingIntent.getActivity(this, R.string.app_name, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotification() {
        Notification.DecoratedCustomViewStyle viewStyle = new Notification.DecoratedCustomViewStyle();
        return new Notification.Builder(getApplicationContext(), id)
                .setContent(remoteViews)
//                .setDefaults(Notification.DEFAULT_ALL)//干掉震动
//                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
//                .setContentTitle("")
                .setStyle(viewStyle)
//                .setContentText("")
                .setChannelId(id)
                .setSmallIcon(R.drawable.ic_launcher);
//                .setChannelId("123");
//                .setAutoCancel(true);
    }

    public NotificationCompat.Builder getNotification_25() {
        return new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true);
    }

    public void sendNotification() {
        if (manager == null) {
            init();
        }
        remoteViews.setTextViewText(R.id.tv_progress, "准备下载");
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            notification = getChannelNotification
                    ().build();
            notification.contentIntent = contentIntent;
        } else {
            notification = getNotification_25
                    ().build();
            notification.contentView = remoteViews;
//            notification.contentIntent = contentIntent;
//            manager.notify(1, notification);
        }
        manager.notify(1,notification);
    }

    public Notification initNotification() {
        if (manager == null) {
            init();
        }
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            notification = getChannelNotification
                    ().build();
            notification.contentIntent = contentIntent;
        } else {
            notification = getNotification_25
                    ().build();
            notification.contentView = remoteViews;
//            notification.contentIntent = contentIntent;
//            manager.notify(1, notification);
        }
        return  notification ;
    }

    private RemoteViews createRemoteView() {
        remoteViews = new RemoteViews(getPackageName(), R.layout.download_noti);
        //为remoteView设置图片和文本
        remoteViews.setTextViewText(R.id.tv_progress, "准备下载");
        remoteViews.setProgressBar(R.id.progress, 100, 0, false);
        return remoteViews;
    }

    public void setProress(String bookName , String msg) {
        remoteViews.setProgressBar(R.id.progress, 100, Integer.parseInt(msg), false);
        remoteViews.setTextViewText(R.id.tv_progress, bookName+":"+msg + "%");
        manager.notify(1, notification);
    }


    public void cancel() {
        manager.cancel(1);
    }
}
