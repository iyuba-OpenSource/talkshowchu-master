package com.iyuba.talkshow.newce.study.eval;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.play.IJKPlayer;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.local.PreferencesHelper;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.model.WavListItem;
import com.iyuba.talkshow.databinding.FragmentEvalBinding;
import com.iyuba.talkshow.event.StudyReportEvent;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_mvp.util.BigDecimalUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newce.study.StudyActivity;
import com.iyuba.talkshow.newdata.AudioComposeApi;
import com.iyuba.talkshow.newdata.AudioSendApi;
import com.iyuba.talkshow.newdata.Config;
import com.iyuba.talkshow.newdata.EvaMixBean;
import com.iyuba.talkshow.newdata.EvaSendBean;
import com.iyuba.talkshow.newdata.MyIjkPlayer;
import com.iyuba.talkshow.newdata.Playmanager;
import com.iyuba.talkshow.newdata.RefreshEvent;
import com.iyuba.talkshow.newdata.RefreshRankEvent;
import com.iyuba.talkshow.newdata.RetrofitUtils;
import com.iyuba.talkshow.newdata.SPconfig;
import com.iyuba.talkshow.newdata.ShareUtils;
import com.iyuba.talkshow.newview.CustomDialog;
import com.iyuba.talkshow.newview.WaittingDialog;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.preview.PreviewActivity;
import com.iyuba.talkshow.ui.preview.PreviewInfoBean;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.TextAttr;
import com.iyuba.talkshow.util.TimeUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.talkshow.util.UploadStudyRecordUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by carl shen on 2020/7/31
 * New Primary English, new study experience.
 */
public class EvalFragment extends BaseFragment implements EvalMvpView, EvalAdapter.MixSound {
    public static final String TAG = "EvalFragment";
    @Inject
    public PreferencesHelper prefHelper;
    @Inject
    public EvalPresenter mPresenter;
    @Inject
    public EvalAdapter evalAdapter;
    FragmentEvalBinding binding;

    //    private Context mContext;
    private CustomDialog waitingDialog;
    private LoadingDialog mLoadingDialog;

    private Voa mVoa;
    private final List<VoaText> mVoaTextList = new ArrayList<>();

//    public ExtendedPlayer mixPlayer = null;
    private ExoPlayer mixPlayer = null;
    private IJKPlayer player;
    private String soundUrl;
    private int unitId = 0;

    private boolean isMix = false;
    private boolean isPrepared = false;
    private String mixUrl;
    private boolean isRePlay = false;
    private String shuoshuoId;
    private int totalScore = 0;
    private String StringUrls = "";
    private int sentenceSize = 0;
    private PreviewInfoBean previewInfoBean;
    private long mTimeStamp;
    private final Map<Integer, WavListItem> map = new HashMap<>();
    UploadStudyRecordUtil studyRecordUpdateUtil;
    private HandlerThread mHandlerThread;
    private Handler mSubHandler;

    //当前是否处于item的录音评测状态中
    private boolean isRecording = false;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (((StudyActivity) mContext).isDestroyed() && !EvalFragment.this.isAdded()) {
                return;
            }
            switch (msg.what) {
                case 0:
                    if ((mixPlayer != null) && (mixPlayer.isPlaying())) {
//                        binding.imvCurrentTime.setText(getTime(mixPlayer.getCurrentPosition()));
//                        binding.imvTotalTime.setText(getTime(mixPlayer.getDuration()));
//                        binding.imvSeekbarPlayer.setMax(mixPlayer.getDuration());
//                        binding.imvSeekbarPlayer.setProgress(mixPlayer.getCurrentPosition());
                        binding.imvCurrentTime.setText(getTime(mixPlayer.getCurrentPosition()));
                        binding.imvTotalTime.setText(getTime(mixPlayer.getDuration()));
                        binding.imvSeekbarPlayer.setMax((int) mixPlayer.getDuration());
                        binding.imvSeekbarPlayer.setProgress((int) mixPlayer.getCurrentPosition());
                        handler.sendEmptyMessageDelayed(0, 300L);
                    } else {
                        handler.removeMessages(0);
//                        Log.e(TAG, "handleMessage binding.tvReadMix.getText().toString() = " + binding.tvReadMix.getText().toString());
                        if ("停止".equals(binding.tvReadMix.getText().toString())) {
                            binding.tvReadMix.setText("重听");
                            curMixAudioState = MixAudioState.rePlay;
                        } else if ("重听".equals(binding.tvReadMix.getText().toString())) {
                            binding.tvReadMix.setText("重听");
                            curMixAudioState = MixAudioState.rePlay;
                        } else {
                            binding.tvReadMix.setText("试听");
                            curMixAudioState = MixAudioState.tryPlay;
                        }
                    }
                    break;
                case 1:
                    String addscore = String.valueOf(msg.arg1);
                    if (addscore.equals("5")) {
                        String mg = "语音成功发送至排行榜，恭喜您获得了" + addscore + "分";
                        ToastUtil.showToast(mContext, mg);
                    } else {
                        String mg = "语音成功发送至排行榜";
                        ToastUtil.showToast(mContext, mg);
                    }
                    if (ConfigData.openShare) {
                        binding.tvReadShare.setText("分享");
                    }
                    break;
                case 111:
                    stopMixPlayer();
                    break;
                case 122:
                    EventBus.getDefault().post(new StudyReportEvent(0, TypeLibrary.RefreshDataType.study_other));
                    break;
            }
        }
    };


    private static final String Article = "article";

    public static EvalFragment newInstance(Voa voa, int unit) {
        EvalFragment evalFragment = new EvalFragment();
        Bundle args = new Bundle();
        args.putParcelable(Article, voa);
        args.putInt(StudyActivity.UNIT, unit);
        evalFragment.setArguments(args);
        return evalFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
        mPresenter.attachView(this);
        mVoa = getArguments().getParcelable(Article);
        unitId = getArguments().getInt(StudyActivity.UNIT, 0);
        mTimeStamp = System.currentTimeMillis();
        startStudyRecord();

        //设置voaId数据
        SPconfig.Instance().putInt(Config.currVoaId, mVoa.voaId());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEvalBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
//        mContext = getActivity();
        previewInfoBean = new PreviewInfoBean();
        waitingDialog = WaittingDialog.showDialog(this.mContext);
        mLoadingDialog = new LoadingDialog(mContext);
        initData();
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
        mSubHandler = new Handler(mHandlerThread.getLooper());
        evalAdapter.SetSubHandler(mSubHandler);
        evalAdapter.setOnEvalListener(new EvalAdapter.OnEvalListener() {
            @Override
            public void onEvalRecord(boolean isRecordAndEval) {
                isRecording = isRecordAndEval;
            }

            @Override
            public void onMixStop() {
                //关闭合成音频播放
                stopMixPlay();
            }
        });
        initPlayer();
        initMixPlayer();
        initClick();
    }

    private void initClick() {
        binding.tvReadMix.setOnClickListener(v -> mixOrPlayUrl());
        binding.tvReadShare.setOnClickListener(v -> sendOrShare());
    }

    private void initData() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
//        evalAdapter = new EvalAdapter(mVoaTextList, this.mContext);
        evalAdapter.SetVoa(mVoa);
        evalAdapter.SetActivity(mActivity);
        evalAdapter.SetMainHandler(handler);
        binding.recyclerView.setAdapter(evalAdapter);
        evalAdapter.setMixSound(this);
    }

    private void initMixPlayer() {
        /*if (mixPlayer == null) {
            mixPlayer = new ExtendedPlayer(mContext);
        }
        mixPlayer.setOnPreparedListener(mp -> {
            isPrepared = true;
            binding.tvReadMix.setText("停止");
            curMixAudioState = MixAudioState.stopPlay;
            handler.sendEmptyMessage(0);
        });
        mixPlayer.setOnCompletionListener(mp -> {
            stopMixPlay();
        });*/

        if (mixPlayer==null){
            mixPlayer = new ExoPlayer.Builder(getActivity()).build();
            mixPlayer.setPlayWhenReady(false);
        }
        mixPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //准备
                        isPrepared = true;
                        binding.tvReadMix.setText("停止");
                        curMixAudioState = MixAudioState.stopPlay;
                        handler.sendEmptyMessage(0);

                        mixPlayer.play();
                        break;
                    case Player.STATE_ENDED:
                        //完成
                        stopMixPlay();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                //失败
                ToastUtil.show(getActivity(),"加载合成音频失败，请重试");
            }
        });

        binding.imvSeekbarPlayer.setMax(100);
        binding.imvSeekbarPlayer.setProgress(0);
        binding.imvSeekbarPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if ((fromUser) && (EvalFragment.this.mixPlayer != null) && (EvalFragment.this.mixPlayer.isPlaying())) {
                    mixPlayer.seekTo(progress);
                }
            }

            public void onStartTrackingTouch(SeekBar bar) {
            }

            public void onStopTrackingTouch(SeekBar bar) {

            }
        });
    }

    private void initPlayer() {
        mSubHandler.post(() -> {
            if (player == null) {
                IJKPlayer.initNative();
                player = new IJKPlayer();
            }
            boolean isAmerican = prefHelper.loadBoolean(Config.ISAMEICAN, true);

            if (isAmerican) {
                //美音
                soundUrl = Constant.Web.sound_vip + mVoa.voaId() / 1000 + "_" + mVoa.voaId() % 1000 + ".mp3";
            } else {
                //英音
                soundUrl = Constant.Web.sound_vip + "british/" + mVoa.voaId() / 1000 + "/" + mVoa.voaId() / 1000 + "_" + mVoa.voaId() % 1000 + ".mp3";
                if (mVoa.voaId() < 2000 && mVoa.voaId() % 2 == 0) {
                    //美音  第一册偶数课还是美音
                    soundUrl = Constant.Web.sound_vip + mVoa.voaId() / 1000 + "_" + mVoa.voaId() % 1000 + ".mp3";
                }
            }

            soundUrl = Constant.getSoundMp3Url(mVoa.sound(), mVoa.voaId());
            Log.e(TAG, "new  ------------- soundUrl " + soundUrl);
//            player.initialize(soundUrl);
            evalAdapter.setPlayer(player);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(mContext);
        mPresenter.getVoaTexts(mVoa.voaId());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);

        //停止音频和录音等操作
        StopPlayer();
    }

    public void StopPlayer() {
        if (evalAdapter != null) {
            evalAdapter.stopAllVoice(null);
        }
        if (mSubHandler != null) {
            mSubHandler.post(() -> {
                if ((player != null) && (player.isPlaying())) {
                    player.pause();
                }
            });
        }
        //设置录音状态
        isRecording = false;
        //关闭合成音频
        stopMixPlay();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /*if (mixPlayer != null) {
            mixPlayer.stopAndRelease();
            mixPlayer = null;
        }*/
        if (mixPlayer!=null){
            mixPlayer.stop();
            mixPlayer.release();
            mixPlayer = null;
        }
        if (player != null) {
            try {
                player.reset();
                IJKPlayer.endNative();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mSubHandler != null) {
            mSubHandler.removeCallbacks(null);
            mSubHandler = null;
        }
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
    }

    @Override
    public void reStart() {
        //评测后重置和好合成按钮
        binding.tvReadMix.setText("合成");
        curMixAudioState = MixAudioState.mix;
        binding.tvReadShare.setText("发布");
        curMixAudioState = MixAudioState.mix;
        binding.imvCurrentTime.setText("00:00");
        binding.imvTotalTime.setText("00:00");
        binding.tvReadSore.setVisibility(View.INVISIBLE);
        binding.imvSeekbarPlayer.setProgress(0);
        isMix = false;
    }

    public void refreshData() {
        initData();
        initPlayer();
        reStart();
        mPresenter.getVoaTexts(mVoa.voaId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(RefreshEvent event) {
        int voaId = SPconfig.Instance().loadInt(Config.currVoaId);
        if (Playmanager.getInstance().getVoaFromList(voaId) != null) {
            Log.e(TAG, "RefreshEvent  ----------");
            mVoa = Playmanager.getInstance().getVoaFromList(voaId);
            refreshData();
        }
    }

    public void stopMixPlayer() {
        if ((mixPlayer != null) && (mixPlayer.isPlaying())) {
            try {
                mixPlayer.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*private String getTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second, Locale.CHINA);
    }*/

    private String getTime(long time) {
        time /= 1000;
        long minute = time / 60;
        long second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second, Locale.CHINA);
    }

    //合成语音上发至服务器
    private void sendToRank() {
        if (!UserInfoManager.getInstance().isLogin()) {
            NewLoginUtil.startToLogin(getActivity());
            return;
        }
        waitingDialog.show();
        boolean isAmerican = prefHelper.loadBoolean(Config.ISAMEICAN, true);


        String currVoaId;
        if (isAmerican) {
            currVoaId = String.valueOf(mVoa.voaId());
        } else {
            currVoaId = String.valueOf(mVoa.voaId() * 10);
        }

        //增加奖励机制
        int rewardVersion = 1;

        AudioSendApi audioSendApi = RetrofitUtils.getInstance().getApiService(AudioSendApi.BASEURL, AudioSendApi.class);
        audioSendApi.audioSendApi(
                "UnicomApi",
                Constant.EVAL_TYPE, currVoaId,
                AudioSendApi.platform, AudioSendApi.format,
                AudioSendApi.protocol, String.valueOf(UserInfoManager.getInstance().getUserId()),
                TextAttr.encode(UserInfoManager.getInstance().getUserName()),
                currVoaId, totalScore / sentenceSize + "", "4", mixUrl, App.APP_ID, rewardVersion).enqueue(new Callback<EvaSendBean>() {
            @Override
            public void onResponse(Call<EvaSendBean> call, Response<EvaSendBean> response) {
                waitingDialog.dismiss();
                if ((response != null) && response.isSuccessful()) {
                    EvaSendBean evaSendBean = response.body();
                    shuoshuoId = evaSendBean.getShuoshuoId() + "";
                    String resultNew = evaSendBean.getResultCode();
                    if (resultNew.equals("501")) {

                        //这里从积分换成奖励
//                        Message msg = handler.obtainMessage();
//                        msg.what = 1;
//                        msg.arg1 = evaSendBean.getAddScore();
//                        handler.sendMessage(msg);

                        ToastUtil.showToast(mContext, "发布成功，请前往排行榜查看");

                        //显示积分
                        double price = Integer.parseInt(evaSendBean.getReward()) * 0.01;
                        if (price > 0) {
                            price = BigDecimalUtil.trans2Double(price);
                            String showMsg = String.format(ResUtil.getInstance().getString(R.string.reward_show), price);
                            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_dialog, showMsg));
                        }

                        if (ConfigData.openShare) {
                            binding.tvReadShare.setText("分享");
                        }

                        //这里刷新排行榜的数据显示
                        EventBus.getDefault().post(new RefreshRankEvent());
                    }
                } else {
                    ToastUtil.showToast(mContext, "发布失败");
                }
            }

            @Override
            public void onFailure(Call<EvaSendBean> call, Throwable t) {
                waitingDialog.dismiss();
                ToastUtil.showToast(mContext, "发布失败");
            }
        });
    }

    private boolean addToList() {
        totalScore = 0;
        StringUrls = "";
        sentenceSize = 0;
        List<VoaSoundNew> localArrayList = mPresenter.getVoaSoundVoaId(mVoa.voaId());
        for (int i = 0; i < localArrayList.size(); i++) {
            VoaSoundNew voaSound = localArrayList.get(i);
            Log.e(TAG, "localArrayList voaRecord.audio() = " + voaSound.sound_url());
            if (!TextUtils.isEmpty(voaSound.sound_url())) {
                totalScore += voaSound.totalscore();
                StringUrls = String.format("%s%s,", this.StringUrls, voaSound.sound_url());
                sentenceSize++;
            }
        }
        if (sentenceSize <= 1) {
            ToastUtil.showToast(this.mContext, "至少读两句方可合成");
            return false;
        }
        return true;
    }

    //    @OnClick(R.id.tv_read_mix)
    void mixOrPlayUrl() {
        if (isRecording) {
            ToastUtil.showToast(getActivity(), "正在录音评测中~");
            return;
        }

        MyIjkPlayer textPlayer;
        textPlayer = MyIjkPlayer.getInstance();
        if ((textPlayer != null) && (textPlayer.isPlaying())) {
            textPlayer.pause();
        }
        if (evalAdapter != null) {
            evalAdapter.stopAllVoice(null);
        }
        if ("合成".equals(binding.tvReadMix.getText().toString())) {
            if (addToList()) {
                if (UserInfoManager.getInstance().isLogin()) {
                    audioCompose(StringUrls);
                } else {
                    NewLoginUtil.startToLogin(getActivity());
                }
            }
        } else if ("试听".equals(binding.tvReadMix.getText().toString())) {
            if (mixPlayer == null) {
                return;
            }
            if (isRePlay) {
                isRePlay = false;

//                mSubHandler.post(() -> {
//                    mixPlayer.initialize(Constant.Web.EVAL_PREFIX + mixUrl);
//                    mixPlayer.prepareAndPlay();
//                });
                MediaItem mediaItem = MediaItem.fromUri(Constant.Web.EVAL_PREFIX + mixUrl);
                mixPlayer.setMediaItem(mediaItem);
                mixPlayer.prepare();
                return;
            }

            /*if (mixPlayer.isPausing()) {
                mixPlayer.start();
                handler.sendEmptyMessage(0);
                binding.tvReadMix.setText("停止");
                curMixAudioState = MixAudioState.stopPlay;
                return;
            }*/
            if (!mixPlayer.isPlaying()){
                mixPlayer.play();
                handler.sendEmptyMessage(0);
                binding.tvReadMix.setText("停止");
                curMixAudioState = MixAudioState.stopPlay;
                return;
            }

            if (!isPrepared) {
                isPrepared = true;

//                mSubHandler.post(() -> {
//                    mixPlayer.initialize(Constant.Web.EVAL_PREFIX + mixUrl);
//                    mixPlayer.prepareAndPlay();
//                });
                MediaItem mediaItem = MediaItem.fromUri(Constant.Web.EVAL_PREFIX + mixUrl);
                mixPlayer.setMediaItem(mediaItem);
                mixPlayer.prepare();
            } else {
                try {
//                    mixPlayer.start();
                    mixPlayer.play();
                    handler.sendEmptyMessage(0);
                    binding.tvReadMix.setText("停止");
                    curMixAudioState = MixAudioState.stopPlay;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if ("停止".equals(binding.tvReadMix.getText().toString())) {
            if (mixPlayer == null) {
                return;
            }
            if (mixPlayer.isPlaying()) {
//                mixPlayer.stopPlay();
                mixPlayer.stop();
                mixPlayer.seekTo(0);
            }
            //取消进度
            handler.removeMessages(0);
            //设置进度到0
            binding.imvSeekbarPlayer.setProgress(0);
            binding.imvCurrentTime.setText("00:00");
            binding.tvReadMix.setText("重听");
            curMixAudioState = MixAudioState.rePlay;
        } else if ("重听".equals(binding.tvReadMix.getText().toString())) {
            if (mixPlayer == null) {
                return;
            }

            //设置进度到0
            binding.imvSeekbarPlayer.setProgress(0);
            binding.imvCurrentTime.setText("00:00");
            //设置停止
            if (mixPlayer != null && mixPlayer.isPlaying()) {
                mixPlayer.seekTo(0);
//                mixPlayer.stopPlay();
                mixPlayer.stop();
            }
            handler.removeMessages(0);

            //重置播放
//            mSubHandler.post(() -> {
//                mixPlayer.initialize(Constant.Web.EVAL_PREFIX + mixUrl);
//                mixPlayer.prepareAndPlay();
//            });
            MediaItem mediaItem = MediaItem.fromUri(Constant.Web.EVAL_PREFIX + mixUrl);
            mixPlayer.setMediaItem(mediaItem);
            mixPlayer.prepare();

            binding.tvReadMix.setText("停止");
            curMixAudioState = MixAudioState.stopPlay;
        }
    }

    /**
     * 合成语音
     *
     * @param audios
     */
    private void audioCompose(String audios) {
        AudioComposeApi audioComposeApi = RetrofitUtils.getInstance().getApiService(AudioComposeApi.BASEURL, AudioComposeApi.class);
        audioComposeApi.audioComposeApi(AudioComposeApi.BASEURL, audios, Constant.EVAL_TYPE).enqueue(new Callback<EvaMixBean>() {

            @Override
            public void onResponse(Call<EvaMixBean> call, Response<EvaMixBean> response) {
                if (response.isSuccessful()) {
                    EvaMixBean evaMixBean = response.body();
                    String result = evaMixBean.getResult();
                    if ("1".equals(result)) {
                        //成功
                        mixUrl = evaMixBean.getURL();
                        binding.tvReadSore.setVisibility(View.VISIBLE);
                        binding.tvReadSore.setText(totalScore / sentenceSize + "");
                        binding.tvReadMix.setText("试听");
                        curMixAudioState = MixAudioState.tryPlay;
                        isMix = true;
                        isRePlay = true;
                        binding.imvSeekbarPlayer.setProgress(0);
                        if (prefHelper.loadBoolean(ConfigManager.Key.STUDY_REPORT, true)) {
                            EventBus.getDefault().post(new StudyReportEvent(2, TypeLibrary.RefreshDataType.study_other));

                            //这里因为担心时间太短，影响查看，因此去掉自动关闭
//                            handler.sendEmptyMessageDelayed(122, 5000);
                        }
                    } else {
                        ToastUtil.showToast(mContext, "合成失败，请稍后再试");
                    }
                } else {
                    Log.e(TAG, "audioCompose onResponse fail.");
                    ToastUtil.showToast(mContext, "合成失败，请稍后再试");
                }
            }

            @Override
            public void onFailure(Call<EvaMixBean> call, Throwable t) {
                if (t != null) {
                    Log.e(TAG, "audioCompose onFailure " + t.getMessage());
                }
                ToastUtil.showToast(mContext, "合成失败，请稍后再试");
            }
        });
    }

    //    @OnClick(R.id.tv_read_share)
    void sendOrShare() {
        if (isRecording) {
            ToastUtil.showToast(getActivity(), "正在录音评测中~");
            return;
        }

        if ("发布".equals(binding.tvReadShare.getText().toString())) {
            //中断播放
            if (mixPlayer.isPlaying()) {
//                mixPlayer.stopPlay();
                mixPlayer.stop();
                mixPlayer.seekTo(0);
            }
            //取消进度
            handler.removeMessages(0);
            //设置进度到0
            binding.imvSeekbarPlayer.setProgress(0);
            binding.imvCurrentTime.setText("00:00");

            if (!UserInfoManager.getInstance().isLogin()) {
                NewLoginUtil.startToLogin(getActivity());
                return;
            }

            if (!isMix) {
                ToastUtil.showToast(mContext, "请先合成后再发布");
                binding.tvReadMix.setText("合成");
                curMixAudioState = MixAudioState.mix;
                return;
            }

            binding.tvReadMix.setText("重听");
            curMixAudioState = MixAudioState.rePlay;

            sendToRank();
        } else {
            String userName = UserInfoManager.getInstance().getUserName();
            String content = ((StudyActivity) mContext).tvCeterTop.getText().toString();
            String siteUrl = "http://voa." + Constant.Web.WEB_SUFFIX + "voa/play.jsp?id=" + shuoshuoId
                    + "&addr=" + mixUrl + "&apptype=" + Constant.EVAL_TYPE;
            String imageUrl = App.Url.APP_ICON_URL;
            String title = userName + "在爱语吧评测中获得了" + (totalScore / sentenceSize) + "分";
            Log.e("EvalFragment", "sendOrShare " + title);
            ToastUtil.showToast(mContext, title);
            File file1 = StorageUtil.getAacMergeFile(mContext, mVoa.voaId(), mTimeStamp);
            Log.e("EvalFragment", "file1 " + file1.getAbsolutePath());
            File file = new File(file1.getAbsolutePath().replace("aac", "mp3"));
            Log.e("EvalFragment", "file======  " + file.getAbsolutePath());
            ShareUtils localShareUtils = new ShareUtils();
            localShareUtils.setMContext(mContext);
            localShareUtils.setVoaId(mVoa.voaId());
            if (ConfigData.openShare) {
                localShareUtils.showShare(mContext, shuoshuoId, imageUrl, siteUrl, title, content, localShareUtils.platformActionListener);
            } else {
                ToastUtil.showToast(mContext, "对不起，分享暂时不支持");
            }
        }
    }

    @Override
    public void showVoaTexts(List<VoaText> voaTextList) {
        mVoaTextList.clear();
        if (voaTextList == null || voaTextList.size() < 1) {
            Log.e("EvalFragment", "showVoaTexts is null.");
        } else {
            mVoaTextList.addAll(voaTextList);
            Log.e("EvalFragment", "showVoaTexts size = " + voaTextList.size());
        }
        evalAdapter.SetVoaList(voaTextList);
        evalAdapter.notifyDataSetChanged();
        studyRecordUpdateUtil.getStudyRecord().setWordCount(voaTextList);
        previewInfoBean.setVoaTexts(voaTextList);
        mPresenter.checkDraftExist(mTimeStamp);
    }

    @Override
    public void showEmptyTexts() {
        evalAdapter.SetVoaList(Collections.emptyList());
        evalAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void showMergeDialog() {
//        binding.loadingView.loadingTv.setText(getString(R.string.merging));
//        binding.loadingView.root.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissMergeDialog() {
//        binding.loadingView.root.setVisibility(View.GONE);
    }

    @Override
    public void startPreviewActivity() {
        stopStudyRecord("1");
        previewInfoBean.initIndexList();
        Record record = getDraftRecord();
        Intent intent = PreviewActivity.buildIntent(mVoaTextList, getActivity(), mVoa, previewInfoBean, record, mTimeStamp, false);
        startActivity(intent);
    }

    @Override
    public void showToast(int resId) {
        ToastUtil.show(getActivity(), getResources().getString(resId));
    }

    public void startStudyRecord() {
        studyRecordUpdateUtil = new UploadStudyRecordUtil(UserInfoManager.getInstance().isLogin(), TalkShowApplication.getInstance(), UserInfoManager.getInstance().getUserId(), mVoa.voaId(), "1", "2");
    }

    public void stopStudyRecord(String flag) {
        studyRecordUpdateUtil.stopStudyRecord(TalkShowApplication.getInstance(), UserInfoManager.getInstance().isLogin(), flag, mPresenter.getUploadStudyRecordService());
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

    @Override
    public void onDraftRecordExist(Record record) {
        map.putAll(MyIjkPlayer.buildScoreMap(mVoa, mTimeStamp, mVoaTextList, record));
        previewInfoBean.initIndexList(record, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewInfoBean.setVoaTextScore(mVoaTextList, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        evalAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    //停止播放合成音频
    private void stopMixPlay() {
        if (mixPlayer == null) {
            return;
        }
        if (mixPlayer.isPlaying()) {
//            mixPlayer.stopPlay();
            mixPlayer.stop();
            mixPlayer.seekTo(0);
        }
        //取消进度
        handler.removeMessages(0);
        //设置进度到0
        if (binding != null) {
            binding.imvSeekbarPlayer.setProgress(0);
            binding.imvCurrentTime.setText("00:00");

            //根据状态判断
            if (curMixAudioState == MixAudioState.mix) {
                binding.tvReadMix.setText("合成");
            } else if (curMixAudioState == MixAudioState.tryPlay) {
                binding.tvReadMix.setText("试听");
            } else if (curMixAudioState == MixAudioState.stopPlay) {
                binding.tvReadMix.setText("重听");
            } else if (curMixAudioState == MixAudioState.rePlay) {
                binding.tvReadMix.setText("重听");
            }
        }
    }

    //当前合成音频状态
    private int curMixAudioState = 0;

    //合成音频状态
    interface MixAudioState {
        int mix = 0;//合成
        int tryPlay = 1;//试听
        int stopPlay = 2;//停止
        int rePlay = 3;//重听
    }
}
