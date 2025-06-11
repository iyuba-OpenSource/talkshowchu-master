package com.iyuba.talkshow.ui.user.me;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.result.ShareInfoRecord;
import com.iyuba.talkshow.databinding.ActivityClockInfoBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.widget.divider.LinearItemDivider;
import com.iyuba.talkshow.util.NetStateUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by carl shen on 2021/4/12
 * New Primary English, new study experience.
 */
public class ClockInfoActivity extends BaseActivity implements ClockInfoMvpView {
    private static final String TAG = ClockInfoActivity.class.getSimpleName();
    private static int PAGE_NUM = 1;
    private static final int PAGE_SIZE = 20;
    @Inject
    ClockInfoAdapter mAdapter;
    @Inject
    ClockInfoPresenter mPresenter;
    ActivityClockInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClockInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activityComponent().inject(this);
        setSupportActionBar(binding.clockToolbar);
        mPresenter.attachView(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        binding.clockRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearItemDivider divider = new LinearItemDivider(mContext, LinearItemDivider.VERTICAL_LIST);
        divider.setDivider(getResources().getDrawable(R.drawable.voa_ranking_divider));
        binding.clockRecyclerView.addItemDecoration(divider);
        binding.clockRecyclerView.setLayoutManager(layoutManager);
        binding.clockRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        binding.clockRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> refreshData());
        binding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetStateUtil.isConnected(mContext)) {
                    refreshLayout.finishRefresh();
                    return;
                }
                PAGE_NUM++;
                mPresenter.getMoreRanking(PAGE_NUM, PAGE_SIZE);
                refreshLayout.finishLoadMore(2000);
            }
        });
        showLoadingLayout();
        mPresenter.getRanking(PAGE_NUM, PAGE_SIZE);
    }

    private void refreshData() {
        if (!NetStateUtil.isConnected(mContext)) {
            binding.refreshLayout.finishRefresh();
            return;
        }
        PAGE_NUM = 1;
        mPresenter.getRanking(PAGE_NUM, PAGE_SIZE);
        binding.refreshLayout.finishRefresh(2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void showRankings(List<ShareInfoRecord> rankingList) {
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.refreshLayout.setVisibility(View.VISIBLE);
        mAdapter.setRankingList(rankingList);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void showMoreRankings(List<ShareInfoRecord> rankingList) {
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
    public void showLoadingLayout() {
        binding.loadingLayout.getRoot().setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoadingLayout() {
        binding.loadingLayout.getRoot().setVisibility(View.GONE);
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismissRefreshingView() {
//        binding.refreshLayout.setRefreshing(false);
        binding.refreshLayout.finishRefresh();
    }
}
