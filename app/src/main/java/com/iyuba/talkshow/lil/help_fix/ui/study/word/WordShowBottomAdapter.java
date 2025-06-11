package com.iyuba.talkshow.lil.help_fix.ui.study.word;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.databinding.ItemWordBottomBinding;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;

import java.util.List;

/**
 * @title:
 * @date: 2023/8/15 11:50
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordShowBottomAdapter extends RecyclerView.Adapter<WordShowBottomAdapter.WordShowBottomHolder> {

    private Context context;
    private List<Pair<String,Pair<Integer,String>>> list;//类型-图片,文字

    public WordShowBottomAdapter(Context context, List<Pair<String, Pair<Integer, String>>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WordShowBottomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWordBottomBinding binding = ItemWordBottomBinding.inflate(LayoutInflater.from(context),parent,false);
        return new WordShowBottomHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WordShowBottomHolder holder, int position) {
        if (holder==null){
            return;
        }

        Pair<String,Pair<Integer,String>> pairPair = list.get(position);
        holder.icon.setImageResource(pairPair.second.first);
        holder.text.setText(pairPair.second.second);

        holder.itemView.setOnClickListener(v->{
            if (listener!=null){
                listener.onClick(pairPair.first);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class WordShowBottomHolder extends RecyclerView.ViewHolder{

        private ImageView icon;
        private TextView text;

        public WordShowBottomHolder(ItemWordBottomBinding binding){
            super(binding.getRoot());

            icon = binding.icon;
            text = binding.text;
        }
    }

    //回调
    private OnSimpleClickListener<String> listener;

    public void setListener(OnSimpleClickListener<String> listener) {
        this.listener = listener;
    }
}
