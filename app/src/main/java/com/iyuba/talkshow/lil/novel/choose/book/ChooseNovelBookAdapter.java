package com.iyuba.talkshow.lil.novel.choose.book;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.databinding.ItemChooseBookBinding;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_book;
import com.iyuba.talkshow.lil.help_fix.util.FixUtil;
import com.iyuba.talkshow.lil.help_fix.util.ImageUtil;

import java.util.List;

/**
 * @title:
 * @date: 2023/4/27 14:24
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ChooseNovelBookAdapter extends RecyclerView.Adapter<ChooseNovelBookAdapter.NovelBookHolder> {

    private Context context;
    private List<Novel_book> list;

    public ChooseNovelBookAdapter(Context context, List<Novel_book> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NovelBookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChooseBookBinding binding = ItemChooseBookBinding.inflate(LayoutInflater.from(context),parent,false);
        return new NovelBookHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NovelBookHolder holder, int position) {
        if (holder==null){
            return;
        }

        Novel_book bean = list.get(position);
        ImageUtil.loadImg(FixUtil.fixNovelPicUrl(bean.getPic()),0,holder.bookPic);
        holder.bookName.setText(bean.getBookname_en());

        if (TextUtils.isEmpty(bean.getPic())){
            holder.bookPicLayout.setVisibility(View.GONE);
            holder.bookNameLayout.setVisibility(View.VISIBLE);
        }else {
            holder.bookPicLayout.setVisibility(View.VISIBLE);
            holder.bookNameLayout.setVisibility(View.GONE);
        }

        String bookNameCn = bean.getBookname_cn();
        bookNameCn = bookNameCn.replace("\r\n","");
        holder.bookTitle.setText(bookNameCn);

        holder.itemView.setOnClickListener(v->{
            if (listener!=null){
                listener.onClick(bean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class NovelBookHolder extends RecyclerView.ViewHolder{

        private CardView bookNameLayout;
        private TextView bookName;
        private CardView bookPicLayout;
        private ImageView bookPic;
        private TextView bookTitle;

        public NovelBookHolder(ItemChooseBookBinding binding){
            super(binding.getRoot());

            bookNameLayout = binding.bookNameLayout;
            bookName = binding.bookName;
            bookPicLayout = binding.bookPicLayout;
            bookPic = binding.bookPic;
            bookTitle = binding.bookTitle;
        }
    }

    //回调
    private OnSimpleClickListener<Novel_book> listener;

    public void setListener(OnSimpleClickListener<Novel_book> listener) {
        this.listener = listener;
    }

    //刷新数据
    public void refreshData(List<Novel_book> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }
}
