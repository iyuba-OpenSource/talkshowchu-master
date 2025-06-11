package com.iyuba.talkshow.ui.detail.recommend;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.FragmentRecommendBinding;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.detail.DetailActivity;
import com.iyuba.talkshow.ui.widget.divider.NormalGridItemDivider;
import com.iyuba.talkshow.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class RecommendFragment extends BaseFragment implements RecommendMvpView {
    private static final String TAG = RecommendFragment.class.getSimpleName();

    private static final String VOA_ID = "voaId";
    private static final String CATEGORY = "category";
    private static final String SERIES = "series";


    private static final int PAGE_NUM = 1;
    private static final int PAGE_SIZE = 20;
    private static final int SPAN_COUNT = 2;

    @Inject
    DataManager dataManager;
    @Inject
    RecommendPresenter mPresenter;
    @Inject
    RecommendAdapter mAdapter;

    RecommendAdapter.VoaCallback callback = voa -> {
        if (!UserInfoManager.getInstance().isVip() &&voa.series()>204&& !dataManager.isTrial(voa)){
            ToastUtil.showToast(mContext, "非VIP会员无权限查看，请开通VIP");
        }else {
            Timber.e("VoaCallback---------");
            if (getActivity() instanceof DetailActivity){
                ((DetailActivity) getActivity()).stopPlaying();
            }
            getActivity().finish();
            Intent intent = DetailActivity.buildIntent(getContext(), voa, false);
            startActivity(intent);
        }
    };
    private FragmentRecommendBinding binding;

    public static RecommendFragment newInstance(int voaId, int category) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        args.putInt(VOA_ID, voaId);
        args.putInt(CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    public static RecommendFragment newSeriesInstance(int voaId, int series) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        args.putInt(VOA_ID, voaId);
        args.putInt(SERIES, series);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentComponent().inject(this);
        mPresenter.attachView(this);
        binding  = FragmentRecommendBinding.inflate(inflater,container,false);

        if (getArguments() != null) {
            int voaId = getArguments().getInt(VOA_ID);
            int category = getArguments().getInt(CATEGORY);
            int series = getArguments().getInt(SERIES);
            if (series != 0){
                mPresenter.getSeriesList(voaId, series, PAGE_NUM, PAGE_SIZE);

            }else if (category!= 0){
                mPresenter.getRecommendList(voaId, category, PAGE_NUM, PAGE_SIZE);

            }
            mAdapter.setmVoaCallback(callback);
            binding.recyclerView.setAdapter(mAdapter);
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
            NormalGridItemDivider divider = new NormalGridItemDivider(getActivity());
            divider.setDivider(getResources().getDrawable(R.drawable.voa_activity_divider));
            binding.recyclerView.addItemDecoration(divider);
            binding.recyclerView.setLayoutManager(layoutManager);
            binding.recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
            binding.recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        }
        return binding.getRoot();
    }

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
    public void showRecommend(List<Voa> voaList) {
        mAdapter.setmVoaList(voaList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyRecommend() {
        mAdapter.setmVoaList(Collections.emptyList());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }

}
