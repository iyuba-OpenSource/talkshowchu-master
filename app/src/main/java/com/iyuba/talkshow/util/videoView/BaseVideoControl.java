package com.iyuba.talkshow.util.videoView;

/*
 * Copyright (C) 2016 Brian Wernick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.devbrackets.android.exomedia.ui.animation.TopViewHideShowAnimation;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.util.TimeFormatUtil;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.ui.detail.DetailActivity;
import com.iyuba.talkshow.util.DensityUtil;

import java.util.LinkedList;
import java.util.List;


/**
 * Provides playback controls for the EMVideoView on Mobile
 * (Phone, Tablet, etc.) devices.
 */
@SuppressWarnings("unused")
public abstract class BaseVideoControl extends VideoControls {
    protected SeekBar seekBar;
    protected ImageButton screenBtn;
    protected ImageButton replayBtn;
    protected LinearLayout extraViewsContainer;
    protected ImageView mBackIv;
    private BackCallback mBackCallback;
    private ReplayCallback mReplayCallback;
    private ToTvCallback toTvCallback;
    private LinearLayout titleLayout;

    protected boolean userInteracting = false;
    private boolean isFullScreen = false;
    private int mMode = Mode.SHOW_AUTO;
    private boolean mIsShowPlayPause = true;
    private ImageButton toTvBtn;

    //是否显示返回按钮
    private boolean isShowBackBtn = false;

    public BaseVideoControl(Context context) {
        super(context);
        Activity activity = (Activity)context;
        if(context instanceof DetailActivity){
            toTvBtn.setVisibility(View.VISIBLE);
        }else {
            toTvBtn.setVisibility(View.GONE);
        }
        playDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.play, context.getTheme());
        pauseDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.pause, context.getTheme());
    }

    public BaseVideoControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseVideoControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseVideoControl(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected abstract int getLayoutResource();

    @Override
    public void setPosition(@IntRange(from = 0) long position) {
        currentTimeTextView.setText(TimeFormatUtil.formatMs(position));
        seekBar.setProgress((int) position);
//        ResourcesCompat.getDrawable(getResources(),R.drawable.abc_btn_rating_star_off_mtrl_alpha,null);
    }

    @Override
    public void setDuration(@IntRange(from = 0) long duration) {
        if (duration != seekBar.getMax()) {
            endTimeTextView.setText(TimeFormatUtil.formatMs(duration));
            seekBar.setMax((int) duration);
        }
    }

    @Override
    public void updateProgress(@IntRange(from = 0) long position, @IntRange(from = 0) long duration, @IntRange(from = 0, to = 100) int bufferPercent) {
        if (!userInteracting) {
            seekBar.setSecondaryProgress((int) (seekBar.getMax() * ((float) bufferPercent / 100)));
            seekBar.setProgress((int) position);
            currentTimeTextView.setText(TimeFormatUtil.formatMs(position));
        }
    }

    @Override
    protected void retrieveViews() {
        super.retrieveViews();
        seekBar = findViewById(R.id.exomedia_controls_video_seek);
        screenBtn = findViewById(R.id.exomedia_controls_full_screen_btn);
        toTvBtn = findViewById(R.id.exomedia_controls_to_tv);
        mBackIv = findViewById(R.id.back_iv);
        replayBtn = findViewById(R.id.exomedia_controls_replay_btn);
        titleLayout = findViewById(R.id.exomedia_controls_text_container);
    }

    @Override
    protected void registerListeners() {
        super.registerListeners();
        seekBar.setOnSeekBarChangeListener(new SeekBarChanged());
        screenBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFullScreen) {
                    intoFullScreen();
                } else {
                    exitFullScreen();
                }
            }
        });

        mBackIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    exitFullScreen();
                } else {
                    if (mBackCallback != null) {
                        mBackCallback.onBack();
                    }
                }
            }
        });

        replayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReplayCallback != null) {
                    mReplayCallback.onReplay();
                }
            }
        });
        toTvBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toTvCallback.onToTv();
            }
        });
    }

    public void intoFullScreen() {
        isFullScreen = true;
        ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        screenBtn.setBackgroundResource(R.drawable.exit_full_screen);
        updatePlayPauseLayoutParams(DensityUtil.dp2px(getContext(), 80));
        updateFullScreenBtn(DensityUtil.dp2px(getContext(), 28), DensityUtil.dp2px(getContext(), 6));
    }

    public void exitFullScreen() {
        isFullScreen = false;
        updatePlayPauseLayoutParams(DensityUtil.dp2px(getContext(), 60));
        updateFullScreenBtn(DensityUtil.dp2px(getContext(), 16), DensityUtil.dp2px(getContext(), 6));
        ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        screenBtn.setBackgroundResource(R.drawable.full_screen);
    }

    private void updatePlayPauseLayoutParams(int size) {
        LayoutParams layoutParams = (LayoutParams) playPauseButton.getLayoutParams();
        layoutParams.height = size;
        layoutParams.width = size;
        playPauseButton.setLayoutParams(layoutParams);
    }

    private void updateFullScreenBtn(int size, int padding) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) screenBtn.getLayoutParams();
        layoutParams.height = size;
        layoutParams.width = size;
        screenBtn.setPadding(padding, padding, padding, padding);
        screenBtn.setLayoutParams(layoutParams);

    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreenBtnVisible(boolean visible) {
        if (visible) {
            screenBtn.setVisibility(View.VISIBLE);
        } else {
            screenBtn.setVisibility(View.GONE);
        }
    }

    //上方是否显示
    public void setTopTitleLayoutVisible(boolean visible){
        if (visible){
            titleLayout.setVisibility(VISIBLE);
        }else {
            titleLayout.setVisibility(GONE);
        }
    }

    public void setMode(int mode) {
        this.mMode = mode;
    }

    @Override
    public void addExtraView(@NonNull View view) {
        extraViewsContainer.addView(view);
    }

    @Override
    public void removeExtraView(@NonNull View view) {
        extraViewsContainer.removeView(view);
    }

    @NonNull
    @Override
    public List<View> getExtraViews() {
        int childCount = extraViewsContainer.getChildCount();
        if (childCount <= 0) {
            return super.getExtraViews();
        }

        //Retrieves the layouts children
        List<View> children = new LinkedList<>();
        for (int i = 0; i < childCount; i++) {
            children.add(extraViewsContainer.getChildAt(i));
        }

        return children;
    }

    @Override
    public void hideDelayed(long delay) {
        hideDelay = delay;

        if (delay < 0 || !canViewHide || isLoading) {
            return;
        }

        //If the user is interacting with controls we don't want to start the delayed hide yet
        if (!userInteracting) {
            visibilityHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateVisibility(false);
                }
            }, delay);
        }
    }

    @Override
    protected void animateVisibility(boolean toVisible) {
        isVisible = (controlsContainer.getVisibility() == View.VISIBLE);
        if (isVisible == toVisible) {
            return;
        }

        if (!hideEmptyTextContainer || !isTextContainerEmpty()) {
            if (toVisible) {
                textContainer.setVisibility(View.VISIBLE);
            } else {
                textContainer.setVisibility(View.GONE);
            }

//            textContainer.startAnimation(new TopViewHideShowAnimation(textContainer, toVisible, CONTROL_VISIBILITY_ANIMATION_LENGTH));
        }

        if (!isLoading) {
            if (toVisible) {
                if (mIsShowPlayPause) {
                    playPauseButton.setVisibility(View.VISIBLE);
                }
                controlsContainer.setVisibility(View.VISIBLE);
            } else {
                playPauseButton.setVisibility(View.GONE);
                controlsContainer.setVisibility(View.GONE);
            }

//            controlsContainer.startAnimation(new BottomViewHideShowAnimation(controlsContainer, toVisible, CONTROL_VISIBILITY_ANIMATION_LENGTH));
        }

        isVisible = toVisible;
        onVisibilityChanged();
    }

    @Override
    protected void updateTextContainerVisibility() {
        if (!isVisible) {
            return;
        }

        boolean emptyText = isTextContainerEmpty();
        if (hideEmptyTextContainer && emptyText && textContainer.getVisibility() == VISIBLE) {
            textContainer.clearAnimation();
            textContainer.startAnimation(new TopViewHideShowAnimation(textContainer, false, CONTROL_VISIBILITY_ANIMATION_LENGTH));
        } else if ((!hideEmptyTextContainer || !emptyText) && textContainer.getVisibility() != VISIBLE) {
            textContainer.clearAnimation();
            textContainer.startAnimation(new TopViewHideShowAnimation(textContainer, true, CONTROL_VISIBILITY_ANIMATION_LENGTH));
        }
    }

    @Override
    public void showLoading(boolean initialLoad) {
        if (isLoading) {
            return;
        }

        isLoading = true;
        loadingProgressBar.setVisibility(View.GONE);
        controlsContainer.setVisibility(View.GONE);
        playPauseButton.setVisibility(View.GONE);
        if (mMode == Mode.SHOW_AUTO) {
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        show();
    }

    @Override
    public void finishLoading() {
        if (!isLoading) {
            return;
        }
        isLoading = false;
        loadingProgressBar.setVisibility(View.GONE);
        if (mMode == Mode.SHOW_AUTO) {
            if (mIsShowPlayPause) {
                playPauseButton.setVisibility(View.VISIBLE);
            }
            controlsContainer.setVisibility(View.VISIBLE);
            updatePlaybackState(videoView != null && videoView.isPlaying());
        } else {
            updatePlayPauseImage(videoView != null && videoView.isPlaying());
            progressPollRepeater.start();
        }
    }

    public int getControlVisibility() {
        return controlsContainer.getVisibility();
    }

    public void setBackCallback(BackCallback backCallback) {
        this.mBackCallback = backCallback;
    }

    /**
     * Listens to the seek bar change events and correctly handles the changes
     */
    protected class SeekBarChanged implements SeekBar.OnSeekBarChangeListener {
        private int seekToTime;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }

            seekToTime = progress;
            if (currentTimeTextView != null) {
                currentTimeTextView.setText(TimeFormatUtil.formatMs(seekToTime));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            userInteracting = true;
            if (seekListener == null || !seekListener.onSeekStarted()) {
                internalListener.onSeekStarted();
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            userInteracting = false;
            if (seekListener == null || !seekListener.onSeekEnded(seekToTime)) {
                internalListener.onSeekEnded(seekToTime);
            }
        }
    }

    public void setReplayCallback(ReplayCallback mReplayCallback) {
        this.mReplayCallback = mReplayCallback;
    }

    public void setIsShowPlayPause(boolean mIsShowPlayPause) {
        this.mIsShowPlayPause = mIsShowPlayPause;
    }

    public void setToTvCallBack(ToTvCallback callBack) {
        this.toTvCallback = callBack;
    }

    public void setShowBackBtn(boolean showBackBtn){
        this.isShowBackBtn = showBackBtn;
    }

    public interface BackCallback {
        void onBack();
    }

    public interface ReplayCallback {
        void onReplay();
    }

    public interface ToTvCallback {
        void onToTv();
    }

    public interface Mode {
        int SHOW_MANUAL = 0;
        int SHOW_AUTO = 1;
    }
}
