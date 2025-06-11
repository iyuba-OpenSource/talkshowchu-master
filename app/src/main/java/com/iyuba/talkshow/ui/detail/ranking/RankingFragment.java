package com.iyuba.talkshow.ui.detail.ranking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Ranking;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.FragmentRankingBinding;
import com.iyuba.talkshow.injection.PerFragment;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.detail.DetailActivity;
import com.iyuba.talkshow.ui.detail.ranking.watch.WatchDubbingActivity;
import com.iyuba.talkshow.ui.user.me.dubbing.released.ReleasedBean;
import com.iyuba.talkshow.ui.widget.divider.LinearItemDivider;
import com.iyuba.talkshow.util.NetStateUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;


@PerFragment
public class RankingFragment extends BaseFragment implements RankingMvpView {
    private static final String TAG = RankingFragment.class.getSimpleName();

    private static final String VOA = "voa";
    private static int PAGE_NUM = 1;
    private static final int PAGE_SIZE = 20;

    private Voa mVoa;


    @Inject
    RankingPresenter mPresenter;
    @Inject
    RankingAdapter mAdapter;

    FragmentRankingBinding binding ;

    RankingAdapter.RankingCallback callback = new RankingAdapter.RankingCallback() {
        @Override
        public void onClickThumbs(int id) {
            mPresenter.doAgree(id);
        }

        @Override
        public void onClickLayout(Ranking ranking, int pos) {
            if (getActivity() instanceof DetailActivity){
                ((DetailActivity) getActivity()).stopPlaying();
            }
            Intent intent = WatchDubbingActivity.buildIntent(mContext, ranking, mVoa, ranking.userId());
            startActivity(intent);
        }
    };

    public RankingFragment() {
    }

    public static RankingFragment newInstance(Voa voa) {
        RankingFragment fragment = new RankingFragment();
        Bundle args = new Bundle();
        args.putParcelable(VOA, voa);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding  = FragmentRankingBinding.inflate(getLayoutInflater(), container, false);
        fragmentComponent().inject(this);
        mPresenter.attachView(this);

        if (getArguments() != null) {
            mVoa = getArguments().getParcelable(VOA);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter.setRankingCallback(callback);
        binding.recyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearItemDivider divider = new LinearItemDivider(getActivity(), LinearItemDivider.VERTICAL_LIST);
        divider.setDivider(getResources().getDrawable(R.drawable.voa_ranking_divider));
        binding.recyclerView.addItemDecoration(divider);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> refreshData());
        binding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetStateUtil.isConnected(mContext)) {
                    refreshLayout.finishRefresh();
                    return;
                }
                PAGE_NUM++;
                mPresenter.getMoreRanking(mVoa.voaId(), PAGE_NUM, PAGE_SIZE);
                refreshLayout.finishLoadMore(1000);
            }
        });
        if (mVoa != null) {
            refreshData();
        }
    }

    private void refreshData() {
        if (!NetStateUtil.isConnected(mContext)) {
            binding.refreshLayout.finishRefresh();
            return;
        }
        PAGE_NUM = 1;
        mPresenter.getRanking(mVoa.voaId(), PAGE_NUM, PAGE_SIZE);
        binding.refreshLayout.finishRefresh(2000);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (mVoa != null) {
//            mPresenter.getRanking(mVoa.voaId(), PAGE_NUM, PAGE_SIZE);
//            binding.refreshLayout.setOnRefreshListener(mOnRefreshListener);
//        }
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        mPresenter.detachView();
    }

    /**
     * 刷新监听
     */
    private final SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            binding.recyclerView.post(() -> mPresenter.getRanking(mVoa.voaId(), PAGE_NUM, PAGE_SIZE));
        }
    };

    @Override
    public void showRankings(List<Ranking> rankingList) {
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.refreshLayout.setVisibility(View.VISIBLE);
        mAdapter.setRankingList(rankingList);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void showMoreRankings(List<Ranking> rankingList) {
        if (rankingList == null) {
            Log.e("RankingFragment", "showMoreRankings list is null.");
            return;
        }
        mAdapter.setMoreRankingList(rankingList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyRankings() {
        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
        binding.refreshLayout.setVisibility(View.GONE);
    }

    @Override
    public void showToast(int id) {
        Toast.makeText(mContext, id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingLayout() {
        binding.emptyView.emptyImage.setImageResource(R.drawable.empty_comment_data);
        binding.emptyView.emptyText.setText(getString(R.string.has_no_dubbing));
        binding.loadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoadingLayout() {
        binding.loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void dismissRefreshingView() {
//        binding.refreshLayout.setRefreshing(false);
        binding.refreshLayout.finishRefresh();
    }

    //用于刷新点赞数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPage(ReleasedBean bean){
        if (bean.isRefresh()){
            refreshData();
        }
    }
}
