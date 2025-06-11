//package com.iyuba.talkshow.data;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import com.iyuba.talkshow.newdata.MyIjkPlayer;
//
//import java.util.Date;
//
//public class NotificationPauseReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent paramIntent) {
//        Log.e("LongRunningService=播放停止", new Date().toString());
//        if (BackgroundManager.Instace().bindService == null) {
//            return;
//        }
//        MyIjkPlayer player = BackgroundManager.Instace().bindService.getPlayer();
//        if ((player != null) && (player.isPlaying())) {
//            player.pause();
//        }
//    }
//}
