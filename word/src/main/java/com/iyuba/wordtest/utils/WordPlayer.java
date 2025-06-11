package com.iyuba.wordtest.utils;

import android.media.MediaPlayer;

/**
 * Created by iyuba on 2019/2/22.
 */

public class WordPlayer {

    private MediaPlayer mMediaPlayer;

    // 初始化播放器
    public void initMediaplayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();

    }


    // 播放
    public void playMusic(String url) {
        try {
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
            /* 重置多媒体 */
            mMediaPlayer.reset();
            /* 读取媒体文件 */
            mMediaPlayer.setDataSource(url);
            /* 准备播放 */
            mMediaPlayer.prepare();
            /* 开始播放 */
//            mMediaPlayer.start();
            /* 是否单曲循环 */
            mMediaPlayer.setLooping(false);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void stopPlayer() {
        try {
            if ((mMediaPlayer != null) && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 释放播放器资源
     */
    public void ReleasePlayer() {

        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
