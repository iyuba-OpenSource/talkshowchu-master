package com.iyuba.talkshow.ui.detail.recommend;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.ItemVoaBinding;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by Administrator on 2016/11/29 0029.
 */

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.RecommendHolder> {

    private VoaCallback mVoaCallback;
    private List<Voa> mVoaList;

    ItemVoaBinding binding ;
    @Inject
    public RecommendAdapter() {
        this.mVoaList = new ArrayList<>();
    }

    @Override
    public RecommendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = ItemVoaBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new RecommendHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(RecommendHolder holder, int position) {
        Voa voa = mVoaList.get(position);
        if (voa == null) {
            return;
        }
        RecommendHolder binding = holder;
        Glide.with(holder.itemView.getContext())
                .load(voa.pic())
                .centerCrop()
                .placeholder(R.drawable.voa_default)
                .into(binding.pic);

        if(voa.titleCn() != null) {
            binding.titleCn.setText(voa.titleCn());
        } else {
            binding.titleCn.setText(voa.descCn());
        }
        binding.readCount.setText(String.valueOf(voa.readCount()));
        holder.setOnClick();
    }

    @Override
    public int getItemCount() {
        return mVoaList.size();
    }

    public void setmVoaCallback(VoaCallback mVoaCallback) {
        this.mVoaCallback = mVoaCallback;
    }

    public void setmVoaList(List<Voa> mVoaList) {
        this.mVoaList = mVoaList;
    }

    class RecommendHolder extends RecyclerView.ViewHolder {
        TextView titleCn;
        TextView readCount;
        ImageView pic;

        RecommendHolder(View itemView) {
            super(itemView);
            titleCn = itemView.findViewById(R.id.titleCn);
            pic = itemView.findViewById(R.id.pic);
            readCount = itemView.findViewById(R.id.readCount);
        }

        void onVoaClicked(View view) {
            if(mVoaCallback != null) {
                mVoaCallback.onVoaClicked(mVoaList.get(getPosition()));
            }
        }

        public void setOnClick() {
            binding.voa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVoaClicked(v);
                }
            });
        }
    }

    interface VoaCallback {
        void onVoaClicked(Voa voa);
    }
}
