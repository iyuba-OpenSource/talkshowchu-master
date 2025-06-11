package com.iyuba.talkshow.ui.user.collect;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Collect;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.ItemCollectionBinding;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    private List<Collect> mData;
    private final List<String> mSelectedData;
    private OnCollectionClickListener mListener;
    private int mMode = Mode.SHOW;
    @Inject
    CollectionPresenter mPresenter;
    ItemCollectionBinding binding ;

    @Inject
    public CollectionAdapter() {
        this.mData = new ArrayList<>();
        mSelectedData = new ArrayList<>();
    }

    @Override
    public CollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = ItemCollectionBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new CollectionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(CollectionViewHolder holder, int position) {
        Collect collect = mData.get(position);
        if(mMode == Mode.SHOW) {
            holder.deleteIv.setVisibility(View.GONE);
        } else {
            holder.deleteIv.setVisibility(View.VISIBLE);
            if (mSelectedData.contains(collect.voaId())) {
                holder.deleteIv.setImageResource(R.drawable.checkbox_checked);
            } else {
                holder.deleteIv.setImageResource(R.drawable.checkbox_unchecked);
            }
        }

        if ((collect.getVoa() == null) || TextUtils.isEmpty(collect.getVoa().pic())) {
            List<Voa> voaList = mPresenter.getVoaById(collect.voaId());
            if (voaList != null && voaList.size() > 0) {
                collect.setVoa(voaList.get(0));
            } else {
                Log.e("CollectionAdapter", "getVoaById no voaId " + collect.voaId());
            }
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.default_pic)
                    .centerCrop()
                    .placeholder(R.drawable.default_pic)
                    .into(holder.imageIv);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(collect.getVoa().pic())
                    .centerCrop()
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
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Collect> mData) {
        this.mData = mData;
    }

    public List<String> getSelection() {
        return mSelectedData;
    }

    public void clearSelection() {
        mSelectedData.clear();
    }

    public void setListener(OnCollectionClickListener mListener) {
        this.mListener = mListener;
    }

    public void setMode(int mMode) {
        this.mMode = mMode;
    }

    public int getMode() {
        return mMode;
    }

    public void delete() {
        for(Collect collect : mData) {
            if(mSelectedData.contains(String.valueOf(collect.voaId()))) {
                mData.remove(collect);
            }
        }
        notifyDataSetChanged();
    }

    class CollectionViewHolder extends RecyclerView.ViewHolder{

        private final ImageView deleteIv;
        private final ImageView imageIv;
        private final TextView nameTv;
        private final TextView gradeTv;
        private final TextView timeTv;
        public CollectionViewHolder(ItemCollectionBinding itemView) {
            super(itemView.getRoot());
            deleteIv = itemView.deleteIv;
            imageIv = itemView.imageIv;
            nameTv = itemView.nameTv;
            gradeTv = itemView.gradeTv;
            timeTv = itemView.timeTv;
            itemView.getRoot().setOnClickListener(v -> onClickItem());
        }

        public void onClickItem() {
            if(mMode == Mode.SHOW) {
                mListener.onCollectionClick(mData.get(getPosition()));
            } else {
                Collect collect = mData.get(getPosition());
                if(mSelectedData.contains(String.valueOf(collect.voaId()))) {
                    mSelectedData.remove(String.valueOf(collect.voaId()));
                    deleteIv.setImageResource(R.drawable.checkbox_unchecked);
                } else {
                    mSelectedData.add(String.valueOf(collect.voaId()));
                    deleteIv.setImageResource(R.drawable.checkbox_checked);
                }
            }
        }
    }
}

interface OnCollectionClickListener {
    void onCollectionClick(Collect collect);
}

interface Mode {
    int SHOW = 0;
    int EDIT = 1;
}