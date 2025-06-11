package com.iyuba.talkshow.newce;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.imooclib.data.local.IMoocDBManager;
import com.iyuba.imooclib.data.model.CourseTypeDataBean;
import com.iyuba.imooclib.ui.content.ContentActivity;
import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.ArticleRecord;
import com.iyuba.talkshow.data.model.CategoryFooter;
import com.iyuba.talkshow.data.model.LoopItem;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.data.model.TitleSeries;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.FragmentMainBinding;
import com.iyuba.talkshow.event.EvalEvent;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.event.LoginOutEvent;
import com.iyuba.talkshow.event.ReadEvent;
import com.iyuba.talkshow.event.RefreshWordEvent;
import com.iyuba.talkshow.event.SelectBookEvent;
import com.iyuba.talkshow.event.StudyUploadEvent;
import com.iyuba.talkshow.injection.PerFragment;
import com.iyuba.talkshow.lil.help_fix.manager.StudyUserManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.template.AdTemplateShowManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.template.AdTemplateViewBean;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.template.OnAdTemplateShowListener;
import com.iyuba.talkshow.lil.help_fix.view.dialog.LoadingDialog;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newce.search.newSearch.NewSearchActivity;
import com.iyuba.talkshow.newce.study.StudyActivity;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryBgPlayEvent;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryBgPlayManager;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryBgPlaySession;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryJumpData;
import com.iyuba.talkshow.newdata.Config;
import com.iyuba.talkshow.newdata.Playmanager;
import com.iyuba.talkshow.newdata.SPconfig;
import com.iyuba.talkshow.ui.about.AboutActivity;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.courses.coursechoose.CourseChooseActivity;
import com.iyuba.talkshow.ui.courses.coursechoose.OpenFlag;
import com.iyuba.talkshow.ui.widget.divider.MainGridItemDivider;
import com.iyuba.talkshow.util.DialogFactory;
import com.iyuba.talkshow.util.LogUtil;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.event.WordTestEvent;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import personal.iyuba.personalhomelibrary.event.UserNameChangeEvent;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 中小学-单列表界面
 * Created by carl shen on 2020/8/7
 * New Junior English, new study experience.
 */
@PerFragment
public class MainFragment extends BaseFragment implements MainFragMvpView {
    public static final String TAG = "MainFragment";
    public static final int SPAN_COUNT = 2;
    private boolean ifresh = false;
    private FragmentMainBinding binding;
    int pageNum = 1;
    private IMoocDBManager mDBManager;

    @Inject
    public MainFragPresenter mMainPresenter;
    @Inject
    MainAdapter mListAdapter;
    @Inject
    ConfigManager configManager;

    //处理从详情页返回首页时的数据刷新
    private int toJumpPosition = -1;
    private int toJumpPositionBackUp = -1;

    /*MainAdapter.LoopCallback loopCallback = new MainAdapter.LoopCallback() {
        @Override
        public void onLoopClick(int voaId) {
            mMainPresenter.getVoaById(voaId);
        }
    };

    MainAdapter.DataChangeCallback adapterDataRefreshCallback = new MainAdapter.DataChangeCallback() {
        @Override
        public void onClick(View v, CategoryFooter category, int limit, String ids) {
            mMainPresenter.loadMoreVoas(category, limit, ids);
        }
    };*/
    private boolean isAtSync = false;
    private LoadingDialog mLoadingDialog;
    private boolean isAtFront;
    private int courseId = App.getBookDefaultShowData().getBookId();
    private String curTitle;
    private final int defaultUi = 1;
    private long starttime;


    public static MainFragment getInstance() {
        MainFragment mainFragment = new MainFragment();
        return mainFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        ifresh = false;
        isAtFront = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            fragmentComponent().inject(this);
        } catch (Exception var1) {
            if (var1 != null) {
                var1.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainPresenter.attachView(this);
        EventBus.getDefault().register(this);

        //现在加载弹窗
        showLoadingDialog();

        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));

        if (NetStateUtil.isConnected(TalkShowApplication.getContext())) {
//            mMainPresenter.getVoaSeries("" + configManager.getCourseId());
            mMainPresenter.loadVoas(configManager.getCourseId());
        } else {
            mMainPresenter.loadVoas(configManager.getCourseId());
        }
        if (mListAdapter == null) {
            mListAdapter = new MainAdapter();
        }
        mListAdapter.setVoaCallback(new MainAdapter.VoaCallback() {
            @Override
            public void onItemClick(Voa voa, int pos, int positionInList) {
                //设置跳转位置
                toJumpPosition = positionInList;
                toJumpPositionBackUp = positionInList;

                //之前先是限制前三个不需要登录，后面的vip使用；之后是需要登录才能使用
                if (UserInfoManager.getInstance().isLogin() || pos < 3) {
                    PrimaryBgPlaySession.getInstance().getCurData();

                    startDetailActivity(voa, StudyActivity.title_default, voa.UnitId, positionInList);
                } else {
                    startLogin();
                }
            }

            @Override
            public void onVoaClick(Voa voa, int pos, int positionInList) {
                //设置跳转位置
                toJumpPosition = positionInList;
                toJumpPositionBackUp = positionInList;

                if (UserInfoManager.getInstance().isLogin() || pos < 3) {
                    startDetailActivity(voa, StudyActivity.title_read, voa.UnitId, positionInList);
                } else {
                    startLogin();
                }
            }

            @Override
            public void onDownloadClick(Voa voa) {
                if (UserInfoManager.getInstance().isLogin()) {
//                    downloadMedia(voa);
                } else {
                    startLogin();
                }
            }

            @Override
            public void onEvalClick(Voa voa, int pos, int positionInList) {
                //设置跳转位置
                toJumpPosition = positionInList;
                toJumpPositionBackUp = positionInList;

                if (UserInfoManager.getInstance().isLogin() || pos < 3) {
                    startDetailActivity(voa, StudyActivity.title_eval, voa.UnitId, positionInList);
                } else {
                    startLogin();
                }
            }

            @Override
            public void onMoocClick(Voa voa) {
                //暂停播放音频
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_pause));
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_pause));

                if (mDBManager == null) {
                    mDBManager = IMoocDBManager.getInstance();
                }
                CourseTypeDataBean courseType = mDBManager.findCoursePackTypeById(voa.categoryId());
                String theDesc = courseType != null ? courseType.desc : Constant.MOOC_TYPE;
                Intent intent = ContentActivity.buildIntent(mContext, voa.categoryId(), voa.classId());
                startActivity(intent);
            }
        });
        mListAdapter.setLoopCallback(new MainAdapter.LoopCallback() {
            @Override
            public void onLoopClick(int voaId) {
                mMainPresenter.getVoaById(voaId);
            }
        });
        mListAdapter.setDataChangeCallback(new MainAdapter.DataChangeCallback() {
            @Override
            public void onClick(View v, CategoryFooter category, int limit, String ids) {
                mMainPresenter.loadMoreVoas(category, limit, ids);
            }
        });

        binding.refreshLayout.setOnRefreshListener(refreshLayout -> refreshData());
        binding.recyclerView.setAdapter(mListAdapter);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 2;
            }
        });
        binding.recyclerView.addItemDecoration(new MainGridItemDivider(getContext()));
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。

        binding.refreshWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseChooseActivity.start(mContext, OpenFlag.FINISH, OpenFlag.TO_DETAIL);
            }
        });

        //初中英语暂时没有这个东西，暂时关闭，需要可以打开(内容没写完，后续根据小学英语的功能增加上即可)
        binding.titleSearch.setVisibility(View.GONE);
        binding.titleSearch.setOnClickListener(v -> {
            jumpSearch();
        });

        //初始化控制器操作
        initBottomControl();

        //设置标题样式
        setTitleText();
    }

    private void setTitleText() {
        if (configManager == null) {
            return;
        }
        courseId = configManager.getCourseId();
        String courseTitle = configManager.getCourseTitle();
        if (TextUtils.isEmpty(courseTitle)) {
            curTitle = App.getBookDefaultShowData().getBookName();
            binding.titleWord.setText(curTitle);
        } else {
            curTitle = courseTitle; //App.getPrimaryTitle(courseTitle);
            binding.titleWord.setText(curTitle);
        }
        if (curTitle.contains("(")) {
            curTitle = curTitle.substring(0, curTitle.indexOf("("));
        } else if (curTitle.contains("（")) {
            curTitle = curTitle.substring(0, curTitle.indexOf("（"));
        }
        mListAdapter.setBookTitle(curTitle);
        mListAdapter.setBookId(courseId);

//        List<SeriesData> getSeries = mMainPresenter.getSeries4Id(courseId);
//        if ((getSeries != null) && (getSeries.size() > 0)) {
//            mListAdapter.setMicroId(getSeries.get(0).getHaveMicro());
//        }

        //这里使用新的方式处理，上边的方式容易引起anr
        mMainPresenter.getSeriesById(courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SeriesData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNext(SeriesData seriesData) {
                        if (seriesData != null) {
                            mListAdapter.setMicroId(seriesData.getHaveMicro());
                        }
                        mListAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SelectBookEvent event) {
        if (courseId != event.bookId) {
            isAtSync = true;
        }
        setTitleText();
        if (NetStateUtil.isConnected(TalkShowApplication.getContext())) {
            mMainPresenter.loadVoas(event.bookId);
        } else {
            mMainPresenter.loadVoas(event.bookId);
        }
        TalkShowApplication.getSubHandler().post(new Runnable() {
            @Override
            public void run() {
                List<TalkShowWords> listWords = WordDataBase.getInstance(TalkShowApplication.getContext()).getTalkShowWordsDao().getBookWords(event.bookId);
                if ((listWords == null) || (listWords.size() < Constant.PRODUCT_WORDS)) {
                    mMainPresenter.getWordsById(event.bookId);
                }
            }
        });
    }

    private void refreshData() {
        LogUtil.d("初中英语首页数据刷新", "刷新位置-自动刷新");

        pageNum = 1;
        mMainPresenter.getVoaSeries("" + configManager.getCourseId());
        ifresh = true;
        binding.refreshLayout.finishRefresh(2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(getContext());

        isToOtherPage = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(getContext());

        isToOtherPage = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        ifresh = true;
        isAtFront = false;
    }

    @Override
    public void onDestroyView() {
        //音频暂停
        EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_pause));
        EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_pause));
        //关闭广告
        AdTemplateShowManager.getInstance().stopTemplateAd(adTemplateKey);
        //数据关闭
        mMainPresenter.detachView();
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void resetUserNickName(UserNameChangeEvent event) {
        UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), null);
    }

    @Override
    public void showAlertDialog(String msg, DialogInterface.OnClickListener ocl) {
        AlertDialog alert = new AlertDialog.Builder(getContext()).create();
        alert.setTitle(R.string.alert_title);
        alert.setMessage(msg);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alert_btn_ok), ocl);
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.alert_btn_cancel),
                (dialog, which) -> {
                });
        alert.show();
    }

    @Override
    public void startAboutActivity(String versionCode, String appUrl) {
        Intent intent = AboutActivity.buildIntent(getContext(), versionCode, appUrl);
        startActivity(intent);
    }

    @Override
    public void showVoas(List<Voa> voas) {
        dismissLoadingDialog();

        if ((voas == null) || (voas.size() < 1)) {
            showToast("请下拉刷新进行内容更新。");
            return;
        }

        //将数据设置到后台播放会话中
        PrimaryBgPlaySession.getInstance().setVoaList(voas);

        Playmanager.getInstance().setPlayList(voas);
        mListAdapter.setVoas(voas);
        mListAdapter.notifyDataSetChanged();
        LogUtil.d("初中英语首页数据刷新", "刷新位置2");

        // TODO: 2025/2/28 这里是之前的逻辑，但是好像有点不对
        // TODO: 2025/2/28  为啥需要从接口中重新刷新下数据，忘记了，后期需要的话再打开
//        mMainPresenter.getTitleSeries("" + configManager.getCourseId());

        //刷新广告显示
        refreshTemplateAd();
    }

    @Override
    public void showTitleSeries(List<TitleSeries> voas) {
        LogUtil.d("MainFragment", " showTitleSeries ");
        mListAdapter.setTitleSeries(voas);
        mListAdapter.notifyDataSetChanged();
        LogUtil.d("初中英语首页数据刷新", "刷新位置3");
    }

    @Override
    public void showVoasByCategory(List<Voa> voas, CategoryFooter category) {
        Playmanager.getInstance().setPlayList(voas);
        mListAdapter.setVoasByCategory(voas, category);
    }

    @Override
    public void showMoreVoas(List<Voa> voas) {
        if ((voas == null) || (voas.size() < 1)) {
            Log.e("MainFragment", "showMoreVoas is null. ");
            return;
        } else {
            Playmanager.getInstance().setPlayList(voas);
            mMainPresenter.getTitleSeries("" + configManager.getCourseId());
        }
        mListAdapter.setVoas(voas);
        mListAdapter.notifyDataSetChanged();
        LogUtil.d("初中英语首页数据刷新", "刷新位置1");
    }

    @Override
    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mContext);
            mLoadingDialog.create();
        }
        mLoadingDialog.setMsg("正在加载中～");
        mLoadingDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void refreshNetVoaData() {
        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.show(getActivity(),"请链接网络后手动下拉刷新数据");
            return;
        }

        binding.refreshLayout.autoRefresh();
    }

    @Override
    public void showVoasEmpty() {
        mListAdapter.setVoas(Collections.emptyList());
        mListAdapter.notifyDataSetChanged();
        LogUtil.d("初中英语首页数据刷新", "刷新位置4");
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(getContext(), getString(R.string.error_loading)).show();
    }

    @Override
    public void setBanner(List<LoopItem> loopItemList) {
        mListAdapter.setBanner(loopItemList);
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startDetailActivity(Voa voa, String jumpTitle, int unit, int positionInList) {
        if (voa != null) {
            if (!ifresh) {
                SPconfig.Instance().putInt(Config.currVoaId, voa.voaId());
                //跳转界面
                startActivity(StudyActivity.buildIntent(getContext(), voa, jumpTitle, unit, true, positionInList));
            }
            ifresh = false;
        }
    }

    @Override
    public void dismissRefreshingView() {
        binding.refreshLayout.finishRefresh();
        binding.refreshLayout.finishLoadMore();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EvalSuccess(EvalEvent event) {
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
            LogUtil.d("初中英语首页数据刷新", "刷新位置5");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ReadSuccess(ReadEvent event) {
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
            LogUtil.d("初中英语首页数据刷新", "刷新位置6");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SetBookWords(RefreshWordEvent event) {
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
            LogUtil.d("初中英语首页数据刷新", "刷新位置7");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void WordStepTest(WordTestEvent event) {
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
            LogUtil.d("初中英语首页数据刷新", "刷新位置8");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StudyUploadTest(StudyUploadEvent event) {
        if (mListAdapter != null) {
//            mListAdapter.notifyDataSetChanged();
            if (toJumpPosition != -1) {
                mListAdapter.notifyItemChanged(toJumpPosition);
                toJumpPosition = -1;
            } else {
                mListAdapter.notifyDataSetChanged();
            }
            LogUtil.d("初中英语首页数据刷新", "刷新位置9");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event) {
        mMainPresenter.loadVoas(courseId);
        TalkShowApplication.getSubHandler().post(new Runnable() {
            @Override
            public void run() {
                mMainPresenter.SyncMicroStudyRecord();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginOutEvent event) {
        Log.e(TAG, "LoginOutEvent need reset. ");
        //人教版审核限制
        int bookId = App.getBookDefaultShowData().getBookId();
        String bookName = App.getBookDefaultShowData().getBookName();

        if (configManager != null) {
            bookId = configManager.getCourseId() == 0 ? bookId : configManager.getCourseId();
            bookName = configManager.getCourseTitle() == null ? bookName : configManager.getCourseTitle();

            configManager.putCourseId(bookId);
            configManager.putCourseTitle(bookName);
        }
        setTitleText();

        mMainPresenter.loadVoas(bookId);
    }

    //跳转登陆
    private void startLogin() {
        NewLoginUtil.startToLogin(getActivity());
    }

    /*************************辅助功能**********************/
    //跳转查询界面
    public void jumpSearch() {
        //暂停播放
        EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_pause));
        EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_pause));

        if (!UserInfoManager.getInstance().isLogin()) {
            startLogin();
            return;
        }

        NewSearchActivity.start(mContext);
    }

    /********************************新的播放器操作*******************************/
    //是否切换到其他界面
    private boolean isToOtherPage = false;

    //获取播放倍速
    private float getPlaySpeed() {
        if (!UserInfoManager.getInstance().isVip()) {
            return 1.0f;
        }

        return StudyUserManager.getInstance().getPlaySpeed();
    }

    //获取播放类型
    private int getPlayMode() {
        //0-顺序播放、1-单曲循环、2-随机播放
        return SPconfig.Instance().loadInt(Config.playMode);
    }

    //音频播放回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayEvent(PrimaryBgPlayEvent event) {
        //播放
        if (event.getShowType().equals(PrimaryBgPlayEvent.event_control_play)) {
            Voa voa = PrimaryBgPlaySession.getInstance().getCurData();
            if (voa != null) {
                //进行播放
                ExoPlayer exoPlayer = PrimaryBgPlayManager.getInstance().getPlayService().getPlayer();
                exoPlayer.setPlaybackSpeed(getPlaySpeed());
                if (exoPlayer != null && !exoPlayer.isPlaying()) {
                    exoPlayer.play();
                }
                //显示图标
                binding.reBottom.setVisibility(View.VISIBLE);
                binding.imgPlay.setImageResource(R.mipmap.image_pause);
                if (voa != null) {
                    binding.tvTitle.setText(voa.titleCn());
                    binding.tvTitleCn.setText(voa.title());
                }
                //通知栏处理
                PrimaryBgPlayManager.getInstance().getPlayService().showNotification(false, true, voa);
            }
        }

        //暂停
        if (event.getShowType().equals(PrimaryBgPlayEvent.event_control_pause)) {
            Voa voa = PrimaryBgPlaySession.getInstance().getCurData();
            if (voa != null) {
                //暂停播放
                ExoPlayer exoPlayer = PrimaryBgPlayManager.getInstance().getPlayService().getPlayer();
                exoPlayer.setPlaybackSpeed(getPlaySpeed());
                if (exoPlayer != null && exoPlayer.isPlaying()) {
                    exoPlayer.pause();
                }
                //显示图标
                if (binding.reBottom.getVisibility() == View.VISIBLE) {
                    binding.reBottom.setVisibility(View.VISIBLE);
                }
                binding.imgPlay.setImageResource(R.mipmap.image_play);
                if (voa != null) {
                    binding.tvTitle.setText(voa.titleCn());
                    binding.tvTitleCn.setText(voa.title());

                    //先获取当前是否存在数据，如果数据是100，则不进行处理
                    List<ArticleRecord> recordData = mMainPresenter.getArticleByVoaId(voa.voaId());
                    if (recordData != null && recordData.size() > 0) {
                        ArticleRecord record = recordData.get(0);
                        if (record.is_finish() != 1) {
                            //计算进度显示
                            long curTime = exoPlayer.getCurrentPosition();
                            long totalTime = exoPlayer.getDuration();
                            int percent = (int) (curTime * 100 / totalTime);
                            if (percent > 100) {
                                percent = 100;
                            }
                            if (percent > 0) {
                                //这里刷新当前的播放进度
                                mMainPresenter.saveArticleRecord(ArticleRecord.builder().setUid(UserInfoManager.getInstance().getUserId())
                                        .setVoa_id(voa.voaId())
                                        .setCurr_time((int) curTime / 1000)
                                        .setTotal_time((int) totalTime / 1000)
                                        .setType(0).setIs_finish(0)
                                        .setPercent(percent)
                                        .build());
                                StudyUploadTest(new StudyUploadEvent());
                            }
                        }
                    } else {
                        //计算进度显示
                        long curTime = exoPlayer.getCurrentPosition();
                        long totalTime = exoPlayer.getDuration();
                        int percent = (int) (curTime * 100 / totalTime);
                        if (percent > 100) {
                            percent = 100;
                        }
                        if (percent > 0) {
                            //这里刷新当前的播放进度
                            mMainPresenter.saveArticleRecord(ArticleRecord.builder().setUid(UserInfoManager.getInstance().getUserId())
                                    .setVoa_id(voa.voaId())
                                    .setCurr_time((int) curTime / 1000)
                                    .setTotal_time((int) totalTime / 1000)
                                    .setType(0).setIs_finish(0)
                                    .setPercent(percent)
                                    .build());
                            StudyUploadTest(new StudyUploadEvent());
                        }
                    }
                }
                //通知栏处理
                PrimaryBgPlayManager.getInstance().getPlayService().showNotification(false, false, voa);
            }
        }

        //加载完成
        if (event.getShowType().equals(PrimaryBgPlayEvent.event_audio_prepareFinish)) {
            if (!isToOtherPage) {
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_play));
            }
        }

        //播放完成
        if (event.getShowType().equals(PrimaryBgPlayEvent.event_audio_completeFinish)) {
            this.toJumpPosition = -1;
            this.toJumpPositionBackUp = -1;

            EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_pause));
            //当前的播放器和数据
            Voa curVoa = PrimaryBgPlaySession.getInstance().getCurData();
            ExoPlayer exoPlayer = PrimaryBgPlayManager.getInstance().getPlayService().getPlayer();
            //这里刷新当前的播放进度
            if (!isToOtherPage) {
                //计算进度显示
                mMainPresenter.saveArticleRecord(ArticleRecord.builder()
                        .setUid(UserInfoManager.getInstance().getUserId())
                        .setVoa_id(curVoa.voaId())
                        .setCurr_time((int) exoPlayer.getCurrentPosition() / 1000)
                        .setTotal_time((int) exoPlayer.getDuration() / 1000)
                        .setType(0)
                        .setIs_finish(1)
                        .setPercent(100)
                        .build());
                StudyUploadTest(new StudyUploadEvent());
            }

            //如果在当前界面没有切换，则进行处理
            if (!isToOtherPage) {
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_switch));
            }
        }

        //切换音频
        if (event.getShowType().equals(PrimaryBgPlayEvent.event_audio_switch)) {
            //根据当前类型选择下一个音频的位置
            ExoPlayer exoPlayer = PrimaryBgPlayManager.getInstance().getPlayService().getPlayer();

            int playMode = getPlayMode();
            if (playMode == 0) {
                //顺序播放
                if (PrimaryBgPlaySession.getInstance().getPlayPosition() < PrimaryBgPlaySession.getInstance().getVoaList().size() - 1) {
                    //刷新数据显示
                    int nextPosition = PrimaryBgPlaySession.getInstance().getPlayPosition() + 1;

                    LogUtil.d("下一个数据显示", "数据id：" + nextPosition);
                    if (isToOtherPage) {
                        EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_data_refresh, nextPosition));
                    } else {
                        //获取音频并播放
                        PrimaryBgPlaySession.getInstance().setPlayPosition(nextPosition);
                        //设置跳转数据
                        Voa nextVoa = PrimaryBgPlaySession.getInstance().getCurData();
                        PrimaryJumpData.getInstance().setData(nextVoa,StudyActivity.title_default,nextVoa.UnitId,true,nextPosition);
                        //初始化操作
                        initPlayerAndPlayAudio(PrimaryBgPlaySession.getInstance().getCurData());
                    }
                } else {
                    //这里不需要重新加载
                    PrimaryBgPlayManager.getInstance().getPlayService().setPrepare(false);
                }
            } else if (playMode == 1) {
                //单曲循环
                exoPlayer.seekTo(0);
                //这里不需要重新加载
                PrimaryBgPlayManager.getInstance().getPlayService().setPrepare(false);

                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_play));
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_play));
            } else if (playMode == 2) {
                //随机播放
                //获取随机数
                int randomIndex = (int) (PrimaryBgPlaySession.getInstance().getVoaList().size() * Math.random());
                if (randomIndex == PrimaryBgPlaySession.getInstance().getPlayPosition()) {
                    if (randomIndex == PrimaryBgPlaySession.getInstance().getVoaList().size() - 1) {
                        randomIndex--;
                    }
                    randomIndex++;
                }

                if (isToOtherPage) {
                    EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_data_refresh, randomIndex));
                } else {
                    //获取音频并播放
                    PrimaryBgPlaySession.getInstance().setPlayPosition(randomIndex);
                    //设置跳转数据
                    Voa randomVoa = PrimaryBgPlaySession.getInstance().getCurData();
                    PrimaryJumpData.getInstance().setData(randomVoa, StudyActivity.title_default,randomVoa.UnitId,true,randomIndex);
                    //初始化操作
                    initPlayerAndPlayAudio(PrimaryBgPlaySession.getInstance().getCurData());
                }
            }
        }

        //隐藏控制栏
        if (event.getShowType().equals(PrimaryBgPlayEvent.event_control_hide)) {
            binding.reBottom.setVisibility(View.GONE);
            //检查是否暂停了，暂停了则不刷新即可
            ExoPlayer exoPlayer = PrimaryBgPlayManager.getInstance().getPlayService().getPlayer();
            if (exoPlayer != null && exoPlayer.isPlaying()) {
                EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_pause));
            }
        }
    }

    //初始化底部控制器显示
    private void initBottomControl() {
        binding.reBottom.setVisibility(View.GONE);
        binding.imgPlay.setOnClickListener(v -> {
            this.toJumpPosition = toJumpPositionBackUp;

            //取消临时数据
            PrimaryBgPlaySession.getInstance().setTempData(false);

            ExoPlayer exoPlayer = PrimaryBgPlayManager.getInstance().getPlayService().getPlayer();
            if (exoPlayer != null) {
                if (exoPlayer.isPlaying()) {
                    EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_pause));
                } else {
                    EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_play));
                }
            }
        });
        binding.reBottom.setOnClickListener(v -> {
            this.toJumpPosition = toJumpPositionBackUp;

            //取消临时数据
            PrimaryBgPlaySession.getInstance().setTempData(false);
            //跳转界面
            Voa curVoa = PrimaryBgPlaySession.getInstance().getCurData();
            int curPosition = PrimaryBgPlaySession.getInstance().getPlayPosition();
            //跳转信息
            Intent intent = StudyActivity.buildIntent(getActivity(), curVoa, StudyActivity.title_read, curVoa.UnitId,  true, curPosition);
            startActivity(intent);
        });
        binding.closePlay.setOnClickListener(v -> {
            EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_hide));
        });
    }

    //初始化操作音频播放
    private void initPlayerAndPlayAudio(Voa tempVoa) {
        String soundUrl = getPlayUrl(tempVoa);
        playAudio(soundUrl);
        //设置操作的voaId
        PrimaryBgPlaySession.getInstance().setPreVoaId(tempVoa.voaId());
    }

    //播放音频
    private void playAudio(String urlOrPath) {
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
        PrimaryBgPlayManager.getInstance().getPlayService().getPlayer().setMediaItem(mediaItem);
        PrimaryBgPlayManager.getInstance().getPlayService().getPlayer().prepare();
    }

    //获取播放的音频
    private String getPlayUrl(Voa tempVoa) {
        String audioUrl = Constant.getSoundMp3Url(tempVoa.sound(), tempVoa.voaId());
        File audioFile = StorageUtil.getAudioFile(TalkShowApplication.getInstance(), tempVoa.voaId());
        if (audioFile.exists()) {
            audioUrl = audioFile.getAbsolutePath();
        }
        return audioUrl;
    }

    /*****************************设置新的信息流广告************************/
    //当前信息流广告的key
    private String adTemplateKey = MainFragment.class.getName();
    //模版广告数据
    private AdTemplateViewBean templateViewBean = null;
    //显示广告
    private void showTemplateAd() {
        if (templateViewBean == null) {
            templateViewBean = new AdTemplateViewBean(R.layout.item_ad_mix, R.id.template_container, R.id.ad_whole_body, R.id.native_main_image, R.id.native_title, binding.recyclerView, mListAdapter, new OnAdTemplateShowListener() {
                @Override
                public void onLoadFinishAd() {

                }

                @Override
                public void onAdShow(String showAdMsg) {

                }

                @Override
                public void onAdClick() {

                }
            });
            AdTemplateShowManager.getInstance().setShowData(adTemplateKey, templateViewBean);
        }
        AdTemplateShowManager.getInstance().showTemplateAd(adTemplateKey,getActivity());
    }

    //刷新广告操作[根据类型判断刷新还是隐藏]
    private void refreshTemplateAd(){
//        if (!AdBlocker.getInstance().shouldBlockAd() && !UserInfoManager.getInstance().isVip()) {
//            showTemplateAd();
//        } else {
//            AdTemplateShowManager.getInstance().stopTemplateAd(adTemplateKey);
//        }

        if (!NetworkUtil.isConnected(getActivity())){
            return;
        }

        if (!AdBlocker.getInstance().shouldBlockAd()){
            showTemplateAd();
        }
    }
}
