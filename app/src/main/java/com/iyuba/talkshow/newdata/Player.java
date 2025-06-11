package com.iyuba.talkshow.newdata;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

/**
 * MediaPlayer Wrapper with state and duration query.
 */
public class Player implements OnCompletionListener, OnPreparedListener,
        OnErrorListener {
    private static final String TAG = Player.class.getSimpleName();
    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_PREPARING = 2;
    private static final int STATE_PREPARED = 3;
    private static final int STATE_PLAYING = 4;
    private static final int STATE_PAUSED = 5;
    private static final int STATE_PLAYBACK_COMPLETED = 6;
    private static final int STATE_RELEASED = 7;

    private MediaPlayer mediaPlayer;
    private int mPlayerState;
    private final OnPlayStateChangedListener opscl;
    private String audioUrl;
    // fake default duration value!
    private final int duration = 7000;
    // fake default current position value!
    private final int curTime = 0;
    private int playbackPosition = 0;

    public Player(Context context, OnPlayStateChangedListener opscl) {
        this.opscl = opscl;
        try {
            mediaPlayer = new MediaPlayer();
            mPlayerState = STATE_IDLE;
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * to reset the audio source of the media player
     *
     * @param url audio source
     */
    public void initialize(String url) {
        this.audioUrl = url;
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            } else {
                mediaPlayer = new MediaPlayer();
            }
            mPlayerState = STATE_IDLE;
            mediaPlayer.setDataSource(audioUrl);
            mPlayerState = STATE_INITIALIZED;
        } catch (IllegalArgumentException e) {
            if (opscl != null) {
                opscl.playFaild();
            }
        } catch (SecurityException e) {
            if (opscl != null) {
                opscl.playFaild();
            }
        } catch (IllegalStateException e) {
            if (opscl != null) {
                opscl.playFaild();
            }
        } catch (IOException e) {
            if (opscl != null) {
                opscl.playFaild();
            }
        }
    }

    public void playUrl(final String url) {
        this.audioUrl = url;
        handler.sendEmptyMessage(1);
    }

    public void prepareAndPlay() {
        mediaPlayer.prepareAsync();
        mPlayerState = STATE_PREPARING;
    }

    public void pause() {
        playbackPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
        mPlayerState = STATE_PAUSED;
    }

    public void restart() {
        mediaPlayer.seekTo(playbackPosition);
        mediaPlayer.start();
        mPlayerState = STATE_PLAYING;
    }

    public void start() {
        mediaPlayer.start();
        mPlayerState = STATE_PLAYING;
    }

    public void seekTo(int progress) {
        mediaPlayer.seekTo(progress);
    }

    /**
     * This is a cheat trick here! In order to stop but keep audio resource!
     */
    public void stopPlay() {
        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
        mPlayerState = STATE_PLAYBACK_COMPLETED;
    }

    public void reset() {
        mediaPlayer.reset();
        mPlayerState = STATE_IDLE;
    }

    /**
     * stop and release the media player
     */
    public void stopAndRelease() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            mPlayerState = STATE_RELEASED;
            Log.e(TAG, "the mediaplayer is released now.");
        }
    }

    public boolean isIdle() {
        return mPlayerState == STATE_IDLE;
    }

    public boolean isInitialized() {
        return mPlayerState == STATE_INITIALIZED;
    }

    public boolean isPreparing() {
        return mPlayerState == STATE_PREPARING;
    }

    public boolean isPrepared() {
        return mPlayerState == STATE_PREPARED;
    }

    /**
     * Since we always play audio after it is prepared, the so called "playing"
     * state here we change it to since it is preparing.
     *
     * @return
     */
    public boolean isPlaying() {
        return mPlayerState == STATE_PLAYING || mPlayerState == STATE_PREPARING
                || mPlayerState == STATE_PREPARED;
    }

    public boolean isPausing() {
        return mPlayerState == STATE_PAUSED;
    }

    /**
     * The player has to be playing really, not just from preparing.
     *
     * @return
     */
    public boolean isInPlayingBackState() {
        return mPlayerState == STATE_PLAYING || mPlayerState == STATE_PAUSED;
    }

    public boolean isCompleted() {
        return mPlayerState == STATE_PLAYBACK_COMPLETED;
    }

    public boolean isReleased() {
        return mPlayerState == STATE_RELEASED;
    }

    public boolean isAlreadyGetPrepared() {
        return mPlayerState >= STATE_PREPARED
                && mPlayerState <= STATE_PLAYBACK_COMPLETED;
    }

    public int getPlayerState() {
        return mPlayerState;
    }

    /**
     * start the media player immediately while it was prepared
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayerState = STATE_PREPARED;
        mp.start();
        mPlayerState = STATE_PLAYING;

        opscl.playSuccess();
    }

    /**
     * seek player to start point after a play back is finished
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer.seekTo(0);
        mPlayerState = STATE_PLAYBACK_COMPLETED;
        if (opscl != null) {
            opscl.playCompletion();
        }
    }

    /**
     * just set player to idle state when an error happens
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mPlayerState = STATE_ERROR;
        mp.reset();
        mPlayerState = STATE_IDLE;
        return true;
    }

    public int getDuration() {
        return isAlreadyGetPrepared() ? mediaPlayer.getDuration() : duration;
    }

    public int getCurrentTime() {
        return isAlreadyGetPrepared() ? mediaPlayer.getCurrentPosition() : curTime;
    }

    // Waiting to modify this method to adjust lots of formats
    // Now only support format like 05:30
    public String getCurrentTimeInFormat() {
        StringBuffer sb = new StringBuffer();
        int musicTime = getCurrentTime() / 1000;

        String minu = String.valueOf(musicTime / 60);
        if (minu.length() == 1) {
            minu = "0" + minu;
        }
        String sec = String.valueOf(musicTime % 60);
        if (sec.length() == 1) {
            sec = "0" + sec;
        }

        sb.append(minu).append(":").append(sec);
        return sb.toString();
    }

    // Waiting to modify this method to adjust lots of formats
    // Now only support format like 05:30
    public String getDurationInFormat() {
        StringBuffer sb = new StringBuffer();
        int musicTime = getDuration() / 1000;

        String minu = String.valueOf(musicTime / 60);
        if (minu.length() == 1) {
            minu = "0" + minu;
        }
        String sec = String.valueOf(musicTime % 60);
        if (sec.length() == 1) {
            sec = "0" + sec;
        }

        sb.append(minu).append(":").append(sec);
        return sb.toString();
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mediaPlayer.reset();
                    mPlayerState = STATE_IDLE;
                    try {
                        mediaPlayer.setDataSource(audioUrl);
                    } catch (IllegalArgumentException e) {
                        if (opscl != null) {
                            opscl.playFaild();
                        }
                    } catch (SecurityException e) {
                        if (opscl != null) {
                            opscl.playFaild();
                        }
                    } catch (IllegalStateException e) {
                        if (opscl != null) {
                            opscl.playFaild();
                        }
                    } catch (IOException e) {
                        if (opscl != null) {
                            opscl.playFaild();
                        }
                    }
                    mediaPlayer.prepareAsync();
                    mPlayerState = STATE_PREPARING;
            }
        }
    };

    @SuppressWarnings("unused")
    private void checkMediaplayerIsNull() {
        if (mediaPlayer == null) {
            // TODO to give the message needed for this situation
        }
    }

}
