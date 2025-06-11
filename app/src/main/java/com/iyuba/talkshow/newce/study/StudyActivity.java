package com.iyuba.talkshow.newce.study;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.dlex.bizs.DLTaskInfo;
import com.iyuba.dlex.interfaces.SimpleDListener;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.local.PreferencesHelper;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.ActivityStudyBinding;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.StudySettingManager;
import com.iyuba.talkshow.lil.help_fix.view.dialog.LoadingDialog;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newce.study.dubbingNew.DubbingAboutActivity;
import com.iyuba.talkshow.newce.study.dubbingNew.DubbingNewFragment;
import com.iyuba.talkshow.newce.study.eval.EvalFragment;
import com.iyuba.talkshow.newce.study.image.ImageFragment;
import com.iyuba.talkshow.newce.study.rank.EvalrankFragment;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryBgPlayEvent;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryBgPlaySession;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryJumpData;
import com.iyuba.talkshow.newce.study.read.newRead.ui.NewReadFragment;
import com.iyuba.talkshow.newce.study.read.newRead.ui.NewReadListenEvent;
import com.iyuba.talkshow.newce.study.section.SectionFragment;
import com.iyuba.talkshow.newce.study.word.VoaWordFragment;
import com.iyuba.talkshow.newdata.Config;
import com.iyuba.talkshow.newdata.SPconfig;
import com.iyuba.talkshow.newview.StudyReportPage;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.detail.DetailDownloadPresenter;
import com.iyuba.talkshow.ui.main.drawer.Share;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.talkshow.ui.widget.BubblePopupWindow;
import com.iyuba.talkshow.util.AdBannerUtil;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.SelectPicUtils;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.talkshow.util.Util;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.manager.WordManager;
import com.iyuba.wordtest.utils.LibRxTimer;
import com.iyuba.wordtest.utils.PermissionDialogUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by carl shen on 2020/7/30
 * New Primary English, new study experience.
 */
public class StudyActivity extends BaseActivity implements StudyMvpView {
    public static final String TAG = "StudyActivity";
    public static final String VOA = "voa";
    public static final String POS = "pos";
    public static final String UNIT = "unit";
    public static final String REWARD = "reward";
    private int unitId = 0;
    private String jumpTitle = title_read;
    public TextView tvCeterTop;
    private Voa mVoa;
    //当前章节在列表中的位置
    private int positionInList = 0;


    //    private ReadFragment readFragment;//原文
    private NewReadFragment readFragment;//原文
    public EvalFragment evalFragment;//评测
    public EvalrankFragment evalRankFragment;//评测排行
    private VoaWordFragment voaWordFragment;//单词
    private ImageFragment imageFragment;//点读
    private StudyReportPage readStudy;
    public DubbingNewFragment dubbingNewFragment;//配音
    private SectionFragment sectionFragment;//阅读

    /**********************标题类型********************/
    //上方标题的数据
    public static final String title_read = "原文";
    public static final String title_eval = "跟读";
    public static final String title_rank = "排行";
    public static final String title_word = "单词";
    public static final String title_image = "点读";
    public static final String title_exercise = "习题";
    public static final String title_dubbing = "配音";
    public static final String title_section = "阅读";
    //默认显示的界面(默认第一个)
    public static final String title_default = title_read;

    private List<Fragment> list = new ArrayList<>();
//    private StudyAdapter adapter;
    private StudyNewAdapter2 adapter;
    @Inject
    PreferencesHelper prefHelper;
    @Inject
    StudyPresenter mPresenter;
    private boolean collectFlag = false;
    @Inject
    ConfigManager configManager;
    private AdBannerUtil adBannerUtil = null;

    @Override
    public boolean isSwipeBackEnable() {
        return true;
    }

    //设置是否自动播放
    private static boolean isAutoPlay = false;

    //布局样式
    private ActivityStudyBinding binding;

    public static Intent buildIntent(Context context, Voa voa, String jumpTitle, int unit, boolean isAuto, int positionInList) {
        Intent intent = new Intent();
        intent.setClass(context, StudyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(VOA, voa);
        intent.putExtra(POS, jumpTitle);
        intent.putExtra(UNIT, unit);
        //获取当前章节在列表中的位置
        intent.putExtra(StrLibrary.position, positionInList);
        isAutoPlay = isAuto;

        //设置临时和非临时数据
        PrimaryBgPlaySession.getInstance().setTempData(!isAutoPlay);

        //将所有数据设置进去
        PrimaryJumpData.getInstance().setData(voa, jumpTitle, unit, isAuto, positionInList);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        binding = ActivityStudyBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        activityComponent().inject(this);
        mPresenter.attachView(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//        mVoa = getIntent().getParcelableExtra(VOA);
//        position = getIntent().getIntExtra(POS, 0);
//        positionInList = getIntent().getIntExtra(StrLibrary.position, 0);
//        unitId = getIntent().getIntExtra(UNIT, 0);

        //新的操作数据
        mVoa = PrimaryJumpData.getInstance().getVoa();
        jumpTitle = PrimaryJumpData.getInstance().getJumpTitle();
        positionInList = PrimaryJumpData.getInstance().getPositionInList();
        unitId = PrimaryJumpData.getInstance().getUnit();

        //增加数据初始化
        mPresenter.init(mVoa);
        if (unitId == -2) {
            unitId = mPresenter.getUnitId4Voa(mVoa);
        }
        initTopBar();
        initViewPager();

        /*if (position > 0) {
            if (position < 1000) {
                binding.viewPager.setCurrentItem(position);
            } else {
                binding.viewPager.setCurrentItem(list.size() - 1);
            }
        }*/

        initClick();
        mPresenter.checkCollected(mVoa.voaId());
        initWord();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //通知栏点击进入
//        if (binding.viewPager.getAdapter().getCount() > 0) {
//            binding.viewPager.setCurrentItem(0);
//        }
        if (binding.viewPager.getChildCount()>0){
            binding.viewPager.setCurrentItem(0);
        }
    }

    private void initWord() {
        WordManager.getInstance().init(UserInfoManager.getInstance().getUserName(), String.valueOf(UserInfoManager.getInstance().getUserId()),
                App.APP_ID, Constant.EVAL_TYPE, UserInfoManager.getInstance().isVip() ? 1 : 0, App.APP_NAME_EN);
    }

    @Override
    public void onDeductIntegralSuccess(int type) {
        if (type == DetailDownloadPresenter.PDF_ENG) {
            mPresenter.getPdf(mVoa.voaId(), 1);
        } else {
            mPresenter.getPdf(mVoa.voaId(), 0);
        }
    }

    private void initClick() {
        binding.topBar.imgTopLeft.setOnClickListener(v -> finish());
    }

    private void initTopBar() {
        if (mVoa == null) {
            return;
        }
//        binding.topBar.tvTopCenter.setText(getResources().getString(R.string.study_top_name));
        if (TextUtils.isEmpty(mVoa.titleCn())) {
            binding.topBar.tvTopCenter.setText(mVoa.title());
        } else {
//            if (mVoa.titleCn().length() > 24) {
//                binding.topBar.tvTopCenter.setText(mVoa.titleCn().substring(0, 24));
//            } else {
//                binding.topBar.tvTopCenter.setText(mVoa.titleCn());
//            }
            binding.topBar.tvTopCenter.setText(mVoa.titleCn());
        }
        int courseId = configManager.getCourseId();
        if ((450 <= courseId) && (courseId <= 457)) {
            int index = mVoa.voaId() % 1000;
            binding.topBar.tvTopCenter.setText("Lesson " + index + "  " + mVoa.titleCn());
        }
        binding.topBar.imgTopLeft.setVisibility(View.VISIBLE);
        binding.topBar.reTopRight.setVisibility(View.VISIBLE);
        binding.topBar.imgTopRight.setVisibility(View.VISIBLE);
        binding.topBar.imgTopRight.setImageResource(R.drawable.study_more);
        binding.topBar.imgTopRight.setOnClickListener(v -> showPopUp(binding.topBar.imgTopRight));
    }

    private void initViewPager() {
        //上方标题的集合
        List<String> titleList = new ArrayList<>();
        tvCeterTop = binding.topBar.tvTopCenter;

//        readFragment = ReadFragment.newInstance(mVoa, position, unitId);
//        readFragment.setAutoplay(isAutoPlay);

        //阅读
        sectionFragment = SectionFragment.getInstance(mVoa, mVoa.voaId());
        titleList.add(title_section);
        list.add(sectionFragment);

        //原文
        readFragment = NewReadFragment.getInstance(mVoa, positionInList, unitId);
        titleList.add(title_read);
        list.add(readFragment);
        //评测
        evalFragment = EvalFragment.newInstance(mVoa, unitId);
        list.add(evalFragment);
        titleList.add(title_eval);
        //排行
        evalRankFragment = EvalrankFragment.newInstance(mVoa);
        list.add(evalRankFragment);
        titleList.add(title_rank);

        //查询是否存在单词（单词界面）
        List<TalkShowWords> wordsList = mPresenter.getCurUnitWords(mVoa.voaId());
        if (wordsList != null && wordsList.size() > 0) {
            voaWordFragment = VoaWordFragment.newInstance(mVoa, unitId, positionInList);
            list.add(voaWordFragment);
            titleList.add(title_word);
        }


//        if (unitId >= 0) {
//            voaWordFragment = VoaWordFragment.newInstance(mVoa, unitId,UserInfoManager.getInstance().isLogin());
//            list.add(voaWordFragment);
//        }

        //判断是否显示点读（点读界面）
        if (mVoa.clickRead() > 0) {
            imageFragment = ImageFragment.newInstance(mVoa, jumpTitle, unitId);
            list.add(imageFragment);
            titleList.add(title_image);
        }

        //判断是否显示口语秀（配音界面）
        if (!TextUtils.isEmpty(mVoa.video())) {
            dubbingNewFragment = DubbingNewFragment.newInstance(mVoa);
            list.add(dubbingNewFragment);
            titleList.add(title_dubbing);
        }

        adapter = new StudyNewAdapter2(getSupportFragmentManager(), list);
        //设置成标题
        String[] titleArray = new String[titleList.size()];
        titleList.toArray(titleArray);
        adapter.setPageTitle(titleArray);
        //设置适配器
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(list.size());
        binding.llSecond.setupWithViewPager(binding.viewPager);
        binding.llSecond.setTabIndicatorFullWidth(false);
        //根据要求，需要展示习题的标签
        if (titleList.size() > 5) {
            binding.llSecond.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            binding.llSecond.setTabMode(TabLayout.MODE_FIXED);
        }
        binding.llSecond.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (i >= titleList.size()){
                    return;
                }

                binding.llSecond.getTabAt(i).select();

                String showTitle = titleList.get(i);
                //是否是跟读
                boolean isEval = showTitle.equals("跟读");
                //是否是原文
                boolean isContent = showTitle.equals("原文");
                //是否是点读
                boolean isImage = showTitle.equals("点读");
                //是否是练习
                boolean isExercise = showTitle.equals("习题");
                //是否是配音
                boolean isDubbing = showTitle.equals("配音");
                //是否是排行
                boolean isRank = showTitle.equals("排行");

                if (!isEval && evalFragment != null) {
                    evalFragment.StopPlayer();
                }

                if (!isRank && evalRankFragment != null) {
                    evalRankFragment.dismissLoadingDialog();
                }

                if (readFragment != null) {
                    if (isContent) {
                        readFragment.setCanPlay(true);
                    } else {
                        readFragment.setCanPlay(false);
                    }
                }

                //如果当前不是imageFragment，则暂停点读的播放
                if (imageFragment != null) {
                    if (isImage) {
                        imageFragment.setCanPlay(true);
                    } else {
                        imageFragment.setCanPlay(false);
                    }
                }

                //如果不是配音界面，则暂停任何播放
                if (dubbingNewFragment != null) {
                    if (!isDubbing) {
                        dubbingNewFragment.setCanPlay(false);
                    } else {
                        dubbingNewFragment.setCanPlay(true);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        //设置跳转的位置
        int jumpPosition = 0;
        if (TextUtils.isEmpty(jumpTitle)){
            jumpPosition = titleList.size()-1;
        }else {
            for (int i = 0; i < titleList.size(); i++) {
                if (titleList.get(i).equals(jumpTitle)){
                    jumpPosition = i;
                }
            }
        }
        binding.viewPager.setCurrentItem(jumpPosition);
        binding.llSecond.getTabAt(jumpPosition).select();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if (readFragment != null) {
//            readFragment.StopPlayer(false);
//        }
        if (imageFragment != null) {
            imageFragment.setCanPlay(false);
        }
        if (evalRankFragment != null) {
            evalRankFragment.dismissLoadingDialog();
        }
        if (dubbingNewFragment != null) {
            dubbingNewFragment.setCanPlay(false);
        }

        binding.containerStudy.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        binding.containerStudy.setVisibility(View.GONE);
        LibRxTimer.getInstance().cancelTimer(timer_listenReport);
    }

    @Override
    public void setIsCollected(boolean isCollected) {
        collectFlag = isCollected;
    }

    //显示下拉弹窗
    private void showPopUp(View v) {
        BubblePopupWindow popup = new BubblePopupWindow(StudyActivity.this);
        //显示的tab名称
        String showTitle = binding.llSecond.getTabAt(binding.llSecond.getSelectedTabPosition()).getText().toString();
        //是否是配音
        boolean isDubbing = showTitle.equals("配音");
        //是否是原文
        boolean isListen = showTitle.equals("原文");
        //是否是阅读
        boolean isRead = showTitle.equals("阅读");

        //界面显示
        View bubbleView = LayoutInflater.from(this).inflate(R.layout.layout_study_menu, null, false);
        //pdf导出
        LinearLayout pdfLayout = bubbleView.findViewById(R.id.pdf);
        pdfLayout.setOnClickListener(pdfView -> {
            popup.dismiss();

            if (!UserInfoManager.getInstance().isLogin()) {
                NewLoginUtil.startToLogin(this);
                return;
            }

            downloadPdf();
        });

        //ab点播放
        LinearLayout abLayout = bubbleView.findViewById(R.id.ab);
        abLayout.setOnClickListener(abView -> {
            if (readFragment != null) {
                readFragment.setAbPlay();
            }
            popup.dismiss();
        });

        //文本滚动
        LinearLayout textSyncLayout = bubbleView.findViewById(R.id.textSync);
        ImageView textSyncImage = bubbleView.findViewById(R.id.textSync_image);
        boolean isTextSync = SPconfig.Instance().loadBoolean(Config.playPosition, true);
        textSyncImage.setImageResource(isTextSync?R.drawable.ic_study_sync_ok :R.drawable.ic_study_sync_no);
        textSyncLayout.setOnClickListener(textsyncView -> {
            boolean syncho = SPconfig.Instance().loadBoolean(Config.playPosition, true);
            syncho = !syncho;
            SPconfig.Instance().putBoolean(Config.playPosition, syncho);
            textSyncImage.setImageResource(syncho?R.drawable.ic_study_sync_ok :R.drawable.ic_study_sync_no);
            ToastUtil.showToast(mContext, syncho?"文本自动滚动开启":"文本自动滚动关闭");
            popup.dismiss();
        });

        //文章收藏
        LinearLayout collectLayout = bubbleView.findViewById(R.id.refresh);
        ImageView collectImg = bubbleView.findViewById(R.id.selected);
        boolean isCollected = collectFlag && UserInfoManager.getInstance().isLogin();
        collectImg.setImageResource(isCollected?R.drawable.ic_study_collect_ok :R.drawable.ic_study_collect_no);
        collectLayout.setOnClickListener(collectView -> {
            onCollectClick();
            popup.dismiss();
        });

        //分享
        LinearLayout shareLayout = bubbleView.findViewById(R.id.share);
        shareLayout.setVisibility(ConfigData.openShare ?View.VISIBLE:View.GONE);
        shareLayout.setOnClickListener(shareView -> {
            if (isDubbing) {
                if (dubbingNewFragment != null) {
                    dubbingNewFragment.onShareClick();
                } else {
                    onShareClick();
                }
            } else {
                onShareClick();
            }
            popup.dismiss();
        });

        //学习报告
        LinearLayout reportLayout = bubbleView.findViewById(R.id.study_report);
        ImageView reportImg = bubbleView.findViewById(R.id.report);
        boolean isReport = configManager.isStudyReport();
        reportImg.setImageResource(isReport?R.drawable.ic_study_report_ok :R.drawable.ic_study_report_no);
        reportLayout.setOnClickListener(textsyncView -> {
            boolean reportShow = configManager.isStudyReport();
            reportShow = !reportShow;
            configManager.setStudyReport(reportShow);
            reportImg.setImageResource(reportShow?R.drawable.ic_study_report_ok :R.drawable.ic_study_report_no);
            ToastUtil.showToast(mContext, reportShow?"学习报告将在播放完成后展示":"学习报告将不再展示");
            popup.dismiss();
        });

        //更新原文
        LinearLayout updateLayout = bubbleView.findViewById(R.id.update);
        updateLayout.setOnClickListener(updateView -> {
            if (dubbingNewFragment != null) {
                dubbingNewFragment.refresh();
            }
            popup.dismiss();
        });

        //下载内容
        LinearLayout downloadLayout = bubbleView.findViewById(R.id.download);
        downloadLayout.setVisibility(View.GONE);
        downloadLayout.setOnClickListener(downloadView -> {
            downloadFile();
            popup.dismiss();
        });

        //保存到相册
        LinearLayout albumLayout = bubbleView.findViewById(R.id.download_video);
        albumLayout.setOnClickListener(albumView -> {
            saveVideoToAlbum();
            popup.dismiss();
        });

        //配音排行
        LinearLayout dubbingRankLayout = bubbleView.findViewById(R.id.dubbing_rank);
        dubbingRankLayout.setOnClickListener(dubbingRankView -> {
            DubbingAboutActivity.start(this, DubbingAboutActivity.DUBBING_RANK, mVoa);
            popup.dismiss();
        });

        //专辑
        LinearLayout dubbingAlbumLayout = bubbleView.findViewById(R.id.dubbing_album);
        dubbingAlbumLayout.setOnClickListener(dubbingAlbumView -> {
            DubbingAboutActivity.start(this, DubbingAboutActivity.DUBBING_ALBUM, mVoa);
            popup.dismiss();
        });

        //切换中英文
        LinearLayout languageLayout = bubbleView.findViewById(R.id.read_language);
        String languageType = StudySettingManager.getInstance().getReadShowLanguage();
        ImageView languageImg = bubbleView.findViewById(R.id.read_language_img);
        TextView languageText = bubbleView.findViewById(R.id.read_language_text);
        boolean isAllLanguage = languageType.equals(TypeLibrary.TextShowType.ALL);
        languageImg.setImageResource(isAllLanguage?R.drawable.ic_language_english:R.drawable.ic_language_chinese);
        languageText.setText(isAllLanguage?"切换为英文":"切换为双语");
        languageLayout.setOnClickListener(languageView -> {
            popup.dismiss();

            if (sectionFragment == null) {
                ToastUtil.showToast(this, "阅读界面未初始化～");
                return;
            }

            String showText = languageText.getText().toString();
            if (showText.equals("切换为双语")) {
                sectionFragment.switchTextType(TypeLibrary.TextShowType.ALL);
            } else if (showText.equals("切换为英文")) {
                sectionFragment.switchTextType(TypeLibrary.TextShowType.EN);
            }
        });

        //界面配置显示
        //原文界面
        if (isListen){
            pdfLayout.setVisibility(View.VISIBLE);
            abLayout.setVisibility(View.VISIBLE);
            textSyncLayout.setVisibility(View.VISIBLE);
            reportLayout.setVisibility(View.VISIBLE);
        }else {
            pdfLayout.setVisibility(View.GONE);
            abLayout.setVisibility(View.GONE);
            textSyncLayout.setVisibility(View.GONE);
            reportLayout.setVisibility(View.GONE);
        }

        //配音界面
        if (isDubbing){
            //下载
            if (dubbingNewFragment!=null){
                //下载内容
//                boolean isHasDownloadContent = dubbingNewFragment.isAudioAndVideoExist();
//                downloadLayout.setVisibility(isHasDownloadContent?View.GONE:View.VISIBLE);

                //保存视频到本地
                boolean isHasVideoToAlbum = dubbingNewFragment.isAlbumVideoExist();
                albumLayout.setVisibility(isHasVideoToAlbum?View.GONE:View.VISIBLE);
            }

            updateLayout.setVisibility(View.VISIBLE);
            dubbingRankLayout.setVisibility(View.VISIBLE);
            dubbingAlbumLayout.setVisibility(View.VISIBLE);
        }else {
            albumLayout.setVisibility(View.GONE);
            updateLayout.setVisibility(View.GONE);
            dubbingRankLayout.setVisibility(View.GONE);
            dubbingAlbumLayout.setVisibility(View.GONE);
        }

        //阅读界面
        if (isRead) {
            languageLayout.setVisibility(View.VISIBLE);
        } else {
            languageLayout.setVisibility(View.GONE);
        }

        popup.setBubbleView(bubbleView); // 设置气泡内容
        popup.show(v, Gravity.BOTTOM, 1000, true); // 显示弹窗
    }

    public void onShareClick() {
        if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
            ToastUtil.showToast(mContext, "分享功能需要打开网络数据连接");
            return;
        }

        if (ConfigData.openShare) {
            Share.prepareWechatMessage(this, configManager.getCourseId(), mVoa, mPresenter.getInregralService(), UserInfoManager.getInstance().getUserId());
        } else {
            ToastUtil.showToast(mContext, "对不起，分享暂时不支持");
        }
    }

    public void onCollectClick() {
        if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
            ToastUtil.showToast(mContext, "收藏功能需要打开网络数据连接");
            return;
        }

        if (!UserInfoManager.getInstance().isLogin()) {
            NewLoginUtil.startToLogin(this);
            return;
        }

        if (collectFlag) {
            mPresenter.safeDelete(mVoa.voaId());
        } else {
            mPresenter.safeUpdate(mVoa.voaId());
        }
    }

    private void showCreditDialong() {
        new AlertDialog.Builder(this)
                .setTitle("温馨提示")
                .setMessage("非VIP用户每篇PDF需扣除20积分，VIP用户享受无限制下载PDF")
                .setPositiveButton("购买会员", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NewVipCenterActivity.start(StudyActivity.this, NewVipCenterActivity.BENYINGYONG);
                    }
                }).setNegativeButton("直接下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showPDFDialong();
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public void showPDFDialong() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] choices = {"英文", "中英双语"};
        builder.setTitle("请选择导出文件的语言")
                .setItems(choices, (dialog, which) -> {
                    dialog.dismiss();
                    switch (which) {
                        case 0:
                            if (mPresenter.checkIsFree()) {
                                mPresenter.getPdf(mVoa.voaId(), 1);
                            } else if (UserInfoManager.getInstance().isLogin()) {
                                mPresenter.deductIntegral(DetailDownloadPresenter.PDF_ENG);
                            } else {
                                NewLoginUtil.startToLogin(this);
                            }
                            break;
                        case 1:
                            if (mPresenter.checkIsFree()) {
                                mPresenter.getPdf(mVoa.voaId(), 0);
                            } else if (UserInfoManager.getInstance().isLogin()) {
                                mPresenter.deductIntegral(DetailDownloadPresenter.PDF_BOTH);
                            } else {
                                NewLoginUtil.startToLogin(this);
                            }
                            break;
                        default:
                            break;
                    }
                });
        builder.show();
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
                onDownloadPdf("" + mVoa.voaId(), downloadPath);
            } else {
                onDownloadPdf(mVoa.titleCn().trim(), downloadPath);
            }
        });
        Button neutral = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL);
        neutral.setOnClickListener(v -> {
            if (ConfigData.openShare) {
                String title = mVoa.title() + " PDF";
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

    private DLManager mDLManager;
    private DLTaskInfo task = null;

    public void onDownloadPdf(String title, String url) {
        String downPath = "/sdcard/iyuba/" + App.APP_NAME_EN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            downPath = getExternalFilesDir(null).getAbsolutePath();
            SelectPicUtils.downloadQFile(mContext, url, title + ".pdf");
            ToastUtil.showToast(mContext, "文件将会下载到 /sdcard/Download/" + title + ".pdf");
            return;
        } else {
            File audioDir = new File(downPath);
            if (!audioDir.exists()) {
                audioDir.mkdirs();
            }
        }
        ToastUtil.showToast(mContext, "文件将会下载到 " + downPath + "/" + title + ".pdf");
        File audioFile = new File(downPath, title + ".pdf");
        if (audioFile.exists()) {
            audioFile.delete();
        }
        if (mDLManager == null) {
            mDLManager = DLManager.getInstance();
        }
        if (task == null) {
            task = new DLTaskInfo();
            task.tag = title + ".pdf";
            task.filePath = downPath;
            task.fileName = title + ".pdf";
            task.initalizeUrl(url);
            task.setDListener(new DListener(downPath));
            mDLManager.addDownloadTask(task);
        } else {
            switch (task.state) {
                case DLTaskInfo.TaskState.INIT:
                    ToastUtil.showToast(mContext, "正在初始化");
                    break;
                case DLTaskInfo.TaskState.WAITING:
                    ToastUtil.showToast(mContext, "下载任务正在等待");
                    break;
                case DLTaskInfo.TaskState.PREPARING:
                case DLTaskInfo.TaskState.DOWNLOADING:
                    mDLManager.stopTask(task);
                    break;
                case DLTaskInfo.TaskState.ERROR:
                case DLTaskInfo.TaskState.PAUSING:
                    task.setDListener(new DListener(downPath));
                    mDLManager.resumeTask(task);
                    break;
                default:
                    break;
            }
        }
    }

    private class DListener extends SimpleDListener {
        private int total;
        private final String mDownPath;

        DListener(String headlines) {
            mDownPath = headlines;
        }

        @Override
        public void onStart(String fileName, String realUrl, int fileLength) {
            total = fileLength;
        }

        @Override
        public void onProgress(int progress) {
            int percentage = getCurrentPercentage(progress);
        }

        @Override
        public void onStop(int progress) {
        }

        @Override
        public void onFinish(File file) {
            task = null;
            Log.e(TAG, "DListener onFinish mDownPath " + mDownPath);
        }

        @Override
        public void onError(int status, String error) {
            Log.e(TAG, "DListener onError status " + status);
            Log.e(TAG, "DListener onError error " + error);
        }

        private int getCurrentPercentage(int progress) {
            int result = 0;
            if (total >= 10000) {
                result = progress / (total / 100);
            } else if (total > 0) {
                result = (progress * 100) / total;
            }
            return result;
        }
    }

    //刷新整个vp界面
    private void refreshVPData() {
        initTopBar();

        list = new ArrayList<>();
        List<String> titleList = new ArrayList<>();

//        readFragment = ReadFragment.newInstance(mVoa, position, unitId);
//        readFragment.setAutoplay(isAutoPlay);

        //阅读
        sectionFragment = SectionFragment.getInstance(mVoa, mVoa.voaId());
        list.add(sectionFragment);
        titleList.add(title_section);

        //原文
        readFragment = NewReadFragment.getInstance(mVoa, positionInList, unitId);
        list.add(readFragment);
        titleList.add(title_read);

        //评测
        evalFragment = EvalFragment.newInstance(mVoa, unitId);
        list.add(evalFragment);
        titleList.add(title_eval);

        //评测排行
        evalRankFragment = EvalrankFragment.newInstance(mVoa);
        list.add(evalRankFragment);
        titleList.add(title_rank);

        //单词
        List<TalkShowWords> wordsList = mPresenter.getCurUnitWords(mVoa.voaId());
        if (wordsList != null && wordsList.size() > 0) {
            voaWordFragment = VoaWordFragment.newInstance(mVoa, unitId, positionInList);
            list.add(voaWordFragment);
            titleList.add(title_word);
        }

//        if (unitId >= 0) {
//            voaWordFragment = VoaWordFragment.newInstance(mVoa, unitId,UserInfoManager.getInstance().isLogin());
//            list.add(voaWordFragment);
//
//            if (unitId>0){
//                titleList.add(title_word);
//            }
//        }

        //点读
        if (mVoa.clickRead() > 0) {
            imageFragment = ImageFragment.newInstance(mVoa, jumpTitle, unitId);
            list.add(imageFragment);
            titleList.add(title_image);
        }

        //配音
        if (!TextUtils.isEmpty(mVoa.video())) {
            dubbingNewFragment = DubbingNewFragment.newInstance(mVoa);
            list.add(dubbingNewFragment);
            titleList.add(title_dubbing);
        }

        //标题
        String[] titleArray = new String[titleList.size()];
        titleList.toArray(titleArray);
        //刷新
        adapter.refreshListAndTitle(list, titleArray);
        binding.viewPager.setOffscreenPageLimit(list.size());

        binding.llSecond.setTabIndicatorFullWidth(false);
        //根据要求，需要展示习题的标签
        if (titleList.size() > 5) {
            binding.llSecond.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            binding.llSecond.setTabMode(TabLayout.MODE_FIXED);
        }
        binding.llSecond.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    /************************加载弹窗************************/
    private LoadingDialog loadingDialog;

    private void showLoading() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
            loadingDialog.create();
        }
        loadingDialog.setMsg("正在加载原文学习报告～");
        loadingDialog.show();
    }

    private void closeLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**************************刷新数据******************************/
    //后台音频播放操作
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayEvent(PrimaryBgPlayEvent event) {
        if (event.getShowType().equals(PrimaryBgPlayEvent.event_data_refresh)) {
            //刷新数据
            if (!isAutoPlay) {
                return;
            }

            //判断是否登录并且未登录<3
            positionInList = event.getShowPosition();
            if (positionInList >= 3 && !UserInfoManager.getInstance().isLogin()) {
                ToastUtil.showToast(this, "试听课程已结束，请登录后继续使用");
                NewLoginUtil.startToLogin(this);
                return;
            }

            //当前的数据
            if (positionInList < PrimaryBgPlaySession.getInstance().getVoaList().size()) {
                mVoa = PrimaryBgPlaySession.getInstance().getVoaList().get(positionInList);
                unitId = mVoa.UnitId;

                //将数据再次放置进去
                PrimaryJumpData.getInstance().setData(mVoa, StudyActivity.title_read, unitId, isAutoPlay, positionInList);

                refreshVPData();
            }
        }
    }

    //小学听力学习报告操作
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReportEvent(NewReadListenEvent event) {
        //开启学习报告
        if (event.getType().equals(NewReadListenEvent.type_showReport)) {
            if (readFragment != null) {
                readFragment.stopLoading();
            }

            //展示学习报告(这里处理是否展示学习报告和下一个操作)
            if (configManager.isStudyReport()) {
                //需要展示
                showStudyReport(event.getReward());
                return;
            }

            if (PrimaryBgPlaySession.getInstance().isTempData()) {
                //临时数据没有下一个
                return;
            }

            //下一个切换
            EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_switch));
        }

        //关闭学习报告
        if (event.getType().equals(NewReadListenEvent.type_closeReport)) {
            binding.containerStudy.setVisibility(View.GONE);
            LibRxTimer.getInstance().cancelTimer(timer_listenReport);

            //下一个
            EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_switch));
        }

        //关闭学习报告定时器
        if (event.getType().equals(NewReadListenEvent.type_closeReportTimer)) {
            LibRxTimer.getInstance().cancelTimer(timer_listenReport);
        }
    }

    //学习报告定时器
    private static final String timer_listenReport = "timer_listenReport";

    //显示学习报告
    private void showStudyReport(String reward) {
        Log.d("学习报告的奖励信息", "奖励信息：" + reward);
        StudyReportPage reportPage = StudyReportPage.newInstance(mVoa, 1, unitId, reward, TypeLibrary.RefreshDataType.study_read);
        binding.containerStudy.setVisibility(View.VISIBLE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container_study, reportPage).show(reportPage).commitNowAllowingStateLoss();

        //设置5s定时器弹窗
        LibRxTimer.getInstance().timerInMain(timer_listenReport, 5000L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                binding.containerStudy.setVisibility(View.GONE);
                LibRxTimer.getInstance().cancelTimer(timer_listenReport);

                //下一个
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_switch));
            }
        });
    }

    //保存视频到相册
    private void saveVideoToAlbum() {
        List<Pair<String, Pair<String, String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Pair<>("存储权限", "用于下载视频文件并保存到相册")));

        PermissionDialogUtil.getInstance().showMsgDialog(this, pairList, new PermissionDialogUtil.OnPermissionResultListener() {
            @Override
            public void onGranted(boolean isSuccess) {
                if (isSuccess) {
                    if (dubbingNewFragment != null) {
                        dubbingNewFragment.saveVideoToAlbum();
                    }
                }
            }
        });
    }

    //下载内容
    private void downloadFile() {
        List<Pair<String, Pair<String, String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Pair<>("存储权限", "下载并保存音视频文件，用于辅助配音功能使用")));

        PermissionDialogUtil.getInstance().showMsgDialog(this, pairList, new PermissionDialogUtil.OnPermissionResultListener() {
            @Override
            public void onGranted(boolean isSuccess) {
                if (isSuccess) {
                    dubbingNewFragment.isStudyCallBack = true;

                    if (dubbingNewFragment != null) {
                        dubbingNewFragment.checkPermission(true);
                    }
                }
            }
        });
    }

    //下载pdf
    private void downloadPdf() {
        List<Pair<String, Pair<String, String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Pair<>("存储权限", "用于下载并保存pdf文件")));

        PermissionDialogUtil.getInstance().showMsgDialog(this, pairList, new PermissionDialogUtil.OnPermissionResultListener() {
            @Override
            public void onGranted(boolean isSuccess) {
                if (isSuccess) {
                    if (!UserInfoManager.getInstance().isVip()) {
                        showCreditDialong();
                        return;
                    }

                    showPDFDialong();
                }
            }
        });
    }
}
