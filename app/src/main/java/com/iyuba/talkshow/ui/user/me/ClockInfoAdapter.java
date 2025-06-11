package com.iyuba.talkshow.ui.user.me;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.data.model.result.ShareInfoRecord;
import com.iyuba.talkshow.databinding.ActivityClockListBinding;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by carl shen on 2021/4/12
 * New Primary English, new study experience.
 */
public class ClockInfoAdapter extends RecyclerView.Adapter<ClockInfoAdapter.Holder> {

    private List<ShareInfoRecord> mData;
    ActivityClockListBinding binding ;

    @Inject
    public ClockInfoAdapter() {
        this.mData = new ArrayList<>();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = ActivityClockListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        ShareInfoRecord bean = mData.get(position);
        holder.rank.setText(bean.uid);
        holder.count.setText(bean.scan + "");
        holder.average.setText(bean.createtime);
        holder.score.setText(bean.appid);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setRankingList(List<ShareInfoRecord> mRankingList) {
        this.mData = mRankingList;
    }
    public void setMoreRankingList(List<ShareInfoRecord> mList) {
        mData.addAll(mList);
    }

    static class Holder extends RecyclerView.ViewHolder {
        public TextView count;
        public TextView average;
        public TextView rank;
        public TextView score;

        public Holder(ActivityClockListBinding itemView) {
            super(itemView.getRoot());
            count = itemView.count;
            average = itemView.average;
            rank = itemView.rank;
            score = itemView.score;
        }
    }
}
