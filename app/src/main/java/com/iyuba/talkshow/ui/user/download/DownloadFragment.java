package com.iyuba.talkshow.ui.user.download;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Download;
import com.iyuba.talkshow.databinding.FragmentDownloadBinding;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.detail.DetailActivity;
import com.iyuba.talkshow.ui.user.me.dubbing.*;

import java.util.List;

import javax.inject.Inject;


/**
 * DownloadFragment
 *
 * @author wayne
 * @date 2018/2/6
 */
public class DownloadFragment extends BaseFragment implements Editable, DownloadMvpView {
    @Inject
    DownloadAdapter mAdapter;
    @Inject
    DownloadPresenter mPresenter;

    FragmentDownloadBinding binding ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDownloadBinding.inflate(getLayoutInflater() , container,false);

        fragmentComponent().inject(this);
        mPresenter.attachView(this);
        mPresenter.setContext(mContext);
        binding.emptyView.emptyText.setText("还没有下载的文章");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.downloadRecyclerView.setAdapter(mAdapter);
        mAdapter.setListener(new OnDownloadClickListener() {
            @Override
            public void onItemClick(Download download) {
                startActivity(DetailActivity.buildIntent(mContext, download.getVoa(), false));
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.downloadRecyclerView.setLayoutManager(layoutManager);

        mPresenter.getDownload();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.setMode(Mode.SHOW);
        mPresenter.detachView();
    }

    @Override
    public int getMode() {
        return mAdapter.getMode();
    }

    @Override
    public void setMode(int mode) {
        mAdapter.setMode(mode);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void deleteCollection() {
        List<String> selectedIds = mAdapter.getSelection();
        if (selectedIds.size() > 0) {
            showLoadingLayout();
            mPresenter.deleteDownload(selectedIds);
        } else {
            showToast(R.string.select_nothing);
        }
    }

//    @OnClick(R.id.select_all)
//    public void onSelectAll() {
//        mAdapter.addAllSelection();
//    }

    @Override
    public int getDataSize() {
        return mAdapter.getItemCount();
    }

    @Override
    public void addAllSelection() {
        mAdapter.addAllSelection();
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
        Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContextt() {
        return mContext;
    }
}
