//package com.iyuba.talkshow.lil.help_fix.util.bgService;
///**
// * 后台播放服务
// *
// * @author 陈彤
// * @version 1.1
// * 更新内容 AudioManager管理与其他 播放器播放冲突问题
// */
//
//import android.app.AlarmManager;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.media.AudioManager;
//import android.media.AudioManager.OnAudioFocusChangeListener;
//import android.os.Binder;
//import android.os.Build;
//import android.os.IBinder;
//import android.os.SystemClock;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.google.android.exoplayer2.ExoPlayer;
//import com.iyuba.imooclib.event.ImoocPlayEvent;
//import com.iyuba.talkshow.R;
//import com.iyuba.talkshow.constant.App;
//import com.iyuba.talkshow.data.NotificationCancelReceiver;
//import com.iyuba.talkshow.data.NotificationCloseReceiver;
//import com.iyuba.talkshow.data.model.Voa;
//import com.iyuba.talkshow.event.PauseEvent;
//import com.iyuba.talkshow.newce.study.StudyActivity;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.Date;
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.Observable;
//import io.reactivex.Observer;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.schedulers.Schedulers;
//
//public class FixBackground extends Service {
//    private AudioManager mAm;
//    private ExoPlayer exoPlayer;
//    private int voaid = 0;
//    private MyBinder myBinder = new MyBinder();
//    private MyOnAudioFocusChangeListener mListener;
//    private boolean mPausedByTransientLossOfFocus;
//    public long startTime;
//
//    //新的接收器
//    private NotificationCancelReceiver cancelReceiver;
//    private NotificationCloseReceiver closeReceiver;
//
//    public class MyBinder extends Binder {
//        public FixBackground getService() {
//            return FixBackground.this;
//        }
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return myBinder;
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        return true;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
////        initNotification(Playmanager.getInstance().getVoaFromList());
//        mAm = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
//        mListener = new MyOnAudioFocusChangeListener();
//        //stopForeground(true);
//        EventBus.getDefault().register(this);
//
//        registerNotificationReceiver();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//        mAm.abandonAudioFocus(mListener);
//        try {
//            if (exoPlayer!=null){
//                exoPlayer.stop();
//                exoPlayer.release();
//                exoPlayer = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        unregisterNotificationReceiver();
//    }
//
//    public void init(Context mContext) {
//        exoPlayer = new ExoPlayer.Builder(mContext).build();
//        mAm.requestAudioFocus(mListener, AudioManager.STREAM_MUSIC,
//                AudioManager.AUDIOFOCUS_GAIN);
//    }
//
//    public ExoPlayer getPlayer() {
//        return exoPlayer;
//    }
//
//    public int getTag() {
//        return voaid;
//    }
//
//    public void setTag(int id) {
//        voaid = id;
//    }
//
//    private PendingIntent receiveCloseIntent() {
//        Intent intent = new Intent("iyuhead.close");
//        return PendingIntent.getBroadcast(this, 0, intent, getPendFlag());
//    }
//
//    private PendingIntent receiveCancelIntent() {
//        Intent intent = new Intent("iyuhead.cancel");
//        return PendingIntent.getBroadcast(this, 0, intent, getPendFlag());
//    }
//
//    private PendingIntent receivePauseIntent() {
//        Intent localIntent = new Intent("iyuhead.pause");
//        return PendingIntent.getBroadcast(this, 0, localIntent, getPendFlag());
//    }
//
//    private PendingIntent receivePlayIntent() {
//        Intent localIntent = new Intent("iyuhead.play");
//        return PendingIntent.getBroadcast(this, 0, localIntent, getPendFlag());
//    }
//
//    private int getPendFlag() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            return PendingIntent.FLAG_IMMUTABLE;
//        } else {
//            return PendingIntent.FLAG_UPDATE_CURRENT;
//        }
//    }
//
//    //注册接收器
//    private void registerNotificationReceiver() {
//        IntentFilter intentFilter = new IntentFilter("iyuhead.close");
//        this.closeReceiver = new NotificationCloseReceiver();
//        registerReceiver(closeReceiver, intentFilter);
//
//        intentFilter = new IntentFilter("iyuhead.cancel");
//        this.cancelReceiver = new NotificationCancelReceiver();
//        registerReceiver(cancelReceiver, intentFilter);
//    }
//
//    //取消接收器
//    private void unregisterNotificationReceiver() {
//        try {
//            unregisterReceiver(closeReceiver);
//            unregisterReceiver(cancelReceiver);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private PendingIntent startStudyNewActivity() {
//        //从会话中获取数据并且设置到当前中
//        Intent intent = null;
//        if (FixBackgroundSession.getInstance().getVoa() == null) {
//            intent = null;
//        } else {
//            intent = StudyActivity.buildIntent(this, FixBackgroundSession.getInstance().getVoa(), FixBackgroundSession.getInstance().getPosition(), FixBackgroundSession.getInstance().getUnitId(), FixBackgroundSession.getInstance().isBack(), FixBackgroundSession.getInstance().isAuto(),-1);
//        }
//
//        if (intent!=null){
//            return PendingIntent.getActivity(this, 0, intent, getPendFlag());
//        }else {
//            return null;
//        }
//    }
//
//    public void initNotification(Voa voa) {
//        if (voa == null) {
//            stopForeground(true);
//            return;
//        }
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT > 26) {
//            notificationManager.createNotificationChannel(new NotificationChannel("222", "222", NotificationManager.IMPORTANCE_LOW));
//            Notification notification = new Notification.Builder(this, "222")
//                    .setOngoing(true)
//                    .setContentTitle("正在播放")
//                    .setContentText(voa.descCn())
//                    .setContentIntent(startStudyNewActivity())
//                    .setSmallIcon(R.drawable.ic_launcher)
//                    .setTicker("正在播放")
////                    .addAction(android.R.drawable.ic_media_play, "播放", receivePlayIntent())
////                    .addAction(android.R.drawable.ic_media_pause, "暂停", receivePauseIntent())
////                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "关闭", receiveCloseIntent())
//                    .build();
//            startForeground(222, notification);
//        } else {
//            Notification.Builder localBuilder = new Notification.Builder(this);
//            localBuilder.setOngoing(true).setContentTitle("正在播放")
//                    .setContentText(voa.descCn())
//                    .setContentIntent(startStudyNewActivity())
//                    .setSmallIcon(R.drawable.ic_launcher)
//                    .setTicker("正在播放");
////                    .addAction(android.R.drawable.ic_media_play, "播放", receivePlayIntent())
////                    .addAction(android.R.drawable.ic_media_pause, "暂停", receivePauseIntent())
////                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "关闭", receiveCloseIntent());
//
//            startForeground(222, localBuilder.build());
//        }
//    }
//
//    //真正的刷新通知
//    public void updateNotification(String title, boolean isPlaying) {
//        if (TextUtils.isEmpty(title)) {
//            return;
//        }
//
//        String playText = "正在播放";
//        if (!isPlaying) {
//            playText = "暂停播放";
//        }
//
//        if (isPlaying) {
//            stopTimer();
//        } else {
//            startTimer();
//        }
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT > 26) {
//            NotificationChannel channel = new NotificationChannel(String.valueOf(App.APP_ID), "小学英语通知", NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription("小学英语的通知消息");
//            channel.setSound(null, null);
//            channel.enableLights(false);
//            channel.enableVibration(false);
//            notificationManager.createNotificationChannel(channel);
//
//            Notification notification = new Notification.Builder(this, String.valueOf(App.APP_ID))
//                    .setOngoing(true)
//                    .setContentTitle(playText)
//                    .setContentText(title)
//                    .setContentIntent(startStudyNewActivity())
//                    .setSmallIcon(R.drawable.ic_launcher)
//                    .setTicker(playText)
////                    .addAction(android.R.drawable.ic_media_play, "播放", receivePlayIntent())
////                    .addAction(android.R.drawable.ic_media_pause, "暂停", receivePauseIntent())
//                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "关闭", receiveCloseIntent())
//                    .setDeleteIntent(receiveCancelIntent())
//                    .build();
//            startForeground(App.APP_ID, notification);
//        } else {
//            Notification.Builder localBuilder = new Notification.Builder(this);
//            localBuilder.setOngoing(true)
//                    .setContentTitle(playText)
//                    .setContentText(title)
//                    .setContentIntent(startStudyNewActivity())
//                    .setSmallIcon(R.drawable.ic_launcher)
//                    .setTicker(playText)
////                    .addAction(android.R.drawable.ic_media_play, "播放", receivePlayIntent())
////                    .addAction(android.R.drawable.ic_media_pause, "暂停", receivePauseIntent())
//                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "关闭", receiveCloseIntent())
//                    .setDeleteIntent(receiveCancelIntent());
//
//            startForeground(App.APP_ID, localBuilder.build());
//        }
//    }
//
//    //焦点监听 与别的播放器争抢焦点
//    private class MyOnAudioFocusChangeListener implements OnAudioFocusChangeListener {
//        @Override
//        public void onAudioFocusChange(int focusChange) {
//            switch (focusChange) {
//                case AudioManager.AUDIOFOCUS_LOSS:
//                    if (exoPlayer != null && exoPlayer.isPlaying()) {
//                        // 因为会长时间失去，所以直接暂停
//                        exoPlayer.pause();
//                        mPausedByTransientLossOfFocus = false;
//                    }
//
//                    break;
//                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                    if (exoPlayer != null && exoPlayer.isPlaying()) {
//                        exoPlayer.pause();// 短暂失去焦点，先暂停。同时将标志位置成重新获得焦点后就开始播放
//                        mPausedByTransientLossOfFocus = true;
//                    }
//
//                    break;
//                case AudioManager.AUDIOFOCUS_GAIN:
//                    // 重新获得焦点，且符合播放条件，开始播放
//                    if (exoPlayer != null && !exoPlayer.isPlaying() && mPausedByTransientLossOfFocus) {
//                        mPausedByTransientLossOfFocus = false;
//                        if (exoPlayer!=null){
//                            exoPlayer.play();
//                        }
//                    }
//                    break;
//            }
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(ImoocPlayEvent event) {
//        if (exoPlayer != null && exoPlayer.isPlaying()) {
//            exoPlayer.pause();
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(PauseEvent event) {
//        if (exoPlayer != null && exoPlayer.isPlaying()) {
//            exoPlayer.pause();
//        }
//    }
//
//    /**
//     * 设置定时关闭任务
//     *
//     * @Param min 分钟数
//     */
//    public void setAlarm(int min) {
//        Log.e("LongRunningService", new Date().toString());
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        long triggerAtTime = SystemClock.elapsedRealtime() + 60000 * min;
//        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, receivePauseIntent());
//    }
//
//    /**
//     * 取消定时播放
//     */
//    public void cancelAlarm() {
//        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        try {
//            am.cancel(receivePauseIntent());
//            Log.e("LongRunningService", "Alarm is Canceled.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("LongRunningService", "Alarm is not Canceled: " + e.toString());
//        }
//    }
//
//    //设置定时任务3分钟，在暂停时计时，在开启时关闭
//    private Disposable notificationTimer;
//
//    //开启计时
//    public void startTimer() {
//        Observable.interval(0, 1000L, TimeUnit.MILLISECONDS)
//                .observeOn(Schedulers.io())
//                .subscribe(new Observer<Long>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        notificationTimer = d;
//                    }
//
//                    @Override
//                    public void onNext(Long aLong) {
//                        Log.d("倒计时", "开始倒计时--" + aLong);
//
//                        if (aLong >= 3 * 60) {
//                            try {
//                                ExoPlayer player = FixBackgroundManager.Instace().bindService.getPlayer();
//                                if ((player != null) && (player.isPlaying())) {
//                                    player.pause();
//                                }
//
//                                FixBackgroundManager.Instace().bindService.stopForeground(true);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            stopTimer();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        try {
//                            ExoPlayer player = FixBackgroundManager.Instace().bindService.getPlayer();
//                            if ((player != null) && (player.isPlaying())) {
//                                player.pause();
//                            }
//
//                            FixBackgroundManager.Instace().bindService.stopForeground(true);
//                        } catch (Exception s) {
//                            s.printStackTrace();
//                        }
//                        stopTimer();
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }
//
//    //停止计时
//    public void stopTimer() {
//        Log.d("倒计时", "关闭倒计时--");
//        if (notificationTimer != null && !notificationTimer.isDisposed()) {
//            notificationTimer.dispose();
//            notificationTimer = null;
//        }
//    }
//}
//
//
