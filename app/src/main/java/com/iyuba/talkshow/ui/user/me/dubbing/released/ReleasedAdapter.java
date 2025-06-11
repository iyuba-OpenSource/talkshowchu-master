package com.iyuba.talkshow.ui.user.me.dubbing.released;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Ranking;
import com.iyuba.talkshow.databinding.ItemReleasedBinding;
import com.iyuba.talkshow.ui.user.me.dubbing.Mode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ReleasedAdapter extends RecyclerView.Adapter<ReleasedAdapter.ReleasedViewHolder>{

    private List<Ranking> mData;
    private final List<Integer> mSelectedData;
    private OnReleasedClickListener mListener;
    private static int mMode = Mode.SHOW;


    @Inject
    public ReleasedAdapter() {
        mData = new ArrayList<>();
        mSelectedData = new ArrayList<>();
    }

    @Override
    public ReleasedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemReleasedBinding binding = ItemReleasedBinding.inflate(inflater,parent,false);
        return new ReleasedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ReleasedViewHolder holder, int position) {
        Ranking ranking = mData.get(position);
        holder.setItem(ranking);
        holder.setClick();
    }

    public void delete() {
        for(Ranking record : mData) {
            if(mSelectedData.contains(record.id())) {
                mData.remove(record);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setOnReleasedClickListener(OnReleasedClickListener listener) {
        this.mListener = listener;
    }

    public void setData(List<Ranking> data) {
        this.mData = data;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public List<Integer> getSelectedData() {
        return mSelectedData;
    }

    public void clearSelectedData() {
        mSelectedData.clear();
    }

    public void addAll() {
        mSelectedData.clear();
        for (Ranking record:mData) {
            mSelectedData.add(record.id());
        }
        notifyDataSetChanged();

    }

    class ReleasedViewHolder extends RecyclerView.ViewHolder {

        ImageView deleteIv ;
        ImageView imageIv ;
        private final TextView nameTv;
        private final TextView timeTv;
        private final TextView thumbTv;
        private final View root;

        public ReleasedViewHolder(ItemReleasedBinding binding) {
            super(binding.getRoot());
            deleteIv = binding.deleteIv;
            imageIv = binding.imageIv;
            nameTv = binding.nameTv;
            thumbTv = binding.thumbTv;
            timeTv = binding.timeTv;
            root = binding.getRoot();
        }

        public void onClickItem() {
            if(mMode == Mode.SHOW) {
                mListener.onReleasedClick(mData.get(getPosition()));
            } else {
                Ranking ranking = mData.get(getPosition());
                if(mSelectedData.contains(ranking.id())) {
                    mSelectedData.remove(ranking.id());
                    deleteIv.setImageResource(R.drawable.checkbox_unchecked);
                } else {
                    mSelectedData.add(ranking.id());
                    deleteIv.setImageResource(R.drawable.checkbox_checked);
                }
            }
        }

        public void setItem(Ranking ranking) {
            if(mMode == Mode.SHOW) {
                deleteIv.setVisibility(View.INVISIBLE);
            } else {
                deleteIv.setVisibility(View.VISIBLE);
                if (mSelectedData.contains(ranking.id())) {
                    deleteIv.setImageResource(R.drawable.checkbox_checked);
                } else {
                    deleteIv.setImageResource(R.drawable.checkbox_unchecked);
                }
            }

            Glide.with(itemView.getContext())
                    .load(Constant.Url.getVoaImg(ranking.topicId()))
                    .centerCrop()
                    .placeholder(R.drawable.default_pic)
                    .into(imageIv);
            nameTv.setText(ranking.titleCn());
            timeTv.setText(ranking.createDate());
            thumbTv.setText(String.valueOf(ranking.agreeCount()));
        }

        public void setClick() {
            root.setOnClickListener(v -> onClickItem());
        }
    }
}

interface OnReleasedClickListener {
    void onReleasedClick(Ranking ranking);
}
