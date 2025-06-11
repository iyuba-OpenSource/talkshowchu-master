package com.iyuba.talkshow.lil.help_fix.ui.collect.chapter.novel;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.talkshow.databinding.FragmentListRefreshBinding;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterCollectEntity;
import com.iyuba.talkshow.lil.help_fix.ui.study.StudyActivity;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingFragment;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 小说-章节收藏界面
 * @date: 2023/7/17 16:43
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NovelChapterCollectFragment extends BaseViewBindingFragment<FragmentListRefreshBinding> implements NovelChapterCollectView{

    private NovelChapterCollectAdapter collectAdapter;
    private NovelChapterCollectPresenter presenter;

    public static NovelChapterCollectFragment getInstance(){
        NovelChapterCollectFragment fragment = new NovelChapterCollectFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        presenter = new NovelChapterCollectPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        binding.refreshLayout.autoRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        presenter.detachView();
    }

    /************************初始化************************/
    private void initList(){
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(false);
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData();
            }
        });

        collectAdapter = new NovelChapterCollectAdapter(getActivity(),new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(collectAdapter);
        collectAdapter.setSingleClickListener(new OnSimpleClickListener<ChapterCollectEntity>() {
            @Override
            public void onClick(ChapterCollectEntity entity) {
                StudyActivity.start(getActivity(),entity.types,entity.bookId,entity.voaId);
            }
        });
        collectAdapter.setLongClickListener(new OnSimpleClickListener<ChapterCollectEntity>() {
            @Override
            public void onClick(ChapterCollectEntity entity) {
                showDeleteDialog(entity);
            }
        });
    }


    /*************************刷新数据********************/
    private void refreshData(){
        String[] collectTypes = new String[]{TypeLibrary.BookType.bookworm,TypeLibrary.BookType.newCamstory,TypeLibrary.BookType.newCamstoryColor};
        List<ChapterCollectEntity> collectList = CommonDataManager.getChapterCollectMultiData(collectTypes,String.valueOf(UserInfoManager.getInstance().getUserId()));
        if (collectList!=null&&collectList.size()>0){
            collectAdapter.refreshData(collectList);
            binding.refreshLayout.finishRefresh();
        }else {
            collectAdapter.refreshData(new ArrayList<>());
            binding.refreshLayout.finishRefresh();
            ToastUtil.showToast(getActivity(),"暂无文章收藏数据");
        }
    }

    /*************************回调数据*****************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.novel_lesson_collect)){
            binding.refreshLayout.autoRefresh();
        }
    }

    @Override
    public void showCollectResult(boolean isSuccess) {
        if (isSuccess){
            ToastUtil.showToast(getActivity(),"取消收藏成功~");
            binding.refreshLayout.autoRefresh();
        }else {
            ToastUtil.showToast(getActivity(),"取消收藏失败，请重试～");
        }
    }

    /***************************辅助功能*******************/
    //显示删除弹窗
    private void showDeleteDialog(ChapterCollectEntity entity){
        new AlertDialog.Builder(getActivity())
                .setMessage("取消收藏")
                .setMessage("是否取消收藏 "+entity.title+" 这篇文章?")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.collectAdapter(entity.types, entity.voaId, entity.userId, false);
                    }
                }).setNegativeButton("取消",null)
                .show();
    }
}
