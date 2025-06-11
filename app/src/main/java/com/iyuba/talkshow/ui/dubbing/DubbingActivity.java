package com.iyuba.talkshow.ui.dubbing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoControlsCore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.module.toolbox.GsonUtils;
import com.iyuba.play.ExtendedPlayer;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.local.PreferencesHelper;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.model.WavListItem;
import com.iyuba.talkshow.data.model.WordResponse;
import com.iyuba.talkshow.data.model.result.SendEvaluateResponse;
import com.iyuba.talkshow.databinding.ActivityDubbingBinding;
import com.iyuba.talkshow.event.DownloadEvent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newdata.MediaRecordHelper;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.detail.MyOnTouchListener;
import com.iyuba.talkshow.ui.preview.PreviewActivity;
import com.iyuba.talkshow.ui.preview.PreviewInfoBean;
import com.iyuba.talkshow.util.ScreenUtils;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.TimeUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.talkshow.util.UploadStudyRecordUtil;
import com.iyuba.talkshow.util.videoView.BaseVideoControl;
import com.jaeger.library.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cn.qqtheme.framework.util.LogUtils;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DubbingActivity extends BaseActivity implements DubbingMvpView{

    private static final String VOA = "voa";
    private static final String TIMESTAMP = "timestamp";
    private TranslateAnimation animation;
    private Voa mVoa;
    private List<VoaText> mVoaTextList;
    public static boolean isSend = false;
    private final MediaRecordHelper mediaRecordHelper = new MediaRecordHelper();
//    private AudioEncoder mAudioEncoder;
    private long mTimeStamp;
    private NewScoreCallback mNewScoreCallback;
    private final Map<Integer, WavListItem> map = new HashMap<>();
    ActivityDubbingBinding binding ;

    @Inject
    public DubbingPresenter mPresenter;
    @Inject
    public DubbingAdapter mAdapter;
    @Inject
    public PreferencesHelper mHelper;
    @Inject
    public DataManager mManager;
    private String mDir;
    private String mVideoUrl;
    private String mMediaUrl;
    private long taskMediaId;
    private long taskVideoId;

    private MediaPlayer mAccAudioPlayer;
    private ExtendedPlayer mRecordPlayer;
    private MediaPlayer wordPlayer;

    private DubbingVideoControl mVideoControl;

    private PreviewInfoBean previewInfoBean;

    private boolean mIsFirstIn = true;

    private String wordString  ;

    UploadStudyRecordUtil studyRecordUpdateUtil;
    public long mDuration;

    private void buildMap(int index , WavListItem item ){
        map.put(index , item );
    }

    DubbingAdapter.RecordingCallback mRecordingCallback = new DubbingAdapter.RecordingCallback() {
        @Override
        public void init(String path) {
            initRecord(path);
        }

        @Override
        public void start(final VoaText voaText) {
            startRecording(voaText);
        }

        @Override
        public boolean isRecording() {
            return mediaRecordHelper.isRecording;
//            return isOnRecording();
        }

        @Override
        public void setRecordingState(boolean state) {
//            mAudioEncoder.setState(state);
        }

        @Override
        public void stop() {
            stopRecording();
        }

        @Override
        public void convert(int paraId, List<VoaText> list) {
        }

        @Override
        public void upload(int paraId, int idIndex,List<VoaText> list) {
//            String saveFile = mAudioEncoder.getmSavePath();
            String saveFile = mediaRecordHelper.getFilePath();
            File flacFile = new File(saveFile);

            mPresenter.uploadSentence(list.get(paraId-1 ).sentence(), list.get(paraId-1 ).idIndex(), list.get(paraId-1 ).getVoaId(), paraId, Constant.EVAL_TYPE, String.valueOf(UserInfoManager.getInstance().getUserId()), flacFile).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribeOn(Schedulers.io()).
                    subscribe(new Subscriber<SendEvaluateResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            mNewScoreCallback.onError(paraId, "评测服务暂时有问题，请稍后再试！");
                            if (e != null) {
                                Log.e("DubbingAdapter", "onError  " + e.getMessage());
                            }
                        }

                        @Override
                        public void onNext(SendEvaluateResponse s) {
                            if (isDestroyed()){
                                return ;
                            }
                            int totalScore = (int) (Math.sqrt(Float.parseFloat(s.getData().getTotal_score()) * 2000));
                            mNewScoreCallback.onResult(paraId, totalScore, s.getData());
                            WavListItem item = new WavListItem();
                            item.setUrl(s.getData().getURL());
                            item.setBeginTime(mVoaTextList.get(paraId-1).timing());
                            if (paraId < mVoaTextList.size()  ){
                                item.setEndTime(mVoaTextList.get(paraId).timing());
                            }else {
                                item.setEndTime(mVoaTextList.get(paraId-1).endTiming());
                            }
                            float duration = getAudioFileVoiceTime(flacFile.getAbsolutePath())/1000.0f ;
                            String temp = String.format("%.1f",duration);
                            item.setDuration(Float.parseFloat(temp));
                            item.setIndex(paraId);
                            buildMap(paraId , item);
                            // save to database
                            String wordScore = "";
                            SendEvaluateResponse.DataBean dataBean = s.getData();
                            if (dataBean != null && dataBean.getWords() != null) {
                                List<SendEvaluateResponse.DataBean.WordsBean> words = dataBean.getWords();
                                for (int i = 0; i < words.size(); i++) {
                                    SendEvaluateResponse.DataBean.WordsBean word = words.get(i);
                                    wordScore = wordScore + word.getScore() + ",";
                                }
                            }
                            Log.e("DubbingAdapter", "wordScore  " + wordScore);
                            Log.e("DubbingAdapter", "totalScore  " + totalScore);
                            Log.e("DubbingAdapter", "setItemid  " + mVoa.voaId() + "" + paraId);
                            VoaSoundNew voaSound = VoaSoundNew.builder()
                                    .setItemid(Long.parseLong(mVoa.voaId() + "" + paraId+""+idIndex))
                                    .setUid(UserInfoManager.getInstance().getUserId())
                                    .setVoa_id(mVoa.voaId())
                                    .setTotalscore(totalScore)
                                    .setWordscore(wordScore)
                                    .setFilepath(saveFile)
                                    .setTime("" + mTimeStamp)
                                    .setWords(GsonUtils.toJson(dataBean, SendEvaluateResponse.DataBean.class))
                                    .setSound_url(s.getData().getURL())
                                    .build();
                            mPresenter.saveVoaSound(voaSound);
                        }
                    });
        }
    };

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
            LogUtils.debug("tag", ioException.getMessage());
        }
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        return mediaPlayerDuration;
    }


    DubbingAdapter.PlayVideoCallback mPlayVideoCallback = new DubbingAdapter.PlayVideoCallback() {
        @Override
        public void start(final VoaText voaText) {
            startPlayVideo(voaText);
        }

        @Override
        public boolean isPlaying() {
            return isPlayVideo();
        }

        @Override
        public int getCurPosition() {
            
            return (int) binding.videoView.getCurrentPosition();
        }

        @Override
        public void stop() {
            pause();
        }

        @Override
        public int totalTimes() {
            return (int) binding.videoView.getDuration();
        }
    };

    DubbingAdapter.PlayRecordCallback mPlayRecordCallback = new DubbingAdapter.PlayRecordCallback() {
        @Override
        public void start(final VoaText voaText) {
            startPlayRecord(voaText);
        }

        @Override
        public void stop() {
            pause();
//            mAdapter.mOperateHolder.iPlay.setVisibility(View.VISIBLE);
//            mAdapter.mOperateHolder.iPause.setVisibility(View.INVISIBLE);
        }

        @Override
        public int getLength() {
            return mRecordPlayer.getDuration();
        }
    };

    OnPreparedListener mOnPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            if (mIsFirstIn) {
                mIsFirstIn = false;
                mAdapter.repeatPlayVoaText( mVoaTextList.get(0));
            }
        }
    };

    public static Intent buildIntent(Context context, Voa voa, long timeStamp) {
        Intent intent = new Intent();
        intent.setClass(context, DubbingActivity.class);
        intent.putExtra(VOA, voa);
        intent.putExtra(TIMESTAMP, timeStamp);
        return intent;
    }

    @Override
    public boolean isSwipeBackEnable() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDubbingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), R.color.status_bar_video, getTheme()));
        activityComponent().inject(this);
        mPresenter.attachView(this);
        EventBus.getDefault().register(this);
        mVoa = getIntent().getParcelableExtra(VOA);
        mTimeStamp = getIntent().getLongExtra(TIMESTAMP, 0);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        previewInfoBean = new PreviewInfoBean();
        List<VoaSoundNew> localArrayList = mPresenter.getVoaSoundVoaId(mVoa.voaId());
        if (localArrayList != null && localArrayList.size() > 0) {
            mTimeStamp = Long.parseLong(localArrayList.get(0).time());
            Log.e("DubbingAdapter", "mTimeStamp = " + mTimeStamp);
            for (VoaSoundNew voaSound: localArrayList) {
                if (voaSound != null) {
                    /*previewInfoBean.setSentenceScore(voaSound.itemid(), voaSound.totalscore());
                    previewInfoBean.setSentenceFluent(voaSound.itemid(), voaSound.totalscore());
                    previewInfoBean.setSentenceUrl(voaSound.itemid(), voaSound.sound_url());*/
                    //替换数据
                    previewInfoBean.setSentenceScore(voaSound.voa_id(), voaSound.totalscore());
                    previewInfoBean.setSentenceFluent(voaSound.voa_id(), voaSound.totalscore());
                    previewInfoBean.setSentenceUrl(voaSound.voa_id(), voaSound.sound_url());
                }
            }
        }
        mPresenter.init(mVoa);
        setVideoViewParams();
        startStudyRecord();
        initMedia();
        initAnimation();
        initRecyclerView();
        initClick();
        mPresenter.getVoaTexts(mVoa.voaId());
    }

    private void initClick() {
        binding.previewDubbing.setOnClickListener(v -> onPreviewClick());
        binding.dialogBtnAddword.setOnClickListener(v -> onAddClicked());
        binding.ivAudio.setOnClickListener(v -> onAudioClicked());
        binding.close.setOnClickListener(v -> onCloseClicked());
    }

    private void saveDraft() {
        Record record = getDraftRecord();
//        L.e("save draft ::  " + new Gson().toJson(record));
        mPresenter.saveRecord(record);
    }

    private Record getDraftRecord() {
        int totalNum;
        if (mVoaTextList != null) {
            totalNum = mVoaTextList.size();
        } else {
            totalNum = 0;
        }

        return Record.builder()
                .setTimestamp(mTimeStamp)
                .setVoaId(mVoa.voaId())
                .setTitle(mVoa.title())
                .setTitleCn(mVoa.titleCn())
                .setImg(mVoa.pic())
                .setDate(TimeUtil.getCurDate())
                .setTotalNum(totalNum)
                .setFinishNum(mPresenter.getFinishNum(mVoa.voaId(), mTimeStamp))
                .setScore(previewInfoBean.getAllScore())
                .setAudio(previewInfoBean.getAllAudioUrl())
                .build();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initMedia() {
//        mAudioEncoder = new AudioEncoder();
        mAccAudioPlayer = new MediaPlayer();
        wordPlayer = new MediaPlayer();
        wordPlayer.setOnPreparedListener(mp -> wordPlayer.start());
        mRecordPlayer = new ExtendedPlayer(getApplicationContext());
        MyOnTouchListener listener = new MyOnTouchListener(this);
        listener.setSingleTapListener(mSingleTapListener);
        mVideoControl = new DubbingVideoControl(this);
        mVideoControl.setMode(BaseVideoControl.Mode.SHOW_MANUAL);
        mVideoControl.setFullScreenBtnVisible(false);
        mVideoControl.setButtonListener(new VideoControlsButtonListener() {
            @Override
            public boolean onPlayPauseClicked() {
                if (binding.videoView.isPlaying()) {
                    pause();
                } else {
                    mAdapter.repeatPlayVoaText( mAdapter.getOperateVoaText());
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
        });
        mVideoControl.setBackCallback(() -> {
            if (mPresenter.checkFileExist()) {
                if (!isSend) {
                    int finishNum = mPresenter.getFinishNum(mVoa.voaId(), mTimeStamp);
                    if (finishNum>0) {
                        saveDraft();
                    }
                }
            }
            finish();
        });
        binding.videoView.setControls((VideoControlsCore)mVideoControl);
        binding.videoView.setOnCompletionListener(() -> {
            try {
                binding.videoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoDubbingFile(DubbingActivity.this, mVoa.voaId())));
                mAccAudioPlayer.reset();
                mAccAudioPlayer.setDataSource(StorageUtil.getAudioDubbingFile(DubbingActivity.this, mVoa.voaId()).getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        mVideoControl.setOnTouchListener(listener);
        checkVideoAndMedia();
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

    private void initRecyclerView() {
        mAdapter.setPlayVideoCallback(mPlayVideoCallback);
        mAdapter.setPlayRecordCallback(mPlayRecordCallback);
        mAdapter.setRecordingCallback(mRecordingCallback);
        mAdapter.setScoreCallback((pos, score, fluec, url) -> {
            previewInfoBean.setSentenceScore(pos, score);
            previewInfoBean.setSentenceFluent(pos, fluec);
            previewInfoBean.setSentenceUrl(pos, url);
        });
        mAdapter.setTimeStamp(mTimeStamp);
        mAdapter.SetVoa(mVoa);
        mAdapter.SetActivity(mContext);

        binding.recyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(layoutManager);
    }

    public void checkVideoAndMedia() {
        if (mPresenter.checkFileExist()) {
            binding.loadingView.root.setVisibility(View.GONE);
            setVideoAndAudio();
        } else {
            //下载音频视频
            binding.loadingView.root.setVisibility(View.VISIBLE);
            binding.loadingView.loadingTv.setText(getString(R.string.downloading));
            mPresenter.download();
        }
    }

    public void setVideoAndAudio() {
        try {
            binding.videoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoDubbingFile(this, mVoa.voaId())));
            mAccAudioPlayer.setDataSource(
                    StorageUtil.getAudioDubbingFile(this, mVoa.voaId()).getAbsolutePath());
            binding.videoView.setOnPreparedListener(() -> mDuration = binding.videoView.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        VoaText operateVoaText = mAdapter.getOperateVoaText();
        if (operateVoaText != null) {
            binding.videoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoDubbingFile(this, mVoa.voaId())));
            binding.videoView.seekTo(TimeUtil.secToMilliSec(operateVoaText.timing()));
            mAccAudioPlayer.seekTo(TimeUtil.secToMilliSec(operateVoaText.timing()));
            pauseVideoView();
            mAccAudioPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
//            mPresenter.cancelDownload();
            mPresenter.detachView();
        }
//        mDownloadDialog.dismiss();

        EventBus.getDefault().unregister(this);
        DubbingActivity.isSend = false;
    }

//    @OnClick(R.id.preview_dubbing)
    public void onPreviewClick() {
        List<VoaSoundNew> localArrayList = mPresenter.getVoaSoundVoaId(mVoa.voaId());
        if (localArrayList != null && localArrayList.size() > 0) {
            mPresenter.merge(mVoa.voaId(), mTimeStamp, mVoaTextList, (int) binding.videoView.getDuration());
        } else {
            Toast.makeText(getApplicationContext(), R.string.not_dubbing, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMergeDialog() {
        binding.loadingView.loadingTv.setText(getString(R.string.merging));
        binding.loadingView.root.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissMergeDialog() {
        binding.loadingView.root.setVisibility(View.GONE);
    }

    @Override
    public void showVoaTexts(List<VoaText> voaTextList) {
        mVoaTextList = voaTextList;
        binding.videoView.setOnPreparedListener(mOnPreparedListener);
        mAdapter.setList(voaTextList);
        mAdapter.notifyDataSetChanged();
        studyRecordUpdateUtil.getStudyRecord().setWordCount(voaTextList);
        previewInfoBean.setVoaTexts(voaTextList);
        mPresenter.checkDraftExist(mTimeStamp);
    }

    @Override
    public void showEmptyTexts() {
        mAdapter.setList(Collections.emptyList());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void dismissDubbingDialog() {
//        mDubbingDialog.dismiss();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void startPreviewActivity() {
        stopStudyRecord("1");
        previewInfoBean.initIndexList();
        Record record = getDraftRecord();
        Intent intent = PreviewActivity.buildIntent(mVoaTextList,this, mVoa, previewInfoBean, record, mTimeStamp, false);
        startActivity(intent);
    }

    @Override
    public void showToast(int resId) {
        ToastUtil.show(getApplicationContext(), getResources().getString(resId));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadFinish(DownloadEvent downloadEvent) {
        switch (downloadEvent.status) {
            case DownloadEvent.Status.FINISH:
                binding.loadingView.getRoot().setVisibility(View.GONE);
                setVideoAndAudio();
                break;
            case DownloadEvent.Status.DOWNLOADING:
                binding.loadingView.loadingTv.setText(downloadEvent.msg);
                break;
            default:
                break;
        }
    }

    public void initRecord(String path) {
//        mAudioEncoder.setSavePath(path);
        mediaRecordHelper.setFilePath(path);
    }

    public void startRecording(VoaText voaText) {
        mediaRecordHelper.recorder_Media();
        try {
            binding.videoView.setVolume(0);
            binding.videoView.seekTo(TimeUtil.secToMilliSec(voaText.timing()));
            mAccAudioPlayer.seekTo(TimeUtil.secToMilliSec(voaText.timing()));
            binding.videoView.setOnSeekCompletionListener(() -> {
//                try {
//                    mAudioEncoder.prepare();
//                    mAudioEncoder.start();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            });
            binding.videoView.start();

            if (!mAccAudioPlayer.isPlaying()) {
                mAccAudioPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.record_error);
        }
    }


//    public boolean isOnRecording() {
//        return mAudioEncoder.isRecording();
//    }

    public void stopRecording() {
        mediaRecordHelper.stop_record();
        binding.videoView.setOnSeekCompletionListener(null);
        mAccAudioPlayer.pause();
        pauseVideoView();
//        if (mAudioEncoder.isRecording()) {
//            mAudioEncoder.stop();
//            binding.videoView.setOnSeekCompletionListener(null);
//            mAccAudioPlayer.pause();
//            pauseVideoView();
//        }
    }

    public void startPlayRecord(VoaText voaText) {
        try {
//            mRecordPlayer.reset();
//            mRecordPlayer.setDataSource(voaText.pathLocal);
//                    StorageUtil.getParaRecordAacFile(getApplicationContext(),
//                            voaText.getVoaId(), voaText.paraId(), mTimeStamp).getAbsolutePath());
//            mRecordPlayer.prepareAsync();
            mRecordPlayer.initialize(voaText.pathLocal);
            mRecordPlayer.setOnCompletionListener(mp -> {
                binding.videoView.pause();
                mPlayRecordCallback.stop();
                mAdapter.stopRecordPlayView();
            });
            mRecordPlayer.prepareAndPlay();
            binding.videoView.setVolume(0);
//            mRecordPlayer.setVolume(1.0f, 1.0f);
            seekTo(TimeUtil.secToMilliSec(voaText.timing()));
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startPlayVideo(VoaText voaText) {
        pause();
        binding.videoView.setVolume(1);
        if (voaText != null) {
            binding.videoView.seekTo(TimeUtil.secToMilliSec(voaText.timing()));
        }
        binding.videoView.start();
    }

    public boolean isPlayVideo() {
        return binding.videoView.isPlaying();
    }

    public void start() {
        if (!binding.videoView.isPlaying()) {
            binding.videoView.start();
        }
        if (!mAccAudioPlayer.isPlaying()) {
            mAccAudioPlayer.start();
        }
        if (!mRecordPlayer.isPlaying()) {
            mRecordPlayer.start();
        }
    }

    public void seekTo(int millSec) {

        binding.videoView.seekTo(millSec);
        mAccAudioPlayer.seekTo(millSec);

    }

    @Override
    public void pause() {
        if (binding.videoView.isPlaying()) {
            pauseVideoView();
        }
        if (mAccAudioPlayer.isPlaying()) {
            mAccAudioPlayer.pause();
        }
        if (mRecordPlayer.isPlaying()) {
            mRecordPlayer.pause();
        }

    }

    @Override
    public void onDraftRecordExist(Record record) {
        buildScoreMap(record);
        previewInfoBean.initIndexList(record, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewInfoBean.setVoaTextScore(mVoaTextList, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void buildScoreMap(Record record){
        String score = record.score();
        String audio = record.audio();
        if (TextUtils.isEmpty(score)) {
            return;
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
                    File file = StorageUtil.getParaRecordAacFile(mContext, mVoa.voaId(), i.getKey()+1, mTimeStamp);
                    float duration = getAudioFileVoiceTime(file.getAbsolutePath())/1000.0f ;
                    @SuppressLint("DefaultLocale")
                    String temp = String.format("%.1f",duration);
                    item.setDuration(Float.parseFloat(temp));
                    item.setIndex(i.getKey()+1);
                    buildMap(i.getKey() , item);
                }
            }
        }

    }

    @Override
    public void showWord(WordResponse bean) {
        showWordView(bean);
    }

    private void pauseVideoView() {
        runOnUiThread(() -> binding.videoView.pause());
        stopStudyRecord("0");
    }

    @Override
    public void onBackPressed() {
        if (binding.loadingView.root.getVisibility() == View.VISIBLE) {
            mPresenter.cancelDownload();
            binding.loadingView.root.setVisibility(View.GONE);
            showToastShort("下载已经取消。");
            return;
        } else if (!mPresenter.checkFileExist()) {
            finish();
        } else {
            if (!isSend) {
                if (mPresenter.getFinishNum(mVoa.voaId(), mTimeStamp)==0 ) {
                    finish();
                    return;
                }

                saveDraft();
            } else {
                finish();
            }
        }
    }

    public void startStudyRecord() {
        studyRecordUpdateUtil = new UploadStudyRecordUtil(UserInfoManager.getInstance().isLogin(),
                mContext, UserInfoManager.getInstance().getUserId(), mVoa.voaId(), "1", "2");
    }

    public void stopStudyRecord(String flag) {
        studyRecordUpdateUtil.stopStudyRecord(getApplicationContext(), UserInfoManager.getInstance().isLogin(), flag, mPresenter.getUploadStudyRecordService());
    }

    public void onCloseClicked() {
        binding.jiexiRoot.setVisibility(View.GONE);
    }

    public void onAddClicked() {
        if (!UserInfoManager.getInstance().isLogin()) {
            NewLoginUtil.startToLogin(mContext);
            return;
        }
        List<String> words = Collections.singletonList(wordString);
        mPresenter.insertWords(UserInfoManager.getInstance().getUserId(), words);
    }


    public void onAudioClicked() {
        try {
            wordPlayer.reset();
            String playurl = "http://dict.youdao.com/dictvoice?audio=";
            wordPlayer.setDataSource(playurl +wordString);
            wordPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    interface NewScoreCallback {
        void onResult(int paraId, int score, SendEvaluateResponse.DataBean beans);
        void onError(int pos, String errorMessage);
    }

    void setNewScoreCallback(NewScoreCallback mNewScoreCallback) {
        this.mNewScoreCallback = mNewScoreCallback;
    }


    public void initAnimation(){
        animation = new TranslateAnimation(-300,0,0,0);
        animation.setDuration(500);

    }

    private void showWordView(WordResponse bean ) {
        pauseVideoView();
        wordString = bean.getKey() ;
        binding.jiexiRoot.startAnimation(animation);
        binding.jiexiRoot.setVisibility(View.VISIBLE);
        binding.word.setText(bean.getKey());
        binding.def.setText(bean.getDef());
        binding.pron.setText(String.format("[%s]",bean.getPron()));
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

}
