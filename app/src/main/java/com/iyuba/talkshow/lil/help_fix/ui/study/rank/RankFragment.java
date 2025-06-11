package com.iyuba.talkshow.lil.help_fix.ui.study.rank;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.FragmentFixRankBinding;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Eval_rank;
import com.iyuba.talkshow.lil.help_fix.ui.study.rank.rank_detail.RankDetailActivity;
import com.iyuba.talkshow.lil.help_fix.util.FixUtil;
import com.iyuba.talkshow.lil.help_fix.util.ImageUtil;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingFragment;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.help_mvp.view.NoScrollLinearLayoutManager;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 排行界面
 * @date: 2023/5/25 10:09
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RankFragment extends BaseViewBindingFragment<FragmentFixRankBinding> implements RankView {

    //类型
    private String types;
    //voaId
    private String voaId;
    //开始页码-不是index，是count
    private int startIndex = 0;
    //每页数量
    private int pageCount = 20;
    //类型
    private String rankType = "D";

    private RankPresenter presenter;
    private RankAdapter rankAdapter;

    //是否加载完成
    private boolean isLoadFinish = false;

    public static RankFragment getInstance(String types, String voaId){
        RankFragment fragment = new RankFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types, types);
        bundle.putString(StrLibrary.voaid, voaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        types = getArguments().getString(StrLibrary.types);
        voaId = getArguments().getString(StrLibrary.voaid);

        presenter = new RankPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isLoadFinish){
            isLoadFinish = true;
            binding.swipeRefresh.autoRefresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

        presenter.detachView();
    }

    private void initList(){
        binding.swipeRefresh.setEnableRefresh(true);
        binding.swipeRefresh.setEnableLoadMore(true);
        binding.swipeRefresh.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.swipeRefresh.setRefreshFooter(new ClassicsFooter(getActivity()));
        binding.swipeRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    binding.swipeRefresh.finishLoadMore(false);
                    refreshFailView("请链接网络后重试~");
                    return;
                }

                presenter.getPublishRankData(types,voaId,startIndex,pageCount,rankType,false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    binding.swipeRefresh.finishRefresh(false);
                    ToastUtil.showToast(getActivity(),"请链接网络后重试~");
                    return;
                }

                startIndex = 0;
                presenter.getPublishRankData(types,voaId,startIndex,pageCount,rankType,true);
            }
        });

        rankAdapter = new RankAdapter(getActivity(),new ArrayList<>());
        NoScrollLinearLayoutManager manager = new NoScrollLinearLayoutManager(getActivity(),false);
        binding.rankRecyclerView.setLayoutManager(manager);
        binding.rankRecyclerView.setAdapter(rankAdapter);
        binding.rankRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        rankAdapter.setListener(new OnSimpleClickListener<Eval_rank.DataBean>() {
            @Override
            public void onClick(Eval_rank.DataBean dataBean) {
                RankDetailActivity.start(getActivity(),types,voaId,String.valueOf(dataBean.getUid()),dataBean.getName(),dataBean.getImgSrc());
            }
        });

        binding.topRank.setOnClickListener(v->{
            //获取第一个数据进行展示
            Eval_rank.DataBean dataBean = rankAdapter.getFirstData();
            if (dataBean!=null){
                RankDetailActivity.start(getActivity(),types,voaId,String.valueOf(dataBean.getUid()),dataBean.getName(),dataBean.getImgSrc());
            }else {
                if (!UserInfoManager.getInstance().isLogin()){
                    NewLoginUtil.startToLogin(getActivity());
                    return;
                }

                RankDetailActivity.start(getActivity(),types,voaId,String.valueOf(UserInfoManager.getInstance().getUserId()), UserInfoManager.getInstance().getUserName(), FixUtil.getUserHeadPic(String.valueOf(UserInfoManager.getInstance().getUserId())));
            }
        });

        binding.myRank.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                NewLoginUtil.startToLogin(getActivity());
                return;
            }

            RankDetailActivity.start(getActivity(),types,voaId,String.valueOf(UserInfoManager.getInstance().getUserId()), UserInfoManager.getInstance().getUserName(), FixUtil.getUserHeadPic(String.valueOf(UserInfoManager.getInstance().getUserId())));
        });
    }

    /*******************刷新数据***************/
    private void refreshUser(boolean isRefresh,Eval_rank bean){
        //刷新前边的数据
        if (bean==null||bean.getMyid()==0){
            binding.tvUsername.setText("未登录");
            binding.count.setText("未登录");
            binding.score.setVisibility(View.INVISIBLE);
        }else {
            ImageUtil.loadCircleImg(bean.getMyimgSrc(),0,binding.ivAvatar);
            binding.tvUsername.setText(bean.getMyname());
            binding.tvRank.setText(String.valueOf(bean.getMyranking()));
            binding.count.setText("句子数："+bean.getMycount());
            binding.score.setText(bean.getMyscores()+"分");
            binding.score.setVisibility(View.VISIBLE);
        }

        //刷新冠军数据
        if (bean!=null&&bean.getData()!=null&&bean.getData().size()>0){
            Eval_rank.DataBean dataBean = bean.getData().get(0);
            binding.ivChampion.setVisibility(View.VISIBLE);
            ImageUtil.loadCircleImg(dataBean.getImgSrc(), R.drawable.default_avatar,binding.ivChampion);
            binding.tvChampionName.setText(dataBean.getName());
            binding.tvChampioscore.setText(dataBean.getScores()+"分");
            binding.tvChampionCount.setText(dataBean.getCount()+"句");

            //刷新列表数据
            refreshData(isRefresh,bean.getData());
        }else {
            ImageUtil.loadCircleImg(bean.getMyimgSrc(),0,binding.ivChampion);
            binding.tvChampionName.setText(bean.getMyname());
            binding.tvChampioscore.setText(bean.getMyscores()+"分");
            binding.tvChampionCount.setText(bean.getMycount()+"句");
        }
    }

    private void refreshData(boolean isRefresh,List<Eval_rank.DataBean> list){
        if (list!=null&&list.size()>0){
            //设置数量
            startIndex+=list.size();

            if (isRefresh){
                rankAdapter.refreshData(list);
            }else {
                rankAdapter.addData(list);
            }
        }else {
            rankAdapter.refreshData(new ArrayList<>());
        }
    }

    //回调
    @Override
    public void showData(boolean isRefresh, Eval_rank bean) {
        binding.swipeRefresh.finishRefresh(true);
        binding.swipeRefresh.finishLoadMore(true);

        if (bean==null){
            if (isRefresh){
                refreshFailView("加载数据错误，请重试～");
            }else {
                ToastUtil.showToast(getActivity(),"加载数据错误，请重试～");
            }
            return;
        }

        refreshUser(isRefresh,bean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.eval_rank)){
            binding.swipeRefresh.autoRefresh();
        }
    }

    /****************辅助功能*****************/
    //显示错误数据
    private void refreshFailView(String msg){
        binding.failLayout.setVisibility(View.VISIBLE);
        binding.failText.setText(msg);
        binding.failBtn.setOnClickListener(v->{
            binding.failLayout.setVisibility(View.GONE);
            binding.swipeRefresh.autoRefresh();
        });
    }
}
