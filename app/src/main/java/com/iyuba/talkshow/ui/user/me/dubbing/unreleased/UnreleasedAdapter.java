package com.iyuba.talkshow.ui.user.me.dubbing.unreleased;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.databinding.ItemUnreleasedBinding;
import com.iyuba.talkshow.ui.user.me.dubbing.Mode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class UnreleasedAdapter extends RecyclerView.Adapter<UnreleasedAdapter.UnreleasedViewHolder> {

    private List<Record> mData;
    private final List<Record> mSelectedData;
    private OnUnreleasedClickListener mListener;
    private static int mMode = Mode.SHOW;
    ItemUnreleasedBinding binding ;  

    @Inject
    public UnreleasedAdapter() {
        mData = new ArrayList<>();
        mSelectedData = new ArrayList<>();
    }

    @Override
    public UnreleasedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        
        binding = ItemUnreleasedBinding.inflate( LayoutInflater.from(parent.getContext()),parent,false);
        return new UnreleasedViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(UnreleasedViewHolder holder, int position) {
        Record record = mData.get(position);
        if (mMode == Mode.SHOW) {
            binding.deleteIv.setVisibility(View.INVISIBLE);
        } else {
            binding.deleteIv.setVisibility(View.VISIBLE);
            if (mSelectedData.contains(record.timestamp())) {
                binding.deleteIv.setImageResource(android.R.drawable.checkbox_on_background);
            } else {
                binding.deleteIv.setImageResource(android.R.drawable.checkbox_off_background);
            }
        }

        Glide.with(holder.itemView.getContext())
                .load(record.img())
                .centerCrop()
                .placeholder(R.drawable.default_pic)
                .into(binding.imageIv);
        binding.nameTv.setText(record.titleCn());
        binding.timeTv.setText(record.date());
        holder.setClick();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setOnUnreleasedClickListener(OnUnreleasedClickListener listener) {
        this.mListener = listener;
    }

    public void setData(List<Record> data) {
        this.mData = data;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public List<Record> getSelectedData() {
        return mSelectedData;
    }

    public void clearSelectedData() {
        mSelectedData.clear();
    }

    class UnreleasedViewHolder extends RecyclerView.ViewHolder {

        public UnreleasedViewHolder(View itemView) {
            super(itemView);
        }

        public void onClickItem() {
            if (mMode == Mode.SHOW) {
                if (mListener != null) {
                    mListener.onUnreleasedClick(mData.get(getPosition()));
                }
            } else {
                Record record = mData.get(getPosition());
                if (mSelectedData.contains(record)) {
                    mSelectedData.remove(record);
                    binding.deleteIv.setImageResource(R.drawable.checkbox_unchecked);
                } else {
                    mSelectedData.add(record);
                    binding.deleteIv.setImageResource(R.drawable.checkbox_checked);
                }
            }
        }

        public void setClick() {
            binding.unreleasedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem();
                }
            });
        }
    }
}

interface OnUnreleasedClickListener {
    void onUnreleasedClick(Record record);
}