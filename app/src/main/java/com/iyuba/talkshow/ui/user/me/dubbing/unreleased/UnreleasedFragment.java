package com.iyuba.talkshow.ui.user.me.dubbing.unreleased;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.FragmentUnreleasedBinding;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.user.me.dubbing.Editable;
import com.iyuba.talkshow.ui.user.me.dubbing.draft.DraftFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import javax.inject.Inject;


public class UnreleasedFragment extends BaseFragment implements UnreleasedMvpView, Editable {
    private static final String TAG = DraftFragment.class.getSimpleName();

    @Inject
    UnreleasedPresenter mPresenter;
    @Inject
    UnreleasedAdapter mAdapter;


    FragmentUnreleasedBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUnreleasedBinding.inflate(getLayoutInflater(), container, false);
        mPresenter.attachView(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.emptyView.emptyText.setText(getString(R.string.has_no_dubbing1));

        mAdapter.setOnUnreleasedClickListener(onUnreleasedClickListener);
        binding.unreleasedRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.unreleasedRecyclerView.setLayoutManager(layoutManager);
        binding.unreleasedRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        binding.unreleasedRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。

        showLoadingLayout();
        mPresenter.getUnreleasedData();
    }

    OnUnreleasedClickListener onUnreleasedClickListener = new OnUnreleasedClickListener() {
        @Override
        public void onUnreleasedClick(Record record) {
            mPresenter.getVoa(record);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
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
        mPresenter.detachView();
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
    public void setEmptyData() {
        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
        binding.unreleasedRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void setUnreleasedData(List<Record> mData) {
        mAdapter.setData(mData);
        mAdapter.notifyDataSetChanged();
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.unreleasedRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void startPreviewActivity(Voa voa, long timestamp) {
//        Intent intent = PreviewActivity.buildIntent(getContext(), voa, previewInfoBean, timestamp, true);
//        startActivity(intent);
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
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
        List<Record> selection = mAdapter.getSelectedData();
        if(selection.size() > 0) {
            showLoadingLayout();
            mPresenter.deleteUnreleasedData(selection);
        } else {
            Toast.makeText(getContext(), R.string.select_nothing, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getDataSize() {
        return mAdapter.getItemCount();
    }

    @Override
    public void addAllSelection() {

    }
}
