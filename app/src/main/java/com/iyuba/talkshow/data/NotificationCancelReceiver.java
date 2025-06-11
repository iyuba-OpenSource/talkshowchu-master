//package com.iyuba.talkshow.data;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//import com.iyuba.talkshow.newdata.MyIjkPlayer;
//
///**
// * @desction:
// * @date: 2023/3/19 15:53
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// */
//public class NotificationCancelReceiver extends BroadcastReceiver {
//
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        BackgroundManager.Instace().bindService.stopTimer();
//
//        try {
//            MyIjkPlayer player = BackgroundManager.Instace().bindService.getPlayer();
//            if ((player != null) && (player.isPlaying())) {
//                player.pause();
//            }
//
//            BackgroundManager.Instace().bindService.stopForeground(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
