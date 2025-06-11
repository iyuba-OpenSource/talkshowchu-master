//package com.iyuba.talkshow.data;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//import com.iyuba.talkshow.newdata.MyIjkPlayer;
//
//public class NotificationPlayReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context paramContext, Intent paramIntent) {
//        if (BackgroundManager.Instace().bindService == null) {
//            return;
//        }
//        MyIjkPlayer player = BackgroundManager.Instace().bindService.getPlayer();
//        if ((player != null) && (player.isPlaying())) {
//            player.play();
//        }
//    }
//}
