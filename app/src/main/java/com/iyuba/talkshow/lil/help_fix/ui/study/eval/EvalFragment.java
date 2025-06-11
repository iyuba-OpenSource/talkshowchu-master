package com.iyuba.talkshow.lil.help_fix.ui.study.eval;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.FragmentEvalBinding;
import com.iyuba.talkshow.lil.help_fix.data.bean.BookChapterBean;
import com.iyuba.talkshow.lil.help_fix.data.bean.ChapterDetailBean;
import com.iyuba.talkshow.lil.help_fix.data.bean.EvalChapterBean;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.UrlLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.NetHostManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Publish_eval;
import com.iyuba.talkshow.lil.help_fix.util.FileManager;
import com.iyuba.talkshow.lil.help_fix.util.FixUtil;
import com.iyuba.talkshow.lil.help_fix.util.PermissionUtil;
import com.iyuba.talkshow.lil.help_fix.util.ShareUtil;
import com.iyuba.talkshow.lil.help_fix.view.dialog.LoadingDialog;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingFragment;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxTimer;
import com.iyuba.talkshow.lil.help_mvp.util.xxpermission.PermissionBackListener;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.wordtest.utils.PermissionDialogUtil;
import com.iyuba.wordtest.utils.RecordManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @title: 评测界面
 * @date: 2023/5/23 23:30
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class EvalFragment extends BaseViewBindingFragment<FragmentEvalBinding> implements EvalView {

    //类型
    private String types;
    //voaId
    private String voaId;

    private BookChapterBean chapterBean;
    private EvalAdapter evalAdapter;
    private EvalPresenter presenter;

    //是否正在录音
    private boolean isRecord = false;
    //加载弹窗
    private LoadingDialog loadingDialog;

    //原文播放器
    private ExoPlayer readPlayer;
    //原文播放链接
    private String readPlayUrl = null;
    //原文播放标识位
    private String readPlayTag = "readPlayTag";
    //原文播放是否可用
    private boolean isCanPlay = false;

    //评测播放器
    private ExoPlayer evalPlayer;
    //评测播放标识位
    private String evalPlayTag = "evalPlayTag";

    //录音器
    private RecordManager recordManager;
    //录音的标识位
    private String recordTag = "recordTag";

    //合成播放器
    private ExoPlayer margePlayer;
    //合成播放链接
    private String margePlayUrl = null;
    //合成音频播放标识位
    private String margePlayTag = "margePlayTag";
    //合成音频是否可用
    private boolean isCanMargePlay = false;

    public static EvalFragment getInstance(String types, String voaId) {
        EvalFragment fragment = new EvalFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types, types);
        bundle.putString(StrLibrary.voaid, voaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        types = getArguments().getString(StrLibrary.types);
        voaId = getArguments().getString(StrLibrary.voaid);

        presenter = new EvalPresenter();
        presenter.attachView(this);

        chapterBean = presenter.getChapterData(types, voaId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        initPlayer();
        initBottom();
        initClick();

        refreshData();
    }

    @Override
    public void onPause() {
        super.onPause();

        pauseReadPlay();
        pauseEvalPlay();
        stopMargePlay();
        stopRecord();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        stopLoading();

        presenter.detachView();
    }

    /*********************初始化********************/
    private void initList() {
        evalAdapter = new EvalAdapter(getActivity(), new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(evalAdapter);
        evalAdapter.setOnEvalCallBackListener(new EvalAdapter.OnEvalCallBackListener() {
            @Override
            public void switchItem(int nextPosition) {
                if (isRecord) {
                    ToastUtil.showToast(getActivity(), "正在录音中～");
                    return;
                }

                stopMargePlay();
                pauseReadPlay();
                pauseEvalPlay();

                evalAdapter.refreshIndex(nextPosition);
            }

            @Override
            public void onPlayRead(long startTime, long endTime) {
                if (isRecord) {
                    ToastUtil.showToast(getActivity(), "正在录音中～");
                    return;
                }

                pauseEvalPlay();
                stopMargePlay();

                if (readPlayer != null) {
                    if (readPlayer.isPlaying()) {
                        pauseReadPlay();
                    } else {
                        startReadPlay(startTime, endTime);
                    }
                } else {
                    startReadPlay(startTime, endTime);
                }
            }

            @Override
            public void onRecord(long time, String types, String voaId, String paraId, String idIndex, String sentence) {
                List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
                pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO,new Pair<>("麦克风权限","录制评测时朗读的音频，用于评测打分使用")));
                pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","保存评测的音频文件，用于评测打分使用")));

                PermissionDialogUtil.getInstance().showMsgDialog(getActivity(), pairList, new PermissionDialogUtil.OnPermissionResultListener() {
                    @Override
                    public void onGranted(boolean isSuccess) {
                        if (isSuccess){
                            checkRecord(time, types, voaId, paraId, idIndex, sentence);
                        }
                    }
                });
            }

            @Override
            public void onPlayEval(String playUrl, String playPath) {
                if (isRecord) {
                    ToastUtil.showToast(getActivity(), "正在录音中～");
                    return;
                }

                pauseReadPlay();
                stopMargePlay();

                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后播放音频～");
                    return;
                }

                if (evalPlayer != null) {
                    if (evalPlayer.isPlaying()) {
                        pauseEvalPlay();
                    } else {
                        startEvalPlay(playUrl, playPath);
                    }
                } else {
                    startEvalPlay(playUrl, playPath);
                }
            }

            @Override
            public void onPublish(String types, String voaId, String paraId, String idIndex, int score, String evalUrl) {
                if (isRecord) {
                    ToastUtil.showToast(getActivity(), "正在录音中～");
                    return;
                }

                pauseReadPlay();
                pauseEvalPlay();
                stopMargePlay();

                if (!NetworkUtil.isConnected(getActivity())) {
                    ToastUtil.showToast(getActivity(), "请链接网络后重试~");
                    return;
                }

                startLoading("正在发布到排行榜～");
                presenter.publishSingleEval(types, voaId, idIndex, paraId, score, evalUrl);
            }

            @Override
            public void onShare(String sentence,int totalScore, String audioUrl, String shareUrl) {
                if (isRecord) {
                    ToastUtil.showToast(getActivity(), "正在录音中～");
                    return;
                }

                pauseReadPlay();
                pauseEvalPlay();
                stopMargePlay();

                shareSingleEval(sentence, totalScore, shareUrl, audioUrl);
            }
        });
    }

    private void initPlayer() {
        //获取当前的原文播放链接
        BookChapterBean chapterBean = presenter.getChapterData(types, voaId);
        if (chapterBean != null) {
            readPlayUrl = chapterBean.getAudioUrl();
        }

        //原文播放
        readPlayer = new ExoPlayer.Builder(getActivity()).build();
        MediaItem mediaItem = MediaItem.fromUri(readPlayUrl);
        readPlayer.setMediaItem(mediaItem);
        readPlayer.prepare();
        readPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        //可用
                        isCanPlay = true;
                        break;
                    case Player.STATE_ENDED:
                        //完成
                        pauseReadPlay();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                if (!isResumed()){
                    return;
                }

                isCanPlay = false;
                ToastUtil.showToast(getActivity(), "播放原文音频出错～");
            }
        });

        //评测播放
        evalPlayer = new ExoPlayer.Builder(getActivity()).build();
        evalPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        //准备
                        if (!getActivity().isDestroyed()) {
                            evalPlayer.play();
                        }
                        break;
                    case Player.STATE_ENDED:
                        //完成
                        pauseEvalPlay();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                pauseEvalPlay();
                ToastUtil.showToast(getActivity(), "播放评测音频出错～");
            }
        });

        //合成播放
        margePlayer = new ExoPlayer.Builder(getActivity()).build();
        margePlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        //准备
                        isCanMargePlay = true;
                        binding.tvReadShare.setVisibility(View.VISIBLE);
                        binding.imvTotalTime.setText(showTime(margePlayer.getDuration()));
                        break;
                    case Player.STATE_ENDED:
                        //完成
                        stopMargePlay();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(getActivity(), "合成音频播放失败～");
                stopMargePlay();
            }
        });
    }

    private void initBottom() {
        binding.tvReadShare.setVisibility(View.INVISIBLE);
        binding.imvCurrentTime.setText(showTime(0));
    }

    private void initClick() {
        binding.imvSeekbarPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseMargePlay();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isRecord) {
                    pauseMargePlay();
                    return;
                }

                startMargePlay(seekBar.getProgress());
            }
        });
        binding.tvReadMix.setOnClickListener(v -> {
            if (isRecord) {
                ToastUtil.showToast(getActivity(), "正在录音中～");
                return;
            }

            pauseReadPlay();
            pauseEvalPlay();
            pauseMargePlay();

            String showText = binding.tvReadMix.getText().toString();
            if (showText.equals("合成")) {
                if (!NetworkUtil.isConnected(getActivity())) {
                    ToastUtil.showToast(getActivity(), "请链接网络后重试~");
                    return;
                }

                if (!presenter.isCanMargeAudio(types,voaId)){
                    ToastUtil.showToast(getActivity(),"请至少评测两句后再合成音频");
                    return;
                }

                startLoading("正在合成音频");
                presenter.margeAudio(types, voaId);
            } else if (showText.equals("试听")) {
                startMargePlay(0);
            } else if (showText.equals("停止")) {
                stopMargePlay();
            }
        });
        binding.tvReadShare.setOnClickListener(v -> {
            if (isRecord) {
                ToastUtil.showToast(getActivity(), "正在录音中～");
                return;
            }

            pauseReadPlay();
            pauseEvalPlay();
            pauseMargePlay();

            if (TextUtils.isEmpty(margePlayUrl)) {
                ToastUtil.showToast(getActivity(), "请合成音频后发布～");
                return;
            }

            if (!NetworkUtil.isConnected(getActivity())) {
                ToastUtil.showToast(getActivity(), "请链接网络后重试~");
                return;
            }

            startLoading("正在发布到排行榜～");
            //取出模版数据
            String prefix = UrlLibrary.HTTP_USERSPEECH + NetHostManager.getInstance().getDomainShort() + "/voa/";
            String publishAudioUrl = margePlayUrl;
            if (publishAudioUrl.startsWith(prefix)) {
                publishAudioUrl = publishAudioUrl.replace(prefix, "");
            }
            presenter.publishMargeAudio(types, voaId, publishAudioUrl);
        });
    }

    /*******************刷新数据********************/
    private void refreshData() {
        List<ChapterDetailBean> list = presenter.getChapterDetail(types, voaId);
        if (list != null && list.size() > 0) {
            binding.noData.getRoot().setVisibility(View.GONE);
            evalAdapter.refreshData(list);
        } else {
            binding.noData.getRoot().setVisibility(View.VISIBLE);
            binding.noData.msgNoData.setText("暂无该章节数据");
        }
    }

    /********************回调数据******************/
    @Override
    public void showSingleEval(EvalChapterBean bean) {
        stopLoading();

        if (bean != null) {
            binding.tvReadMix.setText("合成");
            binding.imvTotalTime.setText(showTime(0));
            margePlayUrl = null;
            isCanMargePlay = false;
            binding.tvReadSore.setVisibility(View.INVISIBLE);
            binding.tvReadShare.setVisibility(View.INVISIBLE);

            evalAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showToast(getActivity(), "录音评测失败，请重试～");
        }
    }

    @Override
    public void showMargeAudio(String margeAudioUrl) {
        stopLoading();

        if (!TextUtils.isEmpty(margeAudioUrl)) {
            ToastUtil.showToast(getActivity(), "合成音频成功～");
            margePlayUrl = fixMargeAudioUrl(margeAudioUrl);
            binding.tvReadSore.setVisibility(View.VISIBLE);
            binding.tvReadSore.setText(String.valueOf(presenter.getMargeAudioScore(types, voaId)));
            initMargePlay();
            pauseMargePlay();
        } else {
            ToastUtil.showToast(getActivity(), "合成音频失败～");
        }
    }

    @Override
    public void showPublishRank(boolean isSingle, Publish_eval bean) {
        stopLoading();

        if (bean != null) {
            ToastUtil.showToast(getActivity(), "发布到排行榜成功，请前往排行界面查看");

            if (isSingle) {
                evalAdapter.refreshShare(String.valueOf(bean.getShuoshuoId()));
            }
            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.eval_rank));
        } else {
            ToastUtil.showToast(getActivity(), "发布到排行榜失败，请重试～");
        }
    }

    /******************原文*****************/
    private void startReadPlay(long startTime, long endTime) {
        if (TextUtils.isEmpty(readPlayUrl)) {
            ToastUtil.showToast(getActivity(), "未获取到该音频的文件～");
            return;
        }

        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"请链接网络后播放音频～");
            return;
        }

        if (readPlayer == null) {
            readPlayer = new ExoPlayer.Builder(getActivity()).build();
            MediaItem mediaItem = MediaItem.fromUri(readPlayUrl);
            readPlayer.setMediaItem(mediaItem);
            readPlayer.prepare();
        }

        if (!isCanPlay) {
            ToastUtil.showToast(getActivity(), "原文音频正在加载中～");
            return;
        }

        readPlayer.seekTo(startTime);
        readPlayer.play();

        RxTimer.getInstance().multiTimerInMain(readPlayTag, 0, 200L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                //刷新ui
                long totalPlayTime = endTime - startTime;
                long curPlayTime = readPlayer.getCurrentPosition();
                long playedTime = curPlayTime - startTime;

                Log.d("播放进度", playedTime+"----"+totalPlayTime);
                evalAdapter.refreshReadPlay(true, playedTime, totalPlayTime);

                if (curPlayTime >= endTime) {
                    pauseReadPlay();
                }
            }
        });
    }

    private void pauseReadPlay() {
        if (readPlayer != null && readPlayer.isPlaying()) {
            readPlayer.pause();
        }

        evalAdapter.refreshReadPlay(false, 0, 0);
        RxTimer.getInstance().cancelTimer(readPlayTag);
    }

    /******************录音*****************/
    private void checkRecord(long time, String types, String voaId, String paraId, String idIndex, String sentence) {
        pauseReadPlay();
        pauseEvalPlay();
        stopMargePlay();

        //登录判断
        if (!UserInfoManager.getInstance().isLogin()) {
            showAbilityDialog(true, "录音评测");
            return;
        }

        //会员和限制判断
        if (!presenter.isEvalNext(types, voaId, paraId, idIndex)) {
            showAbilityDialog(false, "录音评测");
            return;
        }

        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"请链接网络后重试～");
            return;
        }

        //权限判断
        PermissionUtil.requestRecordAudio(getActivity(), new PermissionBackListener() {
            @Override
            public void allGranted() {

                if (isRecord) {
                    stopRecord();

                    startLoading("正在进行录音评测~");
                    String recordPath = FileManager.getInstance().getCourseEvalAudioPath(types, voaId, paraId, idIndex, UserInfoManager.getInstance().getUserId());
                    presenter.submitSingleEval(types, true, voaId, paraId, idIndex, sentence, recordPath);
                } else {
                    startRecord(time, types, voaId, paraId, idIndex,sentence);
                }
            }

            @Override
            public void allDenied() {
                ToastUtil.showToast(getActivity(), "当前功能需要授权后使用～");
            }

            @Override
            public void halfPart(List<String> grantedList, List<String> deniedList) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("授权禁止")
                        .setMessage("当前功能所需的部分权限被禁止，请授权后使用")
                        .setPositiveButton("确定", null)
                        .show();
            }

            @Override
            public void warnRequest() {
                new AlertDialog.Builder(getActivity())
                        .setTitle("授权禁止")
                        .setMessage("当前功能所需的 存储权限 和 录音权限 权限被禁止，请手动授权后使用")
                        .setPositiveButton("暂不使用", null)
                        .setNegativeButton("前往授权", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PermissionUtil.jumpToSetting(getActivity());
                            }
                        }).show();
            }
        });
    }

    private void startRecord(long time, String types, String voaId, String paraId, String idIndex,String sentence) {
        try {
            String recordPath = FileManager.getInstance().getCourseEvalAudioPath(types, voaId, paraId, idIndex, UserInfoManager.getInstance().getUserId());
            boolean isCreate = FileManager.getInstance().createEmptyFile(recordPath);
            if (!isCreate) {
                ToastUtil.showToast(getActivity(), "录音文件出现问题，请重试~");
                return;
            }

            isRecord = true;
            recordManager = new RecordManager(new File(recordPath));
            recordManager.startRecord();

            //时间延长3s
            long recordTime = time + 3000L;
            RxTimer.getInstance().multiTimerInMain(recordTag, 0, 100L, new RxTimer.RxActionListener() {
                @Override
                public void onAction(long number) {
                    long curTime = number * 100L;
                    evalAdapter.refreshRecord(true, (int) recordManager.getVolume());

                    Log.d("录音评测", "onAction: --" + recordTime + "--" + curTime);

                    if (curTime >= recordTime) {
                        stopRecord();
                        startLoading("正在进行录音评测~");
                        String recordPath = FileManager.getInstance().getCourseEvalAudioPath(types, voaId, paraId, idIndex, UserInfoManager.getInstance().getUserId());
                        presenter.submitSingleEval(types, true, voaId, paraId, idIndex, sentence, recordPath);
                    }
                }
            });
        } catch (Exception e) {
            ToastUtil.showToast(getActivity(), "录音出现问题，请重试~");
        }
    }

    private void stopRecord() {
        if (recordManager != null && isRecord) {
            recordManager.stopRecord();
        }

        Log.d("录音评测", "onAction: --完成");
        isRecord = false;
        RxTimer.getInstance().cancelTimer(recordTag);
        evalAdapter.refreshRecord(false, 0);
    }

    /******************评测播放**************/
    private void startEvalPlay(String audioUrl, String audioPath) {
        if (TextUtils.isEmpty(audioUrl)) {
            ToastUtil.showToast(getActivity(), "未获取到该评测的音频文件～");
            return;
        }

        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"请链接网络后播放音频～");
            return;
        }

        if (evalPlayer == null) {
            evalPlayer = new ExoPlayer.Builder(getActivity()).build();
        }
        MediaItem mediaItem = MediaItem.fromUri(audioUrl);
        evalPlayer.setMediaItem(mediaItem);
        evalPlayer.prepare();

        RxTimer.getInstance().multiTimerInMain(readPlayTag, 0, 200L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                //刷新ui
                long totalPlayTime = evalPlayer.getDuration();
                long curPlayTime = evalPlayer.getCurrentPosition();
                evalAdapter.refreshEvalPlay(true, curPlayTime, totalPlayTime);

                if (curPlayTime >= totalPlayTime) {
                    pauseEvalPlay();
                }
            }
        });
    }

    private void pauseEvalPlay() {
        if (evalPlayer != null && evalPlayer.isPlaying()) {
            evalPlayer.pause();
        }

        evalAdapter.refreshEvalPlay(false, 0, 0);
        RxTimer.getInstance().cancelTimer(evalPlayTag);
    }

    /*******************合成播放**************/
    private void initMargePlay() {
        if (TextUtils.isEmpty(margePlayUrl)) {
            ToastUtil.showToast(getActivity(), "合成的音频链接不可用");
            return;
        }

        MediaItem mediaItem = MediaItem.fromUri(margePlayUrl);
        margePlayer.setMediaItem(mediaItem);
        margePlayer.setPlayWhenReady(false);
        margePlayer.prepare();

        binding.tvReadMix.setText("试听");
    }

    private void startMargePlay(long progress) {
        if (TextUtils.isEmpty(margePlayUrl)) {
            ToastUtil.showToast(getActivity(), "请合成音频后播放~");
            return;
        }

        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"请链接网络后播放音频～");
            return;
        }

        if (!isCanMargePlay) {
            ToastUtil.showToast(getActivity(), "合成音频未初始化");
            return;
        }

        if (margePlayer != null) {
            margePlayer.seekTo(progress);
            margePlayer.play();
        }

        RxTimer.getInstance().multiTimerInMain(margePlayTag, 0, 200L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                long max = margePlayer.getDuration();
                long progress = margePlayer.getCurrentPosition();

                binding.imvSeekbarPlayer.setMax((int) max);
                binding.imvSeekbarPlayer.setProgress((int) progress);
                binding.tvReadMix.setText("停止");

                binding.imvCurrentTime.setText(showTime(progress));
                binding.imvTotalTime.setText(showTime(max));
            }
        });
    }

    private void pauseMargePlay() {
        if (margePlayer != null && margePlayer.isPlaying()) {
            margePlayer.pause();
        }

        RxTimer.getInstance().cancelTimer(margePlayTag);
    }

    private void stopMargePlay() {
        pauseMargePlay();

        binding.imvCurrentTime.setText(showTime(0));
        binding.imvSeekbarPlayer.setProgress(0);
        if (isCanMargePlay){
            if (!TextUtils.isEmpty(margePlayUrl)&&margePlayer!=null){
                binding.tvReadMix.setText("试听");
            }else {
                binding.tvReadMix.setText("合成");
            }
        }else {
            binding.tvReadMix.setText("合成");
        }
    }

    /*******************辅助功能*******************/
    //时间显示
    private String showTime(long time) {
        if (time == 0) {
            return "00:00";
        }

        long totalTime = time / 1000;

        long minTime = totalTime / 60;
        long secTime = totalTime % 60;

        String showMin = "";
        String showSec = "";
        if (minTime >= 10) {
            showMin = String.valueOf(minTime);
        } else {
            showMin = "0" + String.valueOf(minTime);
        }
        if (secTime >= 10) {
            showSec = String.valueOf(secTime);
        } else {
            showSec = "0" + String.valueOf(secTime);
        }

        return showMin + ":" + showSec;
    }

    //显示加载弹窗
    private void startLoading(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.setMsg(msg);
        loadingDialog.show();
    }

    //关闭加载弹窗
    private void stopLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    //弹窗功能展示
    private void showAbilityDialog(boolean isLogin, String abName) {
        String msg = null;
        if (isLogin) {
            msg = "该功能需要登录后才可以使用，是否立即登录？";
        } else {
            msg = "非VIP会员只能免费评测3句，VIP会员无限制评测，是否立即开通解锁？";
        }

        new AlertDialog.Builder(getActivity())
                .setTitle("温馨提示")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (isLogin) {
                            NewLoginUtil.startToLogin(getActivity());
                        } else {
                            startActivity(new Intent(getActivity(), NewVipCenterActivity.class));
                        }
                    }
                }).setNegativeButton("取消", null)
                .show();
    }

    //分享评测的数据
    private void shareSingleEval(String titleCn, int totalScore, String shuoshuoId, String evalAudioUrl) {
        String title = UserInfoManager.getInstance().getUserName() + "在" + getResources().getString(R.string.app_name) + "的评测中获得了" + totalScore + "分";
        String content = titleCn;
        String evalUrl = evalAudioUrl;

        ShareUtil.getInstance().shareEval(getActivity(), types, voaId, shuoshuoId, evalUrl, UserInfoManager.getInstance().getUserId(), title, content);
    }

    //获取合成音频的音频链接
    private String fixMargeAudioUrl(String suffix){
        String margeAudioUrl = "";

        if (TextUtils.isEmpty(suffix)){
            return margeAudioUrl;
        }

        switch (types){
            case TypeLibrary.BookType.junior_primary:
            case TypeLibrary.BookType.junior_middle:
                //中小学
                margeAudioUrl = FixUtil.fixJuniorEvalAudioUrl(suffix);
                break;
            case TypeLibrary.BookType.bookworm:
            case TypeLibrary.BookType.newCamstory:
            case TypeLibrary.BookType.newCamstoryColor:
                //小说
                margeAudioUrl = FixUtil.fixNovelEvalAudioUrl(suffix);
                break;
        }

        return margeAudioUrl;
    }
}
