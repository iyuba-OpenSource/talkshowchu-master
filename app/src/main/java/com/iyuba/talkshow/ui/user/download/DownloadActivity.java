package com.iyuba.talkshow.ui.user.download;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Download;
import com.iyuba.talkshow.databinding.ActivityDownloadBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.detail.DetailActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import javax.inject.Inject;


public class DownloadActivity extends BaseActivity implements DownloadMvpView {

    @Inject
    DownloadAdapter mAdapter;
    @Inject
    DownloadPresenter mPresenter;

    //布局样式
    private ActivityDownloadBinding binding;

    OnDownloadClickListener mListener = download -> {
        Intent intent = DetailActivity.buildIntent(DownloadActivity.this, download.getVoa(), false);
        startActivity(intent);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDownloadBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        activityComponent().inject(this);
        setSupportActionBar(binding.downloadToolbar);
        mPresenter.attachView(this);
        mPresenter.setContext(mContext);
        binding.selectAll.setVisibility(View.GONE);

        binding.downloadRecyclerView.setAdapter(mAdapter);
        mAdapter.setListener(mListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.downloadRecyclerView.setLayoutManager(layoutManager);

        showLoadingLayout();
        mPresenter.getDownload();
        binding.emptyView.emptyText.setText("还没有下载的文章");
        setClick();
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

    public void setClick(){
        binding.editTv.setOnClickListener(v -> onEditClick());
        binding.selectAll.setOnClickListener(v -> onSelectAll());
        binding.deleteBtn.setOnClickListener(v -> onDeleteClick());
    }

    public void onEditClick() {
        if (mAdapter.getMode() == Mode.SHOW) {
            binding.editTv.setText(getString(R.string.cancel));
            mAdapter.setMode(Mode.EDIT);
            binding.selectAll.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
            binding.deleteBtn.setVisibility(View.VISIBLE);
        } else {
            binding.editTv.setText(getString(R.string.edit));
            mAdapter.setMode(Mode.SHOW);
            binding.selectAll.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
            binding.deleteBtn.setVisibility(View.GONE);
        }
    }

    public void onSelectAll() {
        mAdapter.addAllSelection();
    }

    public void onDeleteClick() {
        List<String> selectedIds = mAdapter.getSelection();
        if (selectedIds.size() > 0) {
            showLoadingLayout();
            mPresenter.deleteDownload(selectedIds);
        } else {
            showToast(R.string.select_nothing);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    public void setAdapterEmpty() {
        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
        binding.downloadRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void setAdapterData(List<Download> data) {
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.downloadRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContextt() {
        return mContext;
    }
}
