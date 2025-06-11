package com.iyuba.talkshow.lil.help_fix.ui.collect.chapter.junior;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.talkshow.data.model.Collect;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.FragmentListRefreshBinding;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;
import com.iyuba.talkshow.lil.help_fix.ui.main.ui.BaseVBFragment;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.newce.study.StudyActivity;
import com.iyuba.talkshow.ui.user.collect.CollectionMvpView;
import com.iyuba.talkshow.ui.user.collect.CollectionPresenter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @title: 中小学-章节收藏界面
 * @date: 2023/7/17 16:43
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class JuniorChapterCollectFragment extends BaseVBFragment<FragmentListRefreshBinding> implements CollectionMvpView {

    @Inject
    CollectionPresenter presenter;
    @Inject
    JuniorChapterCollectAdapter collectAdapter;

    public static JuniorChapterCollectFragment getInstance(){
        JuniorChapterCollectFragment fragment = new JuniorChapterCollectFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        fragmentComponent().inject(this);
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        binding.refreshLayout.autoRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    /*******************************初始化***********************/
    private void initList(){
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(false);
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                presenter.getCollection();
            }
        });

        collectAdapter.setSingleClickListener(new OnSimpleClickListener<Collect>() {
            @Override
            public void onClick(Collect collect) {
                if ((collect == null) || (collect.getVoa() == null)) {
                    showToastShort("请先同步课程原文，才能跳转到相应的页面。");
                    return;
                }
                Voa voa = collect.getVoa();
                int unitId = presenter.getUnitId(voa);
                if (unitId > 0) {
                    voa.UnitId = unitId;
                }
                startActivity(StudyActivity.buildIntent(getActivity(), voa, StudyActivity.title_default, voa.UnitId, false,-1));
            }
        });
        collectAdapter.setLongClickListener(new OnSimpleClickListener<Collect>() {
            @Override
            public void onClick(Collect collect) {
                showDeleteDialog(collect.getVoa());
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(collectAdapter);
    }

    /**********************回调**************************/
    @Override
    public void showLoadingLayout() {

    }

    @Override
    public void dismissLoadingLayout() {

    }

    @Override
    public void setAdapterEmpty() {
        collectAdapter.refreshData(new ArrayList<>());
        binding.refreshLayout.finishRefresh(false);
        ToastUtil.showToast(getActivity(),"暂无收藏数据");
    }

    @Override
    public void setAdapterData(String msg,List<Collect> data) {
        if (TextUtils.isEmpty(msg)){
            //存在数据
            binding.refreshLayout.finishRefresh();
            collectAdapter.refreshData(data);
            return;
        }

        if (data==null){
            //异常信息
            binding.refreshLayout.finishRefresh();
        }else {
            //没有数据
            binding.refreshLayout.finishRefresh();
            collectAdapter.refreshData(new ArrayList<>());
        }
        ToastUtil.showToast(getActivity(),msg);
    }

    @Override
    public void showToast(int resId) {
        ToastUtil.showToast(getActivity(),resId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.junior_lesson_collect)){
            binding.refreshLayout.autoRefresh();
        }
    }

    /***************************辅助功能*******************/
    //显示删除弹窗
    private void showDeleteDialog(Voa voa){
        new AlertDialog.Builder(getActivity())
                .setMessage("取消收藏")
                .setMessage("是否取消收藏 "+voa.titleCn()+" 这篇文章?")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String> deleteList = new ArrayList<>();
                        deleteList.add(String.valueOf(voa.voaId()));
                        presenter.deleteCollection(deleteList);
                    }
                }).setNegativeButton("取消",null)
                .show();
    }
}
