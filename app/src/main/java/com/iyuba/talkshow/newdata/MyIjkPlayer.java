package com.iyuba.talkshow.newdata;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.play.IJKPlayer;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.model.WavListItem;
import com.iyuba.talkshow.util.StorageUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.qqtheme.framework.util.LogUtils;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by carl shen on 2020/7/28
 * New Primary English, new study experience.
 */
public class MyIjkPlayer implements IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener {
    private IjkMediaPlayer ijkMediaPlayer;
    private OnPlayStateChangedListener opscl;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;

    public static int playFlag = 0;
    private static MyIjkPlayer myijkPlayer;
    public static MyIjkPlayer getInstance() {
        if (myijkPlayer == null) {
            myijkPlayer = new MyIjkPlayer();
        }
        return myijkPlayer;
    }

    public void setOpscl(OnPlayStateChangedListener opscl) {
        this.opscl = opscl;
    }

    private MyIjkPlayer() {
        try {
            if (ijkMediaPlayer == null) {
                IJKPlayer.initNative();
                ijkMediaPlayer = new IjkMediaPlayer();
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
                ijkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                ijkMediaPlayer.setOnBufferingUpdateListener(this);
                ijkMediaPlayer.setOnPreparedListener(this);
                ijkMediaPlayer.setOnCompletionListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayable() {
        return ijkMediaPlayer != null && ijkMediaPlayer.isPlayable();
    }

    public boolean isPlaying() {
        return ijkMediaPlayer != null && ijkMediaPlayer.isPlaying();
    }

    public void initialize(String url) {
        try {
            ijkMediaPlayer.reset();
            ijkMediaPlayer.setDataSource(url);
            ijkMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            if (opscl != null) {
                opscl.playFaild();
            }
        }
    }

    public void playOrPause() {
        if (playFlag == PlayFlag.play) {
            pause();
            playFlag = PlayFlag.pause;
        } else if (playFlag == PlayFlag.pause) {
            play();
            playFlag = PlayFlag.play;
        } else {
            play();
            playFlag = PlayFlag.play;
        }
    }

    public void play() {
        if (ijkMediaPlayer != null)
            ijkMediaPlayer.start();
        if (opscl != null) {
            opscl.playStart();
        }
    }


    public void pause() {
        if (ijkMediaPlayer != null)
            ijkMediaPlayer.pause();
        if (opscl != null) {
            opscl.playPause();
        }
    }

    public long getDuration() {
        if (ijkMediaPlayer != null) {
            return ijkMediaPlayer.getDuration();
        }
        return 0;
    }

    public String getDataSource() {
        if (ijkMediaPlayer != null) {
            return ijkMediaPlayer.getDataSource();
        }
        return "";
    }

    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener mPreparedListener) {
        mOnPreparedListener = mPreparedListener;
    }

    public long getCurrentPosition() {
        if (ijkMediaPlayer != null) {
            return ijkMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    private static final String TAG = "Player";

    public void seekTo(long msec) {
        LogUtils.error(TAG, "msec  :  " + msec);
        if (ijkMediaPlayer != null && !ijkMediaPlayer.isPlaying()) {
            LogUtils.error(TAG, "play");
            play();
        } else if (ijkMediaPlayer != null) {
            LogUtils.error(TAG, "seekto:    " + msec);
            ijkMediaPlayer.seekTo((int) msec);
        }
    }

    /**
     * 获取音频总长
     *
     * @return
     */
    public String getAudioAllTime() {
        StringBuffer timeBuffer = new StringBuffer();
        if (ijkMediaPlayer != null) {
            LogUtils.error("获取音频总长=======", ijkMediaPlayer.getDuration() + "");
            long musicTime = ijkMediaPlayer.getDuration() / 1000;
            String minit = "00";
            String second = "00";
            if ((musicTime / 60) < 10) {
                minit = "0" + musicTime / 60;

            } else {
                minit = String.valueOf(musicTime / 60);
            }
            if ((musicTime % 60) < 10)
            {
                second = "0" + musicTime % 60;
            } else {
                second = String.valueOf(musicTime % 60);
            }
            timeBuffer.append(minit).append(":").append(second);

        }
        return timeBuffer.toString();
    }

    /**
     * 获取音频当前播放进度时间
     *
     * @return
     */
    public String getAudioCurrTime() {
        StringBuffer timeBuffer = new StringBuffer();
        if (ijkMediaPlayer != null) {
            long musicTime = ijkMediaPlayer.getCurrentPosition() / 1000;
            String minit = "00";
            String second = "00";
            if ((musicTime / 60) < 10) {
                minit = "0" + musicTime / 60;

            } else {
                minit = String.valueOf(musicTime / 60);
            }
            if ((musicTime % 60) < 10)// �?
            {
                second = "0" + musicTime % 60;
            } else {
                second = String.valueOf(musicTime % 60);
            }
            timeBuffer.append(minit).append(":").append(second);
        }
        return timeBuffer.toString();
    }

    /**
     * 设置循环播放----
     */
    public void resetPlay() {

        ijkMediaPlayer.start();
//        mediaPlayer.setLooping(true);
    }

    /**
     * 设置播放速度
     */
    public void setSpeed(float speed) {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.setSpeed(speed);
        } else {
            LogUtils.error(TAG, "setSpeed for null??");
        }
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        //缓冲
        if (opscl != null) {
            opscl.bufferingUpdate(percent);
        }
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        if (ijkMediaPlayer != null && opscl != null) {
            opscl.playCompletion();
        }
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(ijkMediaPlayer);
        }
        if (opscl != null) {
            opscl.playSuccess();
        }
    }

    /**
     * 释放播放器资源
     */
    public synchronized void releasePlayer() {
        try {
            if (ijkMediaPlayer != null) {
                ijkMediaPlayer.stop();
                ijkMediaPlayer.release();
                ijkMediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<Integer, WavListItem> buildScoreMap(Voa mVoa, long mTimeStamp, List<VoaText> mVoaTextList, Record record) {
        Map<Integer, WavListItem> mapResult = new HashMap<>();
        if (record == null || mVoaTextList == null) {
            return mapResult;
        }
        String score = record.score();
        String audio = record.audio();
        if (TextUtils.isEmpty(score)) {
            return mapResult;
        }
        java.lang.reflect.Type type = new TypeToken<HashMap<Integer, Integer>>() {
        }.getType();
        java.lang.reflect.Type type2 = new TypeToken<HashMap<Integer, String>>() {
        }.getType();
        Map<Integer, Integer> map = new Gson().fromJson(score, type);
        Map<Integer, String> map2 = new Gson().fromJson(audio, type2);
        for (Map.Entry<Integer, String> i:map2.entrySet()) {
            for (Map.Entry<Integer, Integer> j:map.entrySet()){
                if (i.getKey().equals(j.getKey())){
                    WavListItem item = new WavListItem();
                    item.setUrl(i.getValue());
                    item.setBeginTime(mVoaTextList.get(i.getKey()).timing());
                    if (i.getKey() < mVoaTextList.size()  ){
                        item.setEndTime(mVoaTextList.get(i.getKey()+1).timing());
                    }else {
                        item.setEndTime(mVoaTextList.get(i.getKey()).endTiming());
                    }
                    File file = StorageUtil.getParaRecordAacFile(TalkShowApplication.getInstance(), mVoa.voaId(), i.getKey()+1, mTimeStamp);
                    float duration = getAudioFileVoiceTime(file.getAbsolutePath())/1000.0f ;
                    @SuppressLint("DefaultLocale")
                    String temp = String.format("%.1f",duration);
                    item.setDuration(Float.parseFloat(temp));
                    item.setIndex(i.getKey()+1);
                    mapResult.put(i.getKey() , item);
                }
            }
        }
        return mapResult;
    }

    public static long getAudioFileVoiceTime(String filePath) {
        long mediaPlayerDuration = 0L;
        if (filePath == null || filePath.isEmpty()) {
            return 0;
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayerDuration = mediaPlayer.getDuration();
        } catch (IOException ioException) {
            LogUtils.debug("tag", ioException.getMessage());
        }
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        return mediaPlayerDuration;
    }

}
