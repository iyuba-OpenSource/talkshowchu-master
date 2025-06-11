package com.iyuba.talkshow.newce.search.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

/**
 * 单例模式的播放器
 */
public class ExercisePlayer {

    private static ExercisePlayer instance;
    private Context context;

    private MediaPlayer mediaPlayer;

    public static ExercisePlayer getInstance(Context context) {
        if (instance == null) {
            synchronized (ExercisePlayer.class) {
                if (instance == null) {
                    instance = new ExercisePlayer(context);
                }
            }
        }
        return instance;
    }

    //构造器
    private ExercisePlayer(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
    }

    //设置播放路径
    public void setResource(String playUri) {
        //重置播放状态
        if (mediaPlayer!=null){
            mediaPlayer.reset();
        }

        //设置新的播放uri
        try {
            mediaPlayer.setDataSource(context,Uri.parse(playUri));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //准备播放
        mediaPlayer.prepareAsync();
        //设置播放监听
        mediaPlayer.setOnPreparedListener(null);
        mediaPlayer.setOnPreparedListener(mp -> {
            if (onAudioPlayerListener!=null){
                onAudioPlayerListener.onPrepared();
            }
        });
        mediaPlayer.setOnCompletionListener(null);
        mediaPlayer.setOnCompletionListener(player -> {
            if (onAudioPlayerListener != null) {
                onAudioPlayerListener.onCompletion();
            }
        });
    }

    //开始播放
    public void start() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                return;
            }
            mediaPlayer.start();
        }
    }

    //暂停播放
    public void pause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            mediaPlayer.reset();
        }
    }

    //是否正在播放
    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    //获取当前播放位置
    public int getCurPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    //获取当前的总时长
    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    //跳转进度
    public void seekTo(int progress) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(progress);
        }
    }

    //停止播放
    public void stop() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //进度监听接口
    private OnAudioPlayerListener onAudioPlayerListener;

    public interface OnAudioPlayerListener {
        //准备播放
        void onPrepared();

        //播放完成
        void onCompletion();
    }

    public void setOnAudioPlayerListener(OnAudioPlayerListener onAudioPlayerListener) {
        this.onAudioPlayerListener = onAudioPlayerListener;
    }
}
