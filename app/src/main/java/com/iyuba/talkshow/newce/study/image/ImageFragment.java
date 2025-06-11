package com.iyuba.talkshow.newce.study.image;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.databinding.FragmentImageBinding;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxTimer;
import com.iyuba.talkshow.newce.study.StudyActivity;
import com.iyuba.talkshow.newce.study.read.ReadMvpView;
import com.iyuba.talkshow.newdata.Config;
import com.iyuba.talkshow.newdata.GlideUtil;
import com.iyuba.talkshow.newdata.Playmanager;
import com.iyuba.talkshow.newdata.RefreshEvent;
import com.iyuba.talkshow.newdata.SPconfig;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.util.DensityUtil;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by carl shen on 2021/10/29
 * New Primary English, new study experience.
 */
public class ImageFragment extends BaseFragment implements ReadMvpView {
    public static final String TAG = "ImageFragment";
    @Inject
    public ImagePresenter mPresenter;

    private FragmentImageBinding binding;
    private final List<VoaText> mVoaTextList = new ArrayList<>();
    private final HashMap<String, List<VoaText>> mImgPaths = new HashMap<>();
    private final HashMap<String, List<VoaText>> mImgWords = new HashMap<>();
    private final List<String> mImgString = new ArrayList<>();

    //音频播放
//    private ExtendedPlayer myIjkPlayer;
    private ExoPlayer exoPlayer;

    private String audioUrl;
    private Voa mVoa;
    private int unitId = 0;
    private int scrollFlag = 0;
    private volatile int eventFlag = 0;

    //是否将上一张和下一张进行隐藏处理
    private boolean isTagHide = true;
    //是否可以播放
    private boolean isCanPlay = true;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (((StudyActivity) mContext).isDestroyed() || !ImageFragment.this.isAdded()) {
                return;
            }
            switch (msg.what) {
                case 1024:
                    pausePlayer();
                    break;
                default:
                    break;
            }
        }
    };

    public static ImageFragment newInstance(Voa voa, String jumpTitle, int unit) {
        ImageFragment readFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(StudyActivity.VOA, voa);
        args.putString(StudyActivity.POS, jumpTitle);
        args.putInt(StudyActivity.UNIT, unit);
        readFragment.setArguments(args);
        return readFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
        mPresenter.attachView(this);
        mVoa = getArguments().getParcelable(StudyActivity.VOA);
        unitId = getArguments().getInt(StudyActivity.UNIT, 0);
        //获取播放器
        boolean isAmerican = SPconfig.Instance().loadBoolean(Config.ISAMEICAN, true);
        if (isAmerican) {
            audioUrl = Constant.Web.sound_vip + mVoa.voaId() / 1000 + "_" + mVoa.voaId() % 1000 + ".mp3";
        } else {
            audioUrl = Constant.Web.sound_vip + "british/" + mVoa.voaId() / 1000 + "/" + mVoa.voaId() / 1000 + "_" + mVoa.voaId() % 1000 + ".mp3";
        }
        audioUrl = Constant.getSoundMp3Url(mVoa.sound(), mVoa.voaId());
        Log.e(TAG, "voa audioUrl " + audioUrl);
        //这里在公司的测试机上出现了音频不对应的问题，当前直接采用网络数据进行播放显示，暂时不用本地数据
//        File audioFile = StorageUtil.getAudioFile(TalkShowApplication.getInstance(), mVoa.voaId());
//        if (audioFile.exists()) {
//            Log.e(TAG, "voa audioFile " + audioFile.getAbsolutePath());
//            audioUrl = audioFile.getAbsolutePath();
//        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentImageBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //增加eventbus
        EventBus.getDefault().register(this);

        binding.imageShow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isPrepare||!isCanPlay){
                    ToastUtil.showToast(getActivity(),"音频正在加载中，请等待加载完成");
                    return true;
                }
                return processEvent(v, event);
            }
        });
        binding.imageContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        binding.imageContent.setVisibility(View.INVISIBLE);
        binding.imageGuide.setVisibility(View.INVISIBLE);

        init();
    }

    private void init(){
        initClick();
        initPlayer();
        if (NetStateUtil.isConnected(TalkShowApplication.getContext())) {
//            mPresenter.syncVoaTexts(mVoa.voaId());
            mPresenter.getVoaTexts(mVoa.voaId());
        } else {
            mPresenter.getVoaTexts(mVoa.voaId());
        }
    }

    private boolean processEvent(View v, MotionEvent event) {
        // 获得触摸事件的动作类型
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) { // 手指按下
            if (eventFlag > 0) {
//                return false;
            }
//            Log.e(TAG, "onTouchEvent page for v.getWidth() " + v.getWidth());
//            Log.e(TAG, "onTouchEvent page for v.getHeight() " + v.getHeight());
            List<VoaText> mVoaText = mImgPaths.get(mImgString.get(scrollFlag));
            if ((mVoaText == null) || (mVoaText.size() < 1)) {
                Log.e(TAG, "onTouch current image path is null. ");
                return false;
            }
            if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                showToastShort("播放点读需要访问数据网络。");
                return false;
            }
            Collections.sort(mVoaText, new Comparator<VoaText>() {
                @Override
                public int compare(VoaText o1, VoaText o2) {
                    return o1.paraId() - (o2.paraId());
                }
            });
            for (VoaText item : mVoaText) {
                if ((item == null) || TextUtils.isEmpty(item.imgPath())) {
                    continue;
                }
                String readTips = item.sentenceCn();
                int xPos0 = item.startX() * v.getWidth() / DensityUtil.DEFAULT_WIDTH;
                int yPos0 = item.startY() * v.getHeight() / DensityUtil.DEFAULT_HEIGHT;
                int xPos1 = item.endX() * v.getWidth() / DensityUtil.DEFAULT_WIDTH;
                int yPos1 = item.endY() * v.getHeight() / DensityUtil.DEFAULT_HEIGHT;
                if ((xPos0 < event.getX()) && (yPos0 < event.getY())
                        && (xPos1 > event.getX()) && (yPos1 > (event.getY()))) {
                    eventFlag = 1;
                    Log.e(TAG, "onTouchEvent page for event.getX() " + event.getX());
                    Log.e(TAG, "onTouchEvent page for event.getY() " + event.getY());
                    Log.e(TAG, "onTouchEvent page for xPos0 " + xPos0);
                    Log.e(TAG, "onTouchEvent page for yPos0 " + yPos0);
                    Log.e(TAG, "onTouchEvent page for xPos1 " + xPos1);
                    Log.e(TAG, "onTouchEvent page for yPos1 " + yPos1);
                    Log.e(TAG, "============== onTouchEvent click hit. ");
//                    Log.e(TAG, "onTouchEvent page for item.startX() " + item.startX());
//                    Log.e(TAG, "onTouchEvent page for item.startY() " + item.startY());
//                    Log.e(TAG, "onTouchEvent page for item.endX() " + item.endX());
//                    Log.e(TAG, "onTouchEvent page for item.endY() " + item.endY());
                    float timing = item.timing() * 1000;
                    float timed = item.endTiming() * 1000;
                    if (!TextUtils.isEmpty(item.imgWords()) && (mImgWords.get(item.imgWords()) != null)
                            && (mImgWords.get(item.imgWords()).size() > 1)) {
                        List<VoaText> mVoaWords = mImgWords.get(item.imgWords());
                        StringBuilder stringBuilder = new StringBuilder(64);
//                        for (VoaText voa : mVoaWords) {
//                            stringBuilder.append(voa.sentenceCn());
//                            if (voa.timing() * 1000 < timing) {
//                                timing = voa.timing() * 1000;
//                            }
//                            if (voa.endTiming() * 1000 > timed) {
//                                timed = voa.endTiming() * 1000;
//                            }
//                        }
                        for (int i = 0; i < mVoaWords.size(); i++) {
                            VoaText voa = mVoaWords.get(i);
                            stringBuilder.append(voa.sentenceCn());
                            if (voa.timing() * 1000 < timing) {
                                timing = voa.timing() * 1000;
                            }
                            if (voa.endTiming() * 1000 > timed) {
                                timed = voa.endTiming() * 1000;
                            }

                            //如果不是最后一个，则增加空格
                            if (i != mVoaWords.size() - 1) {
                                stringBuilder.append("\n");
                            }
                        }
                        readTips = stringBuilder.toString();
                        Log.e(TAG, "onTouchEvent timing endTiming more than 1 items " + mVoaWords.size());
                        Log.e(TAG, "onTouchEvent timing endTiming more than 1 items readTips " + readTips);
                    } else {
                        for (int i = 0; i < mVoaTextList.size(); ++i) {
                            VoaText voa = mVoaTextList.get(i);
                            if ((voa.paraId() == item.paraId()) && (voa.idIndex() == item.idIndex())) {
                                if (i < (mVoaTextList.size() - 1)) {
                                    voa = mVoaTextList.get(i + 1);
//                                    timed = voa.timing() * 1000;
                                }
                                Log.e(TAG, "onTouchEvent endTiming by next id, timed " + timed);
                                break;
                            }
                        }
                    }

                    //音频播放
//                    if (myIjkPlayer != null) {
//                        try {
//                            if (myIjkPlayer.isAlreadyGetPrepared()) {
//                                myIjkPlayer.seekTo((int) timing);
//                                if (!myIjkPlayer.isPlaying()) {
//                                    myIjkPlayer.start();
//                                }
//                            } else {
//                                final int seekTime = (int) timing;
//                                myIjkPlayer.setOnPreparedListener(mp -> {
//                                    Log.e(TAG, "initPlayer setOnPreparedListener seekTime " + seekTime);
//                                    myIjkPlayer.seekTo(seekTime);
//                                });
//                                myIjkPlayer.initialize(audioUrl);
//                                myIjkPlayer.prepareAndPlay();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        Log.e(TAG, "onTouchEvent page play duration " + (timed - timing));
//                        if (handler.hasMessages(1024)) {
//                            handler.removeMessages(1024);
//                        }
//                        handler.sendEmptyMessageDelayed(1024, (int) (timed - timing));
//                    }

                    if (exoPlayer!=null){
                        startPlayer((long) timing,(long) timed);
                    }

                    binding.imageGuide.setX(xPos0);
                    binding.imageGuide.setY(yPos0);
                    ViewGroup.LayoutParams params = binding.imageGuide.getLayoutParams();
                    params.width = xPos1 - xPos0;
                    params.height = (int) ((yPos1 - yPos0) * 1.3);
                    binding.imageGuide.setLayoutParams(params);
                    binding.imageGuide.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(readTips)) {
//                        showToastLong(readTips);
                        binding.imageContent.setText(readTips);
                        binding.imageContent.setVisibility(View.VISIBLE);
                    }
                    break;
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
        } else if (action == MotionEvent.ACTION_UP) {
        } else if (action == MotionEvent.ACTION_CANCEL) {
        }
        return true;
    }

    //是否缓冲完成
    private boolean isPrepare = false;
    //播放标识位
    private String playTag = "playTag";

    private void initPlayer() {
//        if (myIjkPlayer == null) {
//            myIjkPlayer = new ExtendedPlayer(mContext);
//        }

        exoPlayer = null;
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(getActivity()).build();
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    switch (playbackState) {
                        case Player.STATE_READY:
                            isPrepare = true;
                            break;
                        case Player.STATE_ENDED:
                            break;
                    }
                }

                @Override
                public void onPlayerError(PlaybackException error) {
                    ToastUtil.showToast(getActivity(), "加载音频失败，请退出后重试");
                }
            });

            MediaItem mediaItem = MediaItem.fromUri(audioUrl);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        }
    }

    private void startPlayer(long startTime, long endTime) {
        if (exoPlayer != null) {
            pausePlayer();

            exoPlayer.seekTo(startTime);
            exoPlayer.play();

            RxTimer.getInstance().multiTimerInMain(playTag, 0, 300L, new RxTimer.RxActionListener() {
                @Override
                public void onAction(long number) {
                    long curPlayTime = exoPlayer.getCurrentPosition();

                    if (curPlayTime >= endTime) {
                        pausePlayer();
                    }
                }
            });
        }
    }

    public void pausePlayer() {
        eventFlag = 0;
//        if (myIjkPlayer != null) {
//            try {
//                if (myIjkPlayer.isPlaying()) {
//                    myIjkPlayer.pause();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        if (exoPlayer != null) {
            if (exoPlayer.isPlaying()) {
                exoPlayer.pause();
            }
        }

        if (binding != null) {
            if (binding.imageGuide != null) {
                binding.imageGuide.setVisibility(View.INVISIBLE);
            }
            if (binding.imageContent != null) {
                binding.imageContent.setVisibility(View.INVISIBLE);
            }
        }

        RxTimer.getInstance().cancelTimer(playTag);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(mContext);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);
        pausePlayer();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //解除绑定
        EventBus.getDefault().unregister(this);

//        if (myIjkPlayer != null) {
//            try {
//                myIjkPlayer.stopAndRelease();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        if (exoPlayer != null) {
            exoPlayer.stop();
        }
        RxTimer.getInstance().cancelTimer(playTag);

        showEmptyTexts();
        mPresenter.detachView();
    }

    private void initClick() {
        binding.imagePre.setOnClickListener(v -> formerClick());
        binding.imageNext.setOnClickListener(v -> nextClick());
    }

    private synchronized void formerClick() {
        if ((mImgString == null) || (mImgString.size() < 1)) {
            return;
        }
        if (scrollFlag > 0) {
            --scrollFlag;
            GlideUtil.setImage(Constant.Url.VOA_IMG_BASE + mImgString.get(scrollFlag), mContext, R.drawable.ic_diandu_default, binding.imageShow);
            Log.e(TAG, "formerClick imgPath = " + Constant.Url.VOA_IMG_BASE + mImgString.get(scrollFlag));
            if (isTagHide){
                binding.imageNext.setVisibility(View.VISIBLE);
                binding.imageNext.setBackground(getResources().getDrawable(R.drawable.shape_green_button));
            }
        } else {
            showToastShort("当前已经是第一张图片了");
        }
        if (scrollFlag == 0) {
            //根据要求隐藏
            if (isTagHide) {
                binding.imagePre.setVisibility(View.INVISIBLE);
            } else {
                binding.imagePre.setVisibility(View.VISIBLE);
                binding.imagePre.setBackground(getResources().getDrawable(R.drawable.shape_grey_button));

            }

            binding.imageNext.setVisibility(View.VISIBLE);
            binding.imageNext.setBackground(getResources().getDrawable(R.drawable.shape_green_button));
        }
        pausePlayer();
    }

    private synchronized void nextClick() {
        if ((mImgString == null) || (mImgString.size() < 1)) {
            return;
        }
        if (scrollFlag < (mImgString.size() - 1)) {
            ++scrollFlag;
            GlideUtil.setImage(Constant.Url.VOA_IMG_BASE + mImgString.get(scrollFlag), mContext, R.drawable.ic_diandu_default, binding.imageShow);
            Log.e(TAG, "nextClick imgPath = " + Constant.Url.VOA_IMG_BASE + mImgString.get(scrollFlag));
            if (isTagHide){
                binding.imagePre.setVisibility(View.VISIBLE);
                binding.imagePre.setBackground(getResources().getDrawable(R.drawable.shape_green_button));
            }
        } else {
            showToastShort("当前已经是最后一张图片了");
        }
        if (scrollFlag == (mImgString.size() - 1)) {
            binding.imagePre.setVisibility(View.VISIBLE);
            binding.imagePre.setBackground(getResources().getDrawable(R.drawable.shape_green_button));
            //根据要求隐藏
            if (isTagHide) {
                binding.imageNext.setVisibility(View.INVISIBLE);
            } else {
                binding.imageNext.setVisibility(View.VISIBLE);
                binding.imageNext.setBackground(getResources().getDrawable(R.drawable.shape_grey_button));
            }
        }
        pausePlayer();
    }

    @Override
    public void showVoaTexts(List<VoaText> voaTextList) {
        scrollFlag = 0;
        eventFlag = 0;
        mVoaTextList.clear();
        mImgPaths.clear();
        mImgString.clear();
        mImgWords.clear();
        if ((voaTextList == null) || (voaTextList.size() < 1)) {
            Log.e(TAG, "showVoaTexts is null.");
        } else {
            mVoaTextList.addAll(voaTextList);
            for (VoaText item : voaTextList) {
                if ((item == null) || TextUtils.isEmpty(item.imgPath())) {
                    continue;
                }
                if (mImgPaths.containsKey(item.imgPath().trim())) {
                    List<VoaText> voaTexts = mImgPaths.get(item.imgPath().trim());
                    voaTexts.add(item);
                } else {
                    List<VoaText> voaTexts = new ArrayList<>();
                    voaTexts.add(item);
                    mImgPaths.put(item.imgPath().trim(), voaTexts);
                }
                if (!TextUtils.isEmpty(item.imgWords())) {
                    if (mImgWords.containsKey(item.imgWords())) {
                        List<VoaText> voaTexts = mImgWords.get(item.imgWords());
                        voaTexts.add(item);
                    } else {
                        List<VoaText> voaTexts = new ArrayList<>();
                        voaTexts.add(item);
                        mImgWords.put(item.imgWords(), voaTexts);
                    }
                }
            }
        }
        if ((mImgPaths != null) && (mImgPaths.size() > 0)) {
            for (String key : mImgPaths.keySet()) {
                mImgString.add(key);
            }
            Collections.sort(mImgString, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
        }
        if ((mImgString == null) || (mImgString.size() < 1)) {
            Log.e(TAG, "showVoaTexts mImgString is null. ");
            //根据要求，隐藏处理
            if (isTagHide) {
                binding.imagePre.setVisibility(View.INVISIBLE);
                binding.imageNext.setVisibility(View.INVISIBLE);
            } else {
                binding.imagePre.setVisibility(View.VISIBLE);
                binding.imageNext.setVisibility(View.VISIBLE);

                binding.imagePre.setBackground(getResources().getDrawable(R.drawable.shape_grey_button));
                binding.imageNext.setBackground(getResources().getDrawable(R.drawable.shape_grey_button));
                binding.imageNext.setClickable(false);
                binding.imagePre.setClickable(false);
            }
        } else {
            Log.e(TAG, "showVoaTexts mImgString size " + mImgString.size());
            GlideUtil.setImage(Constant.Url.VOA_IMG_BASE + mImgString.get(scrollFlag), mContext, R.drawable.ic_diandu_default, binding.imageShow);
            Log.e(TAG, "showVoaTexts imgPath = " + Constant.Url.VOA_IMG_BASE + mImgString.get(scrollFlag));
            if (isTagHide){
                binding.imagePre.setVisibility(View.INVISIBLE);
            }else {
                binding.imagePre.setVisibility(View.VISIBLE);
                binding.imagePre.setBackground(getResources().getDrawable(R.drawable.shape_grey_button));
            }
            if (mImgString.size() < 2) {
                if (isTagHide){
                    binding.imageNext.setVisibility(View.INVISIBLE);
                }else {
                    binding.imageNext.setVisibility(View.VISIBLE);
                    binding.imageNext.setBackground(getResources().getDrawable(R.drawable.shape_grey_button));
                    binding.imageNext.setClickable(false);
                    binding.imagePre.setClickable(false);
                }
            } else {
                binding.imageNext.setVisibility(View.VISIBLE);
                binding.imageNext.setBackground(getResources().getDrawable(R.drawable.shape_green_button));
            }
        }
    }

    @Override
    public void showEmptyTexts() {
        Log.e(TAG, "showEmptyTexts is empty.");
        scrollFlag = 0;
        eventFlag = 0;
        mVoaTextList.clear();
        mImgString.clear();
        mImgPaths.clear();
        mImgWords.clear();
    }

    public void showSpeed() {
        String[] items = new String[]{"0.5X", "0.75X", "1.0X", "1.25X", "1.5X", "1.75X", "2.0X"};
        new AlertDialog.Builder(mContext)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                changeSpeed(0.5f);
                                break;
                            case 1:
                                changeSpeed(0.75f);
                                break;
                            case 2:
                                changeSpeed(1.0f);
                                break;
                            case 3:
                                changeSpeed(1.25f);
                                break;
                            case 4:
                                changeSpeed(1.5f);
                                break;
                            case 5:
                                changeSpeed(1.75f);
                                break;
                            case 6:
                                changeSpeed(2.0f);
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void changeSpeed(float speed) {
//        if (mAccountManager.isVip()) {
//            myIjkPlayer.setSpeed(speed);
//            SPconfig.Instance().putFloat(Config.playerSpeed, speed);
//        } else {
//            Toast.makeText(mContext, "成为VIP用户即可调节播放速度！", Toast.LENGTH_SHORT).show();
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(RefreshEvent event) {
        int voaId = SPconfig.Instance().loadInt(Config.currVoaId);
        if (Playmanager.getInstance().getVoaFromList(voaId) != null) {
            Log.e("EvalrankFragment", "RefreshEvent  ----------");
            mVoa = Playmanager.getInstance().getVoaFromList(voaId);

            //获取播放的音频
            audioUrl = Constant.getSoundMp3Url(mVoa.sound(), mVoa.voaId());
            //这里在公司的测试机上出现了音频不对应的问题，当前直接采用网络数据进行播放显示，暂时不用本地数据
//            File audioFile = StorageUtil.getAudioFile(TalkShowApplication.getInstance(), mVoa.voaId());
//            if (audioFile.exists()) {
//                audioUrl = audioFile.getAbsolutePath();
//            }
            //这里需要重新初始化myijkplayer
//            myIjkPlayer = new ExtendedPlayer(mContext);

            //初始化操作
        }
    }

    //设置播放标志
    public void setCanPlay(boolean isPlay){
        this.isCanPlay = isPlay;

        if (!isCanPlay){
            pausePlayer();
        }
    }
}
