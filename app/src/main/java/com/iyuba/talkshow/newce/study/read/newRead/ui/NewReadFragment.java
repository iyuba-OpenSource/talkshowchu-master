package com.iyuba.talkshow.newce.study.read.newRead.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.ArticleRecord;
import com.iyuba.talkshow.data.model.UploadRecordResult;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.databinding.FragmentReadBinding;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.event.StudyUploadEvent;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.StudyUserManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.banner.AdBannerShowManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.banner.AdBannerViewBean;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.upload.AdUploadManager;
import com.iyuba.talkshow.lil.help_fix.view.dialog.LoadingDialog;
import com.iyuba.talkshow.lil.help_fix.view.dialog.searchWord.SearchWordDialog;
import com.iyuba.talkshow.lil.help_mvp.util.BigDecimalUtil;
import com.iyuba.talkshow.lil.help_mvp.util.DateUtil;
import com.iyuba.talkshow.lil.help_mvp.view.CenterLinearLayoutManager;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newce.study.read.ReadMvpView;
import com.iyuba.talkshow.newce.study.read.ReadPresenter;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryBgPlayEvent;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryBgPlayManager;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryBgPlaySession;
import com.iyuba.talkshow.newdata.Config;
import com.iyuba.talkshow.newdata.SPconfig;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.talkshow.ui.web.WebActivity;
import com.iyuba.talkshow.util.DialogUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.utils.LibRxTimer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @title: 新的原文界面操作
 * @date: 2023/12/7 19:12
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewReadFragment extends BaseFragment implements ReadMvpView {

    //当前的课程数据
    private Voa curVoa;
    //当前的位置数据
    private int curPosition = -1;
    //当前的单元数据
    private int curUnitId = 0;

    //播放器
    private ExoPlayer exoPlayer;
    //文本数据
    private List<VoaText> textList = new ArrayList<>();
    //适配器
    private NewReadApter readApter;
    //布局样式
    private FragmentReadBinding binding;
    //数据
    @Inject
    public ReadPresenter presenter;
    @Inject
    public ConfigManager configManager;

    //是否可以播放
    private boolean isCanPlay = true;
    //ab点播放处理
    private long aPositon, bPosition, abState = 0L;

    //学习报告的开始时间(每次暂停或者停止都会计数)
    private long studyStartTime = System.currentTimeMillis();

    //广告是否已经刷新了
    private boolean isAdRefresh = false;

    public static NewReadFragment getInstance(Voa voa, int position, int unitId) {
        NewReadFragment fragment = new NewReadFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(StrLibrary.data, voa);
        bundle.putInt(StrLibrary.position, position);
        bundle.putInt(StrLibrary.unitId, unitId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        curVoa = getArguments().getParcelable(StrLibrary.data);
        curPosition = getArguments().getInt(StrLibrary.position, -1);
        curUnitId = getArguments().getInt(StrLibrary.unitId);

        //这里需要处理下当前位置的信息，因为外面增加了广告，因此位置信息不正确，需要重新计算
        //不是会员，并且也不是临时数据才行
        if (!UserInfoManager.getInstance().isVip() && !PrimaryBgPlaySession.getInstance().isTempData()) {
            List<Voa> playList = PrimaryBgPlaySession.getInstance().getVoaList();
            checkVoa:
            for (int i = 0; i < playList.size(); i++) {
                Voa tempVoa = playList.get(i);
                if (tempVoa.voaId() == curVoa.voaId()) {
                    curPosition = i;
                    break checkVoa;
                }
            }
        }

        //保存voaId
        SPconfig.Instance().putInt(Config.currVoaId, curVoa.voaId());
        //绑定必要的数据
        fragmentComponent().inject(this);
        presenter.attachView(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReadBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        initPlayer();
        initBottom();
        initClick();

        checkData();
    }

    @Override
    public void onPause() {
        super.onPause();

        closeSearchWordDialog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

        if (PrimaryBgPlaySession.getInstance().isTempData()) {
            pauseTempAudio(false);
        }
        //关闭广告
        AdBannerShowManager.getInstance().stopBannerAd();
        //停止计时器
        stopAdTimer();
        presenter.detachView();
    }

    /*************************************初始化数据*******************************/
    private void initList() {
        readApter = new NewReadApter(getActivity(), new ArrayList<>());
        binding.recyclerView.setLayoutManager(new CenterLinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(readApter);
        readApter.setOnSelectWordListener(new NewReadApter.onSelectWordListener() {
            @Override
            public void onClickWord(String keyWord) {
                stopPlayer();

                if (!NetworkUtil.isConnected(getActivity())) {
                    ToastUtil.showToast(getActivity(), "请链接网络后使用查词功能");
                    return;
                }

                if (TextUtils.isEmpty(keyWord)) {
                    ToastUtil.showToast(getActivity(), "请取英文单词");
                    return;
                }

                //这里暂时不进行筛选
                keyWord = presenter.filterWord(keyWord);
//                if (!keyWord.matches("^[a-zA-Z]*")) {
//                    ToastUtil.showToast(getActivity(), "请取英文单词");
//                    return;
//                }

                showSearchWordDialog(keyWord);
            }
        });
    }

    private void initBottom() {
        //根据数据显示和处理
        binding.textControl.setVisibility(View.GONE);

        //设置倍速
        float curSpeed = 1.0f;
        if (UserInfoManager.getInstance().isVip()) {
            curSpeed = StudyUserManager.getInstance().getPlaySpeed();
        }
        if (exoPlayer != null) {
            exoPlayer.setPlaybackSpeed(curSpeed);
        }
        //设置语言
        boolean isShowCn = SPconfig.Instance().loadBoolean(Config.ISSHOWCN);
        readApter.refreshLanguage(isShowCn);

        //设置播放类型
        int playMode = SPconfig.Instance().loadInt(Config.playMode);
        showPlayMode(playMode);
    }

    private void initPlayer() {
        if (PrimaryBgPlaySession.getInstance().isTempData()) {
            initTempPlayer();
        } else {
            exoPlayer = PrimaryBgPlayManager.getInstance().getPlayService().getPlayer();
        }

        //预先加载音频数据
        if (PrimaryBgPlaySession.getInstance().isTempData()) {
            binding.reOneVideo.setVisibility(View.INVISIBLE);

            //播放音频
            playTempAudio(getPlayUrl(curVoa));
        } else {
            binding.reOneVideo.setVisibility(View.VISIBLE);

            //判断是否为同一个数据
            if (curVoa.voaId() == PrimaryBgPlaySession.getInstance().getPreVoaId()
                    && curPosition == PrimaryBgPlaySession.getInstance().getPlayPosition()) {
                //同一个数据-根据外部的状态进行判断
                if (exoPlayer != null) {
                    if (exoPlayer.isPlaying()) {
                        playAudio(null);
                    } else {
                        //显示当前进度数据，然后展示
                        binding.curTime.setText(DateUtil.transPlayFormat(DateUtil.MINUTE, exoPlayer.getCurrentPosition()));
                        binding.totalTime.setText(DateUtil.transPlayFormat(DateUtil.MINUTE, exoPlayer.getDuration()));
                        binding.seekBar.setMax((int) exoPlayer.getDuration());
                        binding.seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                        //跳转到当前
                        scrollToPosition(getShowIndex());
                        //暂停
                        pauseAudio(false);
                    }
                }
                return;
            }

            //非同一个数据，则直接播放
            PrimaryBgPlaySession.getInstance().setPlayPosition(curPosition);
            //设置操作的voaId
            PrimaryBgPlaySession.getInstance().setPreVoaId(curVoa.voaId());
            //播放音频
            playAudio(getPlayUrl(curVoa));
        }
    }

    private void initClick() {
        //切换语言
        binding.reCHN.setOnClickListener(v -> {
            switchLanguage();
        });
        //切换上一句
        binding.formerButton.setOnClickListener(v -> {
            switchPreSentence();
        });
        //切换下一句
        binding.nextButton.setOnClickListener(v -> {
            switchNextSentence();
        });
        //切换播放模式
        binding.reOneVideo.setOnClickListener(v -> {
            setPlayMode();
        });
        //显示倍速弹窗
        binding.tvSpeed.setOnClickListener(v -> {
            showSpeedDialog();
        });
        //切换播放/暂停
        binding.videoPlay.setOnClickListener(v -> {
            if (exoPlayer == null) {
                ToastUtil.showToast(getActivity(), "播放器未初始化");
                return;
            }

            if (PrimaryBgPlaySession.getInstance().isTempData()) {
                if (exoPlayer.isPlaying()) {
                    pauseTempAudio(false);
                } else {
                    playTempAudio(null);
                }
            } else {
                if (exoPlayer.isPlaying()) {
                    pauseAudio(false);
                } else {
                    playAudio(null);
                }
            }
        });
        //进度条
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (PrimaryBgPlaySession.getInstance().isTempData()) {
                    pauseTempAudio(false);
                } else {
                    pauseAudio(false);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //显示的进度
                long showProgress = seekBar.getProgress();
                //设置播放器
                exoPlayer.seekTo(showProgress);
            }
        });
    }

    /**********************************数据*******************************/
    //获取数据显示
    private void checkData() {
        curVoa = getArguments().getParcelable(StrLibrary.data);
        presenter.getVoaTexts(curVoa.voaId());
    }

    /**********************************音频******************************/
    //播放音频
    private void playAudio(String urlOrPath) {
        if (!TextUtils.isEmpty(urlOrPath)) {

            MediaItem mediaItem = null;
            if (urlOrPath.startsWith("http")) {
                mediaItem = MediaItem.fromUri(urlOrPath);
            } else {
                //本地加载
                Uri uri = Uri.fromFile(new File(urlOrPath));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(getActivity(), getResources().getString(R.string.file_provider_name_personal), new File(urlOrPath));
                }
                mediaItem = MediaItem.fromUri(uri);
            }
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        } else {
            if (exoPlayer != null && !exoPlayer.isPlaying()) {
                if (exoPlayer.getCurrentPosition() >= exoPlayer.getDuration()) {
                    exoPlayer.seekTo(0);
                }

                exoPlayer.play();
            }
            //图标文本设置
            binding.videoPlay.setImageResource(R.mipmap.image_pause);
            //倒计时
            startAudioTimer();
            //重置学习报告时间
            studyStartTime = System.currentTimeMillis();

            //显示通知栏信息
            PrimaryBgPlayManager.getInstance().getPlayService().showNotification(false, true, curVoa);
            //显示外部操作
            EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_play));

            if (!isCanPlay) {
                pauseTempAudio(false);
            }
        }
    }

    //暂停播放
    private void pauseAudio(boolean isFinish) {
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            exoPlayer.pause();
        }
        //图标
        if (binding != null) {
            binding.videoPlay.setImageResource(R.mipmap.image_play);
        }
        //倒计时
        stopAudioTimer();
        //外部控制
        EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_pause));
        //通知栏控制
        PrimaryBgPlayManager.getInstance().getPlayService().showNotification(false, false, curVoa);

        //提交学习报告数据
        submitListenReport(isFinish);
    }

    //停止播放
    private void stopPlayer() {
        if (PrimaryBgPlaySession.getInstance().isTempData()) {
            pauseTempAudio(false);
        } else {
            pauseAudio(false);
        }
    }

    /***********************************临时音频**************************/
    private void initTempPlayer() {
        if (PrimaryBgPlaySession.getInstance().isTempData()) {
            exoPlayer = new ExoPlayer.Builder(getActivity()).build();
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    switch (playbackState) {
                        case Player.STATE_READY:
                            //加载完成
                            playTempAudio(null);
                            break;
                        case Player.STATE_ENDED:
                            //播放完成
                            pauseTempAudio(true);
                            break;
                    }
                }

                @Override
                public void onPlayerError(PlaybackException error) {
                    ToastUtil.showToast(getActivity(), "播放器初始化失败");
                }
            });
        }
    }

    //播放临时音频
    private void playTempAudio(String urlOrPath) {
        if (PrimaryBgPlaySession.getInstance().isTempData()) {
            if (!TextUtils.isEmpty(urlOrPath)) {

                MediaItem mediaItem = null;
                if (urlOrPath.startsWith("http")) {
                    mediaItem = MediaItem.fromUri(urlOrPath);
                } else {
                    //本地加载
                    Uri uri = Uri.fromFile(new File(urlOrPath));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(getActivity(), getResources().getString(R.string.file_provider_name_personal), new File(urlOrPath));
                    }
                    mediaItem = MediaItem.fromUri(uri);
                }
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
            } else {
                if (exoPlayer != null && !exoPlayer.isPlaying()) {
                    if (exoPlayer.getCurrentPosition() >= exoPlayer.getDuration()) {
                        exoPlayer.seekTo(0);
                    }

                    exoPlayer.play();
                }
                //图标文本设置
                binding.videoPlay.setImageResource(R.mipmap.image_pause);
                //倒计时
                startAudioTimer();
                //重置学习报告时间
                studyStartTime = System.currentTimeMillis();

                if (!isCanPlay) {
                    pauseTempAudio(false);
                }
            }
        }
    }

    //暂停临时音频
    private void pauseTempAudio(boolean isFinish) {
        if (PrimaryBgPlaySession.getInstance().isTempData()) {
            if (exoPlayer != null && exoPlayer.isPlaying()) {
                exoPlayer.pause();
            }
            //显示按钮
            if (binding != null) {
                binding.videoPlay.setImageResource(R.mipmap.image_play);
            }
            //停止计时器
            stopAudioTimer();

            //提交学习报告数据
            submitListenReport(isFinish);
        }
    }

    /***************************************音频计时器***********************/
    //新的播放计时器
    private final Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case time_audio:
                    //播放计时
                    //计算时间
                    long curTime = exoPlayer.getCurrentPosition();
                    long totalTime = exoPlayer.getDuration();

                    //进度显示
                    binding.seekBar.setProgress((int) curTime);
                    binding.seekBar.setMax((int) totalTime);
                    //时间显示
                    binding.curTime.setText(DateUtil.transPlayFormat(DateUtil.MINUTE, curTime));
                    binding.totalTime.setText(DateUtil.transPlayFormat(DateUtil.MINUTE, totalTime));
                    //刷新跳转
                    scrollToPosition(getShowIndex());

                    //判断ab点数据显示
                    if (aPositon != 0 && bPosition != 0) {
                        if (exoPlayer.getCurrentPosition() >= bPosition) {
                            exoPlayer.seekTo(aPositon);
                        }
                    }

                    timeHandler.sendEmptyMessageDelayed(time_audio,200L);
                    break;
            }
        }
    };

    private static final int time_audio = 0;
    private static final String timer_audio = "timer_audio";

    private void startAudioTimer() {
        binding.seekBar.setMax(100);
        binding.seekBar.setProgress(0);

        timeHandler.sendEmptyMessage(time_audio);
    }

    private void stopAudioTimer() {
        timeHandler.removeMessages(time_audio);
    }

    /********************************单词查询********************************/
    //单词查询弹窗
    private SearchWordDialog searchWordDialog;

    //显示查询弹窗
    private void showSearchWordDialog(String word) {
        searchWordDialog = new SearchWordDialog(getActivity(), word);
        searchWordDialog.create();
        searchWordDialog.show();
    }

    //关闭查询弹窗
    private void closeSearchWordDialog() {
        if (searchWordDialog != null && searchWordDialog.isShowing()) {
            searchWordDialog.dismiss();
        }
    }

    /*******************************学习界面功能*******************************/
    //设置ab播放
    public void setAbPlay() {
        abState++;
        if (abState % 3 == 1) {
            aPositon = exoPlayer.getCurrentPosition();
            ToastUtil.showToast(getActivity(), "开始记录A-，再次点击即可区间播放");
        } else if (abState % 3 == 2) {
            bPosition = exoPlayer.getCurrentPosition();
            if (exoPlayer != null) {
                exoPlayer.seekTo(aPositon);
                if (!exoPlayer.isPlaying()) {

                    if (PrimaryBgPlaySession.getInstance().isTempData()) {
                        playTempAudio(null);
                    } else {
                        playAudio(null);
                    }

                }
            }
            ToastUtil.showToast(getActivity(), "开始播放A-B");
        } else if (abState % 3 == 0) {
            aPositon = 0;
            bPosition = 0;
            ToastUtil.showToast(getActivity(), "区间播放已取消");
        }
    }

    /**********************************底部功能******************************/
    //切换语言
    private void switchLanguage() {
        boolean isShowCn = SPconfig.Instance().loadBoolean(Config.ISSHOWCN);
        SPconfig.Instance().putBoolean(Config.ISSHOWCN, !isShowCn);

        if (SPconfig.Instance().loadBoolean(Config.ISSHOWCN)) {
            binding.CHN.setImageResource(R.mipmap.show_cn);
        } else {
            binding.CHN.setImageResource(R.mipmap.show_en);
        }

        readApter.refreshLanguage(!isShowCn);
    }

    //展示倍速弹窗
    private void showSpeedDialog() {
        if (!UserInfoManager.getInstance().isVip()) {
            pauseAudio(false);
            DialogUtil.showVipDialog(getActivity(), "调速功能需要开通VIP后使用，是否开通会员后使用?", NewVipCenterActivity.BENYINGYONG);
        } else {
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
    }

    //切换播放模式
    private void setPlayMode() {
        int playMode = SPconfig.Instance().loadInt(Config.playMode);
        switch (playMode) {
            case 0:
                SPconfig.Instance().putInt(Config.playMode, 1);
                binding.imgPlayMode.setImageResource(R.mipmap.img_mode1);
                ToastUtil.showToast(mContext, "单曲循环");
                break;
            case 1:
                SPconfig.Instance().putInt(Config.playMode, 2);
                binding.imgPlayMode.setImageResource(R.mipmap.img_mode2);
                ToastUtil.showToast(mContext, "随机播放");
                break;
            case 2:
                SPconfig.Instance().putInt(Config.playMode, 0);
                binding.imgPlayMode.setImageResource(R.mipmap.img_mode0);
                ToastUtil.showToast(mContext, "列表循环");
                break;
        }
    }

    //显示播放模式
    private void showPlayMode(int playMode) {
        switch (playMode) {
            case 0:
                binding.imgPlayMode.setImageResource(R.mipmap.img_mode0);
                break;
            case 1:
                binding.imgPlayMode.setImageResource(R.mipmap.img_mode1);
                break;
            case 2:
                binding.imgPlayMode.setImageResource(R.mipmap.img_mode2);
                break;
        }
    }

    //切换上一句
    private void switchPreSentence() {
        //获取当前的进度
        int selectIndex = readApter.getSelectIndex();
        if (selectIndex == 0) {
            ToastUtil.showToast(getActivity(), "当前已经是第一句了");
        } else {
            int preIndex = selectIndex - 1;
            long preProgress = (long) (textList.get(preIndex).timing() * 1000L);
            exoPlayer.seekTo(preProgress);
        }
    }

    //切换下一句
    private void switchNextSentence() {
        //获取当前的进度
        int selectIndex = readApter.getSelectIndex();
        if (selectIndex == textList.size() - 1) {
            ToastUtil.showToast(getActivity(), "当前已经是最后一句了");
        } else {
            int nextIndex = selectIndex + 1;
            long preProgress = (long) (textList.get(nextIndex).timing() * 1000L);
            exoPlayer.seekTo(preProgress);
        }
    }

    /*************************************学习报告*************************/
    //提交学习报告
    private void submitListenReport(boolean isFinish) {
        if (isFinish) {
            //根据是否需要显示学习报告
            boolean isShowReport = configManager.isStudyReport();
            if (isShowReport){
                startLoading("正在加载学习报告～");
            }
        }

//        if (presenter != null) {
//            presenter.submitListenReport(studyStartTime, System.currentTimeMillis(), isFinish, presenter.getWordByIndex(textList, getShowIndex()), curVoa.voaId());
//        }

        if (presenter != null) {
            presenter.submitListenReportNew(studyStartTime, System.currentTimeMillis(), isFinish, presenter.getWordByIndex(textList, getShowIndex()), curVoa.voaId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<UploadRecordResult>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            stopLoading();

                            if (isFinish){
                                EventBus.getDefault().post(new NewReadListenEvent(NewReadListenEvent.type_showReport,""));
                            }
                        }

                        @Override
                        public void onNext(UploadRecordResult bean) {
                            stopLoading();

                            if (isFinish){
                                if (bean!=null&&bean.getResult().equals("1")){
                                    //获取数据显示
                                    double price = Integer.parseInt(bean.getReward())*0.01;
                                    if (price>0){
                                        price = BigDecimalUtil.trans2Double(price);

                                        //显示学习报告
                                        EventBus.getDefault().post(new NewReadListenEvent(NewReadListenEvent.type_showReport,String.valueOf(price)));
                                        return;
                                    }
                                }

                                EventBus.getDefault().post(new NewReadListenEvent(NewReadListenEvent.type_showReport,""));
                            }
                        }
                    });
        }
    }

    /************************************加载弹窗**************************/
    private LoadingDialog loadingDialog;

    private void startLoading(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.setMsg(msg);
        loadingDialog.show();
    }

    public void stopLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    /*************************************其他功能************************/
    //获取播放的音频
    private String getPlayUrl(Voa tempVoa) {
        String audioUrl = Constant.getSoundMp3Url(tempVoa.sound(), tempVoa.voaId());
        File audioFile = StorageUtil.getAudioFile(TalkShowApplication.getInstance(), tempVoa.voaId());
        if (audioFile.exists()) {
            audioUrl = audioFile.getAbsolutePath();
        }
        return audioUrl;
    }

    //查询当前文本滚动位置
    //最后一个item显示错误时，recyclerView外层增加relativeLayout，增加android:descendantFocusability="blocksDescendants"属性
    private int getShowIndex() {
        if (exoPlayer == null || textList == null || textList.size() == 0) {
            return 0;
        }

        for (int i = 0; i < textList.size(); i++) {
            //音频数据
            long audioTime = exoPlayer.getCurrentPosition();
            //当前数据
            VoaText voaText = textList.get(i);
            long endTime = (long) (voaText.endTiming() * 1000L);

            if (audioTime <= endTime) {
                return i;
            }

            //最后一个数据
            long lastData = (long) (textList.get(textList.size() - 1).endTiming() * 1000L);
            if (i == textList.size() - 1 && audioTime > lastData) {
                return textList.size() - 1;
            }
        }

        return 0;
    }

    //跳转到指定位置
    private void scrollToPosition(int showIndex) {
        //刷新显示
        readApter.refreshIndex(showIndex);
        //跳转
        boolean isScroll = SPconfig.Instance().loadBoolean(Config.playPosition, true);
        if (isScroll) {
            ((CenterLinearLayoutManager) binding.recyclerView.getLayoutManager()).smoothScrollToPosition(binding.recyclerView, new RecyclerView.State(), showIndex);
        }
    }

    //设置停止播放
    public void setCanPlay(boolean isPlay) {
        this.isCanPlay = isPlay;

        if (!isCanPlay) {
            stopPlayer();
        }
    }

    /**********************************回调*****************************/
    @Override
    public void showVoaTexts(List<VoaText> voaTextList) {
        if (voaTextList != null && voaTextList.size() > 0) {
            this.textList = voaTextList;
            binding.textControl.setVisibility(View.VISIBLE);
            readApter.refreshData(textList);

            //设置广告
            refreshAd();
        } else {
            this.textList.clear();
            binding.textControl.setVisibility(View.GONE);
            AdBannerShowManager.getInstance().stopBannerAd();
            readApter.refreshData(textList);
            ToastUtil.showToast(getActivity(), "未查询到当前课程数据");
        }
    }

    @Override
    public void showEmptyTexts() {
        this.textList.clear();
        binding.textControl.setVisibility(View.GONE);
        AdBannerShowManager.getInstance().stopBannerAd();
        readApter.refreshData(textList);
        ToastUtil.showToast(getActivity(), "未查询到当前课程数据");
    }

    //小学英语的后台播放回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayEvent(PrimaryBgPlayEvent event) {
        //播放
        if (event.getShowType().equals(PrimaryBgPlayEvent.event_audio_play)) {
            //显示进度
            if (exoPlayer != null && !PrimaryBgPlayManager.getInstance().getPlayService().isPrepare()) {
                binding.totalTime.setText(DateUtil.transPlayFormat(DateUtil.MINUTE, exoPlayer.getDuration()));
                binding.seekBar.setMax((int) exoPlayer.getDuration());
            }

            if (isCanPlay) {
                playAudio(null);
            } else {
                pauseAudio(false);
            }
        }

        //暂停
        if (event.getShowType().equals(PrimaryBgPlayEvent.event_audio_pause)) {
            pauseAudio(false);
        }

        //播放完成
        if (event.getShowType().equals(PrimaryBgPlayEvent.event_audio_completeFinish)) {
            //重置进度
            pauseAudio(true);

            //刷新播放进度
            presenter.saveArticleRecord(ArticleRecord.builder().setUid(UserInfoManager.getInstance().getUserId())
                    .setVoa_id(curVoa.voaId())
                    .setCurr_time((int) exoPlayer.getCurrentPosition() / 1000)
                    .setTotal_time((int) exoPlayer.getDuration() / 1000)
                    .setType(0)
                    .setIs_finish(1)
                    .setPercent(100)
                    .build());
            EventBus.getDefault().post(new StudyUploadEvent());
        }
    }

    //账号登录后的回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event){
        refreshAd();
    }

    /***************************广告计时器***************************/
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
                    pauseAudio(false);

                    if (isJumpByUserClick){
                        if (TextUtils.isEmpty(jumpUrl)){
                            ToastUtil.showToast(getActivity(),"暂无内容");
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
                                ToastUtil.showToast(TalkShowApplication.getContext(),showMsg);

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
