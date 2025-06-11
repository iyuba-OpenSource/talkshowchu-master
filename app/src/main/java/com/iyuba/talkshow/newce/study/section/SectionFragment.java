package com.iyuba.talkshow.newce.study.section;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.FragmentFixSectionBinding;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.StudySettingManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.interstitial.AdInterstitialShowManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.interstitial.AdInterstitialViewBean;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.upload.AdUploadManager;
import com.iyuba.talkshow.lil.help_fix.ui.study.section.SectionAdapter;
import com.iyuba.talkshow.lil.help_fix.util.FixUtil;
import com.iyuba.talkshow.lil.help_fix.view.dialog.LoadingDialog;
import com.iyuba.talkshow.lil.help_fix.view.dialog.searchWord.SearchWordDialog;
import com.iyuba.talkshow.lil.help_mvp.util.DateUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.help_mvp.view.NoScrollLinearLayoutManager;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @title:  阅读界面
 * @date: 2023/8/31 18:07
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class SectionFragment extends BaseFragment implements SectionView{
    private static final String TAG = "SectionFragment";

    @Inject
    SectionPresenter presenter;
    //数据
    private Voa voa;
    private int voaId;
    //适配器
    private SectionAdapter sectionAdapter;
    //绑定
    private FragmentFixSectionBinding binding;
    //进入的时间
    private long startTime;
    //当前的单词数量
    private long wordCount = 0;
    //阅读速度
    private static final int readSpeed = 600;
    //当前类型
    private String bookType = TypeLibrary.BookType.junior_middle;
    //弹窗
    private LoadingDialog loadingDialog;
    //单词查询弹窗
    private SearchWordDialog searchWordDialog;

    //是否已经点击广告
    private boolean isAdClick = false;

    public static SectionFragment getInstance(Voa voa,int voaId){
        SectionFragment fragment = new SectionFragment();
        Bundle args = new Bundle();
        args.putParcelable(StrLibrary.data, voa);
        args.putInt(StrLibrary.voaId,voaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
        presenter.attachView(this);

        voa = getArguments().getParcelable(StrLibrary.data);
        voaId = getArguments().getInt(StrLibrary.voaId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFixSectionBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initList();

        binding.refreshLayout.autoRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        closeLoadingDialog();
        closeSearchWordDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //关闭广告
        AdInterstitialShowManager.getInstance().stopInterstitialAd();
        //关闭操作
        presenter.detachView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            startTime = System.currentTimeMillis();
        }
    }

    /*********************初始化*********************/
    private void initToolbar(){
        binding.toolbar.getRoot().setVisibility(View.GONE);
    }

    private void initList(){
        binding.submit.setVisibility(View.GONE);

        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(false);
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    binding.refreshLayout.finishRefresh(false);
                    ToastUtil.showToast(getActivity(),"请链接网络后重试～");
                    return;
                }

                presenter.getVoaTexts(voaId);
            }
        });

        sectionAdapter = new SectionAdapter(getActivity(),new ArrayList<>());
        NoScrollLinearLayoutManager manager = new NoScrollLinearLayoutManager(getActivity(),false);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(sectionAdapter);
        sectionAdapter.setOnWordSearchListener(new SectionAdapter.onWordSearchListener() {
            @Override
            public void onWordSearch(String selectText) {
                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后重试～");
                    return;
                }

                if (!TextUtils.isEmpty(selectText)) {
                    //先处理下数据
                    selectText = filterWord(selectText);

                    if (selectText.matches("^[a-zA-Z]*")){
                        showSearchWordDialog(selectText);
                    }else {
                        ToastUtil.showToast(getActivity(), "请取英文单词");
                    }
                } else {
                    ToastUtil.showToast(getActivity(), "请取英文单词");
                }
            }
        });

        //这里调整一下，新概念系列是增加了数据库实现了每个章节都是不同的类型，这里先设置成统一设置类型，之后再进行处理
        sectionAdapter.switchTextType(StudySettingManager.getInstance().getReadShowLanguage());

        binding.submit.setOnClickListener(v->{
            if (!NetworkUtil.isConnected(getActivity())){
                ToastUtil.showToast(getActivity(),"请链接网络后重试～");
                return;
            }

            if (!UserInfoManager.getInstance().isLogin()){
                NewLoginUtil.startToLogin(getActivity());
                return;
            }


            //获取当前读取的进度
            ////这里要求和学习报告中一致，则先去掉毫秒，然后转换
            long startNewTime = startTime/1000*1000;
            long endNewTime = System.currentTimeMillis()/1000*1000;

            long progressTime = endNewTime-startNewTime;
            float showProgressTime = progressTime*1.0f/(1000*60);
            int wordReadSpeed = (int) (wordCount/showProgressTime);

            if (wordReadSpeed>readSpeed){
                showReadWarnDialog();
            }else {
                showReadSubmitDialog(wordReadSpeed,startNewTime,endNewTime);
            }
        });
    }

    /*********************回调数据********************/
    @Override
    public void showVoaText(List<Pair<String, String>> list) {
        binding.refreshLayout.finishRefresh(true);

        if (list!=null&&list.size()>0){
            sectionAdapter.refreshData(list);
            binding.submit.setVisibility(View.VISIBLE);

            //单词数量
            wordCount = presenter.getWordCount(list);
        }else {
            sectionAdapter.refreshData(new ArrayList<>());
            binding.submit.setVisibility(View.GONE);
            ToastUtil.showToast(getActivity(),"暂无课程详情数据～");
        }
    }

    @Override
    public void showReadReportResult(boolean isSubmit) {
        closeLoadingDialog();
        if (isSubmit){
            ToastUtil.showToast(getActivity(),"提交成功");

            //加载插屏广告
            loadAd();
        }else {
            ToastUtil.showToast(getActivity(),"提交失败，服务器链接超时，请稍后重试");
            binding.submit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showClickAdResult(boolean isSuccess, String showMsg) {
        ToastUtil.showToast(getActivity(),showMsg);

        if (isSuccess){
            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
        }
    }

    /*************************辅助功能*************************/
    //展示进度禁止弹窗
    private void showReadWarnDialog(){
        new AlertDialog.Builder(getActivity())
                .setTitle("阅读报告")
                .setMessage("你认真读完这篇文章了吗？请用正常速度阅读")
                .setCancelable(false)
                .setNegativeButton("确定", null)
                .show();
    }

    //展示进度提交弹窗
    private void showReadSubmitDialog(int readSpeedInt,long newStartTime,long newEndTime){
        long readTime = newEndTime-newStartTime;
        String readTimeStr = DateUtil.transPlayFormat(DateUtil.MINUTE,readTime);

        new AlertDialog.Builder(getActivity())
                .setTitle("阅读报告")
                .setMessage("当前阅读统计：\n文章单词数："+wordCount+"\n阅读时长："+readTimeStr+"\n阅读速度："+readSpeedInt+"单词/分钟\n是否提交阅读记录？")
                .setCancelable(false)
                .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        binding.submit.setVisibility(View.GONE);

                        showLoadingDialog("正在提交阅读报告~");
                        presenter.submitReadReport(FixUtil.getTopic(bookType), String.valueOf(voaId),voa.titleCn(),wordCount,newStartTime,newEndTime);
                    }
                }).setNegativeButton("取消",null)
                .show();
    }

    //显示弹窗
    private void showLoadingDialog(String msg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.setMsg(msg);
        loadingDialog.show();
    }

    //关闭弹窗
    private void closeLoadingDialog(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    //切换显示文本类型
    public void switchTextType(String textType){
        StudySettingManager.getInstance().setReadShowLanguage(textType);
//        CommonDataManager.saveReadLanguageSettingToDB(bookType,chapterBean.getBookId(),voaId,textType);
        if (sectionAdapter!=null){
            sectionAdapter.switchTextType(textType);
        }else {
            ToastUtil.showToast(getActivity(),"界面未初始化");
        }
    }

    /*********************单词查询****************/
    //显示查询弹窗
    private void showSearchWordDialog(String word){
        searchWordDialog = new SearchWordDialog(getActivity(),word);
        searchWordDialog.create();
        searchWordDialog.show();

        searchWordDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                sectionAdapter.notifyDataSetChanged();
            }
        });
    }

    //关闭查询弹窗
    private void closeSearchWordDialog(){
        if (searchWordDialog!=null&&searchWordDialog.isShowing()){
            searchWordDialog.dismiss();
        }
    }

    //处理单词数据
    public String filterWord(String selectText){
        selectText = selectText.replace(".","");
        selectText = selectText.replace(",","");
        selectText = selectText.replace("!","");
        selectText = selectText.replace("?","");
        selectText = selectText.replace("'","");

        return selectText;
    }

    /********************************新的插屏广告******************************/
    // TODO: 2024/4/28 根据展姐要求，这里在阅读完成后显示半插屏的广告
    //是否已经获取了奖励
    private boolean isGetRewardByClickAd = false;
    //界面数据
    private AdInterstitialViewBean interstitialViewBean = null;

    //显示插屏广告
    private void showInterstitialAd() {
        //请求广告
        if (interstitialViewBean == null){
            interstitialViewBean = new AdInterstitialViewBean(new AdInterstitialShowManager.OnAdInterstitialShowListener() {
                @Override
                public void onLoadFinishAd() {

                }

                @Override
                public void onAdShow(String adType) {

                }

                @Override
                public void onAdClick(String adType, boolean isJumpByUserClick, String jumpUrl) {
                    if (isJumpByUserClick){
                        //跳转界面操作
                    }

                    //点击广告操作
                    if (!isGetRewardByClickAd){
                        isGetRewardByClickAd = true;

                        String fixShowType = AdShowUtil.NetParam.AdShowPosition.show_interstitial;
                        String fixAdType = adType;
                        AdUploadManager.getInstance().clickAdForReward(fixShowType, fixAdType, new AdUploadManager.OnAdClickCallBackListener() {
                            @Override
                            public void showClickAdResult(boolean isSuccess, String showMsg) {
                                ToastUtil.showToast(getActivity(),showMsg);

                                if (isSuccess){
                                    EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
                                }
                            }
                        });
                    }
                }

                @Override
                public void onAdClose(String adType) {

                }

                @Override
                public void onAdError(String adType) {

                }
            });
            AdInterstitialShowManager.getInstance().setShowData(getActivity(),interstitialViewBean);
        }
        AdInterstitialShowManager.getInstance().showInterstitialAd();
        //重置数据
//        isGetRewardByClickAd = false;
    }
    //加载插屏广告
    private void loadAd(){
        if (getActivity()==null|| getActivity().isFinishing() || getActivity().isDestroyed()){
            return;
        }

        if (AdBlocker.getInstance().shouldBlockAd() || UserInfoManager.getInstance().isVip()) {
            return;
        }

        showInterstitialAd();
    }
}
