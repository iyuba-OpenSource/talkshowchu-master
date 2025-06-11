package com.iyuba.talkshow.newce.study.dubbingNew;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
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
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.data.model.WavListItem;
import com.iyuba.talkshow.data.model.WordResponse;
import com.iyuba.talkshow.data.model.result.SendEvaluateResponse;
import com.iyuba.talkshow.databinding.FragmentDubbingStudyBinding;
import com.iyuba.talkshow.event.DownloadEvent;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.event.LoginOutEvent;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.JuniorDubbingHelpEntity;
import com.iyuba.talkshow.lil.help_mvp.util.BigDecimalUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newdata.MediaRecordHelper;
import com.iyuba.talkshow.newdata.RetrofitUtils;
import com.iyuba.talkshow.newview.WordApi;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.detail.MyOnTouchListener;
import com.iyuba.talkshow.ui.dubbing.DubbingActivity;
import com.iyuba.talkshow.ui.dubbing.DubbingVideoControl;
import com.iyuba.talkshow.ui.main.drawer.Share;
import com.iyuba.talkshow.ui.preview.PreviewActivity;
import com.iyuba.talkshow.ui.preview.PreviewInfoBean;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.ScreenUtils;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.TimeUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.talkshow.util.UploadStudyRecordUtil;
import com.iyuba.talkshow.util.VoaMediaUtil;
import com.iyuba.talkshow.util.videoView.BaseVideoControl;
import com.iyuba.wordtest.db.WordOp;
import com.iyuba.wordtest.entity.WordEntity;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @desction: 学习-配音界面
 * @date: 2023/2/14 15:46
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class DubbingNewFragment extends BaseFragment implements DubbingNewMvpView {
    private static final String TAG = "DubbingFragment";

    private static final String VOA = "voa";
    private static final String showBack = "showBack";
    private Voa mVoa;

    //布局
    private FragmentDubbingStudyBinding binding;

    @Inject
    public DubbingNewPresenter presenter;
    @Inject
    public DubbingNewAdapter mAdapter;
    @Inject
    public ConfigManager configManager;

    private WordApi wordApi;
    private WordEntity wordEntity = null;
    private WordOp wordOp;

    //其他数据
    public long mDuration;
    private PreviewInfoBean previewInfoBean;
    private NewScoreCallback mNewScoreCallback;
    private boolean mIsFirstIn = true;
    private List<VoaText> mVoaTextList;//原文文本数据
    private long mTimeStamp;
    private Map<Integer, WavListItem> map = new HashMap<>();
    private String wordString;
    public boolean isStudyCallBack = false;//是否学习界面操作

    //工具
    private MediaPlayer mAccAudioPlayer;
    private ExtendedPlayer mRecordPlayer;
    private MediaPlayer wordPlayer;
    private MediaRecordHelper mediaRecordHelper = new MediaRecordHelper();

    private DubbingVideoControl mVideoControl;
    private UploadStudyRecordUtil studyRecordUpdateUtil;

    private TranslateAnimation animation;

    //针对下载操作进行处理
    //当前是否正在保存到相册
    private boolean isSaveVideoToAlbum = false;
    //是否正在下载所需资源
    private boolean isDownloading = false;

    //设置是否可以播放
    private boolean isCanPlay = true;

    public static DubbingNewFragment newInstance(Voa voa) {
        DubbingNewFragment fragment = new DubbingNewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(VOA, voa);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static DubbingNewFragment newInstance(Voa voa,boolean isShowBack) {
        DubbingNewFragment fragment = new DubbingNewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(VOA, voa);
        bundle.putBoolean(showBack,isShowBack);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        fragmentComponent().inject(this);
        presenter.attachView(this);
        mVoa = getArguments().getParcelable(VOA);
        mTimeStamp = System.currentTimeMillis();

        wordApi = RetrofitUtils.getInstance().getApiService(Constant.Web.WordBASEURL, "xml", WordApi.class);
        wordOp = new WordOp(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDubbingStudyBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        previewInfoBean = new PreviewInfoBean();
        List<VoaSoundNew> localArrayList = presenter.getVoaSoundVoaId(mVoa.voaId());
        if (localArrayList != null && localArrayList.size() > 0) {
            mTimeStamp = Long.parseLong(localArrayList.get(0).time());
            Log.e("DubbingAdapter", "mTimeStamp = " + mTimeStamp);
            for (VoaSoundNew voaSound : localArrayList) {
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
        presenter.init(mVoa);

        startStudyRecord();
        setVideoViewParams();
        initClick();
        initMedia();
        initAnimation();
        initRecyclerView();
        presenter.getVoaTexts(mVoa.voaId());

        checkPermission(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        VoaText operateVoaText = mAdapter.getOperateVoaText();
//        if (operateVoaText != null) {
//            binding.videoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoFile(getActivity(), mVoa.voaId())));
//            binding.videoView.seekTo(TimeUtil.secToMilliSec(operateVoaText.timing()));
//            mAccAudioPlayer.seekTo(TimeUtil.secToMilliSec(operateVoaText.timing()));
//            pauseVideoView();
//            mAccAudioPlayer.pause();
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (presenter != null) {
            presenter.detachView();
        }

        DubbingActivity.isSend = false;
    }

    private void initClick() {
        binding.loadingBtn.setOnClickListener(v -> {
            String showText = binding.loadingBtn.getText().toString();
            if (showText.equals("点击授权")) {
                requestPermission();
            } else if (showText.equals("点击登录")) {
                NewLoginUtil.startToLogin(getActivity());
            } else if (showText.equals("开通会员")) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), NewVipCenterActivity.class);
                intent.putExtra(NewVipCenterActivity.HUI_YUAN, NewVipCenterActivity.BENYINGYONG);
                startActivity(intent);
            } else if (showText.equals("点击下载")) {
                checkPermission(true);
            }
        });
        binding.previewDubbing.setOnClickListener(v -> {
            if (mAdapter.isRecording){
                ToastUtil.showToast(getActivity(),"正在录音评测中～");
                return;
            }

            List<VoaSoundNew> localArrayList = presenter.getVoaSoundVoaId(mVoa.voaId());
            if (localArrayList != null && localArrayList.size() > 0) {
                presenter.merge(mVoa.voaId(), mTimeStamp, mVoaTextList, (int) binding.videoView.getDuration());
            } else {
                Toast.makeText(getActivity().getApplicationContext(), R.string.not_dubbing, Toast.LENGTH_SHORT).show();
            }
        });
        binding.collect.setOnClickListener(v -> onAddClicked());
        binding.ivAudio.setOnClickListener(v -> onAudioClicked());
        binding.close.setOnClickListener(v -> onCloseClicked());
    }

    /******回调******/
    //登录回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event) {
        checkPermission(false);
    }

    //退出登录回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginOutEvent event) {
        checkPermission(false);
    }

    //下载文件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadFinish(DownloadEvent downloadEvent) {
        switch (downloadEvent.status) {
            case DownloadEvent.Status.FINISH:
                binding.downloadLayout.setVisibility(View.GONE);
                binding.dubbingLayout.setVisibility(View.VISIBLE);

                if (downloadEvent.downloadId == 1002){
                    isSaveVideoToAlbum = false;
                    ToastUtil.showToast(mContext, "保存相册完成");
                }else {
                    isDownloading = false;
                    if (isStudyCallBack){
                        ToastUtil.showToast(mContext, "下载完成");
                    }
                    setVideoAndAudio();
                }
                break;
            case DownloadEvent.Status.ERROR:
                if (downloadEvent.downloadId == 1002){
                    isSaveVideoToAlbum = false;

                    binding.downloadLayout.setVisibility(View.GONE);
                    showToast("保存相册出错，请重试");
                }else {
                    isDownloading = false;

                    binding.downloadLayout.setVisibility(View.VISIBLE);
                    binding.loadingProgress.setVisibility(View.INVISIBLE);
                    binding.loadingBtn.setVisibility(View.VISIBLE);
                    binding.loadingBtn.setText("点击下载");
                    binding.loadingMsg.setText("下载出错，请重试");
                }
                break;
            case DownloadEvent.Status.DOWNLOADING:
                binding.downloadLayout.setVisibility(View.VISIBLE);
                binding.loadingProgress.setVisibility(View.VISIBLE);
                binding.loadingMsg.setText(downloadEvent.msg);
                binding.loadingBtn.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
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

    /******权限******/
    public void checkPermission(boolean isDownload) {
        if (isSaveVideoToAlbum){
            ToastUtil.showToast(getActivity(),"正在保存到相册中，请稍后操作");
            return;
        }

        if (isDownloading){
            ToastUtil.showToast(getActivity(),"正在下载音视频资源，请稍后操作");
            return;
        }

        if (!PermissionX.isGranted(getActivity(), Manifest.permission.RECORD_AUDIO)
                || !PermissionX.isGranted(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            binding.loadingMsg.setText("配音功能需要 存储权限 和 录音权限 ,请全部授权后使用");
            binding.loadingBtn.setText("点击授权");
            return;
        }

        if (!UserInfoManager.getInstance().isLogin()) {
            binding.loadingMsg.setText("配音功能需要登录后使用，请点击登录");
            binding.loadingBtn.setText("点击登录");
            return;
        }

        if (!presenter.isTrial(mVoa) && !UserInfoManager.getInstance().isVip()) {
            binding.loadingMsg.setText("该内容为VIP课程，请开通VIP后使用");
            binding.loadingBtn.setText("开通会员");
            return;
        }

        if (!presenter.checkFileExist()&&!isDownload) {
            binding.loadingMsg.setText("首次使用需要下载音频和视频文件，时间较长，请确认是否下载所需文件~");
            binding.loadingBtn.setText("点击下载");
            return;
        }

        binding.loadingProgress.setVisibility(View.VISIBLE);
        binding.loadingBtn.setVisibility(View.INVISIBLE);
        checkVideoAndMedia();
    }

    //请求权限
    private void requestPermission() {
        PermissionX.init(this)
                .permissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                        if (allGranted) {
                            checkPermission(false);
                        } else {
                            ToastUtil.showToast(getActivity(), "请在设置界面手动授权该应用的 存储权限 和 录音权限 后使用");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:"+getActivity().getPackageName()));
                                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                        startActivityForResult(intent,1010);
                                    }catch (Exception e){

                                    }
                                }
                            },1000L);
                        }
                    }
                });
    }

    /******文件下载******/
    private void checkVideoAndMedia() {
        if (presenter.checkFileExist()) {
            onDownloadFinish(new DownloadEvent(DownloadEvent.Status.FINISH));
        } else {
            isDownloading = true;

            //下载音频视频
            binding.downloadLayout.setVisibility(View.VISIBLE);
            binding.loadingMsg.setText(getString(R.string.downloading));
            presenter.download();
        }
    }

    public boolean isAudioAndVideoExist() {
        return presenter.checkFileExist();
    }

    public boolean isAlbumVideoExist(){
        if (PermissionX.isGranted(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            if (presenter.checkAlbumVideoExist(getAlbumVideoName(mVoa))){
                return true;
            }
        }
        return false;
    }

    /******数据初始化******/
    private void setVideoAndAudio() {
        try {
            //音频播放操作
            if (mAccAudioPlayer != null) {
                mAccAudioPlayer.reset();
            }
            mAccAudioPlayer.setDataSource(StorageUtil.getAudioDubbingFile(getActivity(), mVoa.voaId()).getPath());

            //视频播放操作
            binding.videoView.setOnPreparedListener(() -> mDuration = binding.videoView.getDuration());
            binding.videoView.setOnErrorListener(e -> {
                new AlertDialog.Builder(getActivity())
                        .setTitle("视频错误")
                        .setMessage("视频文件加载错误，请重试")
                        .setPositiveButton("重新加载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setVideoAndAudio();
                            }
                        }).create().show();
                return true;
            });

            File videoFile = StorageUtil.getVideoDubbingFile(getActivity(), mVoa.voaId());
            if (!videoFile.exists()){
                new AlertDialog.Builder(getActivity())
                        .setTitle("文件异常")
                        .setMessage("未找到当前课程的视频文件，请重新下载后使用")
                        .setPositiveButton("重新下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.cancelDownload();
                                //重新下载文件
                                checkVideoAndMedia();
                            }
                        }).create().show();
                return;
            }

            //区分版本处理
            Uri fileUri = Uri.fromFile(videoFile);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                fileUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName()+".fileprovider",videoFile);
            }
            binding.videoView.setVideoURI(fileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMedia() {
        mAccAudioPlayer = new MediaPlayer();
        wordPlayer = new MediaPlayer();
        wordPlayer.setOnPreparedListener(mp -> wordPlayer.start());
        mRecordPlayer = new ExtendedPlayer(getActivity().getApplicationContext());
        MyOnTouchListener listener = new MyOnTouchListener(getActivity());
        listener.setSingleTapListener(mSingleTapListener);
        mVideoControl = new DubbingVideoControl(getActivity());
        mVideoControl.setMode(BaseVideoControl.Mode.SHOW_MANUAL);
        mVideoControl.setFullScreenBtnVisible(false);
        mVideoControl.setTopTitleLayoutVisible(false);
        boolean isShowBack = getArguments().getBoolean(showBack,false);
        if (isShowBack){
            mVideoControl.setTopTitleLayoutVisible(true);
            mVideoControl.setBackCallback(new BaseVideoControl.BackCallback() {
                @Override
                public void onBack() {
                    getActivity().finish();
                }
            });
        }
        mVideoControl.setButtonListener(new VideoControlsButtonListener() {
            @Override
            public boolean onPlayPauseClicked() {
                if (binding.videoView.isPlaying()) {
                    pause();
                } else {
                    mAdapter.repeatPlayVoaText(mAdapter.getOperateVoaText(), true);
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
        binding.videoView.setControls((VideoControlsCore) mVideoControl);
        binding.videoView.setOnCompletionListener(() -> {
            try {
                binding.videoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoDubbingFile(getActivity(), mVoa.voaId())));
                mAccAudioPlayer.reset();
                mAccAudioPlayer.setDataSource(StorageUtil.getAudioDubbingFile(getActivity(), mVoa.voaId()).getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        mVideoControl.setOnTouchListener(listener);
    }

    private void initRecyclerView() {
        mAdapter.setPageCallback(this);
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
        mAdapter.SetActivity(getActivity());

        binding.recyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(layoutManager);
    }

    /******视频******/
    DubbingNewAdapter.PlayVideoCallback mPlayVideoCallback = new DubbingNewAdapter.PlayVideoCallback() {
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

    DubbingNewAdapter.PlayRecordCallback mPlayRecordCallback = new DubbingNewAdapter.PlayRecordCallback() {
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
            return (int) mRecordPlayer.getDuration();
        }
    };

    DubbingNewAdapter.RecordingCallback mRecordingCallback = new DubbingNewAdapter.RecordingCallback() {
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
        public void lookWord(String word) {
            getNetworkInterpretation(word);
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

            presenter.uploadSentence(list.get(paraId - 1).sentence(), list.get(paraId - 1).idIndex(), list.get(paraId - 1).getVoaId(), paraId, Constant.EVAL_TYPE, UserInfoManager.getInstance().getUserId() + "", flacFile).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribeOn(Schedulers.io()).
                    subscribe(new Subscriber<SendEvaluateResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (getActivity().isDestroyed()) {
                                return;
                            }

                            mNewScoreCallback.onError(paraId, "评测服务暂时有问题，请稍后再试！");
                            if (e != null) {
                                Log.e("DubbingAdapter", "onError  " + e.getMessage());
                            }
                        }

                        @Override
                        public void onNext(SendEvaluateResponse s) {
                            if (getActivity().isDestroyed()) {
                                return;
                            }
                            int totalScore = (int) (Math.sqrt(Float.parseFloat(s.getData().getTotal_score()) * 2000));
                            mNewScoreCallback.onResult(paraId, totalScore, s.getData());
                            WavListItem item = new WavListItem();
                            item.setUrl(s.getData().getURL());
                            item.setBeginTime(mVoaTextList.get(paraId - 1).timing());
                            if (paraId < mVoaTextList.size()) {
                                item.setEndTime(mVoaTextList.get(paraId).timing());
                            } else {
                                item.setEndTime(mVoaTextList.get(paraId - 1).endTiming());
                            }
                            float duration = getAudioFileVoiceTime(flacFile.getAbsolutePath()) / 1000.0f;
                            String temp = String.format("%.1f", duration);
                            item.setDuration(Float.parseFloat(temp));
                            item.setIndex(paraId);
                            buildMap(paraId, item);
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
                            presenter.saveVoaSound(voaSound);

                            //同步保存在本地
                            long itemId = Long.parseLong(mVoa.voaId() + "" + paraId+""+idIndex);
                            double showScore = BigDecimalUtil.trans2Double(Float.parseFloat(s.getData().getTotal_score()));
                            JuniorDubbingHelpEntity entity = new JuniorDubbingHelpEntity(
                                    itemId,
                                    UserInfoManager.getInstance().getUserId(),
                                    (long) duration*1000L,
                                    s.getData().getSentence(),
                                    showScore,
                                    showScore*20,
                                    saveFile,
                                    s.getData().getURL(),
                                    GsonUtils.toJson(s.getData().getWords())
                            );
                            CommonDataManager.saveDataToDubbingHelp(entity);
                        }
                    });
        }
    };

    OnPreparedListener mOnPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            if (mIsFirstIn) {
                mIsFirstIn = false;
                mAdapter.repeatPlayVoaText(mVoaTextList.get(0), false);
            }
        }
    };

    interface NewScoreCallback {
        void onResult(int paraId, int score, SendEvaluateResponse.DataBean beans);

        void onError(int pos, String errorMessage);
    }

    void setNewScoreCallback(NewScoreCallback mNewScoreCallback) {
        this.mNewScoreCallback = mNewScoreCallback;
    }

    private void setVideoViewParams() {
        ViewGroup.LayoutParams lp = binding.videoView.getLayoutParams();
        int[] screenSize = ScreenUtils.getScreenSize(getActivity());
        lp.width = screenSize[0];
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = screenSize[1]; // 16 : 9
        } else {
            lp.height = (int) (lp.width * 0.5625);
        }
        binding.videoView.setLayoutParams(lp);
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

    private void pauseVideoView() {
        getActivity().runOnUiThread(() -> binding.videoView.pause());
        stopStudyRecord("0");
    }

    /******音频******/
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

    /******录音******/
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

            String playPath = null;
            File localFile = new File(voaText.pathLocal);
            if (localFile.exists()){
                playPath = localFile.getPath();
            }else {
                playPath = "http://userspeech."+Constant.Web.WEB_SUFFIX+"voa/"+voaText.evaluateBean.getURL();
            }

            mRecordPlayer.initialize(playPath);
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

    /******其他******/
    @Override
    public void showVoaTexts(List<VoaText> voaTextList) {
        mVoaTextList = voaTextList;
        binding.videoView.setOnPreparedListener(mOnPreparedListener);
        mAdapter.setList(voaTextList);
        mAdapter.notifyDataSetChanged();
        studyRecordUpdateUtil.getStudyRecord().setWordCount(voaTextList);
        previewInfoBean.setVoaTexts(voaTextList);
        presenter.checkDraftExist(mTimeStamp);
    }

    @Override
    public void showEmptyTexts() {
        mAdapter.setList(Collections.emptyList());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void dismissDubbingDialog() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMergeDialog() {
        binding.loadingMsg.setText(getString(R.string.merging));
        binding.downloadLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissMergeDialog() {
        binding.downloadLayout.setVisibility(View.GONE);
    }

    @Override
    public void startPreviewActivity() {
        stopStudyRecord("1");
        previewInfoBean.initIndexList();
        Record record = getDraftRecord();

        //用这个来判断是否来自已发布界面
        boolean isShowBtn = getArguments().getBoolean(showBack,false);
        Intent intent = PreviewActivity.buildIntent(mVoaTextList, getActivity(), mVoa, previewInfoBean, record, mTimeStamp, isShowBtn);
        startActivity(intent);
    }

    @Override
    public void showToast(int resId) {
        ToastUtil.show(TalkShowApplication.getContext(), getResources().getString(resId));
    }

    @Override
    public void showToast(String message) {
        ToastUtil.show(TalkShowApplication.getContext(), message);
    }

    @Override
    public void pause() {
        if (binding!=null){
            if (binding.videoView!=null&&binding.videoView.isPlaying()){
                pauseVideoView();
            }
        }

        if (mAccAudioPlayer!=null&&mAccAudioPlayer.isPlaying()) {
            mAccAudioPlayer.pause();
        }
        if (mRecordPlayer!=null&&mRecordPlayer.isPlaying()) {
            mRecordPlayer.pause();
        }
    }

    //关闭查词界面
    public void closeSearchWord(){
        if (binding!=null){
            if (binding.jiexiRoot!=null){
                binding.jiexiRoot.setVisibility(View.GONE);
            }
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

    @Override
    public void showWord(WordResponse bean) {
        showWordView(bean);
    }

    public void seekTo(int millSec) {
        binding.videoView.seekTo(millSec);
        mAccAudioPlayer.seekTo(millSec);
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

    public void startStudyRecord() {
        studyRecordUpdateUtil = new UploadStudyRecordUtil(UserInfoManager.getInstance().isLogin(),
                mContext, UserInfoManager.getInstance().getUserId(), mVoa.voaId(), "1", "2");
    }

    public void stopStudyRecord(String flag) {
        studyRecordUpdateUtil.stopStudyRecord(getActivity().getApplicationContext(), UserInfoManager.getInstance().isLogin(), flag, presenter.getUploadStudyRecordService());
    }

    private void buildMap(int index, WavListItem item) {
        map.put(index, item);
    }

    private void saveDraft() {
        Record record = getDraftRecord();
//        L.e("save draft ::  " + new Gson().toJson(record));
        presenter.saveRecord(record);
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
                .setFinishNum(presenter.getFinishNum(mVoa.voaId(), mTimeStamp))
                .setScore(previewInfoBean.getAllScore())
                .setAudio(previewInfoBean.getAllAudioUrl())
                .build();
    }

    public void buildScoreMap(Record record) {
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
        for (Map.Entry<Integer, String> i : map2.entrySet()) {
            for (Map.Entry<Integer, Integer> j : map.entrySet()) {
                if (i.getKey().equals(j.getKey())) {
                    WavListItem item = new WavListItem();
                    item.setUrl(i.getValue());
                    item.setBeginTime(mVoaTextList.get(i.getKey()).timing());
                    if (i.getKey() < mVoaTextList.size()) {
                        item.setEndTime(mVoaTextList.get(i.getKey() + 1).timing());
                    } else {
                        item.setEndTime(mVoaTextList.get(i.getKey()).endTiming());
                    }
                    File file = StorageUtil.getParaRecordAacFile(mContext, mVoa.voaId(), i.getKey() + 1, mTimeStamp);
                    float duration = getAudioFileVoiceTime(file.getAbsolutePath()) / 1000.0f;
                    @SuppressLint("DefaultLocale")
                    String temp = String.format("%.1f", duration);
                    item.setDuration(Float.parseFloat(temp));
                    item.setIndex(i.getKey() + 1);
                    buildMap(i.getKey(), item);
                }
            }
        }

    }

    private void showWordView(WordResponse bean) {
        pauseVideoView();
        binding.jiexiRoot.setVisibility(View.VISIBLE);
        wordString = bean.getKey();
        binding.jiexiRoot.startAnimation(animation);
        binding.jiexiRoot.setVisibility(View.VISIBLE);
        binding.word.setText(bean.getKey());
        binding.def.setText(bean.getDef());
        binding.pron.setText(String.format("[%s]", bean.getPron()));
    }

    public void onCloseClicked() {
        binding.jiexiRoot.setVisibility(View.GONE);
    }

    public void onAddClicked() {
        if (!UserInfoManager.getInstance().isLogin()) {
            ToastUtil.show(getActivity(), "请先登录");
            return;
        }
//        List<String> words = Collections.singletonList(wordString);
//        presenter.insertWords(UserInfoManager.getInstance().getUserId(), words);

        if (wordOp.isFavorWord(wordEntity.key,mVoa.voaId(),UserInfoManager.getInstance().getUserId())){
            wordApi.updateWord(String.valueOf(UserInfoManager.getInstance().getUserId()),"delete","Iyuba",wordEntity.key)
                    .enqueue(new Callback<WordEntity>() {
                        @Override
                        public void onResponse(Call<WordEntity> call, Response<WordEntity> response) {
                            wordOp.deleteWord(wordEntity.key, UserInfoManager.getInstance().getUserId());
                            ToastUtil.showToast(mContext, "单词取消收藏成功！");
                            binding.collect.setImageResource(R.drawable.ic_word_nocollect);
                        }

                        @Override
                        public void onFailure(Call<WordEntity> call, Throwable t) {

                        }
                    });
        }else {
            wordApi.updateWord(String.valueOf(UserInfoManager.getInstance().getUserId()),"insert","Iyuba",wordEntity.key)
                    .enqueue(new Callback<WordEntity>() {
                        @Override
                        public void onResponse(Call<WordEntity> call, Response<WordEntity> response) {
                            wordEntity.voa = mVoa.voaId();
                            wordEntity.book = mVoa.series();
                            if (!wordOp.isExsitsWord(wordEntity.key, UserInfoManager.getInstance().getUserId())) {
                                long result = wordOp.insertWord(wordEntity, UserInfoManager.getInstance().getUserId());
                                Log.e("DubbingAdapter", "updateWord insertWord result " + result);
                            }
                            ToastUtil.showToast(mContext, "单词收藏成功！");
                            binding.collect.setImageResource(R.drawable.ic_word_collected);
                        }

                        @Override
                        public void onFailure(Call<WordEntity> call, Throwable t) {
                            ToastUtil.showToast(mContext, "单词添加失败，请重试！");
                        }
                    });
        }
    }

    public void onAudioClicked() {
        try {
            wordPlayer.reset();
            String playurl = "http://dict.youdao.com/dictvoice?audio=";
            wordPlayer.setDataSource(playurl + wordString);
            wordPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initAnimation() {
        animation = new TranslateAnimation(-300, 0, 0, 0);
        animation.setDuration(500);
    }

    /******其他功能******/
    //分享
    public void onShareClick() {
        if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
            ToastUtil.showToast(mContext, "分享功能需要打开网络数据连接");
            return;
        }

//        pauseVideoPlayer("0");
        if (ConfigData.openShare) {
            Share.prepareVideoMessage(getActivity(), configManager.getKouId(), mVoa, presenter.getInregralService(), UserInfoManager.getInstance().getUserId());
        } else {
            ToastUtil.showToast(getActivity(), "对不起，分享暂时不支持");
        }
    }

    //保存到相册
    public void saveVideoToAlbum() {
        if (!UserInfoManager.getInstance().isLogin()) {
            NewLoginUtil.startToLogin(getActivity());
            return;
        }

        if (isSaveVideoToAlbum){
            ToastUtil.showToast(getActivity(),"正在保存到相册中，请稍后操作");
            return;
        }

        if (isDownloading){
            ToastUtil.showToast(getActivity(),"正在下载音视频资源，请稍后操作");
            return;
        }

        if (!UserInfoManager.getInstance().isVip()) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("保存视频到相册需要VIP会员权限，是否确认开通VIP会员？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setClass(mContext, NewVipCenterActivity.class);
                            intent.putExtra(NewVipCenterActivity.HUI_YUAN, NewVipCenterActivity.BENYINGYONG);
                            startActivity(intent);
                        }
                    }).show();
            return;
        }

        if (!PermissionX.isGranted(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionX.init(this).permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request((granted, strings, strings2) -> {
                        if (granted) {
                            downloadAndSaveVideo();
                        } else {
                            Toast.makeText(getActivity(), "请授予必要的权限", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            downloadAndSaveVideo();
        }
    }

    private void downloadAndSaveVideo() {
        if (!NetStateUtil.isConnected(mContext)) {
            showToast("网络异常");
            return;
        }

        isSaveVideoToAlbum = true;

        //下载视频
        binding.downloadLayout.setVisibility(View.VISIBLE);
        binding.loadingProgress.setVisibility(View.VISIBLE);
        binding.loadingMsg.setText("正在保存到相册");
        String videoUrl = VoaMediaUtil.getVideoUrl(mVoa.video());
        if (UserInfoManager.getInstance().isVip()) {
            videoUrl = VoaMediaUtil.getVideoVipUrl(mVoa.video());
        }
        String fileName = getAlbumVideoName(mVoa);
        String localPath = getActivity().getExternalFilesDir(null).getPath() + File.separator + fileName;
        presenter.downVideoAndImportAlbum(videoUrl, localPath);
        //这里以1002作为数据显示，出现1002则表示为保存视频到相册
    }

    //获取当前下载视频的名称
    private String getAlbumVideoName(Voa curVoa){
        return curVoa.category()+"_"+curVoa.voaId()+Constant.Voa.MP4_SUFFIX;
    }

    //更新原文
    public void refresh() {
        presenter.getVoaSeries(configManager.getKouId() + "");
    }

    //查询单词信息
    public void getNetworkInterpretation(String word){
        wordString = word;
        wordEntity = wordOp.findWordEntity(wordString,UserInfoManager.getInstance().getUserId());
        if ((wordEntity != null) && !TextUtils.isEmpty(wordEntity.key)) {
            wordEntity.voa = mVoa.voaId();
            wordEntity.book = mVoa.series();
            wordEntity.unit = 0;
            showWordEntity(wordEntity);
        } else if (!NetStateUtil.isConnected(mContext)) {
            showToastShort("查询单词需要开启数据网络");
        } else {
            wordApi.getWordApi(wordString).enqueue(new Callback<WordEntity>() {
                @Override
                public void onResponse(Call<WordEntity> call, Response<WordEntity> response) {
                    if ((response != null) && (response.body() != null) && !TextUtils.isEmpty(response.body().key)) {
                        wordEntity = response.body();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWordEntity(wordEntity);
                            }
                        });

                        //查出来数据不要直接插入数据库，因为根本没有收藏，收藏之后在保存
                        Log.e("DubbingAdapter", "getWordApi onResponse wordEntity.key = " + wordEntity.key);
//                        wordEntity.voa = mVoa.voaId();
//                        wordEntity.book = mVoa.series();
//                        wordEntity.unit = 0;
//                        if (!wordOp.isExsitsWord(wordEntity.key, mUserInfoManager.getInstance().getUserId())) {
//                            long result = wordOp.insertWord(wordEntity, mUserInfoManager.getInstance().getUserId());
//                            Log.e("DubbingAdapter", "getWordApi onResponse insertWord result " + result);
//                        }
                        return;
                    } else {
                        Log.e("DubbingAdapter", "getWordApi onResponse is null? ");
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToastShort("暂时没有查到这个单词的解释");
                        }
                    });
                }

                @Override
                public void onFailure(Call<WordEntity> call, Throwable t) {
                    if (t != null) {
                        Log.e("DubbingAdapter", "getWordApi onFailure =  " + t.getMessage());
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToastShort("暂时没有查到这个单词的解释");
                        }
                    });
                }
            });
        }
    }

    private void showWordEntity(WordEntity bean) {
        pauseVideoView();
        wordString = bean.key ;
        binding.jiexiRoot.startAnimation(animation);
        binding.jiexiRoot.setVisibility(View.VISIBLE);
        binding.word.setText(bean.key);
        binding.def.setText(bean.def);
        if (TextUtils.isEmpty(bean.pron)) {
            binding.pron.setText("");
        } else {
            binding.pron.setText(String.format("[%s]",bean.pron));
        }
        if (TextUtils.isEmpty(bean.audio)) {
            binding.ivAudio.setVisibility(View.INVISIBLE);
        } else {
            binding.ivAudio.setVisibility(View.VISIBLE);
        }
        if (UserInfoManager.getInstance().isLogin()) {
            if (wordOp.isFavorWord(wordString, mVoa.voaId(), UserInfoManager.getInstance().getUserId())) {
//                binding.dialogBtnAddword.setText("取消收藏");
                binding.collect.setImageResource(R.drawable.ic_word_collected);
            } else {
//                binding.dialogBtnAddword.setText("添加收藏");
                binding.collect.setImageResource(R.drawable.ic_word_nocollect);
            }
        } else {
//            binding.dialogBtnAddword.setText("添加收藏");
            binding.collect.setImageResource(R.drawable.ic_word_nocollect);
        }
    }

    //设置播放标志
    public void setCanPlay(boolean isPlay){
        this.isCanPlay = isPlay;
        //切换配音的标志
        this.isStudyCallBack = false;

        if (!isCanPlay){
            pause();
            closeSearchWord();

            //停止录音和音频播放
//            if (mAdapter!=null){
//                mAdapter.stopRecordAndPlay();
//            }
        }
    }
}
