package com.iyuba.talkshow.lil.help_fix.ui.study;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.AtyStudyBinding;
import com.iyuba.talkshow.lil.help_fix.data.bean.BookChapterBean;
import com.iyuba.talkshow.lil.help_fix.data.bean.ChapterDetailBean;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.StudySettingManager;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterCollectEntity;
import com.iyuba.talkshow.lil.help_fix.ui.study.eval.EvalFragment;
import com.iyuba.talkshow.lil.help_fix.ui.study.rank.RankFragment;
import com.iyuba.talkshow.lil.help_fix.ui.study.read.ReadFragment;
import com.iyuba.talkshow.lil.help_fix.ui.study.section.SectionFragment;
import com.iyuba.talkshow.lil.help_fix.util.PdfUtil;
import com.iyuba.talkshow.lil.help_fix.util.ShareUtil;
import com.iyuba.talkshow.lil.help_fix.view.dialog.LoadingDialog;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingActivity;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.help_mvp.util.StackUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxTimer;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.ui.widget.BubblePopupWindow;
import com.iyuba.wordtest.utils.PermissionDialogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 学习界面
 * @date: 2023/5/22 15:50
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class StudyActivity extends BaseViewBindingActivity<AtyStudyBinding> implements StudyView {

    //类型
    private String types;
    //书籍的id
    private String bookId;
    //id(voaId或者unitId)
    private String voaId;
    //本章节的数据
    private BookChapterBean chapterBean;

    //上下章节数据
    private Pair<Integer,Pair<BookChapterBean,BookChapterBean>> switchChapterPair;

    private StudyAdapter studyAdapter;

    private StudyPresenter presenter;
    //加载弹窗
    private LoadingDialog loadingDialog;

    //原文界面
    private ReadFragment readFragment;
    //阅读界面
    private SectionFragment sectionFragment;


//    //听力记录弹窗
//    private ListenStudyReportDialog listenDialog;
    //听力记录弹窗标识位
    private String listenDialogTag = "listenDialogTag";

    public static void start(Context context,String types,String bookId,String voaId){
        Intent intent = new Intent();
        intent.setClass(context,StudyActivity.class);
        intent.putExtra(StrLibrary.types,types);
        intent.putExtra(StrLibrary.bookId,bookId);
        intent.putExtra(StrLibrary.voaid,voaId);
        context.startActivity(intent);
    }

    public static Intent buildIntent(Context context,String types,String bookId,String voaId){
        Intent intent = new Intent();
        intent.setClass(context,StudyActivity.class);
        intent.putExtra(StrLibrary.types,types);
        intent.putExtra(StrLibrary.bookId,bookId);
        intent.putExtra(StrLibrary.voaid,voaId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        types = getIntent().getStringExtra(StrLibrary.types);
        bookId = getIntent().getStringExtra(StrLibrary.bookId);
        voaId = getIntent().getStringExtra(StrLibrary.voaid);

        presenter = new StudyPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initList();

        presenter.getChapterDetail(types,bookId,voaId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        presenter.detachView();
    }

    @Override
    protected void onPause() {
        super.onPause();

//        if (listenDialog!=null){
//            listenDialog.closeSelf();
//        }
        RxTimer.getInstance().cancelTimer(listenDialogTag);
    }

    /**************初始化数据*************/
    private void initToolbar(){
        binding.toolbar.tvTopCenter.setText("选择课程");
        binding.toolbar.imgTopLeft.setVisibility(View.VISIBLE);
        binding.toolbar.imgTopLeft.setImageResource(R.mipmap.img_back);
        binding.toolbar.imgTopLeft.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });
        binding.toolbar.imgTopRight.setVisibility(View.VISIBLE);
        binding.toolbar.imgTopRight.setImageResource(R.drawable.study_more);
        binding.toolbar.imgTopRight.setOnClickListener(v->{
            showMoreDialog();
        });
    }

    private void initList(){
        //进入这里的时候，停止新概念的音频播放
        EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.concept_play));

        binding.titleView.setTabMode(TabLayout.MODE_FIXED);
//        binding.titleView.setTabGravity(TabLayout.GRAVITY_CENTER);
        binding.titleView.setTabIndicatorFullWidth(false);
        binding.titleView.setTabTextColors(ResUtil.getInstance().getColor(R.color.gray), ResUtil.getInstance().getColor(R.color.colorPrimaryDark));
        binding.titleView.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        studyAdapter = new StudyAdapter(this,new ArrayList<>());
        binding.viewPager2.setAdapter(studyAdapter);
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.titleView.getTabAt(position).select();

                String showTitle = binding.titleView.getTabAt(position).getText().toString();
                if (showTitle.equals("原文")||showTitle.equals("听力")){
                    if (readFragment!=null){
                        //关闭切换
                        readFragment.switchPage(false);
                    }
                }else {
                    if (readFragment!=null){
                        //关闭播放
                        readFragment.pausePlay(false);
                        //设置切换
                        readFragment.switchPage(true);
                    }
                }
            }
        });
    }

    /**************刷新数据***************/
    private void refreshData(){
        //清除数据
        binding.titleView.removeAllTabs();

        //获取数据进行展示
        chapterBean = presenter.margeChapterData(types,voaId);
        if (chapterBean==null){
            ToastUtil.showToast(this,"获取数据失败，请重试～");
            return;
        }

        //标题
        binding.toolbar.tvTopCenter.setText(chapterBean.getTitleEn());
        //显示数据
        List<String> titleList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        titleList.add("阅读");
        sectionFragment = SectionFragment.getInstance(types,voaId);
        fragmentList.add(sectionFragment);

        titleList.add("原文");
        readFragment = ReadFragment.getInstance(types,voaId);
        fragmentList.add(readFragment);

        titleList.add("评测");
        fragmentList.add(EvalFragment.getInstance(types,voaId));

        titleList.add("排行");
        fragmentList.add(RankFragment.getInstance(types,voaId));

//        if (chapterBean.isShowWord()){
//            titleList.add("知识");
//        }

//        if (chapterBean.isShowImage()){
//            titleList.add("点读");
//        }

        if (chapterBean.isShowVideo()){
//            titleList.add("配音");
//            fragmentList.add(DubbingFragment.getInstance(types,voaId));
        }

//        if (chapterBean.isShowExercise()){
//            titleList.add("练习");
//        }

        if (fragmentList!=null&&fragmentList.size()>0){
            studyAdapter.refreshData(fragmentList);
        }

        if (titleList!=null&&titleList.size()>0){
            binding.titleView.setVisibility(View.VISIBLE);
            //根据要求，需要展示习题的标签
            if (titleList.size() > 5) {
                binding.titleView.setTabMode(TabLayout.MODE_SCROLLABLE);
            } else {
                binding.titleView.setTabMode(TabLayout.MODE_FIXED);
            }

            new TabLayoutMediator(binding.titleView, binding.viewPager2, true, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    tab.setText(titleList.get(position));
                }
            }).attach();
        }else {
            binding.titleView.setVisibility(View.GONE);
        }

        //如果标题显示一个，则不用显示
        if (titleList.size()<=1){
            binding.titleView.setVisibility(View.GONE);
        }else {
            binding.titleView.setVisibility(View.VISIBLE);
        }

        binding.viewPager2.setCurrentItem(1);
        binding.titleView.getTabAt(1).select();
    }

    @Override
    public void showData(List<ChapterDetailBean> list) {
        stopLoading();

        if (list!=null){
            if (list.size()>0){
                refreshData();
                binding.toolbar.reTopRight.setVisibility(View.VISIBLE);
            }else {
                ToastUtil.showToast(this,"暂无此章节的详情数据");
                binding.toolbar.reTopRight.setVisibility(View.INVISIBLE);
            }
        }else {
            ToastUtil.showToast(this,"获取详情数据失败~");
            binding.toolbar.reTopRight.setVisibility(View.INVISIBLE);
        }
    }


    /*******************加载样式*****************/
    //显示加载
    @Override
    public void showLoading(String msg) {
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
            loadingDialog.create();
        }
        loadingDialog.setMessage(msg);
        loadingDialog.show();
    }

    @Override
    public void showCollectArticle(boolean isSuccess,boolean isCollect) {
        if (isSuccess){
            if (isCollect){
                ChapterCollectEntity entity = new ChapterCollectEntity(
                        types,
                        voaId,
                        String.valueOf(UserInfoManager.getInstance().getUserId()),
                        bookId,
                        chapterBean.getPicUrl(),
                        chapterBean.getTitleEn(),
                        chapterBean.getTitleCn()
                );
                CommonDataManager.saveChapterCollectDataToDB(entity);
                ToastUtil.showToast(getApplicationContext(),"收藏文章成功～");
            }else {
                CommonDataManager.deleteChapterCollectDataToDB(types,voaId,UserInfoManager.getInstance().getUserId());
                ToastUtil.showToast(getApplicationContext(),"取消收藏文章成功～");
            }
        }else {
            ToastUtil.showToast(this,"请检查网络后重试～");
        }
    }

    //停止加载
    private void stopLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    /*********************更多弹窗******************/
    //分享功能
    private void shareAbility(){
        String title = chapterBean.getTitleCn();
        String text = chapterBean.getTitleCn()+"\t"+chapterBean.getTitleEn();
        String imageUrl = chapterBean.getPicUrl();
        String shareUrl = ShareUtil.getInstance().getCourseShareUrl(types,chapterBean.getLevel(),voaId);
        if (types.equals(TypeLibrary.BookType.bookworm)
                ||types.equals(TypeLibrary.BookType.newCamstory)
                ||types.equals(TypeLibrary.BookType.newCamstoryColor)){
            //小说
            shareUrl = ShareUtil.getInstance().getCourseShareUrl(types,chapterBean.getLevel(),chapterBean.getOrderNumber());
        }

        ShareUtil.getInstance().shareArticle(this,chapterBean.getTypes(),chapterBean.getBookId(),chapterBean.getVoaId(),UserInfoManager.getInstance().getUserId(),title,text,imageUrl,shareUrl);
    }

    //pdf下载
    private void downloadPdf(){
        List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","用于下载并保存pdf文件")));

        PermissionDialogUtil.getInstance().showMsgDialog(this, pairList, new PermissionDialogUtil.OnPermissionResultListener() {
            @Override
            public void onGranted(boolean isSuccess) {
                if (isSuccess){
                    if (UserInfoManager.getInstance().isLogin()){
                        PdfUtil.getInstance().checkNext(StudyActivity.this,types,chapterBean.getTitleEn(),chapterBean.getPicUrl(),voaId);
                    }else {
                        NewLoginUtil.startToLogin(StudyActivity.this);
                    }
                }
            }
        });
    }

    //文章收藏
    private void collectArticle(){
        if (UserInfoManager.getInstance().isLogin()){
            ChapterCollectEntity entity = CommonDataManager.getChapterCollectDataFromDB(types,voaId, String.valueOf(UserInfoManager.getInstance().getUserId()));
            if (entity==null){
                //收藏文章
                presenter.collectArticle(types,voaId,String.valueOf(UserInfoManager.getInstance().getUserId()), true);
            }else {
                //取消收藏文章
                presenter.collectArticle(types,voaId,String.valueOf(UserInfoManager.getInstance().getUserId()), false);
            }
        }else {
            NewLoginUtil.startToLogin(this);
        }
    }

    //显示更多弹窗
    private void showMoreDialog(){
        if (readFragment!=null){
            readFragment.pausePlay(false);
        }

        BubblePopupWindow popupWindow = new BubblePopupWindow(this);
        //显示的tab名称
        String showTitle = binding.titleView.getTabAt(binding.titleView.getSelectedTabPosition()).getText().toString();
        View bubbleView = LayoutInflater.from(this).inflate(R.layout.layout_study_menu,null);
        //分享
        LinearLayout shareLayout = bubbleView.findViewById(R.id.share);
        shareLayout.setOnClickListener(v->{
            shareAbility();
            popupWindow.dismiss();
        });
        //导出pdf
        LinearLayout pdfLayout = bubbleView.findViewById(R.id.pdf);
        pdfLayout.setOnClickListener(v->{
            downloadPdf();
            popupWindow.dismiss();
        });
        //文章收藏
        LinearLayout collectLayout = bubbleView.findViewById(R.id.refresh);
        ImageView collectImage = bubbleView.findViewById(R.id.selected);
        ChapterCollectEntity collectData = CommonDataManager.getChapterCollectDataFromDB(types,voaId,String.valueOf(UserInfoManager.getInstance().getUserId()));
        collectImage.setImageResource(collectData==null?R.drawable.ic_study_collect_no :R.drawable.ic_study_collect_ok);
        collectLayout.setOnClickListener(v->{
            collectArticle();
            popupWindow.dismiss();
        });

        LinearLayout abView = bubbleView.findViewById(R.id.ab);
        LinearLayout autoScrollLayout = bubbleView.findViewById(R.id.textSync);
        if (showTitle.equals("原文")||showTitle.equals("口语")){
            //ab区间播放
            abView.setVisibility(View.VISIBLE);
            abView.setOnClickListener(v->{
                if (readFragment!=null){
                    readFragment.setABPlay();
                }
                popupWindow.dismiss();
            });
            //文本自动滚动
            autoScrollLayout.setVisibility(View.VISIBLE);
            ImageView autoScrollImage = bubbleView.findViewById(R.id.textSync_image);
            boolean isRoll = StudySettingManager.getInstance().getRollOpen();
            autoScrollImage.setImageResource(isRoll?R.drawable.ic_study_sync_ok :R.drawable.ic_study_sync_no);
            autoScrollLayout.setOnClickListener(v->{
                if (readFragment!=null){
                    readFragment.setTextSync();
                }
                popupWindow.dismiss();
            });
        }else {
            abView.setVisibility(View.GONE);
            autoScrollLayout.setVisibility(View.GONE);
        }

        //阅读界面切换中英文
        LinearLayout languageView = bubbleView.findViewById(R.id.read_language);
        if (showTitle.equals("阅读")){
            languageView.setVisibility(View.VISIBLE);
            String languageType = StudySettingManager.getInstance().getReadShowLanguage();

            ImageView languageImg = bubbleView.findViewById(R.id.read_language_img);
            TextView languageText = bubbleView.findViewById(R.id.read_language_text);
            if (languageType.equals(TypeLibrary.TextShowType.EN)){
                languageImg.setImageResource(R.drawable.ic_language_chinese);
                languageText.setText("切换双语");
            }else if (languageType.equals(TypeLibrary.TextShowType.ALL)){
                languageImg.setImageResource(R.drawable.ic_language_english);
                languageText.setText("切换英文");
            }

            languageView.setOnClickListener(v->{
                if (sectionFragment==null){
                    ToastUtil.showToast(this,"阅读界面未初始化～");
                    return;
                }

                popupWindow.dismiss();

                String showText = languageText.getText().toString();
                if (showText.equals("切换为双语")){
                    sectionFragment.switchTextType(TypeLibrary.TextShowType.ALL);
                }else if (showText.equals("切换为英文")){
                    sectionFragment.switchTextType(TypeLibrary.TextShowType.EN);
                }
            });
        }else {
            languageView.setVisibility(View.GONE);
        }

        //非必要的全都隐藏
        LinearLayout reportLayout = bubbleView.findViewById(R.id.study_report);
        reportLayout.setVisibility(View.GONE);
        LinearLayout downloadLayout = bubbleView.findViewById(R.id.download);
        downloadLayout.setVisibility(View.GONE);
        LinearLayout updateLayout = bubbleView.findViewById(R.id.update);
        updateLayout.setVisibility(View.GONE);
        LinearLayout downVideoLayout = bubbleView.findViewById(R.id.download_video);
        downVideoLayout.setVisibility(View.GONE);
        LinearLayout rankLayout = bubbleView.findViewById(R.id.dubbing_rank);
        rankLayout.setVisibility(View.GONE);
        LinearLayout albumLayout = bubbleView.findViewById(R.id.dubbing_album);
        albumLayout.setVisibility(View.GONE);

        popupWindow.setBubbleView(bubbleView);
        popupWindow.show(binding.toolbar.reTopRight, Gravity.BOTTOM,1000,true);
    }

    /*****************回调数据*******************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.listenDialog)){
//            if (AccountManager.getInstance().checkUserLogin()
//                    &&ConfigManager.Instance().getSendListenReport()){
//                //显示弹窗
//
//                //延迟关闭
//                RxTimer.timerInMain(listenDialogTag, 5000L, new RxTimer.RxActionListener() {
//                    @Override
//                    public void onAction(long number) {
//                        if (listenDialog!=null){
//                            listenDialog.closeSelf();
//                        }
//                    }
//                });
//            }
        }

        //上一曲
        if (event.getType().equals(TypeLibrary.RefreshDataType.study_pre)){
            //获取上下章节数据
            switchChapterPair = presenter.getCurChapterIndex(chapterBean.getTypes(),chapterBean.getLevel(),chapterBean.getBookId(),chapterBean.getVoaId());
            if ((switchChapterPair.first == -1)
                    ||(switchChapterPair.first == 0)){
                ToastUtil.showToast(this,"当前已经是第一个");
                return;
            }

            BookChapterBean preChapterBean = switchChapterPair.second.first;
            if (preChapterBean==null){
                ToastUtil.showToast(this,"未找到上一章节数据");
                return;
            }

            voaId = preChapterBean.getVoaId();
            presenter.getChapterDetail(types,bookId,voaId);
        }

        //下一曲
        if (event.getType().equals(TypeLibrary.RefreshDataType.study_next)){
            //获取上下章节数据
            switchChapterPair = presenter.getCurChapterIndex(chapterBean.getTypes(),chapterBean.getLevel(),chapterBean.getBookId(),chapterBean.getVoaId());
            if ((switchChapterPair.first == -1)
                    ||(switchChapterPair.first == -2)){
                ToastUtil.showToast(this,"当前已经是最后一个");
                return;
            }

            BookChapterBean nextChapterBean = switchChapterPair.second.second;
            if (nextChapterBean==null){
                ToastUtil.showToast(this,"未找到下一章节数据");
                return;
            }

            voaId = nextChapterBean.getVoaId();
            presenter.getChapterDetail(types,bookId,voaId);
        }

        //随机播放
        if (event.getType().equals(TypeLibrary.RefreshDataType.study_random)){
            BookChapterBean randomChapterData = presenter.getRandomChapterDataNew(chapterBean.getTypes(),chapterBean.getLevel(),chapterBean.getBookId(),voaId);
            if (randomChapterData==null){
                ToastUtil.showToast(this,"未找到下一章节数据");
                return;
            }

            voaId = randomChapterData.getVoaId();
            presenter.getChapterDetail(types,bookId,voaId);
        }

        //奖励显示--toast
        if (event.getType().equals(TypeLibrary.RefreshDataType.reward_refresh_toast)){
            String showMsg = event.getMsg();
            if (!TextUtils.isEmpty(showMsg)){
                ToastUtil.showToast(this,showMsg);

                //刷新用户信息并填充
                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
            }
        }

        //奖励显示--弹窗
        if (event.getType().equals(TypeLibrary.RefreshDataType.reward_refresh_dialog)){
            String showMsg = event.getMsg();
            if (!TextUtils.isEmpty(showMsg)){
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("奖励信息")
                        .setMessage(showMsg)
                        .setPositiveButton("确定",null)
                        .show();

                //刷新用户信息并填充
                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
            }
        }
    }
}
