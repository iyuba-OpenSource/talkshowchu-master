package com.iyuba.talkshow.ui.user.me.dubbing.released;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Ranking;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.FragmentReleasedBinding;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.detail.ranking.watch.WatchDubbingActivity;
import com.iyuba.talkshow.ui.user.me.dubbing.Editable;
import com.iyuba.talkshow.ui.user.me.dubbing.Mode;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

public class ReleasedFragment extends BaseFragment implements ReleasedMvpView, Editable {
    private static final String TAG = ReleasedFragment.class.getSimpleName();

    @Inject
    ReleasedAdapter mAdapter;
    @Inject
    ReleasedPresenter mPresenter;

    FragmentReleasedBinding binding ;
    Integer uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
        mPresenter.attachView(this);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding  = FragmentReleasedBinding.inflate(getLayoutInflater(), container, false);
        mPresenter.attachView(this);
        if (getArguments() != null) {
            String id = getArguments().getString("uid", "");
            if (TextUtils.isEmpty(id)) {
                uid = UserInfoManager.getInstance().getUserId();
            } else {
                uid = Integer.parseInt(id);
            }
        } else {
            uid = UserInfoManager.getInstance().getUserId();
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //针对点赞刷新
        EventBus.getDefault().register(this);

        binding.emptyView.emptyText.setText(getString(R.string.has_no_dubbing1));
        mAdapter.setOnReleasedClickListener(mListener);
        binding.releasedRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.releasedRecyclerView.setLayoutManager(layoutManager);
        binding.releasedRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        binding.releasedRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mPresenter.getReleasedData(uid);
    }

    OnReleasedClickListener mListener = new OnReleasedClickListener() {
        @Override
        public void onReleasedClick(Ranking ranking) {
            mPresenter.getVoa(ranking);
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
        //针对点赞刷新
        EventBus.getDefault().unregister(this);

        mAdapter.setMode(Mode.SHOW);
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
        binding.releasedRecyclerView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
    }

    @Override
    public void setReleasedData(List<Ranking> mData) {
        mAdapter.setData(mData);
        mAdapter.notifyDataSetChanged();
        binding.releasedRecyclerView.setVisibility(View.VISIBLE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
    }

    @Override
    public void startLoginActivity() {
        NewLoginUtil.startToLogin(getActivity());
    }

    @Override
    public void startWatchDubbingActivity(Voa voa, Ranking ranking) {
        Intent intent = WatchDubbingActivity.buildIntent(getContext(), ranking, voa, uid);
        startActivity(intent);
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
        final List<Integer> selection = mAdapter.getSelectedData();
        if (selection.size() <= 0) {
            showToast(R.string.select_nothing);
            return;
        }
        new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage("确定要删除选中的配音吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.deleteReleasedData(selection, uid);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();

    }

    @Override
    public int getDataSize() {
        return mAdapter.getItemCount();
    }

    @Override
    public void addAllSelection() {
        mAdapter.addAll();
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    //用于刷新点赞数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPage(ReleasedBean bean){
        if (bean.isRefresh()){
            mPresenter.getReleasedData(uid);
        }
    }
}
