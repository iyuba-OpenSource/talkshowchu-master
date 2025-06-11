package devcontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.dlna.player.control.IController;
import com.android.dlna.player.control.MultiPointController;

import org.cybergarage.android.R;
import org.cybergarage.android.databinding.ActivityControlPointBinding;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.control.ActionListener;

import java.util.List;

import devsearch.DevSearchActivity;

import static com.android.dlna.manager.DLNADeviceManager.getInstance;

public class DevControlActivity extends Activity implements DevControlMvpView, ControlPointView.OnMediaControlActionListener, SeekBar.OnSeekBarChangeListener {

    ActivityControlPointBinding binding ;
    private static final int REQ_CHOOSE_DEVICE = 100;
//    @BindView(R2.id.controlPointView)
    ControlPointView controlPointView;
    private MultiPointController mController;
    private String currentUri;
    private String title;
    private Device mDevice;


    private final int MAX_VOLUME = 100;
    private final int STEP_VOLUME = 5;
    private int current_volume = 0;


    public static final int HIDE_VOLUME = 100;

    @SuppressLint("HandlerLeak")
    private final Handler volumeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HIDE_VOLUME:
                    binding.volumeLayout.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private List<String> currentUris;
    private List<String> titles;
    private int playingIndex = 0 ;
    private int playListSize ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityControlPointBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();

        controlPointView = binding.controlPointView;
        controlPointView.setOnMediaControlActionListener(this);
        controlPointView.setOnSeekChangeListener(this);
        controlPointView.setTitle(title);
        mController = new MultiPointController();
        mController.setPlayMonitor(new IController.PlayerMonitor() {
            @Override
            public void onPreparing() {

            }

            @Override
            public void onGetMediaDuration(int totalTimeSeconds) {
                controlPointView.setDuration(totalTimeSeconds);
            }

            @Override
            public void onGetMaxVolume(int max) {
                controlPointView.setMaxVolume(max);
//                Log.d("com.iyuba.talkshow", "seconds:" + max + "");
            }

            @Override
            public void onMuteStatusChanged(boolean mute) {

            }

            @Override
            public void onVolumeChanged(int current) {
                current_volume = current;
//                controlPointView.setVolume(current);

            }

            @Override
            public void onPlay() {
                controlPointView.updatePlayBtnStatus(true);
            }

            @Override
            public void onPause() {
                controlPointView.updatePlayBtnStatus(false);
            }

            @Override
            public void onStop() {

            }

            @Override
            public void onError() {
//                Log.d("com.iyuba.talkshow", "error");
            }

            @Override
            public void onProgressUpdated(int currentTimeSeconds) {
                controlPointView.updateProcess(currentTimeSeconds);
            }

            @Override
            public void onSeekComplete() {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onPlayItemChanged() {

            }
        });
        checkDevice();
        binding.volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mController.setVolume(mDevice, seekBar.getProgress());
            }
        });


    }

    private void initData() {
        currentUri = getIntent().getStringExtra("url");
        currentUris = (List<String>) getIntent().getSerializableExtra("urls");
        titles = (List<String>) getIntent().getSerializableExtra("titles");
        title = getIntent().getStringExtra("title");
        playListSize = titles.size();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mController.seek(mDevice, progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPlayBtnClicked() {
        if (mController.isPlaying()) {
            pause();
            return;
        }
        if (mController.isPaused()) {
            resumePlay(controlPointView.getCurrentDuration());
        } else {
            play(currentUri);
        }
    }

    @Override
    public void onNextBtnClicked() {
        mController.play(mDevice,currentUris.get(Math.abs(++playingIndex%playListSize)) );

        controlPointView.setTitle(titles.get(Math.abs(playingIndex%playListSize)));

    }

    @Override
    public void onPreviousBtnClicked() {

        mController.play(mDevice,currentUris.get(Math.abs(--playingIndex%playListSize)) );
        controlPointView.setTitle(titles.get(Math.abs(playingIndex%playListSize)));

    }

    @Override
    public void onVolumeDownClicked() {
        mController.setVolume(mDevice, current_volume - STEP_VOLUME);
        if (current_volume > 0) {
            current_volume = current_volume - STEP_VOLUME;
        }
        showVolumnView();
    }

    private void showVolumnView() {
        binding.volumeLayout.setVisibility(View.VISIBLE);
        binding.volumeSeekbar.setProgress(current_volume);
        if (current_volume <= 0) {
            binding.volumnImg.setImageDrawable(getResources().getDrawable(R.drawable.volumn_silent));
        }else {
            binding.volumnImg.setImageDrawable(getResources().getDrawable(R.drawable.volume_up));
        }
        volumeHandler.removeCallbacksAndMessages(null);
        volumeHandler.sendEmptyMessageDelayed(HIDE_VOLUME, 2000);
    }

    @Override
    public void onVolumeUpClicked() {
        mController.setVolume(mDevice, current_volume + STEP_VOLUME);
        if (current_volume < 100) {
            current_volume = current_volume + STEP_VOLUME;
        }
        showVolumnView();
    }

    @Override
    public void onStopClicked() {
        mController.stop(mDevice);
        finish();
    }

    @Override
    public void onSwitchClicked() {
//        DLNADeviceManager.getInstance().setCurrentDevice(null);
        Intent intent = new Intent(this, DevSearchActivity.class);
        startActivityForResult(intent, REQ_CHOOSE_DEVICE);
//        finish();
    }

    private void play(String uri) {
        mController.play(mDevice, uri);
    }

    private void pause() {
        mController.pause(mDevice);
    }

    private void resumePlay(final int pausedTimeSeconds) {
        mController.resume(mDevice, pausedTimeSeconds);
    }

    private void onDeviceReady() {
        play(currentUri);
        mDevice.setActionListener(new ActionListener() {
            @Override
            public boolean actionControlReceived(Action action) {
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CHOOSE_DEVICE:
                if (resultCode == RESULT_OK) {
                    //refresh device
                    mDevice = getInstance().getCurrentDevice();
                    onDeviceReady();
                } else {
                    Toast.makeText(this, "未选择设备", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void checkDevice() {
        if (getInstance().getCurrentDevice() == null) {
            Intent intent = new Intent(this, DevSearchActivity.class);
            startActivityForResult(intent, REQ_CHOOSE_DEVICE);
        } else {
            mDevice = getInstance().getCurrentDevice();
            onDeviceReady();
        }
    }
}