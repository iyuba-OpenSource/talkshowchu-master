package com.iyuba.talkshow.ui.detail;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MyOnTouchListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
    protected GestureDetector gestureDetector;
    private SingleTapListener mListener;

    public MyOnTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
//        if (mVideoControls != null) {
//            mVideoControls.show();
//
//            if (isPlaying()) {
//                mVideoControls.hideDelayed(VideoControls.DEFAULT_CONTROL_HIDE_DELAY);
//            }
//        }
        mListener.onSingleTap();
        return true;
    }

    public void setSingleTapListener(SingleTapListener listener) {
        this.mListener = listener;
    }

    public interface SingleTapListener {
        void onSingleTap();
    }
}