package com.iyuba.talkshow.ui.detail.ranking.watch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoControlsCore;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Ranking;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.databinding.ActivityWatchDubbingBinding;
import com.iyuba.talkshow.event.CommentEvent;
import com.iyuba.talkshow.event.DownloadEvent;
import com.iyuba.talkshow.event.StartEvent;
import com.iyuba.talkshow.event.StopEvent;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.detail.MyOnTouchListener;
import com.iyuba.talkshow.ui.detail.NormalVideoControl;
import com.iyuba.talkshow.ui.detail.comment.CommentFragment;
import com.iyuba.talkshow.ui.dubbing.dialog.download.DownloadDialog;
import com.iyuba.talkshow.ui.user.me.dubbing.released.ReleasedBean;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.ScreenUtils;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.talkshow.util.UploadStudyRecordUtil;
import com.iyuba.talkshow.util.videoView.BaseVideoControl;
import com.jaeger.library.StatusBarUtil;
import com.permissionx.guolindev.PermissionX;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * 配音排行的详情页
 */
public class WatchDubbingActivity extends BaseActivity implements WatchDubbingMvpView, WatchDownloadMvpView {
    private static final String RANKING = "ranking";
    private static final String VOA = "voa";
    private static final String UID = "uid";

    private boolean isInterrupted = false;
    private Ranking mRanking;
    private Voa mVoa;

    private DownloadDialog mDownloadDialog;

    @Inject
    public WatchDownloadPresenter mDownloadPresenter;

    @Inject
    WatchDubbingPresenter mPresenter;

    UploadStudyRecordUtil uploadStudyRecordUtil;

    private NormalVideoControl mVideoControl;

    int uid;

    //设置下载标志
    private boolean isDownloading = false;

    //布局样式
    private ActivityWatchDubbingBinding binding;

    public static Intent buildIntent(Context context, Ranking ranking, Voa voa, Integer uid) {
        Intent intent = new Intent();
        intent.setClass(context, WatchDubbingActivity.class);
        intent.putExtra(RANKING, ranking);
        intent.putExtra(VOA, voa);
        intent.putExtra(UID, uid);
        return intent;
    }

    @Override
    public boolean isSwipeBackEnable() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWatchDubbingBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        activityComponent().inject(this);
        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), R.color.status_bar_video, getTheme()));
        EventBus.getDefault().register(this);
        mPresenter.attachView(this);
        mDownloadPresenter.attachView(this);
        mRanking = getIntent().getParcelableExtra(RANKING);
        mVoa = getIntent().getParcelableExtra(VOA);
        uid = getIntent().getIntExtra(UID, 0);

    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDownloadPresenter.init(mVoa, mRanking);
        mDownloadPresenter.getVoaTexts(mVoa.voaId());
        initVideo();
        initFragment();
        initView();
        initClick();
        Timber.e(mRanking.toString());
        Timber.e(mVoa.toString());

//        uploadStudyRecordUtil = new UploadStudyRecordUtil(mPresenter.checkLogin(), mContext,
//                mPresenter.getUid(), mVoa.voaId(), "2", "2");

        // TODO: 2022/7/28 这里替换为保存文件到相册
//        if (mDownloadPresenter.isSelf(uid)) {
//            binding.layoutCenter.getRoot().setVisibility(View.VISIBLE);
//
//            if (mDownloadPresenter.checkFileExist()) {
//                binding.layoutCenter.ivDownload.setImageResource(R.drawable.ic_delete_64px);
//                binding.layoutCenter.tvDownload.setText("本地删除");
//            } else {
//                binding.layoutCenter.ivDownload.setImageResource(R.drawable.ic_download_64px);
//                binding.layoutCenter.tvDownload.setText("下载作品");
//            }
//        }
        if (mDownloadPresenter.isSelf(uid)) {
            binding.layoutCenter.ivDownload.setImageResource(R.drawable.ic_download_64px);
            binding.layoutCenter.tvDownload.setText("保存到相册");
        }

        mDownloadDialog = new DownloadDialog(this);
        mDownloadDialog.setmOnDownloadListener(new DownloadDialog.OnDownloadListener() {
            @Override
            public void onContinue() {
                mDownloadDialog.dismiss();
            }

            @Override
            public void onCancel() {
                mDownloadPresenter.cancelDownload();
                finish();
            }
        });
    }

    private void initClick() {
        binding.layoutCenter.thumbLayout.setOnClickListener(v -> onThumbClick());
        binding.layoutCenter.getRoot().setOnClickListener(v -> onClickLayoutCenter());
        binding.layoutCenter.downloadLayout.setOnClickListener(v -> downloadClick());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initVideo() {
        MyOnTouchListener listener = new MyOnTouchListener(this);
        listener.setSingleTapListener(mSingleTapListener);

        mVideoControl = new NormalVideoControl(this);
        mVideoControl.setMode(BaseVideoControl.Mode.SHOW_AUTO);
        mVideoControl.setBackCallback(() -> {
            if (isDownloading) {
                mDownloadDialog.show();
            } else {
                finish();
            }
        });
        mVideoControl.setButtonListener(new VideoControlsButtonListener() {
            @Override
            public boolean onPlayPauseClicked() {
                if (binding.videoView == null) {
                    return false;
                }

                if (binding.videoView.isPlaying()) {
                    binding.videoView.pause();
                    stopVideoView("0");
                } else {
                    binding.videoView.start();
                    EventBus.getDefault().post(new StopEvent(StopEvent.SOURCE.VOICE));
                    startStudyRecord();
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
        mVideoControl.setOnTouchListener(listener);
        binding.videoView.setVideoURI(mDownloadPresenter.getVideoUri());
        Timber.tag("tag").e(mDownloadPresenter.getVideoUri().getPath());
        binding.videoView.setOnPreparedListener(mVideoPreparedListener);
        binding.videoView.setOnCompletionListener(mVideoCompletionListener);
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

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, CommentFragment.newInstance(mVoa, mRanking.id(), mRanking.userName(),mRanking.videoUrl()));
        transaction.commit();
    }

    private void initView() {
        if (mRanking != null) {
            if (mRanking.agreeNum == 0) {
                mRanking.agreeNum = mRanking.agreeCount();
            }
            mPresenter.checkThumb(mRanking.id());
            /*Glide.with(this)
                    .load(mRanking.imgSrc())
                    .transform(new CircleTransform(this))
                    .placeholder(R.drawable.default_avatar)
                    .into(binding.layoutCenter.photoIv);*/
            Glide3Util.loadCircleImg(this,mRanking.imgSrc(),R.drawable.default_avatar,binding.layoutCenter.photoIv);
            binding.layoutCenter.thumbNumTv.setText(String.valueOf(mRanking.agreeNum));
            binding.layoutCenter.userNameTv.setText(mRanking.userName());
            binding.layoutCenter.dateTv.setText(mRanking.createDate());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
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

    public void onThumbClick() {
        mPresenter.doThumb(mRanking.id());
    }
//
    public void onClickLayoutCenter() {
        EventBus.getDefault().post(new CommentEvent(CommentEvent.Status.GONE));
    }

    public void downloadClick() {
        //替换为下面的功能
//        if (mDownloadPresenter.checkFileExist()) {
//            mDownloadPresenter.deleteLocalFile();
//
//            binding.layoutCenter.ivDownload.setImageResource(R.drawable.ic_download_64px);
//            binding.layoutCenter.tvDownload.setText("下载作品");
//
//            showToastShort("本地文件已删除");
//            setVideoAndAudio();
//        } else {
//
//            if (mPresenter.isVip()) {
//                if (!NetStateUtil.isConnected(mContext)) {
//                    showToastShort("网络异常");
//                    return;
//                }
//
//                binding.loadingView.root.setVisibility(View.VISIBLE);
//                binding.loadingView.loadingTv.setText(getString(R.string.downloading));
//                mDownloadPresenter.download();
//            } else {
//                showVipDialog();
//            }
//        }

        if (binding.videoView!=null&&binding.videoView.isPlaying()){
            binding.videoView.pause();
        }

        if (isDownloading){
            ToastUtil.showToast(this,"正在保存到相册，请稍后操作");
            return;
        }

        downVideoToAlbum();
    }

    private void showVipDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage("下载本人作品为VIP功能，是否开通会员")
                .setPositiveButton("开通", (dialogInterface, i) -> {
                    if (UserInfoManager.getInstance().isLogin()) {
                        startActivity(new Intent(mContext, NewVipCenterActivity.class));
                    } else {
                        NewLoginUtil.startToLogin(this);
                    }
                })
                .setNeutralButton("取消", (dialogInterface, i) -> {
                }).show();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopEvent(StopEvent stopEvent) {
        if (stopEvent.source == StopEvent.SOURCE.VIDEO) {
            isInterrupted = true;
            binding.videoView.pause();
            stopVideoView("0");
        }
    }
//
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartEvent(StartEvent startEvent) {
        if (isInterrupted) {
            binding.videoView.start();
        }
    }

    //替换为下面的功能
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onDownloadFinish(DownloadEvent downloadEvent) {
//        switch (downloadEvent.status) {
//            case DownloadEvent.Status.FINISH:
//                binding.layoutCenter.downloadLayout.setVisibility(View.GONE);
//                binding.layoutCenter.ivDownload.setImageResource(R.drawable.ic_delete_64px);
//                binding.layoutCenter.tvDownload.setText("本地删除");
//                setVideoAndAudio();
//                break;
//            case DownloadEvent.Status.DOWNLOADING:
//                binding.layoutCenter.tvDownload.setText(downloadEvent.msg);
//                break;
//            default:
//                break;
//        }
//    }

    public void setVideoAndAudio() {
        try {
            if (binding.videoView.isPlaying()) {
                binding.videoView.pause();
            }
            int pos = (int) binding.videoView.getCurrentPosition();
            binding.videoView.setVideoURI(mDownloadPresenter.getVideoUri());
            Timber.tag("tag").e(mDownloadPresenter.getVideoUri().getPath());
            binding.videoView.seekTo(pos - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    OnPreparedListener mVideoPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            binding.videoView.start();

            //开启下载
            if (mDownloadPresenter.isSelf(uid)){
                if (PermissionX.isGranted(WatchDubbingActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    if (!mDownloadPresenter.checkAlbumVideoExist(getPeiyinVideoName(mVoa))){
                        binding.layoutCenter.downloadLayout.setVisibility(View.VISIBLE);
                    }
                }else {
                    binding.layoutCenter.downloadLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    OnCompletionListener mVideoCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion() {
            binding.videoView.restart();
            binding.videoView.setOnPreparedListener(() -> {
                binding.videoView.pause();
                stopVideoView("1");
            });
        }
    };

    private void startStudyRecord() {
        uploadStudyRecordUtil = new UploadStudyRecordUtil(UserInfoManager.getInstance().isLogin(), mContext,
                UserInfoManager.getInstance().getUserId(), mVoa.voaId(), "2", "2");
    }
    private void stopVideoView(String flag) {
        uploadStudyRecordUtil.stopStudyRecord(getApplicationContext(), UserInfoManager.getInstance().isLogin(),
                flag, mPresenter.getUploadService());
    }

    @Override
    protected void onResume() {
        super.onResume();
        startStudyRecord();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.videoView.pause();
        stopVideoView("0");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDownloadPresenter != null) {
            mDownloadPresenter.cancelDownload();
            mDownloadPresenter.detachView();
        }
        EventBus.getDefault().unregister(this);
        mPresenter.detachView();
    }

    @Override
    public void updateThumbIv(int action) {
        int resId = action == ThumbAction.THUMB ? R.drawable.thumb_green : R.drawable.thumb_gray;
        binding.layoutCenter.thumbIv.setImageResource(resId);
    }

    @Override
    public void updateThumbNumTv() {
        int thumbNum = Integer.parseInt(binding.layoutCenter.thumbNumTv.getText().toString());
        binding.layoutCenter.thumbNumTv.setText(String.valueOf(thumbNum + 1));

        //回调点赞数据，否则会出现问题
        EventBus.getDefault().post(new ReleasedBean(true));
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (isDownloading) {
            mDownloadDialog.show();
        } else {
            if (mVideoControl.isFullScreen()) {
                mVideoControl.exitFullScreen();
            } else {
                finish();
            }
        }
    }

    private boolean isDownloading() {
        return binding.loadingView.root.getVisibility() == View.VISIBLE && !mDownloadPresenter.checkFileExist();
    }

    @Override
    public void showVoaTextLit(List<VoaText> voaTextList) {
        uploadStudyRecordUtil.getStudyRecord().setWordCount(voaTextList);
    }

    //下载视频到本地相册
    private void downVideoToAlbum() {
        //默认你已经登陆了
        if (!UserInfoManager.getInstance().isVip()) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("保存本人配音视频到相册需要VIP会员权限，是否确认开通VIP会员？")
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
                            startDownloadVideo();
                        } else {
                            Toast.makeText(this, "请授予必要的权限", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            startDownloadVideo();
        }
    }

    private void startDownloadVideo() {
        if (!NetStateUtil.isConnected(this)) {
            ToastUtil.showToast(this, "网络异常");
            return;
        }

        isDownloading = true;

        //下载视频
        binding.loadingView.getRoot().setVisibility(View.VISIBLE);
        binding.loadingView.loadingTv.setVisibility(View.VISIBLE);
        binding.loadingView.loadingTv.setText("正在保存到相册");
        String videoUrl = mDownloadPresenter.getDownloadVideoUri().toString();
        String fileName = getPeiyinVideoName(mVoa);
        String localPath = getExternalFilesDir(null).getPath() + File.separator + fileName;
        mDownloadPresenter.downVideoAndImportAlbum(videoUrl, localPath);
        //这里以1001作为回调信息，出现1001则为保存视频到相册
    }

    //获取配音视频名称
    private String getPeiyinVideoName(Voa curVoa){
        return "peiyin_"+curVoa.category()+"_"+curVoa.voaId()+Constant.Voa.MP4_SUFFIX;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadFinish(DownloadEvent downloadEvent) {
        switch (downloadEvent.status) {
            case DownloadEvent.Status.FINISH:
                binding.loadingView.loadingTv.setVisibility(View.GONE);
                binding.loadingView.root.setVisibility(View.GONE);
                binding.layoutCenter.downloadLayout.setVisibility(View.GONE);
//                setVideoAndAudio();
                //这里有点问题，需要关闭处理
//                mDownloadPresenter.addFreeDownloadNumber();

                isDownloading = false;
                if (downloadEvent.downloadId == 1001){
                    ToastUtil.showToast(mContext, "保存相册完成");
                }else {
                    ToastUtil.showToast(mContext, "下载完成");
                }
                break;
            case DownloadEvent.Status.ERROR:
                binding.loadingView.loadingTv.setVisibility(View.GONE);
                binding.loadingView.root.setVisibility(View.GONE);

                isDownloading = false;
                if (downloadEvent.downloadId == 1001){
                    ToastUtil.showToast(mContext, "保存相册出错，请重试");
                }else {
                    ToastUtil.showToast(mContext, "下载出错，请重试");
                }
                break;
            case DownloadEvent.Status.DOWNLOADING:
                binding.loadingView.loadingTv.setText(downloadEvent.msg);
                break;
            default:
                break;
        }
    }
}
