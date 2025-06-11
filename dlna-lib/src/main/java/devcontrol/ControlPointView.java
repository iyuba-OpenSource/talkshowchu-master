package devcontrol;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import org.cybergarage.android.R;
import org.cybergarage.android.databinding.LayoutControlPointBinding;

public class ControlPointView extends RelativeLayout implements View.OnClickListener {


    public interface OnMediaControlActionListener {
        void onPlayBtnClicked();

        void onNextBtnClicked();

        void onPreviousBtnClicked();

        void onVolumeDownClicked();

        void onVolumeUpClicked();

        void onStopClicked();

        void onSwitchClicked();
    }

    private ImageView mPlayBtn;
    private ImageView mPreviousBtn;
    private ImageView mNextBtn;
    private SeekBar mSeekBar;
    private SeekBar mVolumeSeekBar;
    private TextView mDurationTxt;
    private TextView mCurDurationTxt;
    private boolean isSeekable = false;

    private int totalDuration;
    private int currentDuration;
    LayoutControlPointBinding binding ;

    private OnMediaControlActionListener mControlActionListener;

    public ControlPointView(Context context) {
        this(context, null);
    }

    public ControlPointView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlPointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = LayoutControlPointBinding.inflate( LayoutInflater.from(context));
        addView(binding.getRoot());
//        LayoutInflater.from(context).inflate(R.layout.layout_control_point, this, true);
        init();
    }

    @SuppressLint({"WrongViewCast", "ClickableViewAccessibility"})
    private void init() {
        mSeekBar = findViewById(R.id.seekBar);
        mCurDurationTxt = findViewById(R.id.currentDurationTxt);
        mPlayBtn = findViewById(R.id.playBtn);
        mPreviousBtn = findViewById(R.id.control_prev);
        mNextBtn = findViewById(R.id.control_next);
        mVolumeSeekBar = findViewById(R.id.volumeSeekBar);
        mDurationTxt = findViewById(R.id.durationTxt);


        mPlayBtn.setOnClickListener(this);
        mPreviousBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
        binding.volumeDown.setOnClickListener(this);
        binding.volumeUp.setOnClickListener(this);
        binding.stop.setOnClickListener(this);
        binding.controlSwitch.setOnClickListener(this);
        AnimatorSet animatorSet = new AnimatorSet();
        //移动
        ObjectAnimator ty = ObjectAnimator.ofFloat(binding.roundBig, "translationY", 0, 220);
        ty.setDuration(3500);
        ty.setRepeatMode(ValueAnimator.REVERSE);
        ty.setInterpolator(new BounceInterpolator());
        ty.setRepeatCount(-1);
        //旋转
        ObjectAnimator ry = ObjectAnimator.ofFloat(binding.roundBig, "translationX", 0, 360);
        ry.setDuration(3500);
        ry.setRepeatMode(ValueAnimator.REVERSE);
        ry.setInterpolator(new AccelerateDecelerateInterpolator());

        ry.setRepeatCount(-1);

        animatorSet.play(ty).with(ry);
        animatorSet.setDuration(3500);
        animatorSet.start();

        ObjectAnimator tys = ObjectAnimator.ofFloat(binding.roundSmall, "translationY", 0, 200);
        tys.setDuration(3500);
        tys.setRepeatMode(ValueAnimator.REVERSE);
        tys.setInterpolator(new BounceInterpolator());
        tys.setRepeatCount(-1);
        //旋转
        tys.start();
    }

    public SeekBar getVolumeSeekBar() {
        return mVolumeSeekBar;
    }

    public void setDuration(int totalTimeSeconds) {
//        Log.d("com.iyuba.talkshow", "totalSeconds:" + totalTimeSeconds);
        mSeekBar.setMax(totalTimeSeconds);
        totalDuration = totalTimeSeconds;
        currentDuration = 0;
        mDurationTxt.setText(formatTimeSeconds(totalDuration));
        mCurDurationTxt.setText(formatTimeSeconds(currentDuration));
    }

    public void setSeekable(boolean seekAble) {
        this.isSeekable = seekAble;
    }

    public void setMaxVolume(int max) {
        mVolumeSeekBar.setMax(max);
    }

    public void setVolume(int volume) {
        mVolumeSeekBar.setProgress(volume);
    }

    public void setTitle(String title) {
        binding.title.setText(title);
    }

    public void reset() {
        mDurationTxt.setText("__");
        mCurDurationTxt.setText("__");
        mSeekBar.setMax(100);
        mSeekBar.setProgress(0);
        updatePlayBtnStatus(false);
        totalDuration = 0;
        currentDuration = 0;
    }

    public void updateProcess(int timeSeconds) {
        mCurDurationTxt.setText(formatTimeSeconds(timeSeconds));
        mSeekBar.setProgress(timeSeconds);
        currentDuration = timeSeconds;
    }

    public void setOnMediaControlActionListener(OnMediaControlActionListener l) {
        this.mControlActionListener = l;
    }

    public void setOnSeekChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        mSeekBar.setOnSeekBarChangeListener(listener);
    }

    @Override
    public void onClick(View v) {
        if (mControlActionListener == null) {
            return;
        }
        int i = v.getId();
        if (i == R.id.playBtn) {
            mControlActionListener.onPlayBtnClicked();
        } else if (i == R.id.control_prev) {
            mControlActionListener.onPreviousBtnClicked();
        } else if (i == R.id.control_next) {
            mControlActionListener.onNextBtnClicked();
        } else if (i == R.id.volume_down) {
            mControlActionListener.onVolumeDownClicked();
        } else if (i == R.id.volume_up) {
            mControlActionListener.onVolumeUpClicked();
        } else if (i == R.id.stop) {
            mControlActionListener.onStopClicked();
        } else if (i == R.id.control_switch) {
            mControlActionListener.onSwitchClicked();
        }
    }

    public void updatePlayBtnStatus(boolean isPlaying) {
        if (isPlaying) {
            mPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.control_play));
        } else {
            mPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.control_pause));
        }
    }

    public int getCurrentDuration() {
        return currentDuration;
    }

    private String formatTimeSeconds(int seconds) {
        if (seconds < 10) {
            return String.format("00:0%s", seconds);
        }
        if (seconds < 60) {
            return String.format("00:%s", seconds);
        }
        int minutes = seconds / 60;
        int secs = seconds % 60;
        if (minutes < 10) {
            if (secs < 10) {
                return String.format("0%s:0%s", minutes, secs);
            }
            return String.format("0%s:%s", minutes, secs);
        } else {
            return String.format("%s:%s", minutes, secs);
        }
    }
}
