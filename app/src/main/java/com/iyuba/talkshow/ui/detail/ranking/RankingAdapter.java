package com.iyuba.talkshow.ui.detail.ranking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Ranking;
import com.iyuba.talkshow.databinding.ItemRankingBinding;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;


public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingHolder> {

    private List<Ranking> mRankingList;
    private RankingCallback mRankingCallback;
    private Context context ;

    @Inject
    public RankingAdapter() {
        mRankingList = new ArrayList<>();
    }

    @NotNull
    @Override
    public RankingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        ItemRankingBinding binding = ItemRankingBinding.inflate( LayoutInflater.from(parent.getContext()),parent,false);
        return new RankingHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RankingHolder holder, int position) {
        Ranking ranking = mRankingList.get(position);
        setRanking(holder.rank, position);
        Context context = holder.itemView.getContext();
        /*Glide.with(context)
                .load(ranking.imgSrc())
                .centerCrop()
                .transform(new CircleTransform(context))
                .placeholder(R.drawable.default_pic)
                .into(holder.photo);*/
        Glide3Util.loadCircleImg(context,ranking.imgSrc(),R.drawable.default_pic,holder.photo);
        holder.thumbsNum.setText(String.valueOf(ranking.agreeCount()));
        holder.usernameTv.setText(ranking.userName());
        holder.timeTv.setText(ranking.createDate());
        holder.tvScore.setText(Math.round(ranking.score()) + "分");
        holder.setClick(position);
    }

    private void setRanking(TextView tRank, int position) {
        switch (position) {
            case 0:
                tRank.setText("");
                tRank.setBackground(context.getResources().getDrawable(R.drawable.rank_first));
                break;
            case 1:
                tRank.setText("");
                tRank.setBackground(context.getResources().getDrawable(R.drawable.rank_second));
                break;
            case 2:
                tRank.setText("");
                tRank.setBackground(context.getResources().getDrawable(R.drawable.rank_third));
                break;
            default:
                tRank.setText(String.valueOf(position+1));
                tRank.setBackgroundResource(0);
                break;
        }
    }

    public void setRankingList(List<Ranking> mRankingList) {
        this.mRankingList = mRankingList;
//        Collections.sort(this.mRankingList, new SortByScore());
    }
    public void setMoreRankingList(List<Ranking> mList) {
        mRankingList.addAll(mList);
//        Collections.sort(this.mRankingList, new SortByScore());
    }

    public void setRankingCallback(RankingCallback mRankingCallback) {
        this.mRankingCallback = mRankingCallback;
    }

    @Override
    public int getItemCount() {
        return mRankingList.size();
    }

    class RankingHolder extends RecyclerView.ViewHolder {

        public TextView rank;
        public ImageView photo;
        public TextView thumbsNum;
        public TextView usernameTv;
        public TextView timeTv;
        public TextView tvScore;
        private final ViewGroup rankingLayout;

        RankingHolder(ItemRankingBinding itemView) {
            super(itemView.getRoot());
            rank = itemView.rank;
            rankingLayout = itemView.rankingLayout;
            thumbsNum = itemView.thumbsNum;
            usernameTv = itemView.usernameTv;
            timeTv = itemView.timeTv;
            tvScore = itemView.tvScore;
            photo = itemView.photo;
        }

        void onClickLayout(int position) {
            Ranking ranking = mRankingList.get(position);
            mRankingCallback.onClickLayout(ranking, position);    //根据分数进行排序 2018.7.16
        }

        public void setClick(int position) {
            rankingLayout.setOnClickListener(v -> onClickLayout(position));
        }
    }

    public interface RankingCallback {
        void onClickThumbs(int id);

        void onClickLayout(Ranking ranking, int pos);
    }

    //根据分数从高到低进行排序 modified 2018.7.16
    private static class SortByScore implements Comparator{
        @Override
        public int compare(Object o1, Object o2) {
            Ranking s1 = (Ranking) o1;
            Ranking s2 = (Ranking) o2;
            if (s1.score() < s2.score()) //小的在后面
                return 1;
            return -1;
        }
    }
}
