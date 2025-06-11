package com.iyuba.talkshow.newce.study.read.newRead.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.databinding.FragmentReadItemBinding;
import com.iyuba.talkshow.lil.help_fix.view.SelectWordTextView;

import java.util.List;

/**
 * @title:
 * @date: 2023/12/8 14:05
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewReadApter extends RecyclerView.Adapter<NewReadApter.NewReadHolder> {

    private Context context;
    private List<VoaText> list;

    //选中的位置
    private int selectIndex = 0;
    //切换的语言
    private boolean showCn = false;

    public NewReadApter(Context context, List<VoaText> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NewReadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentReadItemBinding binding = FragmentReadItemBinding.inflate(LayoutInflater.from(context),parent,false);
        return new NewReadHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewReadHolder holder, int position) {
        if (holder==null){
            return;
        }

        VoaText voaText = list.get(position);
        holder.sentence.setText(voaText.sentence());
        holder.sentenceCn.setText(voaText.sentenceCn());

        //显示选中
        if (selectIndex == position) {
            holder.sentence.setTextColor(context.getResources().getColor(R.color.colorText));
            holder.sentenceCn.setTextColor(context.getResources().getColor(R.color.colorText));
        } else {
            holder.sentence.setTextColor(context.getResources().getColor(R.color.black));
            holder.sentenceCn.setTextColor(context.getResources().getColor(R.color.black));
        }

        //切换语言
        if (showCn){
            holder.sentenceCn.setVisibility(View.VISIBLE);
        }else {
            holder.sentenceCn.setVisibility(View.GONE);
        }

        holder.sentence.setOnClickWordListener(new SelectWordTextView.OnClickWordListener() {
            @Override
            public void onClickWord(String word) {
                if (onSelectWordListener!=null){
                    onSelectWordListener.onClickWord(word);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class NewReadHolder extends RecyclerView.ViewHolder{
        private SelectWordTextView sentence;
        private TextView sentenceCn;

        public NewReadHolder(FragmentReadItemBinding binding){
            super(binding.getRoot());

            sentence = binding.sentenceEn;
            sentenceCn = binding.sentenceCn;
        }
    }

    //刷新选中的位置
    public void refreshIndex(int refreshIndex){
        this.selectIndex = refreshIndex;
        notifyDataSetChanged();
    }

    //获取当前选中的位置
    public int getSelectIndex(){
        return selectIndex;
    }

    //刷新数据
    public void refreshData(List<VoaText> refreshData){
        this.list = refreshData;
        notifyDataSetChanged();
    }

    //刷新语言
    public void refreshLanguage(boolean isShowCn){
        this.showCn = isShowCn;
        notifyDataSetChanged();
    }

    //回调选中的单词
    public onSelectWordListener onSelectWordListener;

    public interface onSelectWordListener{
        void onClickWord(String keyWord);
    }

    public void setOnSelectWordListener(NewReadApter.onSelectWordListener onSelectWordListener) {
        this.onSelectWordListener = onSelectWordListener;
    }
}
