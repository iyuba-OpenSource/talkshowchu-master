package com.iyuba.talkshow.lil.novel.ui.main;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.databinding.FragmentListRefreshBinding;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.event.LoginOutEvent;
import com.iyuba.talkshow.lil.help_fix.data.bean.BookChapterBean;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.NovelBookChooseManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.template.AdTemplateShowManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.template.AdTemplateViewBean;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.template.OnAdTemplateShowListener;
import com.iyuba.talkshow.lil.help_fix.ui.lesson.LessonPresenter;
import com.iyuba.talkshow.lil.help_fix.ui.lesson.LessonView;
import com.iyuba.talkshow.lil.help_fix.ui.study.StudyActivity;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingFragment;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 小说列表界面
 * @date: 2023/7/3 17:34
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NovelListFragment extends BaseViewBindingFragment<FragmentListRefreshBinding> implements LessonView {

    private LessonPresenter presenter;
    private NovelListAdapter listAdapter;

    public static NovelListFragment getInstance(){
        NovelListFragment fragment = new NovelListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        presenter = new LessonPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();

        //先查询本地数据，没有查询网络数据
        presenter.loadLocalChapterData(NovelBookChooseManager.getInstance().getBookType(),String.valueOf(NovelBookChooseManager.getInstance().getBookLevel()), NovelBookChooseManager.getInstance().getBookId());
    }

    /******************初始化数据*****************/
    private void initList(){
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(false);
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            if (!NetworkUtil.isConnected(getActivity())){
                binding.refreshLayout.finishRefresh(false);
                ToastUtil.showToast(getActivity(),"请链接网络后下拉刷新重试~");
                return;
            }

            presenter.loadNetChapterData(NovelBookChooseManager.getInstance().getBookType(),String.valueOf(NovelBookChooseManager.getInstance().getBookLevel()), NovelBookChooseManager.getInstance().getBookId());
        });

        listAdapter = new NovelListAdapter(getActivity(),new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(listAdapter);
        listAdapter.setListener(chapterInfoBean -> {
            StudyActivity.start(getActivity(), NovelBookChooseManager.getInstance().getBookType(), NovelBookChooseManager.getInstance().getBookId(),chapterInfoBean.getVoaId());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        //关闭广告
        AdTemplateShowManager.getInstance().stopTemplateAd(adTemplateKey);

        presenter.detachView();
    }

    /***************回调数据*************/
    @Override
    public void showData(List<BookChapterBean> list) {
        if (list==null){
            binding.refreshLayout.finishRefresh(false);
            ToastUtil.showToast(getActivity(),"查询该书本的章节内容失败，请重试～");
        }else {
            binding.refreshLayout.finishRefresh(true);
            listAdapter.refreshData(list);

            if (list.size()>0){
                binding.recyclerView.smoothScrollToPosition(0);

                //刷新广告
                refreshTemplateAd();
            }else {
                ToastUtil.showToast(getActivity(),"本书籍暂无章节数据");
            }
        }
    }

    @Override
    public void loadNetData() {
        binding.refreshLayout.autoRefresh();
    }

    //数据刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.novel)){
            //课程界面
            presenter.loadLocalChapterData(NovelBookChooseManager.getInstance().getBookType(),String.valueOf(NovelBookChooseManager.getInstance().getBookLevel()), NovelBookChooseManager.getInstance().getBookId());
        }
    }

    //账号登录刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event){
        //刷新模版广告
        refreshTemplateAd();
    }

    //账号登出刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginOutEvent event){
        //刷新模版广告
        refreshTemplateAd();
    }

    /*****************************设置新的信息流广告************************/
    //当前信息流广告的key
    private String adTemplateKey = NovelListFragment.class.getName();
    //模版广告数据
    private AdTemplateViewBean templateViewBean = null;
    //显示广告
    private void showTemplateAd() {
        if (templateViewBean == null) {
            templateViewBean = new AdTemplateViewBean(R.layout.item_ad_mix, R.id.template_container, R.id.ad_whole_body, R.id.native_main_image, R.id.native_title, binding.recyclerView, listAdapter, new OnAdTemplateShowListener() {
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
