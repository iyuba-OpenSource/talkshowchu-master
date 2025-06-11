package com.iyuba.talkshow.newce.study.read.newRead.service;

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

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.newce.study.StudyActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * @title:
 * @date: 2023/12/5 13:42
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class PrimaryBgPlayService extends Service {

    //app名称
    private static final String APP_NAME = ResUtil.getInstance().getString(R.string.app_name);
    //消息id
    public static final int NOTIFICATION_ID = App.APP_ID;
    //消息类型
    private static final String NOTIFICATION_NAME = APP_NAME+"通知";
    //绑定
    private MyPrimaryPlayBinder binder = new MyPrimaryPlayBinder();
    //播放器
    private ExoPlayer exoPlayer;
    //音频是否正在加载
    private boolean isPrepare = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化播放器
        initPlayer();
        //绑定接收器
        registerPlayReceiver();
        //发送通知消息
        showNotification(true,false,null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //解除绑定
        unregisterPlayReceiver();
        //关闭自己
        stopSelf();
    }


    /******************绑定************/
    public class MyPrimaryPlayBinder extends Binder {
        public PrimaryBgPlayService getService(){
            return PrimaryBgPlayService.this;
        }
    }


    /*******************通知*******************/
    public void showNotification(boolean isInit,boolean isPlay,Voa voa){
        String title = "";
        String showText = "";
        PendingIntent pendingIntent = null;

        if (isInit){
            title = APP_NAME;
            showText = title+"正在运行";
            pendingIntent = null;
        }else {
            if (voa!=null){
                title = voa.title();
            }
            if (isPlay){
                showText = "正在播放";
            }else {
                showText = "暂停播放";
            }
            pendingIntent = getStartIntent();
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
            builder.addAction(android.R.drawable.ic_menu_close_clear_cancel,"关闭",getPlayCloseIntent(false));
        }
        builder.build();
        startForeground(NOTIFICATION_ID,builder.build());
    }

    //跳转界面
    private PendingIntent getStartIntent(){
        Voa curVoa = PrimaryJumpData.getInstance().getVoa();
        String curJumpTitle = StudyActivity.title_read;
        int curUnitId = PrimaryJumpData.getInstance().getUnit();
        boolean auto = PrimaryJumpData.getInstance().isAuto();
        int position = PrimaryJumpData.getInstance().getPositionInList();
        Intent jumpIntent = StudyActivity.buildIntent(ResUtil.getInstance().getContext(),curVoa,curJumpTitle,curUnitId,auto,position);

        return PendingIntent.getActivity(ResUtil.getInstance().getContext(), 0,jumpIntent,getFlag());
    }

    //获取flag
    private int getFlag(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            return PendingIntent.FLAG_IMMUTABLE;
        }else {
            return PendingIntent.FLAG_UPDATE_CURRENT;
        }
    }

    /********************接收器****************/
    private static final String action_close = "action_close";
    private static final String action_cancel = "action_cancel";

    private PrimaryBgPlayCloseReceiver closeReceiver;

    private PendingIntent getPlayCloseIntent(boolean isPlay){
        String playStatus = PrimaryBgPlayEvent.event_audio_pause;
        if (isPlay){
            playStatus = PrimaryBgPlayEvent.event_audio_play;
        }

        Intent intent = new Intent(action_close);
        intent.putExtra(StrLibrary.playStatus,playStatus);
        return PendingIntent.getBroadcast(this,0,intent,getFlag());
    }

    //注册接收器
    private void registerPlayReceiver(){
        IntentFilter closeFilter = new IntentFilter(action_close);
        closeReceiver = new PrimaryBgPlayCloseReceiver();
        registerReceiver(closeReceiver,closeFilter);

//        IntentFilter cancelFilter = new IntentFilter();
//        cancelReceiver = new ConceptBgPlayCancelReceiver();
//        registerReceiver(cancelReceiver,cancelFilter);
    }

    //解除接收器
    private void unregisterPlayReceiver(){
        try {
            unregisterReceiver(closeReceiver);
//            unregisterReceiver(cancelReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /***************************音频************************/
    //获取播放器
    public ExoPlayer getPlayer(){
        return exoPlayer;
    }

    //是否正在加载
    public boolean isPrepare(){
        return isPrepare;
    }

    //设置加载完成(如果不需要重新加载，则进行设置即可)
    public void setPrepare(boolean hasPrepare){
        this.isPrepare = hasPrepare;
    }

    //初始化播放器
    private void initPlayer(){
        exoPlayer = new ExoPlayer.Builder(this).build();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //加载完成
                        isPrepare = false;
                        EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_play));
                        EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_prepareFinish));
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        isPrepare = true;
                        EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_completeFinish));
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(PrimaryBgPlayService.this,"音频播放失败");
            }
        });
    }
}
