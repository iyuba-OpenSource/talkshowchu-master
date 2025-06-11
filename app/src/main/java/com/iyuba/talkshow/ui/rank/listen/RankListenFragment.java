package com.iyuba.talkshow.ui.rank.listen;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.iyuba.talkshow.data.model.RankListenBean;
import com.iyuba.talkshow.databinding.FragmentRankBinding;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.rank.ShareMsgListener;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.ui.widget.divider.LinearItemDivider;
import com.iyuba.talkshow.util.NetStateUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.umeng.analytics.MobclickAgent;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

/**排行榜--听力界面
 * Created by carl shen on 2021/7/26
 * New Primary English, new study experience.
 */
public class RankListenFragment extends BaseFragment implements RankListenView, ShareMsgListener {
    private static final String RANKTYPE = "rank_type";
    @Inject
    RankListenPresenter mPresenter;
    LoadingDialog mLoadingDialog;
    @Inject
    RankListenAdapter mRankingAdapter;
    FragmentRankBinding binding ;
    int uid;
    private String rankType;
    private String type = "D";
    private int total = 30;
    private int start = 0;
    private int dialog_position = 0;

    private boolean isFirst = true;//是否第一次进入
    private String shareMsg;//需要分享的信息

    public static RankListenFragment newInstance(String rank_type) {
        RankListenFragment fragment = new RankListenFragment();
        Bundle args = new Bundle();
        args.putString(RANKTYPE, rank_type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
        mPresenter.attachView(this);
        rankType = getArguments().getString(RANKTYPE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRankBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        total = 30;
        start = 0;
        binding.rankRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
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


        binding.rankNote.setText("今天");
        //选择今天，本周，本月
        binding.rankNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseHeartType(binding.rankNote.getText().toString());
            }
        });
        binding.myRank.setVisibility(UserInfoManager.getInstance().isLogin() ? View.VISIBLE : View.GONE);

        binding.swipeRefresh.setOnRefreshListener(refreshLayout -> refreshData());
        binding.swipeRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetStateUtil.isConnected(mContext)) {
                    refreshLayout.finishRefresh();
                    return;
                }
                getInfoRead();
                refreshLayout.finishLoadMore(1000);
            }
        });
    }

    private void refreshData() {
        if (!NetStateUtil.isConnected(RankListenFragment.this.mContext)) {
            binding.swipeRefresh.finishRefresh();
            return;
        }
        start = 0;
        getInfoRead();
        binding.swipeRefresh.finishRefresh(2000);
    }

    /**
     * 提供给用户选择动态类型的单选列表对话框
     */
    private void choseHeartType(String choose_type) {
        final String typeArray[] = new String[]{"今天", "本周"};
        for (int i = 0; i < typeArray.length; i++) {
            if (choose_type.equals(typeArray[i])) {
                dialog_position = i;
            }
        }
        AlertDialog.Builder b = new AlertDialog.Builder(mContext);
        b.setSingleChoiceItems(typeArray,  //装载数组信息
                //默认选中选项
                dialog_position,
                //为列表添加监听事件
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (dialog_position != 0) {
                                    type = "D";
                                    binding.rankNote.setText(typeArray[0]);
                                    start = 0;
                                    getInfoRead();
                                }
                                break;
                            case 1:
                                if (dialog_position != 1) {
                                    type = "W";
                                    binding.rankNote.setText(typeArray[1]);
                                    start = 0;
                                    getInfoRead();
                                }
                                break;
                            case 2:
                                if (dialog_position != 2) {
                                    type = "M";
                                    binding.rankNote.setText(typeArray[2]);
                                    getInfoRead();
                                }
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void getInfoRead() {
        switch (rankType) {
            case "口语":
//                mPresenter.loadEvalRankList(type, 0, start, total);
                break;
            case "听力":
            default:
                mPresenter.loadListenRankList(type, 0, start, total);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(mContext);

        if (isFirst&&NetStateUtil.isConnected(mContext)) {
            isFirst = false;
            getInfoRead();
        }
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
    public void showRankingList(List<RankListenBean.DataBean> data) {
        if (data == null || data.size() < 1) {
            return;
        }
        start += data.size();
        Collections.sort(data, new SortByTime());
        RankListenBean.DataBean champion = data.remove(0);
        setViews(champion, binding.ivChampion,  binding.tvChampionName,  binding.tvChampionCount,  binding.tvChampioscore,  binding.tvChampioAverage);
        mRankingAdapter.setData(data);
        mRankingAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMoreRankList(List<RankListenBean.DataBean> data) {
        if (data == null || data.size() < 1) {
            return;
        }
        start += data.size();
        Collections.sort(data, new SortByTime());
        mRankingAdapter.addData(data);
        mRankingAdapter.notifyDataSetChanged();
    }

    @Override
    public void showUserInfo(RankListenBean bean) {
        Log.e("RankOralFragment", "showUserInfo is called. " );
        /*Glide.with(mContext)
                .load(bean.myimgSrc)
                .transform(new CircleTransform(mContext))
                .placeholder(R.drawable.default_avatar)
                .into(binding.ivAvatar);*/
        Glide3Util.loadCircleImg(mContext,bean.myimgSrc,R.drawable.default_avatar,binding.ivAvatar);
        uid = bean.myid;
        binding.tvUsername.setText(bean.myname);
        binding.tvRank.setText(bean.myranking + "");
        binding.count.setText("文章数：" + bean.totalEssay);
        binding.score.setText(+((int) bean.totalTime/60) + "分钟");
        binding.average.setText("单词数：" + bean.totalWord);

        shareMsg = "用户"+UserInfoManager.getInstance().getUserName()+"的{0}的听力排行：文章数："+bean.totalEssay+" 单词数："+bean.totalWord+" 时长："+(int)(bean.totalTime/60)+" 排名："+bean.myranking;
    }

    private void setViews(final RankListenBean.DataBean bean, ImageView avatar, TextView name, TextView count, TextView score, TextView average) {
        /*Glide.with(mContext)
                .load(bean.imgSrc)
                .transform(new CircleTransform(mContext))
                .placeholder(R.drawable.default_avatar)
                .into(avatar);*/
        Glide3Util.loadCircleImg(mContext,bean.imgSrc,R.drawable.default_avatar,avatar);
        name.setText(bean.name);
        count.setText(bean.totalEssay + "文章");
        average.setText(bean.totalWord + "单词");
        score.setText(((int) bean.totalTime/60) + "分钟");
    }

    private String getAverage(int score, int count) {
        if (count == 0) {
            return "0分";
        }
        return (score / count) + "分";
    }

    @Override
    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mContext);
        }
        mLoadingDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        if ((mLoadingDialog != null) && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public String getShareMsg() {
        String date = null;
        if (type.equals("D")){
            date = "今日";
        }else if (type.equals("W")){
            date = "本周";
        }else if (type.equals("M")){
            date = "本月";
        }

        return MessageFormat.format(shareMsg,date);
    }

    @Override
    public String getShareUrl() {
        return mPresenter.getShareRankUrl();
    }

    public class SortByTime implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            RankListenBean.DataBean s1 = (RankListenBean.DataBean) o1;
            RankListenBean.DataBean s2 = (RankListenBean.DataBean) o2;
            if (s1.ranking > s2.ranking)
                return 1;
            return -1;
        }
    }
}
