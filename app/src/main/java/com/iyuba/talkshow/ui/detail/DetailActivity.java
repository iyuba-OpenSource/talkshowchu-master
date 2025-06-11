package com.iyuba.talkshow.ui.detail;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.iyuba.talkshow.BuildConfig;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.databinding.ActivityDetailBinding;
import com.iyuba.talkshow.event.DownloadEvent;
import com.iyuba.talkshow.lil.help_fix.ui.dubbing.DubbingNewActivity;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.detail.introduction.IntroductionFragment;
import com.iyuba.talkshow.ui.detail.ranking.RankingFragment;
import com.iyuba.talkshow.ui.detail.recommend.RecommendFragment;
import com.iyuba.talkshow.ui.dubbing.dialog.download.DownloadDialog;
import com.iyuba.talkshow.ui.main.drawer.Share;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.talkshow.ui.widget.BubblePopupWindow;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.ScreenUtils;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.TimeUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.talkshow.util.UploadStudyRecordUtil;
import com.iyuba.talkshow.util.Util;
import com.iyuba.talkshow.util.VoaMediaUtil;
import com.iyuba.talkshow.util.videoView.BaseVideoControl;
import com.jaeger.library.StatusBarUtil;
import com.permissionx.guolindev.PermissionX;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import devcontrol.DevControlActivity;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * 详情界面
 */
public class DetailActivity extends BaseActivity implements DetailMvpView {
    private static final String TAG = "DetailActivity";

    private static final String VOA = "voa";
    private static final String BACK_TO_MAIN = "back_to_main";
    private Voa mVoa;
    private boolean mIsPause;
    private final List<Fragment> mFragmentList = new ArrayList<>();

    @Inject
    public DetailPresenter mPresenter;

    @Inject
    DetailDownloadPresenter mDownloadPresenter;

    Subscription s;
    private long mCurPosition;

    @Inject
    public DataManager mDataManager;
    @Inject
    ConfigManager configManager;

    UploadStudyRecordUtil studyRecordUpdateUtil;


    private DownloadDialog mDownloadDialog;

    private NormalVideoControl mVideoControl;

    //针对下载操作进行处理
    //当前是否正在保存到相册
    private boolean isSaveVideoToAlbum = false;
    //是否正在下载所需资源
    private boolean isDownloading = false;

    //布局样式
    private ActivityDetailBinding binding;

    OnPreparedListener videoPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            if (mCurPosition != 0) {
                pauseVideoPlayer("0");
            } else {
                if (!mIsPause) {
                    binding.videoView.start();
                }
            }
        }
    };

    OnCompletionListener videoCompletionListener = () -> {
        binding.videoView.restart();
//        binding.videoView.setOnPreparedListener(() -> pauseVideoPlayer("1"));
    };
    private static final int REQUECT_CODE_RECORD_AUDIO = 1111;
    private boolean collectFlag;

    public static Intent buildIntent(Context context, Voa voa, boolean backToMain) {
        Intent intent = new Intent();
        intent.setClass(context, DetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(VOA, voa);
        intent.putExtra(BACK_TO_MAIN, backToMain);
        return intent;
    }

    public static Intent buildIntentFromRecommend(Context context, Voa voa, boolean backToMain) {
        Intent intent = new Intent();
        intent.setClass(context, DetailActivity.class);
        intent.putExtra(VOA, voa);
        intent.putExtra(BACK_TO_MAIN, backToMain);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), R.color.status_bar_video, getTheme()));
        activityComponent().inject(this);
        mPresenter.attachView(this);
        mDownloadPresenter.attachView(this);
        EventBus.getDefault().register(this);
        mVoa = getIntent().getParcelableExtra(VOA);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mPresenter.setVoa(mVoa);
        mDownloadPresenter.init(mVoa);
        initVideo();
        initView();
        initClick();
        initFragment();
        studyRecordUpdateUtil = new UploadStudyRecordUtil(UserInfoManager.getInstance().isLogin(),
                mContext, UserInfoManager.getInstance().getUserId(), mVoa.voaId(), "0", "2");
        if (mDownloadPresenter.checkFileExist()) {
            binding.otherLayout.ivDownload.setVisibility(View.GONE);
        }
//
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
        binding.otherLayout.collect.setOnClickListener(v -> onCollectClick());
        binding.otherLayout.icMore.setOnClickListener(v -> clickMore());
        binding.otherLayout.dubbing.setOnClickListener(v -> onDubbingClick());
        binding.otherLayout.ivDownload.setOnClickListener(v -> download());
        binding.otherLayout.refreshOrig.setOnClickListener(v -> refresh());
    }

    public void initVideo() {
        MyOnTouchListener listener = new MyOnTouchListener(this);
        listener.setSingleTapListener(mSingleTapListener);
        mVideoControl = new NormalVideoControl(this);
        mVideoControl.setPlayPauseDrawables(getResources().getDrawable(R.drawable.play), getResources().getDrawable(R.drawable.pause));
        mVideoControl.setMode(BaseVideoControl.Mode.SHOW_AUTO);
        mVideoControl.setBackCallback(() -> {
            if (!isDownloading()) {
                finish();
            } else {
                mDownloadDialog.show();
            }
        });
        mVideoControl.setToTvCallBack(() -> {
            if (!UserInfoManager.getInstance().isVip()){
                ToastUtil.showToast(this, "非VIP会员不能进行投屏，如需进行投屏，请开通VIP后重试");
                return;
            }
                s = mDataManager.getSeriesList(mVoa.voaId(), mVoa.series(), 1, 10)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Func1<List<Voa>, Observable<Pair<List<String>, List<String>>>>) voas -> {
                    List<String> stringUrl = new ArrayList<>();
                    List<String> stringTitle = new ArrayList<>();
                    for (int i = 0; i < voas.size(); i++) {
                        //替换vip的video链接
                        stringUrl.add(VoaMediaUtil.getVideoVipUrl(voas.get(i).video()));
                        stringTitle.add(voas.get(i).titleCn() + "");
                    }
                    Pair<List<String>, List<String>> pair = new Pair<>(stringUrl, stringTitle);
                    return Observable.just(pair);
                })
                .subscribe(listListPair -> chooseDevice(
                        VoaMediaUtil.getVideoVipUrl(mVoa.video()),
                        mVoa.titleCn(), listListPair.first, listListPair.second));});
        binding.videoView.setControls( mVideoControl);
        mVideoControl.setOnTouchListener(listener);
        binding.videoView.setOnPreparedListener(videoPreparedListener);
        binding.videoView.setOnCompletionListener(videoCompletionListener);
        binding.videoView.setVideoURI(mPresenter.getVideoUri());
        setVideoViewParams();
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            View decor = getWindow().getDecorView();
//            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            //| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setVideoViewParams();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        binding.otherLayout.refreshOrig.setClickable(true);
//            if(mCurPosition != 0) {
//                mVideoView.seekTo((int) mCurPosition);
//            }
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        pauseVideoPlayer("0");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mDownloadDialog.dismiss();
        mPresenter.detachView();
        if (mDownloadPresenter != null) {
            mDownloadPresenter.cancelDownload();
            mDownloadPresenter.detachView();
        }
        EventBus.getDefault().unregister(this);

        binding.videoView.release();
        super.onDestroy();
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

    private void initFragment() {
        mFragmentList.add(IntroductionFragment.newInstance(mVoa.descCn()));
        mFragmentList.add(RankingFragment.newInstance(mVoa));
        String[] titles = getResources().getStringArray(R.array.detail_page_title_default);
        if (mVoa.series() != 0) {
            mFragmentList.add(RecommendFragment.newSeriesInstance(mVoa.voaId(), mVoa.series()));
            titles = getResources().getStringArray(R.array.detail_page_title);
        }
        if (!BuildConfig.APPLICATION_ID.contains("xiaoxue")&&!BuildConfig.APPLICATION_ID.contains("childenglish")
                &&!BuildConfig.APPLICATION_ID.contains("primaryenglish") && !BuildConfig.APPLICATION_ID.contains("junior")){
            mFragmentList.add(RecommendFragment.newInstance(mVoa.voaId(), mVoa.category()));
        }
        FragmentAdapter mFragmentAdapter = new FragmentAdapter(
                this.getSupportFragmentManager(), mFragmentList, titles);
        binding.viewpager.setOffscreenPageLimit(2);
        binding.viewpager.setAdapter(mFragmentAdapter);
        binding.viewpager.setCurrentItem(0);
        binding.detailTabs.setupWithViewPager(binding.viewpager);
    }

    private void initView() {
        Drawable drawable = binding.otherLayout.difficultyRb.getProgressDrawable();
        int drawableSize = (int) getResources().getDimension(R.dimen.difficulty_image_size);
        drawable.setBounds(0, 0, drawableSize, drawableSize);
        binding.otherLayout.difficultyRb.setMax(Constant.Voa.MAX_DIFFICULTY);
        binding.otherLayout.difficultyRb.setProgress(mVoa.hotFlag());
        mPresenter.checkCollected(mVoa.voaId());
    }

    public void stopPlaying() {
        if (binding.videoView.isPlaying()) {
            mCurPosition = binding.videoView.getCurrentPosition();
//            pauseVideoPlayer("0");
        }
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setIsCollected(boolean isCollected) {
        collectFlag = isCollected;
        int resId = isCollected ? R.drawable.ic_study_collect_ok : R.drawable.ic_study_collect_no;
        binding.otherLayout.collect.setImageDrawable(getResources().getDrawable(resId));
    }

//    @Override
//    public void setCollectTvText(int resId) {
//        mCollectTv.setText(getString(resId));
//    }

    @Override
    public void showVoaTextLit(List<VoaText> voaTextList) {
//        mDownloadPresenter.setWordCount(voaTextList, studyRecordUpdateUtil.getStudyRecord());
    }

    public void showPDFDialong() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] choices = {"英文", "中英双语"};
        builder.setTitle("请选择导出文件的语言")
                .setItems(choices, (dialog, which) -> {
                    dialog.dismiss();
                    switch (which) {
                        case 0:
                            if (mDownloadPresenter.checkIsFree()) {
                                mPresenter.getPdf(mVoa.voaId(), 1);
                            } else if (UserInfoManager.getInstance().isLogin()) {
                                mDownloadPresenter.deductIntegral(DetailDownloadPresenter.PDF_ENG);
                            } else {
                                startLogin();
                            }
                            break;
                        case 1:
                            if (mDownloadPresenter.checkIsFree()) {
                                mPresenter.getPdf(mVoa.voaId(), 0);
                            } else if (UserInfoManager.getInstance().isLogin()) {
                                mDownloadPresenter.deductIntegral(DetailDownloadPresenter.PDF_BOTH);
                            } else {
                                startLogin();
                            }
                            break;

                        default:
                            break;
                    }
                });
        builder.show();
    }

    @Override
    public void onDeductIntegralSuccess(int type ) {
        if(type ==DetailDownloadPresenter.TYPE_DOWNLOAD ){
            startDownload();
        }else if (type == DetailDownloadPresenter.PDF_ENG){
            mPresenter.getPdf(mVoa.voaId(), 1);
        }else {
            mPresenter.getPdf(mVoa.voaId(), 0);
        }

    }

    @Override
    public void showPdfFinishDialog(String url) {
        final String downloadPath = "http://apps." + Constant.Web.WEB_SUFFIX + "iyuba" + url;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        final androidx.appcompat.app.AlertDialog dialog = builder.setTitle("PDF已生成 请妥善保存。")
                .setMessage("下载链接：" + downloadPath + "\n[已复制到剪贴板]\n")
                .setNegativeButton("下载", null)
                .setPositiveButton("关闭", null)
                .setNeutralButton("发送", null)
                .create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        try {
            View v = dialog.getWindow().getDecorView().findViewById(android.R.id.message);
            if (v != null) {
                v.setOnClickListener(v1 -> {
                    Util.copy2ClipBoard(mContext, downloadPath);
                    ToastUtil.showToast(this, "PDF下载链接已复制到剪贴板");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button positive = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE);
        positive.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mVoa.titleCn())) {
                mDownloadPresenter.onDownloadPdf(mContext, "" + mVoa.voaId(), downloadPath);
            } else {
                mDownloadPresenter.onDownloadPdf(mContext, mVoa.titleCn().trim(), downloadPath);
            }
        });
        Button neutral = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL);
        neutral.setOnClickListener(v -> {
            String title = mVoa.title() + " PDF";
            if (ConfigData.openShare) {
                Share.sharePdfMessage(mContext, App.Url.APP_ICON_URL, "", downloadPath, title, null);
            } else {
                ToastUtil.showToast(this, "对不起，发送暂时不支持");
            }
        });
        if (ConfigData.openShare) {
            neutral.setVisibility(View.VISIBLE);
        }else {
            neutral.setVisibility(View.GONE);
        }
        Util.copy2ClipBoard(mContext, downloadPath);
        ToastUtil.showToast(this, "PDF下载链接已复制到剪贴板");
    }

//    @OnClick(R.id.collect)
    public void onCollectClick() {
        if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
            ToastUtil.showToast(mContext, "收藏功能需要打开网络数据连接");
            return;
        }
        if (collectFlag) {
            mPresenter.safeDelete(mVoa.voaId());
        } else {
            mPresenter.safeUpdate(mVoa.voaId());
        }
    }

    public void clickMore() {
        //如果正在下载，则提示
        if (isSaveVideoToAlbum){
            ToastUtil.showToast(this,"正在保存到相册中，请稍后操作");
            return;
        }

        if (isDownloading){
            ToastUtil.showToast(this,"正在下载音视频资源，请稍后操作");
            return;
        }

        showPopUp(binding.otherLayout.icMore);
    }

    public void onDubbingClick() {
        if (mDownloadPresenter.checkFileExist()) {
            askForPermisions();
        } else {
            if (!NetStateUtil.isConnected(mContext)) {
                showToast(R.string.main_need_network);
            } else {
                askForPermisions();
            }
        }
    }

    private void askForPermisions() {
        PermissionX.init(this).permissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request((granted, strings, strings2) -> {
                    if (granted) requestAudioSuccess();
                    else  requestAudioFailed();
                });
    }


    public void requestAudioSuccess() {
        stopPlaying();
        mIsPause = true;
        long timestamp = TimeUtil.getTimeStamp();

//        Intent intent = DubbingActivity.buildIntent(this, mVoa, timestamp);
//        startActivity(intent);
        DubbingNewActivity.start(this,mVoa);
    }

    public void requestAudioFailed() {
        Toast.makeText(this, "请授予必要的权限", Toast.LENGTH_LONG).show();
    }


//    @OnClick(R.id.share)
    public void onShareClick() {
//        pauseVideoPlayer("0");
        binding.otherLayout.share.setClickable(false);
        if (ConfigData.openShare) {
            Share.prepareVideoMessage(this, configManager.getKouId(), mVoa, mPresenter.getInregralService(), UserInfoManager.getInstance().getUserId());
        } else {
            ToastUtil.showToast(this, "对不起，分享暂时不支持");
        }
    }

    public void download() {
        if (mDownloadPresenter.checkIsFree()) {
//            startDownload();
            askForSdcardPermisions();
        } else {
            showIntegralDialog();
        }
    }
    private void askForSdcardPermisions() {
        PermissionX.init(this).permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request((granted, strings, strings2) -> {
                    if (granted) startDownload();
                    else  requestAudioFailed();
                });
    }

    public void refresh() {
        mPresenter.getVoaSeries(configManager.getKouId() + "");
    }

    private void chooseDevice(String url, String title, List<String> voaUrls, List<String> voaTitles) {
        Intent intent = new Intent(this, DevControlActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("urls", (Serializable) voaUrls);
        intent.putExtra("titles", (Serializable) voaTitles);
        intent.putExtra("title", title);
        startActivity(intent);
        s.unsubscribe();
    }

    private void startDownload() {
        if (mDownloadPresenter.checkFileExist()) {
            binding.otherLayout.ivDownload.setVisibility(View.GONE);
        } else {
            if (!NetStateUtil.isConnected(mContext)) {
                showToast("网络异常");
                return;
            }
            if (!UserInfoManager.getInstance().isLogin()) {
//                startActivity(new Intent(mContext, LoginActivity.class));
                startLogin();
                return;
            }

            isDownloading = true;

            //下载音频视频
            binding.loadingView.getRoot().setVisibility(View.VISIBLE);
            binding.loadingView.loadingTv.setText(getString(R.string.downloading));
            mDownloadPresenter.download();
        }
    }

    private void showIntegralDialog() {
        new AlertDialog.Builder(mContext).setTitle("提示")
//                .setMessage("您的免费下载次数已用完，非会员用户下载需消耗20积分，请确认是否下载")
                .setMessage("非会员用户下载需消耗20积分，请确认是否下载")
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("下载", (dialog, which) -> {
                    if (UserInfoManager.getInstance().isLogin()) {
                        mDownloadPresenter.deductIntegral(DetailDownloadPresenter.TYPE_DOWNLOAD);
                    } else {
                        startLogin();
                    }
                })
                .show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadFinish(DownloadEvent downloadEvent) {
        switch (downloadEvent.status) {
            case DownloadEvent.Status.FINISH:
                binding.loadingView.loadingTv.setVisibility(View.GONE);
                binding.loadingView.root.setVisibility(View.GONE);
                binding.otherLayout.ivDownload.setVisibility(View.GONE);
//                setVideoAndAudio();
                //这里根据功能逻辑，去掉免费次数
//                mDownloadPresenter.addFreeDownloadNumber();

                if (downloadEvent.downloadId == 1002){
                    isSaveVideoToAlbum = false;
                    ToastUtil.showToast(mContext, "保存相册完成");
                }else {
                    isDownloading = false;
                    ToastUtil.showToast(mContext, "下载完成");
                }
                break;
            case DownloadEvent.Status.ERROR:
                binding.loadingView.loadingTv.setVisibility(View.GONE);
                binding.loadingView.root.setVisibility(View.GONE);
                binding.otherLayout.ivDownload.setVisibility(View.GONE);
                if (downloadEvent.downloadId == 1002){
                    isSaveVideoToAlbum = false;
                    ToastUtil.showToast(mContext, "保存相册出错，请重试");
                }else {
                    isDownloading = false;
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

    public void setVideoAndAudio() {
        try {
            if (binding.videoView.isPlaying()) {
                binding.videoView.pause();
            }
            int pos = (int) binding.videoView.getCurrentPosition();
            binding.videoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoDubbingFile(this, mVoa.voaId())));
            binding.videoView.seekTo(pos - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//

    private void pauseVideoPlayer(String flag) {
        binding.videoView.pause();
        studyRecordUpdateUtil.stopStudyRecord(getApplicationContext(), UserInfoManager.getInstance().isLogin(), flag, mDataManager.getUploadStudyRecordService());
    }

    @Override
    public void onBackPressed() {
        if (isDownloading()||isDownloading||isSaveVideoToAlbum) {
//            mDownloadDialog.show();
            mDownloadPresenter.cancelDownload();
            binding.loadingView.root.setVisibility(View.GONE);
            showToastShort("下载已经取消。");

            isDownloading = false;
            isSaveVideoToAlbum = false;
        } else {
            if (mVideoControl.isFullScreen()) {
                mVideoControl.exitFullScreen();
            } else {
                finish();
            }
        }
    }

    private void showPopUp(View v) {

        BubblePopupWindow popup = new BubblePopupWindow(DetailActivity.this);
        View bubbleView = LayoutInflater.from(this).inflate(R.layout.layout_popup_menu, null,false);
        LinearLayout share = bubbleView.findViewById(R.id.share);
        if (ConfigData.openShare) {
            share.setVisibility(View.VISIBLE);
        } else {
            share.setVisibility(View.GONE);
        }
        LinearLayout download = bubbleView.findViewById(R.id.download);
        LinearLayout pdf = bubbleView.findViewById(R.id.pdf);
        LinearLayout refresh = bubbleView.findViewById(R.id.refresh);
        if(mDownloadPresenter.checkFileExist()){
            download.setVisibility(View.GONE);
        }
        LinearLayout saveVideo = bubbleView.findViewById(R.id.download_video);
        saveVideo.setVisibility(View.VISIBLE);
        //根据权限判断
        if (PermissionX.isGranted(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            if (mDownloadPresenter.checkAlbumVideoExist(getAlbumVideoName(mVoa))){
                saveVideo.setVisibility(View.GONE);
            }
        }

        popup.setBubbleView(bubbleView); // 设置气泡内容
        popup.show(v, Gravity.BOTTOM, 1000,true); // 显示弹窗
        View.OnClickListener listener  = v1 -> {
            if (v1.getId()== R.id.share){
                onShareClick();
                popup.dismiss();
            }else if (v1.getId()== R.id.download){
                if (UserInfoManager.getInstance().isLogin()){
                    download();
                }else {
                    NewLoginUtil.startToLogin(mContext);
                }
                popup.dismiss();
            }else if (v1.getId()== R.id.pdf) {
                if (!UserInfoManager.getInstance().isLogin()){
                    NewLoginUtil.startToLogin(mContext);
                }else {
                    if (UserInfoManager.getInstance().isVip()){
                        showPDFDialong();
                    }else {
                        showCreditDialong();
                    }
                }
                popup.dismiss();
            }else if (v1.getId()== R.id.refresh){
                refresh();
                popup.dismiss();
            }
            //增加下载视频到相册
            else if (v1.getId() == R.id.download_video){
                checkDownloadCondition();
                popup.dismiss();
            }
        };
        share.setOnClickListener(listener);
        download.setOnClickListener(listener);
        pdf.setOnClickListener(listener);
        refresh.setOnClickListener(listener);
        saveVideo.setOnClickListener(listener);
    }

    private void showCreditDialong() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("非VIP用户每篇PDF需扣除20积分");
        builder.setPositiveButton("我知道了", (dialog, which) -> {
            showPDFDialong();
            dialog.dismiss();
        });
        builder.show();

    }

    private boolean isDownloading() {
        return binding.loadingView.getRoot().getVisibility() == View.VISIBLE && !mDownloadPresenter.checkFileExist();
    }

    /*@Override
    public void goResultActivity(LoginResult data) {
        if (data == null) {
            NewLoginUtil.startToLogin(this);
        } else if (!TextUtils.isEmpty(data.getPhone())) {
            String randNum = "" + System.currentTimeMillis();
            String user = "iyuba" + randNum.substring(randNum.length() - 4) + data.getPhone().substring(data.getPhone().length() - 4);
            String pass = data.getPhone().substring(data.getPhone().length() - 6);
            Log.e(TAG, "goResultActivity.user  " + user);
            Log.e(TAG, "goResultActivity.pass  " + pass);
            Intent intent = new Intent(mContext, RegisterSubmitActivity.class);
            intent.putExtra(RegisterSubmitActivity.PhoneNum, data.getPhone());
            intent.putExtra(RegisterSubmitActivity.UserName, user);
            intent.putExtra(RegisterSubmitActivity.PassWord, pass);
            intent.putExtra(RegisterSubmitActivity.RegisterMob, 1);
            startActivity(intent);
        } else {
            Log.e(TAG, "goResultActivity LoginResult is ok. ");
        }
        SecVerify.finishOAuthPage();
        CommonProgressDialog.dismissProgressDialog();
    }*/

    //检查下载前置
    private void checkDownloadCondition(){
        if (!UserInfoManager.getInstance().isLogin()){
            startLogin();
            return;
        }

        if (!UserInfoManager.getInstance().isVip()){
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("保存视频到相册需要VIP会员权限，是否确认开通VIP会员？")
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setClass(mContext, NewVipCenterActivity.class);
                            intent.putExtra(NewVipCenterActivity.HUI_YUAN,NewVipCenterActivity.BENYINGYONG);
                            startActivity(intent);
                        }
                    }).show();
            return;
        }

        if (!PermissionX.isGranted(mContext,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            PermissionX.init(this).permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request((granted, strings, strings2) -> {
                        if (granted) downloadVideoToAlbum();
                        else requestAudioFailed();
                    });
        }else {
            downloadVideoToAlbum();
        }
    }

    //下载文件
    private void downloadVideoToAlbum(){
        if (!NetStateUtil.isConnected(mContext)) {
            showToast("网络异常");
            return;
        }

        isSaveVideoToAlbum = true;

        //下载视频
        binding.loadingView.getRoot().setVisibility(View.VISIBLE);
        binding.loadingView.loadingTv.setVisibility(View.VISIBLE);
        binding.loadingView.loadingTv.setText("正在保存到相册");
        String videoUrl = VoaMediaUtil.getVideoUrl(mVoa.video());
        if (UserInfoManager.getInstance().isVip()){
            videoUrl = VoaMediaUtil.getVideoVipUrl(mVoa.video());
        }
        String fileName = getAlbumVideoName(mVoa);
        String localPath = getExternalFilesDir(null).getPath()+ File.separator+fileName;
        mDownloadPresenter.downVideoAndImportAlbum(videoUrl,localPath);
        //这里以1002作为数据显示，出现1002则表示为保存视频到相册
    }

    //获取当前下载视频的名称
    private String getAlbumVideoName(Voa curVoa){
        return curVoa.category()+"_"+curVoa.voaId()+Constant.Voa.MP4_SUFFIX;
    }

    //跳转到登陆界面
    private void startLogin(){
        NewLoginUtil.startToLogin(this);
    }
}