package com.iyuba.talkshow.lil.help_fix.ui.study.read;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.databinding.FragmentFixReadBinding;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.lil.help_fix.data.bean.BookChapterBean;
import com.iyuba.talkshow.lil.help_fix.data.bean.ChapterDetailBean;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.StudySettingManager;
import com.iyuba.talkshow.lil.help_fix.manager.StudyUserManager;
import com.iyuba.talkshow.lil.help_fix.manager.studyReport.StudyReportManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.banner.AdBannerShowManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.banner.AdBannerViewBean;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.upload.AdUploadManager;
import com.iyuba.talkshow.lil.help_fix.view.dialog.searchWord.SearchWordDialog;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingFragment;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxTimer;
import com.iyuba.talkshow.lil.help_mvp.view.CenterLinearLayoutManager;
import com.iyuba.talkshow.lil.novel.service.FixBgServiceManager;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.talkshow.ui.web.WebActivity;
import com.iyuba.wordtest.utils.LibRxTimer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 通用的原文界面
 * @date: 2023/7/17 11:08
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ReadFragment extends BaseViewBindingFragment<FragmentFixReadBinding> implements ReadView {

    private String bookType;
    private String voaId;

    private ReadPresenter presenter;
    private ReadAdapter readAdapter;

    //章节数据
    private BookChapterBean chapterBean;
    //详情数据
    private List<ChapterDetailBean> list;
    //播放器
    private ExoPlayer exoPlayer;
    //音频播放地址
    private String playAudioUrl = null;
    //是否可以播放
    private boolean isCanPlay = false;
    //是否切换到其他界面
    private boolean isSwitchPage = false;

    //播放时间和进度显示标志位
    private String playTag = "playTag_" + System.currentTimeMillis();
    //单词查询弹窗
    private SearchWordDialog searchWordDialog;

    //ab点次数
    private long abState = 0;
    //ab开始点
    private long abStartPosition = 0;
    //ab结束点
    private long abEndPosition = 0;

    //是否是第一次提交学习报告
    private boolean isFirstReport = true;

    //设置广告是否刷新
    private boolean isAdRefresh = false;

    public static ReadFragment getInstance(String types, String voaId) {
        ReadFragment fragment = new ReadFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types, types);
        bundle.putString(StrLibrary.voaId, voaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        bookType = getArguments().getString(StrLibrary.types);
        voaId = getArguments().getString(StrLibrary.voaId);

        presenter = new ReadPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        initPlayer();
        initClick();
        initUi();

        //刷新数据
        refreshData();
        //开启广告循环
        refreshAd();
    }

    @Override
    public void onPause() {
        super.onPause();

        closeSearchWordDialog();
//        pausePlay();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        stopPlay();

        //关闭广告
        AdBannerShowManager.getInstance().stopBannerAd();
        stopAdTimer();

        //直接重置通知栏
        FixBgServiceManager.getInstance().fixBgService.showNotification(true,false,null,null,null,null);

        presenter.detachView();
    }

    /*********************************初始化***************************/
    private void initList() {
        readAdapter = new ReadAdapter(getActivity(), new ArrayList<>());
        CenterLinearLayoutManager manager = new CenterLinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(readAdapter);
        readAdapter.setOnWordSearchListener(new ReadAdapter.onWordSearchListener() {
            @Override
            public void onWordSearch(String selectText) {
                pausePlay(false);
                if (!NetworkUtil.isConnected(getActivity())) {
                    ToastUtil.showToast(getActivity(), "请链接网络后重试～");
                    return;
                }

                if (!TextUtils.isEmpty(selectText)) {
                    //先处理下数据(这里暂时不进行筛选)
                    selectText = filterWord(selectText);
//                    if (!selectText.matches("^[a-zA-Z]*")) {
//                        ToastUtil.showToast(getActivity(), "请取英文单词");
//                        return;
//                    }

                    showSearchWordDialog(selectText);
                } else {
                    ToastUtil.showToast(getActivity(), "请取英文单词");
                }
            }
        });
    }

    private void initPlayer() {
        BookChapterBean chapterBean = presenter.getChapterData(bookType, voaId);
        if (chapterBean != null) {
            playAudioUrl = chapterBean.getAudioUrl();
        }

        MediaItem mediaItem = null;
        //先查询路径，没有数据则使用uri
//        String audioPath = FileManager.getInstance().getCourseAudioPath(types,voaId);
//        if (FileManager.getInstance().isFileExist(audioPath)){
//
//        }
        mediaItem = MediaItem.fromUri(playAudioUrl);
        exoPlayer = new ExoPlayer.Builder(getActivity()).build();
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.prepare();

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        //加载完成
                        isCanPlay = true;

                        //默认自动播放
                        startPlay();
                        break;
                    case Player.STATE_ENDED:
                        //播放结束
                        binding.seekBar.setProgress(binding.seekBar.getMax());
                        pausePlay(true);

                        //显示听力学习报告
                        EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.listenDialog));

                        //判断类型，处理显示
                        String playMode = StudySettingManager.getInstance().getSyncMode();
                        if (playMode.equals(TypeLibrary.PlayModeType.SINGLE_SYNC)) {
                            //单曲循环
                            startPlay();
                        } else if (playMode.equals(TypeLibrary.PlayModeType.ORDER_PLAY)) {
                            //顺序播放
                            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.study_next));
                        } else if (playMode.equals(TypeLibrary.PlayModeType.RANDOM_PLAY)) {
                            //随机播放
                            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.study_random));
                        }
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                if (!isResumed()) {
                    return;
                }

                pausePlay(false);
                ToastUtil.showToast(getActivity(), "播放音频失败~");
            }
        });
    }

    private void initClick() {
        //播放进度条
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    exoPlayer.seekTo(progress);
                    Log.d("点击进度显示", "onProgressChanged: --" + progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pausePlay(false);
                Log.d("点击进度显示", "onStartTrackingTouch: ");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() > abEndPosition) {
                    abState = 0;
                    abStartPosition = 0;
                    abEndPosition = 0;
                }

                startPlay();
                Log.d("点击进度显示", "onStopTrackingTouch: ");
            }
        });
        //播放倍速
        binding.tvSpeed.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                showAbilityDialog(true, "调速功能");
                return;
            }

            if (!UserInfoManager.getInstance().isVip()) {
                showAbilityDialog(false, "调速功能");
                return;
            }

            //弹出倍速
            showSpeedDialog();
        });
        //播放类型
        binding.imgPlayMode.setOnClickListener(v -> {
            String playMode = StudySettingManager.getInstance().getSyncMode();
            if (playMode.equals(TypeLibrary.PlayModeType.ORDER_PLAY)) {

                StudySettingManager.getInstance().setSyncMode(TypeLibrary.PlayModeType.SINGLE_SYNC);
                binding.imgPlayMode.setImageResource(R.mipmap.img_mode1);
                ToastUtil.showToast(getActivity(), "单曲循环");

            } else if (playMode.equals(TypeLibrary.PlayModeType.SINGLE_SYNC)) {

                StudySettingManager.getInstance().setSyncMode(TypeLibrary.PlayModeType.RANDOM_PLAY);
                binding.imgPlayMode.setImageResource(R.mipmap.img_mode2);
                ToastUtil.showToast(getActivity(), "随机播放");

            } else if (playMode.equals(TypeLibrary.PlayModeType.RANDOM_PLAY)) {

                StudySettingManager.getInstance().setSyncMode(TypeLibrary.PlayModeType.ORDER_PLAY);
                binding.imgPlayMode.setImageResource(R.mipmap.img_mode0);
                ToastUtil.showToast(getActivity(), "顺序播放");

            }
        });
        //切换中英文
        binding.reCHN.setOnClickListener(v -> {
            String textType = StudySettingManager.getInstance().getTextType();
            if (textType.equals(TypeLibrary.TextShowType.ALL)) {
                StudySettingManager.getInstance().setTextType(TypeLibrary.TextShowType.EN);
                binding.CHN.setImageResource(R.mipmap.show_en);
            } else if (textType.equals(TypeLibrary.TextShowType.EN)) {
                StudySettingManager.getInstance().setTextType(TypeLibrary.TextShowType.ALL);
                binding.CHN.setImageResource(R.mipmap.show_cn);
            }

            readAdapter.refreshShowTextType(StudySettingManager.getInstance().getTextType());
        });
        //课程音频播放和暂停
        binding.videoPlay.setOnClickListener(v -> {
            if (!NetworkUtil.isConnected(getActivity())) {
                ToastUtil.showToast(getActivity(), "请链接网络后播放音频～");
                return;
            }

            if (!isCanPlay) {
                ToastUtil.showToast(getActivity(), "正在加载音频文件～");
                return;
            }

            if (exoPlayer != null) {
                if (exoPlayer.isPlaying()) {
                    pausePlay(false);
                } else {
                    startPlay();
                }
            }
        });
        //上一章节
        binding.formerButton.setOnClickListener(v -> {
            closeSearchWordDialog();

            int selectIndex = readAdapter.getSelectIndex();
            if (selectIndex == 0) {
                ToastUtil.showToast(getActivity(), "当前已经是第一个了");
            } else {
                int preIndex = selectIndex - 1;
                long preProgress = (long) (list.get(preIndex).getTiming() * 1000L);
                exoPlayer.seekTo(preProgress);
            }
        });
        //下一章节
        binding.nextButton.setOnClickListener(v -> {
            closeSearchWordDialog();

            int selectIndex = readAdapter.getSelectIndex();
            if (selectIndex == list.size() - 1) {
                ToastUtil.showToast(getActivity(), "当前已经是最后一个了");
            } else {
                int nextIndex = selectIndex + 1;
                long nextProgress = (long) (list.get(nextIndex).getTiming() * 1000L);
                exoPlayer.seekTo(nextProgress);
            }
        });
    }

    private void initUi() {
        //文本显示类型
        String textType = StudySettingManager.getInstance().getTextType();
        if (textType.equals(TypeLibrary.TextShowType.ALL)) {
            binding.CHN.setImageResource(R.mipmap.show_cn);
        } else if (textType.equals(TypeLibrary.TextShowType.EN)) {
            binding.CHN.setImageResource(R.mipmap.show_en);
        }
        readAdapter.refreshShowTextType(StudySettingManager.getInstance().getTextType());

        //播放类型
        String playMode = StudySettingManager.getInstance().getSyncMode();
        if (playMode.equals(TypeLibrary.PlayModeType.ORDER_PLAY)) {
            binding.imgPlayMode.setImageResource(R.mipmap.img_mode0);
        } else if (playMode.equals(TypeLibrary.PlayModeType.RANDOM_PLAY)) {
            binding.imgPlayMode.setImageResource(R.mipmap.img_mode2);
        } else if (playMode.equals(TypeLibrary.PlayModeType.SINGLE_SYNC)) {
            binding.imgPlayMode.setImageResource(R.mipmap.img_mode1);
        }

        //倍速数据
        float speed = StudyUserManager.getInstance().getPlaySpeed();
        exoPlayer.setPlaybackSpeed(speed);
    }

    /*********************刷新数据显示***************/
    private void refreshData() {
        chapterBean = presenter.getChapterData(bookType, voaId);

        list = presenter.getChapterDetail(bookType, voaId);
        readAdapter.refreshData(list);
    }

    /*********************音频操作*******************/
    //播放音频
    private void startPlay() {
        if (requireActivity().isDestroyed()) {
            return;
        }

        if (!NetworkUtil.isConnected(getActivity())) {
            ToastUtil.showToast(getActivity(), "请链接网络后播放音频～");
            return;
        }

        if (exoPlayer == null || !isCanPlay) {
            ToastUtil.showToast(getActivity(), "播放器未初始化～");
            return;
        }

        if (isSwitchPage) {
            return;
        }

        if (exoPlayer.getCurrentPosition() >= exoPlayer.getDuration()) {
            exoPlayer.seekTo(0);
        }

        //保存学习报告信息
        StudyReportManager.getInstance().saveListenReportData(System.currentTimeMillis(), isFirstReport ? list : null);

        exoPlayer.play();
        binding.curTime.setText(showTime(binding.seekBar.getProgress()));
        binding.totalTime.setText(showTime(exoPlayer.getDuration()));

        FixBgServiceManager.getInstance().fixBgService.showNotification(false, true, chapterBean.getTitleEn(), chapterBean.getTypes(), chapterBean.getBookId(), chapterBean.getVoaId());
        binding.seekBar.setMax((int) exoPlayer.getDuration());
        binding.videoPlay.setImageResource(R.mipmap.image_pause);

        RxTimer.getInstance().cancelTimer(playTag);
        RxTimer.getInstance().multiTimerInMain(playTag, 0, 200L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                if (binding == null) {
                    return;
                }

                long curPlayTime = exoPlayer.getCurrentPosition();

                Log.d("时间进度", "onAction: --" + exoPlayer.getContentPosition());
                binding.seekBar.setProgress((int) curPlayTime);
                binding.curTime.setText(showTime(curPlayTime));
                showSentenceProgress();

                //这里处理下ab点播放
                if (abEndPosition > 0
                        && abEndPosition > abStartPosition
                        && curPlayTime >= abEndPosition) {
                    exoPlayer.seekTo(abStartPosition);
                }
            }
        });
    }

    //暂停播放
    public void pausePlay(boolean isFinish) {
        RxTimer.getInstance().cancelTimer(playTag);
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            exoPlayer.pause();
        }

        //提交学习报告
        if (UserInfoManager.getInstance().isLogin()) {
            StudyReportManager.getInstance().submitListenReportData(bookType, System.currentTimeMillis(), isFinish, voaId);
        }
        isFirstReport = false;

        if (FixBgServiceManager.getInstance().fixBgService != null) {
            FixBgServiceManager.getInstance().fixBgService.showNotification(false, false, chapterBean.getTitleEn(), chapterBean.getTypes(), chapterBean.getBookId(), chapterBean.getVoaId());
        }

        if (binding != null) {
            binding.videoPlay.setImageResource(R.mipmap.image_play);
        }
    }

    //停止播放
    public void stopPlay() {
        RxTimer.getInstance().cancelTimer(playTag);
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }

        isFirstReport = false;

        if (FixBgServiceManager.getInstance().fixBgService != null) {
            FixBgServiceManager.getInstance().fixBgService.showNotification(false, false, chapterBean.getTitleEn(), chapterBean.getTypes(), chapterBean.getBookId(), chapterBean.getVoaId());
        }

        if (binding != null) {
            binding.videoPlay.setImageResource(R.mipmap.image_play);
        }
    }

    /*********************单词查询****************/
    //显示查询弹窗
    private void showSearchWordDialog(String word) {
        searchWordDialog = new SearchWordDialog(getActivity(), word);
        searchWordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                readAdapter.resetSelectWordStyle();
            }
        });
        searchWordDialog.create();
        searchWordDialog.show();
    }

    //关闭查询弹窗
    private void closeSearchWordDialog() {
        if (searchWordDialog != null && searchWordDialog.isShowing()) {
            searchWordDialog.dismiss();
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

    //查询当前显示的位置并跳转
    private void showSentenceProgress() {
        //当前时间处于第几个
        int curPosition = 0;

        time:
        for (int i = 0; i < list.size(); i++) {
            ChapterDetailBean textsBean = list.get(i);

            //获取开始和结束时间
            long startTime = 0;
            long endTime = 0;

            if (i == list.size() - 1) {
                endTime = exoPlayer.getDuration();
            } else {
                endTime = (long) (textsBean.getEndTiming() * 1000);
            }

            if (i == 0) {
                startTime = 0;
            } else {
                ChapterDetailBean temp = list.get(i - 1);
                if (temp.getEndTiming() > 0) {
                    startTime = (long) (temp.getEndTiming() * 1000);
                } else {
                    startTime = (long) (textsBean.getTiming() * 1000);
                }
            }

            //逐个进行判断
            long curPlayerTime = exoPlayer.getCurrentPosition();
            if (curPlayerTime >= startTime && curPlayerTime <= endTime) {
                Log.d("进度显示000", "showSentenceProgress: --" + startTime + "--" + endTime);
                curPosition = i;
                break time;
            }
        }

        //执行刷新
        readAdapter.refreshIndex(curPosition);
        if (StudySettingManager.getInstance().getRollOpen()) {
            ((CenterLinearLayoutManager) binding.recyclerView.getLayoutManager()).smoothScrollToPosition(binding.recyclerView, new RecyclerView.State(), curPosition);
        }
    }

    //显示倍速弹窗
    private void showSpeedDialog() {
        //弹出倍速
        float curSpeed = StudyUserManager.getInstance().getPlaySpeed();
        String[] items = new String[]{"0.5x", "0.75x", "1.0x", "1.5x", "2.0x"};
        int showIndex = 2;

        for (int i = 0; i < items.length; i++) {
            float showSpeed = Float.parseFloat(items[i].replace("x", ""));
            if (showSpeed == curSpeed) {
                showIndex = i;
            }
        }

        new AlertDialog.Builder(getActivity())
                .setTitle("倍速调整")
                .setSingleChoiceItems(items, showIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        String speed = items[which];
                        float speedDub = Float.parseFloat(speed.replace("x", ""));
                        if (exoPlayer != null) {
                            exoPlayer.setPlaybackSpeed(speedDub);
                        }
                        StudyUserManager.getInstance().savePlaySpeed(speedDub);
                    }
                })
                .create()
                .show();
    }

    //弹窗功能展示
    private void showAbilityDialog(boolean isLogin, String abName) {
        String msg = null;
        if (isLogin) {
            msg = "该功能需要登录后才可以使用，是否立即登录？";
        } else {
            msg = abName + "需要VIP权限，是否立即开通解锁？";
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(abName)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        pausePlay(false);

                        if (isLogin) {
                            NewLoginUtil.startToLogin(getActivity());
                        } else {
                            startActivity(new Intent(getActivity(), NewVipCenterActivity.class));
                        }
                    }
                }).setNegativeButton("取消", null)
                .show();
    }

    //刷新列表文字大小
    private void refreshTextSize(int size) {
        readAdapter.refreshShowTextSize(size);
    }

    //处理单词数据
    private String filterWord(String selectText) {
        if (selectText.startsWith(".")||selectText.endsWith(".")){
            selectText = selectText.replace(".", "");
        }
        if (selectText.startsWith(",")||selectText.endsWith(",")){
            selectText = selectText.replace(",", "");
        }
        if (selectText.startsWith("!")||selectText.endsWith("!")){
            selectText = selectText.replace("!", "");
        }
        if (selectText.startsWith("?")||selectText.endsWith("?")){
            selectText = selectText.replace("?", "");
        }
        if (selectText.startsWith("'")||selectText.endsWith("?")){
            selectText = selectText.replace("'", "");
        }
        if (selectText.startsWith("\"")||selectText.endsWith("\"")){
            selectText = selectText.replace("\"", "");
        }

        return selectText;
    }

    //设置文本滚动
    public void setTextSync() {
        boolean textRoll = StudySettingManager.getInstance().getRollOpen();
        if (textRoll) {
            StudySettingManager.getInstance().setRollOpen(false);
            ToastUtil.showToast(getActivity(), "文本自动滚动关闭");
        } else {
            StudySettingManager.getInstance().setRollOpen(true);
            ToastUtil.showToast(getActivity(), "文本自动滚动开启");
        }
    }

    //设置ab点播放
    public void setABPlay() {
        if (exoPlayer == null || !isCanPlay) {
            ToastUtil.showToast(getActivity(), "播放器未初始化～");
            return;
        }

        abState++;
        if (abState % 3 == 1) {
            //a点记录
            abStartPosition = exoPlayer.getCurrentPosition();
            ToastUtil.showToast(getActivity(), "开始记录A-，再次点击即可区间播放");
        } else if (abState % 3 == 2) {
            //b点播放
            abEndPosition = exoPlayer.getCurrentPosition();
            ToastUtil.showToast(getActivity(), "开始播放A-B");
        } else if (abState % 3 == 0) {
            //停止循环
            abStartPosition = 0;
            abEndPosition = 0;
            ToastUtil.showToast(getActivity(), "区间播放已取消");
        }
    }

    //切换界面标志
    public void switchPage(boolean isSwitch) {
        this.isSwitchPage = isSwitch;
    }

    /******************************回调数据***************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event) {
        if (event.getType().equals(TypeLibrary.RefreshDataType.read_stop)) {
            pausePlay(false);
        }
    }

    //账号登录后回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent loginEvent){
        refreshAd();
    }

    /**************************广告计时器**************************/
    //广告定时器
    private static final String timer_ad = "timer_ad";
    //广告间隔时间
    private static final long adScaleTime = 20*1000L;
    //开始计时
    private void startAdTimer() {
        stopAdTimer();
        LibRxTimer.getInstance().multiTimerInMain(timer_ad, 0, adScaleTime, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                showBannerAd();
            }
        });
    }
    //停止计时
    private void stopAdTimer() {
        LibRxTimer.getInstance().cancelTimer(timer_ad);
    }

    /*******************************新的banner广告显示**********************/
    //是否已经获取了奖励
    private boolean isGetRewardByClickAd = false;
    //显示的界面模型
    private AdBannerViewBean bannerViewBean = null;
    //显示banner广告
    private void showBannerAd(){
        //请求广告
        if (bannerViewBean==null){
            bannerViewBean = new AdBannerViewBean(binding.adLayout.iyubaSdkAdLayout, binding.adLayout.webAdLayout, binding.adLayout.webAdImage, binding.adLayout.webAdClose,binding.adLayout.webAdTips, new AdBannerShowManager.OnAdBannerShowListener() {
                @Override
                public void onLoadFinishAd() {

                }

                @Override
                public void onAdShow(String adType) {
                    binding.adLayout.getRoot().setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClick(String adType, boolean isJumpByUserClick, String jumpUrl) {
                    pausePlay(false);

                    if (isJumpByUserClick){
                        if (TextUtils.isEmpty(jumpUrl)){
                            com.iyuba.talkshow.util.ToastUtil.showToast(getActivity(),"暂无内容");
                            return;
                        }

                        Intent intent = new Intent();
                        intent.setClass(getActivity(), WebActivity.class);
                        intent.putExtra("url", jumpUrl);
                        startActivity(intent);
                    }

                    //点击广告获取奖励
                    if (!isGetRewardByClickAd){
                        isGetRewardByClickAd = true;

                        //获取奖励
                        String fixShowType = AdShowUtil.NetParam.AdShowPosition.show_banner;
                        String fixAdType = adType;
                        AdUploadManager.getInstance().clickAdForReward(fixShowType, fixAdType, new AdUploadManager.OnAdClickCallBackListener() {
                            @Override
                            public void showClickAdResult(boolean isSuccess, String showMsg) {
                                //直接显示信息即可
                                com.iyuba.talkshow.util.ToastUtil.showToast(TalkShowApplication.getContext(),showMsg);

                                if (isSuccess){
                                    EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
                                }
                            }
                        });
                        //点击广告提交数据
                        /*List<AdLocalMarkBean> localAdList = new ArrayList<>();
                        localAdList.add(new AdLocalMarkBean(
                                fixAdType,
                                fixShowType,
                                AdShowUtil.NetParam.AdOperation.operation_click,
                                System.currentTimeMillis()/1000L
                        ));
                        AdUploadManager.getInstance().submitAdMsgData(getActivity().getPackageName(), localAdList, new AdUploadManager.OnAdSubmitCallbackListener() {
                            @Override
                            public void showSubmitAdResult(boolean isSuccess, String showMsg) {
                                //不进行处理
                            }
                        });*/
                    }
                }

                @Override
                public void onAdClose(String adType) {
                    //关闭界面
                    binding.adLayout.getRoot().setVisibility(View.GONE);
                    //关闭计时器
                    stopAdTimer();
                    //关闭广告
                    AdBannerShowManager.getInstance().stopBannerAd();
                }

                @Override
                public void onAdError(String adType) {

                }
            });
            AdBannerShowManager.getInstance().setShowData(getActivity(),bannerViewBean);
        }
        AdBannerShowManager.getInstance().showBannerAd();
        //重置数据
        isGetRewardByClickAd = false;
    }
    //配置广告显示
    private void refreshAd(){
        if (!UserInfoManager.getInstance().isVip() && !AdBlocker.getInstance().shouldBlockAd()) {
            startAdTimer();
        }else {
            binding.adLayout.getRoot().setVisibility(View.GONE);
            stopAdTimer();
            AdBannerShowManager.getInstance().stopBannerAd();
        }
    }
}
