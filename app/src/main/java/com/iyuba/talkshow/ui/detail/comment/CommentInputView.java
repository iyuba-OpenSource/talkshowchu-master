package com.iyuba.talkshow.ui.detail.comment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.CommentInputViewBinding;
import com.iyuba.talkshow.event.PauseEvent;
import com.iyuba.talkshow.event.StopEvent;
import com.iyuba.talkshow.util.Recorder;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

public class CommentInputView extends RelativeLayout {

    private RequestPermissionCallback mPermissionCb;

    public interface Mode {
        int TEXT = 1;
        int VOICE = 2;
    }

    private Recorder mRecorder;
    private MediaPlayer mMediaPlayer;

    private boolean mIsRecordReady = false;
    private boolean mGestureUp = false;

    private String mRecordFilePath;

    public int mMode = Mode.TEXT;
    private OnCommentSendListener mSendListener;
    private RecorderListener mRecorderListener;
    private InputMethodCallback mInputMethodCallback;

    CommentInputViewBinding binding;


    public CommentInputView(Context context) {
        this(context, null);
    }

    public CommentInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = CommentInputViewBinding.inflate(LayoutInflater.from(context),this,false);
        addView(binding.getRoot());
        setClick();

//        inflate(context, R.layout.comment_input_view, this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setClick() {
        binding.ivSwitchMode.setOnClickListener(v -> clickModeSwitch());
        binding.tvListeningTest.setOnClickListener(v -> clickPlayVoice());
        binding.tvSend.setOnClickListener(v -> clickVoiceSend());
        binding.tvTouchSay.setOnTouchListener((v, event) -> {
            return touchVoiceBtn(v, event);
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initState();

    }

    void clickModeSwitch() {
        changeMode();
        initState();
    }

    public void changeMode() {
        if (mMode == Mode.TEXT) {
            mMode = Mode.VOICE;
        } else if (mMode == Mode.VOICE) {
            binding.etText.requestFocus();
            mMode = Mode.TEXT;
        }
    }

    void clickPlayVoice() {
        if (mIsRecordReady) {
            try {
                EventBus.getDefault().post(new StopEvent(StopEvent.SOURCE.VIDEO));
                mMediaPlayer.setOnCompletionListener(null);
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mRecordFilePath);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void clickVoiceSend() {
        if (mSendListener != null) {
            switch (mMode) {
                case Mode.VOICE:
                    if (mIsRecordReady) {
                        File file = new File(mRecordFilePath);
                        if (file.exists()) {
                            mSendListener.onVoiceSend(file);
                        }
                    }

                    break;
                case Mode.TEXT:
                default:
                    String textComment = binding.etText.getText().toString();
                    if (!textComment.equals("") && mSendListener != null) {
                        InputMethodManager imm = (InputMethodManager) getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(binding.etText.getWindowToken(), 0);
                        mSendListener.onTextSend(textComment);
                    }
                    break;
            }
        }
    }

    //    @OnTouch(R.id.tv_touch_say)
    boolean touchVoiceBtn(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mGestureUp = false;
                EventBus.getDefault().post(new StopEvent(StopEvent.SOURCE.VIDEO));
                if (mRecordFilePath != null && mPermissionCb != null) {
                    mPermissionCb.requestRecordPermission();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                mGestureUp = true;
                if (mRecorder != null) {
                    mRecorder.stop();
                }
                break;
        }
        return true;
    }

    public void onRecordPermissionGranted() {
        if (!mGestureUp) {
            EventBus.getDefault().post(new PauseEvent());
            Timber.e("mRecorder.start------------");
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mRecorder.start(mRecordFilePath);
        }
    }

    public void initState() {
        switch (mMode) {
            case Mode.VOICE:
                binding.textInputContainer.setVisibility(View.GONE);
                binding.voiceInputContainer.setVisibility(View.VISIBLE);
                binding.ivSwitchMode.setImageResource(R.drawable.text_mode);
                if (mInputMethodCallback != null) {
                    mInputMethodCallback.hide();
                }
                break;
            case Mode.TEXT:
            default:
                binding.textInputContainer.setVisibility(View.VISIBLE);
                binding.voiceInputContainer.setVisibility(View.GONE);
                binding.ivSwitchMode.setImageResource(R.drawable.voice_mode);
                if (getVisibility() == VISIBLE) {
                    binding.etText.requestFocus();
                }
                if (mInputMethodCallback != null) {
                    mInputMethodCallback.show();
                }
                break;
        }
    }

    public void release() {
        new File(mRecordFilePath).delete();
    }

    private final Recorder.OnStateChangeListener mStateListener = new Recorder.OnStateChangeListener() {
        @Override
        public void onStateChange(int newState) {
            switch (newState) {
                case Recorder.State.INITIAL:
                    Timber.e("INITIAL");
                    mTimerHandler.removeMessages(0);
                    break;
                case Recorder.State.RECORDING:
                    Timber.e("RECORDING");
                    mIsRecordReady = false;
                    if (mRecorderListener != null) mRecorderListener.onBegin();
                    mTimerHandler.sendEmptyMessage(0);
                    break;
                case Recorder.State.COMPLETED:
                    Timber.e("COMPLETED");
                    mIsRecordReady = true;
                    if (mRecorderListener != null) mRecorderListener.onEnd();
                    mTimerHandler.removeMessages(0);
                    break;
                case Recorder.State.ERROR:
                    Timber.e("ERROR");
                    if (mRecorderListener != null) mRecorderListener.onError();
                    mTimerHandler.removeMessages(0);
                    break;
                default:
                    break;
            }
        }
    };

    private final Handler mTimerHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mRecorderListener != null)
                mRecorderListener.onVolumeChanged(mRecorder.getCurrentDB());
            mTimerHandler.sendEmptyMessageDelayed(0, 200);
            return false;
        }
    });

    public void setRecordFilePath(String path) {
        this.mRecordFilePath = path;
    }

    public void setRecorder(Recorder recorder) {
        if (recorder != null) {
            mRecorder = recorder;
            mRecorder.setOnStateChangeListener(mStateListener);
        }
    }

    public void replyToSomeone(String targetUserName) {
        if (mMode == Mode.VOICE) {
            clickModeSwitch();
        }
        binding.etText.findFocus();
        binding.etText.requestFocus();
        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(binding.etText, 0);
        binding.etText.setText(getResources().getString(R.string.reply) + targetUserName + ":");
        binding.etText.setSelection(binding.etText.length());
    }

    public void setOnCommentSendListener(OnCommentSendListener l) {
        this.mSendListener = l;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mMediaPlayer = mediaPlayer;
    }

    public void setRecorderListener(RecorderListener l) {
        this.mRecorderListener = l;
    }

    public void setRequestPermissionCallback(RequestPermissionCallback callback) {
        this.mPermissionCb = callback;
    }

    public void setInputMethodCallback(InputMethodCallback inputMethodCallback) {
        this.mInputMethodCallback = inputMethodCallback;
    }

    public void clearInputText() {
        binding.etText.setText("");
    }

    public interface OnCommentSendListener {
        void onTextSend(String comment);

        void onVoiceSend(File record);
    }

    public interface RequestPermissionCallback {
        void requestRecordPermission();
    }

    public interface RecorderListener {
        void onBegin();

        void onVolumeChanged(int db);

        void onEnd();

        void onError();
    }

    public interface InputMethodCallback {
        void show();

        void hide();
    }
}
