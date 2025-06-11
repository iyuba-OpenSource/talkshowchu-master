//package com.iyuba.talkshow.newce.study.read;
//
//import android.annotation.SuppressLint;
//import android.content.DialogInterface;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.animation.Animation;
//import android.view.animation.TranslateAnimation;
//import android.widget.ImageView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.iyuba.talkshow.Constant;
//import com.iyuba.talkshow.R;
//import com.iyuba.talkshow.TalkShowApplication;
//import com.iyuba.talkshow.data.BackgroundManager;
//import com.iyuba.talkshow.data.DataManager;
//import com.iyuba.talkshow.data.manager.ConfigManager;
//import com.iyuba.talkshow.data.model.ArticleRecord;
//import com.iyuba.talkshow.data.model.Voa;
//import com.iyuba.talkshow.data.model.VoaText;
//import com.iyuba.talkshow.databinding.FragmentReadBinding;
//import com.iyuba.talkshow.event.PlayListEvent;
//import com.iyuba.talkshow.event.StudyPlayRefreshEvent;
//import com.iyuba.talkshow.event.StudyReportEvent;
//import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
//import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
//import com.iyuba.talkshow.lil.user.UserInfoManager;
//import com.iyuba.talkshow.newce.study.StudyActivity;
//import com.iyuba.talkshow.newdata.Config;
//import com.iyuba.talkshow.newdata.MyIjkPlayer;
//import com.iyuba.talkshow.newdata.OnPlayStateChangedListener;
//import com.iyuba.talkshow.newdata.PlayFlag;
//import com.iyuba.talkshow.newdata.Playmanager;
//import com.iyuba.talkshow.newdata.ResourceUtil;
//import com.iyuba.talkshow.newdata.SPconfig;
//import com.iyuba.talkshow.newview.CenterLayoutManager;
//import com.iyuba.talkshow.newview.TextPageSelectTextCallBack;
//import com.iyuba.talkshow.ui.base.BaseViewBindingFragmet;
//import com.iyuba.talkshow.util.NetStateUtil;
//import com.iyuba.talkshow.util.StorageUtil;
//import com.iyuba.talkshow.util.ToastUtil;
//import com.iyuba.talkshow.util.UploadStudyRecordUtil;
//import com.umeng.analytics.MobclickAgent;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import javax.inject.Inject;
//
///**
// * Created by carl shen on 2020/7/29
// * New Primary English, new study experience.
// */
//public class ReadFragment extends BaseViewBindingFragmet<FragmentReadBinding> implements ReadMvpView {
//    public static final String TAG = "ReadFragment";
//    private static final String Article = "article";
//    @Inject
//    ConfigManager configManager;
//    @Inject
//    public DataManager mManager;
//    @Inject
//    public ReadPresenter mPresenter;
//    @Inject
//    public ReadAdapter mAdapter;
//
//    private FragmentReadBinding binding ;
//    private final List<VoaText> mVoaTextList = new ArrayList<>();
//    private Voa mVoa;
//    private MyIjkPlayer myIjkPlayer;
//    private String audioUrl;
//    private int currCourseId;
//    private float totalTime = 0;
//    private float startTime = 0;
//    private double currTime = 0;
//    private int position = 0;
//    private int unitId = 0;
//    private UploadStudyRecordUtil studyRecordUpdateUtil;
//    private CenterLayoutManager centerLayout;
//
//    private final TextPageSelectTextCallBack tpscb = new TextPageSelectTextCallBack() {
//        @Override
//        public void selectTextEvent(String selectText) {
//            if (selectText.matches("^[a-zA-Z'-]*.{1}")) {
//                String regEx = "[^a-zA-Z'-]";
//                Pattern p = Pattern.compile(regEx);
//                Matcher m = p.matcher(selectText);
//                selectText = m.replaceAll("").trim();
//                binding.wordcard.searchWord(selectText);
//                TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
//                        0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                        -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
//                mShowAction.setDuration(500);
//                binding.wordcard.startAnimation(mShowAction);
//                binding.wordcard.setVisibility(View.VISIBLE);
//            } else {
//                ToastUtil.showToast(mContext, ResourceUtil.getString(mContext, R.string.take_english_words));
//            }
//        }
//
//        @Override
//        public void selectParagraph(int paragraph) {
//        }
//
//        @Override
//        public void cancelWordCard() {
//            if (binding.wordcard.getVisibility() == View.VISIBLE) {
//                TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
//                        0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                        0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
//                mShowAction.setDuration(500);
//                binding.wordcard.startAnimation(mShowAction);
//                binding.wordcard.setVisibility(View.GONE);
//            }
//        }
//    };
//
//    private volatile int scrollFlag = 0;
//    @SuppressLint("HandlerLeak")
//    private final Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (((StudyActivity) mContext).isDestroyed() && !ReadFragment.this.isAdded()) {
//                return;
//            }
//            if ((myIjkPlayer == null)) {
//                return;
//            }
//            switch (msg.what) {
//                case 0:
//                    try {
//                        binding.curTime.setText(myIjkPlayer.getAudioCurrTime());
//                        binding.totalTime.setText(myIjkPlayer.getAudioAllTime());
//                        binding.seekBar.setMax((int) myIjkPlayer.getDuration());
//                        binding.seekBar.setProgress((int) myIjkPlayer.getCurrentPosition());
//                        currTime = 1.00 * myIjkPlayer.getCurrentPosition() / 1000;
//                        viewScroll(currTime);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    handler.sendEmptyMessageDelayed(0, 500);
////                    if (myIjkPlayer.getCurrentPosition() < myIjkPlayer.getDuration()) {
////                        handler.sendEmptyMessageDelayed(4, 200L);
////                    } else {
////                        handler.removeMessages(4);
////                        binding.videoPlay.setImageResource(R.mipmap.image_play);
////                        try {
////                            myIjkPlayer.pause();
////                        } catch (Exception e) {
////                            e.printStackTrace();
////                        }
////                    }
//                    break;
//                case 1:
//                    try {
//                        binding.curTime.setText(myIjkPlayer.getAudioAllTime());
//                        binding.totalTime.setText(myIjkPlayer.getAudioAllTime());
//                        binding.seekBar.setMax((int) myIjkPlayer.getDuration());
//                        binding.seekBar.setProgress((int) myIjkPlayer.getDuration());
//                        binding.videoPlay.setImageResource(R.mipmap.image_play);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                case 10:
//                    ToastUtil.showToast(mContext, "区间播放已取消");
//                    handler.removeMessages(11);// 手动停止A-B播放
//                    break;
//                case 11:
//                    myIjkPlayer.seekTo(aPosition);// A-B播放
//                    handler.sendEmptyMessageDelayed(11, bPosition - aPosition + 300);
//                    break;
//                case 112:
//                    EventBus.getDefault().post(new StudyReportEvent(0,TypeLibrary.RefreshDataType.study_read));
//                    break;
//                case 12:
//                    ToastUtil.showToast(getContext(),"无网络连接,无法播放");
//                    break;
//            }
//        }
//    };
//
//    public static ReadFragment newInstance(Voa voa, int pos, int unit) {
//        ReadFragment readFragment = new ReadFragment();
//        Bundle args = new Bundle();
//        args.putParcelable(Article, voa);
//        args.putInt(StudyActivity.POS, pos);
//        args.putInt(StudyActivity.UNIT, unit);
//        readFragment.setArguments(args);
//        return readFragment;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        fragmentComponent().inject(this);
//        mPresenter.attachView(this);
//        EventBus.getDefault().register(this);
//        mVoa = getArguments().getParcelable(Article);
//        position = getArguments().getInt(StudyActivity.POS, 0);
//        unitId = getArguments().getInt(StudyActivity.UNIT, 0);
//        //获取播放器（下面这个当前暂时没有输入的数据，意味着全都是第一种情况处理）
////        boolean isAmerican = SPconfig.Instance().loadBoolean(Config.ISAMEICAN, true);
////        if (isAmerican) {
////            audioUrl = Constant.Web.sound_vip + mVoa.voaId() / 1000 + "_" + mVoa.voaId() % 1000 + ".mp3";
////        } else {
////            audioUrl = Constant.Web.sound_vip + "british/" + mVoa.voaId() / 1000 + "/" + mVoa.voaId() / 1000 + "_" + mVoa.voaId() % 1000 + ".mp3";
////        }
//        audioUrl = Constant.getSoundMp3Url(mVoa.sound(), mVoa.voaId());
//        Log.e(TAG, "voa audioUrl " + audioUrl);
//        File audioFile = StorageUtil.getAudioFile(TalkShowApplication.getInstance(), mVoa.voaId());
//        if (audioFile.exists()) {
//            Log.e(TAG, "voa audioFile " + audioFile.getAbsolutePath());
//            audioUrl = audioFile.getAbsolutePath();
//        }
//
//        Log.e(TAG, "reaal voa audioUrl " + audioUrl);
//
//        currCourseId = mVoa.voaId();
//        SPconfig.Instance().putInt(Config.currVoaId, currCourseId);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        binding = FragmentReadBinding.inflate(getLayoutInflater(),container,false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        initPlayer();
//    }
//
//    @Override
//    public void init() {
//        mVoaTextList.clear();
//        mPresenter.getVoaTexts(mVoa.voaId());
//
//        mAdapter.SetTextPageCallback(tpscb);
//        centerLayout = new CenterLayoutManager(mContext);
//        binding.recyclerView.setLayoutManager(centerLayout);
//        binding.recyclerView.setAdapter(mAdapter);
//        binding.wordcard.voa = mVoa.voaId();
//        binding.wordcard.unit = unitId;
//        binding.wordcard.book = configManager.getCourseId();
//        initClick();
//        startStudyRecord();
//    }
//
//    private void initPlayer() {
//        if (Constant.PlayerService) {
//            if (BackgroundManager.Instace().bindService == null) {
//                Log.e(TAG, "initPlayer bindService is null?? ");
//            } else {
//                myIjkPlayer = BackgroundManager.Instace().bindService.getPlayer();
//            }
//        }
//        if (myIjkPlayer == null) {
//            myIjkPlayer = MyIjkPlayer.getInstance();
//        }
//        Log.e(TAG, "initPlayer  ----------");
//
//        Log.e(TAG, "initPlayer: 1-------"+(myIjkPlayer==null)+"---"+myIjkPlayer.getDataSource());
//        if (Constant.PlayerService) {
//            Log.e(TAG, "initPlayer  2----------"+(mVoa==null));
//
//            if (BackgroundManager.Instace().bindService == null) {
//                Log.e(TAG, "-------------------bindService is null?? ");
//                return;
//            }
//
//            Log.e(TAG, "initPlayer  2.5----------"+(mVoa.voaId()));
//
//            if (mVoa.voaId() == BackgroundManager.Instace().bindService.getTag()) {
//                Log.e(TAG, "initPlayer  3----------");
//                if (myIjkPlayer.isPlaying()) {
//                    Log.e(TAG, "-------------------myIjkPlayer is isPlaying, go on.");
//                } else {
//                    Log.e(TAG, "initPlayer  4----------");
//                    if (NetStateUtil.isConnected(TalkShowApplication.getContext()) || StorageUtil.getAudioFile(TalkShowApplication.getInstance(), mVoa.voaId()).exists()) {
//                        Log.e(TAG, "initPlayer  4.6----------"+(myIjkPlayer==null)+"---"+audioUrl);
//
//                        myIjkPlayer.initialize(audioUrl);
//                    } else {
//                        Log.e(TAG, "initPlayer  4.7----------");
//
//                        myIjkPlayer.pause();
//                    }
//
//                    Log.e(TAG, "initPlayer  4.5----------");
//                    BackgroundManager.Instace().bindService.startTime = System.currentTimeMillis();
//                }
//            } else {
//                Log.e(TAG, "initPlayer  3.5----------"+(myIjkPlayer==null));
//
//                if (NetStateUtil.isConnected(TalkShowApplication.getContext()) || StorageUtil.getAudioFile(TalkShowApplication.getInstance(), mVoa.voaId()).exists()) {
//                    myIjkPlayer.initialize(audioUrl);
//                } else {
//                    myIjkPlayer.pause();
//                }
//                BackgroundManager.Instace().bindService.setTag(mVoa.voaId());
//                BackgroundManager.Instace().bindService.startTime = System.currentTimeMillis();
//            }
//        } else {
//            Log.e(TAG, "initPlayer: -----数据需要重新处理");
//
//            if (NetStateUtil.isConnected(TalkShowApplication.getContext()) || StorageUtil.getAudioFile(TalkShowApplication.getInstance(), mVoa.voaId()).exists()) {
//                myIjkPlayer.initialize(audioUrl);
//            } else {
//                myIjkPlayer.pause();
//            }
//        }
//    }
//
//    private void initPlayViews() {
//        //中英切换显示
//        boolean isShowCn = SPconfig.Instance().loadBoolean(Config.ISSHOWCN);
//        if (isShowCn) {
//            binding.CHN.setImageResource(R.mipmap.show_cn);
//        } else {
//            binding.CHN.setImageResource(R.mipmap.show_en);
//        }
//        //播放模式显示
//        int playMode = SPconfig.Instance().loadInt(Config.playMode);
//        switch (playMode) {
//            case 0:
//                binding.imgPlayMode.setImageResource(R.mipmap.img_mode0);
//                break;
//            case 1:
//                binding.imgPlayMode.setImageResource(R.mipmap.img_mode1);
//                break;
//            case 2:
//                binding.imgPlayMode.setImageResource(R.mipmap.img_mode2);
//                break;
//        }
//        int currId = SPconfig.Instance().loadInt(Config.currVoaId);
//        if (currId != currCourseId) {
//            if (NetStateUtil.isConnected(TalkShowApplication.getContext())) {
//                myIjkPlayer.initialize(audioUrl);
//            }
//        } else {
//            binding.totalTime.setText(myIjkPlayer.getAudioAllTime());
//            binding.curTime.setText(myIjkPlayer.getAudioCurrTime());
//            currTime = 1.00 * myIjkPlayer.getCurrentPosition() / 1000;
//            binding.seekBar.setMax((int) myIjkPlayer.getDuration());
//            binding.seekBar.setProgress((int) myIjkPlayer.getCurrentPosition());
//            viewScroll(currTime);
//        }
////        setSpeed();
//        if (myIjkPlayer.isPlaying()) {
//            handler.sendEmptyMessage(0);
//            binding.videoPlay.setImageResource(R.mipmap.image_pause);
//        } else {
//            binding.videoPlay.setImageResource(R.mipmap.image_play);
//        }
//
//        myIjkPlayer.setOpscl(new OnPlayStateChangedListener() {
//            @Override
//            public void playSuccess() {
//                if (!((StudyActivity) mContext).isDestroyed()) {
//                    binding.videoPlay.setImageResource(R.mipmap.image_pause);
//                    //显示通知
//                    BackgroundManager.Instace().bindService.updateNotification(mVoa,true);
//
//                    handler.sendEmptyMessage(0);
//                }
//                Log.e(TAG, "-------------------playSuccess position " + position);
//                if (position > 0) {
//                    StopPlayer();
//                    position = 0;
//                }
//                //替换到播放完成的位置
////                SPconfig.Instance().putInt(Config.currVoaId, currCourseId);
////                EventBus.getDefault().post(new RefreshEvent());
//                MyIjkPlayer.playFlag = PlayFlag.play;
//                setSpeed();
//            }
//
//            @Override
//            public void playFaild() {
//                Log.e(TAG, "-------------------playFaild nextId ");
//
//                //显示通知
//                BackgroundManager.Instace().bindService.updateNotification(mVoa,false);
//            }
//
//            @Override
//            public void playCompletion() {
//                Log.e(TAG, "-------------------playCompletion  audioUrl= " + audioUrl);
//                Log.e(TAG, "-------------------getDuration  " + myIjkPlayer.getDuration());
//
//                //显示通知
//                BackgroundManager.Instace().bindService.updateNotification(mVoa,false);
//
//                if (myIjkPlayer.getDuration() == 0) {
//                    if (NetStateUtil.isConnected(TalkShowApplication.getContext())) {
//                        ToastUtil.showToast(mContext, "暂时没有播放的数据，请稍后再试，或者联系客服。");
//                    } else {
//                        ToastUtil.showToast(mContext, "播放的数据需要访问数据网络。");
//                    }
//                    return;
//                }
//                scrollFlag = 0;
//
//                if (!((StudyActivity) mContext).isDestroyed()) {
//                    handler.removeMessages(0);
//                    handler.sendEmptyMessage(1);
//                    mAdapter.setLightPosition(0);
//
//                    //显示学习报告的弹窗
////                    if (configManager.isStudyReport()) {
////                        EventBus.getDefault().post(new StudyReportEvent(1));
////                        handler.sendEmptyMessageDelayed(112, 5000);
////                    }
//
//                }
//
//                MyIjkPlayer.playFlag = PlayFlag.pause;
//
//                currCourseId = Playmanager.getInstance().setNextVoaId();
//                BackgroundManager.Instace().bindService.setTag(currCourseId);
//                Log.e(TAG, "playCompletion nextId " + currCourseId);
//                SPconfig.Instance().putInt(Config.currVoaId, currCourseId);
//
//                if (NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
//
//                    //准备显示学习报告界面
//                    if (UserInfoManager.getInstance().isLogin()){
//                        EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.read_refresh_tips,"0"));
//                    }else {
//                        EventBus.getDefault().post(new StudyPlayRefreshEvent());
//                    }
//
//                    //发送学习报告信息
//                    stopStudyRecord("1", myIjkPlayer.getCurrentPosition(), myIjkPlayer.getDuration());
//                    // collect data and report
//                    mPresenter.saveArticleRecord(ArticleRecord.builder().setUid(UserInfoManager.getInstance().getUserId())
//                            .setVoa_id(mVoa.voaId())
//                            .setCurr_time((int) myIjkPlayer.getCurrentPosition()/1000)
//                            .setTotal_time((int) myIjkPlayer.getDuration()/1000)
//                            .setType(0).setIs_finish(1)
//                            .setPercent(100)
//                            .build());
//                } else {
//                    //直接刷新下一个
//                    EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_toast,""));
//                }
//            }
//
//            @Override
//            public void playPause() {
//                if (!((StudyActivity) mContext).isDestroyed()) {
//                    binding.videoPlay.setImageResource(R.mipmap.image_play);
//                    handler.removeMessages(0);
//                }
//                MyIjkPlayer.playFlag = PlayFlag.pause;
//
//                //显示通知
//                BackgroundManager.Instace().bindService.updateNotification(mVoa,false);
//            }
//
//            @Override
//            public void playStart() {
//				if (!getUserVisibleHint()){
//                    return;
//                }
//                if (!((StudyActivity) mContext).isDestroyed()) {
//                    binding.videoPlay.setImageResource(R.mipmap.image_pause);
//                    handler.sendEmptyMessageDelayed(0, 500);
//                }
//                MyIjkPlayer.playFlag = PlayFlag.play;
//
//                //显示通知
//                BackgroundManager.Instace().bindService.updateNotification(mVoa,true);
//            }
//
//            @Override
//            public void bufferingUpdate(int progress) {
//            }
//        });
//        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//                    myIjkPlayer.seekTo(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
//        // auto text scroll
//        syncho = SPconfig.Instance().loadBoolean(Config.playPosition, false);
//        if (syncho) {
//            binding.ivSync.setImageResource(R.drawable.icon_sync);
//        } else {
//            binding.ivSync.setImageResource(R.drawable.icon_unsync);
//        }
//        binding.seekbarSpeed.setProgress((int) (SPconfig.Instance().loadFloat(Config.playerSpeed) * 10));
//        binding.seekbarSpeed.getParent().requestDisallowInterceptTouchEvent(true);
//        binding.seekbarSpeed.setOnSeekBarChangeListener(
//                new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        Log.e(TAG, " OnSeekBarChangeListener  progress " + progress);
////                        if (mAccountManager.isVip()) {
////                            float speed = (float) ((progress + 5) / 10.0);
////                            SPconfig.Instance().putFloat(Config.playerSpeed, speed);
////                            myIjkPlayer.setSpeed(speed);
////                            binding.textPlaySpeed10.setText(String.format("倍速 %sx", speed));
////                        } else {
////                            ToastUtil.showToast(mContext, "开通VIP后可使用调速功能");
////                        }
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//                    }
//                }
//        );
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onResume(mContext);
//        initPlayViews();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.e(TAG, "onPause  ----------");
//        MobclickAgent.onPause(mContext);
//    }
//
//    public void StopPlayer() {
//        if (myIjkPlayer!=null&&!myIjkPlayer.isPlaying()){
//            return;
//        }
//
//        if ((myIjkPlayer != null) && myIjkPlayer.isPlaying()) {
//            myIjkPlayer.pause();
//
//            if (position == 0) {
//                // collect data and report
//                if (NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
//                    stopStudyRecord("0", myIjkPlayer.getCurrentPosition(), myIjkPlayer.getDuration());
//                    Log.e(TAG, "StopPlayer save  ArticleRecord    ---- ");
//                    mPresenter.saveArticleRecord(ArticleRecord.builder().setUid(UserInfoManager.getInstance().getUserId())
//                            .setVoa_id(mVoa.voaId())
//                            .setCurr_time((int) myIjkPlayer.getCurrentPosition() / 1000)
//                            .setTotal_time((int) myIjkPlayer.getDuration() / 1000)
//                            .setType(0).setIs_finish(0)
//                            .setPercent((int) (100 * myIjkPlayer.getCurrentPosition() / myIjkPlayer.getDuration()))
//                            .build());
//                }
//            }
//        }
//        if ((binding != null) && (binding.videoPlay != null)) {
//            binding.videoPlay.setImageResource(R.mipmap.image_play);
//        }
//        if (handler != null) {
//            handler.removeMessages(0);
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if ((myIjkPlayer != null) && myIjkPlayer.isPlaying()) {
//            if (Constant.PlayerService&&isAutoPlay) {
//                Log.e(TAG, "onDestroyView no need pause for play bar.");
//            } else {
//                myIjkPlayer.pause();
//
//                // collect data and report
//                if (NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
//                    stopStudyRecord("0", myIjkPlayer.getCurrentPosition(), myIjkPlayer.getDuration());
//                    Log.e(TAG, "onDestroyView save  ArticleRecord    ---- ");
//                    TalkShowApplication.getSubHandler().post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mManager.saveArticleRecordNew(ArticleRecord.builder().setUid(UserInfoManager.getInstance().getUserId())
//                                    .setVoa_id(mVoa.voaId())
//                                    .setCurr_time((int) myIjkPlayer.getCurrentPosition()/1000)
//                                    .setTotal_time((int) myIjkPlayer.getDuration()/1000)
//                                    .setType(0).setIs_finish(0)
//                                    .setPercent((int) (100 * myIjkPlayer.getCurrentPosition() / myIjkPlayer.getDuration()))
//                                    .build());
//                        }
//                    });
//                }
//            }
//        }
//        if (handler != null) {
//            handler.removeMessages(0);
//            handler.removeCallbacksAndMessages(null);
//        }
//        mAdapter.SetVoaList(new ArrayList<>());
//        mPresenter.detachView();
//        EventBus.getDefault().unregister(this);
//    }
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(PlayListEvent event) {
//        Log.e(TAG, "PlayListEvent to pause player. ");
//        StopPlayer();
//    }
//
//    public void startStudyRecord() {
//        studyRecordUpdateUtil = new UploadStudyRecordUtil(UserInfoManager.getInstance().isLogin(), TalkShowApplication.getInstance(), UserInfoManager.getInstance().getUserId(), mVoa.voaId(), "1", "1");
//    }
//
//    public void stopStudyRecord(String flag, long beginTime, long endTime) {
//        Log.e(TAG, " stopStudyRecord  flag " + flag);
////        studyRecordUpdateUtil.getStudyRecord().setStarttime(beginTime);
////        studyRecordUpdateUtil.getStudyRecord().setEndtime(endTime);
//        studyRecordUpdateUtil.stopStudyRecord(TalkShowApplication.getInstance(), UserInfoManager.getInstance().isLogin(), flag, mPresenter.getUploadStudyRecordService());
//    }
//
//    private void initClick() {
//        binding.reCHN.setOnClickListener(v -> reChnClick());
//        binding.formerButton.setOnClickListener(v -> reFromerClick());
//        binding.videoPlay.setOnClickListener(v -> reVideoClick());
//        binding.nextButton.setOnClickListener(v -> reNextClick());
//        binding.reOneVideo.setOnClickListener(v -> setPlayMode());
//        binding.abplay.setOnClickListener(v -> abPlay());
//        binding.reSync.setOnClickListener(v -> syncText());
//        binding.textPlaySpeed10.setOnClickListener(v -> setSpeedText());
//        //修改界面
////        binding.rePlayMode.setOnClickListener(v -> setMoreFunction());
//        binding.tvSpeed.setOnClickListener(v->setSpeedText());
//    }
//
//    private void reChnClick() {
//        boolean isShowCn = SPconfig.Instance().loadBoolean(Config.ISSHOWCN);
//        SPconfig.Instance().putBoolean(Config.ISSHOWCN, !isShowCn);
//
//        if (SPconfig.Instance().loadBoolean(Config.ISSHOWCN)) {
//            binding.CHN.setImageResource(R.mipmap.show_cn);
//        } else {
//            binding.CHN.setImageResource(R.mipmap.show_en);
//        }
//        mAdapter.refreshAdpter();
//    }
//
//    private void reVideoClick() {
//        Log.e(TAG, "reVideoClick     ---- initialize");
//        if (binding.wordcard != null) {
//            binding.wordcard.setVisibility(View.GONE);
//        }
//        if (myIjkPlayer != null) {
//            try {
//                if (myIjkPlayer.isPlaying()) {
//                    myIjkPlayer.pause();
//
//                    binding.videoPlay.setImageResource(R.mipmap.image_play);
//                    // collect data and report
//                    if (NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
//                        stopStudyRecord("0", myIjkPlayer.getCurrentPosition(), myIjkPlayer.getDuration());
//                        Log.e(TAG, "save  ArticleRecord    ---- ");
//                        mPresenter.saveArticleRecord(ArticleRecord.builder().setUid(UserInfoManager.getInstance().getUserId())
//                                .setVoa_id(mVoa.voaId())
//                                .setCurr_time((int) myIjkPlayer.getCurrentPosition()/1000)
//                                .setTotal_time((int) myIjkPlayer.getDuration()/1000)
//                                .setType(0).setIs_finish(0)
//                                .setPercent((int) (100 * myIjkPlayer.getCurrentPosition() / myIjkPlayer.getDuration()))
//                                .build());
//                    } else {
////                        ToastUtil.showToast(mContext, "原文评测上传需要数据网络。");
//                        Log.e(TAG, "reVideoClick can't send record as no data network.");
//                    }
//                } else {
//                    if (!NetStateUtil.isConnected(TalkShowApplication.getContext()) && !StorageUtil.getAudioFile(TalkShowApplication.getInstance(), mVoa.voaId()).exists()) {
//                        ToastUtil.showToast(mContext, "播放原文需要访问数据网络。");
//                        return;
//                    }
//                    if (TextUtils.isEmpty(myIjkPlayer.getDataSource())) {
//                        myIjkPlayer.setOnPreparedListener(mp -> {
//                            myIjkPlayer.play();
//                            binding.videoPlay.setImageResource(R.mipmap.image_pause);
//                            startStudyRecord();
//                        });
//                        myIjkPlayer.initialize(audioUrl);
//                    } else {
//                        myIjkPlayer.play();
//                        binding.videoPlay.setImageResource(R.mipmap.image_pause);
//                        startStudyRecord();
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            binding.videoPlay.setImageResource(R.mipmap.image_pause);
//            Log.e(TAG, "reVideoClick need initialize");
//        }
//    }
//
//    private synchronized void reFromerClick() {
//        if (mVoaTextList == null) {
//            return;
//        }
//        if (!NetStateUtil.isConnected(TalkShowApplication.getContext()) && !StorageUtil.getAudioFile(TalkShowApplication.getInstance(), mVoa.voaId()).exists()) {
//            ToastUtil.showToast(mContext, "播放原文需要访问数据网络。");
//            return;
//        }
//        if (TextUtils.isEmpty(myIjkPlayer.getDataSource())) {
//            myIjkPlayer.initialize(audioUrl);
//        }
//        if (scrollFlag > 0) {
//            scrollFlag--;
//            handler.removeMessages(0);
//            handler.sendEmptyMessageDelayed(0, 1000);
//            // 将videoView移动到指定的时间
//            if (myIjkPlayer != null) {
//                myIjkPlayer.seekTo((int) mVoaTextList.get(scrollFlag).timing() * 1000);
//                if (!myIjkPlayer.isPlaying()) {
//                    myIjkPlayer.play();
//                }
//                binding.videoPlay.setImageResource(R.mipmap.image_pause);
//            }
//            scrollToMiddle(scrollFlag);
//        } else {
//            ToastUtil.showToast(mContext, getString(R.string.study_first));
//        }
//    }
//
//    private synchronized void reNextClick() {
//        if (mVoaTextList == null) {
//            return;
//        }
//        if (!NetStateUtil.isConnected(TalkShowApplication.getContext()) && !StorageUtil.getAudioFile(TalkShowApplication.getInstance(), mVoa.voaId()).exists()) {
//            ToastUtil.showToast(mContext, "播放原文需要访问数据网络。");
//            return;
//        }
//        if (TextUtils.isEmpty(myIjkPlayer.getDataSource())) {
//            myIjkPlayer.initialize(audioUrl);
//        }
//        if (scrollFlag < (mVoaTextList.size()-1)) {
//            scrollFlag++;
//            handler.removeMessages(0);
//            handler.sendEmptyMessageDelayed(0, 1000);
//            if (myIjkPlayer != null) {
//                myIjkPlayer.seekTo((int) (mVoaTextList.get(scrollFlag).timing() * 1000));
//                if (!myIjkPlayer.isPlaying()) {
//                    myIjkPlayer.play();
//                }
//                binding.videoPlay.setImageResource(R.mipmap.image_pause);
//            }
//            scrollToMiddle(scrollFlag);
//        } else {
//            ToastUtil.showToast(mContext, getString(R.string.study_last));
//        }
//    }
//
//    private void setPlayMode() {
//        int playMode = SPconfig.Instance().loadInt(Config.playMode);
//        switch (playMode) {
//            case 0:
//                SPconfig.Instance().putInt(Config.playMode, 1);
//                binding.imgPlayMode.setImageResource(R.mipmap.img_mode1);
//                ToastUtil.showToast(mContext, "单曲循环");
//                break;
//            case 1:
//                SPconfig.Instance().putInt(Config.playMode, 2);
//                binding.imgPlayMode.setImageResource(R.mipmap.img_mode2);
//                ToastUtil.showToast(mContext, "随机播放");
//                break;
//            case 2:
//                SPconfig.Instance().putInt(Config.playMode, 0);
//                binding.imgPlayMode.setImageResource(R.mipmap.img_mode0);
//                ToastUtil.showToast(mContext, "列表循环");
//                break;
//        }
//    }
//
//    private void viewScroll(double time) {
//        if (mVoaTextList == null) {
//            return;
//        }
//        for (int i = 0; i < mVoaTextList.size(); i++) {
//            VoaText detail = mVoaTextList.get(i);
//            if ((i > 0) && (i == mVoaTextList.size() - 1)) {
//                VoaText preDetail = mVoaTextList.get(i - 1);
//                if ((preDetail.timing() < detail.timing()) && (time > detail.timing())) {
//                    if (i != scrollFlag) {
//                        scrollFlag = i;
//                        scrollToMiddle(i);
////                        LogUtils.e("scrollFlag", i + "");
//                        break;
//                    }
//                }
//            } else {
//                VoaText detailNext = mVoaTextList.get(i + 1);
//                if (time >= detail.timing() && time < detailNext.timing()) {
//                    if (i != scrollFlag) {
//                        scrollFlag = i;
//                        scrollToMiddle(i);
////                        LogUtils.e("scrollFlag", i + "");
//                        break;
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * 点击上一首下一首刷新页面
//     */
//    private void reFreshData() {
//        Log.e(TAG, "reFreshData SetVoaList ");
//        mVoaTextList.clear();
//        if (mPresenter.isViewAttached()) {
//            mPresenter.getVoaTexts(currCourseId);
//        }
//        audioUrl = Constant.getSoundMp3Url(mVoa.sound(), mVoa.voaId());
//        Log.e(TAG, "reFreshData audioUrl " + audioUrl);
//        File audioFile = StorageUtil.getAudioFile(TalkShowApplication.getInstance(), mVoa.voaId());
//        if (audioFile.exists()) {
//            Log.e(TAG, "reFreshData audioFile " + audioFile.getAbsolutePath());
//            audioUrl = audioFile.getAbsolutePath();
//        }
//        binding.wordcard.voa = mVoa.voaId();
//        unitId = mPresenter.getUnitId4Voa(mVoa);
//        binding.wordcard.unit = unitId;
//        binding.wordcard.setVisibility(View.GONE);
//
//        TalkShowApplication.getSubHandler().post(new Runnable() {
//            @Override
//            public void run() {
//                if (Constant.PlayerService) {
//                    if (BackgroundManager.Instace().bindService == null) {
//                        Log.e(TAG, " reFreshData bindService is null?? ");
//                    } else {
//                        if (NetStateUtil.isConnected(TalkShowApplication.getContext()) || StorageUtil.getAudioFile(TalkShowApplication.getInstance(), mVoa.voaId()).exists()) {
//                            myIjkPlayer.initialize(audioUrl);
//                        }
//                        BackgroundManager.Instace().bindService.setTag(mVoa.voaId());
//                        BackgroundManager.Instace().bindService.startTime = System.currentTimeMillis();
//                    }
//                } else {
//                    if (NetStateUtil.isConnected(TalkShowApplication.getContext()) || StorageUtil.getAudioFile(TalkShowApplication.getInstance(), mVoa.voaId()).exists()) {
//                        myIjkPlayer.initialize(audioUrl);
//                    }
//                }
//            }
//        });
////        setSpeed();
//        scrollFlag = 0;
//
//        if (binding.recyclerView != null) {
//            if (TextUtils.isEmpty(mVoa.titleCn())) {
//                ((StudyActivity) mContext).tvCeterTop.setText(mVoa.title());
//            } else {
//                ((StudyActivity) mContext).tvCeterTop.setText(mVoa.titleCn());
//            }
//            if ((450 <= configManager.getCourseId()) && (configManager.getCourseId() <= 457)) {
//                int index = mVoa.voaId() % 1000;
//                ((StudyActivity) mContext).tvCeterTop.setText("Lesson " + index + "  " + mVoa.titleCn());
//            }
//            binding.recyclerView.smoothScrollToPosition(0);
//            mAdapter.notifyDataSetChanged();
//            mAdapter.setLightPosition(0);
//        }
//    }
//
//    /**
//     * 滑动到指定位置
//     */
//    void scrollToMiddle(final int position) {
//        mAdapter.setLightPosition(position);
//        syncho = SPconfig.Instance().loadBoolean(Config.playPosition, false);
//        if (syncho) {
//            centerLayout.smoothScrollToPosition(binding.recyclerView, new RecyclerView.State(), position);
//        } else {
//            binding.recyclerView.scrollToPosition(position);
//        }
//        Log.e(TAG, "scrollToMiddle position " + position );
//    }
//
//    public void setSpeed() {
//        //播放速度切换
//        if (myIjkPlayer != null)
//            if (!UserInfoManager.getInstance().isVip()) {
//                myIjkPlayer.setSpeed(1.0f);
//            } else {
//                float speed = SPconfig.Instance().loadFloat(Config.playerSpeed, 1.0f);
//                Log.d(TAG, " getSpeed " + speed);
//                binding.seekbarSpeed.setProgress((int) (speed * 10) - 5);
//                binding.textPlaySpeed10.setText("倍速 "+speed+"x");
//                myIjkPlayer.setSpeed(speed);
//            }
//    }
//
//    @Override
//    public void showVoaTexts(List<VoaText> voaTextList) {
//        if (voaTextList == null || voaTextList.size() <1) {
//            Log.e(TAG, "showVoaTexts is null.");
//            totalTime = 0;
//            startTime = 0;
//            mVoaTextList.clear();
//        } else {
//            totalTime = 0;
//            int index = 0;
//            for (VoaText item: voaTextList) {
//                if (item.paraId() == 1) {
//                    startTime = item.timing();
//                }
//                if (item.paraId() > index) {
//                    index = item.paraId();
//                    totalTime = item.endTiming();
//                }
//            }
//            Log.e(TAG, "showVoaTexts startTime = " + startTime);
//            Log.e(TAG, "showVoaTexts totalTime = " + totalTime);
//            Log.e(TAG, "voaTextList = " + (voaTextList==null));
//            mVoaTextList.clear();
//            mVoaTextList.addAll(voaTextList);
//        }
//
//        Log.e(TAG, "测试-----1111--"+(mAdapter==null)+"---"+(voaTextList==null));
//        studyRecordUpdateUtil.getStudyRecord().setWordCount(voaTextList);
//        mAdapter.SetVoaList(mVoaTextList);
//        mAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void showEmptyTexts() {
//        Log.e(TAG, "showEmptyTexts is empty.");
//        binding.totalTime.setText("" + totalTime);
//        binding.seekBar.setMax((int) totalTime);
//        mVoaTextList.clear();
//        mAdapter.SetVoaList(mVoaTextList);
//        mAdapter.notifyDataSetChanged();
//    }
//
//    private long aPosition, bPosition, abState = 0;
//    private boolean syncho;
//    public void abPlay() {
//        abState++;
//        if (abState % 3 == 1) {
//            aPosition = myIjkPlayer.getCurrentPosition();
//            ToastUtil.showToast(mContext, "开始记录A-，再次点击即可区间播放");
//        } else if (abState % 3 == 2) {
//            bPosition = myIjkPlayer.getCurrentPosition();
//            ToastUtil.showToast(mContext, "开始播放A-B");
//            handler.sendEmptyMessage(11);// 开始A-B循环
//        } else if (abState % 3 == 0) {
//            handler.sendEmptyMessage(10);// 手动停止
//        }
//    }
//
//    void syncText() {
//        syncho = SPconfig.Instance().loadBoolean(Config.playPosition, false);
//        syncho = !syncho;
//        SPconfig.Instance().putBoolean(Config.playPosition, syncho);
//        if (syncho) {
//            ToastUtil.showToast(mContext, "文本自动滚动开启");
//            binding.ivSync.setImageResource(R.drawable.icon_sync);
//        } else {
//            ToastUtil.showToast(mContext, "文本自动滚动关闭");
//            binding.ivSync.setImageResource(R.drawable.icon_unsync);
//        }
//    }
//
//    void setSpeedText() {
//        if (!UserInfoManager.getInstance().isVip()) {
//            ToastUtil.showToast(mContext, "开通VIP后可使用调速功能");
//        } else {
//            //弹出倍速
////            showSpeed();
//            showSpeedDialog();
//        }
//    }
//    public void showSpeed(){
//        String[] items = new String[]{"0.5X","0.75X","1.0X","1.25X","1.5X","1.75X","2.0X"};
//        new AlertDialog.Builder(mContext)
//                .setItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case 0:
//                                changeSpeed(0.5f);
//                                break;
//                            case 1:
//                                changeSpeed(0.75f);
//                                break;
//                            case 2:
//                                changeSpeed(1.0f);
//                                break;
//                            case 3:
//                                changeSpeed(1.25f);
//                                break;
//                            case 4:
//                                changeSpeed(1.5f);
//                                break;
//                            case 5:
//                                changeSpeed(1.75f);
//                                break;
//                            case 6:
//                                changeSpeed(2.0f);
//                                break;
//                        }
//                        dialog.dismiss();
//                    }
//                })
//                .create()
//                .show();
//    }
//
//    private void changeSpeed(float speed) {
//        if (UserInfoManager.getInstance().isVip()) {
//            myIjkPlayer.setSpeed(speed);
//            SPconfig.Instance().putFloat(Config.playerSpeed, speed);
//            binding.textPlaySpeed10.setText("倍速 "+speed+"x");
//        } else {
//            Toast.makeText(mContext, "成为VIP用户即可调节播放速度！", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    void setMoreFunction() {
//        if (binding.llMoreFunction.getVisibility() == View.VISIBLE) {
//            binding.llMoreFunction.setVisibility(View.GONE);
//        } else {
//            binding.llMoreFunction.setVisibility(View.VISIBLE);
//        }
//    }
//
//    //倍速的数组
//    private final String[] speedArray = {"0.5x","0.75x","1.0x","1.25x","1.5x","1.75x","2.0x"};
//    //获取当前倍速的位置显示
//    private int getSpeedPosition(String speedText){
//        for (int i = 0; i < speedArray.length; i++) {
//            if (speedArray[i].equals(speedText)){
//                return i;
//            }
//        }
//        return 0;
//    }
//    //切换倍速
//    private void changeSpeed(int speedPosition) {
//        switch (speedPosition) {
//            case 0:
//                changeSpeed(0.5f);
//                break;
//            case 1:
//                changeSpeed(0.75f);
//                break;
//            case 2:
//                changeSpeed(1.0f);
//                break;
//            case 3:
//                changeSpeed(1.25f);
//                break;
//            case 4:
//                changeSpeed(1.5f);
//                break;
//            case 5:
//                changeSpeed(1.75f);
//                break;
//            case 6:
//                changeSpeed(2.0f);
//                break;
//            default:
//                break;
//        }
//    }
//    //倍速显示的dialog
//    private AlertDialog speedDialog;
//    private void showSpeedDialog(){
//        speedDialog = new AlertDialog.Builder(getActivity()).create();
//        speedDialog.setCanceledOnTouchOutside(true);
//        speedDialog.show();
//
//        Window window = speedDialog.getWindow();
//        if (window!=null){
//            window.setContentView(R.layout.dialog_speed);
//            window.setGravity(Gravity.BOTTOM);
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            window.setAttributes(lp);
//
//            final TextView tvSpeed = window.findViewById(R.id.tv_speed);
//            float speed = SPconfig.Instance().loadFloat(Config.playerSpeed, 1.0f);
//            tvSpeed.setText("倍速 "+speed+"x");
//
//            SeekBar seekBar = window.findViewById(R.id.seekbar_progress);
//            seekBar.setMax(6);
//            seekBar.setProgress(getSpeedPosition(speed+"x"));
//            seekBar.getParent().requestDisallowInterceptTouchEvent(true);
//            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    tvSpeed.setText("倍速 "+speedArray[progress]);
//                    changeSpeed(progress);
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//
//                }
//            });
//
//            ImageView ivReduce = window.findViewById(R.id.iv_reduce);
//            ivReduce.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (seekBar.getProgress() <= 0){
//                        return;
//                    }
//                    int progress = seekBar.getProgress()-1;
//                    seekBar.setProgress(progress);
//                    tvSpeed.setText("倍速 "+speedArray[progress]);
//                    changeSpeed(progress);
//                }
//            });
//            ImageView ivAdd = window.findViewById(R.id.iv_add);
//            ivAdd.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (seekBar.getProgress() >= seekBar.getMax()){
//                        return;
//                    }
//                    int progress = seekBar.getProgress()+1;
//                    seekBar.setProgress(progress);
//                    tvSpeed.setText("倍速 "+speedArray[progress]);
//                    changeSpeed(progress);
//                }
//            });
//        }
//    }
//
//
//    //是否自动播放标志
//    private static boolean isAutoPlay = false;
//
//    public void setAutoplay(boolean isAuto){
//        isAutoPlay = isAuto;
//    }
//}
