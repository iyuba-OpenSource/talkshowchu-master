//package com.iyuba.talkshow.data;
//
//import android.app.NotificationManager;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//import com.iyuba.talkshow.newdata.MyIjkPlayer;
//
//public class NotificationCloseReceiver extends BroadcastReceiver {
//
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (BackgroundManager.Instace().bindService!=null){
//            BackgroundManager.Instace().bindService.stopTimer();
//        }
//
//        try {
//            MyIjkPlayer player = BackgroundManager.Instace().bindService.getPlayer();
//            if ((player != null) && (player.isPlaying())) {
//                player.pause();
//            }
//
//            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            manager.cancelAll();
//            BackgroundManager.Instace().bindService.stopForeground(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
////        ((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE)).cancel(222);
//    }
//}
