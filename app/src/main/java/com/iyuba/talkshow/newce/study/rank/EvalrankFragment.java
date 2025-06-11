package com.iyuba.talkshow.newce.study.rank;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.model.RankOralBean;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.FragmentEvalrankBinding;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.lil.help_mvp.view.NoScrollLinearLayoutManager;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newce.comment.CommentActivity;
import com.iyuba.talkshow.newdata.Config;
import com.iyuba.talkshow.newdata.Playmanager;
import com.iyuba.talkshow.newdata.RefreshRankEvent;
import com.iyuba.talkshow.newdata.SPconfig;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.ui.widget.divider.LinearItemDivider;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class EvalrankFragment extends BaseFragment implements EvalrankMvpView {
    private static final String VOA = "voa";
    @Inject
    EvalrankPresenter mPresenter;
    LoadingDialog mLoadingDialog;
    @Inject
    EvalrankAdapter mRankingAdapter;
    FragmentEvalrankBinding binding ;
    int uid;
    private Voa mVoa;
    private RankOralBean rankOralBean;
    private List<RankOralBean.DataBean> rankUserList = new ArrayList<>();


    //是否当前界面
    private boolean isCurPage = false;

    public static EvalrankFragment newInstance(Voa voa) {
        EvalrankFragment fragment = new EvalrankFragment();
        Bundle args = new Bundle();
        args.putParcelable(VOA, voa);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
        mPresenter.attachView(this);
        mVoa = getArguments().getParcelable(VOA);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        this.isCurPage = isVisibleToUser;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEvalrankBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //增加eventbus
        EventBus.getDefault().register(this);

        binding.rankRecyclerView.setLayoutManager(new NoScrollLinearLayoutManager(mContext,false));
        LinearItemDivider divider = new LinearItemDivider(mContext, LinearItemDivider.VERTICAL_LIST);
        divider.setDivider(ResourcesCompat.getDrawable(getResources(), R.drawable.voa_ranking_divider, getActivity().getTheme()));
        binding.rankRecyclerView.addItemDecoration(divider);
        // 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        binding.rankRecyclerView.setHasFixedSize(true);
        // 设置Item默认动画，加也行，不加也行。
        binding.rankRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.rankRecyclerView.setFocusable(false);
        binding.rankRecyclerView.setAdapter(mRankingAdapter);
        binding.rankRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 这里在加入判断，判断是否滑动到底部
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    int last = ((LinearLayoutManager) binding.rankRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
//                    if (last == mRankingAdapter.getItemCount() - 1) {
//                        mPresenter.loadMoreDayEvalRank(mVoa.voaId());
//                    }
//                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mRankingAdapter.setCurVoaId(mVoa.voaId());
//        mPresenter.loadDayRank();
//        mPresenter.loadDayEvalRank(mVoa.voaId());
        binding.myRank.setVisibility(UserInfoManager.getInstance().isLogin() ? View.VISIBLE : View.GONE);
        binding.myRank.setOnClickListener(v -> {
            if ((rankOralBean == null) || (rankOralBean.getMyid() < 1)) {
                ToastUtil.showToast(mContext, "暂时没有数据");
                return;
            }
            Intent intent = new Intent(mContext, CommentActivity.class);
            intent.putExtra("uid", rankOralBean.getMyid());
            intent.putExtra("voaId", mVoa.voaId());
            intent.putExtra("userName", rankOralBean.getMyname());
            intent.putExtra("userPic", rankOralBean.getMyimgSrc());
            mContext.startActivity(intent);
        });

        binding.swipeRefresh.setOnRefreshListener(refreshLayout -> refreshData());
        binding.swipeRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetStateUtil.isConnected(mContext)) {
                    refreshLayout.finishRefresh();
                    return;
                }
                mPresenter.loadMoreDayEvalRank(mVoa.voaId());
                refreshLayout.finishLoadMore(1000);
            }
        });
    }

    private void refreshData() {
        if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
            showToastShort("排行信息需要使用数据网络");
            binding.swipeRefresh.finishRefresh();
            return;
        }
//        mPresenter.loadDayRank();
        mPresenter.loadDayEvalRank(mVoa.voaId());
        binding.swipeRefresh.finishRefresh(2000);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetStateUtil.isConnected(TalkShowApplication.getContext())) {
            mPresenter.loadDayEvalRank(mVoa.voaId());
        } else {
            showToastShort("排行信息需要使用数据网络");
        }
        MobclickAgent.onResume(mContext);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //解除绑定
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showRankingList(List<RankOralBean.DataBean> data) {
        if (data == null || data.size() < 1) {
            Log.e("EvalrankFragment", "showRankingList is null. ");
            return;
        }
        if (rankUserList != null) {
            rankUserList.clear();
            rankUserList.addAll(data);
        } else {
            rankUserList = data;
        }
        Collections.sort(rankUserList, new CommentActivity.SortByScore());
        RankOralBean.DataBean champion = rankUserList.get(0);
        setViews(champion, binding.ivChampion,  binding.tvChampionName,  binding.tvChampionCount,  binding.tvChampioscore,  binding.tvChampioAverage);
        mRankingAdapter.clear();
        mRankingAdapter.addData(rankUserList);
        mRankingAdapter.setCurVoaId(mVoa.voaId());
        mRankingAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMoreRankList(List<RankOralBean.DataBean> data) {
        if (data == null || data.size() < 1) {
            Log.e("EvalrankFragment", "showMoreRankList is null. ");
            return;
        }
        if (rankUserList != null) {
            rankUserList.addAll(data);
        } else {
            rankUserList = data;
        }
        Collections.sort(rankUserList, new CommentActivity.SortByScore());
        mRankingAdapter.addData(rankUserList);
        mRankingAdapter.setCurVoaId(mVoa.voaId());
        mRankingAdapter.notifyDataSetChanged();
    }

    @Override
    public void showUserInfo(RankOralBean bean) {
        /*Glide.with(mContext)
                .load(bean.getMyimgSrc())
                .transform(new CircleTransform(mContext))
                .placeholder(R.drawable.default_avatar)
                .into(binding.ivAvatar);*/
        Glide3Util.loadCircleImg(mContext,bean.getMyimgSrc(),R.drawable.default_avatar,binding.ivAvatar);
//        try {
//            RankingListBean.DataBean champion = bean.getData().get(0);
//            setViews(champion, binding.ivChampion,  binding.tvChampionName,  binding.tvChampionCount,  binding.tvChampioscore,  binding.tvChampioAverage);
//            RankingListBean.DataBean second = bean.getData().get(1);
//            setViews(second,  binding.ivSecondAvatar,  binding.tvSecondName,  binding.tvSecondCount,  binding.tvSecondScore,  binding.tvSecondAverage);
//            RankingListBean.DataBean third = bean.getData().get(2);
//            setViews(third,  binding.ivThirdAvatar,  binding.tvThirdName,  binding.tvThirdCount,  binding.tvThirdScore,  binding.tvThirdAverage);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        rankOralBean = bean;
        uid = bean.getMyid();
        binding.tvUsername.setText(bean.getMyname());
        binding.tvRank.setText(bean.getMyranking() + "");
        binding.count.setText("句子数：" + bean.getMycount());
        binding.score.setText(bean.getMyscores() + "分");
        binding.average.setText(getAverage(bean.getMyscores(), bean.getMycount()));
    }

    private void setViews(final RankOralBean.DataBean bean, ImageView avatar, TextView name, TextView count, TextView score, TextView average) {
        /*Glide.with(mContext)
                .load(bean.getImgSrc())
                .transform(new CircleTransform(mContext))
                .placeholder(R.drawable.default_avatar)
                .into(avatar);*/
        Glide3Util.loadCircleImg(mContext,bean.getImgSrc(),R.drawable.default_avatar,avatar);
        name.setText(bean.getName());
        count.setText(bean.getCount() + "句");
        average.setText(getAverage(bean.getScores(), bean.getCount()));
        score.setText(bean.getScores() + "分");
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((bean == null) || (bean.getUid() < 1)) {
                    ToastUtil.showToast(mContext, "暂时没有数据");
                    return;
                }
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("uid", bean.getUid());
                intent.putExtra("voaId", mVoa.voaId());
                intent.putExtra("userName", bean.getName());
                intent.putExtra("userPic", bean.getImgSrc());
                mContext.startActivity(intent);
            }
        });
    }

    private String getAverage(int score, int count) {
        if (count == 0) {
            return "0分";
        }
        return (score / count) + "分";
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog = new LoadingDialog(mContext);

        if (isCurPage){
            mLoadingDialog.show();
        }
    }

    @Override
    public void dismissLoadingDialog() {
        if ((mLoadingDialog != null) && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(RefreshRankEvent event) {
        int voaId = SPconfig.Instance().loadInt(Config.currVoaId);
        if (Playmanager.getInstance().getVoaFromList(voaId) != null) {
            Log.e("EvalrankFragment", "RefreshEvent  ----------");
            mVoa = Playmanager.getInstance().getVoaFromList(voaId);
            refreshData();
        }
    }

}
