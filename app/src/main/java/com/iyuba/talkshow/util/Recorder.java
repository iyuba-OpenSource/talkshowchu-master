package com.iyuba.talkshow.util;

import android.media.MediaRecorder;

import java.io.IOException;

public class Recorder {
    private static final int MAX_LENGTH = 1000 * 60 * 10;// unit : ms

    public interface State {
        int ERROR = -1;
        int INITIAL = 0;
        int RECORDING = 1;
        int COMPLETED = 2;
        int RELEASED = 3;
    }

    private final MediaRecorder mMediaRecorder;
    private int mState;
    private OnStateChangeListener mStateChangeListener;

    private final int mBaseAmplitude = 80; // magic number...just a joke

    public Recorder() {
        mMediaRecorder = new MediaRecorder();
        mState = State.INITIAL;
        mMediaRecorder.setOnErrorListener(mErrorListener);
    }

    public void setOnStateChangeListener(OnStateChangeListener l) {
        mStateChangeListener = l;
    }

    public void start(String filePath) {
        if (mState == State.ERROR || mState == State.RECORDING) {
            mMediaRecorder.reset();
            mState = State.INITIAL;
            notifyStateChanged(State.INITIAL);
        }
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setOutputFile(filePath);
        mMediaRecorder.setMaxDuration(MAX_LENGTH);
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mState = State.RECORDING;
            notifyStateChanged(State.RECORDING);
        } catch (IOException e) {
            reset();
        }
    }

    public void reset() {
        try{
            mMediaRecorder.reset();
            mState = State.INITIAL;
            notifyStateChanged(State.INITIAL);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void stop() {
        if (mState == State.RECORDING) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
                reset();
            }

            mState = State.COMPLETED;
            notifyStateChanged(State.COMPLETED);
        }
    }

    public void release() {
        mMediaRecorder.release();
        mState = State.RELEASED;
        notifyStateChanged(State.RELEASED);
    }

    public int getCurrentDB() {
        if (mState == State.RECORDING) {
            int result = (int) (20 * Math.log10(mMediaRecorder.getMaxAmplitude() / mBaseAmplitude));
            return (result >= 0) ? result : 0;
        } else {
            return 0;
        }
    }

    private void notifyStateChanged(int newstate) {
        if (mStateChangeListener != null) mStateChangeListener.onStateChange(newstate);
    }

    private final MediaRecorder.OnErrorListener mErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            mState = State.ERROR;
            notifyStateChanged(State.ERROR);
        }
    };

    public interface OnStateChangeListener {
        void onStateChange(int newState);
    }

}
