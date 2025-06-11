package com.iyuba.talkshow.lil.help_fix.ui.collect.chapter.junior;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Collect;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.ItemCollectionBinding;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.ui.user.collect.CollectionPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @title:
 * @date: 2023/7/17 18:38
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class JuniorChapterCollectAdapter extends RecyclerView.Adapter<JuniorChapterCollectAdapter.JuniorCollectHolder> {

    private List<Collect> list;

    @Inject
    CollectionPresenter mPresenter;

    @Inject
    public JuniorChapterCollectAdapter() {
        this.list = new ArrayList<>();
    }

    @NonNull
    @Override
    public JuniorCollectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCollectionBinding binding = ItemCollectionBinding.inflate(LayoutInflater.from(ResUtil.getInstance().getContext()),parent,false);
        return new JuniorCollectHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JuniorCollectHolder holder, int position) {
        if (holder==null){
            return;
        }

        Collect collect = list.get(position);
        if ((collect.getVoa() == null) || TextUtils.isEmpty(collect.getVoa().pic())) {
            List<Voa> voaList = mPresenter.getVoaById(collect.voaId());
            if (voaList != null && voaList.size() > 0) {
                collect.setVoa(voaList.get(0));
            } else {
                Log.e("CollectionAdapter", "getVoaById no voaId " + collect.voaId());
            }
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.default_pic)
                    .fitCenter()
                    .placeholder(R.drawable.default_pic)
                    .into(holder.imageIv);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(collect.getVoa().pic())
                    .fitCenter()
                    .placeholder(R.drawable.default_pic)
                    .into(holder.imageIv);
        }
        if (collect.getVoa() == null) {
            holder.nameTv.setText(collect.uid() + "");
            holder.gradeTv.setText(collect.voaId() + "");
        } else {
            holder.nameTv.setText(collect.getVoa().title());
            holder.gradeTv.setText(collect.getVoa().titleCn());
        }
        String[] mCreateTime = collect.date().split(" ");
        if ((mCreateTime != null) && (mCreateTime.length > 0)) {
            holder.timeTv.setText(mCreateTime[0]);
        } else {
            holder.timeTv.setText(collect.date());
        }

        //点击
        holder.itemView.setOnClickListener(v->{
            if (singleClickListener!=null){
                singleClickListener.onClick(collect);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener!=null){
                longClickListener.onClick(collect);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class JuniorCollectHolder extends RecyclerView.ViewHolder{

        private ImageView deleteIv;
        private ImageView imageIv;
        private TextView nameTv;
        private TextView gradeTv;
        private TextView timeTv;

        public JuniorCollectHolder(ItemCollectionBinding binding){
            super(binding.getRoot());

            imageIv = binding.imageIv;
            nameTv = binding.nameTv;
            gradeTv = binding.gradeTv;
            timeTv = binding.timeTv;
            deleteIv = binding.deleteIv;
            deleteIv.setVisibility(View.GONE);
        }
    }

    //回调接口
    private OnSimpleClickListener<Collect> singleClickListener;

    public void setSingleClickListener(OnSimpleClickListener<Collect> singleClickListener) {
        this.singleClickListener = singleClickListener;
    }

    private OnSimpleClickListener<Collect> longClickListener;

    public void setLongClickListener(OnSimpleClickListener<Collect> longClickListener) {
        this.longClickListener = longClickListener;
    }

    //刷新数据
    public void refreshData(List<Collect> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }
}
