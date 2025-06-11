package com.iyuba.talkshow.ui.user.me.dubbing.draft;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Record;
import com.iyuba.talkshow.databinding.ItemDraftBinding;
import com.iyuba.talkshow.ui.user.me.dubbing.Mode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.DraftViewHolder> {

    private List<Record> mData;
    private final List<String> mSelectedData;
    private OnDraftClickListener mListener;
    private static int mMode = Mode.SHOW;


    @Inject
    public DraftAdapter() {
        mData = new ArrayList<>();
        mSelectedData = new ArrayList<>();
    }

    @Override
    public DraftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemDraftBinding binding = ItemDraftBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new DraftViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(DraftViewHolder holder, int position) {
        Record record = mData.get(position);
        holder.setItem(record);
        holder.setClick();
    }

    public void delete() {
        for (Record record : mData) {
            if (mSelectedData.contains(String.valueOf(record.timestamp()))) {
                mData.remove(record);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setOnDraftClickListener(OnDraftClickListener mListener) {
        this.mListener = mListener;
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

    public List<String> getSelectedData() {
        return mSelectedData;
    }

    public void clearSelectedData() {
        mSelectedData.clear();
    }

    public void addAll() {
            mSelectedData.clear();
            for (Record record:mData) {
                mSelectedData.add(String.valueOf(record.timestamp()));
            }
            notifyDataSetChanged();
    }

    class DraftViewHolder extends RecyclerView.ViewHolder {

        private final ImageView deleteIv;
        private final ViewGroup draftLayout;
        private final ImageView imageIv;
        private final TextView nameTv;
        private final TextView progressTv;
        private final TextView timeTv;

        public DraftViewHolder(ItemDraftBinding itemView) {
            super(itemView.getRoot());
            deleteIv = itemView.deleteIv;
            draftLayout = itemView.draftLayout;
            imageIv = itemView.imageIv;
            nameTv = itemView.nameTv;
            progressTv = itemView.progressTv;
            timeTv = itemView.timeTv;
        }

        public void onClickItem() {
            if (mMode == Mode.SHOW) {
                mListener.onDraftClick(mData.get(getPosition()));
            } else {
                Record record = mData.get(getPosition());
                if (mSelectedData.contains(String.valueOf(record.timestamp()))) {
                    mSelectedData.remove(String.valueOf(record.timestamp()));
                    deleteIv.setImageResource(R.drawable.checkbox_unchecked);
                } else {
                    mSelectedData.add(String.valueOf(record.timestamp()));
                    deleteIv.setImageResource(R.drawable.checkbox_checked);
                }
            }
        }

        public void setClick() {
            draftLayout.setOnClickListener(v -> onClickItem());
        }

        public void setItem(Record record) {
            if (mMode == Mode.SHOW) {
                deleteIv.setVisibility(View.INVISIBLE);
            } else {
                deleteIv.setVisibility(View.VISIBLE);
                if (mSelectedData.contains(String.valueOf(record.timestamp()))) {
                    deleteIv.setImageResource(R.drawable.checkbox_checked);
                } else {
                    deleteIv.setImageResource(R.drawable.checkbox_unchecked);
                }
            }

            Glide.with(itemView.getContext())
                    .load(record.img())
                    .centerCrop()
                    .placeholder(R.drawable.default_pic)
                    .into(imageIv);
            nameTv.setText(record.titleCn());
            String progress = MessageFormat.format(
                    itemView.getContext().getString(R.string.draft_progress),
                    record.finishNum(), record.totalNum());
            progressTv.setText(progress);
            timeTv.setText(record.date());
        }
    }
}

interface OnDraftClickListener {
    void onDraftClick(Record record);
}
