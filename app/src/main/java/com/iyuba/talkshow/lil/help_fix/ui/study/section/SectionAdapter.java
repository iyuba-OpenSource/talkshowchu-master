package com.iyuba.talkshow.lil.help_fix.ui.study.section;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.databinding.ItemFixSectionBinding;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.view.SelectWordTextView;

import java.util.List;

/**
 * @title:
 * @date: 2023/7/7 09:11
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionHolder> {

    private Context context;
    private List<Pair<String,String>> list;

    //文本显示类型
    private String showType = TypeLibrary.TextShowType.EN;

    public SectionAdapter(Context context, List<Pair<String,String>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFixSectionBinding binding = ItemFixSectionBinding.inflate(LayoutInflater.from(context),parent,false);
        return new SectionHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        Pair<String,String> detailBean = list.get(position);
        holder.sentenceEn.setText(detailBean.first);
        holder.sentenceCn.setText(detailBean.second);

        holder.sentenceEn.setTextColor(Color.parseColor("#2983c1"));
        holder.sentenceCn.setTextColor(Color.parseColor("#2983c1"));

        if (showType.equals(TypeLibrary.TextShowType.ALL)){
            holder.sentenceCn.setVisibility(View.VISIBLE);
        }else if (showType.equals(TypeLibrary.TextShowType.EN)){
            holder.sentenceCn.setVisibility(View.GONE);
        }else {
            holder.sentenceCn.setVisibility(View.GONE);
        }

        holder.sentenceEn.setOnClickWordListener(new SelectWordTextView.OnClickWordListener() {
            @Override
            public void onClickWord(String selectText) {
                //查询单词并且收藏操作
                if (onWordSearchListener!=null){
                    onWordSearchListener.onWordSearch(selectText);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class SectionHolder extends RecyclerView.ViewHolder{

        SelectWordTextView sentenceEn;
        TextView sentenceCn;

        public SectionHolder(ItemFixSectionBinding binding) {
            super(binding.getRoot());

            sentenceEn = binding.sentence;
            sentenceEn.setEnabled(false);
            sentenceCn = binding.sentenceCn;
        }
    }

    //刷新数据
    public void refreshData(List<Pair<String,String>> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //展示的文本显示类型
    public void switchTextType(String type){
        this.showType = type;
        notifyDataSetChanged();
    }

    //回调单词查询
    private onWordSearchListener onWordSearchListener;

    public interface onWordSearchListener{
        //查询操作
        void onWordSearch(String word);
    }

    public void setOnWordSearchListener(onWordSearchListener onWordSearchListener) {
        this.onWordSearchListener = onWordSearchListener;
    }
}
