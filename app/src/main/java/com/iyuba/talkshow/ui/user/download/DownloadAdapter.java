package com.iyuba.talkshow.ui.user.download;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Download;
import com.iyuba.talkshow.databinding.ItemDownloadBinding;
import com.iyuba.talkshow.lil.help_mvp.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by Administrator on 2016/12/27/027.
 */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {

    private List<Download> mData;
    private final List<String> mSelectedData;
    private OnDownloadClickListener mListener;
    private int mMode = Mode.SHOW;

    @Inject
    public DownloadAdapter() {
        mData = new ArrayList<>();
        mSelectedData = new ArrayList<>();
    }

    @Override
    public DownloadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemDownloadBinding binding = ItemDownloadBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new DownloadViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(DownloadViewHolder holder, int position) {
        Download download = mData.get(position);
        holder.setItem(download);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setListener(OnDownloadClickListener mListener) {
        this.mListener = mListener;
    }

    public void setData(List<Download> mData) {
        this.mData = mData;
    }

    public void setMode(int mMode) {
        this.mMode = mMode;
    }

    public int getMode() {
        return mMode;
    }

    public List<String> getSelection() {
        return mSelectedData;
    }

    public void clearSelection() {
        mSelectedData.clear();
    }

    public void addAllSelection() {
        mSelectedData.clear();
        for (Download download:mData) {
            mSelectedData.add(String.valueOf(download.voaId()));
        }
        notifyDataSetChanged();
    }


    class DownloadViewHolder extends RecyclerView.ViewHolder {

        private final ImageView deleteIv;
        private final ImageView imageIv;
        private final TextView nameTv;
        private final TextView timeTv;

        public DownloadViewHolder(ItemDownloadBinding itemView) {
            super(itemView.getRoot());
            deleteIv = itemView.deleteIv;
            imageIv = itemView.imageIv;
            nameTv = itemView.nameTv;
            timeTv = itemView.timeTv;
            itemView.getRoot().setOnClickListener(v -> onClickItem());
        }

        public void onClickItem() {
            if (mMode == Mode.SHOW) {
                mListener.onItemClick(mData.get(getPosition()));
            } else {
                Download download = mData.get(getPosition());
                if (mSelectedData.contains(String.valueOf(download.voaId()))) {
                    mSelectedData.remove(String.valueOf(download.voaId()));
                    deleteIv.setImageResource(R.drawable.checkbox_unchecked);
                } else {
                    mSelectedData.add(String.valueOf(download.voaId()));
                    deleteIv.setImageResource(R.drawable.checkbox_checked);

                }
            }
        }

        public void setItem(Download download) {
            if (mMode == Mode.SHOW) {
                deleteIv.setVisibility(View.INVISIBLE);
            } else {
                deleteIv.setVisibility(View.VISIBLE);
                if (mSelectedData.contains(String.valueOf(download.voaId()))) {
                    deleteIv.setImageResource(R.drawable.checkbox_checked);
                } else {
                    deleteIv.setImageResource(R.drawable.checkbox_unchecked);
                }
            }
            Glide.with(itemView.getContext())
                    .load(download.getVoa().pic())
                    .centerCrop()
                    .placeholder(R.drawable.default_pic)
                    .into(imageIv);
            nameTv.setText(download.getVoa().titleCn());

            //这里纯sb，你保存的时间是哪个你不知道啊
//            timeTv.setText(download.getVoa().createTime());
            timeTv.setText(download.date());
        }
    }
}

interface OnDownloadClickListener {
    void onItemClick(Download download);
}

interface Mode {
    int SHOW = 0;
    int EDIT = 1;
}