package com.iyuba.talkshow.ui.user.me.dubbing.draft;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.FragmentDraftBinding;
import com.iyuba.talkshow.lil.help_fix.ui.dubbing.DubbingNewActivity;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.user.me.dubbing.Editable;
import com.iyuba.talkshow.ui.user.me.dubbing.Mode;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import javax.inject.Inject;


public class DraftFragment extends BaseFragment implements DraftMvpView, Editable{
    private static final String TAG = DraftFragment.class.getSimpleName();

    //布局样式
    private FragmentDraftBinding binding;

    @Inject
    DraftPresenter mPresenter;
    @Inject
    DraftAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
        mPresenter.attachView(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDraftBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.attachView(this);
        binding.emptyView.emptyText.setText(getString(R.string.has_no_dubbing1));
        binding.draftRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnDraftClickListener(mListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.draftRecyclerView.setLayoutManager(layoutManager);
        binding.draftRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        binding.draftRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        showLoadingLayout();
        mPresenter.getDraftData();
    }

    OnDraftClickListener mListener = new OnDraftClickListener() {
        @Override
        public void onDraftClick(Record record) {
            mPresenter.getVoa(record.voaId(), record.timestamp());
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
        mAdapter.setMode(Mode.SHOW);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();

    }

    @Override
    public void setDraftData(List<Record> mData) {
        mAdapter.setData(mData);
        mAdapter.notifyDataSetChanged();
        binding.draftRecyclerView.setVisibility(View.VISIBLE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
    }

    @Override
    public void setEmptyData() {
        binding.draftRecyclerView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
    }

    @Override
    public void startDubbingActivity(Voa voa, long timestamp) {
//        Intent intent = DubbingActivity.buildIntent(getContext(), voa, timestamp);
//        startActivity(intent);

        //换个操作
        DubbingNewActivity.start(getActivity(),voa);
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
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
        List<String> selection = mAdapter.getSelectedData();
        if(selection.size() > 0) {
            mPresenter.deleteDraftData(selection);
        } else {
            showToast(R.string.select_nothing);
        }
    }

    @Override
    public int getDataSize() {
        return mAdapter.getItemCount();
    }

    @Override
    public void addAllSelection() {
        mAdapter.addAll();
    }
}
