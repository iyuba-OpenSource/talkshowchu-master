package com.iyuba.talkshow.newce.search.util;

import android.util.Log;

import com.iyuba.talkshow.newce.search.view.ExercisePlayer;

import java.util.Timer;
import java.util.TimerTask;

public class ExerciseFixTimerSingle {

    private static ExerciseFixTimerSingle instance;
    private Timer timer;

    //定时器时间间隔
    private static final long timerInterval = 10L;
    //当前的播放时长
    private static long curPlayTime = 0;
    //上一个播放器的时间（避免最后一句播放存在问题）
    private static long playerLastTime = 0;
    //临时最终时间
    private long tempEndTime = 0;

    public static ExerciseFixTimerSingle getInstance(){
        if (instance==null){
            synchronized (ExerciseFixTimerSingle.class){
                if (instance==null){
                    instance = new ExerciseFixTimerSingle();
                }
            }
        }
        return instance;
    }

    //获取定时器
    public Timer getTimer(){
        return timer;
    }

    //启动播放定时器
    public void startPlayerTimer(ExercisePlayer player, long endTime, OnTimerCallBackListener onTimerCallBackListener){
        stopTimer();

        tempEndTime = endTime;

        if (timer==null){
            timer = new Timer();
        }

        //重置最终时间
        playerLastTime = 0;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //根据播放器来处理时间，因为播放器存在延迟
                long curPlayerTime = player.getCurPosition();
                //时间间隔
                long tempTime = tempEndTime-curPlayerTime;

                Log.d("倒计时器", " 请看时间--"+tempEndTime+"--"+curPlayerTime);

                //如果两次最终时间一致，则直接结束
                if ((curPlayerTime == playerLastTime)&&(tempTime<timerInterval)){
                    onTimerCallBackListener.onFinish();
                    stopTimer();
                    Log.d("倒计时器", "1");
                    return;
                }

                playerLastTime = curPlayerTime;

                //如果时间间隔在定时时间/2之内，则之内结束
                if (tempTime<timerInterval/4*3){
                    onTimerCallBackListener.onFinish();
                    stopTimer();
                    Log.d("倒计时器", "2");
                    return;
                }

                //根据状态进行操作
                if (curPlayerTime>=tempEndTime){
                    onTimerCallBackListener.onFinish();
                    stopTimer();
                    Log.d("倒计时器", "3");
                }else {
                    onTimerCallBackListener.onTick();
                    Log.d("倒计时器", "继续操作");
                }
            }
        },0,timerInterval);
    }
    
    //启动定时器
    public void startTimer(long playTime,OnTimerCallBackListener onTimerCallBackListener){
        stopTimer();

        if (timer==null){
            timer = new Timer();
        }

        //重置当前时长
        curPlayTime = 0;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                curPlayTime+=timerInterval;

                //如果超过时间，则直接完成
                if (curPlayTime>=playTime){
                    onTimerCallBackListener.onFinish();
                    stopTimer();
                }else {
                    onTimerCallBackListener.onTick();
                }

                Log.d("倒计时器", " 请看时间--"+playTime+"--"+curPlayTime);
            }
        },0,timerInterval);
    }

    //关闭定时器
    public void stopTimer(){
        if (timer!=null){
            timer.cancel();
            timer = null;
        }

        //这里直接把倒计时的回调给结束
        if (onTimerCallBackListener!=null){
            onTimerCallBackListener.onFinish();
        }
    }

    //接口
    public interface OnTimerCallBackListener{
        //进行中
        void onTick();
        //完成
        void onFinish();
    }

    private OnTimerCallBackListener onTimerCallBackListener;

    public void setOnTimerCallBackListener(OnTimerCallBackListener onTimerCallBackListener) {
        this.onTimerCallBackListener = onTimerCallBackListener;
    }
}
