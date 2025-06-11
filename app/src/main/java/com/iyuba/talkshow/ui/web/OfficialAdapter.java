package com.iyuba.talkshow.ui.web;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.ItemOfficialBinding;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.wordtest.db.OfficialAccount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by carl shen on 2021/6/7
 * New Primary English, new study experience.
 */
public class OfficialAdapter extends RecyclerView.Adapter<OfficialAdapter.OfficialViewHolder> {

    private List<OfficialAccount> mData;
    private OnOfficialClickListener mListener;
    ItemOfficialBinding binding ;

    @Inject
    public OfficialAdapter() {
        this.mData = new ArrayList<>();
    }

    @Override
    public OfficialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = ItemOfficialBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new OfficialViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(OfficialViewHolder holder, int position) {
        OfficialAccount collect = mData.get(position);
        if (collect != null) {
            Log.e("OfficialPresenter", "onBindViewHolder image_url " + collect.image_url);
            /*Glide.with(holder.itemView.getContext())
                    .load(collect.image_url)
                    .centerCrop()
                    .signature(new StringSignature(collect.image_url))
                    .placeholder(R.drawable.default_pic)
                    .into(holder.imageIv);*/
            Glide3Util.loadImg(holder.itemView.getContext(),collect.image_url,R.drawable.default_pic,holder.imageIv);
            holder.nameTv.setText(collect.title);
            holder.fromTv.setText(collect.newsfrom);
            String[] mCreateTime = collect.createTime.split(" ");
            if ((mCreateTime != null) && (mCreateTime.length > 0)) {
                holder.timeTv.setText(mCreateTime[0]);
            } else {
                holder.timeTv.setText(collect.createTime);
            }
            holder.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onOfficialClick(collect);
                }
            });
        } else {
            Log.e("OfficialPresenter", "onBindViewHolder OfficialAccount is null? ");
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    public OfficialAccount getItemData(int position) {
        return mData == null ? null : mData.get(position);
    }

    public void setData(List<OfficialAccount> data) {
        if (data == null) {
            mData = new ArrayList<>();
        } else {
            mData = data;
        }
        sortData(mData);
    }

    public void addData(List<OfficialAccount> data) {
        if (mData == null) {
            mData = data;
        } else {
            mData.addAll(data);
        }
        sortData(mData);
    }

    private void sortData(List<OfficialAccount> data) {
        Collections.sort(data, new Comparator<OfficialAccount>() {
            @Override
            public int compare(OfficialAccount o1, OfficialAccount o2) {
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                return o2.id - o1.id;
            }
        });
    }

    public void setListener(OnOfficialClickListener mListener) {
        this.mListener = mListener;
    }

    class OfficialViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageIv;
        private final TextView nameTv;
        private final TextView fromTv;
        private final TextView timeTv;
        public OfficialViewHolder(ItemOfficialBinding itemView) {
            super(itemView.getRoot());
            imageIv = itemView.imageIv;
            nameTv = itemView.nameTv;
            fromTv = itemView.fromTv;
            timeTv = itemView.timeTv;
        }
    }
}

interface OnOfficialClickListener {
    void onOfficialClick(OfficialAccount collect);
}
