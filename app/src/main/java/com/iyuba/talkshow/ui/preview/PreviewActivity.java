package com.iyuba.talkshow.ui.preview;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoControlsCore;
import com.iyuba.play.ExtendedPlayer;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.model.WavListItem;
import com.iyuba.talkshow.databinding.ActivityPreviewBinding;
import com.iyuba.talkshow.lil.help_fix.ui.dubbing.DubbingNewActivity;
import com.iyuba.talkshow.lil.help_mvp.util.StackUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newce.ContianerActivity;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.detail.MyOnTouchListener;
import com.iyuba.talkshow.ui.detail.NormalVideoControl;
import com.iyuba.talkshow.ui.detail.ranking.watch.WatchDubbingActivity;
import com.iyuba.talkshow.ui.main.drawer.Share;
import com.iyuba.talkshow.ui.user.me.dubbing.MyDubbingActivity;
import com.iyuba.talkshow.ui.widget.LoadingAdDialog;
import com.iyuba.talkshow.util.ScreenUtils;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.videoView.BaseVideoControl;
import com.jaeger.library.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PreviewActivity extends BaseActivity implements PreviewMvpView {

    private static final String IS_FROM_UNRELEASED = "isFromReleased";
    private static final String TIMESTAMP = "timestamp";
    private static final String VOA = "voa";
    private static final String SIZE = "SIZE";
    private static final String MAP = "map";
    private static final String PREVIEW_INFO = "previewInfo";
    private static final String RECORD = "RECORD";
    private static final String VOATEXT = "VOATEXT";

    private Voa mVoa;
    private long mTimestamp;
    private boolean isFromReleased;
    private PreviewInfoBean previewInfoBean;

    private MediaPlayer mMp3MediaPlayer;
    private IjkMediaPlayer dubbingPlayer;
    private boolean isAccPrepared = false;
    private boolean isVideoPrepared = false;
    private boolean isMp3Prepared = false;
    private String shareString ;
    private long mCurPosition;
    private ExtendedPlayer mAacMediaPlayer;
    private List<VoaSoundNew> voaRecord = null;

    ActivityPreviewBinding binding ;
    @Inject
    PreViewPresenter mPresenter;

    private NormalVideoControl mVideoControl;
    private LoadingAdDialog mLoadingDialog;

    Record draftRecord;
    private final HashMap<Integer , WavListItem> map = new HashMap<>();
    private List<VoaText> mVoaTexts;
    private Disposable dis;
    private int dubbingPosition = -1;

    public static Intent buildIntent(List<VoaText> voaTextList , Context context, Voa voa,
                                     PreviewInfoBean previewInfoBean, Record draftRecord, long timeStamp, boolean isFromReleased) {
        Intent intent = new Intent();
        intent.putExtra(VOA, voa);
        intent.putExtra(VOATEXT, (Serializable) voaTextList);
        intent.putExtra(RECORD, draftRecord);
        intent.putExtra(PREVIEW_INFO, previewInfoBean);
        intent.putExtra(TIMESTAMP, timeStamp);
        intent.putExtra(IS_FROM_UNRELEASED, isFromReleased);
        intent.setClass(context, PreviewActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), R.color.status_bar_video, getTheme()));
        mPresenter.attachView(this);

        mTimestamp = getIntent().getLongExtra(TIMESTAMP, 0);
        mVoa = getIntent().getParcelableExtra(VOA);
        mVoaTexts = (List<VoaText>) getIntent().getSerializableExtra(VOATEXT);
        isFromReleased = getIntent().getBooleanExtra(IS_FROM_UNRELEASED, false);
        draftRecord = getIntent().getParcelableExtra(RECORD);
        previewInfoBean = (PreviewInfoBean) getIntent().getSerializableExtra(PREVIEW_INFO);

        mLoadingDialog = new LoadingAdDialog(this);
        mLoadingDialog.setTitleText(mPresenter.formatTitle(getString(R.string.record_publishing)));
        mLoadingDialog.setMessageText(
                mPresenter.formatMessage(
                        previewInfoBean.getWordCount(),
                        previewInfoBean.getAverageScore(),
                        previewInfoBean.getRecordTime()
                )
        );
        mLoadingDialog.setOnDismissListener(dialogInterface -> mPresenter.cancelUpload());
        mLoadingDialog.setRetryOnClick(view -> {
            mLoadingDialog.retry();
            onClickReleaseAndShare();
        });
        setProgressBar();
        try {
            dubbingPlayer = new IjkMediaPlayer();
            mAacMediaPlayer = new ExtendedPlayer(getApplicationContext());
            startDubbingOb();
            dubbingPlayer.setOnCompletionListener(iMediaPlayer -> {
                if (dubbingPosition == mVoaTexts.size() -1 ) return ;
                startDubbingOb();
            });
//            mAacMediaPlayer.setDataSource(mPresenter.getMp3RecordPath(mVoa.voaId(), mTimestamp));
//            mAacMediaPlayer.prepareAsync();
//            mAacMediaPlayer.setVolume(1f,1f);
//            mAacMediaPlayer.setOnPreparedListener(mAacPreparedListener);
            mMp3MediaPlayer = new MediaPlayer();
            mMp3MediaPlayer.setDataSource(mPresenter.getMp3Path(mVoa.voaId()));

            mMp3MediaPlayer.prepareAsync();
            mMp3MediaPlayer.setVolume(1f,1f);

            mMp3MediaPlayer.setOnPreparedListener(mMp3PreparedListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isFromReleased) {
            binding.tvSaveDraft.setVisibility(View.GONE);
            binding.backBtn.setVisibility(View.GONE);
        } else {
            binding.tvSaveDraft.setVisibility(View.VISIBLE);
            binding.backBtn.setVisibility(View.VISIBLE);
        }

        initMedia();
        initClick();
    }

    private void initClick() {
        binding.tvPublishShare.setOnClickListener(v -> onClickReleaseAndShare());
        binding.tvShareFriends.setOnClickListener(v -> onClickShareFriends());
        binding.tvSaveDraft.setOnClickListener(v -> onClickSaveLocal());
        binding.tvBackHome.setOnClickListener(v -> onClickBackHome());
        binding.backBtn.setOnClickListener(v -> OnClickBackBtn());
    }

    private void startDubbingOb() {
        dis =  Observable.interval(50, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (dubbingPosition !=findVoaPosition(binding.videoView.getCurrentPosition())) {
                            dubbingPosition = findVoaPosition(binding.videoView.getCurrentPosition());
                        }else {
                            return ;
                        }
                        File file = StorageUtil.getParaRecordAacFile(mContext, mVoa.voaId(), dubbingPosition + 1, mTimestamp);
                        Log.e("PreviewActivity", "startDubbingOb = " + file.getAbsolutePath());
                        if (file.exists()) {
//                            dubbingPlayer.reset();
//                            dubbingPlayer.setDataSource(file.getAbsolutePath());
//                            dubbingPlayer.prepareAsync();
//                            dubbingPlayer.start();
                            Log.e("PreviewActivity", "startDubbingOb dubbingPosition = " + dubbingPosition);
                            mAacMediaPlayer.initialize(file.getAbsolutePath());
                            mAacMediaPlayer.prepareAndPlay();
                            mAacMediaPlayer.setOnCompletionListener(iPlayer -> {
                                if (dubbingPosition == mVoaTexts.size() -1 ) return ;
                                startDubbingOb();
                            });
                            dis.dispose();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private int findVoaPosition(long currentPosition) {
        int res = -1 ;
        for (int i = 0 ; i<mVoaTexts.size() ; i++  ){
            if (currentPosition>mVoaTexts.get(i).timing()*1000){
                res = i ;
            }
        }
        return  res ;
    }

    private void setProgressBar() {
        voaRecord = mPresenter.getVoaSoundVoaId(mVoa.voaId());
        if (voaRecord != null && voaRecord.size() > 0) {
            int total = voaRecord.size();
            int score = 0;
            double fluence = 0.0;
            for (VoaSoundNew soundNew: voaRecord) {
                score += soundNew.totalscore();
                String[] wordArray = soundNew.wordscore().split(",");
                if (wordArray != null && wordArray.length > 0) {
                    double wordAll = 0.0;
                    for (String  word: wordArray) {
                        if (!TextUtils.isEmpty(word)) {
                            wordAll += Double.parseDouble(word);
                        }
                    }
                    fluence += wordAll / wordArray.length;
                }
            }
            int average = score / total;
            int avFluence = (int) (20 * fluence / total);
            binding.progressAccuracy.setProgress(average);
            binding.tvAccuracy.setText(average + "");
            binding.progressCompleteness.setProgress(previewInfoBean.getCompleteness(total));
            binding.tvCompleteness.setText(previewInfoBean.getCompleteness(total) + "");
            binding.progressFluence.setProgress(avFluence);
            binding.tvFluence.setText(avFluence + "");
            return;
        }
        binding.progressAccuracy.setProgress(previewInfoBean.getAverageScore());
        binding.tvAccuracy.setText(previewInfoBean.getAverageScore() + "");
        binding.progressCompleteness.setProgress(previewInfoBean.getCompleteness());
        binding.tvCompleteness.setText(previewInfoBean.getCompleteness() + "");
        binding.progressFluence.setProgress(previewInfoBean.getFluence());
        binding.tvFluence.setText(previewInfoBean.getFluence()+"");
    }

    public void initMedia() {
        MyOnTouchListener listener = new MyOnTouchListener(this);
        listener.setSingleTapListener(mSingleTapListener);

        mVideoControl = new NormalVideoControl(this);
        mVideoControl.setPlayPauseDrawables(getResources().getDrawable(R.drawable.play), getResources().getDrawable(R.drawable.pause));
        mVideoControl.setMode(BaseVideoControl.Mode.SHOW_MANUAL);
        mVideoControl.setButtonListener(mBtnListener);
        mVideoControl.setBackCallback(() -> finish());
        binding.videoView.setControls((VideoControlsCore) mVideoControl);
        mVideoControl.setOnTouchListener(listener);
        binding.videoView.setVideoURI(mPresenter.getVideoUri(mVoa.voaId()));
        binding.videoView.setVolume(0);
        binding.videoView.setOnPreparedListener(mVideoPreparedListener);
        binding.videoView.setOnCompletionListener(mVideoCompletionListener);
        binding.videoView.setOnSeekCompletionListener(() -> {
            long curPosition = binding.videoView.getCurrentPosition();
            mMp3MediaPlayer.seekTo((int) curPosition);
//            mAacMediaPlayer.seekTo((int) curPosition);
        });
    }

    MyOnTouchListener.SingleTapListener mSingleTapListener = new MyOnTouchListener.SingleTapListener() {
        @Override
        public void onSingleTap() {
            if (mVideoControl != null) {
                if (mVideoControl.getControlVisibility() == View.GONE) {
                    mVideoControl.show();
                    if (binding.videoView.isPlaying()) {
                        mVideoControl.hideDelayed(VideoControls.DEFAULT_CONTROL_HIDE_DELAY);
                    }
                } else {
                    mVideoControl.hideDelayed(0);
                }
            }
        }
    };

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            binding.backBtn.setVisibility(View.GONE);
        } else {
            binding.backBtn.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setVideoViewParams();
    }

    private void setVideoViewParams() {
        ViewGroup.LayoutParams lp = binding.videoView.getLayoutParams();
        int[] screenSize = ScreenUtils.getScreenSize(this);
        lp.width = screenSize[0];
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = screenSize[1]; // 16 : 9
        } else {
            lp.height = (int) (lp.width * 0.5625);
        }
        binding.videoView.setLayoutParams(lp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        mCurPosition = binding.videoView.getCurrentPosition();
        pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (mCurPosition > 0) {
            binding.videoView.setVideoURI(mPresenter.getVideoUri(mVoa.voaId()));
            binding.videoView.seekTo((int) mCurPosition);
            binding.videoView.setVolume(0);
            mMp3MediaPlayer.seekTo((int) mCurPosition);
//            mAacMediaPlayer.seekTo((int) mCurPosition);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dis!=null) dis.dispose();
        if (mAacMediaPlayer != null) {
            mAacMediaPlayer.stopAndRelease();
        }
        if (mMp3MediaPlayer != null) {
            mMp3MediaPlayer.release();
        }
        if (dubbingPlayer != null) {
            dubbingPlayer.release();
        }
        binding.videoView.release();
        mPresenter.detachView();

    }

    public void onClickReleaseAndShare() {
        pause();
//        File file1= getAacRecordFile(mVoa.voaId(), mTimestamp);
        File file = StorageUtil.getM4AMergeFile(mContext, mVoa.voaId(), mTimestamp);
        Log.e("PreviewActivity", "share getAacRecordFile = " + file.getAbsolutePath());
        map.clear();
        int totalScore = 0;
        for (VoaSoundNew voaSound: voaRecord) {
            totalScore += voaSound.totalscore();
            String voaid = "" + voaSound.voa_id();
            String itemid = "" + voaSound.itemid();
            int paraId = Integer.parseInt(itemid.replace(voaid, ""));
            //这里有个bug，因为itemid是由voaid+paraid+indexid合并的，因此后面几个是paraid+indexid的，需要判断
            //因为indexid只有一位，所以可以判断最后一个是indexid
            para:for (int i = 0; i < String.valueOf(paraId).length(); i++) {
                paraId = paraId/10;
                if (paraId>mVoaTexts.size()){
                    continue;
                }else {
                    break para;
                }
            }

            WavListItem item = new WavListItem();
            item.setUrl(voaSound.sound_url());
            if ((0 < paraId) && (paraId < mVoaTexts.size())) {
                item.setBeginTime(mVoaTexts.get(paraId-1).timing());
                item.setEndTime(mVoaTexts.get(paraId).timing());
            } else {
                item.setBeginTime(mVoaTexts.get(paraId-1).timing());
                item.setEndTime(mVoaTexts.get(paraId-1).endTiming());
            }
            float duration = getAudioFileVoiceTime(voaSound.filepath())/1000.0f ;
            Log.e("PreviewActivity", "share duration " + duration);
            String temp = String.format("%.1f",duration);
            item.setDuration(Float.parseFloat(temp));
            item.setIndex(paraId);
            map.put(paraId, item );
        }
        Log.e("PreviewActivity", "share totalScore/voaRecord.size() = " + totalScore/voaRecord.size());
        mPresenter.releaseDubbing(map , mVoa.voaId(), mVoa.sound(), mTimestamp, totalScore/voaRecord.size(), file, mVoa.category());
    }

    public long getAudioFileVoiceTime(String filePath) {
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
            Log.e("PreviewActivity", ioException.getMessage());
        }
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        return mediaPlayerDuration;
    }

    private File getAacRecordFile(int voaId, long timeStamp) {
        return StorageUtil.getAacMergeFile(mContext, voaId, timeStamp);
    }

    public void onClickShareFriends() {
        pause();
        showShareView(shareString);
    }

    public void onClickSaveLocal() {
        mPresenter.saveRecord(draftRecord);
    }

    public void onClickBackHome() {
        mPresenter.deleteRecord(mVoa.voaId(), mTimestamp);
        startMainActivity();
    }

    public void OnClickBackBtn() {
        finish();
    }

    MediaPlayer.OnPreparedListener mAacPreparedListener = mp -> {
        isAccPrepared = true;
        if (isVideoPrepared && isMp3Prepared) {
            mp.start();
        }
    };

    MediaPlayer.OnPreparedListener mMp3PreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            isMp3Prepared = true;
//            if (isAccPrepared && isVideoPrepared) {
                start();
//            }
        }
    };

    OnPreparedListener mVideoPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            isVideoPrepared = true;
//            if (isAccPrepared && isMp3Prepared) {
                start();
//            }
        }
    };

    OnCompletionListener mVideoCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion() {
            binding.videoView.restart();

            dubbingPosition = -1;
            startDubbingOb();
            mMp3MediaPlayer.seekTo(0);
            mMp3MediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMp3MediaPlayer.pause();
                }
            });
        }
    };

    public void start() {
        if (binding.videoView != null  && mMp3MediaPlayer != null) {
            binding.videoView.start();
//            mAacMediaPlayer.start();
            try {
                if (!mMp3MediaPlayer.isPlaying()) {
                    mMp3MediaPlayer.start();
                }
            } catch (Exception var1) {
                var1.printStackTrace();
            }
        }
    }

    public void pause() {
        if (binding.videoView != null) {
            binding.videoView.pause();
        }
//        if (mAacMediaPlayer != null) {
//            mAacMediaPlayer.pause();
//        }
        if (mMp3MediaPlayer != null) {
            mMp3MediaPlayer.pause();
        }
    }

    VideoControlsButtonListener mBtnListener = new VideoControlsButtonListener() {
        @Override
        public boolean onPlayPauseClicked() {
            if (binding.videoView == null ||
//                    mAacMediaPlayer == null ||
                    mMp3MediaPlayer == null) {
                return false;
            }

            if (binding.videoView.isPlaying()
//                    || mAacMediaPlayer.isPlaying()
                    || mMp3MediaPlayer.isPlaying()) {
                pause();
            } else {
                start();
            }

            return true;
        }

        @Override
        public boolean onPreviousClicked() {
            return false;
        }

        @Override
        public boolean onNextClicked() {
            return false;
        }

        @Override
        public boolean onRewindClicked() {
            return false;
        }

        @Override
        public boolean onFastForwardClicked() {
            return false;
        }
    };

    @Override
    public boolean isSwipeBackEnable() {
        return false;
    }

    @Override
    public void startLoginActivity() {
        NewLoginUtil.startToLogin(this);
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showShareView(String filePath) {
        shareString = filePath ;
        if (ConfigData.openShare) {
            Share.prepareDubbingMessage(this ,  mVoa, mPresenter.getShuoshuoId(), UserInfoManager.getInstance().getUserName(), mPresenter.getIntegralService(), UserInfoManager.getInstance().getUserId());
        } else {
            ToastUtil.showToast(this, "对不起，分享暂时不支持");
        }
    }

    @Override
    public void showShareHideReleaseButton() {
        if (ConfigData.openShare) {
            binding.tvPublishShare.setVisibility(View.GONE);
            binding.tvShareFriends.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void startMyDubbingActivity() {
        Intent intent = MyDubbingActivity.buildIntent(this, MyDubbingActivity.Item.UNRELEASED, true);
        startActivity(intent);
    }

    @Override
    public void showReleaseButton() {
        binding.tvPublishShare.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.show(UserInfoManager.getInstance().isVip());
    }

    @Override
    public void dismissLoadingDialog() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void showPublishSuccess(int resID) {
        mLoadingDialog.setCanceledOnTouchOutside(true);
        mLoadingDialog.showSuccess(mPresenter.formatTitle(getString(resID)));
        dismissPublishDialog();
    }

    @Override
    public void showPublishFailure(int resID) {
        mLoadingDialog.setCanceledOnTouchOutside(true);
        mLoadingDialog.showFailure(getString(resID));
        mLoadingDialog.showRetryButton();
    }

    void dismissPublishDialog() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog.dismiss();
            }
        }, 5 * 1000);
    }

    @Override
    public void startMainActivity() {

        //这里根据来的界面不同，跳转不同的界面
        if (isFromReleased){
            StackUtil.getInstance().finish(DubbingNewActivity.class);
            StackUtil.getInstance().finish(PreviewActivity.class);
        }else {
            Intent intent = new Intent(this, ContianerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (mVideoControl.isFullScreen()) {
            mVideoControl.exitFullScreen();
        } else {
            finish();
        }
//        mAacMediaPlayer.stop();
        mMp3MediaPlayer.stop();
    }
}
